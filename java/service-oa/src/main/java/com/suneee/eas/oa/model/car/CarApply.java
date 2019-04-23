package com.suneee.eas.oa.model.car;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;
import com.suneee.eas.common.model.BaseModel;
import com.suneee.eas.common.utils.DateUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 车辆申请model
 * @user 子华
 * @created 2018/8/28
 */
public class CarApply extends BaseModel implements Serializable {

    private static final long serialVersionUID = 7502299554435599841L;
    //流程启动关键key
    public static final String BPMN_KEY_CAR="car_apply";
    //审批节点ID声明
    //申请人
    public static final String NODE_APPLY="applyNode";
    //部门审批
    public static final String NODE_DEPARTMENT="departmentNode";
    //车辆管理员派车
    public static final String NODE_ARRANGE_CAR="arrangeCarNode";
    //驾驶员出车
    public static final String NODE_CAR_OUT="carOutNode";
    //驾驶员还车
    public static final String NODE_CAR_BACK="carBackNode";
    //待审批(暂时屏蔽，待审批与审批中合并为一)
    //public static final int STATUS_AUDIT_PENDDING=0;
    //审批中
    public static final int STATUS_AUDIT_AUDITING=1;
    //审批拒绝
    public static final int STATUS_AUDIT_FAIL=2;
    //已撤回
    public static final int STATUS_AUDIT_FEEBACK=9;
    //待派车
    public static final int STATUS_CAR_ARRANGE_PENDING=3;
    //派车不通过
    public static final int STATUS_CAR_ARRANGE_FAIL=4;
    //待出车
    public static final int STATUS_CAR_DRIVE_PENDDING=5;
    //不出车
    public static final int STATUS_CAR_DRIVE_FAIL=6;
    //待还车
    public static final int STATUS_CAR_BACK_PENDING=7;
    //已还车
    public static final int STATUS_CAR_BACK_DONE=8;
    //已取消
    public static final int STATUS_CANCEL=10;

    //已删除状态
    public static final int DELETE_YES=1;
    //未删除状态
    public static final int DELETE_NO=0;
    //自驾
    public static final int SELF_DRIVE_YES=1;
    //不自驾
    public static final int SELF_DRIVE_NO=0;
    //申请id(主键)
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long applyId;
    //申请人id
    @NotNull(message = "申请人ID不能为空")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long applicantId;
    //申请人名字
    @NotNull(message = "请填写申请人名字")
    private String applicantName;
    //申请人联系电话
    private String mobile;
    //是否自驾,暂时屏蔽
    //@NotNull(message = "请选择是否为自驾")
    private Integer isSelfDrive;
    //乘车人数
    @Max(value = 7,message = "请填写乘车人数有效范围")
    @Min(value = 0,message = "请填写乘车人数有效范围")
    private Integer passengerCount;
    //乘车人id，多个id用逗号隔开
    private String passengerIds;
    //乘车人姓名，多个id用逗号隔开
    private String passengerNames;
    //乘车人通知方式
    private String informType;
    //用车开始时间
    @DateTimeFormat(pattern = DateUtil.FORMAT_DATETIME)
    @NotNull(message = "请填写开始时间")
    private Date startTime;
    //用车结束时间
    @DateTimeFormat(pattern = DateUtil.FORMAT_DATETIME)
    @NotNull(message = "请填写结束时间")
    private Date endTime;
    //出发地
    @NotNull(message = "请填写出发地")
    private String origin;
    //目的地
    @NotNull(message = "请填写目的地")
    private String destination;
    //预计里程数
    @NotNull(message = "请填写预计里程数")
    private String expMileage;
    //用车事由
    private String content;
    //申请状态：0=待审批，1=审批中，2=审批拒绝，3=待派车，4=派车不通过，5=待出车，6=不出车，7=待还车，8=已还车，9=已撤回
    private Integer status;
    //企业编码
    @JsonIgnore
    private String enterpriseCode;
    //是否删除：0=未删除，1=已删除
    private Integer isDelete;
    //流程实例ID
    private String procInstId;
    //任务ID
    private String taskId;

    //当前流程节点
    private String currentNode;

    /**
     * 审批人
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long assigneeId;

    /**
     * 审批人姓名
     */
    private String assigneeName;

    //审核状态：1=同意，2=不同意
    private int auditStatus;

    public Long getApplyId() {
        return applyId;
    }

    public void setApplyId(Long applyId) {
        this.applyId = applyId;
    }

    public Long getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getIsSelfDrive() {
        return isSelfDrive;
    }

    public void setIsSelfDrive(Integer isSelfDrive) {
        this.isSelfDrive = isSelfDrive;
    }

    public Integer getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Integer passengerCount) {
        this.passengerCount = passengerCount;
    }

    public String getPassengerIds() {
        return passengerIds;
    }

    public void setPassengerIds(String passengerIds) {
        this.passengerIds = passengerIds;
    }

    public String getPassengerNames() {
        return passengerNames;
    }

    public void setPassengerNames(String passengerNames) {
        this.passengerNames = passengerNames;
    }

    public String getInformType() {
        return informType;
    }

    public void setInformType(String informType) {
        this.informType = informType;
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

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getExpMileage() {
        return expMileage;
    }

    public void setExpMileage(String expMileage) {
        this.expMileage = expMileage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(String currentNode) {
        this.currentNode = currentNode;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public int getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(int auditStatus) {
        this.auditStatus = auditStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("applyId", applyId)
                .append("applicantId", applicantId)
                .append("applicantName", applicantName)
                .append("mobile", mobile)
                .append("isSelfDrive", isSelfDrive)
                .append("passengerCount", passengerCount)
                .append("passengerIds", passengerIds)
                .append("passengerNames", passengerNames)
                .append("informType", informType)
                .append("startTime", startTime)
                .append("endTime", endTime)
                .append("origin", origin)
                .append("destination", destination)
                .append("expMileage", expMileage)
                .append("content", content)
                .append("status", status)
                .append("enterpriseCode", enterpriseCode)
                .append("isDelete", isDelete)
                .append("createBy", createBy)
                .append("createTime", createTime)
                .append("procInstId", procInstId)
                .append("taskId", taskId)
                .append("currentNode", currentNode)
                .append("assigneeId", assigneeId)
                .append("assigneeName", assigneeName)
                .append("auditStatus", auditStatus)
                .toString();
    }
}
