package com.suneee.platform.dao.calendar;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.calendar.util.CalendarUtil;
import com.suneee.platform.model.calendar.PersonalCalendar;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:个人日历 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2014-03-12 14:50:14
 *</pre>
 */
@Repository
public class PersonalCalendarDao extends BaseDao<PersonalCalendar>
{
	@Override
	public Class<?> getEntityClass()
	{
		return PersonalCalendar.class;
	}

	public List<PersonalCalendar> getPersonalCalendar(Map<String, Object> params) {
		return this.getBySqlKey("getPersonalCalendar", CalendarUtil.getCalendarMap(params));
	}

}