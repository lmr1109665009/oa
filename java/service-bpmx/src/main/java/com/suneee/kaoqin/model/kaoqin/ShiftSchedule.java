package com.suneee.kaoqin.model.kaoqin;

import java.util.Date;

/**
 * 班次排班信息，用于页面日历展示
 * @author mikel
 *
 */
public class ShiftSchedule {

	private int day;// 日期
	private boolean thisMonth;// 是否本月日期
	private String desc;// 日期描述
	private boolean working;// 是否上班
	private Long dayScheduleId;// 单日排班ID
	private Long holidayId;// 节假日ID
	
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public boolean isThisMonth() {
		return thisMonth;
	}
	public void setThisMonth(boolean thisMonth) {
		this.thisMonth = thisMonth;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public boolean isWorking() {
		return working;
	}
	public void setWorking(boolean working) {
		this.working = working;
	}
	public Long getDayScheduleId() {
		return dayScheduleId;
	}
	public void setDayScheduleId(Long dayScheduleId) {
		this.dayScheduleId = dayScheduleId;
	}
	public Long getHolidayId() {
		return holidayId;
	}
	public void setHolidayId(Long holidayId) {
		this.holidayId = holidayId;
	}
	
}
