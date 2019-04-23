package com.suneee.platform.model.ats;

import com.suneee.core.model.BaseModel;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.StringPool;

import java.util.Date;
import java.util.Set;

/**
 * 对象功能:考勤计算 Model对象 开发公司:广州宏天软件有限公司 开发人员:zxh 创建时间:2015-05-31 13:51:08
 */
public class AtsAttenceCalculate extends BaseModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2593296754329148605L;
	// 主键
	protected Long id;
	// 考勤档案
	protected Long fileId;
	// 考勤日期
	protected Date attenceTime;
	// 是否排班
	protected Short isScheduleShift = AtsConstant.YES;
	// 日期类型
	protected Short dateType;
	// 节假日名 如果是节假日
	protected String holidayName;
	// 是否有打卡记录
	protected Short isCardRecord;
	// 班次时间
	protected String shiftTime;
	// 应出勤时数
	protected Double shouldAttenceHours;
	// 实际出勤时数
	protected Double actualAttenceHours;
	// 打卡记录
	protected String cardRecord;
	// 有效打卡记录
	protected String validCardRecord;
	// 旷工次数
	protected Double absentNumber;
	// 旷工小时数
	protected Double absentTime;
	// 旷工记录
	protected String absentRecord;
	// 迟到次数
	protected Double lateNumber;
	// 迟到分钟数
	protected Double lateTime;
	// 迟到记录
	protected String lateRecord;
	// 早退次数
	protected Double leaveNumber;
	// 早退分钟数
	protected Double leaveTime;
	// 早退记录
	protected String leaveRecord;
	// 加班次数
	protected Double otNumber;
	// 加班分钟数
	protected Double otTime;
	// 加班记录
	protected String otRecord;
	// 请假次数
	protected Double holidayNumber;
	// 请假分钟数
	protected Double holidayTime;
	// 请假时间单位
	protected Short holidayUnit;
	// 请假记录
	protected String holidayRecord;
	// 出差次数
	protected Double tripNumber;
	// 出差分钟数
	protected Double tripTime;
	// 出差记录
	protected String tripRecord;

	// 考勤类型
	protected String attenceType;
	// 班次ID
	protected Long shiftId;

	protected String shiftName;
	//异常 : 
	protected Short abnormity=AbnormityType.normal;

	// 非数据库字段
	protected String userName;
	protected Long orgId;
	protected String orgName;
	protected String account;
	protected Long userId;

	protected String shiftTime11;
	protected String shiftTime12;
	protected String shiftTime21;
	protected String shiftTime22;
	protected String shiftTime31;
	protected String shiftTime32;

	protected String absentRecord11;
	protected String absentRecord12;
	protected String absentRecord21;
	protected String absentRecord22;
	protected String absentRecord31;
	protected String absentRecord32;

	protected String unit;

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 返回 主键
	 * 
	 * @return
	 */
	public Long getId() {
		return this.id;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	/**
	 * 返回 考勤档案
	 * 
	 * @return
	 */
	public Long getFileId() {
		return this.fileId;
	}

	public void setAttenceTime(Date attenceTime) {
		this.attenceTime = attenceTime;
	}

	/**
	 * 返回 考勤日期
	 * 
	 * @return
	 */
	public Date getAttenceTime() {
		return this.attenceTime;
	}

	public void setShouldAttenceHours(Double shouldAttenceHours) {
		this.shouldAttenceHours = shouldAttenceHours;
	}

	/**
	 * 返回 应出勤时数
	 * 
	 * @return
	 */
	public Double getShouldAttenceHours() {
		return this.shouldAttenceHours;
	}

	public void setActualAttenceHours(Double actualAttenceHours) {
		this.actualAttenceHours = actualAttenceHours;
	}

	/**
	 * 返回 实际出勤时数
	 * 
	 * @return
	 */
	public Double getActualAttenceHours() {
		return this.actualAttenceHours;
	}

	public void setCardRecord(String cardRecord) {
		this.cardRecord = cardRecord;
	}

	public void setCardRecord(Set<Date> cardRecordSet) {
		StringBuffer sb = new StringBuffer();
		for (Date date : cardRecordSet) {
			sb.append(DateFormatUtil.format(date, StringPool.DATE_FORMAT_TIME) + "|");
		}
		setCardRecord(sb.toString());
	}

	/**
	 * 返回 有效打卡记录
	 * 
	 * @return
	 */
	public String getCardRecord() {
		return this.cardRecord;
	}

	public void setAbsentNumber(Double absentNumber) {
		this.absentNumber = absentNumber;
	}

	/**
	 * 返回 旷工次数
	 * 
	 * @return
	 */
	public Double getAbsentNumber() {
		return this.absentNumber;
	}

	public void setAbsentTime(Double absentTime) {
		this.absentTime = absentTime;
	}

	/**
	 * 返回 旷工小时数
	 * 
	 * @return
	 */
	public Double getAbsentTime() {
		return this.absentTime;
	}

	public void setAbsentRecord(String absentRecord) {
		this.absentRecord = absentRecord;
	}

	/**
	 * 返回 旷工记录
	 * 
	 * @return
	 */
	public String getAbsentRecord() {
		return this.absentRecord;
	}

	public void setLateNumber(Double lateNumber) {
		this.lateNumber = lateNumber;
	}

	/**
	 * 返回 迟到次数
	 * 
	 * @return
	 */
	public Double getLateNumber() {
		return this.lateNumber;
	}

	public void setLateTime(Double lateTime) {
		this.lateTime = lateTime;
	}

	/**
	 * 返回 迟到分钟数
	 * 
	 * @return
	 */
	public Double getLateTime() {
		return this.lateTime;
	}

	public void setLateRecord(String lateRecord) {
		this.lateRecord = lateRecord;
	}

	/**
	 * 返回 迟到记录
	 * 
	 * @return
	 */
	public String getLateRecord() {
		return this.lateRecord;
	}

	public void setLeaveNumber(Double leaveNumber) {
		this.leaveNumber = leaveNumber;
	}

	/**
	 * 返回 早退次数
	 * 
	 * @return
	 */
	public Double getLeaveNumber() {
		return this.leaveNumber;
	}

	public void setLeaveTime(Double leaveTime) {
		this.leaveTime = leaveTime;
	}

	/**
	 * 返回 早退分钟数
	 * 
	 * @return
	 */
	public Double getLeaveTime() {
		return this.leaveTime;
	}

	public void setLeaveRecord(String leaveRecord) {
		this.leaveRecord = leaveRecord;
	}

	/**
	 * 返回 早退记录
	 * 
	 * @return
	 */
	public String getLeaveRecord() {
		return this.leaveRecord;
	}

	public void setOtNumber(Double otNumber) {
		this.otNumber = otNumber;
	}

	/**
	 * 返回 加班次数
	 * 
	 * @return
	 */
	public Double getOtNumber() {
		return this.otNumber;
	}

	public void setOtTime(Double otTime) {
		this.otTime = otTime;
	}

	/**
	 * 返回 加班分钟数
	 * 
	 * @return
	 */
	public Double getOtTime() {
		return this.otTime;
	}

	public void setOtRecord(String otRecord) {
		this.otRecord = otRecord;
	}

	/**
	 * 返回 加班记录
	 * 
	 * @return
	 */
	public String getOtRecord() {
		return this.otRecord;
	}

	public void setHolidayNumber(Double holidayNumber) {
		this.holidayNumber = holidayNumber;
	}

	/**
	 * 返回 请假次数
	 * 
	 * @return
	 */
	public Double getHolidayNumber() {
		return this.holidayNumber;
	}

	public void setHolidayTime(Double holidayTime) {
		this.holidayTime = holidayTime;
	}

	/**
	 * 返回 请假分钟数
	 * 
	 * @return
	 */
	public Double getHolidayTime() {
		return this.holidayTime;
	}

	public void setHolidayUnit(Short holidayUnit) {
		this.holidayUnit = holidayUnit;
	}

	/**
	 * 返回 请假时间单位
	 * 
	 * @return
	 */
	public Short getHolidayUnit() {
		return this.holidayUnit;
	}

	public void setHolidayRecord(String holidayRecord) {
		this.holidayRecord = holidayRecord;
	}

	/**
	 * 返回 请假记录
	 * 
	 * @return
	 */
	public String getHolidayRecord() {
		return this.holidayRecord;
	}

	public void setTripNumber(Double tripNumber) {
		this.tripNumber = tripNumber;
	}

	/**
	 * 返回 出差次数
	 * 
	 * @return
	 */
	public Double getTripNumber() {
		return this.tripNumber;
	}

	public void setTripTime(Double tripTime) {
		this.tripTime = tripTime;
	}

	/**
	 * 返回 出差分钟数
	 * 
	 * @return
	 */
	public Double getTripTime() {
		return this.tripTime;
	}

	public void setTripRecord(String tripRecord) {
		this.tripRecord = tripRecord;
	}

	/**
	 * 返回 出差记录
	 * 
	 * @return
	 */
	public String getTripRecord() {
		return this.tripRecord;
	}

	public Short getIsScheduleShift() {
		return isScheduleShift;
	}

	public void setIsScheduleShift(Short isScheduleShift) {
		this.isScheduleShift = isScheduleShift;
	}

	public Short getDateType() {
		return dateType;
	}

	public void setDateType(Short dateType) {
		this.dateType = dateType;
	}

	public String getHolidayName() {
		return holidayName;
	}

	public void setHolidayName(String holidayName) {
		this.holidayName = holidayName;
	}

	public Short getIsCardRecord() {
		return isCardRecord;
	}

	public void setIsCardRecord(Short isCardRecord) {
		this.isCardRecord = isCardRecord;
	}

	public String getShiftTime() {
		return shiftTime;
	}

	public void setShiftTime(String shiftTime) {
		this.shiftTime = shiftTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getShiftTime11() {
		return shiftTime11;
	}

	public void setShiftTime11(String shiftTime11) {
		this.shiftTime11 = shiftTime11;
	}

	public String getShiftTime12() {
		return shiftTime12;
	}

	public void setShiftTime12(String shiftTime12) {
		this.shiftTime12 = shiftTime12;
	}

	public String getShiftTime21() {
		return shiftTime21;
	}

	public void setShiftTime21(String shiftTime21) {
		this.shiftTime21 = shiftTime21;
	}

	public String getShiftTime22() {
		return shiftTime22;
	}

	public void setShiftTime22(String shiftTime22) {
		this.shiftTime22 = shiftTime22;
	}

	public String getShiftTime31() {
		return shiftTime31;
	}

	public void setShiftTime31(String shiftTime31) {
		this.shiftTime31 = shiftTime31;
	}

	public String getShiftTime32() {
		return shiftTime32;
	}

	public void setShiftTime32(String shiftTime32) {
		this.shiftTime32 = shiftTime32;
	}

	public String getAbsentRecord11() {
		return absentRecord11;
	}

	public void setAbsentRecord11(String absentRecord11) {
		this.absentRecord11 = absentRecord11;
	}

	public String getAbsentRecord12() {
		return absentRecord12;
	}

	public void setAbsentRecord12(String absentRecord12) {
		this.absentRecord12 = absentRecord12;
	}

	public String getAbsentRecord21() {
		return absentRecord21;
	}

	public void setAbsentRecord21(String absentRecord21) {
		this.absentRecord21 = absentRecord21;
	}

	public String getAbsentRecord22() {
		return absentRecord22;
	}

	public void setAbsentRecord22(String absentRecord22) {
		this.absentRecord22 = absentRecord22;
	}

	public String getAbsentRecord31() {
		return absentRecord31;
	}

	public void setAbsentRecord31(String absentRecord31) {
		this.absentRecord31 = absentRecord31;
	}

	public String getAbsentRecord32() {
		return absentRecord32;
	}

	public void setAbsentRecord32(String absentRecord32) {
		this.absentRecord32 = absentRecord32;
	}

	public String getValidCardRecord() {
		return validCardRecord;
	}

	public void setValidCardRecord(String validCardRecord) {
		this.validCardRecord = validCardRecord;
	}

	public String getAttenceType() {
		return attenceType;
	}

	public void setAttenceType(String attenceType) {
		this.attenceType = attenceType;
	}

	public Long getShiftId() {
		return shiftId;
	}

	public void setShiftId(Long shiftId) {
		this.shiftId = shiftId;
	}

	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}

	/**
	 * abnormity
	 * @return  the abnormity
	 * @since   1.0.0
	 */
	
	public Short getAbnormity() {
		return abnormity;
	}

	/**
	 * @param abnormity the abnormity to set
	 */
	public void setAbnormity(Short abnormity) {
		this.abnormity = abnormity;
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof AtsAttenceCalculate)) {
			return false;
		}
		AtsAttenceCalculate rhs = (AtsAttenceCalculate) object;
		return rhs.getId() == this.getId();
	}

	//异常类型的内部状态类
	public static class AbnormityType {
		/**
		 * 正常 0
		 */
		public static Short normal = 0;
		/**
		 * 异常 -1
		 */
		public static Short abnormity = -1;
	}
}