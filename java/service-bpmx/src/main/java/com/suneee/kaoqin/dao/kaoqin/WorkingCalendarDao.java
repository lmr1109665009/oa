package com.suneee.kaoqin.dao.kaoqin;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.kaoqin.model.kaoqin.WorkingCalendar;
/**
 *<pre>
 * 对象功能:工作日历 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:12:02
 *</pre>
 */
@Repository
public class WorkingCalendarDao extends BaseDao<WorkingCalendar>
{
	@Override
	public Class<?> getEntityClass()
	{
		return WorkingCalendar.class;
	}

	
	
	
	
}