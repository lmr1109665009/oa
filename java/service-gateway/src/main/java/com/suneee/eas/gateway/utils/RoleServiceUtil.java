package com.suneee.eas.gateway.utils;

import com.suneee.eas.gateway.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @user 子华
 * @created 2018/9/14
 */
@Configuration
public class RoleServiceUtil {
    private static SysRoleService roleService;

    @Autowired
    public void setRoleService(SysRoleService roleService) {
        RoleServiceUtil.roleService = roleService;
    }

    public static SysRoleService getRoleService() {
        return roleService;
    }
}
