package com.suneee.kaoqin.model.kaoqin;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import com.suneee.core.model.BaseModel;
import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;


/**
 * 对象功能:排班设置 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:08:44
 */
public class ShiftSetting extends BaseModel {
	// ID
	protected Long id;
	// 日历ID
	protected Long calendarId;
	// 班次ID
	protected Long shiftId;
	// 开始日期
	protected Date startDate;
	// 结束日期
	protected Date endDate;
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
	public void setCalendarId(Long calendarId){
		this.calendarId = calendarId;
	}
	/**
	 * 返回 日历ID
	 * @return
	 */
	public Long getCalendarId() {
		return this.calendarId;
	}
	public void setShiftId(Long shiftId){
		this.shiftId = shiftId;
	}
	/**
	 * 返回 班次ID
	 * @return
	 */
	public Long getShiftId() {
		return this.shiftId;
	}
	public void setStartDate(Date startDate){
		this.startDate = startDate;
	}
	/**
	 * 返回 开始日期
	 * @return
	 */
	public Date getStartDate() {
		return this.startDate;
	}
	public void setEndDate(Date endDate){
		this.endDate = endDate;
	}
	/**
	 * 返回 结束日期
	 * @return
	 */
	public Date getEndDate() {
		return this.endDate;
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
		if (!(object instanceof ShiftSetting)) 
		{
			return false;
		}
		ShiftSetting rhs = (ShiftSetting) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.calendarId, rhs.calendarId)
		.append(this.shiftId, rhs.shiftId)
		.append(this.startDate, rhs.startDate)
		.append(this.endDate, rhs.endDate)
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
		.append(this.calendarId) 
		.append(this.shiftId) 
		.append(this.startDate) 
		.append(this.endDate) 
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
		.append("calendarId", this.calendarId) 
		.append("shiftId", this.shiftId) 
		.append("startDate", this.startDate) 
		.append("endDate", this.endDate) 
		.append("description", this.description) 
		.append("status", this.status) 
		.append("createby", this.createby) 
		.append("createtime", this.createtime) 
		.toString();
	}
   
  

}