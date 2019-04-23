package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class HistoryActivityDao extends BaseDao<HistoricActivityInstanceEntity> {

	@Override
	public Class getEntityClass() {
		return HistoricActivityInstanceEntity.class;
	}
	
	/**
	 * 根据流程实例ID 历史活动实例。
	 * @param actInstId
	 * @param nodeId
	 * @return
	 */
	public List<HistoricActivityInstanceEntity> getByInstanceId(Long actInstId,String nodeId){
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("actInstId", actInstId);
		params.put("nodeId", nodeId);
		return this.getBySqlKey("getByInstanceId", params);
	}
	
	
	

	/**
	 * 更新执行人。
	 * @param actInstId
	 * @param nodeId
	 * @param assignee
	 */
	public void updateAssignee(HistoricActivityInstanceEntity hisActEnt){
		this.update("updateAssignee", hisActEnt);
	}
	
	/**
	 * 修改历史为开始。
	 * @param actInstId	实例ID
	 * @param nodeId	节点ID
	 */
	public void updateIsStart(Long actInstId,String nodeId){
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("processInstanceId", actInstId);
		params.put("activityId", nodeId);
		this.update("updateIsStart", params);
	}

	public List<HistoricActivityInstanceEntity> getByFilter(Map<String,Object> params){
		return	this.getBySqlKey("getByFilter", params);
	}
	
	/**
	 * 根据executionId获取流程历史实例。
	 * @param executionId
	 * @param nodeId
	 * @return
	 */
	public List<HistoricActivityInstanceEntity> getByExecutionId(String executionId,String nodeId){
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("executionId", executionId);
		params.put("nodeId", nodeId);
		return this.getBySqlKey("getByExecutionId", params);
	}
	
	public List<HistoricActivityInstanceEntity> getNotFinishByExecutionId(String executionId){
		return this.getBySqlKey("getNotFinishByExecutionId", executionId);
	}
	
	

	public List<HistoricActivityInstanceEntity> getByActInstId(
			String actInstId, String nodeId) {
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("actInstId", actInstId);
		params.put("nodeId", nodeId);
		return this.getBySqlKey("getByActInstId", params);
	}
	
	/**
	 * 根据actDefId删除任务历史
	 * @param actDefId 
	 * void
	 * @exception 
	 * @since  1.0.0
	 */
	public void delByProcDefId(String actDefId){
		delBySqlKey("delByProcDefId", actDefId);
	}
	
	
	
	
	public void updateParams(HistoricActivityInstanceEntity ent,int fromMobile){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("id", new Long( ent.getId()));
		params.put("durationInMillis", System.currentTimeMillis()-ent.getStartTime().getTime());
		params.put("endTime", new Date());
		params.put("assignee", ent.getAssignee());
		params.put("fromMobile", fromMobile);
		
		this.update("updateParams", params);
		
	}
	
	
}
