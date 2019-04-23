package com.suneee.kaoqin.dao.kaoqin;

import com.suneee.core.db.BaseDao;
import com.suneee.kaoqin.model.kaoqin.ShiftDaySetting;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:单日排班设置 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:08:00
 *</pre>
 */
@Repository
public class ShiftDaySettingDao extends BaseDao<ShiftDaySetting>
{
	@Override
	public Class<?> getEntityClass()
	{
		return ShiftDaySetting.class;
	}

}