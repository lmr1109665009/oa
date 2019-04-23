package com.suneee.ucp.mh.service.attendance;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.base.util.DateUtils;
import com.suneee.ucp.mh.dao.attendance.ShiftTimeDao;
import com.suneee.ucp.mh.model.attendance.ShiftTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 *<pre>
 * 对象功能:班次时间段 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:mikel
 * 创建时间:2017-05-02 10:09:27
 *</pre>
 */
@Service("ucpShiftTimeService")
public class ShiftTimeService extends UcpBaseService<ShiftTime>
{
	@Resource(name="ucpShiftTimeDao")
	private ShiftTimeDao dao;
	
	public ShiftTimeService()
	{
	}
	
	@Override
	protected IEntityDao<ShiftTime, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 根据班次ID获取班次时间段列表
	 * @param shiftId
	 * @return
	 */
	public List<ShiftTime> getByShiftIdAndType(Long shiftId, Short type) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("shiftId", shiftId);
		params.put("type", type);
		return dao.getBySqlKey("getAll", params);
	}
	
	/**
	 * 根据json字符串获取ShiftTime对象
	 * @param json
	 * @return
	 */
	public ShiftTime getShiftTime(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		ShiftTime shiftTime = JSONObjectUtil.toBean(json, ShiftTime.class);
		return shiftTime;
	}
	
	/**
	 * 保存 班次时间段 信息
	 * @param shiftTime
	 */
	public void save(ShiftTime shiftTime){
		Long id=shiftTime.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			shiftTime.setId(id);
			this.add(shiftTime);
		}
		else{
			this.update(shiftTime);
		}
	}
	
	/**
	 * 保存时间段信息
	 * @param shiftTimeList
	 * @param shiftId
	 */
	public void addShiftTimes(Long shiftId, List<ShiftTime> shiftTimeList, Short type){
		// 如果班次的休息时间段为空，则执行保存操作
		if(shiftTimeList == null || shiftTimeList.size() == 0){
			return;
		}
		for(ShiftTime shiftTime : shiftTimeList){
			if(shiftTime == null){
				continue;
			}
			// 构造时间段对象并保存
			shiftTime.setId(UniqueIdUtil.genId());
			shiftTime.setShiftId(shiftId);
			shiftTime.setRestTime(0L);
			shiftTime.setStatus(ShiftTime.STATUS_NORMAL);
			shiftTime.setCreateby(ContextUtil.getCurrentUserId());
			shiftTime.setCreatetime(new Date());
			shiftTime.setType(type);
			this.add(shiftTime);
		}
	}
	
	/**
	 * 计算班次工作时间段的总时长
	 * @param shiftTimeList
	 * @return
	 */
	public double computeTotalTime(List<ShiftTime> shiftTimeList, List<ShiftTime> restShiftTimeList){
		// 用来记录班次工作时长
		double totalWorkTime = 0;
		// 如果时间段为空，则不计算班次工作时长
		if(shiftTimeList == null || shiftTimeList.size() == 0){
			return totalWorkTime;
		}
		
		Calendar day = Calendar.getInstance();
		for(ShiftTime shiftTime : shiftTimeList){
			if(shiftTime == null){
				continue;
			}
			// 如果开始时间或结束时间为空，则不计算此时间段时长
			if(shiftTime.getStartTime() == null || shiftTime.getEndTime() == null){
				continue;
			}
			// 计算时间段的标准时间
			this.computeStandardShiftTime(shiftTime, day, false);
			
			// 计算工作时长
			double workTime = DateUtil.betweenMinute(shiftTime.getStartTime(), shiftTime.getEndTime());
			// 计算休息时长
			double totalMinus = this.computeTotalRestTime(shiftTime.getStartTime(), shiftTime.getEndTime(), restShiftTimeList, day);
			// 计算实际工作时长
			totalWorkTime = totalWorkTime + workTime - totalMinus;
		}
		return totalWorkTime;
	}
	
	/**
	 * 计算某个时间段内的休息时间
	 * @param startTime 计算时间段开始时间
	 * @param endTime 计算时间段结束时间
	 * @param restShiftTimeList 休息时间段
	 * @param day 计算的日期
	 * @return
	 */
	public double computeTotalRestTime(Date startTime, Date endTime, List<ShiftTime> restShiftTimeList, Calendar day){
		// 休息时间
		double totalMinus = 0;
		if(restShiftTimeList == null || restShiftTimeList.isEmpty()){
			return totalMinus;
		}
		for(ShiftTime restTime : restShiftTimeList){
			if(restTime == null){
				continue;
			}
			// 获取当前休息时间段的标准时间
			this.computeStandardShiftTime(restTime, day, false);
			
			// 如果计算时间段与当前休息时间段没有交集，则进入下一个休息时间段的计算
			if(restTime.getStartTime().after(endTime) || restTime.getEndTime().before(startTime)){
				continue;
			}
			// 如果休息开始时间小于计算开始时间，则休息开始时间调整为计算开始时间
			if(restTime.getStartTime().before(startTime)){
				restTime.setStartTime(startTime);
			}
			// 如果休息结束时间大于计算结束时间，则休息结束时间调整为计算结束时间
			if(restTime.getEndTime().after(endTime)){
				restTime.setEndTime(endTime);
			}
			totalMinus = totalMinus + DateUtil.betweenMinute(restTime.getStartTime(), restTime.getEndTime());
		}
		return totalMinus;
	}
	
	/**
	 * 计算时间段某一天的标准时间（标准打卡时间、标准取卡范围）
	 * @param shiftTime 班次时间段
	 * @param day 指定的某一天
	 * @param needRange 是否需要计算取卡范围
	 * @return 
	 */
	public ShiftTime computeStandardShiftTime(ShiftTime shiftTime, Calendar day, boolean needRange){
		// 获取当前时间段的标准开始时间和结束时间
		Date standardStartTime = this.computeStandardTime(shiftTime.getStartTime(), day);
		Date standardEndTime = this.computeStandardTime(shiftTime.getEndTime(), day);
		// 如果标准开始时间大于标准结束时间，则将标准结束时间往后调整一天(班次时间段跨天的情况)
		if(standardStartTime.after(standardEndTime)){
			standardEndTime = DateUtils.getDayOfMonthBefore(standardEndTime, 1).getTime();
		}
		shiftTime.setStartTime(standardStartTime);
		shiftTime.setEndTime(standardEndTime);
		
		// 如果是考勤时间段，需要计算考勤时间点的取卡范围
		if(ShiftTime.TYPE_WORK == shiftTime.getType() && needRange){
			// 计算上班时间的取卡范围
			Date[] startRange = this.computeStandardTimeRange(standardStartTime, 
					shiftTime.getStartBeginRange(), shiftTime.getStartEndRange(), day);
			shiftTime.setStartBeginRange(startRange[0]);
			shiftTime.setStartEndRange(startRange[1]);
			// 计算下班时间的取卡范围
			Date[] endRange = this.computeStandardTimeRange(standardEndTime, 
					shiftTime.getEndBeginRange(), shiftTime.getEndEndRange(), day);
			shiftTime.setEndBeginRange(endRange[0]);
			shiftTime.setEndEndRange(endRange[1]);
		}
		return shiftTime;
	}
	
	/**
	 * 计算某一时间点某一天的标准打卡范围
	 * @param standardTime 标准时间
	 * @param beginRange 开始范围
	 * @param endRange 结束范围
	 * @param day 指定的某一天
	 */
	private Date[] computeStandardTimeRange(Date standardTime, Date beginRange, Date endRange, Calendar day){
		Date[] range = new Date[2]; 
		// 计算范围的开始时间
		if(beginRange == null){
			beginRange = DateUtil.setStartDay(day).getTime();
		}else{
			beginRange = this.computeStandardTime(beginRange, day);
		}
		// 如果开始时间在标准时间之后，则开始时间前推一天
		if(beginRange.after(standardTime)){
			beginRange = DateUtils.getDayOfMonthBefore(beginRange, -1).getTime();
		}
		range[0] = beginRange;
		
		// 计算范围的结束时间
		if(endRange == null){
			endRange = DateUtil.setEndDay(day).getTime();
		}else{
			endRange = this.computeStandardTime(endRange, day);
		}
		// 如果结束时间在标准事件之前，则结束时间后推一天
		if(endRange.before(standardTime)){
			endRange = DateUtils.getDayOfMonthBefore(endRange, 1).getTime();
		}
		range[1] = endRange;
		
		return range;
	}
	
	
	/**
	 * 计算某个打卡时间点在当天的标准打卡时间
	 * @param time
	 * @param day
	 * @return
	 */
	private Date computeStandardTime(Date time, Calendar day) {
		if(time != null && day != null){
			Calendar c = Calendar.getInstance();
			c.setTime(time);
			this.setSameCalendarDate(day, c);
			return c.getTime();
		}
		return null;
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
}
