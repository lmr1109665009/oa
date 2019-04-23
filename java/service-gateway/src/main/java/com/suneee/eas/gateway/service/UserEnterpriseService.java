package com.suneee.eas.gateway.service;

import com.suneee.ucp.base.model.user.UserEnterprise;

import java.io.UnsupportedEncodingException;

/**
 * buc用户与企业关系service
 * @user 子华
 * @created 2018/9/12
 */
public interface UserEnterpriseService {

    /**
     * 根据用户ID和企业编码来获取企业信息
     * @param userId
     * @param enterpriseCode
     * @return
     */
    UserEnterprise getByUserIdAndCode(Long userId, String enterpriseCode) throws UnsupportedEncodingException;
}
