package com.suneee.ucp.mh.model.customColumn;

import java.util.List;

/**
 * 首页栏目中的数据
 * @author ytw
 * */
public class CustomLCTabVO {

    private Long tabId;

    private Long columnId;

    private String tabName;


    //对应tab下的数据
    private List<TabDataVO> data;

    public Long getTabId() {
        return tabId;
    }

    public void setTabId(Long tabId) {
        this.tabId = tabId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public List<TabDataVO> getData() {
        return data;
    }

    public void setData(List<TabDataVO> data) {
        this.data = data;
    }
}
