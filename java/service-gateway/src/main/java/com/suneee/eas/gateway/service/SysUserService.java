package com.suneee.eas.gateway.service;

import com.suneee.platform.model.system.SysUser;

import java.io.UnsupportedEncodingException;

/**
 * buc系统用户service
 * @user 子华
 * @created 2018/9/12
 */
public interface SysUserService {
    /**
     * 根据账号获取系统用户信息
     * @param account
     * @return
     */
    SysUser getByAccount(String account) throws UnsupportedEncodingException;
}
