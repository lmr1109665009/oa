package com.suneee.ucp.mh.job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.suneee.Constants;
import com.suneee.core.scheduler.BaseJob;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.kaoqin.model.kaoqin.AnnualVacationSetting;
import com.suneee.kaoqin.service.kaoqin.AnnualVacationSettingService;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.mh.model.attendance.AttendanceShift;
import com.suneee.ucp.mh.model.attendance.AttendanceVacation;
import com.suneee.ucp.mh.model.attendance.VacationLog;
import com.suneee.ucp.mh.model.attendance.VacationRemain;
import com.suneee.ucp.mh.service.attendance.AttendanceShiftService;
import com.suneee.ucp.mh.service.attendance.AttendanceVacationService;
import com.suneee.ucp.mh.service.attendance.VacationRemainService;

/**
 * 假期结余计算
 * @author mikel
 *
 */
public class VacationComputeJob  extends BaseJob{

	@Override
	public void executeJob(JobExecutionContext context) throws Exception {
		Map<String, Object> params = context.getJobDetail().getJobDataMap();
		Date today = new Date();
		Object calculateDate = params.get("calculateDate");
		if (calculateDate != null) {
			try {
				today = DateFormatUtil.parseDate(calculateDate.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String annualCode = Constants.ANNUAL_VACATION_CODE;
		Object annualCodeObj = params.get("annualCode");
		if (annualCodeObj != null) {
			annualCode = annualCodeObj.toString();
		}
		
		// 获取年假信息
		AttendanceVacationService vacationService = AppUtil.getBean(AttendanceVacationService.class);
		AttendanceVacation annualVacation = vacationService.getVacationByCode(annualCode);
		// 年假信息为空，不见算年假
		if (annualVacation == null) {
			return;
		}
		
		// 获取年假设置信息
		AnnualVacationSettingService service = AppUtil.getBean(AnnualVacationSettingService.class);
		List<AnnualVacationSetting> settings = service.getAll();
		// 获取用户信息
		SysUserService userService = AppUtil.getBean(SysUserService.class);
		List<SysUser> users = userService.getAll();
		// 用户信息列表为空或者年假设置列表为空，则不计算年假
		if(users.isEmpty() || settings.isEmpty()){
			return;
		}
		
		// 入职时间
		Calendar duty = Calendar.getInstance();
		duty.setTime(today);
		// 工作时间
		Calendar work = Calendar.getInstance();
		work.setTime(today);
		int year = duty.get(Calendar.YEAR);
		
		VacationRemainService vacationRemainService = AppUtil.getBean(VacationRemainService.class);
		AttendanceShiftService attendanceShiftService = AppUtil.getBean(AttendanceShiftService.class);
		for (SysUser user : users) {
			// 入职年限假期天数
			long dutyDay = 0;
			// 工作年限假期天数
			long workDay = 0;
			
			// 查询用户年假结余信息
			VacationRemain remain = vacationRemainService.getByUserIdAndType(user.getUserId(), annualVacation.getId());
			// 如果已经计算过年假，则不重复计算
			if(remain != null && remain.getComputeYear() >= year){
				return;
			}
			
			// 获取用户班次信息
			AttendanceShift attendanceShift = attendanceShiftService.getShiftByUserId(user.getUserId());
			// 班次工作时长默认为8小时
			double workHour = 8;
			if(attendanceShift != null && attendanceShift.getWorkHour() > 0){
				workHour = attendanceShift.getWorkHour();
			}
			for (AnnualVacationSetting setting : settings) {
				// 年假设置的年限类型为入职年限
				if (setting.getYearType() == AnnualVacationSetting.TYPE_DUTY) {
					// 用户入职日期不为空，计算年假
					if(user.getEntryDate() != null){
						// 入职日期
						duty.setTime(user.getEntryDate());
						duty.add(Calendar.YEAR, setting.getYearLimit());
						// 如果满足年限要求
						if (duty.getTime().before(today)) {
							if (dutyDay < setting.getDays()) {
								dutyDay = setting.getDays();
							}
						}
					}
				}
				// 年假设置的年限类型为工作年限
				else if (setting.getYearType() == AnnualVacationSetting.TYPE_WORK) {
					// 根据工作日期计算年假添加数量
					if (user.getWorkDate() != null) {
						work.setTime(user.getWorkDate());
						work.add(Calendar.YEAR, setting.getYearLimit());
						// 如果满足年限要求
						if (work.getTime().before(today)) {
							if (workDay < setting.getDays()) {
								workDay = setting.getDays();
							}
						}
						
					}
				}
					
				// 添加的年假天数为工作年限和入职年限设定之和
				double addNums = dutyDay + workDay;
				// 年假是小时为单位，则乘以班次工作时长
				if (annualVacation.getUnit() == 2) {
					addNums *= workHour;
				}
				// 年假调整数大于0，调整年假结余并记录调整日志
				if (addNums > 0) {
					vacationRemainService.saveVacationRemain(user.getUserId(), annualVacation, addNums, 
							VacationLog.TYPE_SUPPLY, "年假自动添加", year, null);
				}
			}
		}
	}
}
