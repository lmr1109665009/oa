package com.suneee.eas.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.suneee.eas.gateway.component.AuthenticationProperty;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 网关基础过滤器
 * @user 子华
 * @created 2018/9/17
 */
public abstract class BaseZuulFilter extends ZuulFilter {
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 获取请求上下文对象
     * @return
     */
    protected RequestContext getContext(){
        return RequestContext.getCurrentContext();
    }

    /**
     * 获取Request对象
     * @return
     */
    protected HttpServletRequest getRequest(){
        return getContext().getRequest();
    }

    /**
     * 获取Response对象
     * @return
     */
    protected HttpServletResponse getResponse(){
        return getContext().getResponse();
    }

    /**
     * 获取Session对象
     * @return
     */
    protected HttpSession getSession(){
        return getRequest().getSession();
    }

    /**
     * 获取参数中企业编码
     * @return
     */
    protected String getParamEnterpriseCode() {
        // 获取用户所属企业：从请求头中获取企业编码，当请求头中不存在时，获取用户所属企业信息中的第一个
        HttpServletRequest request=getRequest();
        String enterpriseCode = request.getHeader("enterpriseCode");
        if(StringUtils.isBlank(enterpriseCode)){
            enterpriseCode = request.getParameter("enterpriseCode");
        }
        return enterpriseCode;
    }
}
