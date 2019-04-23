package com.suneee;

import com.suneee.eas.common.component.MyCookieSerializer;
import com.suneee.eas.common.component.RestTemplateInterceptor;
import com.suneee.eas.common.utils.RestTemplateUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class})
@EnableEurekaClient
@ServletComponentScan({"com.suneee"})
@ImportResource(locations={"classpath:/conf/app-context.xml"})
public class ServiceBucApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceBucApplication.class, args);
    }


    /**
     * 配置分布式session策略
     *
     * @return
     */
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        CookieHttpSessionIdResolver resolver = new CookieHttpSessionIdResolver();
        MyCookieSerializer serializer = new MyCookieSerializer();
        serializer.setCookieMaxAge(3600 * 12);
        serializer.setCookiePath("/");
        resolver.setCookieSerializer(serializer);

        return resolver;
    }

    /**
     * 配置RestTemplate
     * @return
     */
    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(){
        RestTemplate restTemplate=new RestTemplate();
        List<ClientHttpRequestInterceptor> list=new ArrayList<>();
        list.add(new RestTemplateInterceptor());
        restTemplate.setInterceptors(list);
        RestTemplateUtil.setTemplate(restTemplate);
        return restTemplate;
    }

    /**
     * 解决springboot @value 多properties获取value问题
     * Author: ouyang
     * Date: 2018-09-20 10:13
     * @return PropertySourcesPlaceholderConfigurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
        c.setIgnoreUnresolvablePlaceholders(true);
        return c;
    }

}