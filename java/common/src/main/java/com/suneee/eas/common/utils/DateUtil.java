/**
 * @Title: DateUtil.java 
 * @Package com.suneee.eas.common.utils 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @ClassName: DateUtil 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-07-31 16:44:28 
 *
 */
public class DateUtil {
	public static final String FORMAT_DATE = "yyyy-MM-dd";
	public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_DATETIME_WITH_MILLIS = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String FORMAT_DATETIME_WITHOUT_SECONDS = "yyyy-MM-dd HH:mm";
	public static final String FORMAT_TIME = "HH:mm:ss";
	public static final String FORMAT_TIME_WITHOUT_SECONDS = "HH:mm";
	public static final String FORMAT_DATE_CST = "EEE MMM dd HH:mm:ss 'CST' yyyy";

	/** 
	 * 将日期字符串转换成日期，默认格式为yyyy-MM-dd HH:mm:ss
	 * @param date
	 * @return
	 */
	public static Date getDate(String date){
		return getDate(date, FORMAT_DATETIME);
	}
	
	/** 
	 * 将日期字符串转换成指定格式的日期
	 * @param date 日期字符串
	 * @param format 目标日期格式
	 * @return
	 */
	public static Date getDate(String date, String format){
		try {
			return DateUtils.parseDate(date, format);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 取得多少天后的数据。
	 *
	 * @param days
	 * @return
	 */
	public static long getNextDays(int days) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, days);
		String str = String.valueOf(cal.getTimeInMillis());
		return Long.parseLong((str.substring(0, str.length() - 3) + "000"));
	}

	/**
	 * 取得下一天。
	 *
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date getNextDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	/**
	 * 格式化日期
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatDate(Date date, String format){
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * 时区字符串时间转Date
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static Date getStringToDate(String date) throws Exception{
		return getStringToDate(date, null);
	}

	/**
	 * 时区字符串时间转Date
	 * @param date
	 * @param format
	 * @return
	 * @throws Exception
	 */
	public static Date getStringToDate(String date, String format) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_CST, Locale.US);
		if(StringUtils.isNotEmpty(format)){
			sdf = new SimpleDateFormat(format);
		}
		return sdf.parse(date);
	}
}
