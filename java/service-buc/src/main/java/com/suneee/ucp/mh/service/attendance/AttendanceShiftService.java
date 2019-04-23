package com.suneee.ucp.mh.service.attendance;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.dao.kaoqin.ExemmptSettingDao;
import com.suneee.kaoqin.dao.kaoqin.ShiftCalendarDao;
import com.suneee.kaoqin.model.kaoqin.*;
import com.suneee.kaoqin.service.kaoqin.*;
import com.suneee.platform.model.system.SysUser;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.mh.dao.attendance.AttendanceShiftDao;
import com.suneee.ucp.mh.dao.attendance.ShiftTimeDao;
import com.suneee.ucp.mh.model.attendance.AttendanceShift;
import com.suneee.ucp.mh.model.attendance.ShiftTime;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *<pre>
 * 对象功能:考勤班次表 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:03:50
 *</pre>
 */
@Service("ucpAttendanceShiftService")
public class AttendanceShiftService extends UcpBaseService<AttendanceShift>
{
	@Resource(name="ucpAttendanceShiftDao")
	private AttendanceShiftDao dao;
	@Resource(name="ucpShiftTimeDao")
	private ShiftTimeDao timeDao;
	@Resource
	private ShiftCalendarDao calendarDao;
	@Resource
	private ExemmptSettingDao exemmptSettingDao;
	@Resource
	private ShiftUserSettingService userSettingService;
	@Resource
	private AttendanceResultService resultService;
	@Resource
	private AttendanceRecordService recordService;
	@Resource
	private HolidaysSettingService holidaysSettingService;
	@Resource
	private ShiftDaySettingService daySettingService;
	@Resource(name="ucpShiftTimeService")
	private ShiftTimeService shiftTimeService;
	@Resource 
	private ShiftCalendarService shiftCalendarService;
	
	public AttendanceShiftService()
	{
	}
	
	@Override
	protected IEntityDao<AttendanceShift, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 根据json字符串获取AttendanceShift对象
	 * @param json
	 * @return
	 */
	public AttendanceShift getAttendanceShift(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		AttendanceShift attendanceShift = JSONObjectUtil.toBean(json, AttendanceShift.class);
		return attendanceShift;
	}

	/**
	 * 保存 考勤班次表 信息
	 * @param attendanceShift
	 */
	public void save(AttendanceShift attendanceShift){
		Long id=attendanceShift.getShiftId();
		
		// 班次考勤时间段信息
		String workShiftTimes = attendanceShift.getTimeList();
		List<ShiftTime> workShiftTimeList = null;
		if(StringUtils.isNotBlank(workShiftTimes)){
			workShiftTimeList = JSONObjectUtil.toBeanList(workShiftTimes, ShiftTime.class);
		}
		
		// 班次休息时间段信息
		String restShiftTimes = attendanceShift.getRestTimes();
		List<ShiftTime> restShiftTimeList = null;
		if(StringUtils.isNotBlank(restShiftTimes)){
			restShiftTimeList = JSONObjectUtil.toBeanList(restShiftTimes, ShiftTime.class);
		}
		
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			attendanceShift.setShiftId(id);
			// 保存班次的工作时间段
			shiftTimeService.addShiftTimes(id, workShiftTimeList, ShiftTime.TYPE_WORK);
			// 保存班次的休息时间段
			shiftTimeService.addShiftTimes(id, restShiftTimeList, ShiftTime.TYPE_REST);
			// 保存班次的休息日设置
			this.addShiftCalendars(attendanceShift);
			
			// 计算班次的工作时长
			double minutes = shiftTimeService.computeTotalTime(workShiftTimeList, restShiftTimeList);
			attendanceShift.setWorkHour(minutes/60);
			attendanceShift.setStatus(AttendanceShift.STATUS_NORMAL);
			attendanceShift.setCreateby(ContextUtil.getCurrentUserId());
			attendanceShift.setCreatetime(new Date());
			// 保存班次基本信息
			this.add(attendanceShift);
		}
		else{
			// 删除班次的原有时间段（包括考勤时间段和休息时间段）
			timeDao.removeTimeByShiftId(attendanceShift.getShiftId());
			// 保存班次的工作时间段
			shiftTimeService.addShiftTimes(id, workShiftTimeList, ShiftTime.TYPE_WORK);
			// 保存班次的休息时间段
			shiftTimeService.addShiftTimes(id, restShiftTimeList, ShiftTime.TYPE_REST);
			// 删除班次的休息日设置
			calendarDao.removeByShiftId(attendanceShift.getShiftId());
			// 保存班次的休息日设置
			this.addShiftCalendars(attendanceShift);
			// 计算班次的工作时长
			double minutes = shiftTimeService.computeTotalTime(workShiftTimeList, restShiftTimeList);
			attendanceShift.setWorkHour(minutes/60);
			this.update(attendanceShift);
		}
	}

	/**
	 * 获取设置的班次列表
	 * @param queryFilter
	 * @return
	 */
	public List<AttendanceShift> getShiftList(QueryFilter queryFilter) {
		return dao.getBySqlKey("getShiftList", queryFilter);
	}
	
	/**
	 * 获取关联人员信息的班次列表
	 * @return
	 */
	public List<AttendanceShift> getShiftUserList(QueryFilter filter){
		return dao.getShiftUserList(filter);
	}

	/**
	 * 判断某个某次在指定日期是否是工作日
	 * @param day 日期
	 * @param shiftId 班次Id
	 * @return
	 */
	public boolean isWorkDay(Calendar day, Long shiftId) {
		// 查询班次的排班日历
		ShiftCalendar shiftCalendar = shiftCalendarService.getShiftCalendarBy(shiftId, day);
		
		// 获取班次当日的单日排班
		ShiftDaySetting daySetting = daySettingService.getShiftSettingByDay(shiftId, day.getTime());
		// 当天没有单日排班
		if (daySetting == null){
			// 没有排班日历设置，或者排班日历设置为工作日
			if(shiftCalendar == null || shiftCalendar.getDayType() == ShiftCalendar.TYPE_WORK){
				// 获取节假日设置
				HolidaysSetting setting = holidaysSettingService.getHolidaysSettingByDay(day.getTime());
				// 存在节假日设置，则当天为非工作日
				if(setting != null){
					return false;
				}
			}else{
				return false;
			}
		}else{
			// 当天的单日排班设置为休息日，则天当为非工作日
			if(daySetting.getScheduleType() == ShiftDaySetting.TYPE_REST){
				return false;
			}
		}
		
		return true;
	}

	/**
	 * 计算指某天的流程类考勤数据
	 * @param shift
	 * @param day
	 */
	public void computeProcessResult(Calendar day) {

		Calendar c = Calendar.getInstance();
		c.setTime(day.getTime());
		c.set(Calendar.MILLISECOND, 0);
		DateUtil.setStartDay(c);
		Date startTime = c.getTime();
		DateUtil.setEndDay(c);
		Date endTime = c.getTime();
		// 获取班次的考勤人员列表
		List<AttendanceResult> results = resultService.getProcessResultList(startTime, endTime);
		// 获取用户的打卡记录
		List<AttendanceRecord> records = recordService.getRecordList(null, startTime, endTime);
		Date scopes[] = new Date[]{startTime, endTime};
		for (AttendanceResult result : results) {
			matchRecord(records, result.getStandardTime(), scopes, result.getCheckType() == 1, result, 0L);
			resultService.save(result);
		}
	}
	
	/**
	 * 计算加班考勤
	 * @param userId 申请人ID
	 * @param businessId 申请记录ID
	 * @param startDate 加班申请开始时间
	 * @param endDate 加班申请结束时间
	 */
	public void computeOvertimeAttendance(Long userId, Long businessId, Date startDate, Date endDate){
		// 查询用户是否是免签人员，免签人员不计算考勤
		ExemmptSetting exemmptSetting = exemmptSettingDao.getByTargetId(userId);
		if (exemmptSetting != null) {
			return;
		}
		
		// 查询用户的班次
		AttendanceShift attendanceShift = this.getShiftByUserId(userId);
		// 用户没有设置班次时，不计算加班时间
		if(attendanceShift == null){
			return;
		}
		
		Long shiftId = attendanceShift.getShiftId();
		// 查询班次的考勤时间段
		List<ShiftTime> workShiftTimeList = shiftTimeService.getByShiftIdAndType(shiftId, ShiftTime.TYPE_WORK);
		// 如果考勤班次为空，则不增加考勤记录
		if(workShiftTimeList.isEmpty()){
			return;
		}
		
		Calendar cycleTime = Calendar.getInstance();
		cycleTime.setTime(startDate);
		while(cycleTime.getTime().compareTo(endDate) <= 0){
			// 考勤班次在当天不是工作日，则计算加班考勤
			if(!this.isWorkDay(cycleTime, shiftId)){
				// 查询已有考勤结果（针对补提交加班申请的情况）
				List<AttendanceResult> results = resultService.getResultList(userId, shiftId, cycleTime.getTime());
				for (AttendanceResult result : results) {
					// 更新已有的考勤结果
					result.setRealTime(null);
					result.setRecordId(null);
					result.setCheckResult(AttendanceResult.RESULT_NONE);
					result.setResultType(AttendanceResult.TYPE_OVERTIME);
					resultService.update(result);
				}
				// 查询用户当日的打卡记录
				List<AttendanceRecord> records = recordService.getRecordList(userId, cycleTime.getTime());
				AttendanceRecord startRecord = null;
				AttendanceRecord endRecord = null;
				// 获取用户当日的最早打卡记录和最晚打卡记录
				if(records.size() >= 1){
					startRecord = records.get(0);
					if(records.size() >= 2){
						endRecord = records.get(records.size() - 1);
					}
				}
				short seqKey = 0;
				int length = workShiftTimeList.size();
				ShiftTime shiftTime = null;
				Date startTime = null;
				Date endTime = null;
				Long startRecordId = null;
				Long endRecordId = null;
				for(int i = 0 ; i < length; i++){
					shiftTime = workShiftTimeList.get(0);
					// 计算当前考勤时间段的当天的标准时间
					shiftTimeService.computeStandardShiftTime(shiftTime, cycleTime, false);
					// 班次的最早打卡点取开始打卡记录
					if(i == 0 && startRecord != null){
						startTime = startRecord.getCheckTime();
						startRecordId = startRecord.getAttendanceId();
					}
				    this.saveOvertimeResult(results, shiftTime.getStartTime(), userId, shiftId, startRecordId, 
				    		businessId, seqKey++, startTime, true);
				    // 班次的最晚打卡点最结束打卡记录
				    if(i == (length -1) && endRecord != null){
				    	endTime = endRecord.getCheckTime();
				    	endRecordId = endRecord.getAttendanceId();
				    }
				    this.saveOvertimeResult(results, shiftTime.getEndTime(), userId, shiftId, endRecordId, 
				    		businessId, seqKey, endTime, false);
				}
			}
			cycleTime.add(Calendar.DAY_OF_MONTH, 1);
			DateUtil.setStartDay(cycleTime);
		}
	}
	
	/**
	 * 计算用户通过流程申请后的考勤结果
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @param type （3-请假，4-出差，5-外出，6-销假）
	 */
	public void computeUserProcessResult(Long userId, Long businessId, Date startTime, Date endTime, short type) throws IOException {
		// 查询用户是否是免签人员，免签人员不计算考勤
		ExemmptSetting exemmptSetting = exemmptSettingDao.getByTargetId(userId);
		if (exemmptSetting != null) {
			return;
		}
		
		// 获取用户的考勤班次
		AttendanceShift attendanceShift = this.getShiftByUserId(userId);
		// 用户没有设置考勤班次，则不计算考勤
		if(attendanceShift == null){
			return;
		}
		Long shiftId = attendanceShift.getShiftId();
		
		// 查询用户所在班次的考勤时间
		List<ShiftTime> times = shiftTimeService.getByShiftIdAndType(shiftId, ShiftTime.TYPE_WORK);
		
		// 确定循环计算的开始时间
		Calendar cycleTime = Calendar.getInstance();
		cycleTime.setTime(startTime);
		DateUtil.setStartDay(cycleTime);
		while(cycleTime.getTime().compareTo(endTime) <= 0) {
			// 如果班次在当日是工作日，则计算考勤
			if (this.isWorkDay(cycleTime, shiftId)) {
				// 获取用户当日的已有考勤结果（补申请的情况）
				List<AttendanceResult> results = resultService.getResultList(userId, shiftId, cycleTime.getTime());
				for (AttendanceResult result : results) {
					// 如果是工作日考勤，则删除考勤结果
					if(result.getResultType() == AttendanceResult.TYPE_WORK){
						resultService.delById(result.getResultId());
					}
					// 如果是流程类考勤
					else{
						//标准打卡时间在销假申请的开始时间和结束时间之间（需要根据打卡记录考勤），则删除考勤结果
						if(result.getStandardTime().compareTo(startTime) >= 0 && result.getStandardTime().compareTo(endTime) <= 0){
							resultService.delById(result.getResultId());
						}
					}
				}
				
				// 销假不需要计算流程类考勤结果
				if(type != AttendanceResult.TYPE_BACK){
					short seqKey = 0;
					for (ShiftTime time : times) {
						// 计算考勤时间段的标准打卡时间
						shiftTimeService.computeStandardShiftTime(time, cycleTime, false);
						
						// 如果标准上班打卡时间在申请的开始时间和结束时间之间，则增加一条上班的考勤记录
						AttendanceResult result = genResult(userId, shiftId, seqKey++, cycleTime.getTime(), time.getStartTime(), businessId, type, true);
						if(time.getStartTime().compareTo(startTime) >= 0 && time.getStartTime().compareTo(endTime) <= 0){
							resultService.add(result);
						}
						
						// 如果标准下班打卡时间在开始时间和结束时间之间，则增加一条下班的考勤记录
						result = genResult(userId, shiftId, seqKey++, cycleTime.getTime(), time.getEndTime(), businessId, type, false);
						if(time.getEndTime().compareTo(startTime) >= 0 && time.getEndTime().compareTo(endTime) <= 0){
							resultService.add(result);
						}
					}
				}
				// 补申请时重新计算当天的考勤信息
				if (cycleTime.before(Calendar.getInstance())) {
					computeUserAttendanceOfDay(attendanceShift, times, userId, cycleTime);
				}
			}
			cycleTime.add(Calendar.DAY_OF_MONTH, 1);
		}
	}
	
	/**
	 * 计算签卡的考勤结果
	 * @param userId
	 * @param businessId
	 * @param signTime
	 */
	public void computeSignResult(Long userId, Long businessId, Date signTime) throws IOException {
		// 查询用户班次
		AttendanceShift attendanceShift = this.getShiftByUserId(userId);
		// 用户没有设置班次，则不计算考勤
		if (attendanceShift == null) {
			return;
		}
		Long shiftId = attendanceShift.getShiftId();
		
		Calendar c = Calendar.getInstance();
		c.setTime(signTime);
		DateUtil.setStartDay(c);
		
		// 获取用户的已有考勤结果
		List<AttendanceResult> oldResults = resultService.getResultList(userId, shiftId, c.getTime());
		// 还原考勤结果
		for (AttendanceResult result : oldResults) {
			if (result.getResultType() == AttendanceResult.TYPE_WORK) {
				resultService.delById(result.getResultId());
			}
		}
		// 添加补签卡记录
		AttendanceRecord record = new AttendanceRecord();
		record.setCheckFrom(AttendanceRecord.FROM_PATCH);
		record.setCheckTime(signTime);
		record.setUserId(userId);
		record.setMemoinfo(businessId+"");
		recordService.save(record);
		
		Calendar day = Calendar.getInstance();
		day.setTime(signTime);
		DateUtil.setStartDay(day);
		List<ShiftTime> times = shiftTimeService.getByShiftIdAndType(shiftId, ShiftTime.TYPE_WORK);
		computeUserAttendanceOfDay(attendanceShift, times, userId, day);
	}
	
	/**
	 * 计算某个班次某天的考勤结果
	 * @param shift
	 * @param day
	 */
	public void computeShiftAttendanceOfDay(AttendanceShift shift, Calendar day, List<SysUser> excludeUsers) {
		// 获取班次的考勤人员列表
		List<SysUser> users = this.getShiftUsers(shift.getShiftId());
		// 去除班次内的免签人员
		users.removeAll(excludeUsers);
		
		// 查询班次的考勤时间段
		List<ShiftTime> workTimeList = shiftTimeService.getByShiftIdAndType(shift.getShiftId(), ShiftTime.TYPE_WORK);
		for (SysUser user : users) {
			this.computeUserAttendanceOfDay(shift, workTimeList, user.getUserId(), day);
		}
	}
	
	/**
	 * 计算某个用户的某个班次某天的考勤结果
	 * @param shift 班次信息
	 * @param times 考勤时间段列表
	 * @param userId 用户ID
	 * @param day 指定的某一天
	 */
	public void computeUserAttendanceOfDay(AttendanceShift shift, List<ShiftTime> times, Long userId, Calendar day) {
		// 班次未设置考勤时间段时，不计算用户考勤
		if(times == null || times.isEmpty()){
			return;
		}
		Long shiftId = shift.getShiftId();
		short seqKey = 0;
		for (int i = 0; i < times.size(); i++ ) {
			ShiftTime time = times.get(i);
			// 计算考勤时间段的标准打卡时间及打卡范围
			shiftTimeService.computeStandardShiftTime(time, day, true);
			Date[] upScopes = new Date[]{time.getStartBeginRange(), time.getStartEndRange()};
			Date[] downScopes = new Date[]{time.getEndBeginRange(), time.getEndEndRange()};
			// 获取用户的打卡记录
			List<AttendanceRecord> records = recordService.getRecordList(userId, upScopes[0], downScopes[1]);
			// 获取用户的已有考勤结果
			List<AttendanceResult> results = resultService.getResultList(userId, shiftId, upScopes[0], downScopes[1]);
			AttendanceResult up = this.computeTimeResult(userId, shift, records, results, time.getStartTime(), upScopes, day, seqKey++, true);
			AttendanceResult down = this.computeTimeResult(userId, shift, records, results, time.getEndTime(), downScopes, day, seqKey++, false);
			Date checkStart = time.getStartTime();
			if (up != null && up.getRealTime() != null) {
				checkStart = up.getRealTime();
			}
			Date checkEnd = time.getEndTime();
			if (down != null && down.getRealTime() != null) {
				checkEnd = down.getRealTime();
			}
			double standardMinite = DateUtil.betweenMinute(time.getStartTime(), time.getEndTime());
			double minite = DateUtil.betweenMinute(checkStart, checkEnd);
			// 如果实际时长小于标准时长
			if (standardMinite - minite > 1) {
				if (up != null && down != null && down.getCheckResult() == AttendanceResult.RESULT_NORMAL){
					up.setOverTime((long) (standardMinite - minite));
					up.setCheckResult(AttendanceResult.RESULT_LATE);
				}
			}
			if (up != null) {
				if (up.getResultId() == null) {
					up.setResultId(UniqueIdUtil.genId());
					resultService.add(up);
				} else {
					resultService.update(up);
				}
			}
			if (down != null) {
				if (down.getResultId() == null) {
					down.setResultId(UniqueIdUtil.genId());
					resultService.add(down);
				} else {
					resultService.update(down);
				}
			}
		}
	}
	
	/**
	 * 获取班次的考勤人员列表
	 * @param shiftId
	 * @return
	 */
	public List<SysUser> getShiftUsers(Long shiftId) {
		return dao.getBySqlKeyGenericity("getShiftUsers", shiftId);
	}

	/**
	 * 根据条件获取用户的排班信息
	 * @param filter
	 * @return
	 */
	public List<AttendanceShift> getShiftByUserDay(QueryFilter filter) {
		return dao.getBySqlKey("getShiftByUserDay", filter);
	}
	
	/**
	 * 根据用户ID获取用户班次信息
	 * @param userId
	 * @return
	 */
	public AttendanceShift getShiftByUserId(Long userId){
		QueryFilter filter = new QueryFilter(new JSONObject());
		filter.addFilter("userId", userId);
		List<AttendanceShift> shifts = this.getShiftByUserDay(filter);
		if(shifts.isEmpty()){
			return null;
		}
		return shifts.get(0);
	}
	
	/**
	 * 添加班次休息日设置记录
	 * @param shift
	 */
	private void addShiftCalendars(AttendanceShift shift) {
		String reskDays = shift.getReskDays();
		if (reskDays != null) {
			String days[] = reskDays.split(",");
			for(String day: days) {
				ShiftCalendar calendar = this.genShiftCalendar(shift.getShiftId());
				calendar.setWeek(Short.valueOf(day));
				calendar.setDayType(ShiftCalendar.TYPE_RESK);
				calendarDao.add(calendar);
				shift.getReskWeeks()[calendar.getWeek()] = 1;
			}
			for (int i =1; i<shift.getReskWeeks().length; i++) {
				if (shift.getReskWeeks()[i] == 0){
					ShiftCalendar calendar = this.genShiftCalendar(shift.getShiftId());
					calendar.setWeek((short)i);
					calendar.setDayType(ShiftCalendar.TYPE_WORK);
					calendarDao.add(calendar);
				}
			}
		}
	}

	private ShiftCalendar genShiftCalendar(Long shiftId) {
		ShiftCalendar calendar = new ShiftCalendar();
		calendar.setId(UniqueIdUtil.genId());
		calendar.setShiftId(shiftId);
		return calendar;
	}
	
	/**
	 * 保存加班的考勤结果
	 * 
	 * @param results 考勤结果列表
	 * @param standardTime 标准考勤时间
	 * @param userId 用户ID
	 * @param shiftId 考勤班次
	 * @param recordId 打卡记录ID
	 * @param businessId 申请ID
	 * @param seqKey
	 * @param checkTime 打卡时间
	 * @param upTime 上/下班：true-上班，false-下班
	 */
	private void saveOvertimeResult(List<AttendanceResult> results, Date standardTime, Long userId, Long shiftId, 
		Long recordId, Long businessId,short seqKey, Date checkTime, boolean upTime){
		// 匹配标准打卡点的考勤结果（针对重新提交加班申请的情况）
		AttendanceResult computeResult = null;
		for(AttendanceResult result : results){
			if(result.getStandardTime().compareTo(standardTime) == 0){
				computeResult = result;
				break;
			}
		}
		
		// 不存在考勤结果
		if(computeResult == null){
			computeResult = this.genResult(userId, shiftId, seqKey, standardTime, standardTime, businessId, 
				AttendanceResult.TYPE_OVERTIME, upTime);
			
			computeResult.setRealTime(checkTime);
			computeResult.setRecordId(recordId);
			if(checkTime == null){
				computeResult.setCheckType(AttendanceResult.RESULT_NONE);
			}
			resultService.add(computeResult);
		}
		// 已存在考勤结果
		else{
			computeResult.setRealTime(checkTime);
			computeResult.setRecordId(recordId);
			computeResult.setBusinessId(businessId);
			if(checkTime != null){
				computeResult.setCheckType(AttendanceResult.RESULT_NORMAL);
			}
			resultService.update(computeResult);
		}
	}

	/**
	 * 计算某个时间点的考勤结果
	 * @return
	 */
	private AttendanceResult computeTimeResult(Long userId, AttendanceShift shift, List<AttendanceRecord> records, List<AttendanceResult> results, 
			Date standardTime, Date scopes[], Calendar day, short seqKey, boolean upTime) {
		// 考勤结果
		AttendanceResult computeResult = null;
		for (AttendanceResult result : results) {
			if (result.getStandardTime().equals(standardTime)) {
				computeResult = result;
				break;
			}
		}
		// 考勤结果已经存在，不需要重新计算
		if (computeResult != null) {
			return null;
		} else {
			computeResult = new AttendanceResult();
			computeResult.setUserId(userId);
			computeResult.setShiftId(shift.getShiftId());
			computeResult.setAttendanceDate(day.getTime());
			computeResult.setStandardTime(standardTime);
			computeResult.setCheckType(upTime ? AttendanceResult.TYPE_UP : AttendanceResult.TYPE_DWON);
			computeResult.setResultType(AttendanceResult.TYPE_WORK);
			computeResult.setSeqKey(seqKey);
			matchRecord(records, standardTime, scopes, upTime, computeResult, shift.getFloatTime());
			return computeResult;
		}
	}

	private void matchRecord(List<AttendanceRecord> records, Date standardTime, Date[] scopes,
			boolean upTime, AttendanceResult computeResult, Long floatTime) {
		// 匹配的打卡记录
		AttendanceRecord validRecord = null;
		// 优先取卡范围
		Date scopesFirst[] = new Date[2];
		// 其次取卡范围
		Date scopesSend[] = new Date[2];
		if (upTime) {
			scopesFirst[0] = scopes[0];
			scopesFirst[1] = standardTime;
			scopesSend[0] = standardTime;
			scopesSend[1] = scopes[1];
		} else {
			scopesSend[0] = scopes[0];
			scopesSend[1] = standardTime;
			scopesFirst[0] = standardTime;
			scopesFirst[1] = scopes[1];
		}
		double maxMinute = 0;
		for (AttendanceRecord record : records) {
			if (!computeResult.getUserId().equals(record.getUserId())) {
				continue;
			}
			// 打卡时间
			Date checkTime = record.getCheckTime();
			if(checkTime.compareTo(scopesFirst[0]) >= 0 && checkTime.compareTo(scopesFirst[1]) <= 0){
				double minute = Math.abs(DateUtil.betweenMinute(checkTime, standardTime));
				// 匹配离标准打卡时间最远的记录（即上班取最早，下班取最晚）
				if (minute > maxMinute) {
					validRecord = record;
					maxMinute = minute;
				}
			}
		}
		// 如果优先范围没有匹配到
		if (validRecord == null) {
			double minMinute = DateUtil.betweenMinute(scopesSend[0], scopesSend[1]);
			for (AttendanceRecord record : records) {
				if (!computeResult.getUserId().equals(record.getUserId())) {
					continue;
				}
				// 打卡时间
				Date checkTime = record.getCheckTime();
				if (checkTime.after(scopesSend[0]) && checkTime.before(scopesSend[1])) {
					double minute = Math.abs(DateUtil.betweenMinute(checkTime, standardTime));
					// 匹配离标准打卡时间最近的记录
					if (minute < minMinute) {
						validRecord = record;
						minMinute = minute;
					}
				}
			}
		}
		if (validRecord != null) {
			// 删除打卡，避免重复计算
			records.remove(validRecord);
			computeResult.setRecordId(validRecord.getAttendanceId());
			computeResult.setRealTime(validRecord.getCheckTime());
			computeResult.setCheckResult(AttendanceResult.RESULT_NORMAL);
			if (validRecord.getCheckFrom() == AttendanceRecord.FROM_PATCH) {
				if (validRecord.getMemoinfo() != null) {
					computeResult.setBusinessId(Long.valueOf(validRecord.getMemoinfo()));
				}
			}
			// 流程类的暂不计算迟到早退
			if (computeResult.getResultType() != AttendanceResult.TYPE_WORK) {
				return;
			}
			// 如果是上班时间，计算浮动
			if (upTime) {
				// 浮动打卡时间
				if (floatTime == null) {
					floatTime = 0L;
				}
				Calendar c = Calendar.getInstance();
				c.setTime(standardTime);
				c.add(Calendar.MINUTE, floatTime.intValue());

				double minite = DateUtil.betweenMinute(c.getTime(), validRecord.getCheckTime());
				// 上班迟到
				if (minite > 1) {
					computeResult.setOverTime((long)minite);
					computeResult.setCheckResult(AttendanceResult.RESULT_LATE);
				}
			} else {
				// 下班早退
				double minite = DateUtil.betweenMinute(validRecord.getCheckTime(), standardTime);
				if (minite > 1) {
					computeResult.setOverTime((long)minite);
					computeResult.setCheckResult(AttendanceResult.RESULT_EARLY);
				}
			}
		} else {
			// 未打卡
			computeResult.setCheckResult(AttendanceResult.RESULT_NONE);
		}
	}

	private AttendanceResult genResult(Long userId, Long shiftId, short seqKey, 
			Date attendanceDate, Date standardTime, Long businessId,Short type, boolean upTime) {
		AttendanceResult computeResult = new AttendanceResult();
		computeResult.setResultId(UniqueIdUtil.genId());
		computeResult.setUserId(userId);
		computeResult.setShiftId(shiftId);
		computeResult.setSeqKey(seqKey);
		computeResult.setAttendanceDate(attendanceDate);
		computeResult.setStandardTime(standardTime);
		computeResult.setBusinessId(businessId);
		computeResult.setResultType(type);
		computeResult.setRealTime(null);
		computeResult.setCheckType(upTime? AttendanceResult.TYPE_UP:AttendanceResult.TYPE_DWON);
//		if (shiftId != 0) {
//			computeResult.setCheckResult(AttendanceResult.RESULT_NORMAL);
//		} else {
//			computeResult.setCheckResult(AttendanceResult.RESULT_NONE);
//		}
		// 流程类的暂时都默认为正常
		computeResult.setCheckResult(AttendanceResult.RESULT_NORMAL);
		computeResult.setOverTime(null);
		return computeResult;
	}
}
