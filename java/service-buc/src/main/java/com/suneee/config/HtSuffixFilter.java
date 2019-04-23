package com.suneee.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.RequestContextFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义支持自定义后缀
 * @author 子华
 */
@Configuration
@WebFilter(urlPatterns = "*.ht")
public class HtSuffixFilter extends RequestContextFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.endsWith(".ht")) {
            uri = uri.substring(0, uri.length() - 3).replace(request.getContextPath(),"");
            request.getRequestDispatcher(uri).forward(request, response);
        } else {
            super.doFilterInternal(request, response, filterChain);
        }
    }
}