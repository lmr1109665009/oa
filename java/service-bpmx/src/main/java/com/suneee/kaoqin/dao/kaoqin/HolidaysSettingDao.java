package com.suneee.kaoqin.dao.kaoqin;

import com.suneee.core.db.BaseDao;
import com.suneee.kaoqin.model.kaoqin.HolidaysSetting;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:节假日设置 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:07:14
 *</pre>
 */
@Repository
public class HolidaysSettingDao extends BaseDao<HolidaysSetting>
{
	@Override
	public Class<?> getEntityClass()
	{
		return HolidaysSetting.class;
	}

	
	
	
	
}