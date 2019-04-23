package com.suneee.kaoqin.model.kaoqin;

import java.util.Date;

import com.suneee.core.model.BaseModel;
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
@Alias("kVacationLog")
public class VacationLog extends BaseModel {
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
	// ID
	protected Long id;
	// 假期类型
	protected Long vacationType;
	// 调整用户ID
	protected Long userId;
	// 变更类型(1、手工调整，2：请假扣减，3：过期清零，4：周期补充)
	protected Short changeType;
	// 变更值
	protected Long changeValue;
	// 变更值有效期
	protected Date validDate;
	// 调整前值
	protected Long beforeValue;
	// 调整后值
	protected Long afterValue;
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
	public void setChangeValue(Long changeValue){
		this.changeValue = changeValue;
	}
	/**
	 * 返回 smallint
	 * @return
	 */
	public Long getChangeValue() {
		return this.changeValue;
	}
	public void setBeforeValue(Long beforeValue){
		this.beforeValue = beforeValue;
	}
	/**
	 * 返回 调整前值
	 * @return
	 */
	public Long getBeforeValue() {
		return this.beforeValue;
	}
	public void setAfterValue(Long afterValue){
		this.afterValue = afterValue;
	}
	/**
	 * 返回 调整后值
	 * @return
	 */
	public Long getAfterValue() {
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
		.append(this.changeValue, rhs.changeValue)
		.append(this.beforeValue, rhs.beforeValue)
		.append(this.afterValue, rhs.afterValue)
		.append(this.memo, rhs.memo)
		.append(this.updateBy, rhs.updateBy)
		.append(this.updatetime, rhs.updatetime)
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
		.append(this.changeValue) 
		.append(this.beforeValue) 
		.append(this.afterValue) 
		.append(this.memo) 
		.append(this.updateBy) 
		.append(this.updatetime) 
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
		.append("changeValue", this.changeValue) 
		.append("beforeValue", this.beforeValue) 
		.append("afterValue", this.afterValue) 
		.append("memo", this.memo) 
		.append("updateBy", this.updateBy) 
		.append("updatetime", this.updatetime) 
		.toString();
	}
   
  

}