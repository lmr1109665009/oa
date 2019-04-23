package com.suneee.platform.model.form;

import com.suneee.core.model.BaseModel;
import com.suneee.core.model.BaseModel;


/**
 * 对象功能:在线表单数据日志记录，记录为什么会丢失数据 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ouxb
 * 创建时间:2015-11-30 17:15:23
 */
public class BpmFormDataLog extends BaseModel {
	// 主键
	protected Long id;
	// 业务ID
	protected String businessKey;
	// 业务JSON数据
	protected String formData;
	// 流程定义版本
	protected String actDefId;
	// 流程节点
	protected String nodeId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBusinessKey() {
		return businessKey;
	}
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}
	public String getFormData() {
		return formData;
	}
	public void setFormData(String formData) {
		this.formData = formData;
	}
	public String getActDefId() {
		return actDefId;
	}
	public void setActDefId(String actDefId) {
		this.actDefId = actDefId;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	
   
  

}