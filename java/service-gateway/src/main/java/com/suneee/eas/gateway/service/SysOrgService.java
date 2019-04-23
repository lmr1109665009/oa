package com.suneee.eas.gateway.service;

import com.suneee.platform.model.system.SysOrg;

import java.io.UnsupportedEncodingException;

/**
 * @user 子华
 * @created 2018/10/9
 */
public interface SysOrgService {
    /**
     * 根据用户ID获取主组织
     * @param userId
     * @return
     */
    public SysOrg getPrimaryOrgByUserId(Long userId) throws UnsupportedEncodingException;
}
