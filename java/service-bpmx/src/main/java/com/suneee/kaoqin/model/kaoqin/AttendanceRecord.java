package com.suneee.kaoqin.model.kaoqin;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;


/**
 * 对象功能:考勤记录表 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:mikel
 * 创建时间:2017-05-03 11:58:37
 */
public class AttendanceRecord extends BaseModel {

	/** 1-考勤系统同步 */
	public static final short FROM_SYNC = 1;
	/** 2-补签卡 */
	public static final short FROM_PATCH = 2;
	
	// ID
	protected Long attendanceId;
	// 用户ID
	protected Long userId;
	// 打卡时间
	protected Date checkTime;
	// 打卡来源（1：考勤系统，2：签卡）
	protected Short checkFrom;
	// 考勤编号
	protected String badgenumber;
	// 姓名
	protected String name;
	// 部门
	protected String department;
	// 身份证号
	protected String ssn;
	// 打卡类型
	protected String checkType;
	// 验证码
	protected Long verifyCode;
	// 传感器编号
	protected String sensorid;
	// 备注信息
	protected String memoinfo;
	// 工作码
	protected String workcode;
	// 序列号
	protected String sn;
	// 扩展
	protected Short userextfmt;
	// 录入时间
	protected Date createtime;
	// 工号
	protected String staffNo;

	public String getStaffNo() {
		return staffNo;
	}
	public void setStaffNo(String staffNo) {
		this.staffNo = staffNo;
	}
	public void setAttendanceId(Long attendanceId){
		this.attendanceId = attendanceId;
	}
	/**
	 * 返回 ID
	 * @return
	 */
	public Long getAttendanceId() {
		return this.attendanceId;
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
	public void setCheckTime(Date checkTime){
		this.checkTime = checkTime;
	}
	/**
	 * 返回 打卡时间
	 * @return
	 */
	public Date getCheckTime() {
		return this.checkTime;
	}
	public void setCheckFrom(Short checkFrom){
		this.checkFrom = checkFrom;
	}
	/**
	 * 返回 打卡来源（1：考勤系统，2：签卡）
	 * @return
	 */
	public Short getCheckFrom() {
		return this.checkFrom;
	}
	public void setBadgenumber(String badgenumber){
		this.badgenumber = badgenumber;
	}
	/**
	 * 返回 考勤编号
	 * @return
	 */
	public String getBadgenumber() {
		return this.badgenumber;
	}
	public void setName(String name){
		this.name = name;
	}
	/**
	 * 返回 姓名
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	public void setDepartment(String department){
		this.department = department;
	}
	/**
	 * 返回 部门
	 * @return
	 */
	public String getDepartment() {
		return this.department;
	}
	public void setSsn(String ssn){
		this.ssn = ssn;
	}
	/**
	 * 返回 身份证号
	 * @return
	 */
	public String getSsn() {
		return this.ssn;
	}
	public void setCheckType(String checkType){
		this.checkType = checkType;
	}
	/**
	 * 返回 打卡类型
	 * @return
	 */
	public String getCheckType() {
		return this.checkType;
	}
	public void setVerifyCode(Long verifyCode){
		this.verifyCode = verifyCode;
	}
	/**
	 * 返回 验证码
	 * @return
	 */
	public Long getVerifyCode() {
		return this.verifyCode;
	}
	public void setSensorid(String sensorid){
		this.sensorid = sensorid;
	}
	/**
	 * 返回 传感器编号
	 * @return
	 */
	public String getSensorid() {
		return this.sensorid;
	}
	public void setMemoinfo(String memoinfo){
		this.memoinfo = memoinfo;
	}
	/**
	 * 返回 备注信息
	 * @return
	 */
	public String getMemoinfo() {
		return this.memoinfo;
	}
	public void setWorkcode(String workcode){
		this.workcode = workcode;
	}
	/**
	 * 返回 工作码
	 * @return
	 */
	public String getWorkcode() {
		return this.workcode;
	}
	public void setSn(String sn){
		this.sn = sn;
	}
	/**
	 * 返回 序列号
	 * @return
	 */
	public String getSn() {
		return this.sn;
	}
	public void setUserextfmt(Short userextfmt){
		this.userextfmt = userextfmt;
	}
	/**
	 * 返回 扩展
	 * @return
	 */
	public Short getUserextfmt() {
		return this.userextfmt;
	}
	public void setCreatetime(Date createtime){
		this.createtime = createtime;
	}
	/**
	 * 返回 录入时间
	 * @return
	 */
	public Date getCreatetime() {
		return this.createtime;
	}
	

   	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof AttendanceRecord)) 
		{
			return false;
		}
		AttendanceRecord rhs = (AttendanceRecord) object;
		return new EqualsBuilder()
		.append(this.attendanceId, rhs.attendanceId)
		.append(this.userId, rhs.userId)
		.append(this.checkTime, rhs.checkTime)
		.append(this.checkFrom, rhs.checkFrom)
		.append(this.badgenumber, rhs.badgenumber)
		.append(this.name, rhs.name)
		.append(this.department, rhs.department)
		.append(this.ssn, rhs.ssn)
		.append(this.checkType, rhs.checkType)
		.append(this.verifyCode, rhs.verifyCode)
		.append(this.sensorid, rhs.sensorid)
		.append(this.memoinfo, rhs.memoinfo)
		.append(this.workcode, rhs.workcode)
		.append(this.sn, rhs.sn)
		.append(this.userextfmt, rhs.userextfmt)
		.append(this.createtime, rhs.createtime)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.attendanceId) 
		.append(this.userId) 
		.append(this.checkTime) 
		.append(this.checkFrom) 
		.append(this.badgenumber) 
		.append(this.name) 
		.append(this.department) 
		.append(this.ssn) 
		.append(this.checkType) 
		.append(this.verifyCode) 
		.append(this.sensorid) 
		.append(this.memoinfo) 
		.append(this.workcode) 
		.append(this.sn) 
		.append(this.userextfmt) 
		.append(this.createtime) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("attendanceId", this.attendanceId) 
		.append("userId", this.userId) 
		.append("checkTime", this.checkTime) 
		.append("checkFrom", this.checkFrom) 
		.append("badgenumber", this.badgenumber) 
		.append("name", this.name) 
		.append("department", this.department) 
		.append("ssn", this.ssn) 
		.append("checkType", this.checkType) 
		.append("verifyCode", this.verifyCode) 
		.append("sensorid", this.sensorid) 
		.append("memoinfo", this.memoinfo) 
		.append("workcode", this.workcode) 
		.append("sn", this.sn) 
		.append("userextfmt", this.userextfmt) 
		.append("createtime", this.createtime) 
		.toString();
	}
   
  

}