package com.suneee.kaoqin.model.kaoqin;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;


/**
 * 对象功能:单日排班设置 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:08:00
 */
public class ShiftDaySetting extends BaseModel {
	/** -1-禁用 */
	public static final short STATUS_FORBIDDEN = -1;
	/** 0-正常 */
	public static final short STATUS_NORMAL = 0;
	/** 1-工作 */
	public static final short TYPE_WORK = 1;
	/** 2-休息 */
	public static final short TYPE_REST = 2;
	// ID
	protected Long id;
	// 班次ID
	protected Long settingId;
	// 设置日期
	protected Date scheduleDate;
	// 排班类型（1：工作，2：休息）
	protected Short scheduleType;
	// 备注说明
	protected String description;
	// 状态（0：正常，-1：禁用）
	protected Short status;
	// 创建人ID
	protected Long createby;
	// 创建时间
	protected Date createtime;

	public void setId(Long id){
		this.id = id;
	}
	/**
	 * 返回 ID
	 * @return
	 */
	public Long getId() {
		return this.id;
	}
	public void setSettingId(Long settingId){
		this.settingId = settingId;
	}
	/**
	 * 返回 排班ID
	 * @return
	 */
	public Long getSettingId() {
		return this.settingId;
	}
	public void setScheduleDate(Date scheduleDate){
		this.scheduleDate = scheduleDate;
	}
	/**
	 * 返回 开始日期
	 * @return
	 */
	public Date getScheduleDate() {
		return this.scheduleDate;
	}
	public void setScheduleType(Short scheduleType){
		this.scheduleType = scheduleType;
	}
	/**
	 * 返回 排班类型（1：工作，2：休息）
	 * @return
	 */
	public Short getScheduleType() {
		return this.scheduleType;
	}
	public void setDescription(String description){
		this.description = description;
	}
	/**
	 * 返回 备注说明
	 * @return
	 */
	public String getDescription() {
		return this.description;
	}
	public void setStatus(Short status){
		this.status = status;
	}
	/**
	 * 返回 状态（0：正常，-1：禁用）
	 * @return
	 */
	public Short getStatus() {
		return this.status;
	}
	public void setCreateby(Long createby){
		this.createby = createby;
	}
	/**
	 * 返回 创建人ID
	 * @return
	 */
	public Long getCreateby() {
		return this.createby;
	}
	public void setCreatetime(Date createtime){
		this.createtime = createtime;
	}
	/**
	 * 返回 创建时间
	 * @return
	 */
	public Date getCreatetime() {
		return this.createtime;
	}
	

   	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof ShiftDaySetting)) 
		{
			return false;
		}
		ShiftDaySetting rhs = (ShiftDaySetting) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.settingId, rhs.settingId)
		.append(this.scheduleDate, rhs.scheduleDate)
		.append(this.scheduleType, rhs.scheduleType)
		.append(this.description, rhs.description)
		.append(this.status, rhs.status)
		.append(this.createby, rhs.createby)
		.append(this.createtime, rhs.createtime)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.settingId) 
		.append(this.scheduleDate) 
		.append(this.scheduleType) 
		.append(this.description) 
		.append(this.status) 
		.append(this.createby) 
		.append(this.createtime) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("settingId", this.settingId) 
		.append("scheduleDate", this.scheduleDate) 
		.append("scheduleType", this.scheduleType) 
		.append("description", this.description) 
		.append("status", this.status) 
		.append("createby", this.createby) 
		.append("createtime", this.createtime) 
		.toString();
	}
   
  

}