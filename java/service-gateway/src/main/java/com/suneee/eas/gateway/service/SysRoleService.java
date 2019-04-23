package com.suneee.eas.gateway.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * buc系统角色service
 * @user 子华
 * @created 2018/9/12
 */
public interface SysRoleService {

    /**
     * 根据用户ID来获取角色
     * @param userId
     * @return
     */
    List<String> getRolesByUserId(Long userId,String enterpriseCode) throws UnsupportedEncodingException;
}
