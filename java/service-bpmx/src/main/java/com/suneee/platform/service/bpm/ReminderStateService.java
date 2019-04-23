package com.suneee.platform.service.bpm;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.bpm.ReminderStateDao;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.model.bpm.ReminderState;
import com.suneee.platform.dao.bpm.ReminderStateDao;

/**
 * 对象功能:任务催办执行情况 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-02-17 17:17:37
 */
@Service
public class ReminderStateService extends BaseService<ReminderState>
{
	@Resource
	private ReminderStateDao dao;
	
	public ReminderStateService()
	{
	}
	
	@Override
	protected IEntityDao<ReminderState, Long> getEntityDao()
	{
		return dao;
	}
}
