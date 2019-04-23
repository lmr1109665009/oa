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
 * 对象功能:出差申请 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-04 14:57:21
 */
public class BusinessTravel extends BaseModel {
	// 主键
	protected Long id;
	// 申请人
	protected String applyUser;
	// 申请人ID
	protected String applyUserid;
	// 所在部门
	protected String deptName;
	// 所在部门ID
	protected String deptNameid;
	// 出差开始时间
	protected Date startTime;
	// 出差结束时间
	protected Date endTime;
	// 出差地点
	protected String address;
	// 出差说明
	protected String comments;
	// 岗位级别
	protected Long positionLeve;
	// 流水号
	protected String waterNum;
	//流程实例ID
	protected Long instanceId;
	//当前审批人
	protected String assingner;
	//流程名称
	protected String proccessName;
	//员工编号
	protected String account;
	//申请时间
	protected Date applyDate;

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
	public String getAccount() {
		return account;
	}
	public Date getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	public void setAccount(String account) {
		this.account = account;
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
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getDeptNameid() {
		return deptNameid;
	}
	public void setDeptNameid(String deptNameid) {
		this.deptNameid = deptNameid;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
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
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof BusinessTravel)) 
		{
			return false;
		}
		BusinessTravel rhs = (BusinessTravel) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.applyUser, rhs.applyUser)
		.append(this.applyUserid, rhs.applyUserid)
		.append(this.deptName, rhs.deptName)
		.append(this.deptNameid, rhs.deptNameid)
		.append(this.startTime, rhs.startTime)
		.append(this.endTime, rhs.endTime)
		.append(this.address, rhs.address)
		.append(this.comments, rhs.comments)
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
		.append(this.deptName) 
		.append(this.deptNameid) 
		.append(this.startTime) 
		.append(this.endTime) 
		.append(this.address) 
		.append(this.comments) 
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
		.append("deptName", this.deptName) 
		.append("deptNameid", this.deptNameid) 
		.append("startTime", this.startTime) 
		.append("endTime", this.endTime) 
		.append("address", this.address) 
		.append("comments", this.comments) 
		.append("positionLeve", this.positionLeve) 
		.append("waterNum", this.waterNum)
		.append("account", this.account)
		.append("applyDate", this.applyDate)
		.toString();
	}
   
  

}