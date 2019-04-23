package com.suneee.weixin.service;

import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.form.BpmMobileFormDef;
import net.sf.json.JSONObject;

public interface IWeixinFormService {
	
	
	/**
	 * 初始显示表单数据。
	 * @param formTable		表元数据。
	 * @param isPreview		是否预览。
	 * @return
	 */
	JSONObject getByFormTable(BpmFormTable formTable, boolean isPreview);
	
	/**
	 * 获取审批数据。	
	 * @param formTable		表元数据。
	 * @param actDefId		流程定义ID
	 * @param nodeId		节点ID
	 * @param pkValue		主键
	 * @return
	 */
	JSONObject getApproveData(BpmFormTable formTable, String actDefId, String nodeId, String pkValue) throws Exception ;
	
	/**
	 * 获取草稿数据。
	 * @param formTable		表元数据。
	 * @param pkValue		主键数据。
	 * @param isRecalcute	是否重新计算。
	 * @return
	 */
	JSONObject getDraftData(BpmFormTable formTable, String pkValue, boolean isRecalcute) throws Exception;
	
	
	/**
	 * 根据流程定义ID获取表单模板定义。
	 * 
	 * 在发起流程时，程序首先在本地存储查询
	 * 
	 * 
	 * 1.首先根据流程定义ID 获取第一个节点的表单。
	 * 2.如果没有获取到，则获取全局表单。
	 * @param actDefId			流程定义ID
	 * @param needTemplate		是否取模板
	 * @return
	 */
	BpmMobileFormDef getByDefId(Long defId, String formKey, int version);
	
	
	/**
	 * 获取实例手机表单定义。
	 * @param defId
	 * @param formKey
	 * @param version
	 * @return
	 */
	BpmMobileFormDef getInstByDefId(String actDefId, String parentActDefId, String formKey, int version);
	
	
	/**
	 * 根据节点ID获取流程定义数据。
	 * 1.获取节点表单配置。
	 * 2.获取不到则获取全局表单。
	 * @param actDefId			流程定义ID
	 * @param parentActDefId	父流程定义ID
	 * @param nodeId			流程节点ID
	 * @param needTemplate		是否取模板
	 * @return
	 */
	BpmMobileFormDef getByNodeId(String actDefId, String parentActDefId, String nodeId, String localFormKey, int version);
	

}
