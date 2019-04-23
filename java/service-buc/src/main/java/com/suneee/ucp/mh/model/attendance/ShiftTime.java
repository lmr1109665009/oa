package com.suneee.ucp.mh.model.attendance;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.ibatis.type.Alias;

import java.util.Date;


/**
 * 对象功能:班次时间段 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:09:27
 */
@Alias("aShiftTime")
public class ShiftTime extends BaseModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2748138962039052366L;
	/** -1-禁用 */
	public static final short STATUS_FORBIDDEN = -1;
	/** 0-正常 */
	public static final short STATUS_NORMAL = 0;
	/**
	 * 类型：1-考勤时间
	 */
	public static final short TYPE_WORK = 1;
	/**
	 * 类型：2-休息时间
	 */
	public static final short TYPE_REST = 2;
	// ID
	protected Long id;
	// 班次ID
	protected Long shiftId;
	// 开始时间
	protected Date startTime;
	// 结束时间
	protected Date endTime;
	// 休息时长（分钟）
	protected Long restTime;
	// 状态（0：正常，-1：禁用）
	protected Short status;
	// 创建人ID
	protected Long createby;
	// 创建时间
	protected Date createtime;
	/**
	 * 开始时间取卡范围的开始取卡点
	 */
	protected Date startBeginRange;
	/**
	 * 开始时间取卡范围的结束取卡点
	 */
	protected Date startEndRange;
	/**
	 * 结束时间取卡范围的开始取卡点
	 */
	protected Date endBeginRange;
	/**
	 * 结束时间取卡范围的结束取卡点
	 */
	protected Date endEndRange;
	/**
	 * 类型（1-考勤时间，2-休息时间）
	 */
	protected Short type;
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
	public void setStartTime(Date startTime){
		this.startTime = startTime;
	}
	/**
	 * 返回 开始时间
	 * @return
	 */
	public Date getStartTime() {
		return this.startTime;
	}
	public void setEndTime(Date endTime){
		this.endTime = endTime;
	}
	/**
	 * 返回 结束时间
	 * @return
	 */
	public Date getEndTime() {
		return this.endTime;
	}
	public void setRestTime(Long restTime){
		this.restTime = restTime;
	}
	/**
	 * 返回 休息时长（分钟）
	 * @return
	 */
	public Long getRestTime() {
		return this.restTime;
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
	 * @return the startBeginRange
	 */
	public Date getStartBeginRange() {
		return startBeginRange;
	}
	/**
	 * @param startBeginRange the startBeginRange to set
	 */
	public void setStartBeginRange(Date startBeginRange) {
		this.startBeginRange = startBeginRange;
	}
	/**
	 * @return the startEndRange
	 */
	public Date getStartEndRange() {
		return startEndRange;
	}
	/**
	 * @param startEndRange the startEndRange to set
	 */
	public void setStartEndRange(Date startEndRange) {
		this.startEndRange = startEndRange;
	}
	/**
	 * @return the endBeginRange
	 */
	public Date getEndBeginRange() {
		return endBeginRange;
	}
	/**
	 * @param endBeginRange the endBeginRange to set
	 */
	public void setEndBeginRange(Date endBeginRange) {
		this.endBeginRange = endBeginRange;
	}
	/**
	 * @return the endEndRange
	 */
	public Date getEndEndRange() {
		return endEndRange;
	}
	/**
	 * @param endEndRange the endEndRange to set
	 */
	public void setEndEndRange(Date endEndRange) {
		this.endEndRange = endEndRange;
	}
	/**
	 * @return the type
	 */
	public Short getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(Short type) {
		this.type = type;
	}
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof ShiftTime)) 
		{
			return false;
		}
		ShiftTime rhs = (ShiftTime) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.shiftId, rhs.shiftId)
		.append(this.startTime, rhs.startTime)
		.append(this.endTime, rhs.endTime)
		.append(this.restTime, rhs.restTime)
		.append(this.status, rhs.status)
		.append(this.createby, rhs.createby)
		.append(this.createtime, rhs.createtime)
		.append(this.startBeginRange, rhs.startBeginRange)
		.append(this.startEndRange, rhs.startEndRange)
		.append(this.endBeginRange, rhs.endBeginRange)
		.append(this.endEndRange, rhs.endEndRange)
		.append(this.type, rhs.type)
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
		.append(this.startTime) 
		.append(this.endTime) 
		.append(this.restTime) 
		.append(this.status) 
		.append(this.createby) 
		.append(this.createtime) 
		.append(this.startBeginRange)
		.append(this.startEndRange)
		.append(this.endBeginRange)
		.append(this.endEndRange)
		.append(this.type)
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
		.append("startTime", this.startTime) 
		.append("endTime", this.endTime) 
		.append("restTime", this.restTime) 
		.append("status", this.status) 
		.append("createby", this.createby) 
		.append("createtime", this.createtime)
		.append("startBeginRange", this.startBeginRange)
		.append("startEndRange", this.startEndRange)
		.append("endBeginRange", this.endBeginRange)
		.append("endEndRange", this.endEndRange)
		.append("type", this.type)
		.toString();
	}
   
  

}