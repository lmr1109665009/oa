package com.suneee.ucp.me.model.conference;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.suneee.ucp.base.model.UcpBaseModel;

/**
 * 对象功能:会议室信息 Model对象
 * 开发公司:深圳象翌
 * 开发人员:xiongxianyun
 * 创建时间:2017-04-27 15:24:26
 */
public class ConferenceRoom extends UcpBaseModel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5407321989575211432L;
	
	/**
	 * 地区数据字典key值
	 */
	public static final String REGION_NODE_KEY = "dq";
	
	/**
	 * 会议室ID
	 */
	private Long roomId;
	
	/**
	 * 会议室名称
	 */
	private String roomName;
	
	/**
	 * 地区
	 */
	private Long region;
	
	/**
	 * 所在地点
	 */
	private String location;
	
	/**
	 * 设备情况
	 */
	private String deviceCondition;
	
	/**
	 * 会议室描述
	 */
	private String description;
	
	/**
	 * 地区名称
	 */
	private String regionName;

	/**
	 * 是否删除
	 */
	private Long isDelete=0L;

	/**
	 * 所属企业
	 */
	private String enterpriseCode;

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
	
	public void setRoomName(String roomName){
		this.roomName = roomName;
	}
	
	/**
	 * 返回 会议室名称
	 * @return
	 */
	public String getRoomName() {
		return this.roomName;
	}

	/**
	 * @param region the region to set
	 */
	public void setRegion(Long region) {
		this.region = region;
	}
	
	/**
	 * 返回地区
	 * @return the region
	 */
	public Long getRegion() {
		return region;
	}

	public void setLocation(String location){
		this.location = location;
	}
	
	/**
	 * 返回 所在地点
	 * @return
	 */
	public String getLocation() {
		return this.location;
	}
	
	public void setDeviceCondition(String deviceCondition){
		this.deviceCondition = deviceCondition;
	}
	
	/**
	 * 返回 会议室管理员
	 * @return
	 */
	public String getDeviceCondition() {
		return this.deviceCondition;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	/**
	 * 返回 会议室描述
	 * @return
	 */
	public String getDescription() {
		return this.description;
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
		if (!(object instanceof ConferenceRoom)) 
		{
			return false;
		}
		ConferenceRoom rhs = (ConferenceRoom) object;
		return new EqualsBuilder()
		.append(this.roomId, rhs.roomId)
		.append(this.roomName, rhs.roomName)
		.append(this.region, rhs.region)
		.append(this.location, rhs.location)
		.append(this.deviceCondition, rhs.deviceCondition)
		.append(this.description, rhs.description)
		.append(this.regionName, rhs.regionName)
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
		.append(this.roomId) 
		.append(this.roomName) 
		.append(this.region) 
		.append(this.location) 
		.append(this.deviceCondition) 
		.append(this.description)
		.append(this.regionName) 
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
		.append("roomId", this.roomId) 
		.append("roomName", this.roomName) 
		.append("region", this.region) 
		.append("location", this.location) 
		.append("deviceCondition", this.deviceCondition) 
		.append("description", this.description) 
		.append("regionname", this.regionName)  
		.append("createtime", this.createtime) 
		.append("createBy", this.createBy) 
		.append("updatetime", this.updatetime) 
		.append("updateBy", this.updateBy) 
		.toString();
	}
}