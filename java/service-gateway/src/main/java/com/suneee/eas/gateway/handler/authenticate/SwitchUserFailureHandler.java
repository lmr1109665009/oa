/**
 * @Title: SwitchUserFailureHandler.java 
 * @Package com.suneee.oa.handler.authenticate 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.gateway.handler.authenticate;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.HttpUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName: SwitchUserFailureHandler 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-04-23 11:43:21 
 *
 */
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
			AuthenticationException exception) throws IOException{
		HttpUtil.writeResponseJson(response, ResponseMessage.fail(exception.getMessage()));
		exception.printStackTrace();
	}
}
