package com.suneee.kaoqin.model.kaoqin;

import java.util.Date;
import com.suneee.core.model.BaseModel;
import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;


/**
 * 对象功能:请假申请 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-11 09:35:17
 */
public class LeaveApply extends BaseModel {
	// 主键
	protected Long id;
	// 申请人
	protected String applyUser;
	// 申请人ID
	protected String applyUserid;
	// 工号
	protected String account;
	// 申请时间
	protected Date applyDate;
	// 请假类型
	protected String leaveType;
	// 请假单位 1天，2小时
	protected String leaveUnit;
	// 开始时间
	protected Date startTime;
	// 请假结束时间
	protected Date endTime;
	// 请假时长
	protected Long leaveLong;
	// 请假原因
	protected String reason;
	// 岗位级别
	protected Long position;
	// 所在部门
	protected String dept;
	// 所在部门ID
	protected String deptid;
	//流程实例ID
	protected Long instanceId;
	//当前审批人
	protected String assingner;
	//流程名称
	protected String proccessName;

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
	
	

   	public String getApplyUser() {
		return applyUser;
	}
	public void setApplyUser(String applyUser) {
		this.applyUser = applyUser;
	}
	public String getApplyUserid() {
		return applyUserid;
	}
	public void setApplyUserid(String applyUserid) {
		this.applyUserid = applyUserid;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public Date getApplyDate() {
		return applyDate;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getDeptid() {
		return deptid;
	}
	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	public String getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}
	public String getLeaveUnit() {
		return leaveUnit;
	}
	public void setLeaveUnit(String leaveUnit) {
		this.leaveUnit = leaveUnit;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Long getLeaveLong() {
		return leaveLong;
	}
	public void setLeaveLong(Long leaveLong) {
		this.leaveLong = leaveLong;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Long getPosition() {
		return position;
	}
	public void setPosition(Long position) {
		this.position = position;
	}
	public Long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}
	public String getAssingner() {
		return assingner;
	}
	public void setAssingner(String assingner) {
		this.assingner = assingner;
	}
	public String getProccessName() {
		return proccessName;
	}
	public void setProccessName(String proccessName) {
		this.proccessName = proccessName;
	}
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof LeaveApply)) 
		{
			return false;
		}
		LeaveApply rhs = (LeaveApply) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.applyUser, rhs.applyUser)
		.append(this.applyUserid, rhs.applyUserid)
		.append(this.account, rhs.account)
		.append(this.applyDate, rhs.applyDate)
		.append(this.leaveType, rhs.leaveType)
		.append(this.leaveUnit, rhs.leaveUnit)
		.append(this.startTime, rhs.startTime)
		.append(this.endTime, rhs.endTime)
		.append(this.leaveLong, rhs.leaveLong)
		.append(this.reason, rhs.reason)
		.append(this.position, rhs.position)
		.append(this.dept, rhs.dept)
		.append(this.deptid, rhs.deptid)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.applyUser) 
		.append(this.applyUserid) 
		.append(this.account) 
		.append(this.applyDate) 
		.append(this.leaveType) 
		.append(this.leaveUnit) 
		.append(this.startTime) 
		.append(this.endTime) 
		.append(this.leaveLong) 
		.append(this.reason) 
		.append(this.position)
		.append(this.dept) 
		.append(this.deptid)
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("applyUser", this.applyUser) 
		.append("applyUserid", this.applyUserid) 
		.append("account", this.account) 
		.append("applyDate", this.applyDate) 
		.append("leaveType", this.leaveType) 
		.append("leaveUnit", this.leaveUnit) 
		.append("startTime", this.startTime) 
		.append("endTime", this.endTime) 
		.append("leaveLong", this.leaveLong) 
		.append("reason", this.reason) 
		.append("position", this.position)
		.append("dept", this.dept) 
		.append("deptid", this.deptid)
		.toString();
	}
   
  

}