package com.suneee.eas.oa.service.user.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.suneee.eas.common.api.buc.UserApi;
import com.suneee.eas.common.constant.ServiceConstant;
import com.suneee.eas.common.utils.RestTemplateUtil;
import com.suneee.eas.oa.service.user.UserPositionService;
import com.suneee.platform.model.system.UserPosition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * 用户岗位信息service
 * @user 子华
 * @created 2018/8/22
 */
@Service
public class UserPositionServiceImpl implements UserPositionService {
    private Logger log= LogManager.getLogger(UserPositionServiceImpl.class);
    @Override
    public List<UserPosition> getByAccount(String account) {
        MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
        params.add("account",account);
        log.info("Request Url: " + ServiceConstant.getBucServiceUrl() +UserApi.batchFindUser + ", request parameter: " + params);
        JSONObject result = RestTemplateUtil.post(ServiceConstant.getBucServiceUrl()+UserApi.getPositionListByAccount, JSONObject.class, params);
        log.info("Response: " + result);
        if(result.getIntValue("status") == 0){
            JSONArray array = result.getJSONArray("data");
            return array.toJavaList(UserPosition.class);
        } else {
            throw new RuntimeException("Failed to obtain the org list of user belong to : " + result.getString("message"));
        }
    }
}
