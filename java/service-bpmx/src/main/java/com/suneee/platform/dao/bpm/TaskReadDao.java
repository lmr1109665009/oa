package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.TaskRead;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:任务是否已读 Dao类
 * 开发公司:SF
 * 开发人员:xxx
 * 创建时间:2013-04-16 17:30:53
 *</pre>
 */
@Repository
public class TaskReadDao extends BaseDao<TaskRead>
{
	@Override
	public Class<?> getEntityClass()
	{
		return TaskRead.class;
	}
	
	/**
	 * 判断任务是否存在。
	 * @param taskId
	 * @param userId
	 * @return
	 */
	public boolean isTaskRead(Long taskId,Long userId){
		Map<String,Long> params=new HashMap<String, Long>();
		params.put("taskid", taskId);
		params.put("userid", userId);
		Integer rtn=(Integer) this.getOne("isTaskRead", params);
		return rtn>0;
	}
	
	/**
	 * 根据流程实例删除。
	 * @param actInstId
	 */
	public void delByActInstId(Long actInstId){
		this.delBySqlKey("delByActInstId", actInstId);
	}
	
	/**
	 * 删除任务。
	 * @param taskId
	 */
	public void delByTaskId(Long taskId){
		this.delBySqlKey("delByTaskId", taskId);
	}

	public List<TaskRead> getTaskRead(Long actInstId, Long taskId,String assignee) {
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("actInstId", actInstId);
		params.put("taskId", taskId);
		params.put("assignee", assignee);
		return this.getBySqlKey("getTaskRead", params);
	}
	public TaskRead getByTaskId(Long taskId){
		return this.getEBySqlKey("getByTaskId", taskId);
	}
}