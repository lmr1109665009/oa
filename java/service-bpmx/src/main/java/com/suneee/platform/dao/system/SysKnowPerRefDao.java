package com.suneee.platform.dao.system;

import java.util.List;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysKnowPerRef;
/**
 *<pre>
 * 对象功能:权限关联主表 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:dyg
 * 创建时间:2015-12-31 16:05:42
 *</pre>
 */
@Repository
public class SysKnowPerRefDao extends BaseDao<SysKnowPerRef>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysKnowPerRef.class;
	}

	
	
	
	
}