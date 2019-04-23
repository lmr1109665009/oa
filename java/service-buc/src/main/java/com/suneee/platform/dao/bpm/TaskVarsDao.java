package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.bpm.TaskVars;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class TaskVarsDao extends BaseDao<TaskVars>
{
	@Override
	public Class<TaskVars> getEntityClass()
	{
		return TaskVars.class;
	}
	
	/**
	 * 获取本任务所有的流程变量
	 * @param queryFilter
	 * @return
	 */
	public List<TaskVars> getTaskVars(QueryFilter queryFilter)
	{
		
		return getBySqlKey("getTaskVars", queryFilter);
	}

	public void delVarsByActInstId(String actInstId) {
		this.delBySqlKey("delVarsByActInstId", actInstId);
		
	}
}
