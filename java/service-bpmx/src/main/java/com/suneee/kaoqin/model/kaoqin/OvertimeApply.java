package com.suneee.kaoqin.model.kaoqin;

import java.util.Date;
import com.suneee.core.model.BaseModel;
import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;


/**
 * 对象功能:加班申请流程 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-05 10:20:27
 */
public class OvertimeApply extends BaseModel {
	// 主键
	protected Long id;
	// 申请人
	protected String applyUser;
	// 申请人ID
	protected String applyUserid;
	// 所在部门
	protected String dept;
	// 所在部门ID
	protected String deptid;
	// 加班开始时间
	protected Date startTime;
	// 加班结束时间
	protected Date endTime;
	// 加班时长
	protected Long overtime;
	// 加班原因
	protected String reason;
	// 岗位级别
	protected Long positionLeve;
	// 流水号
	protected String waterNum;
	// 员工编号
	protected String account;
	//流程实例ID
	protected Long instanceId;
	//当前审批人
	protected String assingner;
	//流程名称
	protected String proccessName;
	// 申请时间
	protected Date applyDate;

	public Date getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
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
	public Long getOvertime() {
		return overtime;
	}
	public void setOvertime(Long overtime) {
		this.overtime = overtime;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Long getPositionLeve() {
		return positionLeve;
	}
	public void setPositionLeve(Long positionLeve) {
		this.positionLeve = positionLeve;
	}
	public String getWaterNum() {
		return waterNum;
	}
	public void setWaterNum(String waterNum) {
		this.waterNum = waterNum;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
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
		if (!(object instanceof OvertimeApply)) 
		{
			return false;
		}
		OvertimeApply rhs = (OvertimeApply) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.applyUser, rhs.applyUser)
		.append(this.applyUserid, rhs.applyUserid)
		.append(this.dept, rhs.dept)
		.append(this.deptid, rhs.deptid)
		.append(this.startTime, rhs.startTime)
		.append(this.endTime, rhs.endTime)
		.append(this.overtime, rhs.overtime)
		.append(this.reason, rhs.reason)
		.append(this.positionLeve, rhs.positionLeve)
		.append(this.waterNum, rhs.waterNum)
		.append(this.account, rhs.account)
		.append(this.applyDate, rhs.applyDate)
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
		.append(this.dept) 
		.append(this.deptid) 
		.append(this.startTime) 
		.append(this.endTime) 
		.append(this.overtime) 
		.append(this.reason) 
		.append(this.positionLeve) 
		.append(this.waterNum) 
		.append(this.account)
		.append(this.applyDate)
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
		.append("dept", this.dept) 
		.append("deptid", this.deptid) 
		.append("startTime", this.startTime) 
		.append("endTime", this.endTime) 
		.append("overtime", this.overtime) 
		.append("reason", this.reason) 
		.append("positionLeve", this.positionLeve) 
		.append("waterNum", this.waterNum) 
		.append("account", this.account) 
		.append("applyDate", this.applyDate) 
		.toString();
	}
   
}