/**
 * 
 */
package com.suneee.ucp.mh.service.script;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.engine.IScript;
import com.suneee.core.util.DateUtil;
import com.suneee.kaoqin.model.kaoqin.AttendanceResult;
import com.suneee.kaoqin.service.kaoqin.AttendanceRecordService;
import com.suneee.kaoqin.service.kaoqin.HolidaysSettingService;
import com.suneee.ucp.mh.model.attendance.AttendanceShift;
import com.suneee.ucp.mh.model.attendance.AttendanceVacation;
import com.suneee.ucp.mh.model.attendance.VacationLog;
import com.suneee.ucp.mh.service.attendance.AttendanceShiftService;
import com.suneee.ucp.mh.service.attendance.AttendanceVacationService;
import com.suneee.ucp.mh.service.attendance.ShiftTimeService;
import com.suneee.ucp.mh.service.attendance.VacationRemainService;

/**
 * 考勤相关流程结束触发事件
 * @author xiongxianyun
 *
 */
@Service
public class AttendanceProcessScriptImpl implements IScript{
	@Resource(name="ucpAttendanceVacationService")
	private AttendanceVacationService attendanceVacationService;
	@Resource(name="ucpAttendanceShiftService")
	private AttendanceShiftService attendanceShiftService;
	@Resource
	private HolidaysSettingService holidaysSettingService;
	@Resource(name="ucpShiftTimeService")
	private ShiftTimeService shiftTimeService;
	@Resource(name="ucpVacationRemainService")
	private VacationRemainService vacationRemainService;
	@Resource
	private AttendanceRecordService attendanceRecordService;
	
	/**
	 * 请假流程结束，计算假期结余与考勤
	 * @param startUserId 流程发起人ID
	 * @param businessKey 流程对应主表记录id
	 * @param cmd 流程执行命令实体
	 * @param startDate 申请开始时间
	 * @param endDate 申请结束时间
	 * @param vacationType 假期类型
	 */
	public void vacation(String startUserId,String businessKey,ProcessCmd cmd, Date startDate, Date endDate, String vacationType){
		// 0-正常，1-退回，2-退回发起人
		Integer isBack = cmd.isBack();
		// 如果流程被退回，则不计算假期结余与考勤
		if(isBack != 0){
			return;
		}
		
		// 根据假期类型查询假期信息，如果假期信息不存在，则不计算假期结余与考勤
		AttendanceVacation vacation = attendanceVacationService.getById(Long.parseLong(vacationType));
		if (vacation == null) {
			return;
		}
		
		// 假期单位为天，则需要将申请的开始时间和结束时间调整为年月日时分秒的格式
		if(vacation.getUnit() == 1){
			Calendar calendar = Calendar.getInstance();
			// 开始时间调整为：申请开始日期  00:00:00
			calendar.setTime(startDate);
			startDate = DateUtil.setStartDay(calendar).getTime();
			// 结束时间调整为：申请结束日期 23:59:59
			calendar.setTime(endDate);
			endDate = DateUtil.setEndDay(calendar).getTime();
		}
		
		Long businessId = Long.parseLong(businessKey);
		Long userId = Long.parseLong(startUserId);
		
		// 计算请假时长并调整假期结余
		vacationRemainService.computeVacationRemain(startDate, endDate, userId, businessId, vacation, VacationLog.TYPE_VACATOIN, "请假扣减");
		
		// 计算请假考勤
		try {
			attendanceShiftService.computeUserProcessResult(userId, businessId, startDate, endDate, AttendanceResult.TYPE_LEAVE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 销假流程结束，计算假期结余
	 * @param startUserId 流程发起人ID
	 * @param businessKey 流程对应主表记录id
	 * @param cmd 流程执行命令实体
	 * @param startDate 申请开始时间
	 * @param endDate 申请结束时间
	 * @param vacationType 假期类型
	 */
	public void vacationBack(String startUserId, String businessKey, ProcessCmd cmd, Date startDate, Date endDate, 
			String vacationType){
		// 0-正常，1-退回，2-退回发起人
		Integer isBack = cmd.isBack();
		// 如果流程被退回，则不计算假期结余与考勤
		if(isBack != 0){
			return;
		}
		
		// 根据假期类型查询假期信息，如果假期信息不存在则不计算销假时长
		AttendanceVacation vacation = attendanceVacationService.getById(Long.parseLong(vacationType));
		if (vacation == null) {
			return;
		}
		
		// 假期单位为天，则需要将申请的开始时间和结束时间调整为年月日时分秒的格式
		if(vacation.getUnit() == 1){
			Calendar calendar = Calendar.getInstance();
			// 开始时间调整为：申请开始日期  00:00:00
			calendar.setTime(startDate);
			startDate = DateUtil.setStartDay(calendar).getTime();
			// 结束时间调整为：申请结束日期 23:59:59
			calendar.setTime(endDate);
			endDate = DateUtil.setEndDay(calendar).getTime();
		}
		
		Long userId = Long.parseLong(startUserId);
		Long businessId = Long.parseLong(businessKey);
		// 计算销假时长并调整假期结余
		vacationRemainService.computeVacationRemain(startDate, endDate, userId, businessId, vacation, VacationLog.TYPE_BACK, "销假添加");
		
		// 计算销假期间的考勤
		try {
			attendanceShiftService.computeUserProcessResult(userId, businessId, startDate, endDate, AttendanceResult.TYPE_BACK);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 加班申请流程结束，计算加班考勤与加班时长
	 * @param startUserId 流程发起人ID
	 * @param businessKey 流程对应主表记录id
	 * @param cmd 流程执行命令实体
	 * @param startDate 申请开始时间
	 * @param endDate 申请结束时间
	 */
	public void overtime(String startUserId,String businessKey,ProcessCmd cmd, Date startDate, Date endDate){
		// 0-正常，1-退回，2-退回发起人
		Integer isBack = cmd.isBack();
		// 如果流程被退回，则不计算加班考勤及加班时长
		if(isBack != 0){
			return;
		}
		
		Long userId = Long.parseLong(startUserId);
		Long businessId = Long.parseLong(businessKey);
		// 计算加班时长
		vacationRemainService.computeOvertimeDuration(startDate, endDate, userId, businessId);
		
		// 计算加班考勤
		attendanceShiftService.computeOvertimeAttendance(userId, businessId, startDate, endDate);
	}
	
	/**
	 * 签卡申请流程结束，计算签卡考勤
	 * @param startUserId 流程发起人ID
	 * @param businessKey 流程对应主表记录id
	 * @param cmd 流程执行命令实体
	 * @param signTime 申请签卡时间
	 */
	public void signApply(String startUserId,String businessKey,ProcessCmd cmd, Date signTime){
		// 0-正常，1-退回，2-退回发起人
		Integer isBack = cmd.isBack();
		// 如果流程被退回，则不计算签卡考勤
		if(isBack != 0){
			return;
		}
		
		Long userId = Long.parseLong(startUserId);
		Long businessId = Long.parseLong(businessKey);
		// 计算签卡考勤
		try {
			attendanceShiftService.computeSignResult(userId, businessId, signTime);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 外出申请流程结束，计算外出考勤
	 * @param startUserId 流程发起人ID
	 * @param businessKey 流程对应主表记录id
	 * @param cmd 流程执行命令实体
	 * @param startDate 申请开始时间
	 * @param endDate 申请结束时间
	 */
	public void outApply(String startUserId,String businessKey,ProcessCmd cmd, Date startDate, Date endDate){
		// 0-正常，1-退回，2-退回发起人
		Integer isBack = cmd.isBack();
		// 如果流程被退回，则不计算外出考勤
		if(isBack != 0){
			return;
		}
		
		Long userId = Long.parseLong(startUserId);
		Long businessId = Long.parseLong(businessKey);
		// 计算外出考勤
		try {
			attendanceShiftService.computeUserProcessResult(userId, businessId, startDate, endDate, AttendanceResult.TYPE_OUT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 出差申请流程结束，计算出差考勤
	 * @param startUserId 流程发起人ID
	 * @param businessKey 流程对应主表记录id
	 * @param cmd 流程执行命令实体
	 * @param startDate 申请开始时间
	 * @param endDate 申请结束时间
	 */
	public void businessOut(String startUserId,String businessKey,ProcessCmd cmd, Date startDate, Date endDate){
		// 0-正常，1-退回，2-退回发起人
		Integer isBack = cmd.isBack();
		// 如果流程被退回，则不计算出差考勤
		if(isBack != 0){
			return;
		}
		
		Calendar calendar = Calendar.getInstance();
		// 开始时间调整为：申请开始日期  00:00:00
		calendar.setTime(startDate);
		startDate = DateUtil.setStartDay(calendar).getTime();
		// 结束时间调整为：申请结束日期 23:59:59
		calendar.setTime(endDate);
		endDate = DateUtil.setEndDay(calendar).getTime();
		
		Long userId = Long.parseLong(startUserId);
		Long businessId = Long.parseLong(businessKey);
		// 计算出差考勤
		try {
			attendanceShiftService.computeUserProcessResult(userId, businessId, startDate, endDate, AttendanceResult.TYPE_BUSINESS);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取用户请假时长
	 * @param userId 用户ID
	 * @param startDate 开始时间
	 * @param endDate 结束时间
	 * @param vacationType 休假类型
	 * @return
	 */
	public double getVocationDuration(String userId, Date startDate, Date endDate, String vacationType){
		Long userIdLong = Long.parseLong(userId);
		// 获取用户的考勤班次
		AttendanceShift attendanceShift = attendanceShiftService.getShiftByUserId(userIdLong);
		// 根据假期类型查询假期信息，如果假期信息不存在，则不计算假期结余与考勤
		Long typeId = Long.parseLong(vacationType);
		AttendanceVacation vacation = attendanceVacationService.getById(typeId);
		double duration = vacationRemainService.getVacationDuration(userIdLong, startDate, endDate, attendanceShift, vacation, VacationLog.TYPE_VACATOIN);
		return Math.abs(duration);
	}
}
