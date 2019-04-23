/**
 * @Title: ConferenceReserveParticipant.java 
 * @Package com.suneee.eas.oa.model.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.model.conference;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;

/**
 * @ClassName: ConferenceReserveParticipant 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 10:30:46 
 *
 */
public class ConferenceReserveParticipant {
	public static final Byte TYPE_PENDING = 1;
	public static final Byte TYPE_ATTENDED = 2;
	/**
	 * 会议ID
	 */ 
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long conferenceId;
	/**
	 * 类型：1=待参加，2=已参加 
	 */ 
	private Byte type;
	/**
	 * 参会人ID 
	 */ 
	private Long userId;
	/**
	 * 参会人姓名
	 */ 
	private String userName;
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
	 * @return the type
	 */
	public Byte getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(Byte type) {
		this.type = type;
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
		builder.append(" [conferenceId=");
		builder.append(conferenceId);
		builder.append(", type=");
		builder.append(type);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", userName=");
		builder.append(userName);
		builder.append("]");
		return builder.toString();
	}
}
