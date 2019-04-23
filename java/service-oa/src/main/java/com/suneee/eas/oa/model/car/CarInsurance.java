/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: Insurance
 * Author:   lmr
 * Date:     2018/8/13 11:11
 * Description: 保险
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.model.car;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;
import com.suneee.eas.common.model.BaseModel;
import com.suneee.eas.common.utils.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 〈一句话功能简述〉<br> 
 * 〈保险〉
 *
 * @author lmr
 * @create 2018/8/13
 * @since 1.0.0
 */
public class CarInsurance extends BaseModel {
    /**
     * 保险id(主键)
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long insurId;
    /**
     * 车辆id
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long carId;
    /**
     * 车牌号
     */
    private String plateNum;
    /**
     * 车辆名称
     */
    private String carName;
    /**
     * 保单号
     */
    private String insurNum;
    /**
     * 投保日期
     */
    @DateTimeFormat(pattern = DateUtil.FORMAT_DATE)
    private Date insurDate;
    /**
     * 到期日期
     */
    @DateTimeFormat(pattern = DateUtil.FORMAT_DATE)
    private Date expireDate;
    /**
     * 投保金额
     */
    private  Double charge;
    /**
     * 投保公司
     */
    private String insurComp;
    /**
     * 责任人id
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long respId;
    /**
     * 责任人姓名
     */
    private String respName;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 是否删除：0=未删除，1=已删除
     */
    private Byte isDelete;
    /**
     * 所属企业
     */
    private String enterpriseCode;

    public Long getInsurId() {
        return insurId;
    }

    public void setInsurId(Long insurId) {
        this.insurId = insurId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getInsurNum() {
        return insurNum;
    }

    public void setInsurNum(String insurNum) {
        this.insurNum = insurNum;
    }

    public String getPlateNum() {
        return plateNum;
    }

    public void setPlateNum(String plateNum) {
        this.plateNum = plateNum;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public Date getInsurDate() {
        return insurDate;
    }

    public void setInsurDate(Date insurDate) {
        this.insurDate = insurDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public Double getCharge() {
        return charge;
    }

    public void setCharge(Double charge) {
        this.charge = charge;
    }

    public String getInsurComp() {
        return insurComp;
    }

    public void setInsurComp(String insurComp) {
        this.insurComp = insurComp;
    }

    public Long getRespId() {
        return respId;
    }

    public void setRespId(Long respId) {
        this.respId = respId;
    }

    public String getRespName() {
        return respName;
    }

    public void setRespName(String respName) {
        this.respName = respName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }
}