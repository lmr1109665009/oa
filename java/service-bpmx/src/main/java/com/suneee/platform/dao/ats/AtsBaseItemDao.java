package com.suneee.platform.dao.ats;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.ats.AtsBaseItem;
/**
 *<pre>
 * 对象功能:基础数据 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-16 18:08:43
 *</pre>
 */
@Repository
public class AtsBaseItemDao extends BaseDao<AtsBaseItem>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AtsBaseItem.class;
	}

	
	
	
	
}