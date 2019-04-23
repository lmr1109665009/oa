package com.suneee.kaoqin.model.kaoqin;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;


/**
 * 对象功能:节假日设置 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:07:14
 */
public class HolidaysSetting extends BaseModel {
	/** -1-禁用 */
	public static final short STATUS_FORBIDDEN = -1;
	/** 0-正常 */
	public static final short STATUS_NORMAL = 0;
	// ID
	protected Long id;
	// 节假日名称
	protected String holidayName;
	// 开始日期
	protected Date startDate;
	// 结束日期
	protected Date endDate;
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
	public void setHolidayName(String holidayName){
		this.holidayName = holidayName;
	}
	/**
	 * 返回 节假日名称
	 * @return
	 */
	public String getHolidayName() {
		return this.holidayName;
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
		if (!(object instanceof HolidaysSetting)) 
		{
			return false;
		}
		HolidaysSetting rhs = (HolidaysSetting) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.holidayName, rhs.holidayName)
		.append(this.startDate, rhs.startDate)
		.append(this.endDate, rhs.endDate)
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
		.append(this.holidayName) 
		.append(this.startDate) 
		.append(this.endDate) 
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
		.append("holidayName", this.holidayName) 
		.append("startDate", this.startDate) 
		.append("endDate", this.endDate) 
		.append("status", this.status) 
		.append("createby", this.createby) 
		.append("createtime", this.createtime) 
		.toString();
	}
   
  

}