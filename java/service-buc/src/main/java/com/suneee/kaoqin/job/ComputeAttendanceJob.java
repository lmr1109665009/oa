package com.suneee.kaoqin.job;

import com.suneee.core.scheduler.BaseJob;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.DateUtil;
import com.suneee.kaoqin.model.kaoqin.AttendanceShift;
import com.suneee.kaoqin.model.kaoqin.HolidaysSetting;
import com.suneee.kaoqin.service.kaoqin.AttendanceResultService;
import com.suneee.kaoqin.service.kaoqin.AttendanceShiftService;
import com.suneee.kaoqin.service.kaoqin.ExemmptSettingService;
import com.suneee.kaoqin.service.kaoqin.HolidaysSettingService;
import com.suneee.platform.model.system.SysUser;
import org.quartz.JobExecutionContext;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 计算考勤结果
 * @author mikel
 *
 */
public class ComputeAttendanceJob extends BaseJob {

	/**
     * @param context
     * @throws Exception 
     * @see BaseJob#executeJob(JobExecutionContext)
     */ 
	@Override
	public void executeJob(JobExecutionContext context) throws Exception {
		
		Map<String, Object> params = context.getJobDetail().getJobDataMap();
		Calendar startDay = Calendar.getInstance();
		Calendar endDay = Calendar.getInstance();
		Object startDate = params.get("startDate");
		if (startDate != null) {
			try {
				startDay.setTime(DateUtil.parseDate(startDate.toString()));
			} catch (Exception e) {
			}
		} else {
			Object backDays = params.get("backDays");
			if (backDays != null) {
				try {
					startDay.add(Calendar.DAY_OF_MONTH, -Integer.valueOf(backDays.toString()));
				} catch (Exception e) {
				}
			} else {
				startDay.add(Calendar.DAY_OF_MONTH, -1);
			}
		}
		Object endDate = params.get("endDate");
		if (endDate != null) {
			try {
				endDay.setTime(DateUtil.parseDate(endDate.toString()));
			} catch (Exception e) {
			}
		}
		DateUtil.setStartDay(startDay);
		DateUtil.setStartDay(endDay);
		
		while(startDay.compareTo(endDay) <= 0) {
			computeAttendanceOfDay(startDay);
			startDay.add(Calendar.DAY_OF_MONTH, 1);
		}
	}
	
	private void computeAttendanceOfDay(Calendar day) {
		
		AttendanceResultService resultService = AppUtil.getBean(AttendanceResultService.class);
		resultService.clearAllComputedResults(day);
		
		// 获取当日有效的考勤班次列表
		AttendanceShiftService attendanceShiftService = AppUtil.getBean(AttendanceShiftService.class);
		List<AttendanceShift> shifts = attendanceShiftService.getWorkingShiftOfDay(day, null);
		// 获取是否有免签节假日设定
		HolidaysSettingService holidaysSettingService = AppUtil.getBean(HolidaysSettingService.class);
		HolidaysSetting setting = holidaysSettingService.getHolidaysSettingByDay(day.getTime());
		
		ExemmptSettingService exemmptSettingService = AppUtil.getBean(ExemmptSettingService.class);
		List<SysUser> excludeUsers = exemmptSettingService.getExcludeUsers();
		
		for (AttendanceShift shift : shifts) {
			if (!attendanceShiftService.isShiftAttendaceOfDay(day, shift, setting)) {
				continue;
			}
			attendanceShiftService.computeShiftAttendanceOfDay(shift, day, excludeUsers);
		}
		
		// 获取加班人员
//		List<SysUser> overtimeUsers = resultService.getOvertimeUsersOfDay(day.getTime());
//		overtimeUsers.removeAll(excludeUsers);
		// 计算加班、出差、请假、外出人员考勤结果
		attendanceShiftService.computeProcessResult(day);
	}
}
