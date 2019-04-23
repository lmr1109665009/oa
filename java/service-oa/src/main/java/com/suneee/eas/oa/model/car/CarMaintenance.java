/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: Maintenance
 * Author:   lmr
 * Date:     2018/8/13 11:50
 * Description: 维护/保养
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
 * 〈维护/保养〉
 *
 * @author lmr
 * @create 2018/8/13
 * @since 1.0.0
 */
public class CarMaintenance extends BaseModel {
   public static final Byte MAINT_TYPE_WX=0;
   public static final Byte MAINT_TYPE_BY=1;
    /**
     * 车辆维护保养id
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long maintId;
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
     * 送修时间
     */
    @DateTimeFormat(pattern = DateUtil.FORMAT_DATE)
    private Date maintDate;
    /**
     * 取回时间
     */
    @DateTimeFormat(pattern = DateUtil.FORMAT_DATE)
    private Date backDate;
    /**
     * 维修地点
     */
    private String maintPlace;
    /**
     * 下次保养公里数
     */
    private String nextMaintMileage;
    /**
     * 下次保养时间
     */
    @DateTimeFormat(pattern = DateUtil.FORMAT_DATE)
    private Date nextMaintDate;
    /**
     * 费用
     */
    private Double charge;
    /**
     * 维护类型：0：维修，1：保养
     */
    private Byte type;
    /**
     * 维护项目
     */
    private String maintItem;
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

    public Long getMaintId() {
        return maintId;
    }

    public void setMaintId(Long maintId) {
        this.maintId = maintId;
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

    public Date getMaintDate() {
        return maintDate;
    }

    public void setMaintDate(Date maintDate) {
        this.maintDate = maintDate;
    }

    public Date getBackDate() {
        return backDate;
    }

    public void setBackDate(Date backDate) {
        this.backDate = backDate;
    }

    public String getMaintPlace() {
        return maintPlace;
    }

    public void setMaintPlace(String maintPlace) {
        this.maintPlace = maintPlace;
    }

    public String getNextMaintMileage() {
        return nextMaintMileage;
    }

    public void setNextMaintMileage(String nextMaintMileage) {
        this.nextMaintMileage = nextMaintMileage;
    }

    public Date getNextMaintDate() {
        return nextMaintDate;
    }

    public void setNextMaintDate(Date nextMaintDate) {
        this.nextMaintDate = nextMaintDate;
    }

    public Double getCharge() {
        return charge;
    }

    public void setCharge(Double charge) {
        this.charge = charge;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getMaintItem() {
        return maintItem;
    }

    public void setMaintItem(String maintItem) {
        this.maintItem = maintItem;
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