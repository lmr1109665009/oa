package com.suneee.platform.model.ats;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;


/**
 * 对象功能:考勤加班单 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-07-15 15:00:33
 */
public class AtsOverTime extends BaseModel {
	// 主键
	protected Long id;
	// 用户ID
	protected Long userId;
	// 加班类型
	protected String otType;
	// 开始时间
	protected Date startTime;
	// 结束时间
	protected Date endTime;
	// 加班时间
	protected Double otTime;
	// 加班补偿方式
	protected Short otCompens;
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
	public void setOtType(String otType){
		this.otType = otType;
	}
	/**
	 * 返回 加班类型
	 * @return
	 */
	public String getOtType() {
		return this.otType;
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
	public void setOtTime(Double otTime){
		this.otTime = otTime;
	}
	/**
	 * 返回 加班时间
	 * @return
	 */
	public Double getOtTime() {
		return this.otTime;
	}
	public void setOtCompens(Short otCompens){
		this.otCompens = otCompens;
	}
	/**
	 * 返回 加班补偿方式
	 * @return
	 */
	public Short getOtCompens() {
		return this.otCompens;
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
		if (!(object instanceof AtsOverTime)) 
		{
			return false;
		}
		AtsOverTime rhs = (AtsOverTime) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.userId, rhs.userId)
		.append(this.otType, rhs.otType)
		.append(this.startTime, rhs.startTime)
		.append(this.endTime, rhs.endTime)
		.append(this.otTime, rhs.otTime)
		.append(this.otCompens, rhs.otCompens)
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
		.append(this.otType) 
		.append(this.startTime) 
		.append(this.endTime) 
		.append(this.otTime) 
		.append(this.otCompens) 
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
		.append("otType", this.otType) 
		.append("startTime", this.startTime) 
		.append("endTime", this.endTime) 
		.append("otTime", this.otTime) 
		.append("otCompens", this.otCompens) 
		.append("runId", this.runId) 
		.toString();
	}
   
  

}