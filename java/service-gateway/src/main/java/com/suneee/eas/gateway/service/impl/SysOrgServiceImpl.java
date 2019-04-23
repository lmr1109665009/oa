package com.suneee.eas.gateway.service.impl;

import com.suneee.eas.common.api.buc.UserApi;
import com.suneee.eas.common.constant.ServiceConstant;
import com.suneee.eas.common.utils.RestTemplateUtil;
import com.suneee.eas.gateway.service.SysOrgService;
import com.suneee.platform.model.system.SysOrg;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;

/**
 * 用户组织service
 * @user 子华
 * @created 2018/10/9
 */
@Service
public class SysOrgServiceImpl implements SysOrgService {
    @Override
    public SysOrg getPrimaryOrgByUserId(Long userId) throws UnsupportedEncodingException {
        MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
        params.add("userId",userId);
        return RestTemplateUtil.get(ServiceConstant.getBucServiceUrl() + UserApi.getPrimaryOrgByUserId, SysOrg.class,params);
    }
}
