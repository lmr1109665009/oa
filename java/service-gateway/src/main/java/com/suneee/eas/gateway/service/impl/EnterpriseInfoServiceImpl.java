package com.suneee.eas.gateway.service.impl;

import com.suneee.eas.common.api.buc.UserApi;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.ServiceConstant;
import com.suneee.eas.common.utils.RestTemplateUtil;
import com.suneee.eas.gateway.service.EnterpriseInfoService;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.model.system.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @user 子华
 * @created 2018/9/12
 */
@Service
public class EnterpriseInfoServiceImpl implements EnterpriseInfoService {
    private Logger log= LogManager.getLogger(EnterpriseInfoServiceImpl.class);

    @Override
    public Enterpriseinfo getByCompCode(String enterpriseCode) throws UnsupportedEncodingException {
        MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
        params.add("enterpriseCode",enterpriseCode);
        return RestTemplateUtil.get(ServiceConstant.getBucServiceUrl() +UserApi.getEnterpriseByCompCode,Enterpriseinfo.class,params);
    }

    @Override
    public List<Enterpriseinfo> getByUserId(Long userId) throws UnsupportedEncodingException {
        MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
        params.add("userId",userId);
        ResponseEntity<ResponseMessage<List<Enterpriseinfo>>> entity= (ResponseEntity<ResponseMessage<List<Enterpriseinfo>>>) RestTemplateUtil.get(ServiceConstant.getBucServiceUrl()+UserApi.getEnterpriseListByUserId, new ParameterizedTypeReference<ResponseMessage<List<Enterpriseinfo>>>() {},params);
        if (entity==null||entity.getBody()==null){
            log.error("根据用户ID（"+userId+"）获取企业信息列表为空");
            return null;
        }
        if (entity.getBody().getStatus()==ResponseMessage.STATUS_FAIL){
            log.error("根据用户ID（"+userId+"）获取企业信息列表异常，errorCode:"+entity.getBody().getMessage());
            return null;
        }
        return entity.getBody().getData();
    }

    @Override
    public Position getOrgByUserId(Long userId) throws UnsupportedEncodingException {
        MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
        params.add("userId",userId);
        ResponseEntity<ResponseMessage<Position>> entity= (ResponseEntity<ResponseMessage<Position>>) RestTemplateUtil.get(ServiceConstant.getBucServiceUrl()+UserApi.getOrgByUserId, new ParameterizedTypeReference<ResponseMessage<Position>>() {},params);
        if (entity==null||entity.getBody()==null){
            log.error("根据用户ID（"+userId+"）获取组织列表为空");
            return null;
        }
        if (entity.getBody().getStatus()==ResponseMessage.STATUS_FAIL){
            log.error("根据用户ID（"+userId+"）获取组织信息列表异常，errorCode:"+entity.getBody().getMessage());
            return null;
        }
        return  entity.getBody().getData();
    }
}
