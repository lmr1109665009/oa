package com.suneee.platform.dao.bpm;

import com.suneee.core.bpm.model.ProcessTaskHistory;
import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TaskHistoryDao extends BaseDao<ProcessTaskHistory >
{
	
	
	@Override
	public Class getEntityClass()
	{
		return ProcessTaskHistory.class;
	}
	
	/**
	 * 根据流程实例id和用户获取最后审批的历史任务。
	 * @param processInstanceId
	 * @return
	 */
	public ProcessTaskHistory getLastFinshTaskByProcId(Long processInstanceId,Long userId){	
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("processInstanceId", processInstanceId);
		params.put("userId", userId);
		List<ProcessTaskHistory> list= getBySqlKey("getFinshTaskByProcId", params);
		if(list.size()>0)
			return list.get(0);
		return  null;
	}
	
	/**
	 * 根据任务ID返回历史任务列表。
	 * @param taskIds
	 * @return
	 */
	public List<ProcessTaskHistory> getByTaskIds(String taskIds){
		List<ProcessTaskHistory> list= getBySqlKey("getByTaskIds", taskIds);
		return list;
	}

}
