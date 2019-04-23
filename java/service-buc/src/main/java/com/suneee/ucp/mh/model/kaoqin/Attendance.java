/**
 * 
 */
package com.suneee.ucp.mh.model.kaoqin;

import java.util.Date;

/**
 * 考勤实体类
 * @author xiongxianyun
 *
 */
public class Attendance{
	/**
	 * 考勤ID
	 */
	private Long attendanceId;
	
	/**
	 * 考勤编号
	 */
	protected String badgenumber;
	
	/**
	 * 姓名
	 */
	protected String name;
	
	/**
	 * 身份证号
	 */
	protected String ssn;
	
	/**
	 * 工号
	 */
	protected String staffNo;
	
	/**
	 * 打卡时间
	 */
	protected Date checkTime;
	
	/**
	 * 打卡类型
	 */
	protected String checkType;
	
	/**
	 * 
	 */
	protected int verifyCode;
	
	/**
	 * 
	 */
	protected String sensorid;
	
	/**
	 * 
	 */
	protected String memoinfo;
	
	/**
	 * 
	 */
	protected String workcode;
	
	/**
	 * 
	 */
	protected String sn;
	
	/**
	 * 
	 */
	protected int userextfmt;

	/**
	 * @return the attendanceId
	 */
	public Long getAttendanceId() {
		return attendanceId;
	}

	/**
	 * @param attendanceId the attendanceId to set
	 */
	public void setAttendanceId(Long attendanceId) {
		this.attendanceId = attendanceId;
	}

	/**
	 * @return the badgenumber
	 */
	public String getBadgenumber() {
		return badgenumber;
	}

	/**
	 * @param badgenumber the badgenumber to set
	 */
	public void setBadgenumber(String badgenumber) {
		this.badgenumber = badgenumber;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the ssn
	 */
	public String getSsn() {
		return ssn;
	}

	/**
	 * @param ssn the ssn to set
	 */
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	/**
	 * @return the staffNo
	 */
	public String getStaffNo() {
		return staffNo;
	}

	/**
	 * @param staffNo the staffNo to set
	 */
	public void setStaffNo(String staffNo) {
		this.staffNo = staffNo;
	}

	/**
	 * @return the checkTime
	 */
	public Date getCheckTime() {
		return checkTime;
	}

	/**
	 * @param checkTime the checkTime to set
	 */
	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}

	/**
	 * @return the checkType
	 */
	public String getCheckType() {
		return checkType;
	}

	/**
	 * @param checkType the checkType to set
	 */
	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}

	/**
	 * @return the verifyCode
	 */
	public int getVerifyCode() {
		return verifyCode;
	}

	/**
	 * @param verifyCode the verifyCode to set
	 */
	public void setVerifyCode(int verifyCode) {
		this.verifyCode = verifyCode;
	}

	/**
	 * @return the sensorid
	 */
	public String getSensorid() {
		return sensorid;
	}

	/**
	 * @param sensorid the sensorid to set
	 */
	public void setSensorid(String sensorid) {
		this.sensorid = sensorid;
	}

	/**
	 * @return the memoinfo
	 */
	public String getMemoinfo() {
		return memoinfo;
	}

	/**
	 * @param memoinfo the memoinfo to set
	 */
	public void setMemoinfo(String memoinfo) {
		this.memoinfo = memoinfo;
	}

	/**
	 * @return the workcode
	 */
	public String getWorkcode() {
		return workcode;
	}

	/**
	 * @param workcode the workcode to set
	 */
	public void setWorkcode(String workcode) {
		this.workcode = workcode;
	}

	/**
	 * @return the sn
	 */
	public String getSn() {
		return sn;
	}

	/**
	 * @param sn the sn to set
	 */
	public void setSn(String sn) {
		this.sn = sn;
	}

	/**
	 * @return the userextfmt
	 */
	public int getUserextfmt() {
		return userextfmt;
	}

	/**
	 * @param userextfmt the userextfmt to set
	 */
	public void setUserextfmt(int userextfmt) {
		this.userextfmt = userextfmt;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [attendanceId=");
		builder.append(attendanceId);
		builder.append(", badgenumber=");
		builder.append(badgenumber);
		builder.append(", name=");
		builder.append(name);
		builder.append(", ssn=");
		builder.append(ssn);
		builder.append(", staffNo=");
		builder.append(staffNo);
		builder.append(", checkTime=");
		builder.append(checkTime);
		builder.append(", checkType=");
		builder.append(checkType);
		builder.append(", verifyCode=");
		builder.append(verifyCode);
		builder.append(", sensorid=");
		builder.append(sensorid);
		builder.append(", memoinfo=");
		builder.append(memoinfo);
		builder.append(", workcode=");
		builder.append(workcode);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", userextfmt=");
		builder.append(userextfmt);
		builder.append("]");
		return builder.toString();
	}
}
