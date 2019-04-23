package com.suneee.eas.gateway.service.impl;

import com.suneee.eas.common.api.buc.UserApi;
import com.suneee.eas.common.constant.ServiceConstant;
import com.suneee.eas.common.utils.RestTemplateUtil;
import com.suneee.eas.gateway.service.PositionService;
import com.suneee.ucp.base.model.system.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;

/**
 * @user ouyang
 * @created 2018/9/18
 */
@Service
public class PositionServiceImpl implements PositionService {
    private Logger log= LogManager.getLogger(PositionServiceImpl.class);

    /**
     * 根据用户id获取主岗位。
     * @param userId
     * @return
     */
    @Override
    public Position getPrimaryPositionByUserId(Long userId)  throws UnsupportedEncodingException  {
        MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
        params.add("userId",userId);
        return RestTemplateUtil.get(ServiceConstant.getBucServiceUrl() +UserApi.getPrimaryPositionByUserId,Position.class,params);
    }
}
