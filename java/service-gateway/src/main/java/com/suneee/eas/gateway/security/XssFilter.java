package com.suneee.eas.gateway.security;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.HttpUtil;
import com.suneee.eas.common.utils.StringUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * XSS安全过滤器。
 * <pre>
 *  这个功能是为了放置XSS攻击。
 *  如果有Xss攻击：
 *  	1.表单提交方式，平台将去到一个提示页面。
 *  	2.AJAX提交方式，弹出提示信息。
 *  可以配置某些不需要检测的URL.
 * </pre>
 * @author ray
 *
 */
public class XssFilter extends AbstractFilter implements Filter {
	
	private Pattern regex = Pattern.compile("<(\\S*?)[^>]*>.*?</\\1>|<[^>]+>", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL | Pattern.MULTILINE);
	

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req=(HttpServletRequest) request;
		HttpServletResponse resp=(HttpServletResponse) response;
		//页面是否忽略。
		boolean isIngoreUrl=isContainUrl(req.getRequestURI());
		if(isIngoreUrl){
			chain.doFilter(request, response);
		}
		else{
			//检测是否有XSS攻击。
			boolean hasXss= checkXss(req);
			if(hasXss){
				HttpUtil.writeResponseJson(resp, ResponseMessage.fail("检测到XSS攻击，请检是否输入了HTML字符！"));
			}
			else{
				chain.doFilter(request, response);
			}
		}
	}
	
	

	@Override
	public void init(FilterConfig config) throws ServletException {
	}
	
	/**
	 * 判断输入是否有XSS注入问题。
	 * @param request
	 * @return
	 */
	private boolean checkXss(HttpServletRequest request){
		Enumeration<?> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String key = params.nextElement().toString();
			String[] vals=request.getParameterValues(key);
			String val=StringUtil.join(vals, "");
			if(StringUtil.isEmpty(val)) continue;
			
			Matcher regexMatcher = regex.matcher(val);
			if(regexMatcher.find()){
				return true;
			}
		}
		return false;
	}

	
	
}
