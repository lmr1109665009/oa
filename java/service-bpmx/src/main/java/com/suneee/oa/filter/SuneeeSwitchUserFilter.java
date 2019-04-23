/**
 * @Title: SuneeeSwitchUserFilter.java 
 * @Package com.suneee.oa.filter 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.filter;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.service.system.ResourcesExtendService;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.platform.model.system.Resources;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.util.JsonUtils;
import com.suneee.ucp.base.vo.ResultVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @ClassName: SuneeeSwitchUserFilter 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-06-02 13:32:50 
 *
 */
public class SuneeeSwitchUserFilter extends  SwitchUserFilter{
	
	public static final String SwitchAccount="origSwitch";
	public static final String SWITCHUSERID = "origUserId";
	public static final String FLAG_SWITCH_USER = "SWITCH_USER";
	public static final String FLAG_EXIT_SWITCH = "EXIT_SWITCH";
	private String encoding = "UTF-8";
	private String contentType = "application/json; charset=utf-8";
	/**
	 * 在切换路径之前把原来用户加入session中
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
		
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse)res;
        response.setCharacterEncoding(encoding);
        //response.setContentType(contentType);
        // 切换用户
        if (requiresSwitchUser(request)) {
        	String origAccount = CookieUitl.getValueByName(SwitchAccount, request);
        	if(StringUtils.isBlank(origAccount)){
        		String account = RequestUtil.getString(request, SwitchUserFilter.SPRING_SECURITY_SWITCH_USERNAME_KEY);
        		Short fromType = RequestUtil.getShort(request, "fromType", Resources.FROMTYPE_SERVER);
        		// 判断切换用户是否拥有当前企业下的资源访问权限，如果没有则切换失败
        		if(!this.hasResourcesRight(account, fromType)){
        			String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
        			EnterpriseinfoService enterpriseinfoService = AppUtil.getBean(EnterpriseinfoService.class);
					Enterpriseinfo enterpriseinfo = enterpriseinfoService.getByCompCode(enterpriseCode);
					String enterpriseName = enterpriseCode;
					if(enterpriseinfo != null){
						enterpriseName = enterpriseinfo.getComp_name();
					}
					this.handleReturn(response, ResultVo.COMMON_STATUS_FAILED, "切换用户失败：用户【" + account + "】没有【" + enterpriseName + "】企业下的资源访问权限！");
					return;
        		}
        		request.setAttribute("SWITCH_FLAG", FLAG_SWITCH_USER);
            	request.setAttribute(SwitchAccount, ContextUtil.getCurrentUser().getAccount());
            	request.setAttribute(SWITCHUSERID, ContextUtil.getCurrentUserId());
        	} else {
        		this.handleReturn(response, ResultVo.COMMON_STATUS_FAILED, "已执行过切换用户操作，切换用户之后不能再次执行切换用户操作");
        		return;
        	}
        } 
        //退出切换
        else if (requiresExitUser(request)) {
        	String origAccount = CookieUitl.getValueByName(SwitchAccount, request);
        	if(StringUtils.isBlank(origAccount)){
        		this.handleReturn(response, ResultVo.COMMON_STATUS_FAILED, "未切换用户，不可执行退出切换用户操作");
        		return;
        	} else {
            	request.setAttribute("SWITCH_FLAG", FLAG_EXIT_SWITCH);
            	request.setAttribute(SWITCHUSERID, ContextUtil.getCurrentUserId());
        	}
        }
        super.doFilter(req, response, chain);
    }
	
	public void setEncoding(String encoding){
		Assert.notNull(encoding, "encoding cannot be empty.");
		this.encoding = encoding;
	}
	
	public void setContentType(String contentType){
		Assert.notNull(contentType, "contentType cannot be empty");
		this.contentType = contentType;
	}
	
	private boolean hasResourcesRight(String account, Short fromType){
		SysUserService sysUserService = AppUtil.getBean(SysUserService.class);
		SysUser sysUser = sysUserService.getByAccount(account);
		if(sysUser == null){
			return false;
		}
		// 获取用户在该企业下的权限信息
		ResourcesExtendService resourcesExtendService = AppUtil.getBean(ResourcesExtendService.class);
		List<Resources> resourcesList=resourcesExtendService.getRoleResources(1L, sysUser, fromType);
		if(resourcesList.size() == 0){
			return false;
		}
		return true;
	}

	private void handleReturn(HttpServletResponse response, int status, String message) throws IOException{
		ResultVo resultVo = new ResultVo(status, message);
		response.getWriter().print(JsonUtils.toJson(resultVo));
	}
}
