package com.suneee.ucp.mh.model.gatewayManage;

public class GatewaySettingVO {

    /**
     * 主键
     *
     * isNullAble:0
     */
    private Long id;

    /**
     * 门户名称
     * isNullAble:1
     */
    private String gatewayName;

    /**
     * 门户类型
     * isNullAble:0
     */
    private String gatewayTypeName;

    /**
     * 门户类型
     * isNullAble:0
     */
    private Long gatewayType;

    /**
     * 对应职位级别的value
     * isNullAble:0
     */
    private Integer type;

    /**
     * 所属公司
     * isNullAble:0
     */
    private String companyName;

    /**
     * 模板
     * isNullAble:0
     */
    private String templet;

    //内容
    private String content;

    //json格式布局信息
    private String layoutJSON;

    public Long getGatewayType() {
        return gatewayType;
    }

    public void setGatewayType(Long gatewayType) {
        this.gatewayType = gatewayType;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getLayoutJSON() {
        return layoutJSON;
    }

    public void setLayoutJSON(String layoutJSON) {
        this.layoutJSON = layoutJSON;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getGatewayTypeName() {
        return gatewayTypeName;
    }

    public void setGatewayTypeName(String gatewayTypeName) {
        this.gatewayTypeName = gatewayTypeName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTemplet() {
        return templet;
    }

    public void setTemplet(String templet) {
        this.templet = templet;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
