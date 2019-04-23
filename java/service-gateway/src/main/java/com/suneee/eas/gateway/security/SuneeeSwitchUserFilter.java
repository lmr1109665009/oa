/**
 * @Title: SuneeeSwitchUserFilter.java 
 * @Package com.suneee.oa.filter 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.gateway.security;

import com.suneee.eas.common.api.oa.RoleResourceApi;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.ServiceConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.HttpUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.common.utils.RestTemplateUtil;
import com.suneee.eas.gateway.handler.authenticate.SwitchUserFailureHandler;
import com.suneee.eas.gateway.handler.authenticate.SwitchUserSuccessHandler;
import com.suneee.eas.gateway.service.EnterpriseInfoService;
import com.suneee.eas.gateway.service.SysUserService;
import com.suneee.eas.gateway.utils.CookieUitl;
import com.suneee.platform.model.system.SysUser;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @ClassName: SuneeeSwitchUserFilter
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-06-02 13:32:50
 *
 */
@Component
public class SuneeeSwitchUserFilter extends  SwitchUserFilter{
	public static final Logger log= LogManager.getLogger(SuneeeSwitchUserFilter.class);
	public static final String SwitchAccount="origSwitch";
	public static final String SWITCHUSERID = "origUserId";
	public static final String FLAG_SWITCH_USER = "SWITCH_USER";
	public static final String FLAG_EXIT_SWITCH = "EXIT_SWITCH";
	public static final String PARMAS_USERNAME="j_username";
	@Autowired
	private EnterpriseInfoService enterpriseInfoService;
	@Autowired
	private SysUserService sysUserService;

	@Autowired
	public void setUserDetailsService(UserDetailsService userDetailsService){
		super.setUserDetailsService(userDetailsService);
		super.setSwitchUserUrl("/j_spring_security_switch_user");
		super.setExitUserUrl("/j_spring_security_exit_user");
		super.setSuccessHandler(new SwitchUserSuccessHandler());
		super.setFailureHandler(new SwitchUserFailureHandler());
	}

	@PostConstruct
	public void init(){
		setUsernameParameter(PARMAS_USERNAME);
	}

	/**
	 * 在切换路径之前把原来用户加入session中
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse)res;
        // 切换用户
        if (requiresSwitchUser(request)) {
			if (!runSwitchUser(request, response)) return;
		}
        //退出切换
        else if (requiresExitUser(request)) {
			if (!exitSwitchUser(request, response)) return;
		}
        super.doFilter(req, response, chain);
    }

	/**
	 * 切换用户
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	private boolean runSwitchUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String origAccount = CookieUitl.getValueByName(SwitchAccount, request);
		if(!StringUtils.isBlank(origAccount)){
			HttpUtil.writeResponseJson(response,ResponseMessage.fail("已执行过切换用户操作，切换用户之后不能再次执行切换用户操作"));
			return false;
		}
		String account = RequestUtil.getString(request, PARMAS_USERNAME);
		// 判断切换用户是否拥有当前企业下的资源访问权限，如果没有则切换失败
		if(!this.hasResourcesRight(account)){
			String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
			Enterpriseinfo enterpriseinfo = enterpriseInfoService.getByCompCode(enterpriseCode);
			String enterpriseName = enterpriseCode;
			if(enterpriseinfo != null){
				enterpriseName = enterpriseinfo.getComp_name();
			}
			HttpUtil.writeResponseJson(response, ResponseMessage.fail("切换用户失败：用户【" + account + "】没有【" + enterpriseName + "】企业下的资源访问权限！"));
			return false;
		}
		request.setAttribute("SWITCH_FLAG", FLAG_SWITCH_USER);
		request.setAttribute(SwitchAccount, ContextSupportUtil.getCurrentUser().getAccount());
		request.setAttribute(SWITCHUSERID, ContextSupportUtil.getCurrentUserId());
		return true;
	}

	/**
	 * 退出切换用户
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	private boolean exitSwitchUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String origAccount = CookieUitl.getValueByName(SwitchAccount, request);
		if(StringUtils.isBlank(origAccount)){
			HttpUtil.writeResponseJson(response, ResponseMessage.fail("未切换用户，不可执行退出切换用户操作"));
			return false;
		} else {
			request.setAttribute("SWITCH_FLAG", FLAG_EXIT_SWITCH);
			request.setAttribute(SWITCHUSERID, ContextSupportUtil.getCurrentUserId());
		}
		return true;
	}

	/**
	 * 判断当前企业是否有资源权限
	 * @param account
	 * @return
	 */
	private boolean hasResourcesRight(String account) throws UnsupportedEncodingException {
		SysUser sysUser = sysUserService.getByAccount(account);
		if(sysUser == null){
		    log.warn("判断当前企业是否有资源权限功能，用户不存在");
			return false;
		}
        MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		params.add("userId",sysUser.getUserId());
		params.add("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		ResponseEntity<ResponseMessage<Integer>> responseEntity= (ResponseEntity<ResponseMessage<Integer>>) RestTemplateUtil.get(ServiceConstant.getOaServiceUrl() +RoleResourceApi.checkRoleResources, new ParameterizedTypeReference<ResponseMessage<Integer>>() {},params);
		ResponseMessage<Integer> responseMessage=responseEntity.getBody();
		if (responseMessage==null){
			log.error("请求OA资源检查权限，没有数据返回");
			return false;
		}
		if (responseMessage.getStatus()==ResponseMessage.STATUS_FAIL){
			log.error("请求OA资源检查权限异常，错误信息："+responseMessage.getMessage());
			return false;
		}
		if (responseMessage.getData()==0){
			log.info("当前用户没有资源权限");
			return false;
		}
		return true;
	}
}
