package com.suneee.platform.dao.system;

import java.util.List;
import java.util.Map;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysPlanExchange;
/**
 *<pre>
 * 对象功能:日程交流表 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2013-12-19 11:26:03
 *</pre>
 */
@Repository
public class SysPlanExchangeDao extends BaseDao<SysPlanExchange>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysPlanExchange.class;
	}
	
	public List<SysPlanExchange> getByPlanId(Long planId){		
		return this.getBySqlKey("getByPlanId", planId);
	}
	
	public void delByPlanId(Long planId){		
		this.delBySqlKey("delByPlanId", planId);
	}
}