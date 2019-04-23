/**
 * @Title: ConferenceReserve.java 
 * @Package com.suneee.eas.oa.model.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.model.conference;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;
import com.suneee.eas.common.model.BaseModel;

/**
 * @ClassName: ConferenceReserve 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-07-31 11:21:37 
 *
 */
public class ConferenceReserve extends BaseModel{
	/**
	 * 周期性类型：0=非周期性 
	 */ 
	public static final Byte CYC_NO = 0;
	/**
	 * 周期性类型：1=按周
	 */ 
	public static final Byte CYC_WEEK = 1;
	/**
	 * 周期性类型：2=按月
	 */ 
	public static final Byte CYC_MONTH = 2;
	
	/**
	 * 会议ID 
	 */ 
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long conferenceId;
	
	/**
	 * 会议室ID 
	 */ 
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long roomId;

	/**
	 * 区域
	 */ 
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long region;
	
	/**
	 * 会议主题 
	 */ 
	private String theme;
	
	/**
	 * 会议开始时间 
	 */ 
	private Date beginTime;
	
	/**
	 * 会议结束时间
	 */ 
	private Date endTime;
	
	/**
	 * 备注 
	 */ 
	private String description;
	
	/**
	 * 会议纪要员 
	 */ 
	private Long recorder;
	
	/**
	 * 会议纪要员姓名
	 */ 
	private String recorderName;
	
	/**
	 * 会议组织人 
	 */ 
	private Long organizer;
	
	/**
	 * 会议组织人姓名
	 */ 
	private String organizerName;
	
	/**
	 * 会议通知方式 
	 */ 
	private String informType;
	
	/**
	 * 审批通知方式 
	 */ 
	private String auditInformType;
	
	/**
	 * 附件ID，多个ID使用英文逗号分隔 
	 */ 
	private String attachmentIds;
	
	/**
	 * 会议名称，多个名称之间使用英文逗号分隔 
	 */ 
	private String attachmentNames;
	
	/**
	 * 周期性类型：0=非周期性，1=按周，2=按月
	 */ 
	private Byte cycType;
	
	/**
	 * 周期性编号
	 */ 
	private Long cycNo;
	
	/**
	 * 审批状态
	 */ 
	private Byte auditStatus;
	
	/**
	 * 会议纪要状态 
	 */ 
	private Byte noteStatus;
	
	/**
	 * 所属企业
	 */ 
	private String enterpriseCode;
	
	/**
	 * 是否删除：0=未删除，1=已删除
	 */ 
	private Byte isDelete;
	
	// 前端使用参数
	// 参会人员ID
	private String participantIds;
	// 参会人员姓名
	private String participantNames;
	// 会议设备类型ID
	private String deviceTypeIds;
	// 会议设备类型名称
	private String deviceTypeNames;
	// 附件可下载人员Id
	private String downloadUserIds;
	// 附件可下载人员姓名
	private String downloadUserNames;
	// 会议室名称
	private String roomName;
	// 区域名称
	private String regionName;
	// 会议纪要信息
	private ConferenceNote conferenceNote;
	// 审批Id
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long auditId; 
	// 附件下载地址
	private List<String> attachmentUrl;
	//是否已回执 0:表示未回执
	private int isReceipt;
	/**
	 * @return the conferenceId
	 */
	public Long getConferenceId() {
		return conferenceId;
	}

	/**
	 * @param conferenceId the conferenceId to set
	 */
	public void setConferenceId(Long conferenceId) {
		this.conferenceId = conferenceId;
	}

	/**
	 * @return the roomId
	 */
	public Long getRoomId() {
		return roomId;
	}

	/**
	 * @param roomId the roomId to set
	 */
	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	/**
	 * @return the region
	 */
	public Long getRegion() {
		return region;
	}

	/**
	 * @param region the region to set
	 */
	public void setRegion(Long region) {
		this.region = region;
	}

	/**
	 * @return the theme
	 */
	public String getTheme() {
		return theme;
	}

	/**
	 * @param theme the theme to set
	 */
	public void setTheme(String theme) {
		this.theme = theme;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the recorder
	 */
	public Long getRecorder() {
		return recorder;
	}

	/**
	 * @param recorder the recorder to set
	 */
	public void setRecorder(Long recorder) {
		this.recorder = recorder;
	}

	/**
	 * @return the recorderName
	 */
	public String getRecorderName() {
		return recorderName;
	}

	/**
	 * @param recorderName the recorderName to set
	 */
	public void setRecorderName(String recorderName) {
		this.recorderName = recorderName;
	}

	/**
	 * @return the organizer
	 */
	public Long getOrganizer() {
		return organizer;
	}

	/**
	 * @param organizer the organizer to set
	 */
	public void setOrganizer(Long organizer) {
		this.organizer = organizer;
	}

	/**
	 * @return the organizerName
	 */
	public String getOrganizerName() {
		return organizerName;
	}

	/**
	 * @param organizerName the organizerName to set
	 */
	public void setOrganizerName(String organizerName) {
		this.organizerName = organizerName;
	}

	/**
	 * @return the informType
	 */
	public String getInformType() {
		return informType;
	}

	/**
	 * @param informType the informType to set
	 */
	public void setInformType(String informType) {
		this.informType = informType;
	}

	/**
	 * @return the auditInformType
	 */
	public String getAuditInformType() {
		return auditInformType;
	}

	/**
	 * @param auditInformType the auditInformType to set
	 */
	public void setAuditInformType(String auditInformType) {
		this.auditInformType = auditInformType;
	}

	/**
	 * @return the attachmentIds
	 */
	public String getAttachmentIds() {
		return attachmentIds;
	}

	/**
	 * @param attachmentIds the attachmentIds to set
	 */
	public void setAttachmentIds(String attachmentIds) {
		this.attachmentIds = attachmentIds;
	}

	/**
	 * @return the attachmentNames
	 */
	public String getAttachmentNames() {
		return attachmentNames;
	}

	/**
	 * @param attachmentNames the attachmentNames to set
	 */
	public void setAttachmentNames(String attachmentNames) {
		this.attachmentNames = attachmentNames;
	}

	/**
	 * @return the cycType
	 */
	public Byte getCycType() {
		return cycType;
	}

	/**
	 * @param cycType the cycType to set
	 */
	public void setCycType(Byte cycType) {
		this.cycType = cycType;
	}

	/**
	 * @return the cycNo
	 */
	public Long getCycNo() {
		return cycNo;
	}

	/**
	 * @param cycNo the cycNo to set
	 */
	public void setCycNo(Long cycNo) {
		this.cycNo = cycNo;
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
	 * @return the noteStatus
	 */
	public Byte getNoteStatus() {
		return noteStatus;
	}

	/**
	 * @param noteStatus the noteStatus to set
	 */
	public void setNoteStatus(Byte noteStatus) {
		this.noteStatus = noteStatus;
	}

	/**
	 * @return the enterpriseCode
	 */
	public String getEnterpriseCode() {
		return enterpriseCode;
	}

	/**
	 * @param enterpriseCode the enterpriseCode to set
	 */
	public void setEnterpriseCode(String enterpriseCode) {
		this.enterpriseCode = enterpriseCode;
	}

	/**
	 * @return the isDelete
	 */
	public Byte getIsDelete() {
		return isDelete;
	}

	/**
	 * @param isDelete the isDelete to set
	 */
	public void setIsDelete(Byte isDelete) {
		this.isDelete = isDelete;
	}

	/**
	 * @return the participantIds
	 */
	public String getParticipantIds() {
		return participantIds;
	}

	/**
	 * @param participantIds the participantIds to set
	 */
	public void setParticipantIds(String participantIds) {
		this.participantIds = participantIds;
	}

	/**
	 * @return the participantNames
	 */
	public String getParticipantNames() {
		return participantNames;
	}

	/**
	 * @param participantNames the participantNames to set
	 */
	public void setParticipantNames(String participantNames) {
		this.participantNames = participantNames;
	}

	/**
	 * @return the deviceTypeIds
	 */
	public String getDeviceTypeIds() {
		return deviceTypeIds;
	}

	/**
	 * @param deviceTypeIds the deviceTypeIds to set
	 */
	public void setDeviceTypeIds(String deviceTypeIds) {
		this.deviceTypeIds = deviceTypeIds;
	}

	/**
	 * @return the deviceTypeNames
	 */
	public String getDeviceTypeNames() {
		return deviceTypeNames;
	}

	/**
	 * @param deviceTypeNames the deviceTypeNames to set
	 */
	public void setDeviceTypeNames(String deviceTypeNames) {
		this.deviceTypeNames = deviceTypeNames;
	}

	/**
	 * @return the downloadUserIds
	 */
	public String getDownloadUserIds() {
		return downloadUserIds;
	}

	/**
	 * @param downloadUserIds the downloadUserIds to set
	 */
	public void setDownloadUserIds(String downloadUserIds) {
		this.downloadUserIds = downloadUserIds;
	}

	/**
	 * @return the downloadUserNames
	 */
	public String getDownloadUserNames() {
		return downloadUserNames;
	}

	/**
	 * @param downloadUserNames the downloadUserNames to set
	 */
	public void setDownloadUserNames(String downloadUserNames) {
		this.downloadUserNames = downloadUserNames;
	}

	/**
	 * @return the roomName
	 */
	public String getRoomName() {
		return roomName;
	}

	/**
	 * @param roomName the roomName to set
	 */
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	/**
	 * @return the regionName
	 */
	public String getRegionName() {
		return regionName;
	}

	/**
	 * @param regionName the regionName to set
	 */
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	/**
	 * @return the conferenceNote
	 */
	public ConferenceNote getConferenceNote() {
		return conferenceNote;
	}

	/**
	 * @param conferenceNote the conferenceNote to set
	 */
	public void setConferenceNote(ConferenceNote conferenceNote) {
		this.conferenceNote = conferenceNote;
	}

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
	 * @return the attachmentUrl
	 */
	public List<String> getAttachmentUrl() {
		return attachmentUrl;
	}

	public int getIsReceipt() {
		return isReceipt;
	}

	public void setIsReceipt(int isReceipt) {
		this.isReceipt = isReceipt;
	}

	/**
	 * @param attachmentUrl the attachmentUrl to set
	 */
	public void setAttachmentUrl(List<String> attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getClass().getSimpleName());
		builder.append(" [conferenceId=").append(conferenceId);
		builder.append(", roomId=").append(roomId);
		builder.append(", roomName=").append(roomName);
		builder.append(", region=").append(region);
		builder.append(", theme=").append(theme);
		builder.append(", beginTime=").append(beginTime);
		builder.append(", endTime=").append(endTime);
		builder.append(", description=").append(description);
		builder.append(", recorder=").append(recorder);
		builder.append(", recorderName=").append(recorderName);
		builder.append(", organizer=").append(organizer);
		builder.append(", organizerName=").append(organizerName);
		builder.append(", informType=").append(informType);
		builder.append(", auditInformType=").append(auditInformType);
		builder.append(", attachmentIds=").append(attachmentIds);
		builder.append(", attachmentNames=").append(attachmentNames);
		builder.append(", cycType=").append(cycType);
		builder.append(", cycNo=").append(cycNo);
		builder.append(", auditStatus=").append(auditStatus);
		builder.append(", noteStatus=").append(noteStatus);
		builder.append(", enterpriseCode=").append(enterpriseCode);
		builder.append(", isDelete=").append(isDelete);
		builder.append(", createBy=").append(createBy);
		builder.append(", createByName=").append(createByName);
		builder.append(", createTime=").append(createTime);
		builder.append(", updateBy=").append(updateBy);
		builder.append(", updateByName=").append(updateByName);
		builder.append(", updateTime=").append(updateTime);
		builder.append("]");
		return builder.toString();
	}
}
