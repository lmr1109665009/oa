package com.suneee.eas.oa.model.car;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;
import com.suneee.eas.common.model.BaseModel;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @user zousiyu
 * @date 2018/8/20 9:19
 */
public class OilCard  extends BaseModel{
    
    /**
     * 油卡id
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long cardId;

    /**
     * 油卡编号
     */
    private String cardNum;
    
    /**
     * 发卡日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    /**
     * 经手人Id
     */
    private Long handId;

    /**
     * 经手人姓名
     */
    private String handName;

    /**
     * 发行单位
     */
    private String issueUnit;

    /**
     * 保管人id
     */
    private Long  managerId;

    /**
     * 保管人姓名
     */
    private String managerName;

    /**
     * 使用状态：0：在用，1：闲置，2：挂失，3：停用
     */
    private Byte status;

    /**
     * 密码
     */
    private String password;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 所属企业
     */
    private String enterpriseCode;

    /**
     * 是否删除：0=未删除，1=已删除
     */
    private Byte isDelete;

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getHandId() {
        return handId;
    }

    public void setHandId(Long handId) {
        this.handId = handId;
    }

    public String getHandName() {
        return handName;
    }

    public void setHandName(String handName) {
        this.handName = handName;
    }

    public String getIssueUnit() {
        return issueUnit;
    }

    public void setIssueUnit(String issueUnit) {
        this.issueUnit = issueUnit;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }
    
    @Override
    public String toString(){
       return new ToStringBuilder("this")
                .append("cardNum",this.cardNum)
                .append("date",this.date)
                .append("handId",this.handId)
                .append("handName",this.handName)
                .append("issueUnit",this.issueUnit)
                .append("managerId",this.managerId)
                .append("managerName",this.managerName)
                .append("status",this.status)
                .append("password",this.password)
                .append("remarks",this.remarks)
                .append("enterpriseCode",this.enterpriseCode)
                .append("isDelete",this.isDelete)
                .append("createBy",this.createBy)
                .append("createTime",this.createTime)
                .append("updateBy",this.updateBy)
                .append("updateTime",this.updateTime)
                .toString();
    }
}
