package com.suneee.platform.service.calendar;
import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.calendar.PersonalCalendarDao;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.calendar.PersonalCalendarDao;
import com.suneee.platform.model.calendar.PersonalCalendar;

/**
 *<pre>
 * 对象功能:个人日历 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2014-03-12 14:50:14
 *</pre>
 */
@Service
public class PersonalCalendarService extends BaseService<PersonalCalendar>
{
	@Resource
	private PersonalCalendarDao dao;
	
	
	
	public PersonalCalendarService()
	{
	}
	
	@Override
	protected IEntityDao<PersonalCalendar, Long> getEntityDao()
	{
		return dao;
	}
	
	
}
