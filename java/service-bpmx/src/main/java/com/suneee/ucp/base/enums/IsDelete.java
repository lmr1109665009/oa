package com.suneee.ucp.base.enums;

/**
 * 删除标识
 * @author ytw
 * */
public enum IsDelete {

    DEL(1, "已删除"),
    NOT_DEL(0, "未删除");

    IsDelete(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;

    private String desc;


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
