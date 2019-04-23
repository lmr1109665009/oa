/**
 * 对象功能:InnoDB free: 8192 kB Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:cjj
 * 创建时间:2011-12-06 13:41:44
 */
package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.BpmNodeSet;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BpmNodeSetDao extends BaseDao<BpmNodeSet>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return BpmNodeSet.class;
	}
	
	/**
	 * 根据流程设置ID取流程节点设置
	 * @param defId
	 * @return
	 */
	public List<BpmNodeSet> getByDefId(Long defId)
	{
		return getBySqlKey("getByDefId", defId);
	}
	
	/**
	 * 
	 * 根据流程设置ID取流程节点设置(所有的)
	 * @param defId
	 * @return
	 */
	public List<BpmNodeSet> getAllByDefId(Long defId){
		return getBySqlKey("getAllByDefId", defId);
	}
	
	/**
	 * 根据流程的定义ID获取流程节点设置列表。
	 * @param actDefId	流程定义ID。
	 * @return
	 */
	public List<BpmNodeSet> getByActDef(String actDefId){
		return this.getBySqlKey("getByActDef", actDefId);
	}
	
	
	
	
	
	
	/**
	 * 通过流程发布ID及节点id获取流程设置节点实体
	 * @param actDefId
	 * @param nodeId
	 * @return
	 */
	public BpmNodeSet getByActDefIdNodeId(String actDefId,String nodeId, String parentActDefId){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("actDefId", actDefId);
		params.put("nodeId", nodeId);
		params.put("parentActDefId", parentActDefId);
		return getUnique("getByActDefIdNodeIdAndParentActDefId", params);
	}
	
	/**
	 * 通过流程发布ID及节点id获取流程设置节点实体
	 * @param actDefId
	 * @param nodeId
	 * @return
	 */
	public BpmNodeSet getByActDefIdNodeId(String actDefId,String nodeId)
	{
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("actDefId", actDefId);
		params.put("nodeId", nodeId);
		return getUnique("getByActDefIdNodeId",params);
	}
	
	/**
	 * 取得某个流程定义中对应的某个节点为汇总节点的配置
	 * @param actDefId
	 * @param joinTaskKey
	 * @return
	 */
	public BpmNodeSet getByActDefIdJoinTaskKey(String actDefId,String joinTaskKey)
	{
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("actDefId", actDefId);
		params.put("joinTaskKey", joinTaskKey);
		return getUnique("getByActDefIdJoinTaskKey",params);
	}
	
	

	/**
	 * 根据流程定义获取流程节点设置对象。
	 * @param defId
	 * @return
	 */
	public Map<String, BpmNodeSet> getMapByDefId(Long defId) {
		Map<String, BpmNodeSet> map=new HashMap<String, BpmNodeSet>();
		List<BpmNodeSet> list= getByDefId(defId);
		for(BpmNodeSet bpmNodeSet:list){
			map.put(bpmNodeSet.getNodeId(), bpmNodeSet);
		}
		return map;
		
	}
	
	/**
	 * 根据流程defId删除流程节点或删除以actDefId为父流程定义ID的流程节点。
	 * @param defId
	 * @param actDefId
	 */
	public void delByDefId(Long defId, String actDefId){
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("defId", defId);
		params.put("parentActDefId", actDefId);
		this.delBySqlKey("delByDefId", params);
	}
	
	/**
	 * 根据流程定义Id和 表单类型查询 默认表单和起始表单。
	 * @param defId
	 * @param setType 值为(1，开始表单，2，全局表单)
	 * @return
	 */
	public BpmNodeSet getBySetType(String actDefId,Short setType){
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("actDefId", actDefId);
		params.put("setType", setType);
		BpmNodeSet bpmNodeSet= this.getUnique("getBySetType", params);
		
		return bpmNodeSet;
	}
	
	/**
	 * 根据子流程定义Id、父流程定义Id和 表单类型查询 默认表单和起始表单。
	 * @param defId
	 * @param setType 值为(1，开始表单，2，全局表单)
	 * @param parentActDefId
	 * @return
	 */
	public BpmNodeSet getBySetTypeAndParentActDefId(String actDefId,Short setType, String parentActDefId){
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("actDefId", actDefId);
		params.put("setType", setType);
		params.put("parentActDefId", parentActDefId);
		return this.getUnique("getBySetTypeAndParentActDefId", params);
	}
	
	
	
	/**
	 * 取得非节点的NODESET.
	 * @param defId
	 * @return
	 */
	public List<BpmNodeSet>  getByOther(Long defId){
		List<BpmNodeSet> list=this.getBySqlKey("getByOther", defId);
		return list;
	}
	
	/**
	 * 删除起始和缺省的表单。
	 * @param defId
	 */
	public void delByStartGlobalDefId(Long defId){
		this.delBySqlKey("delByStartGlobalDefId", defId);
	}
	
	/**
	 * 删除起始和缺省的表单(子流程)。
	 * @param defId
	 * @param parentActDefId
	 */
	public void delByStartGlobalDefId(Long defId, String parentActDefId){
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("defId", defId);
		params.put("parentActDefId", parentActDefId);
		this.delBySqlKey("delByStartGlobalDefIdAndParentActDefId", params);
	}
	
	
	
	
	/**
	 * 根据actDefId获取流程节点数据。
	 * <pre>
	 * 这个关联了在线表单最新的表单id。
	 * </pre>
	 * @param actDefId
	 * @return
	 */
	public List<BpmNodeSet> getByActDefId(String actDefId){
		return this.getBySqlKey("getByActDefId", actDefId);
	}
	
	/**
	 * 根据actDefId获取流程节点数据。
	 * <pre>
	 * 这个关联了在线表单最新的表单id。
	 * </pre>
	 * @param actDefId
	 * @return
	 */
	public List<BpmNodeSet> getByActDefId(String actDefId, String parentActDefId){
		Map<String, String> params=new HashMap<String, String>();
		params.put("actDefId", actDefId);
		params.put("parentActDefId", parentActDefId);
		return this.getBySqlKey("getByActDefIdAndParentId", params);
	}
	
	/**
	 * 根据parentActDefId和defId获取流程节点数据。
	 * @param defId
	 * @param parentActDefId
	 * @param isAll (true :查询包括全局和业务表单的bpmNodeSet ，false:只是任务节点)
	 * @return
	 */
	public List<BpmNodeSet> getByDefIdAndParentActDefId(Long defId, String parentActDefId,boolean isAll){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("defId", defId);
		params.put("parentActDefId", parentActDefId);
		if (isAll) {
			params.put("isAll", "isAll");
		}
		return this.getBySqlKey("getByDefIdAndParentActDefId", params);
	}
	
	/**
	 * 根据actdefid 获取在线表单的数据。
	 * @param actDefId
	 * @return
	 */
	public List<BpmNodeSet> getOnlineFormByActDefId(String actDefId){
		return this.getBySqlKey("getOnlineFormByActDefId", actDefId);
	}
	
	/**
	 * 根据actdefid 和父流程定义ID获取在线表单的数据。
	 * @param actDefId
	 * @param parentActDefId
	 * @return
	 */
	public List<BpmNodeSet> getOnlineFormByActDefId(String actDefId, String parentActDefId){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("parentActDefId", parentActDefId);
		params.put("actDefId", actDefId);
		return this.getBySqlKey("getOnlineFormByActDefIdAndParentDefId", params);
	}
	
	/**
	 * 通过定义ID及节点Id更新isJumpForDef字段的设置
	 * @param nodeId
	 * @param actDefId
	 * @param isJumpForDef
	 */
	public void updateIsJumpForDef(String nodeId,String actDefId,Short isJumpForDef){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("isJumpForDef", isJumpForDef);
		params.put("nodeId", nodeId);
		params.put("actDefId", actDefId);
		update("updateIsJumpForDef",params);
	}
	
	public List<BpmNodeSet> getByParentActDefId(String parentActDefId){
		return this.getBySqlKey("getByParentActDefId", parentActDefId);
	}
	
	public void delByDefIdAndParentActDefId(Long defId, String parentActDefId){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("defId", defId);
		params.put("parentActDefId", parentActDefId);
		this.delBySqlKey("delByDefIdAndParentActDefId", params);
	}
	
	public void delByDefIdNodeId(String nodeId, Long defId){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("nodeId", nodeId);
		params.put("defId", defId);
		this.delBySqlKey("delByDefIdNodeId", params);
	}
	
	public List<BpmNodeSet> getParentIdByDefId(Long defId){
		return this.getBySqlKey("getParentIdByDefId", defId);
	}
	
	public List<BpmNodeSet> getParentByDefIdAndNodeId(Long defId, String nodeId){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("nodeId", nodeId);
		params.put("defId", defId);
		return this.getBySqlKey("getParentByDefIdAndNodeId", params);
	}
	
	

	public void updateScopeById(Long setId, String scope) {
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("setId", setId);
		params.put("scope", scope);
		this.update("updateScopeById", params);
	}
	
	public List<BpmNodeSet> getByDefIdOpinion(Long defId,String parentActDefId){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("defId", defId);
		params.put("parentActDefId", parentActDefId);
		
		return this.getBySqlKey("getByDefIdOpinion", params);
	}

	public List<String> getOpinionFields(String actDefId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("actDefId", actDefId);
		return this.getBySqlKeyGenericity("getOpinionFields", params);
	}
	
	/**
	 * 沟通人员设置
	 * @param setId
	 * @param communicate
	 */
	public void updateCommunicateById(Long setId, String communicate) {
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("setId", setId);
		params.put("communicate", communicate);
		this.update("updateCommunicateById", params);
	}
}