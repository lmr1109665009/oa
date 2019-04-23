package com.suneee.eas.oa.model.car;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;
import com.suneee.eas.common.utils.DateUtil;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @user zousiyu
 * @date 2018/9/21 9:45
 */
public class MainRemindSetting {
    /**
     * 当前里程数
     */
    private String currentMile;

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
     * 负责人id
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long respId;

    public String getCurrentMile() {
        return currentMile;
    }

    public void setCurrentMile(String currentMile) {
        this.currentMile = currentMile;
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

    public Long getRespId() {
        return respId;
    }

    public void setRespId(Long respId) {
        this.respId = respId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("currentMile", currentMile)
                .append("nextMaintMileage", nextMaintMileage)
                .append("nextMaintDate", nextMaintDate)
                .append("respId", respId)
                .toString();
    }
}
