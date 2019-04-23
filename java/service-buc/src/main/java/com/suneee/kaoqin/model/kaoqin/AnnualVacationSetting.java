package com.suneee.kaoqin.model.kaoqin;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.suneee.core.model.BaseModel;


/**
 * 对象功能:年假设置 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 11:30:12
 */
public class AnnualVacationSetting extends BaseModel {
	/** 1-入职年限 */
	public static final short TYPE_DUTY = 1;
	/** 2-工作年限 */
	public static final short TYPE_WORK = 2;
	// ID
	protected Long id;
	// 年限类型（1：入职年限，2：工作年限）
	protected Short yearType;
	// 年限
	protected Short yearLimit;
	// 假期天数
	protected Short days;

	public Short getYearType() {
		return yearType;
	}
	public void setYearType(Short yearType) {
		this.yearType = yearType;
	}
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
	public void setYearLimit(Short yearLimit){
		this.yearLimit = yearLimit;
	}
	/**
	 * 返回 年限
	 * @return
	 */
	public Short getYearLimit() {
		return this.yearLimit;
	}
	public void setDays(Short days){
		this.days = days;
	}
	/**
	 * 返回 假期天数
	 * @return
	 */
	public Short getDays() {
		return this.days;
	}
	

   	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof AnnualVacationSetting)) 
		{
			return false;
		}
		AnnualVacationSetting rhs = (AnnualVacationSetting) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.yearLimit, rhs.yearLimit)
		.append(this.days, rhs.days)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.yearLimit) 
		.append(this.days) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("yearLimit", this.yearLimit) 
		.append("days", this.days) 
		.toString();
	}
   
  

}