package com.suneee.platform.service.bpm;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.bpm.TaskHistoryDao;
import org.springframework.stereotype.Service;

import com.suneee.core.bpm.model.ProcessTaskHistory;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.bpm.TaskHistoryDao;

@Service
public class TaskHistoryService extends BaseService<ProcessTaskHistory> {
	@Resource
	private TaskHistoryDao dao;

	public TaskHistoryService() {
	}

	@Override
	protected IEntityDao<ProcessTaskHistory, Long> getEntityDao() {
		// TODO Auto-generated method stub
		return dao;
	}

	
}
