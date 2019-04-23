package com.suneee.weixin.model;

import com.suneee.platform.model.system.SysUser;

public class UserResult extends SysUser {

    private String isInterna;

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    private String authId;



    public String getIsInterna() {
        return isInterna;
    }

    public void setIsInterna(String isInterna) {
        this.isInterna = isInterna;
    }
}
