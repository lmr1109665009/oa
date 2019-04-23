package com.suneee.kaoqin.model.kaoqin;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 对象功能:考勤结果表 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:mikel
 * 创建时间:2017-05-04 10:48:13
 */
public class AttendanceResult extends BaseModel {
	/** 1-上班 */
	public static final short TYPE_UP = 1;
	/** 2-下班 */
	public static final short TYPE_DWON = 2;
	/** 1-工作日 */
	public static final short TYPE_WORK = 1;
	/** 2-加班 */
	public static final short TYPE_OVERTIME = 2;
	/** 3-请假 */
	public static final short TYPE_LEAVE = 3;
	/** 4-出差 */
	public static final short TYPE_BUSINESS = 4;
	/** 5-外出 */
	public static final short TYPE_OUT = 5;
	/** 6-销假*/
	public static final short TYPE_BACK = 6;
	/** 0-正常 */
	public static final short RESULT_NORMAL = 0;
	/** 1-迟到 */
	public static final short RESULT_LATE = 1;
	/** 2-早退 */
	public static final short RESULT_EARLY = 2;
	/** 3-未打卡 */
	public static final short RESULT_NONE = 3;
	// ID
	protected Long resultId;
	// 用户ID
	protected Long userId;
	// 班次ID
	protected Long shiftId;
	// 排序
	protected Short seqKey;
	// 考勤日期
	protected Date attendanceDate;
	// 标准打卡时间
	protected Date standardTime;
	// 实际打卡时间
	protected Date realTime;
	// 关联打卡记录ID
	protected Long recordId;
	// 流程表单ID（签卡、请假、出差，外出、加班）
	protected Long businessId;
	// 结果类型 1：工作日，2：加班，3：请假，4：出差，5：外出
	protected Short resultType;
	// 打卡类型（1：上班，2：下班）
	protected Short checkType;
	// 打卡结果（0，正常，1：迟到，2：早退，3：未打卡）
	protected Short checkResult;
	// 异常时长（单位分）
	protected Long overTime;

	// 统计展示属性
	private String account;
	private String staffNo;
	private String fullname;
	private String shiftName;
	private Long orgId;
	private String orgName;
	private String checkTime;
	private Integer times;
	private List<ViewModel> checkTimes = new ArrayList<ViewModel>();
	private Integer late;
	private Integer early;
	private Integer unup;
	private Integer undown;
	
	public Long getBusinessId() {
		return businessId;
	}
	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public Integer getLate() {
		return late;
	}
	public void setLate(Integer late) {
		this.late = late;
	}
	public Integer getEarly() {
		return early;
	}
	public void setEarly(Integer early) {
		this.early = early;
	}
	public Integer getUnup() {
		return unup;
	}
	public void setUnup(Integer unup) {
		this.unup = unup;
	}
	public Integer getUndown() {
		return undown;
	}
	public void setUndown(Integer undown) {
		this.undown = undown;
	}
	public Integer getTimes() {
		return times;
	}
	public void setTimes(Integer times) {
		this.times = times;
	}
	public List<ViewModel> getCheckTimes() {
		return checkTimes;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getStaffNo() {
		return staffNo;
	}
	public void setStaffNo(String staffNo) {
		this.staffNo = staffNo;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getShiftName() {
		return shiftName;
	}
	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}
	public String getCheckTime() {
		return checkTime;
	}
	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}
	public void setResultId(Long resultId){
		this.resultId = resultId;
	}
	/**
	 * 返回 ID
	 * @return
	 */
	public Long getResultId() {
		return this.resultId;
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
	public void setShiftId(Long shiftId){
		this.shiftId = shiftId;
	}
	/**
	 * 返回 班次ID
	 * @return
	 */
	public Long getShiftId() {
		return this.shiftId;
	}
	public void setSeqKey(Short seqKey){
		this.seqKey = seqKey;
	}
	/**
	 * 返回 排序
	 * @return
	 */
	public Short getSeqKey() {
		return this.seqKey;
	}
	public void setAttendanceDate(Date attendanceDate){
		this.attendanceDate = attendanceDate;
	}
	/**
	 * 返回 考勤日期
	 * @return
	 */
	public Date getAttendanceDate() {
		return this.attendanceDate;
	}
	public void setStandardTime(Date standardTime){
		this.standardTime = standardTime;
	}
	/**
	 * 返回 标准打卡时间
	 * @return
	 */
	public Date getStandardTime() {
		return this.standardTime;
	}
	public Date getRealTime() {
		return realTime;
	}
	public void setRealTime(Date realTime) {
		this.realTime = realTime;
	}
	public void setRecordId(Long recordId){
		this.recordId = recordId;
	}
	/**
	 * 返回 打卡记录ID
	 * @return
	 */
	public Long getRecordId() {
		return this.recordId;
	}
	public Short getResultType() {
		return resultType;
	}
	public void setResultType(Short resultType) {
		this.resultType = resultType;
	}
	public void setCheckType(Short checkType){
		this.checkType = checkType;
	}
	/**
	 * 返回 打卡类型（1：上班，2：下班）
	 * @return
	 */
	public Short getCheckType() {
		return this.checkType;
	}
	public void setCheckResult(Short checkResult){
		this.checkResult = checkResult;
	}
	/**
	 * 返回 打卡结果（0，正常，1：迟到，2：早退，3：未打卡）
	 * @return
	 */
	public Short getCheckResult() {
		return this.checkResult;
	}

   	public Long getOverTime() {
		return overTime;
	}
	public void setOverTime(Long overTime) {
		this.overTime = overTime;
	}
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof AttendanceResult)) 
		{
			return false;
		}
		AttendanceResult rhs = (AttendanceResult) object;
		return new EqualsBuilder()
		.append(this.resultId, rhs.resultId)
		.append(this.userId, rhs.userId)
		.append(this.shiftId, rhs.shiftId)
		.append(this.seqKey, rhs.seqKey)
		.append(this.attendanceDate, rhs.attendanceDate)
		.append(this.realTime, rhs.realTime)
		.append(this.standardTime, rhs.standardTime)
		.append(this.recordId, rhs.recordId)
		.append(this.resultType, rhs.resultType)
		.append(this.checkType, rhs.checkType)
		.append(this.checkResult, rhs.checkResult)
		.append(this.overTime, rhs.overTime)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.resultId) 
		.append(this.userId) 
		.append(this.shiftId) 
		.append(this.seqKey) 
		.append(this.attendanceDate) 
		.append(this.realTime)
		.append(this.standardTime) 
		.append(this.recordId) 
		.append(this.resultType)
		.append(this.checkType) 
		.append(this.checkResult) 
		.append(this.overTime) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("resultId", this.resultId) 
		.append("userId", this.userId) 
		.append("shiftId", this.shiftId) 
		.append("seqKey", this.seqKey) 
		.append("attendanceDate", this.attendanceDate) 
		.append("realTime", this.realTime) 
		.append("standardTime", this.standardTime) 
		.append("recordId", this.recordId) 
		.append("resultType", this.resultType)
		.append("checkType", this.checkType) 
		.append("checkResult", this.checkResult) 
		.append("overTime", this.overTime) 
		.toString();
	}
   
  

}