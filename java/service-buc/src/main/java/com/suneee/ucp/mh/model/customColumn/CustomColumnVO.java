package com.suneee.ucp.mh.model.customColumn;

import com.suneee.core.model.BaseModel;

import java.util.List;

/**
 * 自定义栏目VO
 *
 * @author lmr
 */
public class CustomColumnVO extends BaseModel {
    ///主键
    private Long id;
    //名字
    private String name;

    //自定义的栏目拥有者
    private Long owner;

    //数据来源  sys为系统数据
    private String source;

    //刷新时间
    private Long refreshTime;

    //图标
    private String icon;

    //颜色
    private String color;

    //对应基础表的数据
    private Long columnId;
    //类型
    private String columnType;
    //名称
    private String columnName;
    //别名
    private String columnAlias;
    //模板html
    private String templateHtml;
    //高度
    private Long high;
    //查看的字段选择
    private String view;
    //排序
    private Long sn;
     // 企业编码
    private String enterpriseCode;
    //0禁用1启用
    private int status;
    //栏目下的tab
    private List<CustomColumn> customTabList;
    //所在门户
    private Long gatewayId;
    //备注描述
    private String remarks;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getColumnAlias() {
        return columnAlias;
    }

    public void setColumnAlias(String columnAlias) {
        this.columnAlias = columnAlias;
    }

    public String getTemplateHtml() {
        return templateHtml;
    }

    public void setTemplateHtml(String templateHtml) {
        this.templateHtml = templateHtml;
    }

    public Long getHigh() {
        return high;
    }

    public void setHigh(Long high) {
        this.high = high;
    }

    public Long getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(Long gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public List<CustomColumn> getCustomTabList() {
        return customTabList;
    }

    public void setCustomTabList(List<CustomColumn> customTabList) {
        this.customTabList = customTabList;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Long getSn() {
        return sn;
    }

    public void setSn(Long sn) {
        this.sn = sn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(Long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
