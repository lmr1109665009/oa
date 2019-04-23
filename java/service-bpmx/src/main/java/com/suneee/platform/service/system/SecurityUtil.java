package com.suneee.platform.service.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.system.ResourcesUrlExt;
import com.suneee.platform.model.system.SysRole;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.SystemConst;
import com.suneee.platform.web.security.CustomPwdEncoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
public class SecurityUtil {
	
	protected static Logger logger = LoggerFactory.getLogger(SecurityUtil.class);
	
	private static String rememberPrivateKey = "bpm3PrivateKey";
	

	
	/**
	 * 将系统 的功能和角色列表加入到映射中。
	 * @param systemId		系统id。
	 * @param funcRoleList	功能和角色映射列表。
	 */
	private static FunctionRights getFuncRoleList(String sysAlias,String function){
		
		SysRoleService sysRoleService=(SysRoleService) AppUtil.getBean(SysRoleService.class);
		List<ResourcesUrlExt> funcRoleList=sysRoleService.getFunctionRoleList(sysAlias,function);
		boolean hasFunction=false;
		if(funcRoleList.size()>0){
			hasFunction=true;
		}
		
		Collection<ConfigAttribute> collectoin=new HashSet<ConfigAttribute>();
		for(ResourcesUrlExt table:funcRoleList){
			String role=(String)table.getRole();
			if(StringUtil.isEmpty(role)) continue;
			collectoin.add(new SecurityConfig(role));
		}
		SecurityUtil util=new SecurityUtil();
		FunctionRights rights=util.new FunctionRights(hasFunction, collectoin);
		return rights;

	}
	
	
	
	
	
	/**
	 * 添加系统和角色的关系映射。
	 * 系统id ： 角色set集合。
	 * @param systemId
	 */
	public static Set<String> getSystemRole(Long systemId){
		SysRoleService sysRoleService=(SysRoleService)AppUtil.getBean(SysRoleService.class);
		List<SysRole> listRole= sysRoleService.getBySystemId(systemId);
//		ICache iCache = (ICache) AppUtil.getBean(ICache.class);
//		String systemRoleKey=SystemRoleMap + systemId;
		//URL 和角色列表映射。
		Set<String> roleSet=new HashSet<String>();
		for(SysRole role: listRole){
			roleSet.add(role.getAlias());
		}
//		iCache.add(systemRoleKey, roleSet);
		return roleSet;
	}
	

	
	
	
	/**
	 * 根据系统和功能别名判断是否有权限访问。
	 * @param systemId
	 * @param function
	 * @return
	 */
	public static boolean hasFuncPermission(String systemAlias, String function){
		
		FunctionRights functionRights= getFuncRoleList(systemAlias, function);
		
		
		
		SysUser currentUser= (SysUser) ContextUtil.getCurrentUser();
		//超级管理员
		if(currentUser.getAuthorities().contains(SystemConst.ROLE_GRANT_SUPER)){
			return true;
		}
		//当功能在系统功能表中，匹配当前用户的角色是否在功能的角色列表中。
		else {
			if(!functionRights.isHasFunction()) return true ;
			Collection<ConfigAttribute> functionRole=functionRights.getRoles();
			if(functionRole.size()==0) return false;
			
			for(GrantedAuthority hadRole:currentUser.getAuthorities()){
				if(functionRole.contains(new SecurityConfig(hadRole.getAuthority()))){  
	                return true;
	            }
	        }
			return false;
	    }
    }
	
	
	public class FunctionRights{
		
		private boolean hasFunction=false;
		
		private Collection<ConfigAttribute> roles=new ArrayList<ConfigAttribute>();
		
		public FunctionRights(boolean hasFunction,Collection<ConfigAttribute> roles){
			this.hasFunction=hasFunction;
			this.roles=roles;
		}

		public boolean isHasFunction() {
			return hasFunction;
		}

		public void setHasFunction(boolean hasFunction) {
			this.hasFunction = hasFunction;
		}

		public Collection<ConfigAttribute> getRoles() {
			return roles;
		}

		public void setRoles(Collection<ConfigAttribute> roles) {
			this.roles = roles;
		}
		
	}
	
	/**
	 * 登录系统让系统实现登录。
	 * @param request
	 * @param userName		用户名
	 * @param pwd			密码
	 * @param isIgnorePwd	是否忽略密码
	 * @return 
	 */
	public static Authentication login(HttpServletRequest request,String userName,String pwd,boolean isIgnorePwd){
		AuthenticationManager authenticationManager =(AuthenticationManager) AppUtil.getBean("authenticationManager");
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
		String rememberMe = request.getParameter("_spring_security_remember_me");
		if (!"1".equals(rememberMe)) return;
		
		long tokenValiditySeconds = 1209600; // 14 days
		long tokenExpiryTime = System.currentTimeMillis() + (tokenValiditySeconds * 1000);
		String signatureValue = DigestUtils.md5Hex(username + ":" + tokenExpiryTime + ":" + enPassword + ":" + rememberPrivateKey);
		String tokenValue = username + ":" + tokenExpiryTime + ":" + signatureValue;
		String tokenValueBase64 = new String(Base64.encodeBase64(tokenValue.getBytes()));
		Cookie cookie = new Cookie(TokenBasedRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY, tokenValueBase64);
		cookie.setMaxAge(60 * 60 * 24 * 365 * 5); // 5 years
		cookie.setPath(org.springframework.util.StringUtils.hasLength(request.getContextPath()) ? request.getContextPath() : "/");
		response.addCookie(cookie);
	}
	
	
	

}



 
