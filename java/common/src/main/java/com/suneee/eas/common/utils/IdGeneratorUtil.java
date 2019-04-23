package com.suneee.eas.common.utils;

import com.suneee.eas.common.api.config.IdGeneratorApi;
import com.suneee.eas.common.constant.ServiceConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * ID生成工具类
 * @user 子华
 * @created 2018/8/1
 */
@Component
public class IdGeneratorUtil {
    private static RestTemplate template;

    @Autowired
    public void setTemplate(RestTemplate template) {
        IdGeneratorUtil.template = template;
    }

    /**
     * 生成唯一ID
     * @return
     */
    public static Long getNextId(){
        Long id=template.getForObject(ServiceConstant.getConfigServiceUrl() +IdGeneratorApi.getNextId,Long.class);
        return id;
    }
}
