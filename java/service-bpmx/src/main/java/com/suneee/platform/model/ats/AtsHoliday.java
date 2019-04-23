package com.suneee.platform.model.ats;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import com.suneee.core.model.BaseModel;
import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;


/**
 * 对象功能:考勤请假单 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-07-15 14:59:52
 */
public class AtsHoliday extends BaseModel {
	// 主键
	protected Long id;
	// 用户ID
	protected Long userId;
	// 请假类型
	protected String holidayType;
	// 开始时间
	protected Date startTime;
	// 结束时间
	protected Date endTime;
	// 请假时间
	protected Double holidayTime;
	// 时间长度
	protected Short duration;
	// 流程运行ID
	protected Long runId;

	public void setId(Long id){
		this.id = id;
	}
	/**
	 * 返回 主键
	 * @return
	 */
	public Long getId() {
		return this.id;
	}
	public void setUserId(Long userId){
		this.userId = userId;
	}
	/**
	 * 返回 用户ID
	 * @return
	 */
	public Long getUserId() {
		return this.userId;
	}
	public void setHolidayType(String holidayType){
		this.holidayType = holidayType;
	}
	/**
	 * 返回 请假类型
	 * @return
	 */
	public String getHolidayType() {
		return this.holidayType;
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
	public void setHolidayTime(Double holidayTime){
		this.holidayTime = holidayTime;
	}
	/**
	 * 返回 请假时间
	 * @return
	 */
	public Double getHolidayTime() {
		return this.holidayTime;
	}
	public void setDuration(Short duration){
		this.duration = duration;
	}
	/**
	 * 返回 时间长度
	 * @return
	 */
	public Short getDuration() {
		return this.duration;
	}
	public void setRunId(Long runId){
		this.runId = runId;
	}
	/**
	 * 返回 流程运行ID
	 * @return
	 */
	public Long getRunId() {
		return this.runId;
	}
	

   	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof AtsHoliday)) 
		{
			return false;
		}
		AtsHoliday rhs = (AtsHoliday) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.userId, rhs.userId)
		.append(this.holidayType, rhs.holidayType)
		.append(this.startTime, rhs.startTime)
		.append(this.endTime, rhs.endTime)
		.append(this.holidayTime, rhs.holidayTime)
		.append(this.duration, rhs.duration)
		.append(this.runId, rhs.runId)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.userId) 
		.append(this.holidayType) 
		.append(this.startTime) 
		.append(this.endTime) 
		.append(this.holidayTime) 
		.append(this.duration) 
		.append(this.runId) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("userId", this.userId) 
		.append("holidayType", this.holidayType) 
		.append("startTime", this.startTime) 
		.append("endTime", this.endTime) 
		.append("holidayTime", this.holidayTime) 
		.append("duration", this.duration) 
		.append("runId", this.runId) 
		.toString();
	}
   
  

}