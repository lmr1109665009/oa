/**
 * @Title: CalendarUtil.java 
 * @Package com.suneee.eas.common.utils 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.platform.calendar.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.suneee.eas.common.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;


/**
 * @ClassName: CalendarUtil 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-07-31 16:27:15 
 *
 */
public class CalendarUtil {
	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		System.out.println(cal.get(Calendar.HOUR));
		System.out.println(cal.get(Calendar.HOUR_OF_DAY));
	}
	
	/** 
	 * 获取指定日期的日历对象
	 * 
	 * @param date 指定日期
	 * @return
	 */
	public static Calendar getCalendar(Date date){
		if(date == null){
			throw new IllegalArgumentException("date must not be null.");
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	
	/** 获取指定日期当天的开始时间
	 * @param date
	 * @return
	 */
	public static Date getDateBeginTime(Date date){
		Calendar calendar = getCalendar(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	/** 获取指定日期当天的结束时间
	 * @param date
	 * @return
	 */
	public static Date getDateEndTime(Date date){
		Calendar calendar = getCalendar(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}
	/** 
	 * 获取指定时间的日期
	 * @param date 日期
	 * @param time 时间，格式为：HH:mm或者HH:mm:ss
	 * @return
	 */
	public static Date getDate(Date date, String time){
		if(time == null){
			throw new IllegalArgumentException("time must not be null.");
		}
		String[] timeArr = time.split(":");
		if(timeArr.length < 2){
			throw new IllegalArgumentException("the format of startTime is error.");
		}
		int hour = Integer.parseInt(timeArr[0]);
		int minute = Integer.parseInt(timeArr[1]);
		int second = 0;
		if(timeArr.length == 3){
			second = Integer.parseInt(timeArr[2]);
		}
		return getDate(date, hour, minute, second);
	}
	
	
	/** 
	 *  获取指定时、分、秒的日期，默认为0毫秒
	 * @param date
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static Date getDate(Date date, int hour, int minute, int second){
		Calendar calendar = getCalendar(date);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	/** 
	 * 获取两个日期之间相隔的月份数
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ParseException 
	 */
	public static int getMonthBetween(String startDate, String endDate) throws ParseException{
		Date start = DateUtil.getDate(startDate, DateUtil.FORMAT_DATE);
		Date end = DateUtil.getDate(endDate, DateUtil.FORMAT_DATE);
		return getMonthBetween(getCalendar(start), getCalendar(end));
	}
	
	/** 
	 * 获取两个日期之间相隔的月份数
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getMonthBetween(Date startDate, Date endDate){
		return getMonthBetween(getCalendar(startDate), getCalendar(endDate));
	}
	
	/** 
	 * 获取两个日期之间相隔的月份数
	 * @param startCal
	 * @param endCal
	 * @return
	 */
	public static int getMonthBetween(Calendar startCal, Calendar endCal){
		if(startCal == null || endCal == null){
			throw new IllegalArgumentException("startCal and endCal must not be null");
		}
		
		// 获取开始年份和结束年份
		int yearStart = startCal.get(Calendar.YEAR);
		int yearEnd = endCal.get(Calendar.YEAR);
		// 获取开始月份和结束月份
		int monthStart = startCal.get(Calendar.MONTH);
		int monthEnd = endCal.get(Calendar.MONTH);
		// 两个日期之间相隔的月份数
		return (yearEnd - yearStart) * 12 + (monthEnd - monthStart);
	}
	/** 
	 * 获取指定时间段内指定日的日期
	 * 
	 * @param startDate 开始时间
	 * @param endDate 结束时间
	 * @param dates 指定日，多个数值之间使用英文逗号分隔
	 * @return
	 * @throws ParseException
	 */
	public static List<Date> getDateOfMonth(String startDate, String endDate, String dates) throws ParseException{
		Date start = DateUtil.getDate(startDate, DateUtil.FORMAT_DATE);
		Date end = DateUtil.getDate(endDate, DateUtil.FORMAT_DATE);
		return getDateOfMonth(start, end, dates);
	}
	
	/** 
	  * 获取指定时间段内指定日的日期
	 * 
	 * @param startDate 开始时间
	 * @param endDate 结束时间
	 * @param dates 指定日，多个数值之间使用英文逗号分隔
	 * @return
	 */
	public static List<Date> getDateOfMonth(Date startDate, Date endDate, String dates){
		if(startDate == null || endDate == null || dates == null){
			throw new IllegalArgumentException("startDate, endDate and dates must not be null");
		}
		// 将日期对象转换为日历对象
		Calendar calStart = getCalendar(startDate);
		Calendar calEnd = getCalendar(endDate);
		
		// 将日期字符串转换为数字数组
		String[] dateArr = dates.split(",");
		List<Integer> dateList = new ArrayList<Integer>();
		for(int i = 0; i < dateArr.length; i++){
			if(StringUtils.isNotBlank(dateArr[i])){
				dateList.add(Integer.valueOf(dateArr[i]));
			}
		}
		
		// 两个日期之间相隔的月份数
		int monthInterval = getMonthBetween(calStart, calEnd);
		
		Calendar calTemp = getCalendar(startDate);
		List<Date> list = new ArrayList<Date>();
		for(; monthInterval >= 0; monthInterval--){
			int actualMax = calTemp.getActualMaximum(Calendar.DAY_OF_MONTH);
			for(Integer date : dateList){
				// 当月的最大日期大于等于指定日时才进行下面的逻辑，否则进入一下循环
				if(actualMax >= date){
					calTemp.set(Calendar.DAY_OF_MONTH, date);
					if(calTemp.getTimeInMillis() >= calStart.getTimeInMillis() 
							&& calTemp.getTimeInMillis() <= calEnd.getTimeInMillis()){
						list.add(calTemp.getTime());
					}
				}
			}
			// 月份加一，进行下一月份的数据处理
			calTemp.add(Calendar.MONTH, 1);
		}
		
		return list;
	}
	
	/** 
	 * 获取指定时间段内指定星期的日期集合
	 * 
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @param weeks 指定星期字符串，多个数值之间使用英文逗号分隔
	 * @return
	 * @throws ParseException
	 */
	public static List<Date> getDateOfWeek(String startDate, String endDate, String weeks) throws ParseException{
		Date start = DateUtil.getDate(startDate, DateUtil.FORMAT_DATE);
		Date end = DateUtil.getDate(endDate, DateUtil.FORMAT_DATE);
		return getDateOfWeek(start, end, weeks);
	}

	/** 
	 * 获取指定时间段内指定星期的日期集合
	 * 
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @param weeks 指定星期字符串，多个数值之间使用英文逗号分隔
	 * @return
	 */
	public static List<Date> getDateOfWeek(Date startDate, Date endDate, String weeks){
		if(startDate == null || endDate == null || weeks == null){
			throw new IllegalArgumentException("startDate, endDate and weeks must not be null");
		}
		// 将日期对象转换为日历对象
		Calendar calStart = getCalendar(startDate);
		Calendar calEnd = getCalendar(endDate);
		
		List<Date> list = new ArrayList<Date>();
		// 当结束时间大于等于开始时间时进入循环
		while(calEnd.getTimeInMillis() >= calStart.getTimeInMillis()){
			// 获取当前开始时间的星期值，并判断是否在指定的星期数值之内
			int weekday = calStart.get(Calendar.DAY_OF_WEEK);
			if(weeks.indexOf(String.valueOf(weekday)) != -1){
				list.add(calStart.getTime());
			}
			// 当前开始时间增加一天，进入下一天的日期处理
			calStart.add(Calendar.DATE, 1);
		}
		
		return list;
	}
}
