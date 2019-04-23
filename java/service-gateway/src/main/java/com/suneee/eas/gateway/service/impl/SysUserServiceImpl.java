package com.suneee.eas.gateway.service.impl;

import com.suneee.eas.common.api.buc.UserApi;
import com.suneee.eas.common.constant.ServiceConstant;
import com.suneee.eas.common.utils.RestTemplateUtil;
import com.suneee.eas.gateway.service.SysUserService;
import com.suneee.platform.model.system.SysUser;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;

/**
 * @user 子华
 * @created 2018/9/12
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Override
    public SysUser getByAccount(String account) throws UnsupportedEncodingException {
        MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
        params.add("account",account);
        return RestTemplateUtil.get(ServiceConstant.getBucServiceUrl() +UserApi.getUserByAccount,SysUser.class,params);
    }
}
