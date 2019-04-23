package com.suneee.platform.xml.bpm;

import com.suneee.platform.model.bpm.*;
import com.suneee.platform.model.form.BpmFormRights;
import com.suneee.platform.model.system.SysFile;
import com.suneee.platform.xml.form.BpmFormDefXml;
import com.suneee.platform.xml.table.BpmFormTableXml;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * <pre>
 * 对象功能:流程定义的XMl配置
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2012-12-29 13:59:56
 * </pre>
 */
@XmlRootElement(name = "bpmDefinitions")
@XmlAccessorType(XmlAccessType.FIELD)
public class BpmDefinitionXml {
	/**
	 * 流程定义
	 */
	@XmlElement(name = "bpmDefinition", type = BpmDefinition.class)
	private BpmDefinition bpmDefinition;

	/**
	 * 流程定义其它版本
	 */
	@XmlElementWrapper(name = "bpmDefinitionList")
	@XmlElements({ @XmlElement(name = "bpmDefinitions", type = BpmDefinitionXml.class) })
	private List<BpmDefinitionXml> bpmDefinitionXmlList;

	

	/**
	 * 流程定义规则
	 */
	@XmlElementWrapper(name = "bpmNodeRuleList")
	@XmlElements({ @XmlElement(name = "bpmNodeRule", type = BpmNodeRule.class) })
	private List<BpmNodeRule> bpmNodeRuleList;

	/**
	 * 流程事件脚本
	 */
	@XmlElementWrapper(name = "bpmNodeScriptList")
	@XmlElements({ @XmlElement(name = "bpmNodeScript", type = BpmNodeScript.class) })
	private List<BpmNodeScript> bpmNodeScriptList;

	/**
	 * 流程变量
	 */
	@XmlElementWrapper(name = "bpmDefVarList")
	@XmlElements({ @XmlElement(name = "bpmDefVar", type = BpmDefVar.class) })
	private List<BpmDefVar> bpmDefVarList;

	/**
	 * 流程会签规则
	 */
	@XmlElementWrapper(name = "bpmNodeSignList")
	@XmlElements({ @XmlElement(name = "bpmNodeSign", type = BpmNodeSign.class) })
	private List<BpmNodeSign> bpmNodeSignList;

	/**
	 * 流程消息
	 */
	@XmlElementWrapper(name = "bpmNodeMessageList")
	@XmlElements({ @XmlElement(name = "bpmNodeMessage", type = BpmNodeMessage.class) })
	private List<BpmNodeMessage> bpmNodeMessageList;

	/**
	 * 流程节点设置
	 */
	@XmlElementWrapper(name = "bpmNodeSetList")
	@XmlElements({ @XmlElement(name = "bpmNodeSet", type = BpmNodeSet.class) })
	private List<BpmNodeSet> bpmNodeSetList;

	/**
	 * 流程节点条件设置
	 */
	@XmlElementWrapper(name = "bpmUserConditionList")
	@XmlElements({ @XmlElement(name = "bpmUserCondition", type = BpmUserCondition.class) })
	private List<BpmUserCondition> bpmUserConditionList;

	/**
	 * 节点下的人员设置
	 */
	@XmlElementWrapper(name = "bpmNodeUserList")
	@XmlElements({ @XmlElement(name = "bpmNodeUser", type = BpmNodeUser.class) })
	private List<BpmNodeUser> bpmNodeUserList;
	/**
	 * 用户节点的上下级设置
	 */
	@XmlElementWrapper(name = "bpmNodeUserUplowList")
	@XmlElements({ @XmlElement(name = "bpmNodeUserUplow", type = BpmNodeUserUplow.class) })
	private List<BpmNodeUserUplow> bpmNodeUserUplowList;

	/**
	 * 流程按钮设置
	 */
	@XmlElementWrapper(name = "bpmNodeButtonList")
	@XmlElements({ @XmlElement(name = "bpmNodeButton", type = BpmNodeButton.class) })
	private List<BpmNodeButton> bpmNodeButtonList;

	/**
	 * 常用语设置
	 */
	@XmlElementWrapper(name = "taskApprovalItemsList")
	@XmlElements({ @XmlElement(name = "taskApprovalItems", type = TaskApprovalItems.class) })
	private List<TaskApprovalItems> taskApprovalItemsList;

	/**
	 * 任务节点催办时间设置
	 */
	@XmlElementWrapper(name = "taskReminderList")
	@XmlElements({ @XmlElement(name = "taskReminder", type = TaskReminder.class) })
	private List<TaskReminder> taskReminderList;


	/**
	 * 流程联动设置
	 */
	@XmlElementWrapper(name = "bpmGangedSetList")
	@XmlElements({ @XmlElement(name = "bpmGangedSet", type = BpmGangedSet.class) })
	private List<BpmGangedSet> bpmGangedSetList;
	
	/**
	 * 子流程
	 */
	@XmlElementWrapper(name = "subBpmDefinitionList")
	@XmlElements({ @XmlElement(name = "subBpmDefinitions", type = BpmDefinitionXml.class) })
	private List<BpmDefinitionXml> subBpmDefinitionXmlList;

	
	/**
	 * 对应的自定义表单
	 */
	@XmlElementWrapper(name = "bpmFormDefXmlList")
	@XmlElements({ @XmlElement(name = "formDefs", type = BpmFormDefXml.class) })
	private List<BpmFormDefXml> bpmFormDefXmlList;
	
	/**
	 * 表单权限List
	 */
	@XmlElementWrapper(name = "bpmFormRightsList")
	@XmlElements({ @XmlElement(name = "bpmFormRights", type = BpmFormRights.class) })
	private List<BpmFormRights> bpmFormRightsList;
	
	/**
	 * 对应的自定义表
	 */
	@XmlElementWrapper(name = "bpmFormTableXmlList")
	@XmlElements({ @XmlElement(name = "tables", type = BpmFormTableXml.class) })
	private List<BpmFormTableXml> bpmFormTableXmlList;
	
	/**
	 * 附件或者帮助文档
	 */
	@XmlElementWrapper(name = "sysFileList")
	@XmlElements({ @XmlElement(name = "sysFile", type = SysFile.class) })
	private List<SysFile> sysFileList;
	
	/**
	 * 流程引用
	 */
	@XmlElementWrapper(name = "bpmReferDefinitionList")
	@XmlElements({ @XmlElement(name = "bpmReferDefinition", type = BpmReferDefinition.class) })
	private List<BpmReferDefinition> bpmReferDefinitionList;
	
	/**
	 * 流程节点SQL定义。
	 */
	@XmlElementWrapper(name = "bpmNodeSqlList")
	@XmlElements({ @XmlElement(name = "bpmNodeSql", type = BpmNodeSql.class) })
	private List<BpmNodeSql> bpmNodeSqlList;
	
	/**
	 * 流程触发。
	 */
	@XmlElementWrapper(name = "bpmTriggerList")
	@XmlElements({ @XmlElement(name = "bpmTrigger", type = BpmNewFlowTrigger.class) })
	private List<BpmNewFlowTrigger> bpmTriggerList;
	
	/**
	 * 消息模版。
	 */
	@XmlElementWrapper(name = "msgTemplateList")
	@XmlElements({ @XmlElement(name = "msgTemplate", type = NodeMsgTemplate.class) })
	private List<NodeMsgTemplate> msgTemplates;
	
	
	
	
	// ==========以下是getting和setting的方法================
	public BpmDefinition getBpmDefinition() {
		return bpmDefinition;
	}

	public void setBpmDefinition(BpmDefinition bpmDefinition) {
		this.bpmDefinition = bpmDefinition;
	}

	public List<BpmDefinitionXml> getBpmDefinitionXmlList() {
		return bpmDefinitionXmlList;
	}

	public void setBpmDefinitionXmlList(
			List<BpmDefinitionXml> bpmDefinitionXmlList) {
		this.bpmDefinitionXmlList = bpmDefinitionXmlList;
	}

	

	public List<BpmNodeRule> getBpmNodeRuleList() {
		return bpmNodeRuleList;
	}

	public void setBpmNodeRuleList(List<BpmNodeRule> bpmNodeRuleList) {
		this.bpmNodeRuleList = bpmNodeRuleList;
	}

	public List<BpmNodeScript> getBpmNodeScriptList() {
		return bpmNodeScriptList;
	}

	public void setBpmNodeScriptList(List<BpmNodeScript> bpmNodeScriptList) {
		this.bpmNodeScriptList = bpmNodeScriptList;
	}

	public List<BpmDefVar> getBpmDefVarList() {
		return bpmDefVarList;
	}

	public void setBpmDefVarList(List<BpmDefVar> bpmDefVarList) {
		this.bpmDefVarList = bpmDefVarList;
	}

	public List<BpmNodeSign> getBpmNodeSignList() {
		return bpmNodeSignList;
	}

	public void setBpmNodeSignList(List<BpmNodeSign> bpmNodeSignList) {
		this.bpmNodeSignList = bpmNodeSignList;
	}

	public List<BpmNodeMessage> getBpmNodeMessageList() {
		return bpmNodeMessageList;
	}

	public void setBpmNodeMessageList(List<BpmNodeMessage> bpmNodeMessageList) {
		this.bpmNodeMessageList = bpmNodeMessageList;
	}

	public List<BpmNodeSet> getBpmNodeSetList() {
		return bpmNodeSetList;
	}

	public void setBpmNodeSetList(List<BpmNodeSet> bpmNodeSetList) {
		this.bpmNodeSetList = bpmNodeSetList;
	}

	public List<BpmUserCondition> getBpmUserConditionList() {
		return bpmUserConditionList;
	}

	public void setBpmUserConditionList(
			List<BpmUserCondition> bpmUserConditionList) {
		this.bpmUserConditionList = bpmUserConditionList;
	}

	public List<BpmNodeUser> getBpmNodeUserList() {
		return bpmNodeUserList;
	}

	public void setBpmNodeUserList(List<BpmNodeUser> bpmNodeUserList) {
		this.bpmNodeUserList = bpmNodeUserList;
	}

	public List<BpmNodeUserUplow> getBpmNodeUserUplowList() {
		return bpmNodeUserUplowList;
	}

	public void setBpmNodeUserUplowList(
			List<BpmNodeUserUplow> bpmNodeUserUplowList) {
		this.bpmNodeUserUplowList = bpmNodeUserUplowList;
	}

	public List<BpmNodeButton> getBpmNodeButtonList() {
		return bpmNodeButtonList;
	}

	public void setBpmNodeButtonList(List<BpmNodeButton> bpmNodeButtonList) {
		this.bpmNodeButtonList = bpmNodeButtonList;
	}

	public List<TaskApprovalItems> getTaskApprovalItemsList() {
		return taskApprovalItemsList;
	}

	public void setTaskApprovalItemsList(
			List<TaskApprovalItems> taskApprovalItemsList) {
		this.taskApprovalItemsList = taskApprovalItemsList;
	}

	public List<TaskReminder> getTaskReminderList() {
		return taskReminderList;
	}

	public void setTaskReminderList(List<TaskReminder> taskReminderList) {
		this.taskReminderList = taskReminderList;
	}

	public List<BpmDefinitionXml> getSubBpmDefinitionXmlList() {
		return subBpmDefinitionXmlList;
	}

	public void setSubBpmDefinitionXmlList(
			List<BpmDefinitionXml> subBpmDefinitionXmlList) {
		this.subBpmDefinitionXmlList = subBpmDefinitionXmlList;
	}

	public List<BpmFormDefXml> getBpmFormDefXmlList() {
		return bpmFormDefXmlList;
	}

	public void setBpmFormDefXmlList(List<BpmFormDefXml> bpmFormDefXmlList) {
		this.bpmFormDefXmlList = bpmFormDefXmlList;
	}

	public List<BpmFormTableXml> getBpmFormTableXmlList() {
		return bpmFormTableXmlList;
	}

	public void setBpmFormTableXmlList(List<BpmFormTableXml> bpmFormTableXmlList) {
		this.bpmFormTableXmlList = bpmFormTableXmlList;
	}

	public List<BpmFormRights> getBpmFormRightsList() {
		return bpmFormRightsList;
	}

	public void setBpmFormRightsList(List<BpmFormRights> bpmFormRightsList) {
		this.bpmFormRightsList = bpmFormRightsList;
	}

	public List<SysFile> getSysFileList() {
		return sysFileList;
	}

	public void setSysFileList(List<SysFile> sysFileList) {
		this.sysFileList = sysFileList;
	}

	public List<BpmGangedSet> getBpmGangedSetList() {
		return bpmGangedSetList;
	}

	public void setBpmGangedSetList(List<BpmGangedSet> bpmGangedSetList) {
		this.bpmGangedSetList = bpmGangedSetList;
	}

	public List<BpmReferDefinition> getBpmReferDefinitionList() {
		return bpmReferDefinitionList;
	}

	public void setBpmReferDefinitionList(List<BpmReferDefinition> bpmReferDefinitionList) {
		this.bpmReferDefinitionList = bpmReferDefinitionList;
	}

	public List<BpmNodeSql> getBpmNodeSqlList() {
		return bpmNodeSqlList;
	}

	public void setBpmNodeSqlList(List<BpmNodeSql> bpmNodeSqlList) {
		this.bpmNodeSqlList = bpmNodeSqlList;
	}

	public List<BpmNewFlowTrigger> getBpmTriggerList() {
		return bpmTriggerList;
	}

	public void setBpmTriggerList(List<BpmNewFlowTrigger> bpmTriggerList) {
		this.bpmTriggerList = bpmTriggerList;
	}

	public List<NodeMsgTemplate> getMsgTemplates() {
		return msgTemplates;
	}

	public void setMsgTemplates(List<NodeMsgTemplate> msgTemplates) {
		this.msgTemplates = msgTemplates;
	}
	
	
	
	
}
