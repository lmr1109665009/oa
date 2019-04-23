package com.suneee.ucp.mh.model.customColumn;

import com.suneee.ucp.base.model.UcpBaseModel;

/**
 * 自定义栏目下的tab
 * @author ytw
 * */
public class CustomColumn extends UcpBaseModel{

    private Long id;


    //栏目别名
    private Long columnId;

    //顺序
    private Integer sn;

    //tab名字
    private String tabName;

    //流程定义id，逗号分隔多个
    private String data;

    //流程name，逗号分隔多个，和defId顺序相同
    private String dataName;

    //0-未删除，1-已删除
    private Integer isDelete;
    private String type;
    //栏目的类型
    private String columnType;

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
