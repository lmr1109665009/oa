package com.suneee.weixin.model;

import com.suneee.platform.model.system.SysUser;

public class UserLoginMss {

    private SysUser sysUser;

    private String sessionId;

    private String userCenterId;

    public SysUser getSysUser() {
        return sysUser;
    }

    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserCenterId() {
        return userCenterId;
    }

    public void setUserCenterId(String userCenterId) {
        this.userCenterId = userCenterId;
    }
}
