/**
 * @Title: SwitchUserSuccessHandler.java 
 * @Package com.suneee.oa.handler.authenticate 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.handler.authenticate;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.oa.filter.SuneeeSwitchUserFilter;
import com.suneee.ucp.base.util.JsonUtils;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName: SwitchUserSuccessHandler 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-04-23 11:41:38 
 *
 */
@Component
public class SwitchUserSuccessHandler implements AuthenticationSuccessHandler{

	/** (non-Javadoc)
	 * @Title: onAuthenticationSuccess 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param request
	 * @param response
	 * @param authentication
	 * @throws IOException
	 * @throws ServletException 
	 * @see AuthenticationSuccessHandler#onAuthenticationSuccess(HttpServletRequest, HttpServletResponse, Authentication)
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String flag = (String)request.getAttribute("SWITCH_FLAG");
		String message = null;
		// 切换用户
		if(SuneeeSwitchUserFilter.FLAG_SWITCH_USER.equals(flag)){
			message = "切换用户成功！";
			String account = (String)request.getAttribute(SuneeeSwitchUserFilter.SwitchAccount);
			// 设置切换之前的用户账号，存入cookie中
			setAccount(account,request,response);
			// 获取切换之前的用户ID
			Long origUserId = (Long) request.getAttribute(SuneeeSwitchUserFilter.SWITCHUSERID);
			// 设置切换之后用户的所属企业编码，存入cookie中
			setEnterpriseCode(origUserId, request, response);
			// 删除切换之前用户的所属企业编码
			removeEnterpriseCode(origUserId, request, response);
		}
		// 退出切换
		else if(SuneeeSwitchUserFilter.FLAG_EXIT_SWITCH.equals(flag)) {
			message = "退出切换成功！";
			// 从cookie中删除切换之前的用户账号
			removeAccount(request,response);
			// 获取切换之后的用户ID
			Long switchUserId = (Long) request.getAttribute(SuneeeSwitchUserFilter.SWITCHUSERID);
			// 设置切换之前用户的所属企业编码，存入cookie
			setEnterpriseCode(switchUserId, request, response);
			// 删除切换之后用户的所属企业编码
			removeEnterpriseCode(switchUserId, request, response);
		}
		ResultVo result = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, message, ContextUtil.getCurrentUser());
		response.getWriter().print(JsonUtils.toJson(result));
	}

	private void setAccount(String account,HttpServletRequest req,HttpServletResponse res){
		CookieUitl.addCookie(SuneeeSwitchUserFilter.SwitchAccount, account, -1, "/", req, res);
	}
	
	private void removeAccount(HttpServletRequest req,HttpServletResponse res){
		CookieUitl.delCookie(SuneeeSwitchUserFilter.SwitchAccount, "/",req, res);
	}
	
	/** 
	 * 设置切换之后用户的所属企业编码
	 * @param userId
	 * @param req
	 * @param res
	 */
	private void setEnterpriseCode(Long userId,HttpServletRequest req,HttpServletResponse resp){
		// 获取切换之前用户的企业编码
		String enterpriseCode = CookieUitl.getEnterpriseCodeByUserId(userId);
		// 让切换之后用户的企业编码与切换之前保持一致
		CookieUitl.addCookieAndSession(CookieUitl.getSwitchEntCookieName(), enterpriseCode, -1, "/", req, resp);
	}
	
	/** 
	 * 移除指定用户的所属企业编码
	 * @param userId
	 * @param req
	 * @param res
	 */
	private void removeEnterpriseCode(Long userId,HttpServletRequest req,HttpServletResponse resp){
		String cookieName = CookieUitl.getSwitchEntCookieName(userId);
		CookieUitl.delCookieAndSession(cookieName, "/", req, resp);
	}
}
