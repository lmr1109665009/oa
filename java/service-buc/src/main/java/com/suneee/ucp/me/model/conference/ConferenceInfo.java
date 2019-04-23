package com.suneee.ucp.me.model.conference;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.suneee.ucp.base.model.UcpBaseModel;

/**
 * 对象功能:会议信息 Model对象
 * 开发公司:深圳象翌
 * 开发人员:xiongxianyun
 * 创建时间:2017-05-05 13:21:21
 */
public class ConferenceInfo extends UcpBaseModel{
	public static final Integer STATUS_OK=1;
	public static final Integer STATUS_CANCEL=0;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4618901310354390875L;
	
	/**
	 * 会议ID
	 */
	private Long conferenceId;
	
	/**
	 * 会议室ID
	 */
	private Long roomId;

	/**
	 * 会议预定状态，默认为1预定成功
	 */
	private Integer status;
	
	/**
	 * 会议召开时间
	 */
	private Date convokeDate;
	
	/**
	 * 开始时间
	 */
	private String beginTime;
	
	/**
	 * 结束时间
	 */
	private String endTime;
	
	/**
	 * 会议主题
	 */
	private String theme;
	
	/**
	 * 会议描述
	 */
	private String description;
	
	/**
	 * 会议设备，多个设备之间使用“,”分隔
	 */
	private String devices;
	
	/**
	 * 出席人员，多个人员之间使用“,”分隔
	 */
	private String participants;
	
	/**
	 * 获取会议室名称
	 */
	private String roomName;
	
	/**
	 * 会议号开日期：用于显示
	 */
	private String convokeDateText;
	
	/**
	 * 会议设备，用于显示
	 */
	private String devicesText;
	
	/**
	 * 出席人员，用于显示
	 */
	private String participantsText;

	/**
	 * 申请人姓名
	 */
	private String createByName;

	/**
	 * 会议地点

	 */
	private String conferencePlace;

	public String getConferencePlace() {
		return conferencePlace;
	}

	public void setConferencePlace(String conferencePlace) {
		this.conferencePlace = conferencePlace;
	}

	/**
	 * 设置会议ID
	 * @param conferenceId
	 */
	public void setConferenceId(Long conferenceId){
		this.conferenceId = conferenceId;
	}
	
	/**
	 * 返回 会议ID
	 * @return
	 */
	public Long getConferenceId() {
		return this.conferenceId;
	}
	
	/**
	 * 设置会议室ID
	 * @param roomId
	 */
	public void setRoomId(Long roomId){
		this.roomId = roomId;
	}
	
	/**
	 * 返回 会议室ID
	 * @return
	 */
	public Long getRoomId() {
		return this.roomId;
	}
	
	/**
	 * 设置会议召开时间
	 * @param convokeDate
	 */
	public void setConvokeDate(Date convokeDate){
		this.convokeDate = convokeDate;
	}
	
	/**
	 * 返回 会议召开时间
	 * @return
	 */
	public Date getConvokeDate() {
		return this.convokeDate;
	}
	
	/**
	 * 设置 开始时间
	 * @param beginTime
	 */
	public void setBeginTime(String beginTime){
		this.beginTime = beginTime;
	}
	
	/**
	 * 返回 开始时间
	 * @return
	 */
	public String getBeginTime() {
		return this.beginTime;
	}
	
	/**
	 * 设置结束时间
	 * @param endTime
	 */
	public void setEndTime(String endTime){
		this.endTime = endTime;
	}
	
	/**
	 * 返回 结束时间
	 * @return
	 */
	public String getEndTime() {
		return this.endTime;
	}
	
	/**
	 * 设置会议主题
	 * @param theme
	 */
	public void setTheme(String theme){
		this.theme = theme;
	}
	
	/**
	 * 返回 会议主题
	 * @return
	 */
	public String getTheme() {
		return this.theme;
	}
	
	/**
	 * 设置会议描述
	 * @param description
	 */
	public void setDescription(String description){
		this.description = description;
	}
	
	/**
	 * 返回 会议描述
	 * @return
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * 设置会议设备
	 * @param devices
	 */
	public void setDevices(String devices){
		this.devices = devices;
	}
	
	/**
	 * 返回 会议设备
	 * @return
	 */
	public String getDevices() {
		return this.devices;
	}
	
	/**
	 * 设置出席人员
	 * @param participants
	 */
	public void setParticipants(String participants){
		this.participants = participants;
	}
	
	/**
	 * 返回 出席人员
	 * @return
	 */
	public String getParticipants() {
		return this.participants;
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
	 * @return the convokeDateText
	 */
	public String getConvokeDateText() {
		return convokeDateText;
	}

	/**
	 * @param convokeDateText the convokeDateText to set
	 */
	public void setConvokeDateText(String convokeDateText) {
		this.convokeDateText = convokeDateText;
	}

	/**
	 * @return the devicesText
	 */
	public String getDevicesText() {
		return devicesText;
	}

	/**
	 * @param devicesText the devicesText to set
	 */
	public void setDevicesText(String devicesText) {
		this.devicesText = devicesText;
	}

	/**
	 * @return the participantsText
	 */
	public String getParticipantsText() {
		return participantsText;
	}

	/**
	 * @param participantsText the participantsText to set
	 */
	public void setParticipantsText(String participantsText) {
		this.participantsText = participantsText;
	}

	/**
	 * @return the createByName
	 */
	public String getCreateByName() {
		return createByName;
	}

	/**
	 * @param createByName the createByName to set
	 */
	public void setCreateByName(String createByName) {
		this.createByName = createByName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof ConferenceInfo)) 
		{
			return false;
		}
		ConferenceInfo rhs = (ConferenceInfo) object;
		return new EqualsBuilder()
		.append(this.conferenceId, rhs.conferenceId)
		.append(this.roomId, rhs.roomId)
		.append(this.convokeDate, rhs.convokeDate)
		.append(this.beginTime, rhs.beginTime)
		.append(this.endTime, rhs.endTime)
		.append(this.theme, rhs.theme)
		.append(this.description, rhs.description)
		.append(this.devices, rhs.devices)
		.append(this.participants, rhs.participants)
		.append(this.roomName, rhs.roomName)
		.append(this.convokeDateText, rhs.convokeDateText)
		.append(this.devicesText, rhs.devicesText)
		.append(this.participantsText, rhs.participantsText)
		.append(this.createtime, rhs.createtime)
		.append(this.createBy, rhs.createBy)
		.append(this.createByName, rhs.createByName)
		.append(this.updatetime, rhs.updatetime)
		.append(this.updateBy, rhs.updateBy)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.conferenceId) 
		.append(this.roomId) 
		.append(this.status)
		.append(this.convokeDate)
		.append(this.beginTime) 
		.append(this.endTime) 
		.append(this.theme) 
		.append(this.description) 
		.append(this.devices) 
		.append(this.participants) 
		.append(this.roomName)
		.append(this.convokeDateText)
		.append(this.devicesText) 
		.append(this.participantsText) 
		.append(this.createtime) 
		.append(this.createBy) 
		.append(this.createByName) 
		.append(this.updatetime) 
		.append(this.updateBy) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString()
	{
		return new ToStringBuilder(this)
		.append("conferenceId", this.conferenceId)
		.append("roomId", this.roomId)
		.append("status", this.status)
		.append("convokeDate", this.convokeDate)
		.append("beginTime", this.beginTime)
		.append("endTime", this.endTime)
		.append("theme", this.theme)
		.append("description", this.description)
		.append("devices", this.devices)
		.append("participants", this.participants)
		.append("roomName", this.roomName)
		.append("convokeDateText", this.convokeDateText)
		.append("devicesText", this.devicesText)
		.append("participantsText", this.participantsText)
		.append("createtime", this.createtime)
		.append("createBy", this.createBy)
		.append("createByName", this.createByName)
		.append("updatetime", this.updatetime)
		.append("updateBy", this.updateBy)
		.toString();
	}


}