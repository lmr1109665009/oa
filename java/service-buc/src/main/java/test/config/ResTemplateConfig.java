package test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @program: ems
 * @description: 配置RestTemplate
 * @author: liuhai
 * @create: 2018-06-25 17:30
 **/
@Configuration
public class ResTemplateConfig {

    @Bean
    public RestTemplate createRestTemplate(){
        return new RestTemplate();
    }
}
