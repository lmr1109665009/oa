package com.suneee.kaoqin.controller.kaoqin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.kaoqin.model.kaoqin.AttendanceResult;
import com.suneee.kaoqin.service.kaoqin.AttendanceResultService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.calendar.util.CalendarUtil;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.mh.model.attendance.AttendanceShift;
import com.suneee.ucp.mh.model.attendance.VacationRemain;
import com.suneee.ucp.mh.service.attendance.AttendanceShiftService;
import com.suneee.ucp.mh.service.attendance.VacationRemainService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 考勤模块对外接口API控制器
 * @author mikel
 *
 */
@Controller
@RequestMapping("/kaoqin/kaoqin/api/")
public class AttendanceAPIController extends BaseController
{
	@Resource
	private AttendanceResultService attendanceResultService;
	@Resource(name="ucpAttendanceShiftService")
	private AttendanceShiftService shiftService;
	@Resource(name="ucpVacationRemainService")
	private VacationRemainService remainService;
	@Resource
	private SysUserService userService;
	
	@RequestMapping("userDayInfo")
	@Action(description="查询用户某天的考勤信息")
	@ResponseBody
	public JSONObject userDayInfo(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		QueryFilter filter = new QueryFilter(new JSONObject());
		Long userId = RequestUtil.getLong(request, "userId");
		if (userId == 0) {
			String account = RequestUtil.getString(request, "account");
			SysUser user = userService.getByAccount(account);
			if (user != null) {
				userId = user.getUserId();
			}
		}
		Date attendanceDate = RequestUtil.getDate(request, "attendanceDate");
		filter.addFilter("userId", userId);
		filter.addFilter("attendanceDate", attendanceDate);
		// 获取用户班次
		List<AttendanceShift> shifts = shiftService.getShiftByUserDay(filter);
		JSONObject obj = new JSONObject();
		if (shifts.size() > 0) {
			AttendanceShift shift = shifts.get(0);
			obj.put("shiftName", shift.getShiftName());
			filter.addFilter("shiftId", shift.getShiftId());
		}
		// 排序
		filter.getFilters().put("orderField", "seq_key");
		filter.getFilters().put("orderSeq", "asc");
		// 获取考勤信息
		List<AttendanceResult> list=attendanceResultService.getAll(filter);
		JSONArray array = new JSONArray();
		int checkTimes = 0;
		for (AttendanceResult result : list) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("standardTime", DateFormatUtil.format(result.getStandardTime(), "yyyy-MM-dd HH:mm"));
			if (result.getRealTime()==null){
				item.put("realTime", "");
			}else{
				item.put("realTime", DateFormatUtil.format(result.getRealTime(),"HH:mm"));
			}
			if (result.getResultType()==null){
				item.put("resultType", "");
			}else{
				item.put("resultType", result.getResultType());
			}

			if (result.getCheckType()==null){
				item.put("checkType", "");
			}else{
				item.put("checkType", result.getCheckType());
			}
			if (result.getCheckResult()==null){
				item.put("checkResult", "");
			}else{
				item.put("checkResult", result.getCheckResult());
			}
			if (result.getOverTime()==null){
				item.put("overTime", "");
			}else{
				item.put("overTime", result.getOverTime());
			}
			array.add(item);
			if (result.getResultType() <= AttendanceResult.TYPE_OVERTIME) {
				checkTimes ++;
			}
		}
		obj.put("checkTimes", checkTimes);
		obj.put("list", array);
		return obj;
	}
	
	@RequestMapping("userVacationRemains")
	@Action(description="查询用户的假期结余")
	@ResponseBody
	public JSONArray userVacationRemains(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
//		String account = RequestUtil.getString(request, "account");
//		Long userId = userService.getByAccount(account);
		SysUser currentUser = (SysUser) ContextUtil.getCurrentUser();
		// 获取假期结余列表
		List<VacationRemain> remains = remainService.getUserRemainDetail(currentUser.getUserId());
		JSONArray array = new JSONArray();
		Calendar end = Calendar.getInstance();
		end.set(Calendar.DAY_OF_YEAR, end.getActualMaximum(Calendar.DAY_OF_YEAR));
		String endDateStr = DateFormatUtil.formatDate(end.getTime());
		for (VacationRemain remain : remains) {
			Map<String, Object> item = new HashMap<String, Object>();
			int remainNum = 0;
			if (remain.getRemained() != null) {
				remainNum = remain.getRemained().intValue();
			}
			if (remain.getValidDate() == null) {
				item.put("validDate", endDateStr);
			} else {
				item.put("validDate", DateFormatUtil.formatDate(remain.getValidDate()));
			}
			item.put("vacationName", remain.getVacationName());
			item.put("unit", remain.getBaseUnit());
			item.put("remained", remainNum);
			item.put("used", remain.getUsed());
			array.add(item);
		}
		return array;
	}
	

	
	@RequestMapping("userMonthInfo")
	@Action(description="查询用户某个月的考勤信息")
	@ResponseBody
	public JSONArray userMonthInfo(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		Long userId = RequestUtil.getLong(request, "userId");
		if (userId == 0) {
			String account = RequestUtil.getString(request, "account");
			SysUser user = userService.getByAccount(account);
			if (user != null) {
				userId = user.getUserId();
			}
		}
		int year = RequestUtil.getInt(request, "year");
		int month = RequestUtil.getInt(request, "month");
		Calendar[] scopes = CalendarUtil.getMonthScope(year, month);
		// 获取用户班次
		QueryFilter filter = new QueryFilter(new JSONObject());
		filter.addFilter("userId", userId);
		filter.addFilter("startDate", scopes[0].getTime());
		filter.addFilter("endDate", scopes[1].getTime());
		// 获取考勤信息
		List<AttendanceResult> list=attendanceResultService.getMonthAttendance(filter);
		JSONArray array = new JSONArray();
		for (AttendanceResult result : list) {
//			Map<String, Object> item = new HashMap<String, Object>();
//			item.put("attendanceDate", DateFormatUtil.format(result.getAttendanceDate(), "yyyy-MM-dd"));
//			item.put("checkResult", result.getCheckResult());
			array.add(DateFormatUtil.format(result.getAttendanceDate(), "yyyy-MM-dd"));
		}
		return array;
	}
	
	
	
	@RequestMapping("myMonthInfo")
	@Action(description="查询我某个月的考勤信息")
	@ResponseBody
	public JSONArray myMonthInfo(HttpServletRequest request,HttpServletResponse response) throws Exception {	
		Long userId = ContextUtil.getCurrentUserId();
		int year = RequestUtil.getInt(request, "year");
		int month = RequestUtil.getInt(request, "month");
		Calendar[] scopes = CalendarUtil.getMonthScope(year, month);
		// 获取用户班次
		QueryFilter filter = new QueryFilter(new JSONObject());
		filter.addFilter("userId", userId);
		filter.addFilter("startDate", scopes[0].getTime());
		filter.addFilter("endDate", scopes[1].getTime());
		// 获取考勤信息
		List<AttendanceResult> list=attendanceResultService.getMonthAttendance(filter);
		JSONArray array = new JSONArray();
		for (AttendanceResult result : list) {
			array.add(DateFormatUtil.format(result.getAttendanceDate(), "yyyy-MM-dd"));
		}
		return array;
	}
	
	
	/**
	 * 查询我的某天的打卡记录
	 *  sibo 2017-05-19
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("myDayInfo")
	@Action(description="查询我某天的考勤信息")
	@ResponseBody
	public Map<String, Object> myDayInfo(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		QueryFilter filter = new QueryFilter(new JSONObject());
		Long userId = ContextUtil.getCurrentUserId();
		Date attendanceDate = RequestUtil.getDate(request, "attendanceDate");
		filter.addFilter("userId", userId);
		filter.addFilter("attendanceDate", attendanceDate);
		// 获取用户班次
		List<AttendanceShift> shifts = shiftService.getShiftByUserDay(filter);
		Map<String,Object> obj = new HashMap<String, Object>();
		if (shifts.size() > 0) {
			AttendanceShift shift = shifts.get(0);
			obj.put("shiftName", shift.getShiftName());
			filter.addFilter("shiftId", shift.getShiftId());
		}
		// 排序
		filter.getFilters().put("orderField", "seq_key");
		filter.getFilters().put("orderSeq", "asc");
		// 获取考勤信息
		List<AttendanceResult> list=attendanceResultService.getAll(filter);
		List<Map<String, Object>> array = new ArrayList<Map<String, Object>>();
		int checkTimes = 0;
		for (AttendanceResult result : list) {
			Map<String, Object> item = new HashMap<String, Object>();
			String standardTime = "";
			if (result.getStandardTime() != null) {
				standardTime = DateFormatUtil.format(result.getStandardTime(), "HH:mm");
			}
			item.put("standardTime", standardTime);
			String realTime = "";
			if (result.getRealTime() != null) {
				realTime = DateFormatUtil.format(result.getRealTime(),"HH:mm");
			}
			item.put("realTime", realTime);
			item.put("resultType", result.getResultType());
			item.put("checkType", result.getCheckType());
			item.put("checkResult", result.getCheckResult());
			item.put("overTime", result.getOverTime());
			array.add(item);
			if (result.getResultType() <= AttendanceResult.TYPE_OVERTIME) {
				checkTimes ++;
			}
		}
		obj.put("checkTimes", checkTimes);
		obj.put("list", array);
		return obj;
	}
}
