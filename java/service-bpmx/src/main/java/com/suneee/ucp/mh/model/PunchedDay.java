package com.suneee.ucp.mh.model;

import com.suneee.ucp.base.model.UcpBaseModel;

public class PunchedDay extends UcpBaseModel {
	
	private String date; //详细日期 yyyy-MM-DD
	private int day;	// 日
	private boolean thisMonth; //是否本月日期
	private boolean working; //是否是工作日，true：为工作日；false：非工作日
	private boolean today;  //是否是今天，true：是今天；false:不是今天
	private boolean abnormal;  //是否异常，true:考勤异常；false:考勤正常
	
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
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
	
	public boolean isWorking() {
		return working;
	}
	public void setWorking(boolean working) {
		this.working = working;
	}
	
	public boolean isToday() {
		return today;
	}
	public void setToday(boolean today) {
		this.today = today;
	}
	
	public boolean isAbnormal() {
		return abnormal;
	}
	public void setAbnormal(boolean abnormal) {
		this.abnormal = abnormal;
	}
	
	
}
