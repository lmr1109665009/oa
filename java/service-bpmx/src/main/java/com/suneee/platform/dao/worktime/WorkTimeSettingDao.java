/**
 * 对象功能:班次设置 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-02-20 14:57:31
 */
package com.suneee.platform.dao.worktime;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.worktime.WorkTimeSetting;

@Repository
public class WorkTimeSettingDao extends BaseDao<WorkTimeSetting>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return WorkTimeSetting.class;
	}
}