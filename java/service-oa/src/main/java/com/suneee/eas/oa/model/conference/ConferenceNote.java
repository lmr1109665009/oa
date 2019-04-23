/**
 * @Title: ConferenceNote.java 
 * @Package com.suneee.eas.oa.model.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.model.conference;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;
import com.suneee.eas.common.model.BaseModel;

/**
 * @ClassName: ConferenceNote 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-07-31 19:08:21 
 *
 */
public class ConferenceNote extends BaseModel{
	/**
	 * 会议纪要ID
	 */ 
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long noteId;
	/**
	 * 会议ID 
	 */ 
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long conferenceId;
	/**
	 * 会议纪要内容
	 */ 
	private String content;
	/**
	 * 附件ID，多个ID之间使用英文逗号分隔 
	 */ 
	private String attachmentIds;
	/**
	 * 附件名称，多个名称之间使用英文逗号分隔
	 */ 
	private String attachmentNames;
	/**
	 * 通知方式
	 */ 
	private String informType;
	/**
	 * 状态
	 */ 
	private Byte status;
	/**
	 * 所属企业
	 */ 
	private String enterpriseCode;
	/**
	 * 是否删除：0=未删除，1=已删除
	 */ 
	private Byte isDelete;
	
	// 前端传参使用字段
	private String readerIds;
	private String readerNames;
	private String participantIds;
	private String participantNames;
	// 附件下载地址
	private List<String> attachmentUrl;
	// 纪要审批记录
	private List<ConferenceAudit> noteAuditList;
	/**
	 * @return the noteId
	 */
	public Long getNoteId() {
		return noteId;
	}
	/**
	 * @param noteId the noteId to set
	 */
	public void setNoteId(Long noteId) {
		this.noteId = noteId;
	}
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
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
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
	 * @return the status
	 */
	public Byte getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Byte status) {
		this.status = status;
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
	 * @return the readerIds
	 */
	public String getReaderIds() {
		return readerIds;
	}
	/**
	 * @param readerIds the readerIds to set
	 */
	public void setReaderIds(String readerIds) {
		this.readerIds = readerIds;
	}
	/**
	 * @return the readerNames
	 */
	public String getReaderNames() {
		return readerNames;
	}
	/**
	 * @param readerNames the readerNames to set
	 */
	public void setReaderNames(String readerNames) {
		this.readerNames = readerNames;
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
	 * @return the attachmentUrl
	 */
	public List<String> getAttachmentUrl() {
		return attachmentUrl;
	}
	/**
	 * @param attachmentUrl the attachmentUrl to set
	 */
	public void setAttachmentUrl(List<String> attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}
	/**
	 * @return the noteAuditList
	 */
	public List<ConferenceAudit> getNoteAuditList() {
		return noteAuditList;
	}
	/**
	 * @param noteAuditList the noteAuditList to set
	 */
	public void setNoteAuditList(List<ConferenceAudit> noteAuditList) {
		this.noteAuditList = noteAuditList;
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
		builder.append(" [noteId=").append(noteId);
		builder.append(", conferenceId=").append(conferenceId);
		builder.append(", content=").append(content);
		builder.append(", attachmentIds=").append(attachmentIds);
		builder.append(", attachmentNames=").append(attachmentNames);
		builder.append(", informType=").append(informType);
		builder.append(", status=").append(status);
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
