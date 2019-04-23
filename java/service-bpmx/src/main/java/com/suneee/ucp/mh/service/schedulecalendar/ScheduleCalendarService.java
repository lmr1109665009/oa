package com.suneee.ucp.mh.service.schedulecalendar;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.mh.dao.schedulecalendar.ScheduleCalendarDao;
import com.suneee.ucp.mh.model.schedulecalendar.ScheduleCalendar;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *<pre>
 * 对象功能:日程表 Service类
 * 开发公司:深圳象翌微链股份有限公司
 * 开发人员:xiongxianyun
 * 创建时间:2017-06-26 13:36:34
 *</pre>
 */
@Service
public class ScheduleCalendarService extends UcpBaseService<ScheduleCalendar>
{
	@Resource
	private ScheduleCalendarDao dao;
	
	public ScheduleCalendarService()
	{
	}
	
	@Override
	protected IEntityDao<ScheduleCalendar, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 保存 日程表 信息
	 * @param scheduleCalendar
	 */
	public void save(ScheduleCalendar scheduleCalendar){
		Long id = scheduleCalendar.getId();
		scheduleCalendar.setUpdateBy(ContextUtil.getCurrentUserId());
		scheduleCalendar.setUpdatetime(new Date());
		if(id == null || id == 0){
			id = UniqueIdUtil.genId();
			scheduleCalendar.setId(id);
			scheduleCalendar.setCreateBy(ContextUtil.getCurrentUserId());
			scheduleCalendar.setCreatetime(new Date());
			this.add(scheduleCalendar);
		}
		else{
			this.update(scheduleCalendar);
		}
	}
	
	/**
	 * 获取开始时间和结束时间之间的用户日程信息
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<ScheduleCalendar> getListBy(Date startDate, Date endDate){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("beginendTime", startDate);
		params.put("endstartTime", endDate);
		params.put("createBy", ContextUtil.getCurrentUserId());
		return dao.getListBy(params);
	}
	
	/**
	 * 根据日程ID和用户ID删除日程信息
	 * @param id
	 * @param userId
	 */
	public void delBy(Long id){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("createBy", ContextUtil.getCurrentUserId());
		dao.delBy(params);
	}
	
	/**
	 * 根据日程ID和用户ID获取日程信息
	 * @param id
	 * @param userId
	 * @return
	 */
	public ScheduleCalendar getBy(Long id){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("createBy", ContextUtil.getCurrentUserId());
		return dao.getBy(params);
	}
}
