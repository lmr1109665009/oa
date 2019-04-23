/**
 * 对象功能:系统日历 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-02-20 14:57:32
 */
package com.suneee.platform.dao.worktime;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.worktime.SysCalendar;
import org.springframework.stereotype.Repository;

@Repository
public class SysCalendarDao extends BaseDao<SysCalendar>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return SysCalendar.class;
	}
	
	/**
	 * 取得默认的日历。
	 * @return
	 */
	public SysCalendar getDefaultCalendar(){
		return this.getUnique("getDefaultCalendar", null);
	}
	
	/**
	 * 设置默认日历
	 * @param id
	 */
	public void setNotDefaultCal(){
		this.update("setNotDefaultCal", null);
	}
}