package com.suneee.kaoqin.model.kaoqin;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.ibatis.type.Alias;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 对象功能:假期结余 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 11:32:12
 */
@Alias("kVacationRemain")
public class VacationRemain extends BaseModel {
	// ID
	protected Long id;
	// 用户Id
	protected Long userId;
	// 假期类型
	protected Long vacationType;
	// 假期结余
	protected Long remained = 0L;
	// 结余有效期
	protected Date validDate;
	// 基本单位（1：天，2：小时）
	protected Short baseUnit;
	// 状态（0：正常，-1：禁用）
	protected Short status = 0;
	// 更新时间
	protected Date updatetime;
	
	//备注 
	protected String memo;
	// 调整数
	protected Long changeValue;
	private String staffNo;
	private String userAccount;
	private String userName;
	private String remainedTime;
	private List<String> remainedTimes = new ArrayList<String>();
	
	private String remainedName;
	private String vacationName;
	private Long used = 0L;

	public String getVacationName() {
		return vacationName;
	}
	public void setVacationName(String vacationName) {
		this.vacationName = vacationName;
	}
	public Long getUsed() {
		return used;
	}
	public void setUsed(Long used) {
		this.used = used;
	}
	public String getRemainedName() {
		return remainedName;
	}
	public void setRemainedName(String remainedName) {
		this.remainedName = remainedName;
	}
	public List<String> getRemainedTimes() {
		return remainedTimes;
	}
	public void setRemainedTimes(List<String> remainedTimes) {
		this.remainedTimes = remainedTimes;
	}
	public String getRemainedTime() {
		return remainedTime;
	}
	public void setRemainedTime(String remainedTime) {
		this.remainedTime = remainedTime;
	}
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
	public Long getChangeValue() {
		return changeValue;
	}
	public void setChangeValue(Long changeValue) {
		this.changeValue = changeValue;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
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
	public void setRemained(Long remained){
		this.remained = remained;
	}
	/**
	 * 返回 假期结余
	 * @return
	 */
	public Long getRemained() {
		return this.remained;
	}
	public void setValidDate(Date validDate){
		this.validDate = validDate;
	}
	/**
	 * 返回 结余有效期
	 * @return
	 */
	public Date getValidDate() {
		return this.validDate;
	}
	public void setBaseUnit(Short baseUnit){
		this.baseUnit = baseUnit;
	}
	/**
	 * 返回 基本单位（1：小时，2：天）
	 * @return
	 */
	public Short getBaseUnit() {
		return this.baseUnit;
	}
	public void setStatus(Short status){
		this.status = status;
	}
	/**
	 * 返回 状态（0：正常，-1：禁用）
	 * @return
	 */
	public Short getStatus() {
		return this.status;
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
		if (!(object instanceof VacationRemain)) 
		{
			return false;
		}
		VacationRemain rhs = (VacationRemain) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.userId, rhs.userId)
		.append(this.vacationType, rhs.vacationType)
		.append(this.remained, rhs.remained)
		.append(this.validDate, rhs.validDate)
		.append(this.baseUnit, rhs.baseUnit)
		.append(this.status, rhs.status)
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
		.append(this.userId) 
		.append(this.vacationType) 
		.append(this.remained) 
		.append(this.validDate) 
		.append(this.baseUnit) 
		.append(this.status) 
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
		.append("userId", this.userId) 
		.append("vacationType", this.vacationType) 
		.append("remained", this.remained) 
		.append("validDate", this.validDate) 
		.append("baseUnit", this.baseUnit) 
		.append("status", this.status) 
		.append("updatetime", this.updatetime) 
		.toString();
	}
   
  

}