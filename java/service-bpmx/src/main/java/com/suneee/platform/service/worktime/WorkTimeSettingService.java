package com.suneee.platform.service.worktime;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.worktime.WorkTimeSettingDao;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.model.worktime.WorkTimeSetting;
import com.suneee.platform.dao.worktime.WorkTimeSettingDao;

/**
 * 对象功能:班次设置 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-02-20 14:57:31
 */
@Service
public class WorkTimeSettingService extends BaseService<WorkTimeSetting>
{
	@Resource
	private WorkTimeSettingDao dao;
	
	public WorkTimeSettingService()
	{
	}
	
	@Override
	protected IEntityDao<WorkTimeSetting, Long> getEntityDao()
	{
		return dao;
	}
}
