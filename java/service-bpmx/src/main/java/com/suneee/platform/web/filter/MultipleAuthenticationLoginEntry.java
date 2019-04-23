package com.suneee.platform.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.util.AppConfigUtil;
import com.suneee.core.web.util.CookieUitl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.suneee.core.util.AppConfigUtil;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.ucp.base.common.Constants;

/**
 * 多用户入口实现
 * by cjj
 */
public class MultipleAuthenticationLoginEntry implements
		AuthenticationEntryPoint {

    private String defaultLoginUrl="/login.jsp";  
    private List<DirectUrlResolver> directUrlResolvers = new ArrayList<DirectUrlResolver>();  
  
    /**
     * 根据输入路径与配置项得到跳转路径分别跳到不同登录页面
     */
    @Override  
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {  
    	String ctxPath = request.getContextPath();
    	for (DirectUrlResolver directUrlResolver : directUrlResolvers) {  
            if (directUrlResolver.support(request)) {  
                String loginUrl = directUrlResolver.directUrl();  
                response.sendRedirect(ctxPath+ loginUrl);  
                return;  
            }  
        }  
    	
    	// 取出登陆端标识
    	String loginAction= CookieUitl.getValueByName("loginAction", request);
    	if(StringUtils.isBlank(loginAction)){
    		loginAction = request.getParameter("flag");
    	}
    	// 如果是从web端则跳转到框架登陆页
    	if(StringUtils.isNotBlank(loginAction) && "web".equals(loginAction)){
    		 response.sendRedirect(AppConfigUtil.get(Constants.LOGIN_PAGE_REDIRECT));
    		 return;
    	}
        response.sendRedirect(ctxPath+defaultLoginUrl);  
    }  
  
    public void setDefaultLoginUrl(String defaultLoginUrl) {  
    	
        this.defaultLoginUrl = defaultLoginUrl;  
    }  
  
    public void setDirectUrlResolvers(List<DirectUrlResolver> directUrlResolvers) {  
        this.directUrlResolvers = directUrlResolvers;  
    }  

}
