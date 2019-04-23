/**
 * @Title: SwitchUserFailureHandler.java 
 * @Package com.suneee.oa.handler.authenticate 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.handler.authenticate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.suneee.ucp.base.util.JsonUtils;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Component;

/**
 * @ClassName: SwitchUserFailureHandler 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-04-23 11:43:21 
 *
 */
@Component
public class SwitchUserFailureHandler implements AuthenticationFailureHandler{

	/** (non-Javadoc)
	 * @Title: onAuthenticationFailure 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param request
	 * @param response
	 * @param exception
	 * @throws IOException
	 * @throws ServletException 
	 * @see AuthenticationFailureHandler#onAuthenticationFailure(HttpServletRequest, HttpServletResponse, AuthenticationException)
	 */
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		ResultVo result = new ResultVo(ResultVo.COMMON_STATUS_FAILED, exception.getMessage());
		response.getWriter().print(JsonUtils.toJson(result));
	}
}
