package com.suneee.platform.dao.ats;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.ats.AtsImportPlan;
/**
 *<pre>
 * 对象功能:打卡导入方案 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-26 13:50:13
 *</pre>
 */
@Repository
public class AtsImportPlanDao extends BaseDao<AtsImportPlan>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AtsImportPlan.class;
	}

	
	
	
	
}