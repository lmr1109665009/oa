package com.suneee.eas.gateway.utils;

import com.suneee.eas.gateway.security.CustomPwdEncoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 主要用于系统权限资源缓存。
 * <pre>
 * 	1.系统的url和角色映射。
 *  2.系统的url和参数列表进行映射。
 * 	4.系统和角色进行映射。
 *  3.系统的功能和角色映射。
 * </pre>
 * @author ray
 *
 */
@Component
public class SecurityUtil {
	
	protected static Logger log = LoggerFactory.getLogger(SecurityUtil.class);
	
	public static final String rememberPrivateKey = "suneee-eas-cookie";
	private static AuthenticationManager authenticationManager;
	@Autowired
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		SecurityUtil.authenticationManager = authenticationManager;
	}

	/**
	 * 登录系统让系统实现登录。
	 * @param request
	 * @param userName		用户名
	 * @param pwd			密码
	 * @param isIgnorePwd	是否忽略密码
	 * @return
	 */
	public static Authentication login(HttpServletRequest request, String userName, String pwd, boolean isIgnorePwd){
		CustomPwdEncoder.setIngore(isIgnorePwd);
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userName, pwd);
		authRequest.setDetails(new WebAuthenticationDetails(request));
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication auth = authenticationManager.authenticate(authRequest);
		securityContext.setAuthentication(auth);
		return auth;
	}


	/**
	 * 加上用户登录的remember me 的cookie
	 * 
	 * @param request
	 * @param response
	 * @param username
	 * @param enPassword
	 */
	public static void writeRememberMeCookie(HttpServletRequest request, HttpServletResponse response, String username, String enPassword) {
		String rememberMe = request.getParameter("rememberMe");
		if (!"1".equals(rememberMe)) return;
		long tokenValiditySeconds = 1209600; // 14 days
		long tokenExpiryTime = System.currentTimeMillis() + (tokenValiditySeconds * 1000);
		String signatureValue = DigestUtils.md5Hex(username + ":" + tokenExpiryTime + ":" + enPassword + ":" + rememberPrivateKey);
		String tokenValue = username + ":" + tokenExpiryTime + ":" + signatureValue;
		String tokenValueBase64 = new String(Base64.encodeBase64(tokenValue.getBytes()));
		Cookie cookie = new Cookie(TokenBasedRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY, tokenValueBase64);
		cookie.setMaxAge(60 * 60 * 24 * 365 * 5); // 5 years
		cookie.setPath(StringUtils.hasLength(request.getContextPath()) ? request.getContextPath() : "/");
		response.addCookie(cookie);
	}
	

}



 
