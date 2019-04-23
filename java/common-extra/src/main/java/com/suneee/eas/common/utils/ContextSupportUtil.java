package com.suneee.eas.common.utils;

import com.suneee.core.api.org.model.ISysUser;
import com.suneee.eas.common.constant.UserConstant;
import com.suneee.platform.model.system.SysUser;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.model.system.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


/**
 * 系统上下文拓展工具类
 * @user 子华
 * @created 2018/7/31
 */
@Component
public class ContextSupportUtil {
    private static Environment env;
    //显示名称模式：字号或名字
    private static String usernameMode;
    //字号显示模式
    public static final String MODE_ALIAS="alias";

    @Autowired
    public void setEnv(Environment env) {
        ContextSupportUtil.env = env;
        ContextSupportUtil.usernameMode=env.getProperty("system.username-mode","");
    }

    /**
     * 获取当前用户
     * @return
     */
    public static SysUser getCurrentUser(){
        return (SysUser) ContextUtil.getSession().getAttribute(UserConstant.SESSION_USER_KEY);
    }

    /**
     * 获取当前用户ID
     * @return
     */
    public static Long getCurrentUserId(){
        return getCurrentUser().getUserId();
    }

    /**
     * 获取当前岗位
     * @return
     */
    public static Position getPosition(){
        return (Position) ContextUtil.getSession().getAttribute(UserConstant.SESSION_POSITION_KEY);
    }

    /**
     * 获取当前用户ID
     * @return
     */
    public static Long getCurrentPosId(){
        return getPosition().getPosId();
    }

    /**
     * 判断是否为字号模式
     * @return
     */
    public static boolean isAliasMode(){
        if (MODE_ALIAS.equalsIgnoreCase(usernameMode)){
            return true;
        }
        return false;
    }
    /**
     * 获取当前用户名
     * @return
     */
    public static String getCurrentUsername(){
        if (MODE_ALIAS.equalsIgnoreCase(usernameMode)){
            return getCurrentUser().getAliasName();
        }
        return getCurrentUser().getFullname();
    }

    /**
     * 根据显示名称模式获取用户名
     * @return
     */
    public static String getUsername(SysUser userInfo){
        if (MODE_ALIAS.equalsIgnoreCase(usernameMode)){
            return userInfo.getAliasName();
        }
        return userInfo.getFullname();
    }

    /**
     * 获取当前企业
     * @return
     */
    public static Enterpriseinfo getCurrentEnterprise(){
        return (Enterpriseinfo) ContextUtil.getSession().getAttribute(UserConstant.SESSION_ENTERPRISE_KEY);
    }

    /**
     * 获取当前企业编码
     * @return
     */
    public static String getCurrentEnterpriseCode(){
        return getCurrentEnterprise().getComp_code();
    }

    /**
     * 判断是否为超级管理员
     * @param user
     * @return
     */
    public static boolean isSuperAdmin(ISysUser user){
        String account=env.getProperty("system.super-admin","admin");
        if(account.equals(user.getAccount())){
            return true;
        }
        return false;
    }
    public static boolean isSuperAdmin(){
        return isSuperAdmin(getCurrentUser());
    }

}
