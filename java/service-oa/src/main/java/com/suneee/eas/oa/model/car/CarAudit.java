package com.suneee.eas.oa.model.car;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;

import java.io.Serializable;
import java.util.Date;

/**
 * 车辆审批model
 * @user 子华
 * @created 2018/8/28
 */
public class CarAudit implements Serializable {
    private static final long serialVersionUID = 4210603549305554995L;
    //审批状态：同意
    public static final int STATUS_YES=1;
    //审批状态：不同意
    public static final int STATUS_NO=2;
    //已读
    public static final int READ_YES=1;
    //未读
    public static final int READ_NO=0;
    //审核ID
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long auditId;
    //审核对象ID(任务ID)
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long targetId;
    //审核人ID
    private Long auditorId;
    //审核人姓名
    private String auditorName;
    //审核状态：1=同意，2=不同意
    private int auditStatus;
    //审批节点
    private String nodeName;
    //是否已读：0=未读，1=已读
    private int isRead;
    //审批意见
    private String opinion;
    //审批开始时间
    private Date startTime;
    //审批结束时间
    private Date endTime;
    //审批持续时长（秒）
    private Long duration;
    //流程实例ID
    private String procInstId;

    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Long getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(Long auditorId) {
        this.auditorId = auditorId;
    }

    public String getAuditorName() {
        return auditorName;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    public int getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(int auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }
}
