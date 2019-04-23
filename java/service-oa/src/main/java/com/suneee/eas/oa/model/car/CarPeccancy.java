/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: Peccancy
 * Author:   lmr
 * Date:     2018/8/13 13:37
 * Description: 违章/事故
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
 * 〈违章/事故〉
 *
 * @author lmr
 * @create 2018/8/13
 * @since 1.0.0
 */
public class CarPeccancy extends BaseModel {

    /**
     * 违章事故id
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long peccId;
    /**
     * 违章车辆id
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
     * 违章时间
     */
    @DateTimeFormat(pattern = DateUtil.FORMAT_DATETIME_WITHOUT_SECONDS)
    private Date peccTime;
    /**
     * 违章地点
     */
    private String peccPlace;
    /**
     * 违章行为
     */
    private String peccAction;
    /**
     * 扣分
     */
    private Byte subScore;
    /**
     * 罚款
     */
    private Double fine;
    /**
     * 驾驶员id
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long driverId;
    /**
     * 驾驶员姓名
     */
    private String driverName;
    /**
     * 是否造成事故：0：未造成，1：已造成
     */
    private boolean isAccident;
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

    public Long getPeccId() {
        return peccId;
    }

    public void setPeccId(Long peccId) {
        this.peccId = peccId;
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

    public Date getPeccTime() {
        return peccTime;
    }

    public void setPeccTime(Date peccTime) {
        this.peccTime = peccTime;
    }

    public String getPeccPlace() {
        return peccPlace;
    }

    public void setPeccPlace(String peccPlace) {
        this.peccPlace = peccPlace;
    }

    public String getPeccAction() {
        return peccAction;
    }

    public void setPeccAction(String peccAction) {
        this.peccAction = peccAction;
    }

    public Byte getSubScore() {
        return subScore;
    }

    public void setSubScore(Byte subScore) {
        this.subScore = subScore;
    }

    public Double getFine() {
        return fine;
    }

    public void setFine(Double fine) {
        this.fine = fine;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public boolean getIsAccident() {
        return isAccident;
    }

    public void setIsAccident(boolean isAccident) {
        this.isAccident = isAccident;
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