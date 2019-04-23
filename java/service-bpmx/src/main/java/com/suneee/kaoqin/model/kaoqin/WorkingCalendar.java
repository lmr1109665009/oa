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
 * 对象功能:工作日历 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:12:02
 */
public class WorkingCalendar extends BaseModel {
	// 日历ID
	protected Long id;
	// 日历名称
	protected String name;
	// 周一（1：上班，2：休息）
	protected Short monStatus;
	// 周二
	protected Short tueStatus;
	// 周三
	protected Short wedStatus;
	// 周四
	protected Short thuStatus;
	// 周五
	protected Short friStatus;
	// 周六
	protected Short satStatus;
	// 周日
	protected Short sunStatus;
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
	 * 返回 日历ID
	 * @return
	 */
	public Long getId() {
		return this.id;
	}
	public void setName(String name){
		this.name = name;
	}
	/**
	 * 返回 日历名称
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	public void setMonStatus(Short monStatus){
		this.monStatus = monStatus;
	}
	/**
	 * 返回 周一（1：上班，2：休息）
	 * @return
	 */
	public Short getMonStatus() {
		return this.monStatus;
	}
	public void setTueStatus(Short tueStatus){
		this.tueStatus = tueStatus;
	}
	/**
	 * 返回 周二
	 * @return
	 */
	public Short getTueStatus() {
		return this.tueStatus;
	}
	public void setWedStatus(Short wedStatus){
		this.wedStatus = wedStatus;
	}
	/**
	 * 返回 周三
	 * @return
	 */
	public Short getWedStatus() {
		return this.wedStatus;
	}
	public void setThuStatus(Short thuStatus){
		this.thuStatus = thuStatus;
	}
	/**
	 * 返回 周四
	 * @return
	 */
	public Short getThuStatus() {
		return this.thuStatus;
	}
	public void setFriStatus(Short friStatus){
		this.friStatus = friStatus;
	}
	/**
	 * 返回 周五
	 * @return
	 */
	public Short getFriStatus() {
		return this.friStatus;
	}
	public void setSatStatus(Short satStatus){
		this.satStatus = satStatus;
	}
	/**
	 * 返回 周六
	 * @return
	 */
	public Short getSatStatus() {
		return this.satStatus;
	}
	public void setSunStatus(Short sunStatus){
		this.sunStatus = sunStatus;
	}
	/**
	 * 返回 周日
	 * @return
	 */
	public Short getSunStatus() {
		return this.sunStatus;
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
		if (!(object instanceof WorkingCalendar)) 
		{
			return false;
		}
		WorkingCalendar rhs = (WorkingCalendar) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.name, rhs.name)
		.append(this.monStatus, rhs.monStatus)
		.append(this.tueStatus, rhs.tueStatus)
		.append(this.wedStatus, rhs.wedStatus)
		.append(this.thuStatus, rhs.thuStatus)
		.append(this.friStatus, rhs.friStatus)
		.append(this.satStatus, rhs.satStatus)
		.append(this.sunStatus, rhs.sunStatus)
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
		.append(this.name) 
		.append(this.monStatus) 
		.append(this.tueStatus) 
		.append(this.wedStatus) 
		.append(this.thuStatus) 
		.append(this.friStatus) 
		.append(this.satStatus) 
		.append(this.sunStatus) 
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
		.append("name", this.name) 
		.append("monStatus", this.monStatus) 
		.append("tueStatus", this.tueStatus) 
		.append("wedStatus", this.wedStatus) 
		.append("thuStatus", this.thuStatus) 
		.append("friStatus", this.friStatus) 
		.append("satStatus", this.satStatus) 
		.append("sunStatus", this.sunStatus) 
		.append("status", this.status) 
		.append("createby", this.createby) 
		.append("createtime", this.createtime) 
		.toString();
	}
   
  

}