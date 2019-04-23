package com.suneee.ucp.mh.model.attendance;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.suneee.core.model.BaseModel;
import org.apache.ibatis.type.Alias;


/**
 * 对象功能:结余调整日志 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 11:37:20
 */
@Alias("aVacationLog")
public class VacationLog extends BaseModel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2040585470080419232L;
	/** 1-手工调整 */
	public static final short TYPE_MODIFY = 1;
	/** 2-请假扣减 */
	public static final short TYPE_VACATOIN = 2;
	/** 3-过期清零 */
	public static final short TYPE_CLEAN = 3;
	/** 4-周期补充 */
	public static final short TYPE_SUPPLY = 4;
	/** 5-加班补充 */
	public static final short TYPE_OVERTIME = 5;
	/** 6-销假补充 */
	public static final short TYPE_BACK = 6;
	// ID
	protected Long id;
	// 假期类型
	protected Long vacationType;
	// 调整用户ID
	protected Long userId;
	// 变更类型(1、手工调整，2：请假扣减，3：过期清零，4：周期补充)
	protected Short changeType;
	// 变更值
	protected Double changeValue;
	/**
	 * 变更值单位
	 */
	protected Short changeUnit;
	// 变更值有效期
	protected Date validDate;
	// 调整前值
	protected Double beforeValue;
	// 调整后值
	protected Double afterValue;
	// 备注
	protected String memo;
	// 状态
	protected Short status;
	// 修改人
	protected Long updateBy;
	// 更新时间
	protected Date updatetime;
	
	// 用户工号
	protected String staffNo;
	protected String userAccount;
	protected String userName;
	protected String updateByName;
	protected String vacationName;
	
	/**
	 * 业务表单ID
	 */
	protected Long businessId;
	
	public String getStaffNo() {
		return staffNo;
	}
	public void setStaffNo(String staffNo) {
		this.staffNo = staffNo;
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUpdateByName() {
		return updateByName;
	}
	public void setUpdateByName(String updateByName) {
		this.updateByName = updateByName;
	}
	public String getVacationName() {
		return vacationName;
	}
	public void setVacationName(String vacationName) {
		this.vacationName = vacationName;
	}
	public Date getValidDate() {
		return validDate;
	}
	public void setValidDate(Date validDate) {
		this.validDate = validDate;
	}
	public Short getStatus() {
		return status;
	}
	public void setStatus(Short status) {
		this.status = status;
	}
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
	public void setVacationType(Long vacationType){
		this.vacationType = vacationType;
	}
	/**
	 * 返回 假期类型
	 * @return
	 */
	public Long getVacationType() {
		return this.vacationType;
	}
	public void setUserId(Long userId){
		this.userId = userId;
	}
	/**
	 * 返回 调整用户ID
	 * @return
	 */
	public Long getUserId() {
		return this.userId;
	}
	public Short getChangeType() {
		return changeType;
	}
	public void setChangeType(Short changeType) {
		this.changeType = changeType;
	}
	public void setChangeValue(Double changeValue){
		this.changeValue = changeValue;
	}
	/**
	 * @return the changeUnit
	 */
	public Short getChangeUnit() {
		return changeUnit;
	}
	/**
	 * @param changeUnit the changeUnit to set
	 */
	public void setChangeUnit(Short changeUnit) {
		this.changeUnit = changeUnit;
	}
	/**
	 * 返回 smallint
	 * @return
	 */
	public Double getChangeValue() {
		return this.changeValue;
	}
	public void setBeforeValue(Double beforeValue){
		this.beforeValue = beforeValue;
	}
	/**
	 * 返回 调整前值
	 * @return
	 */
	public Double getBeforeValue() {
		return this.beforeValue;
	}
	public void setAfterValue(Double afterValue){
		this.afterValue = afterValue;
	}
	/**
	 * 返回 调整后值
	 * @return
	 */
	public Double getAfterValue() {
		return this.afterValue;
	}
	public void setMemo(String memo){
		this.memo = memo;
	}
	/**
	 * 返回 备注
	 * @return
	 */
	public String getMemo() {
		return this.memo;
	}
	public void setUpdateBy(Long updateBy){
		this.updateBy = updateBy;
	}
	/**
	 * 返回 修改人
	 * @return
	 */
	public Long getUpdateBy() {
		return this.updateBy;
	}
	public void setUpdatetime(Date updatetime){
		this.updatetime = updatetime;
	}
	/**
	 * 返回 更新时间
	 * @return
	 */
	public Date getUpdatetime() {
		return this.updatetime;
	}
	
	/**
	 * @return the businessId
	 */
	public Long getBusinessId() {
		return businessId;
	}
	/**
	 * @param businessId the businessId to set
	 */
	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof VacationLog)) 
		{
			return false;
		}
		VacationLog rhs = (VacationLog) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.vacationType, rhs.vacationType)
		.append(this.userId, rhs.userId)
		.append(this.changeType, rhs.changeType)
		.append(this.changeValue, rhs.changeValue)
		.append(this.changeUnit, rhs.changeUnit)
		.append(this.beforeValue, rhs.beforeValue)
		.append(this.afterValue, rhs.afterValue)
		.append(this.memo, rhs.memo)
		.append(this.updateBy, rhs.updateBy)
		.append(this.updatetime, rhs.updatetime)
		.append(this.businessId, rhs.businessId)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.vacationType) 
		.append(this.userId) 
		.append(this.changeType) 
		.append(this.changeValue) 
		.append(this.changeUnit) 
		.append(this.beforeValue) 
		.append(this.afterValue) 
		.append(this.memo) 
		.append(this.updateBy) 
		.append(this.updatetime) 
		.append(this.businessId)
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("vacationType", this.vacationType) 
		.append("userId", this.userId) 
		.append("changeType", this.changeType) 
		.append("changeValue", this.changeValue) 
		.append("changeUnit", this.changeUnit) 
		.append("beforeValue", this.beforeValue) 
		.append("afterValue", this.afterValue) 
		.append("memo", this.memo) 
		.append("updateBy", this.updateBy) 
		.append("updatetime", this.updatetime) 
		.append("businessId", this.businessId)
		.toString();
	}
   
  

}