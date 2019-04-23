package com.suneee.eas.gateway.component;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证配置文件
 * @user 子华
 * @created 2018/9/21
 */
@Component
@ConfigurationProperties(prefix = "zuul.authentication")
public class AuthenticationProperty {
    private List<String> ignoreUrls;
    private List<String> xssIgnoreUrls;
    private Map<String,List<String>> authorizeUrls=new HashMap<>();

    public List<String> getIgnoreUrls() {
        return ignoreUrls;
    }

    public void setIgnoreUrls(List<String> ignoreUrls) {
        this.ignoreUrls = ignoreUrls;
    }

    public List<String> getXssIgnoreUrls() {
        return xssIgnoreUrls;
    }

    public void setXssIgnoreUrls(List<String> xssIgnoreUrls) {
        this.xssIgnoreUrls = xssIgnoreUrls;
    }

    public Map<String, List<String>> getAuthorizeUrls() {
        return authorizeUrls;
    }

    public void setAuthorizeUrls(Map<String, List<String>> authorizeUrls) {
        this.authorizeUrls = authorizeUrls;
    }
}
