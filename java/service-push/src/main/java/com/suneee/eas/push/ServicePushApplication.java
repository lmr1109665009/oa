package com.suneee.eas.push;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @description 阿里云推送调用中心启动类
 * @author liuhai
 * @Create 2018/06/21
 */
@EnableDiscoveryClient
@EnableEurekaClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ServicePushApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicePushApplication.class, args);
	}
}
