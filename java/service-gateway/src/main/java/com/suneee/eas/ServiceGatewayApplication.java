package com.suneee.eas;

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
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@EnableZuulProxy
@EnableEurekaClient
@ServletComponentScan(basePackages = {"com.suneee.eas.gateway.servlet"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ServiceGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceGatewayApplication.class, args);
	}

	/**
	 * 配置分布式session策略
	 * @return
	 */
	@Bean
	public HttpSessionIdResolver httpSessionIdResolver() {
		CookieHttpSessionIdResolver resolver=new CookieHttpSessionIdResolver();
		MyCookieSerializer serializer=new MyCookieSerializer();
		serializer.setCookieMaxAge(3600*12);
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
}
