package com.suneee.kaoqin.dao.kaoqin;

import com.suneee.core.db.BaseDao;
import com.suneee.kaoqin.model.kaoqin.ShiftCalendar;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:班次日历 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:mikel
 * 创建时间:2017-05-04 16:19:21
 *</pre>
 */
@Repository
public class ShiftCalendarDao extends BaseDao<ShiftCalendar>
{
	@Override
	public Class<?> getEntityClass()
	{
		return ShiftCalendar.class;
	}

	/**
	 * 根据班次ID删除班次休息日设置数据
	 * @param shiftId
	 */
	public void removeByShiftId(Long shiftId) {
		delBySqlKey("removeByShiftId", shiftId);
	}
	
}