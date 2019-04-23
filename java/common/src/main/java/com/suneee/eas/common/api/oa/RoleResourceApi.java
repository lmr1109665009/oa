package com.suneee.eas.common.api.oa;

import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;

/**
 * OA系统角色资源api接口
 * @user 子华
 * @created 2018/9/12
 */
public class RoleResourceApi {
    /**
     * 检查角色是否拥有资源权限
     */
    public static final String checkRoleResources= ModuleConstant.SYSTEM_MODULE + FunctionConstant.RESOURCE_MANAGER + "checkRoleResources";
    /**
     * 根据用户ID获取角色列表
     */
    public static final String getRolesByUserId= ModuleConstant.SYSTEM_MODULE + FunctionConstant.ROLE_RESOURCES + "getRolesByUserId";

    /**
     * 根据用户ID获取角色ids列表
     */
    public static final String getRoleIdsByUserId= ModuleConstant.SYSTEM_MODULE + FunctionConstant.ROLE_RESOURCES + "getRoleIdsByUserId";
}
