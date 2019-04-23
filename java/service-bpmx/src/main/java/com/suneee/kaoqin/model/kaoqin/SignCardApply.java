package com.suneee.kaoqin.model.kaoqin;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;


/**
 * 对象功能:签卡申请 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-03 14:37:01
 */
public class SignCardApply extends BaseModel {
	// 主键
	protected Long id;
	// 流水号
	protected String waterNum;
	// 申请人
	protected String userCn;
	// 申请人ID
	protected String userCnid;
	// 员工编号
	protected String userCode;
	// 所在部门
	protected String deptName;
	// 所在部门ID
	protected String deptNameid;
	// 签卡时间
	protected Date signTime;
	// 签卡证明
	protected String signProve;
	// 备注
	protected String comments;
	// 岗位级别
	protected Long positionLeve;
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
	
	

   	public String getWaterNum() {
		return waterNum;
	}
	public void setWaterNum(String waterNum) {
		this.waterNum = waterNum;
	}
	public String getUserCn() {
		return userCn;
	}
	public void setUserCn(String userCn) {
		this.userCn = userCn;
	}
	public String getUserCnid() {
		return userCnid;
	}
	public void setUserCnid(String userCnid) {
		this.userCnid = userCnid;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
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
	public Date getSignTime() {
		return signTime;
	}
	public void setSignTime(Date signTime) {
		this.signTime = signTime;
	}
	public String getSignProve() {
		return signProve;
	}
	public void setSignProve(String signProve) {
		this.signProve = signProve;
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
		if (!(object instanceof SignCardApply)) 
		{
			return false;
		}
		SignCardApply rhs = (SignCardApply) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.waterNum, rhs.waterNum)
		.append(this.userCn, rhs.userCn)
		.append(this.userCnid, rhs.userCnid)
		.append(this.userCode, rhs.userCode)
		.append(this.deptName, rhs.deptName)
		.append(this.deptNameid, rhs.deptNameid)
		.append(this.signTime, rhs.signTime)
		.append(this.signProve, rhs.signProve)
		.append(this.comments, rhs.comments)
		.append(this.positionLeve, rhs.positionLeve)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.waterNum) 
		.append(this.userCn) 
		.append(this.userCnid) 
		.append(this.userCode) 
		.append(this.deptName) 
		.append(this.deptNameid) 
		.append(this.signTime) 
		.append(this.signProve) 
		.append(this.comments) 
		.append(this.positionLeve) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("waterNum", this.waterNum) 
		.append("userCn", this.userCn) 
		.append("userCnid", this.userCnid) 
		.append("userCode", this.userCode) 
		.append("deptName", this.deptName) 
		.append("deptNameid", this.deptNameid) 
		.append("signTime", this.signTime) 
		.append("signProve", this.signProve) 
		.append("comments", this.comments) 
		.append("positionLeve", this.positionLeve) 
		.toString();
	}
   
  

}