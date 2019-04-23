/**
 * @Title: SwitchEnterpriseFilter.java 
 * @Package com.suneee.oa.filter 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.gateway.security;

import com.suneee.eas.common.api.oa.RoleResourceApi;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.ServiceConstant;
import com.suneee.eas.common.constant.SymbolConstant;
import com.suneee.eas.common.constant.UserConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.HttpUtil;
import com.suneee.eas.common.utils.RestTemplateUtil;
import com.suneee.eas.gateway.service.EnterpriseInfoService;
import com.suneee.eas.gateway.service.UserEnterpriseService;
import com.suneee.eas.gateway.utils.CookieUitl;
import com.suneee.platform.model.system.SysUser;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.model.user.UserEnterprise;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @ClassName: SwitchEnterpriseFilter 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-05-09 14:42:56 
 *
 */
@Component
public class SuneeeSwitchEnterpriseFilter implements Filter{
	public static final String SWITCH_ENTERPRISE_CODE_KEY = "switchEntcode";
	
	private static final Logger log = LoggerFactory.getLogger(SuneeeSwitchEnterpriseFilter.class);
	
	private String switchEnterpriseUrl = "/switchEnterprise";
	private String switchEntParamName = SWITCH_ENTERPRISE_CODE_KEY;
	@Autowired
	private UserEnterpriseService userEnterpriseService;
	@Autowired
	private EnterpriseInfoService enterpriseInfoService;

	/** (non-Javadoc)
	 * @Title: doFilter 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param request
	 * @param response
	 * @param chain
	 * @throws IOException
	 * @throws ServletException 
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest  req= (HttpServletRequest) request;
        HttpServletResponse  resp= (HttpServletResponse) response;
        
		if(requiresSwitchEnterrpise(req)){
			String enterpriseCode = req.getParameter(switchEntParamName);
			if(StringUtils.isBlank(enterpriseCode)){
				log.error("切换企业失败：切换的企业编码为空！");
				HttpUtil.writeResponseJson(resp, ResponseMessage.fail("切换企业失败：请求参数错误！"));
				return;
			} 

			SysUser user = (SysUser)ContextSupportUtil.getCurrentUser();
			UserEnterprise userEnterprise = userEnterpriseService.getByUserIdAndCode(user.getUserId(), enterpriseCode);
			if(userEnterprise == null){
				log.error("切换企业失败：用户【" + user.getAccount() + "】不属于切换的企业【" + enterpriseCode + "】");
				HttpUtil.writeResponseJson(resp,ResponseMessage.fail("切换企业失败：用户【" + user.getAccount() + "】不属于切换的企业！"));
				return;
			} else {
				Enterpriseinfo enterpriseinfo=null;
				// 判断用户在改企业下是否有权限
				if(!this.hasResourcesRight(user, enterpriseCode)){
					enterpriseinfo = enterpriseInfoService.getByCompCode(enterpriseCode);
					String enterpriseName = enterpriseCode;
					if(enterpriseinfo != null){
						enterpriseName = enterpriseinfo.getComp_name();
					}
					log.error("切换企业失败：用户【" + user.getAccount() + "】没有【" + enterpriseCode + "】企业的资源访问权限");
					HttpUtil.writeResponseJson(resp,ResponseMessage.fail("切换企业失败：用户【" + user.getAccount() + "】没有【" + enterpriseName + "】企业的资源访问权限！"));
					return;
				}
				String name = CookieUitl.getSwitchEntCookieName(user.getUserId());
				CookieUitl.addCookieAndSession(name, enterpriseCode, -1, SymbolConstant.SEPARATOR_BACK_SLANT, req, resp);
				HttpSession session=((HttpServletRequest) request).getSession();
				session.setAttribute(UserConstant.SESSION_ENTERPRISE_KEY,enterpriseinfo);
				session.removeAttribute(UserConstant.SESSION_POSITION_KEY);
				session.removeAttribute(UserConstant.SESSION_ORG_KEY);
				HttpUtil.writeResponseJson(resp,ResponseMessage.success("切换企业成功！"));
				return;
			}
		}
		chain.doFilter(req, resp);
	}
	
	/** (non-Javadoc)
	 * @Title: init 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param filterConfig
	 * @throws ServletException 
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	/** (non-Javadoc)
	 * @Title: destroy 
	 * @Description: TODO(这里用一句话描述这个方法的作用)  
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	/** 
	 * 判断是否是切换企业请求
	 * @param request
	 * @return 如果是
	 */
	protected boolean requiresSwitchEnterrpise(HttpServletRequest request) {
        String uri = stripUri(request);
        
        return uri.endsWith(request.getContextPath() + switchEnterpriseUrl);
    }

	/**
     * 删除请求URI中“;”之后的任何内容
     *
     * @param request 
     *
     * @return 
     */
    private String stripUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        int idx = uri.indexOf(';');

        if (idx > 0) {
            uri = uri.substring(0, idx);
        }

        return uri;
    }

	/**
	 * 判断是否有资源权限
	 * @param sysUser
	 * @param enterpriseCode
	 * @return
	 */
	private boolean hasResourcesRight(SysUser sysUser, String enterpriseCode) throws UnsupportedEncodingException {
    	if(sysUser == null){
    		return false;
    	}
    	// 获取用户在该企业下的权限信息
        MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
    	if (ContextSupportUtil.isSuperAdmin(sysUser)){
            return true;
        }
		params.add("enterpriseCode",enterpriseCode);
		params.add("userId",sysUser.getUserId());
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
