package com.suneee.kaoqin.dao.kaoqin;

import com.suneee.core.db.BaseDao;
import com.suneee.kaoqin.model.kaoqin.VacationRemain;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:假期结余 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 11:32:12
 *</pre>
 */
@Repository
public class VacationRemainDao extends BaseDao<VacationRemain>
{
	@Override
	public Class<?> getEntityClass()
	{
		return VacationRemain.class;
	}

	
	
	
	
}