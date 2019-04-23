package com.suneee.ucp.mh.model.attendance;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.ibatis.type.Alias;

import java.util.Date;


/**
 * 对象功能:假期类型 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 10:43:17
 */
@Alias("aAttendanceVacation")
public class AttendanceVacation extends BaseModel{
	// ID
	protected Long id;
	// 代码
	protected String code;
	// 假期名称
	protected String name;
	// 单位（1：天，2：小时）
	protected Short unit;
	// 申请年限（单位年）
	protected Short yearLimit = 0;
	// 申请最小值
	protected Double minApply;
	// 有效期（单位月，空为不限）
	protected Short validTime;
	// 分配时长（同单位）
	protected Short assignLimit;
	// 系统预设（0：是，1：否）默认0
	protected Short sysDef = 0;
	// 状态（0：正常，-1：禁用）默认0
	protected Short status = 0;
	// 创建人ID
	protected Long createby;
	// 创建时间
	protected Date createtime;
	// 是否允许透资（0：否，1：是）默认0
	protected Short allowOver = 0;
	// 允许透资值
	protected Short overLimit;
	
	private String unitName;
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public Short getAllowOver() {
		return allowOver;
	}
	public void setAllowOver(Short allowOver) {
		this.allowOver = allowOver;
	}
	public Short getOverLimit() {
		return overLimit;
	}
	public void setOverLimit(Short overLimit) {
		this.overLimit = overLimit;
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
	public void setCode(String code){
		this.code = code;
	}
	/**
	 * 返回 代码
	 * @return
	 */
	public String getCode() {
		return this.code;
	}
	public void setName(String name){
		this.name = name;
	}
	/**
	 * 返回 假期名称
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	public void setUnit(Short unit){
		this.unit = unit;
	}
	/**
	 * 返回 单位（1：天，2：小时）
	 * @return
	 */
	public Short getUnit() {
		return this.unit;
	}
	public void setYearLimit(Short yearLimit){
		this.yearLimit = yearLimit;
	}
	/**
	 * 返回 申请年限（单位年）
	 * @return
	 */
	public Short getYearLimit() {
		return this.yearLimit;
	}
	public void setMinApply(Double minApply){
		this.minApply = minApply;
	}
	/**
	 * 返回 申请最小值
	 * @return
	 */
	public Double getMinApply() {
		return this.minApply;
	}
	public void setValidTime(Short validTime){
		this.validTime = validTime;
	}
	/**
	 * 返回 有效期（单位月，空为不限）
	 * @return
	 */
	public Short getValidTime() {
		return this.validTime;
	}
	public void setAssignLimit(Short assignLimit){
		this.assignLimit = assignLimit;
	}
	/**
	 * 返回 分配时长（同单位）
	 * @return
	 */
	public Short getAssignLimit() {
		return this.assignLimit;
	}
	public void setSysDef(Short sysDef){
		this.sysDef = sysDef;
	}
	/**
	 * 返回 系统预设（0：是，1：否）
	 * @return
	 */
	public Short getSysDef() {
		return this.sysDef;
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
		if (!(object instanceof AttendanceVacation)) 
		{
			return false;
		}
		AttendanceVacation rhs = (AttendanceVacation) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.code, rhs.code)
		.append(this.name, rhs.name)
		.append(this.unit, rhs.unit)
		.append(this.yearLimit, rhs.yearLimit)
		.append(this.minApply, rhs.minApply)
		.append(this.validTime, rhs.validTime)
		.append(this.assignLimit, rhs.assignLimit)
		.append(this.sysDef, rhs.sysDef)
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
		.append(this.code) 
		.append(this.name) 
		.append(this.unit) 
		.append(this.yearLimit) 
		.append(this.minApply) 
		.append(this.validTime) 
		.append(this.assignLimit) 
		.append(this.sysDef) 
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
		.append("code", this.code) 
		.append("name", this.name) 
		.append("unit", this.unit) 
		.append("yearLimit", this.yearLimit) 
		.append("minApply", this.minApply) 
		.append("validTime", this.validTime) 
		.append("assignLimit", this.assignLimit) 
		.append("sysDef", this.sysDef) 
		.append("status", this.status) 
		.append("createby", this.createby) 
		.append("createtime", this.createtime) 
		.toString();
	}
   
  

}