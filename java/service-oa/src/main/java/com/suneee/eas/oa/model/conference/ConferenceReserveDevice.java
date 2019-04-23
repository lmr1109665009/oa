/**
 * @Title: ConferenceReserveDevice.java 
 * @Package com.suneee.eas.oa.model.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.model.conference;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;

/**
 * @ClassName: ConferenceReserveDevice 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 10:27:57 
 *
 */
public class ConferenceReserveDevice {
	/**
	 * 会议ID
	 */ 
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long conferenceId;
	/**
	 * 会议设备类型
	 */ 
	private Long deviceType;
	/**
	 * 会议设备类型名称
	 */ 
	private String deviceTypeName;
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
	 * @return the deviceType
	 */
	public Long getDeviceType() {
		return deviceType;
	}
	/**
	 * @param deviceType the deviceType to set
	 */
	public void setDeviceType(Long deviceType) {
		this.deviceType = deviceType;
	}
	/**
	 * @return the deviceTypeName
	 */
	public String getDeviceTypeName() {
		return deviceTypeName;
	}
	/**
	 * @param deviceTypeName the deviceTypeName to set
	 */
	public void setDeviceTypeName(String deviceTypeName) {
		this.deviceTypeName = deviceTypeName;
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
		builder.append(", deviceType=");
		builder.append(deviceType);
		builder.append(", deviceTypeName=");
		builder.append(deviceTypeName);
		builder.append("]");
		return builder.toString();
	}
}
