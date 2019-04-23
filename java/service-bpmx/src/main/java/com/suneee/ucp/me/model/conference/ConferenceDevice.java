package com.suneee.ucp.me.model.conference;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.suneee.ucp.base.model.UcpBaseModel;


/**
 * 对象功能:设备信息表 Model对象
 * 开发公司:深圳象翌
 * 开发人员:xiongxianyun
 * 创建时间:2017-04-27 17:45:03
 */
public class ConferenceDevice extends UcpBaseModel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6808113455964693295L;
	
	/**
	 * 地区数据字典key值
	 */
	public static final String REGION_NODE_KEY = "dq";
	
	/**
	 * 设备类型数据字典key值
	 */
	public static final String DEVICE_TYPE_NODE_KEY = "sblx";
	
	/**
	 * 设备ID
	 */
	private Long deviceId;
	
	/**
	 * 会议室ID
	 */
	private Long roomId;

	/**
	 * 地区
	 */
	private Long region;
	
	/**
	 * 设备类型
	 */
	private Long deviceType;
	
	/**
	 * 设备名称
	 */
	private String deviceName;
	
	/**
	 * 设备编号
	 */
	private String deviceNo;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 是否公用
	 */
	private Integer isPublic;
	
	/**
	 * 会议室名称
	 */
	private String roomName;
	
	/**
	 * 地区名称
	 */
	private String regionName;
	
	/**
	 * 设备类型名称
	 */
	private String deviceTypeName;

	/**
	 * 是否删除
	 */
	private Long isDelete=0L;

	/**
	 * 所属企业
	 */
	private String enterpriseCode;

	/**
	 * @return the deviceId
	 */
	public Long getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
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
	 * @return the deviceName
	 */
	public String getDeviceName() {
		return deviceName;
	}

	/**
	 * @param deviceName the deviceName to set
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	/**
	 * @return the deviceNo
	 */
	public String getDeviceNo() {
		return deviceNo;
	}

	/**
	 * @param deviceNo the deviceNo to set
	 */
	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
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
	 * @return the isPublic
	 */
	public Integer getIsPublic() {
		return isPublic;
	}

	/**
	 * @param isPublic the isPublic to set
	 */
	public void setIsPublic(Integer isPublic) {
		this.isPublic = isPublic;
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

	public Long getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Long isDelete) {
		this.isDelete = isDelete;
	}

	public String getEnterpriseCode() {
		return enterpriseCode;
	}

	public void setEnterpriseCode(String enterpriseCode) {
		this.enterpriseCode = enterpriseCode;
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof ConferenceDevice)) 
		{
			return false;
		}
		ConferenceDevice rhs = (ConferenceDevice) object;
		return new EqualsBuilder()
		.append(this.deviceId, rhs.deviceId)
		.append(this.roomId, rhs.roomId)
		.append(this.region, rhs.region)
		.append(this.deviceType, rhs.deviceType)
		.append(this.deviceName, rhs.deviceName)
		.append(this.deviceNo, rhs.deviceNo)
		.append(this.isPublic, rhs.isPublic)
		.append(this.remark, rhs.remark)
		.append(this.roomName, rhs.roomName)
		.append(this.regionName, rhs.regionName)
		.append(this.deviceTypeName, rhs.deviceTypeName)
		.append(this.createtime, rhs.createtime)
		.append(this.createBy, rhs.createBy)
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
		.append(this.deviceId)  
		.append(this.roomId) 
		.append(this.region) 
		.append(this.deviceType)
		.append(this.deviceName) 
		.append(this.deviceNo)  
		.append(this.isPublic) 
		.append(this.remark) 
		.append(this.roomName)  
		.append(this.regionName) 
		.append(this.deviceTypeName)  
		.append(this.createtime) 
		.append(this.createBy) 
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
		.append("deviceId", this.deviceId) 
		.append("roomId", this.roomId) 
		.append("region", this.region) 
		.append("deviceType", this.deviceType) 
		.append("deviceName", this.deviceName) 
		.append("deviceNo", this.deviceNo) 
		.append("isPublic", this.isPublic) 
		.append("remark", this.remark) 
		.append("roomName", this.roomName) 
		.append("regionName", this.regionName) 
		.append("deviceTypeName", this.deviceTypeName)  
		.append("createtime", this.createtime) 
		.append("createBy", this.createBy) 
		.append("updatetime", this.updatetime) 
		.append("updateBy", this.updateBy) 
		.toString();
	}
}