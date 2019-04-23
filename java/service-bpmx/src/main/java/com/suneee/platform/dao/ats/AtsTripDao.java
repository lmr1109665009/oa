package com.suneee.platform.dao.ats;

import java.util.List;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.ats.AtsTrip;
/**
 *<pre>
 * 对象功能:考勤出差单 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-07-15 14:59:30
 *</pre>
 */
@Repository
public class AtsTripDao extends BaseDao<AtsTrip>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AtsTrip.class;
	}

	
	
	
	
}