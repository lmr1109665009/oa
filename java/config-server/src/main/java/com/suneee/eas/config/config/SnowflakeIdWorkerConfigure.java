package com.suneee.eas.config.config;

import com.suneee.eas.common.component.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @user 子华
 * @created 2018/7/30
 */
@Configuration
public class SnowflakeIdWorkerConfigure {
    @Value("${id.workerId}")
    private Long workerId;
    @Value("${id.datacenterId}")
    private Long datacenterId;

    @Bean
    public SnowflakeIdWorker getSnowflakeIdWorker(){
        return new SnowflakeIdWorker(workerId,datacenterId);
    }

}
