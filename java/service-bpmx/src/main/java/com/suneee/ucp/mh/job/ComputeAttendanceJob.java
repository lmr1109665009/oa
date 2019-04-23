package com.suneee.ucp.mh.job;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.suneee.core.scheduler.BaseJob;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.DateUtil;
import com.suneee.kaoqin.model.kaoqin.HolidaysSetting;
import com.suneee.kaoqin.service.kaoqin.AttendanceResultService;
import com.suneee.kaoqin.service.kaoqin.ExemmptSettingService;
import com.suneee.kaoqin.service.kaoqin.HolidaysSettingService;
import com.suneee.platform.model.system.SysUser;
import com.suneee.ucp.mh.model.attendance.AttendanceShift;
import com.suneee.ucp.mh.model.attendance.ShiftTime;
import com.suneee.ucp.mh.service.attendance.AttendanceShiftService;
import com.suneee.ucp.mh.service.attendance.ShiftTimeService;

/**
 * 计算考勤结果
 * @author mikel
 *
 */
public class ComputeAttendanceJob extends BaseJob{

	/**
     * @param context
     * @throws Exception 
     * @see BaseJob#executeJob(JobExecutionContext)
     */ 
	@Override
	public void executeJob(JobExecutionContext context) throws Exception {
		// 计算计算考勤的日期范围
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
		
		// 获取服务实例
		ExemmptSettingService exemmptSettingService = AppUtil.getBean(ExemmptSettingService.class);
		AttendanceResultService resultService = AppUtil.getBean(AttendanceResultService.class);
		AttendanceShiftService attendanceShiftService = AppUtil.getBean(AttendanceShiftService.class);
		// 获取免签人员
		List<SysUser> excludeUsers = exemmptSettingService.getExcludeUsers();
		// 获取所有的考勤班次
		List<AttendanceShift> shifts = attendanceShiftService.getAll();
					
		// 循环计算每一天的考勤
		while(startDay.compareTo(endDay) <= 0) {
			// 清楚当天的考勤结果
			resultService.clearAllComputedResults(startDay);
			
			for (AttendanceShift shift : shifts) {
				// 如果当前班次当天是非工作日，则不计算考勤，继续下一个班次的计算
				if (!attendanceShiftService.isWorkDay(startDay, shift.getShiftId())) {
					continue;
				}
				attendanceShiftService.computeShiftAttendanceOfDay(shift, startDay, excludeUsers);
			}
			
			startDay.add(Calendar.DAY_OF_MONTH, 1);
		}
	}
}
