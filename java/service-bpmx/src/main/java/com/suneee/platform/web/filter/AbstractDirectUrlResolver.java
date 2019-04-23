package com.suneee.platform.web.filter;

import javax.servlet.http.HttpServletRequest;

/**
 * 实现DirectUrlResolver接口抽象类
 * by cjj
 */
public abstract class AbstractDirectUrlResolver implements DirectUrlResolver {

    protected String pattern;  
    protected String directUrl;  
  
    @Override  
    public abstract boolean support(HttpServletRequest request);  
  
    @Override  
    public String directUrl() {  
        return this.directUrl;  
    }  
  
    public void setPattern(String pattern) {  
        this.pattern = pattern;  
    }  
  
    public void setDirectUrl(String directUrl) {  
        this.directUrl = directUrl;  
    }

}
