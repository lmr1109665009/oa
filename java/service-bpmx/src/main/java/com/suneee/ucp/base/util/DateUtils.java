/**
 * 
 */
package com.suneee.ucp.base.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期工具类
 * @author xiongxianyun
 *
 */
public class DateUtils {
	/**
	 * 日期格式：yyyy-MM-dd HH:mm:ss
	 */
	public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 日期格式：yyyy-MM-dd
	 */
	public static final String FORMAT_DATE = "yyyy-MM-dd";
	
	/**
	 * 日期格式：yyyyMMdd
	 */
	public static final String FORMAT_DATE_WITHOUT_LINE = "yyyyMMdd";
	
	/**
	 * 日期格式：HH:mm:ss
	 */
	public static final String FORMAT_TIME = "HH:mm:ss";
	
	/**
	 * 日期格式：yyyy-MM
	 */
	public static final String FORMAT_DATE_NODAY = "yyyy-MM";
	
	/**
	 * 日期格式：HH:mm
	 */
	public static final String FORMAT_TIME_NOSECOND = "HH:mm";
	
	/**
	 * 获取指定日期前几个月的日期
	 * 
	 * @param date 指定日期，默认为系统当前时间
	 * @param num 与指定日期相差几个月
	 * @return
	 */
	public static Calendar getMonthBefore(Date date, int num){
		Calendar calendar = Calendar.getInstance();
		if(date == null){
			calendar.setTime(date);
		}
		calendar.add(Calendar.MONTH, num);
		return calendar;
	}
	
	/**
	 * 获取指定日期前几天的日期
	 * @param date 指定日期，默认为系统当前时间
	 * @param num 与指定日期相差的天数
	 * @return
	 */
	public static Calendar getDayOfMonthBefore(Date date, int num){
		Calendar calendar = Calendar.getInstance();
		if(date == null){
			calendar.setTime(date);
		}
		calendar.add(Calendar.DAY_OF_MONTH, num);
		return calendar;
	}
	
	/**
	 * 指定日期开始N天内的日期数组
	 * @param startDate
	 * @param num
	 * @param containsWeekend 是否包含周末
	 * @return
	 */
	public static Date[] getDaysBetween(Date startDate, int num, boolean containsWeekend){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		List<Date> list = new ArrayList<Date>();
		for(int i = 0; i < num; i++){
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			if(containsWeekend || (!containsWeekend && 1 != dayOfWeek && 7 != dayOfWeek)){
				list.add(calendar.getTime());
			}
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		return list.toArray(new Date[list.size()]);
	}
}
