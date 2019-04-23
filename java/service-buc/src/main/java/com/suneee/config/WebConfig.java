package com.suneee.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    /**
     * 添加对jsp的支持
     *
     * @return
     */
    @Bean
    public ViewResolver getJspViewResolver() {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix("/WEB-INF/view/");
        internalResourceViewResolver.setSuffix(".jsp");
        internalResourceViewResolver.setOrder(1);
        return internalResourceViewResolver;
    }

    /**
     * 添加对Freemarker支持
     *
     * @return
     */
    @Bean
    public FreeMarkerViewResolver getFreeMarkerViewResolver() {
        FreeMarkerViewResolver freeMarkerViewResolver = new FreeMarkerViewResolver();
        freeMarkerViewResolver.setCache(true);
        freeMarkerViewResolver.setSuffix(".ftl");
        freeMarkerViewResolver.setRequestContextAttribute("request");
        freeMarkerViewResolver.setOrder(0);
        freeMarkerViewResolver.setContentType("text/html;charset=UTF-8");
        return freeMarkerViewResolver;
    }

   /* @Bean
    public FilterRegistrationBean indexFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new SsoFilter());
       *//* registration.addUrlPatterns("");*//*
        registration.addUrlPatterns("/");
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/buc/weixin/login.ht");
        registration.setUrlPatterns(urlPatterns);
        return registration;
    }*/

    /**
     * 添加自定义拦截器(实现切源)
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DataSourceIntercepter()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }

}
