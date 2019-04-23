package com.suneee.platform.service.bpm.impl;

import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.bpm.TaskDao;
import org.springframework.stereotype.Service;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.bpm.TaskDao;
import com.suneee.platform.service.bpm.ITaskService;

@Service
public class TaskServiceImpl implements ITaskService {
	
	@Resource
    TaskDao taskDao;
	

	/**
	 * 获取手机端任务。
	 */
	@Override
	public List getMyMobileTasks(QueryFilter queryFilter) {
		Long userId= ContextUtil.getCurrentUserId();
		List  taskList= taskDao.getMyMobileTasks(userId, queryFilter);
		return taskList;
	}

}
