package com.suneee.eas.gateway.service.impl;

import com.suneee.eas.common.api.buc.UserApi;
import com.suneee.eas.common.constant.ServiceConstant;
import com.suneee.eas.common.utils.RestTemplateUtil;
import com.suneee.eas.gateway.service.UserEnterpriseService;
import com.suneee.ucp.base.model.user.UserEnterprise;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;

/**
 * @user 子华
 * @created 2018/9/12
 */
@Service
public class UserEnterpriseServiceImpl implements UserEnterpriseService {

    @Override
    public UserEnterprise getByUserIdAndCode(Long userId, String enterpriseCode) throws UnsupportedEncodingException {
        MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
        params.add("userId",userId);
        params.add("enterpriseCode",enterpriseCode);
        return RestTemplateUtil.get(ServiceConstant.getBucServiceUrl() +UserApi.getUserEnterpriseByUserIdAndCode,UserEnterprise.class,params);
    }
}
