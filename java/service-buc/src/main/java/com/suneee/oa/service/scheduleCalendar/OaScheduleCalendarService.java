package com.suneee.oa.service.scheduleCalendar;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.mh.dao.schedulecalendar.ScheduleCalendarDao;
import com.suneee.ucp.mh.model.schedulecalendar.ScheduleCalendar;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 *<pre>
 * 对象功能:日程表 Service类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2017-12-19 15:10:00
 *</pre>
 */
@Service
public class OaScheduleCalendarService extends UcpBaseService<ScheduleCalendar> {

	@Resource
	private ScheduleCalendarDao dao;

	public OaScheduleCalendarService()
	{
	}

	@Override
	protected IEntityDao getEntityDao() {
		return dao;
	}


	/**
	 * 保存 日程表 信息
	 * @param scheduleCalendar
	 */
	public void save(ScheduleCalendar scheduleCalendar,String participants){
		Date date = new Date();
		scheduleCalendar.setCreatetime(date);
		scheduleCalendar.setUpdatetime(date);
		String[] split = participants.split(",");
		for(String userId:split){
			Long id = UniqueIdUtil.genId();
			scheduleCalendar.setId(id);
			scheduleCalendar.setCreateBy(Long.valueOf(userId));
			scheduleCalendar.setUpdateBy(Long.valueOf(userId));
			dao.add(scheduleCalendar);
		}
	}

	/**
	 * 根据数据来源id删除对应日程数据
	 */
	public void delBySourceId(Long sourceId){
		dao.delBySourceId(sourceId);
	}
}
