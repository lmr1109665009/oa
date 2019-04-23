/**
 * @Title: ConferenceAudit.java 
 * @Package com.suneee.eas.oa.model.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.model.conference;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;

/**
 * @ClassName: ConferenceAudit 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 10:21:49 
 *
 */
public class ConferenceAudit {
	public static final Byte READ_YES = 1;
	public static final Byte READ_NO = 0;
	
	public static final String TARGET_TYPE_CONFERENCE = "conference";
	public static final String TARGET_TYPE_CONFERENCE_NOTE = "conference-note";
	/**
	 * 审核ID 
	 */ 
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long auditId;
	/**
	 * 审核对象ID 
	 */ 
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long targetId;
	/**
	 * 审核对象类型
	 */ 
	private String targetType;
	/**
	 * 审核人
	 */ 
	private Long auditor;
	/**
	 * 审核人姓名
	 */ 
	private String auditorName;
	/**
	 * 审核状态
	 */ 
	private Byte auditStatus;
	/**
	 * 是否已读：0=未读，1=已读 
	 */ 
	private Byte isRead;
	/**
	 * 审核意见
	 */ 
	private String opinion;
	/**
	 * 审核开始时间 
	 */ 
	private Date beginTime;
	/**
	 * 审核结束时间 
	 */ 
	private Date endTime;
	/**
	 * 审核持续时长（秒） 
	 */ 
	private Long duration;
	
	/**
	 * 审批步骤
	 */ 
	private Byte step;
	
	/**
	 * 审批路径
	 */ 
	private String stepPath;
	
	/**
	 * @return the auditId
	 */
	public Long getAuditId() {
		return auditId;
	}
	/**
	 * @param auditId the auditId to set
	 */
	public void setAuditId(Long auditId) {
		this.auditId = auditId;
	}
	/**
	 * @return the targetId
	 */
	public Long getTargetId() {
		return targetId;
	}
	/**
	 * @param targetId the targetId to set
	 */
	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}
	/**
	 * @return the targetType
	 */
	public String getTargetType() {
		return targetType;
	}
	/**
	 * @param targetType the targetType to set
	 */
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	/**
	 * @return the auditor
	 */
	public Long getAuditor() {
		return auditor;
	}
	/**
	 * @param auditor the auditor to set
	 */
	public void setAuditor(Long auditor) {
		this.auditor = auditor;
	}
	/**
	 * @return the auditorName
	 */
	public String getAuditorName() {
		return auditorName;
	}
	/**
	 * @param auditorName the auditorName to set
	 */
	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}
	/**
	 * @return the auditStatus
	 */
	public Byte getAuditStatus() {
		return auditStatus;
	}
	/**
	 * @param auditStatus the auditStatus to set
	 */
	public void setAuditStatus(Byte auditStatus) {
		this.auditStatus = auditStatus;
	}
	/**
	 * @return the isRead
	 */
	public Byte getIsRead() {
		return isRead;
	}
	/**
	 * @param isRead the isRead to set
	 */
	public void setIsRead(Byte isRead) {
		this.isRead = isRead;
	}
	/**
	 * @return the opinion
	 */
	public String getOpinion() {
		return opinion;
	}
	/**
	 * @param opinion the opinion to set
	 */
	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}
	/**
	 * @return the beginTime
	 */
	public Date getBeginTime() {
		return beginTime;
	}
	/**
	 * @param beginTime the beginTime to set
	 */
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}
	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	/**
	 * @return the duration
	 */
	public Long getDuration() {
		return duration;
	}
	/**
	 * @param duration the duration to set
	 */
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	/**
	 * @return the step
	 */
	public Byte getStep() {
		return step;
	}
	/**
	 * @param step the step to set
	 */
	public void setStep(Byte step) {
		this.step = step;
	}
	/**
	 * @return the stepPath
	 */
	public String getStepPath() {
		return stepPath;
	}
	/**
	 * @param stepPath the stepPath to set
	 */
	public void setStepPath(String stepPath) {
		this.stepPath = stepPath;
	}
	/** (non-Javadoc)
	 * @Title: toString 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @return 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [auditId=").append(auditId);
		builder.append(", targetId=").append(targetId);
		builder.append(", targetType=").append(targetType);
		builder.append(", auditor=").append(auditor);
		builder.append(", auditorName=").append(auditorName);
		builder.append(", auditStatus=").append(auditStatus);
		builder.append(", isRead=").append(isRead);
		builder.append(", opinion=").append(opinion);
		builder.append(", beginTime=").append(beginTime);
		builder.append(", endTime=").append(endTime);
		builder.append(", duration=").append(duration);
		builder.append(", step=").append(step);
		builder.append(", stepPath=").append(stepPath);
		builder.append("]");
		return builder.toString();
	}
}
