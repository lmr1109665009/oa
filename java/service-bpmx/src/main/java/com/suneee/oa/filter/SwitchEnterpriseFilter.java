/**
 * @Title: SwitchEnterpriseFilter.java 
 * @Package com.suneee.oa.filter 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.platform.model.system.Resources;
import com.suneee.platform.model.system.SysUser;
import com.suneee.oa.model.user.UserEnterprise;
import com.suneee.oa.service.system.ResourcesExtendService;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.oa.service.user.UserEnterpriseService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.util.JsonUtils;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Component;

/**
 * @ClassName: SwitchEnterpriseFilter 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-05-09 14:42:56 
 *
 */
@Component
public class SwitchEnterpriseFilter implements Filter{
	public static final String SWITCH_ENTERPRISE_CODE_KEY = "switchEntcode";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SwitchEnterpriseFilter.class);
	
	private String switchEnterpriseUrl = "/switchEnterprise";
	private String switchEntParamName = SWITCH_ENTERPRISE_CODE_KEY;

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
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest  req= (HttpServletRequest) request;
        HttpServletResponse  resp= (HttpServletResponse) response;
        
		if(requiresSwitchEnterrpise(req)){
			String enterpriseCode = req.getParameter(switchEntParamName);
			if(StringUtils.isBlank(enterpriseCode)){
				LOGGER.error("切换企业失败：切换的企业编码为空！");
				this.handleReturn(resp, ResultVo.COMMON_STATUS_FAILED, "切换企业失败：请求参数错误！");
				return;
			} 
			
			// 判断当前用户是否属于切换的企业
			UserEnterpriseService userEnterpriseService = AppUtil.getBean(UserEnterpriseService.class);
			SysUser user = (SysUser)ContextUtil.getCurrentUser();
			UserEnterprise userEnterprise = userEnterpriseService.getByUserIdAndCode(user.getUserId(), enterpriseCode);
			if(userEnterprise == null){
				LOGGER.error("切换企业失败：用户【" + user.getAccount() + "】不属于切换的企业【" + enterpriseCode + "】");
				this.handleReturn(resp, ResultVo.COMMON_STATUS_FAILED, "切换企业失败：用户【" + user.getAccount() + "】不属于切换的企业！");
				return;
			} else {
				// 判断用户在改企业下是否有权限
				if(!this.hasResourcesRight(user, Resources.FROMTYPE_CLIENT, enterpriseCode)){
					EnterpriseinfoService enterpriseinfoService = AppUtil.getBean(EnterpriseinfoService.class);
			 		Enterpriseinfo enterpriseinfo = enterpriseinfoService.getByCompCode(enterpriseCode);
					String enterpriseName = enterpriseCode;
					if(enterpriseinfo != null){
						enterpriseName = enterpriseinfo.getComp_name();
					}
					LOGGER.error("切换企业失败：用户【" + user.getAccount() + "】没有【" + enterpriseCode + "】企业的资源访问权限");
					this.handleReturn(resp, ResultVo.COMMON_STATUS_FAILED, "切换企业失败：用户【" + user.getAccount() + "】没有【" + enterpriseName + "】企业的资源访问权限！");
					return;
				}
				String name = CookieUitl.getSwitchEntCookieName(user.getUserId());
				CookieUitl.addCookieAndSession(name, enterpriseCode, -1, Constants.SEPARATOR_BACK_SLANT, req, resp);
				this.handleReturn(resp, ResultVo.COMMON_STATUS_SUCCESS, "切换企业成功！");
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
    
    private boolean hasResourcesRight(SysUser sysUser, Short fromType, String enterpriseCode){
    	if(sysUser == null){
    		return false;
    	}
    	// 获取用户在该企业下的权限信息
		ResourcesExtendService resourcesExtendService = AppUtil.getBean(ResourcesExtendService.class);
		List<Resources> resourcesList=resourcesExtendService.getRoleResources(1L, sysUser, Resources.FROMTYPE_CLIENT, enterpriseCode);
    	if(resourcesList.size() == 0){
    		return false;
    	}
    	return true;
    }
    
    private final void handleReturn(HttpServletResponse response, int status, String message) throws IOException{
		ResultVo resultVo = new ResultVo(status, message);
		response.getWriter().print(JsonUtils.toJson(resultVo));
	} 
}
