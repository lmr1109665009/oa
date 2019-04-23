package com.suneee.kaoqin.dao.kaoqin;

import java.util.List;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.kaoqin.model.kaoqin.ShiftSetting;
/**
 *<pre>
 * 对象功能:排班设置 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:08:44
 *</pre>
 */
@Repository
public class ShiftSettingDao extends BaseDao<ShiftSetting>
{
	@Override
	public Class<?> getEntityClass()
	{
		return ShiftSetting.class;
	}

	
	
	
	
}