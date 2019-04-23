/**
 * @Title: ConferenceReceipt.java 
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
 * @ClassName: ConferenceReceipt 
 * @Description: 会议回执实体类
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-09 10:58:38 
 */
public class ConferenceReceipt {
	public static final Byte STATUS_PARTICIPANT = 1;
	public static final Byte STATUS_NOT_PARTICIPANT = 2;
	public static final Byte STATUS_UNDETERMINED = 3;
	
	/**
	 * 会议回执ID 
	 */ 
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long receiptId;
	
	/**
	 * 会议ID
	 */ 
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long conferenceId;
	
	/**
	 * 回执人ID
	 */ 
	private Long userId;
	
	/**
	 * 回执人姓名 
	 */ 
	private String userName;
	
	/**
	 * 回执状态：1=参加，2=不参加，3=待定
	 */ 
	private Byte status;
	
	/**
	 * 说明 
	 */ 
	private String remark;
	
	/**
	 * 回执时间
	 */ 
	private Date receiptTime;

	/**
	 * @return the receiptId
	 */
	public Long getReceiptId() {
		return receiptId;
	}

	/**
	 * @param receiptId the receiptId to set
	 */
	public void setReceiptId(Long receiptId) {
		this.receiptId = receiptId;
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
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
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
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * @return the receiptTime
	 */
	public Date getReceiptTime() {
		return receiptTime;
	}

	/**
	 * @param receiptTime the receiptTime to set
	 */
	public void setReceiptTime(Date receiptTime) {
		this.receiptTime = receiptTime;
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
		builder.append(" [receiptId=").append(receiptId);
		builder.append(", conferenceId=").append(conferenceId);
		builder.append(", userId=").append(userId);
		builder.append(", userName=").append(userName);
		builder.append(", status=").append(status);
		builder.append(", remark=").append(remark);
		builder.append(", receiptTime=").append(receiptTime);
		builder.append("]");
		return builder.toString();
	}
	
}
