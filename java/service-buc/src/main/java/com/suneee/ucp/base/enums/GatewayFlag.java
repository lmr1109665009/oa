package com.suneee.ucp.base.enums;

/**
 * 门户设置布局中标识
 * @author ytw
 * Date: 2018-04-23
 * */
public enum GatewayFlag {

    ALREADY(10, "已办事宜"),
    PENDDING(20, "待办事宜"),
    SHORT_CUT(40, "快捷入口");


    private int code;

    private String desc;

    GatewayFlag(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    GatewayFlag() {
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
