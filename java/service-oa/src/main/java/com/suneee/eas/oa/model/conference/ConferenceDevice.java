package com.suneee.eas.oa.model.conference;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;
import com.suneee.eas.common.model.BaseModel;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 会议设备信息对象
 * @Author: kaize
 * @Date: 2018/7/31 11:46
 */
public class ConferenceDevice extends BaseModel {

	/**
	 * 设备ID
	 */
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long deviceId;
	/**
	 * 设备名称
	 */
	private String deviceName;
	/**
	 * 设备编号
 	 */
	private String deviceCode;
	/**
	 * 设备类型
	 */
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long deviceType;
	/**
	 * 设备类型名称
	 */
	private String deviceTypeName;
	/**
	 * 地区
	 */
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long region;
	/**
	 * 地区名称
	 */
	private String regionName;
	/**
	 * 会议室ID
	 */
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long roomId;
	/**
	 * 会议室名称
	 */
	private String roomName;
	/**
	 * 是否公用，0：非公用，1：公用
	 */
	private Integer isPublic;
	/**
	 * 是否可用：0=不可用，1=可用
	 */
	private Integer isAvailable;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 所属企业
	 */
	private String enterpriseCode;
	/**
	 * 是否删除，0：未删除，1：已删除
	 */
	private Integer isDelete = 0;

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public Long getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Long deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceTypeName() {
		return deviceTypeName;
	}

	public void setDeviceTypeName(String deviceTypeName) {
		this.deviceTypeName = deviceTypeName;
	}

	public Long getRegion() {
		return region;
	}

	public void setRegion(Long region) {
		this.region = region;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public Integer getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Integer isPublic) {
		this.isPublic = isPublic;
	}

	public Integer getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Integer isAvailable) {
		this.isAvailable = isAvailable;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("deviceId",this.deviceId)
				.append("deviceName",this.deviceName)
				.append("deviceCode",this.deviceCode)
				.append("deviceType",this.deviceType)
				.append("deviceTypeName",this.deviceTypeName)
				.append("region",this.region)
				.append("regionName",this.regionName)
				.append("roomId",this.roomId)
				.append("roomName",this.roomName)
				.append("isPublic",this.isPublic)
				.append("isAvailable",this.isAvailable)
				.append("remark",this.remark)
				.append("enterpriseCode",this.enterpriseCode)
				.append("createBy",this.createBy)
				.append("createTime",this.createTime)
				.append("updateBy",this.updateBy)
				.append("updateTime",this.updateTime)
				.append("isDelete",this.isDelete)
				.toString();
	}
}
