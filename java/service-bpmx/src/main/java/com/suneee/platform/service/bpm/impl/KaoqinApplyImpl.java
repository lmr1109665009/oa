package com.suneee.platform.service.bpm.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.engine.IScript;
import com.suneee.core.util.DateUtil;
import com.suneee.kaoqin.model.kaoqin.*;
import com.suneee.kaoqin.service.kaoqin.*;
import org.springframework.stereotype.Service;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.engine.IScript;
import com.suneee.core.util.DateUtil;
import com.suneee.kaoqin.model.kaoqin.AttendanceResult;
import com.suneee.kaoqin.model.kaoqin.AttendanceShift;
import com.suneee.kaoqin.model.kaoqin.AttendanceVacation;
import com.suneee.kaoqin.model.kaoqin.HolidaysSetting;
import com.suneee.kaoqin.model.kaoqin.VacationLog;
import com.suneee.kaoqin.model.kaoqin.VacationRemain;
import com.suneee.kaoqin.service.kaoqin.AttendanceShiftService;
import com.suneee.kaoqin.service.kaoqin.AttendanceVacationService;
import com.suneee.kaoqin.service.kaoqin.HolidaysSettingService;
import com.suneee.kaoqin.service.kaoqin.ShiftDaySettingService;
import com.suneee.kaoqin.service.kaoqin.VacationLogService;
import com.suneee.kaoqin.service.kaoqin.VacationRemainService;
/**
 * 考勤相关流程的结束事件处理
 * @author liangqf
 *
 */
@Service
public class KaoqinApplyImpl implements IScript {
	@Resource
	private VacationRemainService vacationRemainService;
	@Resource
	private VacationLogService vacationLogService;
	@Resource
	private AttendanceShiftService shiftService;
	@Resource
	private AttendanceVacationService attendanceVacationService;
	@Resource
	private HolidaysSettingService holidaysSettingService;
	@Resource
	private ShiftDaySettingService daySettingService;
	
	/**
	 * 测试方法
	 * @param startUserId 流程发起人id
	 * @param flowRunId 流程运行id 即bpm_pro_ru中的runid
	 * @param businessKey 表单主键
	 */
	public void test(String startUserId,Long flowRunId,String businessKey,ProcessCmd cmd){
//		String s = startUserId;
//		Integer isBack = cmd.isBack();//0正常  1退回 2退回发起人
	}
	
	/**
	 * 请假流程结束，计算假期结余
	 * @param startUserId 流程发起人id
	 * @param businessKey 流程对应主表记录id
	 * @param cmd
	 */
	public void vacationRemain(String startUserId,String businessKey,ProcessCmd cmd, String vacationType, Date startDate, Date endDate, String unit, Double hours){
		Integer isBack = cmd.isBack();//0正常  1退回 2退回发起人
		Long recordId = Long.parseLong(businessKey);
		Long userId = Long.parseLong(startUserId);
		if(isBack == 0){
			AttendanceVacation vacation = attendanceVacationService.getById(Long.valueOf(vacationType));
			if (vacation == null) {
				return;
			}
			VacationRemain remain = vacationRemainService.getByUserIdAndType(userId, vacation.getId());
			if (remain == null) {
				remain = new VacationRemain();
				remain.setBaseUnit(vacation.getUnit());
				remain.setStatus((short)0);
				remain.setUserId(userId);
				remain.setVacationType(vacation.getId());
				remain.setRemained(0L);
			}
			VacationLog vacationLog = new VacationLog();
			vacationLog.setVacationType(remain.getVacationType());
			vacationLog.setUserId(remain.getUserId());
			vacationLog.setChangeType(VacationLog.TYPE_VACATOIN);
			vacationLog.setBeforeValue(remain.getRemained());
			vacationLog.setStatus((short)0);
			
			long ignoreHour = 0;//节假日忽略小时数
			if ("1".equals(unit)) {
				Calendar c = Calendar.getInstance();
				c.setTime(startDate);
				DateUtil.setStartDay(c);
				c.set(Calendar.MILLISECOND, 0);
				startDate = c.getTime();
				c.setTime(endDate);
				DateUtil.setEndDay(c);
				endDate = c.getTime();
				// 按天请假才计算并排除非工作日
				c.setTime(startDate);
				while (c.getTime().before(endDate)) {
					HolidaysSetting setting = holidaysSettingService.getHolidaysSettingByDay(c.getTime());
					List<AttendanceShift> workingShifts = shiftService.getWorkingShiftOfDay(c, userId);
					if (workingShifts.size() > 0) {
						AttendanceShift shift = workingShifts.get(0);
						if (!shiftService.isShiftAttendaceOfDay(c, shift, setting)) {
							ignoreHour += 24;// 非工作日则加一天
						}
					} else {
						ignoreHour += 24;// 非工作日则加一天
					}
					c.add(Calendar.DAY_OF_MONTH, 1);
				}
			}
			long hour = calculateHour(startDate, endDate) - ignoreHour;
			
			if(vacation.getUnit() == 1){//假期单位是天
				if ("1".equals(unit)) {
					hour /= 24;
				} else {
					// 实际中不应该走这一步，不足8小时部分算一天
					hour /= 8; 
					hour += (hour % 8 == 0 ? 0 : 1); 
				}
				remain.setRemained(remain.getRemained()-hour);
			}else{
				if ("1".equals(unit)) {
					hour /= 3;
				}
				remain.setRemained(remain.getRemained()-hour);
			}
			vacationLog.setAfterValue(remain.getRemained());
			vacationLog.setChangeValue(vacationLog.getAfterValue() - vacationLog.getBeforeValue());
			vacationLog.setMemo("请假扣减");
			vacationLog.setUpdatetime(new Date());
			remain.setUpdatetime(new Date());
			vacationRemainService.save(remain);
			vacationLogService.save(vacationLog);
			
			shiftService.computeUserProcessResult(userId, recordId, startDate, endDate, AttendanceResult.TYPE_LEAVE);
		}
	}
	
	/**
	 * 匹配假期要忽略的小时数
	 * @param setting
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	protected long matchHours(HolidaysSetting setting, Date startDate, Date endDate) {
		// 如果申请开始时间在节假日开始时间之后
		if (setting.getStartDate().before(startDate)) {
			// 申请结束时间在节假日结束时间之后
			if (setting.getEndDate().before(endDate)) {
				Calendar c = Calendar.getInstance();
				c.setTime(setting.getEndDate());
				DateUtil.setEndDay(c);
				return calculateHour(startDate, c.getTime());
			} else {
				// 申请时间都在节假日内
				return calculateHour(startDate, endDate);
			}
		} else { // 申请开始时间在节假日开始时间之前
			// 申请结束时间在节假日结束时间之后
			if (setting.getEndDate().before(endDate)) {
				Calendar c = Calendar.getInstance();
				c.setTime(setting.getEndDate());
				DateUtil.setEndDay(c);
				// 交叉时间
				return calculateHour(setting.getStartDate(), c.getTime());
			} else {
				return calculateHour(setting.getStartDate(), endDate);
			}
		}
	}

	/**
	 * 加班申请通过处理
	 * @param startUserId 流程发起人id
	 * @param businessKey 流程对应主表记录id
	 * @param cmd
	 */
	public void overtimeAccept(String startUserId,String businessKey,ProcessCmd cmd, Date startTime, Date endTime, String unit, Double hours){
		Integer isBack = cmd.isBack();//0正常  1退回 2退回发起人
		Long recordId = Long.parseLong(businessKey);
		Long userId = Long.parseLong(startUserId);
		if(isBack == 0){
			AttendanceVacation vacation = attendanceVacationService.getVacationByCode("dxj");
			if (vacation == null) {
				return;
			}
			VacationRemain remain = vacationRemainService.getByUserIdAndType(userId, vacation.getId());
			if (remain == null) {
				remain = new VacationRemain();
				remain.setBaseUnit(vacation.getUnit());
				remain.setStatus((short)0);
				remain.setUserId(userId);
				remain.setVacationType(vacation.getId());
				remain.setRemained(0L);
			}
			VacationLog vacationLog = new VacationLog();
			vacationLog.setVacationType(remain.getVacationType());
			vacationLog.setUserId(remain.getUserId());
			vacationLog.setChangeType(VacationLog.TYPE_OVERTIME);
			vacationLog.setBeforeValue(remain.getRemained());
			vacationLog.setStatus((short)0);

			if ("1".equals(unit)) {
				Calendar c = Calendar.getInstance();
				c.setTime(startTime);
				DateUtil.setStartDay(c);
				startTime = c.getTime();
				c.setTime(endTime);
				DateUtil.setEndDay(c);
				endTime = c.getTime();
			}
			long hour = calculateHour(startTime, endTime);
			
			long overtimeHour = 0;
			if(vacation.getUnit() == 1){//假期单位是天
				if ("1".equals(unit)) {
					hour /= 24;
				} else {
					hour /= 8; // 实际中不应该走这一步，不足8小时部分直接忽略
				}
				overtimeHour = hour * 8;
				remain.setRemained(remain.getRemained()+hour);
			}else{
				if ("1".equals(unit)) {
					hour /= 3;
				}
				overtimeHour = hour;
				remain.setRemained(remain.getRemained()+hour);
			}
			vacationLog.setAfterValue(remain.getRemained());
			vacationLog.setChangeValue(vacationLog.getAfterValue() - vacationLog.getBeforeValue());
			vacationLog.setMemo("加班累计");
			vacationLog.setUpdatetime(new Date());
			remain.setUpdatetime(new Date());
			vacationRemainService.save(remain);
			vacationLogService.save(vacationLog);
			
			shiftService.generateOvertimeResult(userId, recordId, startTime, endTime, overtimeHour);
		}
	}
	
	/**
	 * 签卡申请通过处理
	 * @param startUserId 流程发起人id
	 * @param businessKey 流程对应主表记录id
	 * @param cmd
	 */
	public void signApplyAccept(String startUserId,String businessKey,ProcessCmd cmd, Date signTime){
		Integer isBack = cmd.isBack();//0正常  1退回 2退回发起人
		Long recordId = Long.parseLong(businessKey);
		Long userId = Long.parseLong(startUserId);
		if(isBack == 0){
			shiftService.computeSignResult(userId, recordId, signTime);
		}
	}
	
	/**
	 * 外出申请通过处理
	 * @param startUserId 流程发起人id
	 * @param businessKey 流程对应主表记录id
	 * @param cmd
	 */
	public void goOutApplyAccept(String startUserId,String businessKey,ProcessCmd cmd, Date startTime, Date endTime, String unit){
		Integer isBack = cmd.isBack();//0正常  1退回 2退回发起人
		Long recordId = Long.parseLong(businessKey);
		Long userId = Long.parseLong(startUserId);
		if(isBack == 0){
			if ("1".equals(unit)) {
				Calendar c = Calendar.getInstance();
				c.setTime(startTime);
				DateUtil.setStartDay(c);
				startTime = c.getTime();
				c.setTime(endTime);
				DateUtil.setEndDay(c);
				endTime = c.getTime();
			}
			shiftService.computeUserProcessResult(userId, recordId, startTime, endTime, AttendanceResult.TYPE_OUT);
		}
	}
	
	/**
	 * 出差申请通过处理
	 * @param startUserId 流程发起人id
	 * @param businessKey 流程对应主表记录id
	 * @param cmd
	 */
	public void businessOutAccept(String startUserId,String businessKey,ProcessCmd cmd, Date startDate, Date endDate){
		Integer isBack = cmd.isBack();//0正常  1退回 2退回发起人
		Long recordId = Long.parseLong(businessKey);
		Long userId = Long.parseLong(startUserId);
		if(isBack == 0){
			Calendar c = Calendar.getInstance();
			c.setTime(startDate);
			DateUtil.setStartDay(c);
			startDate = c.getTime();
			c.setTime(endDate);
			DateUtil.setEndDay(c);
			endDate = c.getTime();
			shiftService.computeUserProcessResult(userId, recordId, startDate, endDate, AttendanceResult.TYPE_BUSINESS);
		}
	}
	
	/**
	 * 计算时间段之间的小时数
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private long calculateHour(Date startDate, Date endDate) {
		return Math.round(DateUtil.betweenHour(startDate, endDate));
	}
}
