package com.suneee.ucp.mh.model.gatewayManage;

import java.util.List;

/**
 * 门户设置，自定义布局列BO
 * */
public class LayoutColVO<T> {

    //标识
    private Integer flag;

    //列模块名,如：快捷入口，日程管理
    private String name;

    private Integer width;

    //其它信息
    private transient List<T> extra;

    //其它信息JSON格式
    private String extraJSON;


    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<T> getExtra() {
        return extra;
    }

    public void setExtra(List<T> extra) {
        this.extra = extra;
    }

    public String getExtraJSON() {
        return extraJSON;
    }

    public void setExtraJSON(String extraJSON) {
        this.extraJSON = extraJSON;
    }
}
