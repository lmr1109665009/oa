package com.suneee.platform.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 防止CSRF跨站请求攻击。
 * <pre>
 * 	这个主要是防止外链连入到本系统。
 * </pre>
 * @author ray
 */
public class CsrfFilter extends AbstractFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req=(HttpServletRequest)request;
		//是否包含当前URL
		boolean isIngoreUrl=isContainUrl(req.getRequestURI());
		if(isIngoreUrl){
			chain.doFilter(request, response);
		}
		else{
			//判断是否外链。
			String referer = req.getHeader("Referer");   
			String serverName = request.getServerName();
			if(null != referer&&referer.indexOf(serverName) < 0){            
				req.getRequestDispatcher("/commons/csrf.jsp").forward(req, response);  
			}
			else{
				chain.doFilter(request, response);
			}
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

}
