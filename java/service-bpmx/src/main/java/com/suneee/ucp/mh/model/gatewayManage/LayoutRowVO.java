package com.suneee.ucp.mh.model.gatewayManage;

import java.util.List;

/**
 * 门户设置，自定义布局行BO
 * @author ytw
 * */
public class LayoutRowVO {

    //模板id
    private Long tempId;

    //列
    private List<LayoutColVO> items;

    public Long getTempId() {
        return tempId;
    }

    public void setTempId(Long tempId) {
        this.tempId = tempId;
    }

    public List<LayoutColVO> getItems() {
        return items;
    }

    public void setItems(List<LayoutColVO> items) {
        this.items = items;
    }
}
