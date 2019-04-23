package com.suneee.platform.dao.ats;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.ats.AtsAttenceCycle;
/**
 *<pre>
 * 对象功能:考勤周期 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-17 22:03:30
 *</pre>
 */
@Repository
public class AtsAttenceCycleDao extends BaseDao<AtsAttenceCycle>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AtsAttenceCycle.class;
	}

	
	
	
	
}