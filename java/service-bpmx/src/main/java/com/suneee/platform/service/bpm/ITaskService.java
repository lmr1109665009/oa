package com.suneee.platform.service.bpm;

import java.util.List;

import com.suneee.core.web.query.QueryFilter;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

import com.suneee.core.web.query.QueryFilter;

public interface ITaskService {
	
	/**
	 * 获取我的手机端任务。
	 * @param queryFilter
	 * @return
	 */
	List getMyMobileTasks(QueryFilter queryFilter);

}
