package com.suneee.ucp.mh.service.attendance;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.model.kaoqin.AttendanceRecord;
import com.suneee.kaoqin.service.kaoqin.AttendanceRecordService;
import com.suneee.kaoqin.service.kaoqin.HolidaysSettingService;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.mh.dao.attendance.VacationRemainDao;
import com.suneee.ucp.mh.model.attendance.AttendanceShift;
import com.suneee.ucp.mh.model.attendance.AttendanceVacation;
import com.suneee.ucp.mh.model.attendance.ShiftTime;
import com.suneee.ucp.mh.model.attendance.VacationLog;
import com.suneee.ucp.mh.model.attendance.VacationRemain;

import net.sf.json.JSONObject;

/**
 *<pre>
 * 对象功能:假期结余 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 11:32:12
 *</pre>
 */
@Service("ucpVacationRemainService")
public class VacationRemainService extends UcpBaseService<VacationRemain>
{
	@Resource(name="ucpVacationRemainDao")
	private VacationRemainDao dao;
	@Resource(name="ucpVacationLogService")
	private VacationLogService vacationLogService;
	@Resource(name="ucpAttendanceVacationService")
	private AttendanceVacationService attendanceVacationService;
	@Resource(name="ucpAttendanceShiftService")
	private AttendanceShiftService attendanceShiftService;
	@Resource
	private HolidaysSettingService holidaysSettingService;
	@Resource(name="ucpShiftTimeService")
	private ShiftTimeService shiftTimeService;
	@Resource
	private AttendanceRecordService attendanceRecordService;
	
	public VacationRemainService()
	{
	}
	
	@Override
	protected IEntityDao<VacationRemain, Long> getEntityDao() 
	{
		return dao;
	}
	
	/**
	 * 根据json字符串获取VacationRemain对象
	 * @param json
	 * @return
	 */
	public VacationRemain getVacationRemain(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		VacationRemain vacationRemain = JSONObjectUtil.toBean(json, VacationRemain.class);
		return vacationRemain;
	}
	
	/**
	 * 保存 假期结余 信息
	 * @param vacationRemain
	 */
	public void save(VacationRemain vacationRemain){
		Long id=vacationRemain.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			vacationRemain.setId(id);
			this.add(vacationRemain);
		}
		else{
			this.update(vacationRemain);
		}
	}
	
	/**
	 * 获取假期结余
	 * @param userId
	 * @param vacationTypeId
	 * @return
	 */
	public VacationRemain getByUserIdAndType(long userId, long vacationTypeId) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("vacationType", vacationTypeId);
		return dao.getUnique("getByUserIdAndType", map);
	}
	
	/**
	 * 获取假期数据
	 * @param userId
	 * @return
	 */
	public List<VacationRemain> getByUserId(Long userId) {
		return dao.getBySqlKey("getByUserId", userId);
	}

	/**
	 * 获取指定用户的假期结余信息
	 * @param userId
	 * @return
	 */
	public List<VacationRemain> getUserRemainDetail(Long userId) {
		return dao.getBySqlKey("getUserRemainDetail", userId);
	}

	/**
	 * 获取假期截止时间在指定时间之前的结余列表
	 * @return
	 */
	public List<VacationRemain> getOverdueRemains(Date date) {
		QueryFilter filter = new QueryFilter(new JSONObject());
		filter.addFilter("endvalidDate", date);
		return dao.getAll(filter);
	}
	
	/**
	 * 计算申请时长并调整假期结余
	 * @param startDate 开始时间
	 * @param endDate 结束时间
	 * @param userId 用户ID
	 * @param businessId 业务表单ID
	 * @param vacation 假期信息
	 * @param type 假期调整类型（1-手工调整，2-请假扣减，3-过期清零，4-周期补充，5-加班补充，6-销假补充）
	 * @param remark 假期调整备注
	 */
	public void computeVacationRemain(Date startDate, Date endDate, Long userId, Long businessId, AttendanceVacation vacation, 
			short type, String remark){
		// 获取用户的考勤班次
		AttendanceShift attendanceShift = attendanceShiftService.getShiftByUserId(userId);
		// 总时长（扣除休息时间）
		double adjust = this.getVacationDuration(userId, startDate, endDate, attendanceShift, vacation, type);
		this.saveVacationRemain(userId, vacation, adjust, type, remark, 0, businessId);
	}
	
	/**
	 * 计算加班时长并调整调休假结余
	 * @param startDate 申请开始时间
	 * @param endDate 申请结束时间
	 * @param userId 用户ID
	 * @param businessId 业务表单ID
	 */
	public void computeOvertimeDuration(Date startDate, Date endDate, Long userId, Long businessId){
		// 查询调休假信息，如果调休假信息不存在，则不计算加班时长
		AttendanceVacation vacation = attendanceVacationService.getVacationByCode("dxj");
		if (vacation == null) {
			return;
		}
		
		// 查询用户的班次
		AttendanceShift attendanceShift = attendanceShiftService.getShiftByUserId(userId);
		// 用户没有设置班次时，不计算加班时间
		if(attendanceShift == null){
			return;
		}
		
		Long shiftId = attendanceShift.getShiftId();
		// 查询班次的休息时间段
		List<ShiftTime> restShiftTimeList = shiftTimeService.getByShiftIdAndType(shiftId, ShiftTime.TYPE_REST);
		
		Calendar cycleTime = Calendar.getInstance();
		cycleTime.setTime(startDate);
		// 加班时长
		double totalMinute = 0;
		while(cycleTime.getTime().compareTo(endDate) <= 0){
			// 考勤班次在当天不是工作日，则计算加班考勤与加班时间
			if(!attendanceShiftService.isWorkDay(cycleTime, shiftId)){
				// 查询用户当日的打卡记录
				List<AttendanceRecord> records = attendanceRecordService.getRecordList(userId, cycleTime.getTime());
				// 有两条以上（含两条）打卡记录时，计算加班时长
				if(records.size() >= 2){
					// 考勤开始时间取第一条打卡记录
					Date startTime = records.get(0).getCheckTime();
					// 考勤结束时间取最后一条打卡记录
					Date endTime = records.get(records.size() - 1).getCheckTime();
					// 计算加班时长
					double total = 	DateUtil.betweenMinute(startTime, endTime);
					// 计算休息时长
					double restTime = shiftTimeService.computeTotalRestTime(startTime, endTime, restShiftTimeList, cycleTime);
					totalMinute = totalMinute + (total - restTime);
				}
			}
			cycleTime.add(Calendar.DAY_OF_MONTH, 1);
			DateUtil.setStartDay(cycleTime);
		}
		
		double adjust = 0;
		// 假期单位为天
		if(vacation.getUnit() == 1){
			adjust = totalMinute/(60*attendanceShift.getWorkHour());
		}
		// 假期单位为小时
		else{
			adjust = totalMinute/60;
		}
		
		// 如果假期单位存在最小限制,将实际时长根据最小限制来调整
		double minApply = vacation.getMinApply();
		if(minApply > 0){
			adjust = Math.floor(adjust/minApply) * minApply;
		}
		this.saveVacationRemain(userId, vacation, adjust, VacationLog.TYPE_OVERTIME, "加班累计", 0, businessId);
	}
	
	/**
	 * 计算用户请假时长
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @param attendanceShift
	 * @return
	 */
	public double getVacationDuration(Long userId, Date startDate, Date endDate, AttendanceShift attendanceShift, 
			AttendanceVacation vacation, short type){
		// 用户没有设置考勤班次，则不计算申请时长
		if(attendanceShift == null){
			return 0;
		}
		// 请假类型为空，则不计算申请时长
		if (vacation == null) {
			return 0;
		}
		// 班次ID
		Long shiftId = attendanceShift.getShiftId();
		
		// 确定用于循环条件判断的时间
		Calendar cycleTime = Calendar.getInstance();
		cycleTime.setTime(startDate);
		
		// 计算的开始时间
		Date computeStartTime = startDate;
		// 计算的结束时间
		Date computeEndTime = endDate;
		
		// 总时长（扣除休息时间）
		double globalTotal = 0;
		while(cycleTime.getTime().compareTo(endDate) <= 0){
			// 如果考勤班次在当天是工作日，则计算请假时长
			if(attendanceShiftService.isWorkDay(cycleTime, shiftId)){
				// 获取班次内的考勤时间段
				List<ShiftTime> workShiftTimeList = shiftTimeService.getByShiftIdAndType(shiftId, ShiftTime.TYPE_WORK);
				// 获取班次内的休息时间段
				List<ShiftTime> restShiftTimeList = shiftTimeService.getByShiftIdAndType(shiftId, ShiftTime.TYPE_REST);
				for(ShiftTime workTime : workShiftTimeList){
					// 获取当前考勤时间段的标准时间
					shiftTimeService.computeStandardShiftTime(workTime, cycleTime, false);
					
					// 如果计算时间段与当前考勤时间段没有交集，则进入下一个考勤时间段的计算
					if(computeStartTime.after(workTime.getEndTime()) || computeEndTime.before(workTime.getStartTime())){
						continue;
					}
					// 当计算开始时间小于标准开始时间时，将计算的开始时间调整为标准开始时间
					if(computeStartTime.before(workTime.getStartTime())){
						computeStartTime = workTime.getStartTime();
					}
					// 当计算结束时间大于标准结束时间时，将计算的结束时间调整为标准结束时间
					if(computeEndTime.after(workTime.getEndTime())){
						computeEndTime = workTime.getEndTime();
					}
					// 计算两个时间之间的总工作时长
					double total = DateUtil.betweenMinute(computeStartTime, computeEndTime);
					// 休息时间
					double totalMinus = shiftTimeService.computeTotalRestTime(computeStartTime, computeEndTime, restShiftTimeList, cycleTime);
					// 计算实际工作时长
					globalTotal = globalTotal + (total - totalMinus);
				}
			}
			
			// 进入下一天的请假时长计算
			cycleTime.add(Calendar.DAY_OF_MONTH, 1);
			DateUtil.setStartDay(cycleTime);
			
			// 重置计算的开始时间和结束时间
			computeStartTime = cycleTime.getTime();
			computeEndTime = endDate;
		}
		double adjust = 0;
		// 假期单位为天
		if(vacation.getUnit() == 1){
			adjust = globalTotal/(60*attendanceShift.getWorkHour());
		}
		// 假期单位为小时
		else{
			adjust = globalTotal/60;
		}
		
		// 当假期存在最小限制时，需要将实际时长根据最小限制来调整
		double minApply= vacation.getMinApply();
		// 请假时长不足假期最小限制的部分按假期最小限制计算
		if(type == VacationLog.TYPE_VACATOIN ){
			if(minApply > 0){
				adjust = Math.ceil(adjust/minApply) * minApply;
			}
			adjust = -adjust;
		}
		// 销假时长不足假期最小限制的部分不计算销假
		else if(type == VacationLog.TYPE_BACK){
			if(minApply > 0){
				adjust = Math.floor(adjust/minApply) * minApply;
			}
		}
		return adjust;
	}
	
	/**
	 * 保存假期结余信息（同时保存假期结余调整日志）
	 * @param userId 用户ID
	 * @param vacation 假期信息
	 * @param adjustMinute 调整分钟数
	 * @param workHour 班次工作时长
	 * @param type 假期调整类型（1-手工调整，2-请假扣减，3-过期清零，4-周期补充，5-加班补充，6-销假补充）
	 * @param remark 假期调整备注
	 * @param computeYear 计算年假年限（仅周期性计算年假时该字段有效）
	 * @param businessId 业务表单ID
	 */
	public void saveVacationRemain(Long userId, AttendanceVacation vacation, double adjustDuration, short type, 
			String remark, int computeYear, Long businessId){
		// 根据用户ID和假期ID查询用户的假期结余信息
		VacationRemain remain = this.getByUserIdAndType(userId, vacation.getId());
		// 假期结余信息不存在时则新建
		if (remain == null) {
			remain = new VacationRemain();
			remain.setBaseUnit(vacation.getUnit());
			remain.setStatus((short)0);
			remain.setUserId(userId);
			remain.setVacationType(vacation.getId());
			remain.setRemained(0.0);
		}
		Double remainTime = remain.getRemained();
		remain.setRemained(remainTime + adjustDuration);
		// 年假自动增加（4-周期补充 ），设置计算年假的年份
		if(type == VacationLog.TYPE_SUPPLY){
			remain.setComputeYear(computeYear);
		}
		// 保存假期结余信息
		this.save(remain);
		
		// 构造假期结余调整信息对象
		VacationLog vacationLog = new VacationLog();
		vacationLog.setVacationType(remain.getVacationType());
		vacationLog.setUserId(remain.getUserId());
		vacationLog.setChangeType(type);
		vacationLog.setBeforeValue(remainTime);
		vacationLog.setStatus((short)0); 
		vacationLog.setAfterValue(remain.getRemained());
		vacationLog.setChangeValue(adjustDuration);
		vacationLog.setChangeUnit(vacation.getUnit());
		vacationLog.setBusinessId(businessId);
		vacationLog.setMemo(remark);
		vacationLog.setUpdatetime(new Date());
		vacationLogService.save(vacationLog);
	}
}
