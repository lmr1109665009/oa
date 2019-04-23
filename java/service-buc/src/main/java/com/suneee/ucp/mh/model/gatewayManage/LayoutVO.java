package com.suneee.ucp.mh.model.gatewayManage;

import java.util.List;

/**
* 门户设置，布局VO
 * @author ytw
 * */
public class LayoutVO {

    //门户名称
    private String gatewayName;

    //门户类型
    private String gatewayType;

    //类型，1-领导的门户模板，2-普通员工的门户模板
    private Integer type;

    //布局中的每一行
    private List<LayoutRowVO> rows;

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getGatewayType() {
        return gatewayType;
    }

    public void setGatewayType(String gatewayType) {
        this.gatewayType = gatewayType;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<LayoutRowVO> getRows() {
        return rows;
    }

    public void setRows(List<LayoutRowVO> rows) {
        this.rows = rows;
    }


}
