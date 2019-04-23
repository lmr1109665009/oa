package com.suneee.kaoqin.model.kaoqin;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * 对象功能:班次日历 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:mikel
 * 创建时间:2017-05-04 16:19:21
 */
public class ShiftCalendar extends BaseModel {
	/** 1-工作日 */
	public static final short TYPE_WORK = 1;
	/** 2-休息日 */
	public static final short TYPE_RESK = 2;
	// 日历ID
	protected Long id;
	// 班次ID
	protected Long shiftId;
	// 星期
	protected Short week;
	// 日期类型（1：工作日，2：休息日）
	protected Short dayType;

	public void setId(Long id){
		this.id = id;
	}
	/**
	 * 返回 日历ID
	 * @return
	 */
	public Long getId() {
		return this.id;
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
	public void setWeek(Short week){
		this.week = week;
	}
	/**
	 * 返回 星期
	 * @return
	 */
	public Short getWeek() {
		return this.week;
	}
	
	public void setDayType(Short dayType){
		this.dayType = dayType;
	}
	/**
	 * 返回 日期类型（1：工作日，2：休息日）
	 * @return
	 */
	public Short getDayType() {
		return this.dayType;
	}
	

   	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof ShiftCalendar)) 
		{
			return false;
		}
		ShiftCalendar rhs = (ShiftCalendar) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.shiftId, rhs.shiftId)
		.append(this.week, rhs.week)
		.append(this.dayType, rhs.dayType)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.shiftId) 
		.append(this.week) 
		.append(this.dayType) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("shiftId", this.shiftId) 
		.append("week", this.week) 
		.append("dayType", this.dayType) 
		.toString();
	}
   
  

}