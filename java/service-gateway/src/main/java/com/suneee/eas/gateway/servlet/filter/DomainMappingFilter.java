package com.suneee.eas.gateway.servlet.filter;

import com.suneee.eas.common.utils.StringUtil;
import com.suneee.eas.gateway.filter.ApiAuthenticationFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * 域名与服务名映射过滤器
 * @user 子华
 * @created 2018/9/19
 */
@Order(0)
@Component
@WebFilter(urlPatterns = {"/**"})
public class DomainMappingFilter implements Filter {
    private Logger log= LogManager.getLogger(DomainMappingFilter.class);
    @Autowired
    private Environment env;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        String domain=request.getServerName();
        domain=domain.replace('.','_');
        String runtimeEnv=env.getProperty("system.domain."+ domain);
        //如果根据域名获取不到就根据IP来获取运行环境配置
        if (StringUtil.isEmpty(runtimeEnv)){
            runtimeEnv=env.getProperty("system.domain.ip_"+ domain);
        }
        log.debug("当前运行环境："+runtimeEnv);
        log.debug("请求URL："+request.getRequestURL());
        Enumeration<String> paraNames=request.getParameterNames();
        List<String> params=new ArrayList<>();
        for(;paraNames.hasMoreElements();){
            String key=paraNames.nextElement();
            String val=request.getParameter(key);
            params.add(key+"="+val);
        }
        log.debug("请求参数："+ StringUtils.join(params,","));
        if (StringUtil.isNotEmpty(runtimeEnv)){
            request.setAttribute("runtimeEnv",runtimeEnv);
        }
        log.debug("---------------------request header-------------------------------------");
        //获取所有的消息头名称
        Enumeration<String> headerNames = request.getHeaderNames();
        //获取获取的消息头名称，获取对应的值，并输出
        while(headerNames.hasMoreElements()){
            String nextElement = headerNames.nextElement();
            log.debug(nextElement+":"+request.getHeader(nextElement));
        }
        log.debug("----------------------------------------------------------");
        filterChain.doFilter(servletRequest,servletResponse);

        //如果是第三方系统访问则销毁session
        String authorizeToken=request.getHeader(ApiAuthenticationFilter.API_AUTHORIZE_TOKEN);
        if (StringUtil.isEmpty(authorizeToken)){
            authorizeToken=request.getParameter(ApiAuthenticationFilter.API_AUTHORIZE_TOKEN);
        }
        if (!StringUtil.isEmpty(authorizeToken)){
            request.getSession().invalidate();
        }
    }

    @Override
    public void destroy() {

    }
}
