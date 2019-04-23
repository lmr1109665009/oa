package com.suneee.kaoqin.model.kaoqin;

import java.util.Date;
import com.suneee.core.model.BaseModel;
import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;


/**
 * 对象功能:外出申请 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 16:30:42
 */
public class GoOutApply extends BaseModel {
	// 主键
	protected Long id;
	// 流水号
	protected String warterNum;
	// 申请人
	protected String userCn;
	// 申请人ID
	protected String userCnid;
	// 所在部门
	protected String deptName;
	// 所在部门ID
	protected String deptNameid;
	// 员工编号
	protected String userCode;
	// 开始时间
	protected Date startTime;
	// 结束时间
	protected Date endTime;
	// 时长
	protected String timeLong;
	// 备注
	protected String comments;
	// 外出时间
	protected Date outTime;
	//岗位级别
	protected Integer positionLeve;
	//流程实例ID
	protected Long instanceId;
	//当前审批人
	protected String assingner;

	public String getAssingner() {
		return assingner;
	}
	public void setAssingner(String assingner) {
		this.assingner = assingner;
	}
	public Long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
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
	public void setWarterNum(String warterNum){
		this.warterNum = warterNum;
	}
	/**
	 * 返回 流水号
	 * @return
	 */
	public String getWarterNum() {
		return this.warterNum;
	}
	public Integer getPositionLeve() {
		return positionLeve;
	}
	public void setPositionLeve(Integer positionLeve) {
		this.positionLeve = positionLeve;
	}
	public void setUserCn(String userCn){
		this.userCn = userCn;
	}
	/**
	 * 返回 申请人
	 * @return
	 */
	public String getUserCn() {
		return this.userCn;
	}
	public void setUserCnid(String userCnid){
		this.userCnid = userCnid;
	}
	/**
	 * 返回 申请人ID
	 * @return
	 */
	public String getUserCnid() {
		return this.userCnid;
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
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
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
	public String getTimeLong() {
		return timeLong;
	}
	public void setTimeLong(String timeLong) {
		this.timeLong = timeLong;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public Date getOutTime() {
		return outTime;
	}
	public void setOutTime(Date outTime) {
		this.outTime = outTime;
	}
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof GoOutApply)) 
		{
			return false;
		}
		GoOutApply rhs = (GoOutApply) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.warterNum, rhs.warterNum)
		.append(this.userCn, rhs.userCn)
		.append(this.userCnid, rhs.userCnid)
		.append(this.deptName, rhs.deptName)
		.append(this.deptNameid, rhs.deptNameid)
		.append(this.userCode, rhs.userCode)
		.append(this.startTime, rhs.startTime)
		.append(this.endTime, rhs.endTime)
		.append(this.timeLong, rhs.timeLong)
		.append(this.comments, rhs.comments)
		.append(this.outTime, rhs.outTime)
		.append(this.positionLeve,rhs.positionLeve)
		.append(this.instanceId,rhs.instanceId)
		.append(this.assingner,rhs.assingner)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.warterNum) 
		.append(this.userCn) 
		.append(this.userCnid) 
		.append(this.deptName) 
		.append(this.deptNameid) 
		.append(this.userCode) 
		.append(this.startTime) 
		.append(this.endTime) 
		.append(this.timeLong) 
		.append(this.comments) 
		.append(this.outTime)
		.append(this.positionLeve)
		.append(this.instanceId)
		.append(this.assingner)
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("warterNum", this.warterNum) 
		.append("userCn", this.userCn) 
		.append("userCnid", this.userCnid) 
		.append("deptName", this.deptName) 
		.append("deptNameid", this.deptNameid) 
		.append("userCode", this.userCode) 
		.append("startTime", this.startTime) 
		.append("endTime", this.endTime) 
		.append("timeLong", this.timeLong) 
		.append("comments", this.comments) 
		.append("outTime", this.outTime)
		.append("positionLeve", this.positionLeve) 
		.append("instanceId", this.instanceId)
		.append("assingner", this.assingner)
		.toString();
	}
   
  

}