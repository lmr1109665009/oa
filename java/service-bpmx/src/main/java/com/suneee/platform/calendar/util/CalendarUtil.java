package com.suneee.platform.calendar.util;

import com.suneee.Constants;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.DateUtil;
import com.suneee.platform.calendar.model.CalendarData;
import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日历帮助类
 * 
 * @author zxh
 * 
 */
public class CalendarUtil {

	/**
	 * 
	 * 设置日历的数据
	 * 
	 * @param calendarData
	 * @param mode
	 * @param nowDate
	 * @param startDate
	 * @param endDate
	 *            void
	 * @since 1.0.0
	 */
	public static void setCalendarData(CalendarData calendarData, String mode,
									   Date nowDate, Date startDate, Date endDate) {
		Date startTime = (startDate == null ? nowDate : startDate);
		Date endTime = (endDate == null && "month".equals(mode) ? startTime
				: endDate);

		String sTime = DateUtil.formatEnDate(startTime);
		String eTime = endTime == null ? "" : DateUtil.formatEnDate(endTime);

		String eTime0 = "";
		if ("month".equals(mode)
				&& sTime.substring(0, 10).equals(eTime.substring(0, 10))) {
			String[] dateArr = sTime.substring(0, 10).split("/");
			eTime0 = DateUtil.addOneDay(dateArr[2] + "-" + dateArr[0] + "-"
					+ dateArr[1])
					+ " 00:00:00 AM";
		}

		if (!"month".equals(mode)) {
			String[] dateArr = sTime.substring(0, 10).split("/");
			eTime0 = DateUtil.addOneHour(dateArr[2] + "-" + dateArr[0] + "-"
					+ dateArr[1] + sTime.substring(10, sTime.length()));
		}

		if ("month".equals(mode))
			calendarData.setStartTime(sTime.substring(0, 10) + " 00:00:00 AM");
		else
			calendarData.setStartTime(sTime);

		if (!eTime0.equals(""))
			calendarData.setEndTime(eTime0);
		else
			calendarData.setEndTime(eTime);
	}

	public static Map<String, Object> getCalendarMap(Map<String, Object> map) {
		String mode = (String) map.get("mode");
		String sDate = (String) map.get("startDate");
		String eDate = (String) map.get("endDate");

		Date startDate = null;
		Date endDate = null;

		if ("month".equals(mode)) {
			try {
				Date reqDate = DateUtils.parseDate(sDate,
						new String[] { "MM/dd/yyyy" });
				Calendar cal = Calendar.getInstance();
				cal.setTime(reqDate);
				startDate = DateUtil.setStartDay(cal).getTime();
				reqDate = DateUtils.parseDate(eDate,
						new String[] { "MM/dd/yyyy" });
				cal.setTime(reqDate);
				endDate = DateUtil.setEndDay(cal).getTime();

			} catch (Exception ex) {
				// logger.error(ex.getMessage());
			}

		} else if ("day".equals(mode)) {
			try {
				Date reqDay = DateUtils.parseDate(sDate,
						new String[] { "MM/dd/yyyy" });

				Calendar cal = Calendar.getInstance();
				cal.setTime(reqDay);

				// 开始日期为本月1号 00时00分00秒
				startDate = DateUtil.setStartDay(cal).getTime();

				cal.add(Calendar.MONTH, 1);
				cal.add(Calendar.DAY_OF_MONTH, -1);

				// 结束日期为本月最后一天的23时59分59秒
				endDate = DateUtil.setEndDay(cal).getTime();

			} catch (Exception ex) {
				// logger.error(ex.getMessage());
			}

		} else if ("week".equals(mode)) {

			try {
				Date reqStartWeek = DateUtils.parseDate(sDate,
						new String[] { "MM/dd/yyyy" });
				Date reqEndWeek = DateUtils.parseDate(eDate,
						new String[] { "MM/dd/yyyy" });
				Calendar cal = Calendar.getInstance();

				cal.setTime(reqStartWeek);

				startDate = DateUtil.setStartDay(cal).getTime();
				cal.setTime(reqEndWeek);

				endDate = DateUtil.setEndDay(cal).getTime();

			} catch (Exception ex) {
				// logger.error(ex.getMessage());
			}

		} else if ("workweek".equals(mode)) {
			try {
				Date reqStartWeek = DateUtils.parseDate(sDate,
						new String[] { "MM/dd/yyyy" });
				Date reqEndWeek = DateUtils.parseDate(eDate,
						new String[] { "MM/dd/yyyy" });
				Calendar cal = Calendar.getInstance();

				cal.setTime(reqStartWeek);

				startDate = DateUtil.setStartDay(cal).getTime();
				cal.setTime(reqEndWeek);

				endDate = DateUtil.setEndDay(cal).getTime();

			} catch (Exception ex) {
				// logger.error(ex.getMessage());
			}
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", ContextUtil.getCurrentUserId());
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		return params;
	}

	/**
	 * 获取某年月的日期范围
	 * @param year
	 * @param month
	 * @return
	 */
	public static Calendar[] getMonthScope(int year, int month) {
		Calendar start = Calendar.getInstance();
		if (year == 0) {
			year = start.get(Calendar.YEAR);
		}
		if (month == 0) {
			month = start.get(Calendar.MONTH) + 1;
		}
		
		// 计算日历展示的第一天
		start.set(Calendar.YEAR, year);
		start.set(Calendar.MONTH, month-1);
		start.set(Calendar.DAY_OF_MONTH, 1);
		DateUtil.setStartDay(start);
		start.set(Calendar.MILLISECOND, 0);
		int dayOfWeek = start.get(Calendar.DAY_OF_WEEK);
		start.add(Calendar.DAY_OF_MONTH, Constants.FIRST_DAY_OF_WEEK - dayOfWeek);
		
		Calendar end = Calendar.getInstance();
		end.setTime(start.getTime());
		while (true) {
			end.add(Calendar.DAY_OF_MONTH, 7);
			int currentMonth = end.get(Calendar.MONTH);
			if (currentMonth + 1 > month) {
				break;
			}
			if (currentMonth == 0 && month == 12) {
				break;
			}
		}
		end.add(Calendar.DAY_OF_MONTH, -1);
		return new Calendar[]{start, end};
	}
	
}
