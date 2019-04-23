package com.suneee.platform.dao.ats;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.ats.AtsCalendarTempl;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:日历模版 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-17 15:44:41
 *</pre>
 */
@Repository
public class AtsCalendarTemplDao extends BaseDao<AtsCalendarTempl>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AtsCalendarTempl.class;
	}

	
	
	
	
}