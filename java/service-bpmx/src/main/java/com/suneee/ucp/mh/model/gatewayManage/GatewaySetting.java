package com.suneee.ucp.mh.model.gatewayManage;
import com.suneee.ucp.base.model.UcpBaseModel;

import java.io.Serializable;
import java.util.Date;

/**
*   门户设置po
*  @author ytw
 *
*/
public class GatewaySetting extends UcpBaseModel implements Serializable {

    private static final long serialVersionUID = 1524028179030L;


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
    private Long gatewayType;

    /**
    * 所属公司编码
    * isNullAble:0
    */
    private String orgCode;

    /**
    * 模板
    * isNullAble:0
    */
    private String templet;

    /**
    * 内容，格式如下
    *    [
            {
                 "tempId": "1",
                 "items": [{
                 "flag": 40,
                 "name": "快捷入口",
                 "extra": [1, 2, 3, 4, 5]
                 }]
            },
            {
             "tempId": "2",
             "items": [
                {
                 "name": "日程管理",
                 "flag": 30
                },
                {
                 "name": "已办事宜",
                 "flag": 10
                }
                ]
            }
         ]
    */
    private String contentJSON;

    //内容，展示用
    private String contentView;
    /**
    * 对应职位级别的value
    * isNullAble:0
    */
    private Integer type;

    /**
    * 
    * isNullAble:0,defaultVal:CURRENT_TIMESTAMP
    */
    private Date createtime;

    /**
    * 
    * isNullAble:0,defaultVal:0000-00-00 00:00:00
    */
    private Date updatetime;

    /**
    * 
    * isNullAble:0
    */
    private Long createBy;

    /**
    * 
    * isNullAble:1
    */
    private Long updateBy;

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getContentView() {
        return contentView;
    }

    public void setContentView(String contentView) {
        this.contentView = contentView;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getContentJSON() {
        return contentJSON;
    }

    public void setContentJSON(String contentJSON) {
        this.contentJSON = contentJSON;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getId(){
        return this.id;
    }

    public void setGatewayName(String gatewayName){
        this.gatewayName = gatewayName;
    }

    public String getGatewayName(){
        return this.gatewayName;
    }

    public void setGatewayType(Long gatewayType){
        this.gatewayType = gatewayType;
    }

    public Long getGatewayType(){
        return this.gatewayType;
    }


    public void setTemplet(String templet){
        this.templet = templet;
    }

    public String getTemplet(){
        return this.templet;
    }

    public void setType(Integer type){
        this.type = type;
    }

    public Integer getType(){
        return this.type;
    }


    @Override
    public String toString() {
        return "GatewaySetting{" +
                "id=" + id +
                ", gatewayName='" + gatewayName + '\'' +
                ", gatewayType=" + gatewayType +
                ", orgCode=" + orgCode +
                ", templet='" + templet + '\'' +
                ", contentJSON='" + contentJSON + '\'' +
                ", contentView='" + contentView + '\'' +
                ", type=" + type +
                ", createtime=" + createtime +
                ", updatetime=" + updatetime +
                ", createBy=" + createBy +
                ", updateBy=" + updateBy +
                ", createBy=" + createBy +
                ", createtime=" + createtime +
                ", updatetime=" + updatetime +
                ", updateBy=" + updateBy +
                '}';
    }
}
