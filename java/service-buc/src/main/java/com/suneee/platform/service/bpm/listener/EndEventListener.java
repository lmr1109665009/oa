package com.suneee.platform.service.bpm.listener;

import java.io.IOException;
import java.util.*;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.util.AppConfigUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.bpm.BpmBusLinkDao;
import com.suneee.platform.dao.bpm.BpmDefinitionDao;
import com.suneee.platform.dao.bpm.BpmFormRunDao;
import com.suneee.platform.dao.bpm.TaskDao;
import com.suneee.platform.dao.form.BpmFormHandlerDao;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.event.def.EventUtil;
import com.suneee.platform.event.def.ProcessEndEvent;
import com.suneee.platform.model.bpm.BpmBusLink;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.SysTemplate;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.thread.TaskThreadService;
import com.suneee.ucp.base.util.HttpUtils;
import com.suneee.ucp.mh.dao.codeTable.ApiTaskHistoryDao;
import com.suneee.ucp.mh.model.codeTable.ApiTaskHistory;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.runtime.InterpretableExecution;
import org.springframework.stereotype.Service;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.util.AppConfigUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.bpm.BpmBusLinkDao;
import com.suneee.platform.dao.bpm.BpmDefinitionDao;
import com.suneee.platform.dao.bpm.BpmFormRunDao;
import com.suneee.platform.dao.bpm.TaskDao;
import com.suneee.platform.dao.form.BpmFormHandlerDao;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.event.def.EventUtil;
import com.suneee.platform.event.def.ProcessEndEvent;
import com.suneee.platform.model.bpm.BpmBusLink;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.SysTemplate;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.BpmProCopytoService;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.service.bpm.TaskMessageService;
import com.suneee.platform.service.bpm.thread.TaskThreadService;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.platform.service.system.SysTemplateService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.event.def.ApolloMessage;

/**
 * 结束事件监听器。
 * @author ray
 *
 */
@Service
public class EndEventListener extends BaseNodeEventListener {
	@Resource
	ProcessRunService processRunService;
	@Resource
    BpmFormRunDao bpmFormRunDao;
	@Resource
    TaskDao taskDao;
	@Resource
	BpmProCopytoService bpmProCopytoService;
	@Resource
    SysUserDao sysUserDao;
	@Resource
	SysTemplateService sysTemplateService;
	@Resource
	TaskMessageService taskMessageService;
	@Resource
    BpmBusLinkDao bpmBusLinkDao;
	@Resource
    BpmDefinitionDao bpmDefinitiondao;
	@Resource
	DictionaryService dictionaryService;
	@Resource
	private BpmFormHandlerDao bpmFormHandlerdao;
	@Resource
	private BpmFormDefService bpmFormDefService;
	@Resource
	private ApiTaskHistoryDao apiTaskHistoryDao;
	
	@Override
	protected void execute(DelegateExecution execution, String actDefId,String nodeId) {
		ExecutionEntity ent=(ExecutionEntity)execution;
		if(!ent.isEnded()) return;
		if(this.isForkTask(ent)) return;
		
		//当前的excutionId和主线程相同时。
		if(ent.getId().equals(ent.getProcessInstanceId())  && ent.getParentId()==null){
			ProcessRun processRun =processRunService.getByActInstanceId(new Long(ent.getProcessInstanceId()));
			handEnd(ent,processRun);
			//发布流程结束事件。
			ProcessEndEvent ev=new ProcessEndEvent(processRun);
			ev.setExecutionEntity(ent);
			//发布流程结束事件。
			AppUtil.publishEvent(ev);
			
			// 发布定子链消息事件
//			try {
//				sendApolloMessage(processRun);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
	
	/**
	 * 发布定子链消息
	 * @param processRun
	 * @throws Exception 
	 */
	private void sendApolloMessage(ProcessRun processRun) throws Exception{
		List<com.suneee.platform.model.system.Dictionary> dicList = dictionaryService.getByNodeKey(AppConfigUtil.get(Constants.FLOW_TYPE_KEY));
		if(dicList.isEmpty()){
			return;
		}
		
		BpmDefinition bpmDefinition = bpmDefinitiondao.getById(processRun.getDefId());
		String defKey = bpmDefinition.getDefKey();
		String itemValue = null;
		for(Dictionary dic : dicList){
			if(dic.getItemKey() == defKey){
				itemValue = dic.getItemValue();
				break;
			}
		}
		if(itemValue == null){
			return;
		}
		int msgType = -1;
		// 请假流程
		if(itemValue.equals(AppConfigUtil.get(Constants.FLOW_TYPE_LEAVE_KEY))){
			msgType = ApolloMessage.MSG_TYPE_LEAVE;
		}
		// 外出流程
		else if(itemValue.equals(AppConfigUtil.get(Constants.FLOW_TYPE_OUT_KEY))){
			msgType = ApolloMessage.MSG_TYPE_OUT;
		}
		// 出差流程
		else if(itemValue.equals(AppConfigUtil.get(Constants.FLOW_TYPE_BUSINESS_KEY))){
			msgType = ApolloMessage.MSG_TYPE_BUSINESS;
		}
		// 不是上述的三种流程不发定子链消息
		if(msgType == -1){
			return;
		}
		ProcessCmd processCmd = TaskThreadService.getProcessCmd();
		BpmFormDef formDef = bpmFormDefService.getById(processRun.getFormDefId());
		BpmFormData formData = bpmFormHandlerdao.getByKey(formDef.getBpmFormTable(), processCmd.getBusinessKey(), false);
		EventUtil.publishApolloMessageEvent(msgType, new ArrayList<Map<String, Object>>(),
				ApolloMessage.TARGET_TYPE_PUBLIC, processRun.getStatus(), formData.getMainFields());
			
	}
	
	//判断是否分发节点
	private Boolean isForkTask(ExecutionEntity execution){
		InterpretableExecution parentScopeExecution = (InterpretableExecution)execution.getParent();
		if(BeanUtils.isNotEmpty(parentScopeExecution)){
			List<? extends ActivityExecution> executions = parentScopeExecution.getExecutions();
            if(executions!=null && executions.size() > 0){
            	return true;
            }
		}
		return false;
	}
	
	private void handEnd(ExecutionEntity ent,ProcessRun processRun){
		//更新流程实例状态。
		updProcessRunStatus(processRun);
		//删除知会任务。
		delNotifyTask(ent);
		
		//更新业务中间表。
		updBusLink(processRun);
		//发送操送
		copyTo(ent,processRun);
		//如果是外部系统调用，返回状态
		ApiTaskHistory taskHistory = apiTaskHistoryDao.getByRunId(processRun.getRunId());
		if(taskHistory!=null){
			String comeFrom = taskHistory.getComeFrom();
			JSONObject json = new JSONObject();
			json.put("runId",taskHistory.getRunId());
			json.put("status",processRun.getStatus());
			json.put("actDefId",processRun.getActDefId());
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Content-Type", "application/x-www-form-urlencoded;charset=" + Constants.CHARSET_UTF8);
			String url = AppConfigUtil.get(Constants.HRMS_API_URL);
			try {
				String data = HttpUtils.sendData(url+Constants.CHANGE_STATUS,"rjson="+json.toJSONString(), headers, Constants.CHARSET_UTF8);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * 抄送。
	 * @param ent
	 * @param processRun
	 */
	private void copyTo(ExecutionEntity ent,ProcessRun processRun){
		ProcessCmd processCmd= TaskThreadService.getProcessCmd();
		//根据主键值获取流程定义
		BpmDefinition bpmDefinition= bpmDefinitiondao.getById(processRun.getDefId());
		//获取流程变量
		Map<String,Object> vars = ent.getVariables();
		
		//催办结束流程时候,processCmd 为空
		String currentUserId = processCmd==null? "0" : processCmd.getCurrentUserId().toString();
		//添加抄送任务以及发送提醒
		try {
			bpmProCopytoService.handlerCopyTask(processRun, vars,currentUserId ,bpmDefinition);
			//处理发送提醒消息给发起人
			if(StringUtil.isNotEmpty(bpmDefinition.getInformStart())){
				handSendMsgToStartUser(processRun,bpmDefinition);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	/**
	 * 更新业务中间表数据状态。
	 * @param processRun
	 */
	private void updBusLink(ProcessRun processRun){
		Long businessKey=new Long( processRun.getBusinessKey());
		BpmBusLink bpmBusLink=new BpmBusLink();
		SysUser user=(SysUser) ContextUtil.getCurrentUser();
		if(user!=null){
			bpmBusLink.setBusUpdid(user.getUserId());
			bpmBusLink.setBusUpd(user.getFullname());
		}
		bpmBusLink.setBusStatus(BpmBusLink.BUS_STATUS_END);
		bpmBusLink.setBusUpdtime(new Date());
		bpmBusLink.setBusPk(businessKey);
		bpmBusLinkDao.updateStatus(bpmBusLink);
	}
	
	/**
	 * 处理发送提醒消息给发起人
	 * @throws Exception 
	 */
	private void handSendMsgToStartUser(ProcessRun processRun,BpmDefinition bpmDefinition) throws Exception{
		String informStart=bpmDefinition.getInformStart();
		if(StringUtil.isEmpty(informStart))return;
		
		String subject = processRun.getSubject();
		if(BeanUtils.isEmpty(processRun))return;
		Long startUserId = processRun.getCreatorId();
		SysUser user = sysUserDao.getById(startUserId);
		List<SysUser> receiverUserList = new ArrayList<SysUser>();
		receiverUserList.add(user);
		
		Map<String,String> msgTempMap = sysTemplateService.getTempByFun(SysTemplate.USE_TYPE_NOTIFY_STARTUSER);
		taskMessageService.sendMessage(null, receiverUserList, informStart, msgTempMap, subject, "", null,processRun.getRunId(),null);
	}
	
	/**
	 * 流程终止时删除流程任务。
	 * <pre>
	 * 	1.删除流程实例任务。
	 *  2.删除任务的参与者。
	 *  3.删除流程表单运行情况
	 * </pre>
	 * @param ent
	 */
	private void delNotifyTask(ExecutionEntity ent){
		Long instanceId=new Long( ent.getProcessInstanceId());
		//删除知会任务
		taskDao.delSubCustTaskByInstId(instanceId);
		//删除流程表单运行情况
		bpmFormRunDao.delByInstanceId(String.valueOf(instanceId));
	}
	
	
	/**
	 * 更新流程运行状态。
	 * <pre>
	 * 1.更新流程运行状态为完成。
	 * 2.计算流程过程的时间。
	 * </pre>
	 * @param ent
	 */
	private void updProcessRunStatus(ProcessRun processRun){
		if(BeanUtils.isEmpty(processRun)) return;
		//设置流程状态为完成。
		processRun.setStatus(ProcessRun.STATUS_FINISH);
		processRunService.update(processRun);
	}

	@Override
	protected Integer getScriptType() {
		return BpmConst.EndScript;
	}

}
