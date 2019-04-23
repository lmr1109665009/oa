/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: Inspection
 * Author:   lmr
 * Date:     2018/8/13 13:19
 * Description: 年检
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
import org.apache.xpath.operations.String;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 〈一句话功能简述〉<br> 
 * 〈年检〉
 *
 * @author lmr
 * @create 2018/8/13
 * @since 1.0.0
 */
public class CarInspection extends BaseModel {
    /**
     * 年检id(主键)
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long inspId;
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
     * 年检时间
     */
    @DateTimeFormat(pattern = DateUtil.FORMAT_DATE)
    private Date inspTime;
    /**
     * 到期时间
     */
    @DateTimeFormat(pattern = DateUtil.FORMAT_DATE)
    private Date expireTime;
    /**
     * 费用
     */
    private Double charge;
    /**
     * 年检地点
     */
    private String inspPlace;
    /**
     * 负责人id
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long respId;
    /**
     * 负责人姓名
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

    public Long getInspId() {
        return inspId;
    }

    public void setInspId(Long inspId) {
        this.inspId = inspId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
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

    public Date getInspTime() {
        return inspTime;
    }

    public void setInspTime(Date inspTime) {
        this.inspTime = inspTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Double getCharge() {
        return charge;
    }

    public void setCharge(Double charge) {
        this.charge = charge;
    }

    public String getInspPlace() {
        return inspPlace;
    }

    public void setInspPlace(String inspPlace) {
        this.inspPlace = inspPlace;
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