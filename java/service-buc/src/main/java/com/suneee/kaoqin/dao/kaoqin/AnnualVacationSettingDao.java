package com.suneee.kaoqin.dao.kaoqin;

import java.util.List;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.kaoqin.model.kaoqin.AnnualVacationSetting;
/**
 *<pre>
 * 对象功能:年假设置 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 11:30:12
 *</pre>
 */
@Repository
public class AnnualVacationSettingDao extends BaseDao<AnnualVacationSetting>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AnnualVacationSetting.class;
	}

	
	
	
	
}