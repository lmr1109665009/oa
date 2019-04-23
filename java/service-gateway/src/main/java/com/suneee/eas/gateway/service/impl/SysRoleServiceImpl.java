package com.suneee.eas.gateway.service.impl;

import com.suneee.eas.common.api.oa.RoleResourceApi;
import com.suneee.eas.common.constant.ServiceConstant;
import com.suneee.eas.common.utils.RestTemplateUtil;
import com.suneee.eas.gateway.service.SysRoleService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @user 子华
 * @created 2018/9/12
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Override
    public List<String> getRolesByUserId(Long userId,String enterpriseCode) throws UnsupportedEncodingException {
        MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
        params.add("userId",userId);
        params.add("enterpriseCode",enterpriseCode);
        ResponseEntity<List<String>> responseEntity= (ResponseEntity<List<String>>) RestTemplateUtil.get(ServiceConstant.getOaServiceUrl() +RoleResourceApi.getRolesByUserId, new ParameterizedTypeReference<List<String>>() {},params);
        return responseEntity.getBody();
    }
}
