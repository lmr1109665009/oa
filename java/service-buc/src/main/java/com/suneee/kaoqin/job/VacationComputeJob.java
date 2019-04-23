package com.suneee.kaoqin.job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.suneee.core.scheduler.BaseJob;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.kaoqin.model.kaoqin.AnnualVacationSetting;
import com.suneee.kaoqin.model.kaoqin.AttendanceVacation;
import com.suneee.kaoqin.model.kaoqin.VacationLog;
import com.suneee.kaoqin.model.kaoqin.VacationRemain;
import org.quartz.JobExecutionContext;

import com.suneee.Constants;
import com.suneee.core.scheduler.BaseJob;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.kaoqin.model.kaoqin.AnnualVacationSetting;
import com.suneee.kaoqin.model.kaoqin.AttendanceVacation;
import com.suneee.kaoqin.model.kaoqin.VacationLog;
import com.suneee.kaoqin.model.kaoqin.VacationRemain;
import com.suneee.kaoqin.service.kaoqin.AnnualVacationSettingService;
import com.suneee.kaoqin.service.kaoqin.AttendanceVacationService;
import com.suneee.kaoqin.service.kaoqin.VacationLogService;
import com.suneee.kaoqin.service.kaoqin.VacationRemainService;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysUserService;

/**
 * 假期结余计算
 * @author mikel
 *
 */
public class VacationComputeJob  extends BaseJob {

	@Override
	public void executeJob(JobExecutionContext context) throws Exception {
		Map<String, Object> params = context.getJobDetail().getJobDataMap();
		VacationLogService vacationLogService = AppUtil.getBean(VacationLogService.class);
		VacationRemainService remainService = AppUtil.getBean(VacationRemainService.class);
		Date today = new Date();
		// 清空过期的假期结余数
		/*List<VacationRemain> remains = remainService.getOverdueRemains(today);
		for (VacationRemain remain : remains) {
			
			VacationLog vacationLog = new VacationLog();
			vacationLog.setVacationType(remain.getVacationType());
			vacationLog.setUserId(remain.getUserId());
			vacationLog.setChangeType(VacationLog.TYPE_CLEAN);
			vacationLog.setChangeValue(remain.getRemained());
			vacationLog.setBeforeValue(remain.getRemained());
			vacationLog.setAfterValue(0L);
			vacationLog.setMemo("过期清零");
			vacationLog.setUpdatetime(today);
			
			remain.setRemained(0L);
			remain.setValidDate(null);
			remainService.update(remain);
			vacationLogService.save(vacationLog);
		}*/
		// 重新计算年假天数
//		String resetDate = Constants.ANNUAL_RESET_DATE;
//		Object resetDateObj = params.get("resetDate");
//		if (resetDateObj != null) {
//			resetDate = resetDateObj.toString();
//		}
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
		String mmdd = DateFormatUtil.format(today, "MM-dd");
//		if (mmdd.equals(resetDate)) {
			AttendanceVacationService vacationService = AppUtil.getBean(AttendanceVacationService.class);
			AttendanceVacation annualVacation = vacationService.getVacationByCode(annualCode);
			if (annualVacation != null) {
				AnnualVacationSettingService service = AppUtil.getBean(AnnualVacationSettingService.class);
				List<AnnualVacationSetting> settings = service.getAll();
				SysUserService userService = AppUtil.getBean(SysUserService.class);
				List<SysUser> users = userService.getAll();

				Calendar duty = Calendar.getInstance();
				duty.setTime(today);
				Calendar work = Calendar.getInstance();
				work.setTime(today);
				for (SysUser user : users) {
					long dutyDay = 0;
					long workDay = 0;
					for (AnnualVacationSetting setting : settings) {
						// 根据入职日期计算年假添加数量
						if (user.getEntryDate() != null) {
							String entryDate = DateFormatUtil.format(user.getEntryDate(), "MM-dd");
							if (mmdd.equals(entryDate)) {
								duty.setTime(user.getEntryDate());
								if (setting.getYearType() == AnnualVacationSetting.TYPE_DUTY) {
									duty.add(Calendar.YEAR, setting.getYearLimit());
									// 如果满足年限要求
									if (duty.getTime().before(today)) {
										if (dutyDay < setting.getDays()) {
											dutyDay = setting.getDays();
										}
									}
								}
								// 根据工作日期计算年假添加数量
								if (user.getWorkDate() != null) {
									work.setTime(user.getWorkDate());
									if (setting.getYearType() == AnnualVacationSetting.TYPE_WORK) {
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
								long addNums = dutyDay + workDay;
								// 年假是小时为单位，乘以8
								if (annualVacation.getUnit() == 2) {
									addNums *= 8;
								}
								// 大于0才加
								if (addNums > 0) {
									VacationRemain remain = remainService.getByUserIdAndType(user.getUserId(), annualVacation.getId());
									VacationLog vacationLog = new VacationLog();
									
									if (remain == null) {
										remain = new VacationRemain();
										remain.setId(UniqueIdUtil.genId());
										remain.setBaseUnit(annualVacation.getUnit());
										remain.setRemained(addNums);
										remain.setStatus((short)0);
										remain.setUpdatetime(today);
										remain.setUserId(user.getUserId());
										remain.setVacationType(annualVacation.getId());
										// remain.setValidDate(calendar.getTime());
										remainService.add(remain);
										vacationLog.setBeforeValue(0L);
										vacationLog.setAfterValue(addNums);
									} else {
										// 加上年假数量
										remain.setRemained(remain.getRemained() + addNums);
										remain.setUpdatetime(today);
										// remain.setValidDate(calendar.getTime());
										remainService.update(remain);
										vacationLog.setAfterValue(remain.getRemained());
									}
									vacationLog.setVacationType(annualVacation.getId());
									vacationLog.setUserId(user.getUserId());
									vacationLog.setChangeType(VacationLog.TYPE_SUPPLY);
									vacationLog.setChangeValue(addNums);
									vacationLog.setStatus((short)0);
									vacationLog.setMemo("年假自动添加");
									vacationLog.setUpdatetime(today);
									vacationLogService.save(vacationLog);
								}
							}
						}
					}
				}
			}
//		}
	}

}
