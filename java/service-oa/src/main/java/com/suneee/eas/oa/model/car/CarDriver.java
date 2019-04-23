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
 * @date 2018/8/16 13:11
 */
public class CarDriver extends BaseModel{
    /**
     * 主键id
      */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id ;

    /**
     * 驾驶员姓名
     */
    private  String name;

    /**
     * 手机号码
     */
    private  String mobile;

    /**
     * 初次领证时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date licenseDate;

    /**
     * 证件有效期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date validDate;

    /**
     * 备注
     */
    private  String remarks;

    /**
     * 所属企业
     */
    private  String enterpriseCode;

    /**
     * 是否锁定
     */
    private Byte  isLock;

    /**
     * 是否删除：0=未删除，1=已删除
     */
    private Byte  isDelete;

    /**
     * 创建人id
     */
    private Long createBy ;
    
    /**
     * 更新人id
     */
    private Long updateBy ;

    /**
     * 驾龄
     *
     * @return
     */
    private Long drivingAge;
    

    /**
     * 
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long driverId ;

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }


    public Long getDrivingAge() {
        return drivingAge;
    }

    public void setDrivingAge(Long drivingAge) {
        this.drivingAge = drivingAge;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getLicenseDate() {
        return licenseDate;
    }

    public void setLicenseDate(Date licenseDate) {
        this.licenseDate = licenseDate;
    }

    public Date getValidDate() {
        return validDate;
    }

    public void setValidDate(Date validDate) {
        this.validDate = validDate;
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

    public Byte getIsLock() {
        return isLock;
    }

    public void setIsLock(Byte isLock) {
        this.isLock = isLock;
    }

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    
    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

   
    
    @Override
    public String toString(){
        return new ToStringBuilder(this)
                .append("id",this.id)
                .append("name",this.name)
                .append("mobile",this.mobile)
                .append("licenseDate",this.licenseDate)
                .append("validDate",this.validDate)
                .append("remarks",this.remarks)
                .append("enterpriseCode",this.enterpriseCode)
                .append("isLock",this.isLock)
                .append("isDelete",this.isDelete)
                .append("createBy",this.createBy)
                .append("createTime",this.createTime)
                .append("updateBy",this.updateBy)
                .append("updateTime",this.updateTime)
                .toString();
    }
}
