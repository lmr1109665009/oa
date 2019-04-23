package com.suneee.kaoqin.service.kaoqin;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.dao.kaoqin.AttendanceShiftDao;
import com.suneee.kaoqin.dao.kaoqin.ExemmptSettingDao;
import com.suneee.kaoqin.dao.kaoqin.ShiftCalendarDao;
import com.suneee.kaoqin.dao.kaoqin.ShiftTimeDao;
import com.suneee.kaoqin.model.kaoqin.*;
import com.suneee.platform.model.system.SysUser;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 *<pre>
 * 对象功能:考勤班次表 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:03:50
 *</pre>
 */
@Service
public class AttendanceShiftService extends BaseService<AttendanceShift>
{
	@Resource
	private AttendanceShiftDao dao;
	@Resource
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
	
	public AttendanceShiftService()
	{
	}
	
	@Override
	protected IEntityDao<AttendanceShift, Long> getEntityDao()
	{
		return dao;
	}
	
	
	
	/**
	 * 流程处理器方法 用于处理业务数据
	 * @param cmd
	 * @throws Exception
	 */
	public void processHandler(ProcessCmd cmd)throws Exception{
		Map data=cmd.getFormDataMap();
		if(BeanUtils.isNotEmpty(data)){
			String json=data.get("json").toString();
			AttendanceShift attendanceShift=getAttendanceShift(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				attendanceShift.setShiftId(genId);
				this.add(attendanceShift);
			}else{
				attendanceShift.setShiftId(Long.parseLong(cmd.getBusinessKey()));
				this.update(attendanceShift);
			}
			cmd.setBusinessKey(attendanceShift.getShiftId().toString());
		}
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
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			attendanceShift.setShiftId(id);
			int minutes = addShiftTimes(attendanceShift);
			attendanceShift.setWorkHour((short)(minutes/60));
			addShiftCalendars(attendanceShift);
			attendanceShift.setStatus(AttendanceShift.STATUS_NORMAL);
			attendanceShift.setCreateby(ContextUtil.getCurrentUserId());
			attendanceShift.setCreatetime(new Date());
			this.add(attendanceShift);
		}
		else{
			// 先删除数据再重新创建
			timeDao.removeTimeByShiftId(attendanceShift.getShiftId());
			int minutes = addShiftTimes(attendanceShift);
			attendanceShift.setWorkHour((short)(minutes/60));
			calendarDao.removeByShiftId(attendanceShift.getShiftId());
			addShiftCalendars(attendanceShift);
			this.update(attendanceShift);
		}
	}
	
	/**
	 * 添加班次时间段记录,返回时间段总长度（小时）
	 * @param shift
	 */
	private int addShiftTimes(AttendanceShift shift) {
		String timeList = shift.getTimeList();
		int timeTotal = 0;
		if (timeList != null) {
			String timeSections[] = timeList.split(",");
			for(String timeSection: timeSections) {
				String timeSE[] = timeSection.split("-");
				if (timeSE.length == 2) {
					ShiftTime time = new ShiftTime();
					time.setId(UniqueIdUtil.genId());
					time.setShiftId(shift.getShiftId());
					time.setStartTime(DateUtil.parseDate(timeSE[0]));
					time.setEndTime(DateUtil.parseDate(timeSE[1]));
					timeTotal += (int) DateUtil.betweenMinute(time.getStartTime(), time.getEndTime());
					time.setRestTime(0L);
					time.setStatus(ShiftTime.STATUS_NORMAL);
					time.setCreateby(ContextUtil.getCurrentUserId());
					time.setCreatetime(new Date());
					timeDao.add(time);
				}
			}
		}
		return timeTotal;
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
				ShiftCalendar calendar = genShiftCalendar(shift.getShiftId());
				calendar.setWeek(Short.valueOf(day));
				calendar.setDayType(ShiftCalendar.TYPE_RESK);
				calendarDao.add(calendar);
				shift.getReskWeeks()[calendar.getWeek()] = 1;
			}
			for (int i =1; i<shift.getReskWeeks().length; i++) {
				if (shift.getReskWeeks()[i] == 0){
					ShiftCalendar calendar = genShiftCalendar(shift.getShiftId());
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
	 * 
	 * @param
	 * @return
	 */
	public List<AttendanceShift> getWorkingShiftOfDay(Calendar day, Long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("week", day.get(Calendar.DAY_OF_WEEK));
		params.put("day", day.getTime());
		if (userId != null) {
			params.put("userId", userId);
		}
		return dao.getBySqlKey("getWorkingShiftOfDay", params);
	}

	/**
	 * 计算某个班次某天是否要进行考勤
	 * @param day 日期
	 * @param shift 班次
	 * @param setting 节假日设置
	 * @return
	 */
	public boolean isShiftAttendaceOfDay(Calendar day, AttendanceShift shift, HolidaysSetting setting) {
		ShiftDaySetting daySetting = daySettingService.getShiftSettingByDay(shift.getShiftId(), day.getTime());
		// 如果当天是节假日，且该班次没有单日的上班安排，则不计算该班次当天的考勤
		if (daySetting == null && setting != null) {
			return false;
		}
		// 如果被设定为休息日，则不计算
		if (daySetting != null && daySetting.getScheduleType() == ShiftDaySetting.TYPE_REST){
			return false;
		}
		return true;
	}

	/**
	 * 计算指某天的流程类考勤数据
	 * @param
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
	 * 生成用户加班申请通过后的考勤记录
	 * @param userId
	 * @param startTime
	 * @param endTime
	 */
	public void generateOvertimeResult(Long userId, Long businessId, Date startTime, Date endTime, long overtimeHour) {
		
		ExemmptSetting exemmptSetting = exemmptSettingDao.getByTargetId(userId);
		// 免签人员不计算考勤
		if (exemmptSetting != null) {
			return;
		}
		AttendanceResult result = genResult(userId, 0L, (short)0, startTime, startTime, businessId, AttendanceResult.TYPE_OVERTIME, true);
		result.setOverTime(overtimeHour);
		resultService.add(result);
		result = genResult(userId, 0L, (short)1, endTime, endTime, businessId, AttendanceResult.TYPE_OVERTIME, false);
		result.setOverTime(overtimeHour);
		resultService.add(result);
	}

	/**
	 * 计算某个班次某天的考勤结果
	 * @param shift
	 * @param day
	 */
	public void computeShiftAttendanceOfDay(AttendanceShift shift, Calendar day, List<SysUser> excludeUsers) {

		// 获取班次的考勤人员列表
		List<SysUser> users = getShiftUsers(shift.getShiftId());
		users.removeAll(excludeUsers);
		List<ShiftTime> times = timeDao.getTimesByShiftId(shift.getShiftId());
		for (SysUser user : users) {
			computeUserAttendanceOfDay(shift, times, user.getUserId(), day);
		}
		//computeProcessResult(day);
	}
	
	/**
	 * 计算某个用户的某个班次某天的考勤结果
	 * @param shift
	 * @param
	 * @param day
	 */
	public void computeUserAttendanceOfDay(AttendanceShift shift, List<ShiftTime> times, Long userId, Calendar day) {

		if (times.size() == 0) {
			return;
		}
		Long shiftId = shift.getShiftId();
		short seqKey = 0;
		for (int i = 0; i < times.size(); i++ ) {
			ShiftTime time = times.get(i);
			// 标准打卡时间
			Date standardTimeStart = computeStandardTime(time.getStartTime(), day);
			Date standardTimeEnd = computeStandardTime(time.getEndTime(), day);
			// 如果下班时间在上班时间之前，则是第二天
			if (standardTimeEnd.before(standardTimeStart)) {
				Calendar c = Calendar.getInstance();
				c.setTime(standardTimeEnd);;
				c.add(Calendar.DAY_OF_MONTH, 1);
				standardTimeEnd = c.getTime();
			}
			Date upScopes[] = computeTimeValidScope(shift, standardTimeStart, standardTimeEnd, true);
			Date downScopes[] = computeTimeValidScope(shift, standardTimeStart, standardTimeEnd, false);
			// 获取用户的打卡记录
			List<AttendanceRecord> records = recordService.getRecordList(userId, upScopes[0], downScopes[1]);
			// 获取用户的已有考勤结果
			List<AttendanceResult> results = resultService.getResultList(userId, shiftId, upScopes[0], downScopes[1]);
			AttendanceResult up = computeTimeResult(userId, shift, records, results, standardTimeStart, upScopes, day, true);
			AttendanceResult down = computeTimeResult(userId, shift, records, results, standardTimeEnd, downScopes, day, false);
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
				up.setSeqKey(seqKey++);
				if (up.getResultId() == null) {
					up.setResultId(UniqueIdUtil.genId());
					resultService.add(up);
				} else {
					resultService.update(up);
				}
			}
			if (down != null) {
				down.setSeqKey(seqKey++);
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
	 * 计算某个时间点的考勤结果
	 * @return
	 */
	private AttendanceResult computeTimeResult(Long userId, AttendanceShift shift, List<AttendanceRecord> records, List<AttendanceResult> results, 
			Date standardTime, Date scopes[], Calendar day, boolean upTime) {
		// 考勤结果
		AttendanceResult computeResult = null;
		for (AttendanceResult result : results) {
			if (result.getStandardTime().equals(standardTime)) {
				computeResult = result;
				break;
			}
		}
		if (computeResult != null) {
			// 如果不需要考勤
			if (computeResult.getResultType() > AttendanceResult.TYPE_OVERTIME) {
				return null;
			}
		} else {
			computeResult = new AttendanceResult();
			computeResult.setUserId(userId);
			computeResult.setShiftId(shift.getShiftId());
			computeResult.setAttendanceDate(day.getTime());
			computeResult.setStandardTime(standardTime);
			computeResult.setCheckType(upTime ? AttendanceResult.TYPE_UP : AttendanceResult.TYPE_DWON);
			computeResult.setResultType(AttendanceResult.TYPE_WORK);
		}
		if (computeResult.getRecordId() == null) {
			matchRecord(records, standardTime, scopes, upTime, computeResult, shift.getFloatTime());
		}
		return computeResult;
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
		for (AttendanceRecord record : records) {
			if (!computeResult.getUserId().equals(record.getUserId())) {
				continue;
			}
			// 打卡时间
			Date checkTime = record.getCheckTime();
			if (checkTime.after(scopesFirst[0]) && checkTime.before(scopesFirst[1])) {
				// 只匹配第一条满足条件的记录
				validRecord = record;
				break;
			}
			// 如果刚好是临界打卡时间
			if (checkTime.equals(scopesFirst[0]) || checkTime.equals(scopesFirst[1])) {
				validRecord = record;
				break;
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

	/**
	 * 计算签卡的考勤结果
	 * @param userId
	 * @param businessId
	 * @param signTime
	 */
	public void computeSignResult(Long userId, Long businessId, Date signTime) throws IOException {
		Calendar c = Calendar.getInstance();
		c.setTime(signTime);
		DateUtil.setStartDay(c);
		QueryFilter filter = new QueryFilter(new JSONObject());
		filter.addFilter("userId", userId);
		filter.addFilter("attendanceDate", c.getTime());
		// 获取用户班次
		List<AttendanceShift> shifts = getShiftByUserDay(filter);
		if (shifts.size() == 0) {
			return;
		}
		AttendanceShift shift = shifts.get(0);
		Long shiftId = shift.getShiftId();
		// 获取用户的已有考勤结果
		List<AttendanceResult> oldResults = resultService.getResultList(userId, shiftId, c.getTime());
		// 还原考勤结果
		for (AttendanceResult result : oldResults) {
			if (result.getResultType() == AttendanceResult.TYPE_WORK) {
				resultService.delById(result.getResultId());
			}
			if (result.getShiftId() == 0) {
				result.setRealTime(null);
				result.setCheckResult(AttendanceResult.RESULT_NONE);
				result.setRecordId(null);
				result.setOverTime(null);
				resultService.update(result);
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
		List<ShiftTime> times = timeDao.getTimesByShiftId(shift.getShiftId());
		computeUserAttendanceOfDay(shift, times, userId, day);
		computeProcessResult(day);
	}

	/**
	 * 计算用户通过流程申请后的考勤结果
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @param type （外出、出差、请假）
	 */
	public void computeUserProcessResult(Long userId, Long businessId, Date startTime, Date endTime, short type) throws IOException {
		
		ExemmptSetting exemmptSetting = exemmptSettingDao.getByTargetId(userId);
		// 免签人员不计算考勤
		if (exemmptSetting != null) {
			return;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(startTime);
		DateUtil.setStartDay(c);
		while(true) {
			List<AttendanceShift> workingShifts = getWorkingShiftOfDay(c, userId);
			if (workingShifts.size() > 0) {
				AttendanceShift shift = workingShifts.get(0);
				boolean needComputing = true;
				if (type != AttendanceResult.TYPE_OVERTIME) {
					needComputing = false;
				}
				// 获取是否有免签节假日设定
				HolidaysSetting setting = holidaysSettingService.getHolidaysSettingByDay(c.getTime());
				if (!isShiftAttendaceOfDay(c, shift, setting)) {
					needComputing = false;
				}
				if (needComputing) {
					Long shiftId = shift.getShiftId();
					// 获取用户的已有考勤结果（补请假的情况）
					List<AttendanceResult> results = resultService.getResultList(userId, shiftId, c.getTime(), endTime);
					for (AttendanceResult result : results) {
						// 删除旧的打卡记录
						resultService.delById(result.getResultId());
					}
					List<ShiftTime> times = timeDao.getTimesByShiftId(shift.getShiftId());
					short seqKey = 0;
					for (ShiftTime time : times) {
						Date startStandardTime = computeStandardTime(time.getStartTime(), c);
						Date endStandardTime = computeStandardTime(time.getEndTime(), c);
						// 如果下班时间在上班时间之前，则是第二天
						if (endStandardTime.before(startStandardTime)) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(endStandardTime);;
							calendar.add(Calendar.DAY_OF_MONTH, 1);
							endStandardTime = calendar.getTime();
						}
						AttendanceResult result = genResult(userId, shiftId, seqKey++, c.getTime(), startStandardTime, businessId, type, true);
						saveResult(startTime, endTime, result);
						// 计算流程需要的打卡结果
						if (startTime.after(startStandardTime) && startTime.before(endStandardTime)) {
							result = genResult(userId, 0L, seqKey++, c.getTime(), startTime, businessId, type, false);
							resultService.add(result);
						}
						if (endTime.after(startStandardTime) && endTime.before(endStandardTime)) {
							result = genResult(userId, 0L, seqKey++, c.getTime(), endTime, businessId, type, true);
							resultService.add(result);
						}
						result = genResult(userId, shiftId, seqKey++, c.getTime(), endStandardTime, businessId, type, false);
						saveResult(startTime, endTime, result);
					}
					// 补请假时重新计算当天的考勤信息
					if (c.before(Calendar.getInstance())) {
						computeUserAttendanceOfDay(shift, times, userId, c);
					}
				}
			}
			c.add(Calendar.DAY_OF_MONTH, 1);
			if (c.getTime().after(endTime)) {
				break;
			}
		}
	}
	
	private void saveResult(Date startTime, Date endTime, AttendanceResult result) {
		Date standardTime = result.getStandardTime();
		if (standardTime.compareTo(startTime) >=0 && standardTime.compareTo(endTime) <= 0) {
			resultService.add(result);
		}
	}
	
	/**
	 * 计算某个打卡时间点在当天的标准打卡时间
	 * @param time
	 * @param day
	 * @return
	 */
	private Date computeStandardTime(Date time, Calendar day) {
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		setSameCalendarDate(day, c);
		return c.getTime();
	}
	
	/**
	 * 计算某个打卡时间点的有效打卡时间范围,返回开始和结束时间
	 * @param shift
	 * @param
	 * @return
	 */
	private Date[] computeTimeValidScope(AttendanceShift shift, Date startStandardTime, Date endStandardTime, boolean upTime) {
		Long validScope = shift.getValidScope();
		Date scopes[] = new Date[2];
		Calendar c = Calendar.getInstance();
		if (validScope != null && validScope > 0) {
			if (upTime) {
				c.setTime(startStandardTime);
				c.add(Calendar.MINUTE, -validScope.intValue());
				scopes[0] = c.getTime();
				c.setTime(startStandardTime);
				scopes[1] = endStandardTime;
			} else {
				scopes[0] = startStandardTime;
				c.setTime(endStandardTime);
				c.add(Calendar.MINUTE, validScope.intValue());
				scopes[1] = c.getTime();
			}
		} else {
			// 如果是上班
			if (upTime) {
				c.setTime(startStandardTime);
				scopes[0] = DateUtil.setStartDay(c).getTime();
				scopes[1] = endStandardTime;
			} else {
				scopes[0] = startStandardTime;
				c.setTime(endStandardTime);
				scopes[1] = DateUtil.setEndDay(c).getTime();
			}
		}
		return scopes;
	}
	
	/**
	 * 设置两个日历对象为同一个日期
	 * @param from
	 * @param to
	 */
	private void setSameCalendarDate(Calendar from, Calendar to) {
		to.set(Calendar.YEAR, from.get(Calendar.YEAR));
		to.set(Calendar.MONTH, from.get(Calendar.MONTH));
		to.set(Calendar.DAY_OF_MONTH, from.get(Calendar.DAY_OF_MONTH));
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
	
}
