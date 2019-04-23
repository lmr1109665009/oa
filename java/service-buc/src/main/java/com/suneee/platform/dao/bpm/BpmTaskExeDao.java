package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.core.page.PageBean;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.bpm.BpmTaskExe;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *<pre>
 * 对象功能:任务转办代理 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2013-03-27 12:02:35
 *</pre>
 */
@Repository
public class BpmTaskExeDao extends BaseDao<BpmTaskExe>
{
	@Override
	public Class<?> getEntityClass()
	{
		return BpmTaskExe.class;
	}

	/**
	 * 根据任务ID获得任务转办代理
	 * @param taskId 任务ID
	 * @return
	 */
	public List<BpmTaskExe> getByTaskId(Long taskId) {
		return this.getBySqlKey("getByTaskId", taskId);	
	}

	/**
	 * 根据任务ID获得任务转办代理
	 * @param taskId 任务ID
	 * @return
	 */
	public List<BpmTaskExe> getByTaskIdStatus(Long taskId, Short status) {
		Map<String,Object> map =  new HashMap<String,Object>();
		map.put("taskId", taskId);
		map.put("status", status);
		return this.getBySqlKey("getByTaskIdStatus", map);
	}

	public List<BpmTaskExe> getByRunId(Long runId) {
		return this.getBySqlKey("getByRunId", runId);
	}

	public List<BpmTaskExe> getByActInstId(Long actInstId) {
		return this.getBySqlKey("getByActInstId", actInstId);
	}

	public List<BpmTaskExe> accordingMattersList(QueryFilter filter) {
		return getBySqlKey("accordingMattersList", filter);
	}

	/**
	 * 根据流程定义ID获取统计数量
	 * @param filter
	 * @return
	 */
	public List<?> getAccordingMattersCount(QueryFilter filter) {
		return getBySqlKey("getAccordingMattersCount", filter);
	}

	public List<BpmTaskExe> getMobileAccordingMattersList(QueryFilter filter) {
		return getBySqlKey("getMobileAccordingMattersList", filter);
	}
	
	public List<BpmTaskExe> accordingMattersList(Long ownerId,PageBean pb) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerId", ownerId);
		return getBySqlKey("accordingMattersList",params, pb);
	}
	
	/**
	 * 根据任务id获取是否已经转办。
	 * @param taskId
	 * @return
	 */
	public Integer getByIsAssign(Long taskId){
		return (Integer) this.getOne("getByIsAssign", taskId);
	}

	public int delByRunId(Long runId) {
		return this.delBySqlKey("delByRunId", runId);
	}

	public List<BpmTaskExe> accordingMattersListOrgCode(QueryFilter filter) {
		return getBySqlKey("accordingMattersListOrgCode", filter);
	}
	
}