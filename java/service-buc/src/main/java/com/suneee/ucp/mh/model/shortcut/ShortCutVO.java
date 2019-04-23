package com.suneee.ucp.mh.model.shortcut;

import java.io.Serializable;

/**
 * 快捷入口VO
 * @author ytw
 * date:2018.04.04
 * */
public class ShortCutVO extends ShortCut implements Serializable{

    //未读消息数量
    private int num;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "ShortCutVO{" +
                "num=" + num +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", router='" + router + '\'' +
                ", icon='" + icon + '\'' +
                ", description='" + description + '\'' +
                ", order=" + order +
                ", isDelete=" + isDelete +
                ", createBy=" + createBy +
                ", createtime=" + createtime +
                ", updatetime=" + updatetime +
                ", updateBy=" + updateBy +
                '}';
    }
}
