/**
 * 对象功能:字段权限 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:xwy
 * 创建时间:2012-02-10 10:48:16
 */
package com.suneee.platform.dao.form;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.form.BpmFormRights;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BpmFormRightsDao extends BaseDao<BpmFormRights>
{
	
	@Override
	public Class getEntityClass()
	{
		return BpmFormRights.class;
	}
	
	/**
	 * 根据表单key获取表单权限。
	 * @param formKey		表单key
	 * @param isCascade		是否层叠 获取
	 * 如果为true，则根据表单key获取相关的所有数据，
	 * 如果为false则只 获取表单数据，不获取流程定义和节点的表单数据。
	 * @return
	 */
	public List<BpmFormRights> getByFormKey(String formKey,boolean isCascade){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("formKey", formKey);
		params.put("isCascade", isCascade);
		return this.getBySqlKey("getByFormKey",params);
	}
	
	/**
	 * 根据流程定义ID和父流程定义id获取权限数据。
	 * @param actDefId			流程定义ID
	 * @param parentActDefId	父流程定义ID
	 * 如果parentActDefId为空则只获取流程定义的数据，
	 * 如果parentActDefId不为空则获取父流程定义相关权限数据。
	 * @return
	 */
	public List<BpmFormRights> getByActDefId(String actDefId,String parentActDefId){
		Map<String,String> params=new HashMap<String,String>();
		params.put("actDefId", actDefId);
		params.put("parentActDefId", parentActDefId);
		return this.getBySqlKey("getByActDefId",params);
	}
	
	/**
	 * 获取流程节点的表单权限数据。
	 * @param actDefId
	 * @param nodeId
	 * @param parentActDefId
	 * @return
	 */
	public List<BpmFormRights> getByActDefNodeId(String actDefId,String nodeId, String parentActDefId){
		Map<String,String> params=new HashMap<String,String>();
		params.put("actDefId", actDefId);
		params.put("nodeId", nodeId);
		params.put("parentActDefId", parentActDefId);
		return this.getBySqlKey("getByActDefNodeId",params);
	}
	
	/**
	 * 根据表单key删除流程定义权限数据。
	 * @param formKey
	 * @param isCascade
	 */
	public void delByFormKey(String formKey,boolean isCascade){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("formKey", formKey);
		params.put("isCascade", isCascade);
		this.delBySqlKey("delByFormKey", params);
	}
	
	/**
	 * 根据流程定义id删除表单权限数据。
	 * @param actDefId
	 * @param parentActDefId
	 */
	public void delByActDefId(String actDefId,String parentActDefId){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("actDefId", actDefId);
		params.put("parentActDefId", parentActDefId);
		this.delBySqlKey("delByActDefId", params);
	}
	
	/**
	 * 根据流程定义层叠删除表单数据。
	 * @param actDefId
	 */
	public void delByActDefIdCascade(String actDefId){
		this.delBySqlKey("delByActDefIdCascade", actDefId);
	}
	
	public void delByActDefNodeId(String actDefId,String nodeId,String parentActDefId){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("actDefId", actDefId);
		params.put("nodeId", nodeId);
		params.put("parentActDefId", parentActDefId);
		this.delBySqlKey("delByActDefNodeId", params);
	}
	
	public List<BpmFormRights>  getByActDefIdCascade(String actDefId,String parentActDefId){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("actDefId", actDefId);
		params.put("parentActDefId", parentActDefId);
		return this.getBySqlKey("getByActDefIdCascade",params);
	}
	
	
	

	
}