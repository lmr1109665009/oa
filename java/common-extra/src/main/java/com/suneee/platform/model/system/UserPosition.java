package com.suneee.platform.model.system;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 对象功能:SYS_USER_POS Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2013-11-27 10:19:23
 */
public class UserPosition extends SysUserOrg
{
	/**
	 * 是否主要岗位(0否,1是)
	 */
	public final static short PRIMARY_YES=1;
	public final static short PRIMARY_NO=0;
	
	/**
	 * 是否负责人(0否,1是)
	 */
	public final static Short CHARRGE_YES=1;
	public final static Short CHARRGE_NO=0;
	
	/**
	 * 是否删除(0否,1是)
	 */
	public final static Short DELETE_YES=1;
	public final static Short DELETE_NO=0;
	
	/**
	 * 是否组织管理员（0，否，1是)
	 */
	public final static Short IS_GRADE_MANAGE=1;
	public final static Short IS_NOT_GRADE_MANAGE=0;
	
	public static final String DELFROM_USER_EDIT = "user-edit";
	public static final String DELFROM_USER_DEL = "user-del";
	public static final String DELFROM_ORG_DEL = "org-del";
	public static final String DELFROM_JOB_DEL = "job-del";
	public static final String DELFROM_POS_DEL = "pos-del";
	public static final String DELFROM_USER_POS_DEL = "user-pos-del";
	public static final String DELFROM_USER_BATCH_HANDLE = "user-batch-handle";
	
	// userposId
	protected Long  userPosId;
	// posId
	protected Long  posId;

	// ISDELETE 默认值0
	protected Short  isDelete=DELETE_NO;
	
	// 职务id
	protected Long  jobId;
	/**
	 * 删除源头：user-edit,user-del,job-del,org-del,user-pos-del
	 */ 
	protected String delFrom;
	// 职务名称
	protected String jobName;
	// 岗位名称
	protected String posName;
	//公司名称
	protected String company;
	
	//公司id
	protected Long companyId;
	
	// 员工状态，不做持久化
	protected Short status;
	
	/**
	 * 组织代码
	 */
	protected String orgCode;
	
	/**
	 * 组织名称路径
	 */
	protected String orgPathName;
	
	/**
	 * 职务级别
	 */
	protected Short grade;


	//员工状态
	private String userStatus;


	/**
	 * 工号
	 */
	private String staffNo;


	/**
	 * 字号
	 */
	protected String aliasName;

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getStaffNo() {
		return staffNo;
	}

	public void setStaffNo(String staffNo) {
		this.staffNo = staffNo;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getPosName() {
		return posName;
	}

	public void setPosName(String posName) {
		this.posName = posName;
	}

	public Long getUserPosId() {
		return userPosId;
	}

	public void setUserPosId(Long userPosId) {
		this.userPosId = userPosId;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public Long getPosId() {
		return posId;
	}

	public void setPosId(Long posId) {
		this.posId = posId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Short getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(Short isPrimary) {
		this.isPrimary = isPrimary;
	}

	public Short getIsCharge() {
		return isCharge;
	}

	public void setIsCharge(Short isCharge) {
		this.isCharge = isCharge;
	}

	public Short getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Short isDelete) {
		this.isDelete = isDelete;
	}
	
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	/**
	 * @return the orgCode
	 */
	public String getOrgCode() {
		return orgCode;
	}

	/**
	 * @param orgCode the orgCode to set
	 */
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	/**
	 * @return the orgPathName
	 */
	public String getOrgPathName() {
		return orgPathName;
	}

	/**
	 * @param orgPathName the orgPathName to set
	 */
	public void setOrgPathName(String orgPathName) {
		this.orgPathName = orgPathName;
	}

	/**
	 * @return the grade
	 */
	public Short getGrade() {
		return grade;
	}

	/**
	 * @param grade the grade to set
	 */
	public void setGrade(Short grade) {
		this.grade = grade;
	}

	/**
	 * @return the delFrom
	 */
	public String getDelFrom() {
		return delFrom;
	}

	/**
	 * @param delFrom the delFrom to set
	 */
	public void setDelFrom(String delFrom) {
		this.delFrom = delFrom;
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof UserPosition)) 
		{
			return false;
		}
		UserPosition rhs = (UserPosition) object;
		return new EqualsBuilder()
		.append(this.userPosId, rhs.userPosId)
		.append(this.orgId, rhs.orgId)
		.append(this.posId, rhs.posId)
		.append(this.userId, rhs.userId)
		.append(this.isPrimary, rhs.isPrimary)
		.append(this.isCharge, rhs.isCharge)
		.append(this.isDelete, rhs.isDelete)
		.append(this.jobId, rhs.jobId)
		.append(this.delFrom, rhs.delFrom)
		.append(this.createtime, rhs.createtime)
		.append(this.createBy, rhs.createBy)
		.append(this.updatetime, rhs.updatetime)
		.append(this.updateBy, rhs.updateBy)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.userPosId) 
		.append(this.orgId) 
		.append(this.posId) 
		.append(this.userId) 
		.append(this.isPrimary) 
		.append(this.isCharge) 
		.append(this.isDelete) 
		.append(this.jobId)
		.append(this.delFrom)
		.append(this.createtime)
		.append(this.createBy)
		.append(this.updatetime)
		.append(this.updateBy)
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("userposId", this.userPosId) 
		.append("orgId", this.orgId) 
		.append("posId", this.posId) 
		.append("userId", this.userId) 
		.append("isPrimary", this.isPrimary) 
		.append("isCharge", this.isCharge) 
		.append("isDelete", this.isDelete) 
		.append("jobId", this.jobId)
		.append("delFrom", this.delFrom)
		.append("createtime", this.createtime)
		.append("createBy", this.createBy)
		.append("updatetime", this.updatetime)
		.append("updateBy", this.updateBy) 
		.toString();
	}
   
  

}