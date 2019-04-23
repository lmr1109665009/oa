package com.suneee.ucp.mh.model.attendance;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.ibatis.type.Alias;

import java.util.Date;
import java.util.List;


/**
 * 对象功能:考勤班次表 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:mikel
 * 创建时间:2017-05-02 10:03:50
 */
@Alias("aAttendanceShift")
public class AttendanceShift extends BaseModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3711961097040643655L;
	/** -1-禁用 */
	public static final short STATUS_FORBIDDEN = -1;
	/** 0-正常 */
	public static final short STATUS_NORMAL = 0;
	// 班次ID
	protected Long shiftId;
	// 班次编码
	protected String shiftCode;
	// 班次名称
	protected String shiftName;
	// 工作时长（小时）
	protected Double workHour;
	// 打卡有效范围（分钟）
	protected Long validScope = 0L;
	// 浮动时间（分钟）
	protected Long floatTime = 0L;
	// 状态（0：正常，-1：禁用）
	protected Short status;
	// 创建人ID
	protected Long createby;
	// 创建时间
	protected Date createtime;
	
	// 用于保存表单
	// 休息日列表
	protected String reskDays;
	// 时间段列表
	protected String timeList;
	// 休息时间段列表
	protected String restTimes;
	
	//班次关联人员
	protected String userNames;
	
	// 用于编辑回显
	//休息日设置
	protected short reskWeeks[] = new short[8];
	// 时间段列表
	protected List<ShiftTime> times;
	// 班次休息时间段列表
	protected List<ShiftTime> restTimeList;

	public List<ShiftTime> getTimes() {
		return times;
	}
	public String getUserNames() {
		return userNames;
	}
	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}
	public void setTimes(List<ShiftTime> times) {
		this.times = times;
	}
	public short[] getReskWeeks() {
		return reskWeeks;
	}
	public String getReskDays() {
		return reskDays;
	}
	public void setReskDays(String reskDays) {
		this.reskDays = reskDays;
	}
	public String getTimeList() {
		return timeList;
	}
	public void setTimeList(String timeList) {
		this.timeList = timeList;
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
	public void setShiftCode(String shiftCode){
		this.shiftCode = shiftCode;
	}
	/**
	 * 返回 班次编码
	 * @return
	 */
	public String getShiftCode() {
		return this.shiftCode;
	}
	public void setShiftName(String shiftName){
		this.shiftName = shiftName;
	}
	/**
	 * 返回 班次名称
	 * @return
	 */
	public String getShiftName() {
		return this.shiftName;
	}
	public void setWorkHour(Double workHour){
		this.workHour = workHour;
	}
	/**
	 * 返回 工作时长（小时）
	 * @return
	 */
	public Double getWorkHour() {
		return this.workHour;
	}
	public void setValidScope(Long validScope){
		this.validScope = validScope;
	}
	/**
	 * 返回 打卡有效范围（分钟）
	 * @return
	 */
	public Long getValidScope() {
		return this.validScope;
	}
	public void setFloatTime(Long floatTime){
		this.floatTime = floatTime;
	}
	/**
	 * 返回 浮动时间（分钟）
	 * @return
	 */
	public Long getFloatTime() {
		return this.floatTime;
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
	public void setCreateby(Long createby){
		this.createby = createby;
	}
	/**
	 * 返回 创建人ID
	 * @return
	 */
	public Long getCreateby() {
		return this.createby;
	}
	public void setCreatetime(Date createtime){
		this.createtime = createtime;
	}
	/**
	 * 返回 创建时间
	 * @return
	 */
	public Date getCreatetime() {
		return this.createtime;
	}
	
	/**
	 * @return the restTimes
	 */
	public String getRestTimes() {
		return restTimes;
	}
	/**
	 * @param restTimes the restTimes to set
	 */
	public void setRestTimes(String restTimes) {
		this.restTimes = restTimes;
	}
	/**
	 * @return the restTimeList
	 */
	public List<ShiftTime> getRestTimeList() {
		return restTimeList;
	}
	/**
	 * @param restTimeList the restTimeList to set
	 */
	public void setRestTimeList(List<ShiftTime> restTimeList) {
		this.restTimeList = restTimeList;
	}
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof AttendanceShift)) 
		{
			return false;
		}
		AttendanceShift rhs = (AttendanceShift) object;
		return new EqualsBuilder()
		.append(this.shiftId, rhs.shiftId)
		.append(this.shiftCode, rhs.shiftCode)
		.append(this.shiftName, rhs.shiftName)
		.append(this.workHour, rhs.workHour)
		.append(this.validScope, rhs.validScope)
		.append(this.floatTime, rhs.floatTime)
		.append(this.status, rhs.status)
		.append(this.createby, rhs.createby)
		.append(this.createtime, rhs.createtime)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.shiftId) 
		.append(this.shiftCode) 
		.append(this.shiftName) 
		.append(this.workHour) 
		.append(this.validScope) 
		.append(this.floatTime) 
		.append(this.status) 
		.append(this.createby) 
		.append(this.createtime) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("shiftId", this.shiftId) 
		.append("shiftCode", this.shiftCode) 
		.append("shiftName", this.shiftName) 
		.append("workHour", this.workHour) 
		.append("validScope", this.validScope) 
		.append("floatTime", this.floatTime) 
		.append("status", this.status) 
		.append("createby", this.createby) 
		.append("createtime", this.createtime) 
		.toString();
	}

}