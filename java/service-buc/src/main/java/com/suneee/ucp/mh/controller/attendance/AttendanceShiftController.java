package com.suneee.ucp.mh.controller.attendance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.Constants;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.kaoqin.model.kaoqin.HolidaysSetting;
import com.suneee.kaoqin.model.kaoqin.ShiftCalendar;
import com.suneee.kaoqin.model.kaoqin.ShiftDaySetting;
import com.suneee.kaoqin.model.kaoqin.ShiftSchedule;
import com.suneee.kaoqin.service.kaoqin.HolidaysSettingService;
import com.suneee.kaoqin.service.kaoqin.ShiftCalendarService;
import com.suneee.kaoqin.service.kaoqin.ShiftDaySettingService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.calendar.util.CalendarUtil;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.mh.model.attendance.AttendanceShift;
import com.suneee.ucp.mh.model.attendance.ShiftTime;
import com.suneee.ucp.mh.service.attendance.AttendanceShiftService;
import com.suneee.ucp.mh.service.attendance.ShiftTimeService;

import net.sf.json.JSONObject;
/**
 *<pre>
 * 对象功能:考勤班次表 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:03:50
 *</pre>
 */
@Controller("ucpAttendanceShiftController")
@RequestMapping("/mh/attendance/attendanceShift/")
public class AttendanceShiftController extends UcpBaseController
{
	@Resource(name="ucpAttendanceShiftService")
	private AttendanceShiftService attendanceShiftService;
	@Resource(name="ucpShiftTimeService")
	private ShiftTimeService shiftTimeService;
	@Resource
	private ShiftCalendarService shiftCalendarService;
	@Resource
	private ShiftDaySettingService shiftDaySettingService;
	@Resource
	private HolidaysSettingService holidaysSettingService;
	
	
	/**
	 * 添加或更新考勤班次表。
	 * @param request
	 * @param response
	 * @param attendanceShift 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新考勤班次表")
	public void save(HttpServletRequest request, HttpServletResponse response,AttendanceShift attendanceShift) throws Exception
	{
		String resultMsg=null;
		Long shiftId = RequestUtil.getLong(request, "shiftId");		
		try{
			attendanceShiftService.save(attendanceShift);
			if(shiftId==0){
				resultMsg=getText("添加成功","考勤班次表");
			}else{
				resultMsg=getText("更新成功","考勤班次表");
			}
			writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得考勤班次表分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看考勤班次表分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{		
		List<AttendanceShift> list=attendanceShiftService.getShiftList(new QueryFilter(request,"attendanceShiftItem"));
		for (AttendanceShift shift : list) {
			String reskWeekNames = "";
			String reskDays = shift.getReskDays();
			if (reskDays != null) {
				String days[] = reskDays.split(",");
				for (String day : days) {
					reskWeekNames += shiftCalendarService.getWeekTitle(Short.valueOf(day)) + " ";
				}
				shift.setReskDays(reskWeekNames);
			}
			String timeList = shift.getTimeList();
			if (timeList != null) {
				shift.setTimeList(timeList.replaceAll(",", "&emsp;"));
			}
		}
		ModelAndView mv=this.getAutoView().addObject("attendanceShiftList",list);
		return mv;
	}
	
	/**
	 * 删除考勤班次表
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除考勤班次表")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "shiftId");
			attendanceShiftService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除考勤班次表成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑考勤班次表
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑考勤班次表")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long shiftId = RequestUtil.getLong(request,"shiftId",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		AttendanceShift attendanceShift = new AttendanceShift();
		if (shiftId != 0) {
			attendanceShift = attendanceShiftService.getById(shiftId);
			// 获取班次的考勤时间段
			attendanceShift.setTimes(shiftTimeService.getByShiftIdAndType(shiftId, ShiftTime.TYPE_WORK));
			// 获取班次的休息时间段
			attendanceShift.setRestTimeList(shiftTimeService.getByShiftIdAndType(shiftId, ShiftTime.TYPE_REST));
			// 获取班次的公休日
			List<ShiftCalendar> shiftCalendars = shiftCalendarService.getByShiftId(shiftId);
			for (ShiftCalendar calendar : shiftCalendars) {
				if (calendar.getDayType() == ShiftCalendar.TYPE_RESK) {
					attendanceShift.getReskWeeks()[calendar.getWeek()] = 1;
				}
			}
		}
		// 如果班次的考勤时间段是空的，则创建一个考勤时间段对象放入集合中，保证在初始化编辑页面时考勤时间段的显示
		if (attendanceShift.getTimes() == null || attendanceShift.getTimes().isEmpty()){
			List<ShiftTime> times = new ArrayList<ShiftTime>();
			times.add(new ShiftTime());
			attendanceShift.setTimes(times);
		}
		// 如果班次的休息时间段为空，则创建一个休息时间段对象放入集合中，保证在初始化编辑页面时休息时间段的显示
		if(attendanceShift.getRestTimeList() == null || attendanceShift.getRestTimeList().isEmpty()){
			List<ShiftTime> restTimeList = new ArrayList<ShiftTime>();
			restTimeList.add(new ShiftTime());
			attendanceShift.setRestTimeList(restTimeList);
		}
		ModelAndView mv = getAutoView();
		mv.addObject("attendanceShift",attendanceShift);
		return mv.addObject("returnUrl",returnUrl);
	}

	/**
	 * 为班次进行排班计划设置
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("schedule")
	@Action(description="为班次进行排班计划设置")
	public ModelAndView schedule(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long shiftId=RequestUtil.getLong(request,"shiftId");
		Integer year=RequestUtil.getInt(request,"year");
		Integer month=RequestUtil.getInt(request,"month");
		String flag = RequestUtil.getString(request, "flag");
		QueryFilter filter = new QueryFilter(new JSONObject());
		filter.addFilter("status", AttendanceShift.STATUS_NORMAL);
		List<AttendanceShift> shifts = attendanceShiftService.getAll(filter);
		AttendanceShift attendanceShift = null;
		if (shiftId == 0 && shifts.size() > 0) {
			attendanceShift = shifts.get(0);
			shiftId = attendanceShift.getShiftId();
		}
		ModelAndView mv = getAutoView();
		mv.addObject("shifts", shifts);
		mv.addObject("shiftId", shiftId);
		List<ShiftCalendar> shiftCalendars = shiftCalendarService.getByShiftId(shiftId);
		short reskWeeks[] = new short[8];
		for (ShiftCalendar calendar : shiftCalendars) {
			if (calendar.getDayType() == ShiftCalendar.TYPE_RESK) {
				reskWeeks[calendar.getWeek()] = 1;
			}
		}
		Calendar c = Calendar.getInstance();
		if (year == 0) {
			year = c.get(Calendar.YEAR);
		}
		if (month == 0) {
			month = c.get(Calendar.MONTH) + 1;
		}
		if ("pre".equals(flag)) {
			if (month == 1) {
				year -= 1;
				month = 12;
			} else {
				month -= 1;
			}
		} else if ("next".equals(flag)) {
			if (month == 12) {
				year += 1;
				month = 1;
			} else {
				month += 1;
			}
		}
		mv.addObject("year", year);
		mv.addObject("month", month);
		
		Calendar[] scopes = CalendarUtil.getMonthScope(year, month);
		List<HolidaysSetting> holidaySettings = holidaysSettingService.getHolidaysSettingBetween(scopes[0].getTime(), scopes[1].getTime());
		List<ShiftDaySetting> daySettings = shiftDaySettingService.getShiftDaySettingBetween(shiftId, scopes[0].getTime(), scopes[1].getTime());
		Calendar start = scopes[0];
		List<List<ShiftSchedule>> scheduleWeeks = new ArrayList<List<ShiftSchedule>>();
		List<ShiftSchedule> scheduleDays = new ArrayList<ShiftSchedule>();
		scheduleWeeks.add(scheduleDays);
		while(true) {
			ShiftSchedule schedule = new ShiftSchedule();
			schedule.setDay(start.get(Calendar.DAY_OF_MONTH));
			schedule.setWorking(reskWeeks[start.get(Calendar.DAY_OF_WEEK)] == 0);
			HolidaysSetting holidaySetting = getHolidayOfDay(holidaySettings, start);
			if (holidaySetting != null) {
				schedule.setHolidayId(holidaySetting.getId());
				schedule.setWorking(false);
				schedule.setDesc(holidaySetting.getHolidayName());
			}
			ShiftDaySetting daySetting = getDaySettingOfDay(daySettings, start);
			if (daySetting != null) {
				schedule.setDayScheduleId(daySetting.getId());
				schedule.setWorking(ShiftDaySetting.TYPE_WORK == daySetting.getScheduleType());
				schedule.setDesc(daySetting.getDescription());
			}
			schedule.setThisMonth(month == start.get(Calendar.MONTH) + 1);
			scheduleDays.add(schedule);
			start.add(Calendar.DAY_OF_MONTH, 1);
			if (start.after(scopes[1])) {
				break;
			}
			int week = start.get(Calendar.DAY_OF_WEEK);
			if (Constants.FIRST_DAY_OF_WEEK == week) {
				scheduleDays = new ArrayList<ShiftSchedule>();
				scheduleWeeks.add(scheduleDays);
			}
		}
		mv.addObject("weeks", scheduleWeeks);
		return mv;
	}

	private HolidaysSetting getHolidayOfDay(List<HolidaysSetting> holidaySettings, Calendar day) {
		Date date = day.getTime();
		for (HolidaysSetting setting : holidaySettings) {
			if (setting.getStartDate().compareTo(date) <= 0 && setting.getEndDate().compareTo(date) >= 0) {
				return setting;
			}
		}
		return null;
	}
	
	private ShiftDaySetting getDaySettingOfDay(List<ShiftDaySetting> daySettings, Calendar day) {
		for (ShiftDaySetting setting : daySettings) {
			if (setting.getScheduleDate().compareTo(day.getTime()) == 0) {
				return setting;
			}
		}
		return null;
	}

	/**
	 * 取得考勤班次表明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看考勤班次表明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long shiftId=RequestUtil.getLong(request,"shiftId");
		AttendanceShift attendanceShift = attendanceShiftService.getById(shiftId);	
		return getAutoView().addObject("attendanceShift", attendanceShift);
	}
	
}

