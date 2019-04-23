package com.suneee.eas.oa.model.conference;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;
import com.suneee.eas.common.model.BaseModel;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 会议室信息对象
 * @Author: kaize
 * @Date: 2018/7/31 13:45
 */
public class ConferenceRoom extends BaseModel {
	public static final Integer AUDIT_NO = 0;
	public static final Integer AUDIT_YES = 1;
	public static final Integer AVAILABLE_NO = 0;
	public static final Integer AVAILABLE_YES = 1;
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
	 * 办公区域
	 */
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long region;
	/**
	 * 区域名称
	 */
	private String regionName;
	/**
	 * 位置
	 */
	private String location;
	/**
	 * 可容纳人数
	 */
	private Integer capacity;
	/**
	 * 会议室管理员
	 * 多个管理员之间使用英文逗号分隔
	 */
	private String manager;
	/**
	 * 会议室管理员名称
	 * 多个管理员之间使用英文逗号分隔
	 */
	private String managerName;
	/**
	 * 是否需要审核：0=不需要审核，1=需要审核
	 */
	private Integer needAudit;
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
	 *是否删除：0=未删除，1=已删除
	 */
	private Integer isDelete = 0;
	/**
	 * 申请权限（部门）ID,多个用逗号分隔
	 */
	private String orgIds;
	/**
	 * 申请权限（部门）名称,多个用逗号分隔
	 */
	private String orgNames;
	/**
	 * 申请权限（个人）ID,多个用逗号分隔
	 */
	private String userIds;
	/**
	 * 申请权限（个人）名称,多个用逗号分隔
	 */
	private String userNames;
	
	/**
	 * 设备情况
	 */ 
	private String deviceNames;

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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public Integer getNeedAudit() {
		return needAudit;
	}

	public void setNeedAudit(Integer needAudit) {
		this.needAudit = needAudit;
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

	public String getOrgIds() {
		return orgIds;
	}

	public void setOrgIds(String orgIds) {
		this.orgIds = orgIds;
	}

	public String getOrgNames() {
		return orgNames;
	}

	public void setOrgNames(String orgNames) {
		this.orgNames = orgNames;
	}

	public String getUserIds() {
		return userIds;
	}

	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}

	public String getUserNames() {
		return userNames;
	}

	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}

	/**
	 * @return the deviceNames
	 */
	public String getDeviceNames() {
		return deviceNames;
	}

	/**
	 * @param deviceNames the deviceNames to set
	 */
	public void setDeviceNames(String deviceNames) {
		this.deviceNames = deviceNames;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("roomId",this.roomId)
				.append("roomName",this.roomName)
				.append("region",this.region)
				.append("regionName",this.regionName)
				.append("location",this.location)
				.append("capacity",this.capacity)
				.append("manager",this.manager)
				.append("managerName",this.managerName)
				.append("needAudit",this.needAudit)
				.append("isAvailable",this.isAvailable)
				.append("remark",this.remark)
				.append("enterpriseCode",this.enterpriseCode)
				.append("createBy",this.createBy)
				.append("createTime",this.createTime)
				.append("updateBy",this.updateBy)
				.append("updateTime",this.updateTime)
				.append("isDelete",this.isDelete)
				.append("orgIds",this.orgIds)
				.append("orgNames",this.orgNames)
				.append("userIds",this.userIds)
				.append("userNames",this.userNames)
				.toString();
	}
}
