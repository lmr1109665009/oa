package com.suneee.eas.gateway.handler.user;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.HttpUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义认证失败handler
 * springsecurity 访问拒绝自定义处理器。<br>
 * 接收AccessDeniedException 并交由accessDeniedUrl页面处理。<br>
 * 在处理页面中需要使用 AccessDeniedException ex=(AccessDeniedException)request.getAttribute("ex");
 * <pre>
 * 具体参考app-security.xml。
 *  &lt;security:access-denied-handler ref="htAccessDeniedHandler"/>
 * &lt;bean id="htAccessDeniedHandler" class="com.suneee.core.web.security.HtAccessDeniedHandler">
 *		&lt;property name="accessDeniedUrl" value="/403.jsp">&lt;/property>
 *	&lt;/bean>
 * </pre>
 * @author ray
 *
 */
public class SuneeeAccessDeniedHandler extends AccessDeniedHandlerImpl {
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
		super.handle(request, response, accessDeniedException);
		HttpUtil.writeResponseJson(response, ResponseMessage.fail("认证失败，errorCode:"+accessDeniedException.getMessage()));
	}
}
