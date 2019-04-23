package com.suneee.ucp.mh.controller;

import com.suneee.Constants;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.kaoqin.model.kaoqin.AttendanceResult;
import com.suneee.kaoqin.model.kaoqin.HolidaysSetting;
import com.suneee.kaoqin.model.kaoqin.ShiftCalendar;
import com.suneee.kaoqin.model.kaoqin.ShiftDaySetting;
import com.suneee.kaoqin.service.kaoqin.AttendanceResultService;
import com.suneee.kaoqin.service.kaoqin.HolidaysSettingService;
import com.suneee.kaoqin.service.kaoqin.ShiftCalendarService;
import com.suneee.kaoqin.service.kaoqin.ShiftDaySettingService;
import com.suneee.platform.calendar.util.CalendarUtil;
import com.suneee.platform.service.system.SysPropertyService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.mh.model.PunchedDay;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 
 *  打卡记录控制类
 * @author sibo
 * @time 2017年5月10日 下午1:30:35
 *
 */
@Controller 
@RequestMapping("/mh/punched/")
public class PunchedController extends UcpBaseController {
	@Resource
	private ShiftCalendarService shiftCalendarService;
	@Resource
	private ShiftDaySettingService shiftDaySettingService;
	@Resource
	private HolidaysSettingService holidaysSettingService;
	@Resource
	private AttendanceResultService attendanceResultService;
	@Resource
	private SysPropertyService sysPropertyService;
	
	@RequestMapping("list")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long shiftId=RequestUtil.getLong(request,"shiftId");
		Integer year=RequestUtil.getInt(request,"year");
		Integer month=RequestUtil.getInt(request,"month");
		String flag = RequestUtil.getString(request, "flag");
		
		ModelAndView mv = this.getAutoView();
		mv.addObject("shiftId", shiftId);
		List<ShiftCalendar> shiftCalendars = shiftCalendarService.getByShiftId(shiftId);
		short reskWeeks[] = new short[8];
		for (ShiftCalendar calendar : shiftCalendars) {
			if (calendar.getDayType() == ShiftCalendar.TYPE_RESK) {
				reskWeeks[calendar.getWeek()] = 1;
			}
		}
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
		if ("pre".equals(flag)) {
			start.add(Calendar.MONTH, -1);
		} else if ("next".equals(flag)) {
			start.add(Calendar.MONTH, 1);
		}
		year = start.get(Calendar.YEAR);
		month = start.get(Calendar.MONTH) + 1;
		mv.addObject("year", year);
		mv.addObject("month", month);
		int daysOfMonth = start.getActualMaximum(Calendar.DAY_OF_MONTH);
		int dayOfWeek = start.get(Calendar.DAY_OF_WEEK);
		start.add(Calendar.DAY_OF_MONTH, Constants.FIRST_DAY_OF_WEEK - dayOfWeek);
		
		//查询自己当月的异常数据
		Long userId = ContextUtil.getCurrentUserId();
		Calendar[] scopes = CalendarUtil.getMonthScope(year, month);
		QueryFilter filter = new QueryFilter(new JSONObject());
		filter.addFilter("userId", userId);
		filter.addFilter("startDate", scopes[0].getTime());
		filter.addFilter("endDate", scopes[1].getTime());
		// 获取考勤信息
		List<AttendanceResult> list=attendanceResultService.getMonthAttendance(filter);
		List<String> array = new ArrayList<String>();
		for (AttendanceResult result : list) {
			array.add(DateFormatUtil.format(result.getAttendanceDate(), "yyyy-MM-dd"));
		}
		
		boolean monthEnd = false;
		List<List<PunchedDay>> scheduleWeeks = new ArrayList<List<PunchedDay>>();
		List<PunchedDay> scheduleDays = new ArrayList<PunchedDay>();
		scheduleWeeks.add(scheduleDays);
		while(true) {
			System.out.println(DateFormatUtil.formatDate(start.getTime()));
			PunchedDay schedule = buildScheduleOfDay(shiftId, start, reskWeeks, array);
			schedule.setThisMonth(month == start.get(Calendar.MONTH) + 1);
			scheduleDays.add(schedule);
			if (daysOfMonth == start.get(Calendar.DAY_OF_MONTH)
					&& schedule.isThisMonth()) {
				monthEnd = true;
			}
			start.add(Calendar.DAY_OF_MONTH, 1);
			int week = start.get(Calendar.DAY_OF_WEEK);
			if (Constants.FIRST_DAY_OF_WEEK == week) {
				if (monthEnd) {
					break;
				}
				scheduleDays = new ArrayList<PunchedDay>();
				scheduleWeeks.add(scheduleDays);
			}
		}
		mv.addObject("weeks", scheduleWeeks);
		
		return mv;
	}
	
	/**
	 * 获取流程实例ID
	 * @param request
	 * @param filter
	 */
	@RequestMapping("getDefId")
	@ResponseBody
	private Long getDefId(HttpServletRequest request){
		// 获取流程类型
		String flowType = RequestUtil.getString(request, "flowType");
		Long defId = 0L;
		if(StringUtils.isNotBlank(flowType)){
			// 根据流程类型获取流程实例ID
			defId = sysPropertyService.getLongByAlias(flowType);
		}
		return defId;
	}
	
	private PunchedDay buildScheduleOfDay(Long shiftId, Calendar day, short reskWeeks[], List<String> array) {
		PunchedDay schedule = new PunchedDay();
		schedule.setDay(day.get(Calendar.DAY_OF_MONTH));
		schedule.setWorking(reskWeeks[day.get(Calendar.DAY_OF_WEEK)] == 0);
		HolidaysSetting holidaySetting = holidaysSettingService.getHolidaysSettingByDay(day.getTime());
		if (holidaySetting != null) {
			schedule.setWorking(false);
		}
		ShiftDaySetting daySetting = shiftDaySettingService.getShiftSettingByDay(shiftId, day.getTime());
		if (daySetting != null) {
			schedule.setWorking(ShiftDaySetting.TYPE_WORK == daySetting.getScheduleType());
		}
		
		//判断是否是今天
		Calendar today = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String todayStr = format.format(today.getTime());
		String dayStr = format.format(day.getTime());
		schedule.setDate(dayStr);
		if (dayStr.equals(todayStr)) {
			schedule.setToday(true);
		} else {
			schedule.setToday(false);
		}
		
		for (String date : array) {
			//如果数组中包含该字符串，说明该日考勤异常
			if (date.equals(dayStr)) {
				schedule.setAbnormal(true);
			}
		}
				
		return schedule;
	}
}
