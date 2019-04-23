package com.suneee.ucp.mh.model.shortcut;

import com.suneee.ucp.base.model.UcpBaseModel;

/**
 * 快捷入口po
 * @author ytw
 * */
public class ShortCut extends UcpBaseModel{

    protected Long id;


    //快捷入口name
    protected String name;

    //别名标识
    protected String router;

    //图标
    protected String icon;

    //描述
    protected String description;

    //顺序
    protected Integer order;

    //0-未删除，1-已删除
    protected Integer isDelete;

    //公司id
    private String enterpriseCode;

    //颜色
    private String color;

    //颜色
    private String mobileItem;

    private Long gatewayId;

    public Long getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(Long gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobileItem() {
        return mobileItem;
    }

    public void setMobileItem(String mobileItem) {
        this.mobileItem = mobileItem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public String getRouter() {
        return router;
    }

    public void setRouter(String router) {
        this.router = router;
    }

    @Override
    public String toString() {
        return "ShortCut{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", router='" + router + '\'' +
                ", icon='" + icon + '\'' +
                ", description='" + description + '\'' +
                ", order=" + order +
                ", isDelete=" + isDelete +
                ", enterpriseCode=" + enterpriseCode +
                ", color='" + color + '\'' +
                ", mobileItem='" + mobileItem + '\'' +
                ", gatewayId=" + gatewayId +
                '}';
    }
}
