package com.suneee.platform.service.bpm;

import com.suneee.core.api.bpm.model.IProcessRun;
import com.suneee.core.api.org.ISysUserService;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.bpm.BpmResult;
import com.suneee.core.bpm.DataType;
import com.suneee.core.bpm.model.*;
import com.suneee.core.bpm.setting.ISkipCondition;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.bpm.util.BpmUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.db.datasource.DataSourceUtil;
import com.suneee.core.model.ForkTaskReject;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.page.PageBean;
import com.suneee.core.service.BaseService;
import com.suneee.core.service.GenericService;
import com.suneee.core.table.TableModel;
import com.suneee.core.util.*;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.model.bpm.ProcessRunAmount;
import com.suneee.platform.dao.bpm.*;
import com.suneee.platform.dao.form.BpmFormFieldDao;
import com.suneee.platform.dao.form.BpmFormHandlerDao;
import com.suneee.platform.event.def.EventUtil;
import com.suneee.platform.model.bpm.*;
import com.suneee.platform.model.form.*;
import com.suneee.platform.model.system.*;
import com.suneee.platform.service.bpm.impl.BpmActService;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.bpm.thread.TaskThreadService;
import com.suneee.platform.service.bpm.thread.TaskUserAssignService;
import com.suneee.platform.service.form.*;
import com.suneee.platform.service.system.IdentityService;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.SysPropertyService;
import com.suneee.platform.service.system.SysTemplateService;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.platform.service.worktime.CalendarAssignService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.activity.ActivityRequiredException;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

/**
 * 对象功能:流程实例扩展Service类 开发公司:广州宏天软件有限公司 开发人员:csx 创建时间:2011-12-03 09:33:06
 */
@Service
public class ProcessRunService extends BaseService<ProcessRun> {

	protected Logger log = LoggerFactory.getLogger(ProcessRunService.class);
	@Resource
	BpmFormFieldService bpmFormFieldService;
	@Resource
	private ProcessRunDao dao;
	@Resource
	private BpmDefinitionDao bpmDefinitionDao;
	@Resource
	private BpmService bpmService;
	@Resource
	private TaskSignDataService taskSignDataService;
	@Resource
	private BpmFormHandlerDao bpmFormHandlerDao;
	@Resource
	private BpmFormHandlerService bpmFormHandlerService;
	@Resource
	private BpmNodeSetService bpmNodeSetService;
	@Resource
	private TaskService taskService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskUserAssignService taskUserAssignService;
	@Resource
	private TaskUserService taskUserService;
	@Resource
	private TaskOpinionDao taskOpinionDao;
	@Resource
	private BpmFormRunService bpmFormRunService;
	@Resource
	private TaskDao taskDao;
	@Resource
	private BpmRunLogService bpmRunLogService;
	@Resource
	private SysTemplateService sysTemplateService;

	@Resource
	private CalendarAssignService calendarAssignService;
	@Resource
	private ExecutionDao executionDao;
	@Resource
	private BpmFormDefService bpmFormDefService;
	@Resource
	private BpmFormTableService bpmFormTableService;
	@Resource
	private CommuReceiverService commuReceiverService;
	@Resource
	private TaskHistoryDao taskHistoryDao;
	@Resource
	private BpmTaskExeService bpmTaskExeService;
	@Resource
	private HistoryActivityDao historyActivityDao;
	@Resource
	private TaskOpinionService taskOpinionService;
	@Resource
	private BpmProCopytoService bpmProCopytoService;
	@Resource
	private BpmTaskExeDao bpmTaskExeDao;
	@Resource
	private CommuReceiverDao commuReceiverDao;
	@Resource
	private TaskReadDao taskReadDao;
	@Resource
	private BpmProCopytoDao bpmProCopytoDao;
	@Resource
	private BpmProTransToService bpmProTransToService;
	/** 流程跳转规则 */
	@Resource
	private JumpRule jumpRule;
	@Resource
	private BpmActService bpmActService;
	@Resource
	private ExecutionStackService executionStackService;
	@Resource
	private ISysUserService sysUserServiceImpl;
	@Resource
	private TaskMessageService taskMessageService;
	@Resource
	private IdentityService identityService;

	@Resource
	BpmProStatusDao bpmProStatusDao;
	@Resource
	private BpmFormFieldDao bpmFormFieldDao;
	@Resource
	private TaskForkDao taskForkDao;
	@Resource
	private SysPropertyService sysPropertyService;

	@Resource
	private BpmBusLinkService bpmBusLinkService;

	@Resource
	private SysOrgService orgService;



	public ProcessRunService() {
	}

	@Override
	protected IEntityDao<ProcessRun, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 若下一任务分发任务节点，则指定下一任务的执行人员
	 *
	 * @param processCmd
	 */
	private void setThreadTaskUser(ProcessCmd processCmd) {
		// 回退不设置用户。
		if (processCmd.isBack().intValue() == 0) {
			String[] nodeIds = processCmd.getLastDestTaskIds();
			String[] nodeUserIds = processCmd.getLastDestTaskUids();
			// 设置下一步产生任务的执行人员
			if (nodeIds != null && nodeUserIds != null) {
				taskUserAssignService.addNodeUser(nodeIds, nodeUserIds);
			}
		}
		// 设置了任务执行人。
		if (processCmd.getTaskExecutors().size() > 0) {
			taskUserAssignService.setExecutors(processCmd.getTaskExecutors());
		}
		TaskThreadService.setProcessCmd(processCmd);
	}

	/**
	 * 设置流程变量。
	 *
	 * @param taskId
	 * @param processCmd
	 */
	private void setVariables(TaskEntity entity, ProcessCmd processCmd) {
		if (BeanUtils.isEmpty(entity))
			return;
		String taskId = entity.getId();
		Map<String, Object> vars = processCmd.getVariables();
		if (BeanUtils.isNotEmpty(vars)) {
			for (Iterator<Entry<String, Object>> it = vars.entrySet().iterator(); it.hasNext();) {
				Entry<String, Object> obj = it.next();
				runtimeService.setVariable(entity.getProcessInstanceId(), obj.getKey(), obj.getValue());
				//taskService.setVariable(taskId, obj.getKey(), obj.getValue());
			}
		}
		Map<String, Object> formVars = taskService.getVariables(taskId);
		formVars.put(BpmConst.IS_EXTERNAL_CALL, processCmd.isInvokeExternal());
		formVars.put(BpmConst.FLOW_RUN_SUBJECT, processCmd.getSubject());
		// 设置线程变量。
		TaskThreadService.setVariables(formVars);
		TaskThreadService.putVariables(processCmd.getVariables());
	}

	private TaskEntity getTaskEntByCmd(ProcessCmd processCmd) {
		TaskEntity taskEntity = null;
		String taskId = processCmd.getTaskId();
		Long runId = processCmd.getRunId();
		// 根据任务id获取任务
		if (StringUtil.isNotEmpty(taskId)) {
			taskEntity = bpmService.getTask(taskId);
		}
		// 如果任务实例为空，则根据runid再次获取。
		if (taskEntity == null && runId != null && runId > 0) {
			ProcessRun processRun = this.getById(runId);
			if (processRun == null)
				return null;
			String instanceId = processRun.getActInstId();
			ProcessTask processTask = bpmService.getFirstNodeTask(instanceId);
			if (processTask == null)
				return null;
			taskEntity = bpmService.getTask(processTask.getId());

		}
		return taskEntity;
	}

	private void handFormData(ProcessCmd processCmd, BpmNodeSet bpmNodeSet, ProcessRun processRun, String parentNodeId) throws Exception {
		if (processCmd.isInvokeExternal()) 	return;

		String opinionField = bpmNodeSet.getOpinionField();
		Short opinionHtml = bpmNodeSet.getOpinionHtml();
		if (StringUtil.isNotEmpty(opinionField)) {
			processCmd.addTransientVar(BpmConst.OPINION_FIELD, opinionField);
			processCmd.addTransientVar(BpmConst.OPINION_SUPPORTHTML, opinionHtml);
		}
		handlerFormData(processCmd, processRun, parentNodeId);
	}

	/**
	 * 流程启动下一步
	 *
	 * @param processCmd
	 *            流程执行命令实体。
	 * @return ProcessRun 流程实例
	 * @throws Exception
	 */
	public ProcessRun nextProcess(ProcessCmd processCmd) throws Exception {
		ProcessRun processRun = null;
		String taskId = "";
		// 获取任务对象
		TaskEntity taskEntity = getTaskEntByCmd(processCmd);
		if (taskEntity == null)
			return null;
		String nodeId = taskEntity.getTaskDefinitionKey();
		taskId = taskEntity.getId();

		String taskStatus = taskEntity.getDescription();

		// 通知任务直接返回
		if (taskEntity.getExecutionId() == null && TaskOpinion.STATUS_COMMUNICATION.toString().equals(taskStatus)) {
			return null;
		}

		// 流转中任务，删除产生的流转状态和流转任务
		if (TaskOpinion.STATUS_TRANSTO_ING.toString().equals(taskStatus)) {
			this.handleInterveneTransTo(taskId);
		}

		// 当下一节点为条件同步节点时，可以指定执行路径
		Object nextPathObj = processCmd.getFormDataMap().get("nextPathId");
		if (nextPathObj != null) {
			bpmService.setTaskVariable(taskId, "NextPathId", nextPathObj.toString());
		}
		String parentNodeId = taskEntity.getTaskDefinitionKey();
		String actDefId = taskEntity.getProcessDefinitionId();
		String instanceId = taskEntity.getProcessInstanceId();
		String executionId = taskEntity.getExecutionId();
		BpmDefinition bpmDefinition = bpmDefinitionDao.getByActDefId(actDefId);
		processRun = dao.getByActInstanceId(new Long(instanceId));

		processCmd.addTransientVar(BpmConst.BPM_DEF, bpmDefinition);
		processCmd.setProcessRun((IProcessRun) processRun);

		// 取到当前任务是否带有分发令牌
		String taskToken = (String) taskService.getVariableLocal(taskId, TaskFork.TAKEN_VAR_NAME);
		String parentActDefId = (String) taskService.getVariable(taskId, BpmConst.FLOW_PARENT_ACTDEFID);
		BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, parentNodeId, parentActDefId);

		//将审批意见字段记录下来。
		getOpinionField(processCmd,bpmDefinition.getDefId(),parentActDefId);

		// 设置下一步包括分发任务的用户
		if (processCmd.getActDefId()==null){
			processCmd.setActDefId(actDefId);
		}
		setThreadTaskUser(processCmd);
		// 处理在线表单的业务数据
		handFormData(processCmd, bpmNodeSet, processRun, parentNodeId);
		// 调用前置处理器
		if (!processCmd.isSkipPreHandler()) {
			invokeHandler(processCmd, bpmNodeSet, true);
		}

		// 3.流程回退处理的前置处理，查找该任务节点其回退的堆栈树父节点，以便其跳转时，能跳回该节点上
		ExecutionStack parentStack = backPrepare(processCmd, taskEntity, taskToken);

		if (parentStack != null) {
			parentNodeId = parentStack.getNodeId();
		}
		// 设置会签用户或处理会签意见
		signUsersOrSignVoted(processCmd, taskEntity);
		// 设置流程变量。
		processCmd.setSubject(processRun.getSubject());
		setVariables(taskEntity, processCmd);

		// 若是自跳转方式
		if (BpmNodeSet.JUMP_TYPE_SELF.equals(processCmd.getJumpType())) {
			// 让他跳回本节点
			processCmd.setDestTask(taskEntity.getTaskDefinitionKey());
		}

		// 如果是干预添加干预审批数据
		addInterVene(processCmd, taskEntity);

		// 如果是追回的任务执行时，状态设为重新提交
		if (processRun.getStatus() == 7) {
			addRetrOrRecoverOpinion(processCmd, taskEntity, true);
		}

		// 是否仅完成当前任务
		completeTask(processCmd, taskEntity, bpmNodeSet.getIsJumpForDef());

		// 调用后置处理器这里
		if (!processCmd.isSkipAfterHandler()) {
			invokeHandler(processCmd, bpmNodeSet, false);
		}
		// 如果在流程运行主键为空，并且在processCmd不为空的情况下，我们更新流程流程运行的主键。
		if (StringUtil.isEmpty(processRun.getBusinessKey()) && StringUtil.isNotEmpty(processCmd.getBusinessKey())) {
			processRun.setBusinessKey(processCmd.getBusinessKey());
			// 设置流程主键变量。
			runtimeService.setVariable(executionId, BpmConst.FLOW_BUSINESSKEY, processCmd.getBusinessKey());
		}
		// 任务回退时，弹出历史执行记录的堆栈树节点
		if (processCmd.isBack() > 0 && parentStack != null) {
			executionStackService.pop(parentStack);
		}
		else {
			// 退回到分发节点时
			if(processCmd.isBack() > 0 && BeanUtils.isNotEmpty(processCmd.getForkTaskRejects())) {
				updateForkRejectStack(instanceId, processCmd);
			}
			else{
				// 记录执行的堆栈
				List<String> map = TaskThreadService.getExtSubProcess();
				if (BeanUtils.isEmpty(map)) {
					executionStackService.addStack(instanceId, parentNodeId, taskToken);
				} else {
					// 初始化外部子流程。
					ExecutionStack executionStack = executionStackService.getLastestStack(instanceId, taskEntity.getTaskDefinitionKey(), taskToken);
					initExtSubProcessStack(executionStack.getStackId());
				}
			}
		}

		/*
		 * 为了解决在任务自由跳转或回退时，若流程实例有多个相同Key的任务，会把相同的任务删除。 与TaskCompleteListner里的以下代码对应使用 if(processCmd!=null && (processCmd.isBack()>0 || StringUtils.isNotEmpty(processCmd.getDestTask()))){ taskDao.updateNewTaskDefKeyByInstIdNodeId (delegateTask.getTaskDefinitionKey() + "_1",delegateTask.getTaskDefinitionKey (),delegateTask.getProcessInstanceId()); }
		 */
		if (processCmd.isBack() > 0 || StringUtils.isNotEmpty(processCmd.getDestTask())) {
			// 更新其相对对应的key
			taskDao.updateOldTaskDefKeyByInstIdNodeId(taskEntity.getTaskDefinitionKey() + "_1", taskEntity.getTaskDefinitionKey(), taskEntity.getProcessInstanceId());
		}

		// 处理信息
		// String informType = processCmd.getInformType();
		taskMessageService.notify(TaskThreadService.getNewTasks(), processCmd.getInformType(), processRun.getSubject(), null, processCmd.getVoteContent(), parentActDefId);
		// 处理代理
		handleAgentTaskExe(processCmd);
		// 记录流程执行日志。
		recordLog(processCmd, taskEntity.getName(), processRun.getRunId());

		// 任务完成
		bpmTaskExeService.complete(new Long(taskId));

		List<Task> taskList = TaskThreadService.getNewTasks();


		//增加扩展关键字段；
		this.setExtendData(processRun,processCmd);

		// 有任务
		if (BeanUtils.isNotEmpty(taskList)){
			//子流程实例结束时，产生主流程任务，会错误的更新子流程的状态
			if(taskList.get(0).getProcessDefinitionId().equals(processRun.getActDefId())){
				updateStatus(processCmd, processRun);
			}
		}

		// 抛出nodeSql事件
		String action = getActionByCmdVoteAgree(processCmd.getVoteAgree());
		if (StringUtil.isNotEmpty(action)) {
			//获取cmd的中流程变量，把新的变量放置进来，以免覆盖掉之前的流程变量
			Map<String, Object> variables = processCmd.getVariables();
			Map<String, Object> dataMap = (Map) processCmd.getTransientVar("mainData");

			if(BeanUtils.isEmpty(dataMap)) dataMap = new HashMap<String, Object>();
			dataMap.put("runId", processCmd.getRunId());
			dataMap.put("actDefId", processCmd.getActDefId());
			dataMap.put("defId", processRun.getDefId());
			dataMap.put("startUser",processRun.getCreatorId().toString());
			SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
			if(BeanUtils.isNotEmpty(sysUser)){
				dataMap.put("curUserName", ContextUtil.getCurrentUser().getFullname());
			}
			dataMap.put("curUserId", ContextUtil.getCurrentUserId());
			variables.putAll(dataMap);
			processCmd.setVariables(variables);
			EventUtil.publishNodeSqlEvent(actDefId, nodeId, action, dataMap);
			EventUtil.publishTriggerNewFlowEvent(action, nodeId, processCmd);
		}

		// 正常往下执行,如果下一步的执行人和当前人相同那么直接往下执行。
		handAutoJump(bpmDefinition, processCmd);

		return processRun;
	}

	/**
	 * 流程增加扩展字段。将表单中作为可查询条件的字段保存在extend字段中
	 *
	 * @param processRun
	 * @param processCmd
	 */
	private void setExtendData(ProcessRun processRun, ProcessCmd processCmd) {
		//增加扩展关键字段；
		//获取该流程设置为可查询条件的字段。
		List<BpmFormField> queryList = bpmFormFieldService.getQueryVarByFlowDefId(processRun.getDefId());
		//获取可查绚条件字段的值
		String formData = processCmd.getFormData();
		if (StringUtil.isEmpty(formData)) {
			return;
		}
		JSONArray arr = JSONArray.fromObject("[" + formData + "]");
		Map<String, String> map = new HashMap();
		JSONObject jsonObject = arr.getJSONObject(0);
		String main = jsonObject.get("main").toString();
		if (StringUtil.isEmpty(main)) {
			return;
		}
		arr = JSONArray.fromObject("[" + main + "]");
		JSONObject mainObject = arr.getJSONObject(0);
		//获取formData的fields的值
		String fields = mainObject.get("fields").toString();
		if (StringUtil.isEmpty(fields)) {
			return;
		}
		arr = JSONArray.fromObject("[" + fields + "]");
		JSONObject fieldsObject = arr.getJSONObject(0);
		//遍历field获取每个字段的值。
		Iterator iterator = fieldsObject.keys();
		while (iterator.hasNext()) {
			String json_key = iterator.next().toString();
			String json_value = fieldsObject.get(json_key).toString();
			if (json_value == null) {
				json_value = "";
			}
			map.put(json_key, json_value);
		}
		List<Map<String, String>> resultList = new ArrayList<>();
		//获取勾选可作为查询条件的字段的值放入map中。
		String extend = processRun.getExtend();
		//如果extend不为空，则将需要判断是否要替换还是直接增加。
		if (extend != null && !"{}".equals(extend) && StringUtil.isNotEmpty(extend)) {
			//获取老的扩展数据。如果审批后有新的字段的值则替换，没有就增加（前提是勾选作为查询条件的字段）
			JSONArray extendJ = JSONArray.fromObject(extend);
			int size = extendJ.size();
			boolean flag = false;
			for (BpmFormField bpmFormField : queryList) {
				Map<String, String> fieldMap = new HashMap<>();
				if (map.containsKey(bpmFormField.getFieldName())) {
					//从数据中获取该字段的值
					String value = map.get(bpmFormField.getFieldName());
					flag = false;
					for (int i = 0; i < size; i++) {
						JSONObject fieldObj = extendJ.getJSONObject(i);
						String name = fieldObj.get("fieldName").toString();
						//如果该字段的名是作为可查询条件的字段，将原来的值替换掉
						if (name.equals(bpmFormField.getFieldName())) {
							fieldObj.put("value", value);
							extendJ.set(i, fieldObj);
							//循环一直未进入这个判断表示需要新加入的扩展字段值
							flag = true;
							continue;

						}
					}
					//如果该字段是新加的。原来的扩展extend中没有这个字段，则添加进去。
					if (!flag) {
						fieldMap.put("fieldDesc", bpmFormField.getFieldDesc());
						fieldMap.put("fieldName", bpmFormField.getFieldName());
						fieldMap.put("value", value);
						extendJ.add(fieldMap);
					}
				}
			}
			processRun.setExtend(extendJ.toString());
			//如果为空直接增加
		} else {
			//获取勾选可作为查询条件的字段的值放入map中。
			for (BpmFormField bpmFormField : queryList) {
				Map<String, String> fieldMap = new HashMap<>();
				String value = map.get(bpmFormField.getFieldName());
				fieldMap.put("fieldDesc", bpmFormField.getFieldDesc());
				fieldMap.put("fieldName", bpmFormField.getFieldName());
				fieldMap.put("value", value);
				resultList.add(fieldMap);
			}
			//将map转为json字符串。存进数据库
			if (resultList != null || resultList.size() != 0) {
				JSONArray extendJson = JSONArray.fromObject(resultList);
				processRun.setExtend(extendJson.toString());
			}

		}

	}


	//更新退回到分发节点时的堆栈
	private void updateForkRejectStack(String actInstId, ProcessCmd processCmd){
		if(processCmd.isBack() <= 0) return;
		List<ForkTaskReject> forkTaskRejects = processCmd.getForkTaskRejects();
		if(BeanUtils.isEmpty(forkTaskRejects)) return;
		for (ForkTaskReject forkTaskReject : forkTaskRejects) {
			ExecutionStack executionStack = executionStackService.getLastestStack(actInstId, forkTaskReject.getNodeId(), forkTaskReject.getToken());
			executionStackService.popFork(executionStack);
		}
	}

	private void getOpinionField(ProcessCmd processCmd, Long defId,String parentActDefId){
		List<String> list=new ArrayList<String>();
		List<BpmNodeSet>	bpmNodeSets=bpmNodeSetService.getByDefIdOpinion(defId,parentActDefId);

		for(BpmNodeSet bpmNodeSet:bpmNodeSets){
			list.add(bpmNodeSet.getOpinionField());
		}
		processCmd.addTransientVar("opinionFields", list);
	}



	// 根据cmd的审批
	private String getActionByCmdVoteAgree(Short sot) {
		if (sot == 1 || sot == 5)
			return BpmNodeSql.ACTION_AGREE;
		if (sot == 2 || sot == 6)
			return BpmNodeSql.ACTION_OPPOSITE;
		if (sot == 3)
			return BpmNodeSql.ACTION_REJECT;
		return null;

	}

	/**
	 * 完成任务跳转。
	 *
	 * @param processCmd
	 *            ProcessCmd对象
	 * @param taskEntity
	 *            流程任务实例
	 * @param isJumpForDef
	 *            规则不作用时，是否正常跳转(1,正常跳转,0,不跳转)
	 * @throws ActivityRequiredException
	 */
	private void completeTask(ProcessCmd processCmd, TaskEntity taskEntity, Short isJumpForDef) throws ActivityRequiredException {
		boolean rtn = bpmActService.allowCallActivitiBack(taskEntity.getId());
		if (rtn) {
			bpmActService.reject(taskEntity.getId(), processCmd.getDestTask());
			return;
		}
		String taskId = taskEntity.getId();
		if (processCmd.isOnlyCompleteTask()) {
			bpmService.onlyCompleteTask(taskId);
		} else if (StringUtils.isNotEmpty(processCmd.getDestTask())) {// 自由跳转或回退
			bpmService.transTo(taskEntity, processCmd.getDestTask());
		} else { // 正常流程跳转
			ExecutionEntity execution = bpmActService.getExecution(taskEntity.getExecutionId());
			// 从规则中获取跳转
			String jumpTo = jumpRule.evaluate(execution, isJumpForDef);
			bpmService.transTo(taskEntity, jumpTo);
		}
	}

	/**
	 * 执行节点跳过。
	 *
	 * @param taskList
	 * @param bpmDefinition
	 * @param processCmd
	 * @throws Exception
	 */
	private void handAutoJump(BpmDefinition bpmDefinition, ProcessCmd processCmd) throws Exception {
		List<Task> taskList = TaskThreadService.getNewTasks();
		if (BeanUtils.isEmpty(taskList) || processCmd.getIsManage() != 0)
			return;
		String skipSetting = bpmDefinition.getSkipSetting();
		if (StringUtil.isEmpty(skipSetting))
			return;
		if (processCmd.isBack().intValue() != 0)
			return;

		// 管理员不能连续执行。
		taskUserAssignService.clearExecutors();

		while(taskList.size()>0){
			Task task = taskList.get(0);
			taskList.remove(0);
			boolean rtn = canSkip(skipSetting, task, processCmd);
			if (!rtn)
				continue;
			skipTask(task, processCmd);
		}

	}

	/**
	 * 跳过节点。
	 *
	 * @param task
	 * @param processCmd
	 * @throws Exception
	 */
	private void skipTask(Task task, ProcessCmd processCmd) throws Exception {
		processCmd.setSkip(true);
		processCmd.setTaskId(task.getId());
		processCmd.setVoteAgree(TaskOpinion.STATUS_AGREE);
		if(processCmd.isStartFlow()){//如果是第一个节点那么destTask不清空
			processCmd.setStartFlow(false);
		}else{
			processCmd.setDestTask("");
		}

		nextProcess(processCmd);
		processCmd.setSkip(false);
	}

	/**
	 * 判断任务是否能否跳过。
	 *
	 * @param skipSetting
	 * @param task
	 * @return
	 */
	private boolean canSkip(String skipSetting, Task task, ProcessCmd processCmd) {
		Map<String, ISkipCondition> map = ServiceUtil.getSkipConditionMap();
		// 全局的直接跳过。
		if (skipSetting.indexOf("global") != -1) {
			ISkipCondition condition = map.get("global");
			processCmd.addTransientVar("skipCondition", condition);
			return true;
		}

		String[] arySkip = skipSetting.split(",");
		for (String skip : arySkip) {
			ISkipCondition condition = map.get(skip);
			boolean rtn = condition.canSkip(task);
			if (rtn) {
				processCmd.addTransientVar("skipCondition", condition);
				return true;
			}
		}
		return false;
	}

	/**
	 * 更新状态
	 *
	 * @param cmd
	 * @param processRun
	 */
	private void updateStatus(ProcessCmd cmd, ProcessRun processRun) {
		boolean isRecover = cmd.isRecover();
		int isBack = cmd.isBack();
		Short status = ProcessRun.STATUS_RUNNING;
		switch (isBack) {
		// 正常
		case 0:
			status = ProcessRun.STATUS_RUNNING;
			break;
		// 退回（撤销)
		case 1:
			if (isRecover)
				status = ProcessRun.STATUS_RECOVER;
			else
				status = ProcessRun.STATUS_REJECT;
			break;
		// 退回到发起人（撤销)
		case 2:
			if (isRecover) {
				status = ProcessRun.STATUS_RECOVER;
			} else {
				status = ProcessRun.STATUS_REJECT;
			}
			break;
        case 3:
            //自由退回
            status = ProcessRun.STATUS_REJECT;
            break;
		}
		processRun.setStatus(status);
		this.update(processRun);

	}
	/**
	 * 处理转办或代理
	 *
	 * @param taskList
	 * @param cmd
	 * @throws Exception
	 */
	private void handleAgentTaskExe(ProcessCmd cmd) throws Exception {
		List<Task> taskList = TaskThreadService.getNewTasks();
		if (BeanUtils.isEmpty(taskList))
			return;
		for (Task taskEntity : taskList) {
			if (!TaskOpinion.STATUS_AGENT.toString().equals(taskEntity.getDescription()))
				continue;
			String actDefId = taskEntity.getProcessDefinitionId();
			String nodeId = taskEntity.getTaskDefinitionKey();
			BpmDefinition bpmDefinition = bpmDefinitionDao.getByActDefId(actDefId);
			//TODO 需要考虑父流程，暂时放一下。
			BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId,"");
			String informType = cmd.getInformType();
			if (StringUtil.isEmpty(informType)) {
				if (!BpmDefinition.STATUS_TEST.equals(bpmDefinition.getStatus()) && StringUtil.isNotEmpty(bpmNodeSet.getInformType())) {
					informType = bpmNodeSet.getInformType();
				} else {
					informType = bpmDefinition.getInformType();
				}
				cmd.setInformType(informType);
			}

			/*
			 * // 设置代理消息的发送类型（内部消息） cmd.setInformType("3");
			 */

			String assigeeId = taskEntity.getAssignee();
			ISysUser auth = sysUserServiceImpl.getById(Long.valueOf(taskEntity.getOwner()));
			ISysUser agent = sysUserServiceImpl.getById(Long.valueOf(assigeeId));
			addAgentTaskExe(taskEntity, cmd, auth, agent);
		}
	}

	/**
	 * 添加代理数据。
	 *
	 * @param task
	 *            任务实例
	 * @param cmd
	 *            ProcessCmd对象
	 * @param auth
	 *            授权人
	 * @param agent
	 *            代理人
	 * @throws Exception
	 */
	private void addAgentTaskExe(Task task, ProcessCmd cmd, ISysUser auth, ISysUser agent) throws Exception {
		ProcessRun processRun = (ProcessRun) cmd.getProcessRun();
		if (processRun == null) {
			processRun = this.getByActInstanceId(new Long(task.getProcessInstanceId()));
		}
		String informType = cmd.getInformType();

		String memo = "[" + auth.getAliasName() + "]自动代理给[" + agent.getAliasName() + "]";
		String processSubject = processRun.getSubject();

		BpmTaskExe bpmTaskExe = new BpmTaskExe();
		bpmTaskExe.setId(UniqueIdUtil.genId());
		bpmTaskExe.setTaskId(new Long(task.getId()));
		bpmTaskExe.setAssigneeId(agent.getUserId());
		bpmTaskExe.setAssigneeName(agent.getFullname());
		bpmTaskExe.setOwnerId(auth.getUserId());
		bpmTaskExe.setOwnerName(auth.getFullname());
		bpmTaskExe.setSubject(processSubject);
		bpmTaskExe.setStatus(BpmTaskExe.STATUS_INIT);
		bpmTaskExe.setMemo(memo);
		bpmTaskExe.setCratetime(new Date());
		bpmTaskExe.setActInstId(new Long(task.getProcessInstanceId()));
		bpmTaskExe.setTaskDefKey(task.getTaskDefinitionKey());
		bpmTaskExe.setTaskName(task.getName());
		bpmTaskExe.setAssignType(BpmTaskExe.TYPE_ASSIGNEE);
		bpmTaskExe.setRunId(processRun.getRunId());
		bpmTaskExe.setTypeId(processRun.getTypeId());
		bpmTaskExe.setInformType(informType);
		bpmTaskExeService.assignSave(bpmTaskExe);
	}

	/**
	 * 初始化子流程任务堆栈。
	 *
	 * <pre>
	 * 子流程处理当作新的流程进行处理，进行初始化处理。
	 * </pre>
	 */
	private void initExtSubProcessStack(Long parentStackId) {
		List<String> list = TaskThreadService.getExtSubProcess();
		if (BeanUtils.isEmpty(list))
			return;
		List<Task> taskList = TaskThreadService.getNewTasks();
		Map<String, List<Task>> map = getMapByTaskList(taskList);
		for (String instanceId : list) {
			List<Task> tmpList = map.get(instanceId);
			executionStackService.initStack(instanceId, tmpList, parentStackId);
		}
	}

	private Map<String, List<Task>> getMapByTaskList(List<Task> taskList) {
		Map<String, List<Task>> map = new HashMap<String, List<Task>>();
		for (Task task : taskList) {
			String instanceId = task.getProcessInstanceId();
			if (map.containsKey(instanceId)) {
				map.get(instanceId).add(task);
			} else {
				List<Task> list = new ArrayList<Task>();
				list.add(task);
				map.put(instanceId, list);
			}
		}
		return map;
	}

	/**
	 * 保存沟通或流转意见，并删除任务。 设置流转的转办代理事宜为完成 删除被流转任务产生的沟通任务
	 *
	 * @param taskEntity
	 * @param taskOpinion
	 */
	public void saveOpinion(TaskEntity taskEntity, TaskOpinion taskOpinion) {
		String taskId = taskEntity.getId();
		String description = taskEntity.getDescription();
		// 处理审批意见的父流程ID
		this.dealTaskOpinSupId(taskEntity, taskOpinion);

		taskOpinionDao.add(taskOpinion);
		taskService.deleteTask(taskId);

		if (description.equals(TaskOpinion.STATUS_TRANSTO_ING.toString())) {
			this.handleInterveneTransTo(taskId);
		} else if (description.equals(TaskOpinion.STATUS_TRANSTO.toString())) {
			// 删除被流转任务产生的沟通任务
			taskDao.delCommuTaskByParentTaskId(taskId);
		}
	}

	/**
	 * 流程操作日志记录
	 *
	 * @param processCmd
	 * @throws Exception
	 */
	private void recordLog(ProcessCmd processCmd, String taskName, Long runId) throws Exception {
		String memo = "";
		Integer type = -1;
		// 撤销
		if (processCmd.isRecover()) {
			type = BpmRunLog.OPERATOR_TYPE_RETRIEVE;
			memo = "用户在任务节点[" + taskName + "]执行了撤销操作。";
		}
		// 退回
		else if (BpmConst.TASK_BACK.equals(processCmd.isBack())) {
			type = BpmRunLog.OPERATOR_TYPE_REJECT;
			memo = "用户在任务节点[" + taskName + "]执行了退回操作。";
		}
		// 退回到发起人
		else if (BpmConst.TASK_BACK_TOSTART.equals(processCmd.isBack())) {
			type = BpmRunLog.OPERATOR_TYPE_REJECT2SPONSOR;
			memo = "用户在任务节点[" + taskName + "]执行了退回到发起人操作。";
		}else if (BpmConst.TASK_BACK_TOANYNODE.equals(processCmd.isBack())){
			type = BpmRunLog.OPERATOR_TYPE_REJECT2ANYNODE;
			memo = "用户在任务节点[" + taskName + "]执行了自由退回操作。";
		}else {
			// 同意
			if (TaskOpinion.STATUS_AGREE.equals(processCmd.getVoteAgree())) {
				type = BpmRunLog.OPERATOR_TYPE_AGREE;
				memo = "用户在任务节点[" + taskName + "]执行了同意操作。";
			}
			// 反对
			else if (TaskOpinion.STATUS_REFUSE.equals(processCmd.getVoteAgree())) {
				type = BpmRunLog.OPERATOR_TYPE_OBJECTION;
				memo = "用户在任务节点[" + taskName + "]执行了反对操作。";
			}
			// 弃权
			else if (TaskOpinion.STATUS_ABANDON.equals(processCmd.getVoteAgree())) {
				type = BpmRunLog.OPERATOR_TYPE_ABSTENTION;
				memo = "用户在任务节点[" + taskName + "]执行了弃权操作。";
			}
			// 更改执行路径
			if (TaskOpinion.STATUS_CHANGEPATH.equals(processCmd.getVoteAgree())) {
				type = BpmRunLog.OPERATOR_TYPE_CHANGEPATH;
				memo = "用户在任务节点[" + taskName + "]执行了更改执行路径操作。";
			}
		}
		if (type == -1)
			return;

		bpmRunLogService.addRunLog(runId, type, memo);
	}

	/**
	 * 回退准备。
	 *
	 * @param processCmd
	 * @param taskEntity
	 * @param taskToken
	 * @param backToNodeId
	 * @return
	 * @throws Exception
	 */
	private ExecutionStack backPrepare(ProcessCmd processCmd, TaskEntity taskEntity, String taskToken) throws Exception {
		String instanceId = taskEntity.getProcessInstanceId();
		if (processCmd.isBack() == 0)
			return null;

		// 获取流程定义id
		String actDefId = taskEntity.getProcessDefinitionId();
		String backToNodeId = processCmd.getStartNode();

		if (StringUtils.isEmpty(backToNodeId)) {
			backToNodeId = NodeCache.getFirstNodeId(actDefId).getNodeId();
		}

		ExecutionStack parentStack = null;
		// 退回
		if (processCmd.isBack().equals(BpmConst.TASK_BACK)) {
			if(BeanUtils.isEmpty(processCmd.getForkTaskRejects())){
				//解决在退回时如果勾选了执行路径会导致退回到所选节点的问题
				processCmd.setDestTask("");
			}
			parentStack = executionStackService.backPrepared(processCmd, taskEntity, taskToken);
		}
		// 退回到发起人
		else if (BpmConst.TASK_BACK_TOSTART.equals(processCmd.isBack())) {
			// 获取发起节点
			parentStack = executionStackService.getLastestStack(instanceId, backToNodeId, null);
			if (parentStack != null) {
				processCmd.setDestTask(parentStack.getNodeId());
				taskUserAssignService.addNodeUser(parentStack.getNodeId(), parentStack.getAssignees());
			}
		}else if (BpmConst.TASK_BACK_TOANYNODE.equals(processCmd.isBack())){
			//退回任意节点
			parentStack = executionStackService.getLastestStack(instanceId, backToNodeId, null);
			if (parentStack!=null){
				processCmd.setDestTask(parentStack.getNodeId());
				if (parentStack.getIsMultiTask()==ExecutionStack.MULTI_TASK){
					List<ProcessTaskHistory> taskList=taskHistoryDao.getByTaskIds(parentStack.getTaskIds());
					String[] userIds=new String[taskList.size()];
					int i=0;
					for (ProcessTaskHistory taskItem:taskList){
						userIds[i]=taskItem.getAssignee();
						i++;
					}
					taskUserAssignService.addNodeUser(parentStack.getNodeId(), StringUtil.join(userIds,"#"));
				}else {
					taskUserAssignService.addNodeUser(parentStack.getNodeId(), parentStack.getAssignees());
				}
			}
		}
		return parentStack;
	}

	/**
	 * 更新意见。
	 *
	 * <pre>
	 * 1.首先根据任务id查询意见，应为意见在task创建时这个意见就被产生出来，所以能直接获取到该意见。
	 * 2.获取意见，注意一个节点只允许一个意见填写，不能在同一个任务节点上同时允许两个意见框的填写，比如同时科员意见，局长意见等。
	 * 3.更新意见。
	 * </pre>
	 *
	 * @param optionsMap
	 * @param taskId
	 */
	private void setOpinionByForm(Map<String, String> optionsMap, ProcessCmd cmd) {
		if (BeanUtils.isEmpty(optionsMap))	return;

		Set<String> set = optionsMap.keySet();
		for (Iterator<String> it = set.iterator(); it.hasNext();) {
			String key = it.next();
			String value = optionsMap.get(key);
			if (StringUtil.isNotEmpty(value)) {
				cmd.setVoteFieldName(key);
				cmd.setVoteContent(value);
				break;
			}
		}
	}

	/**
	 * 会签用户的设置及会签投票的处理。
	 *
	 * <pre>
	 * 		1.从上下文中获取会签人员数据，如果会签人员数据不为空，则把人员绑定到线程，供会签任务节点产生会签用户使用。
	 * 		2.如果从上下文中获取了投票的数据。
	 * 			1.进行投票操作。
	 * 			2.设置流程状态，设置会签的意见，状态。
	 * </pre>
	 *
	 * @param processCmd
	 * @param taskId
	 * @param taskDefKey
	 */
	private void signUsersOrSignVoted(ProcessCmd processCmd, TaskEntity taskEntity) {
		// 处理后续的节点若有为多实例时，把页面中提交过来的多实例的人员放置至线程中，在后续的任务创建中进行获取
		String nodeId = taskEntity.getTaskDefinitionKey();
		String taskId = taskEntity.getId();
		// 判断当前任务是否会多实例会签任务
		boolean isSignTask = bpmService.isSignTask(taskEntity);
		// 是会签任务将人员取出并设置会签人员。
		if (isSignTask) {
			// 添加补签意见
			// 查询是否存在审批意见，存在这说明是会签人员，不需要再添加审批意见，没有则说明是新添加的补签人员，需要添加审批意见
			TaskOpinion taskOpinion = taskOpinionService.getByTaskId(new Long(taskEntity.getId()));

			if (BeanUtils.isEmpty(taskOpinion)) {
				addRetrOrRecoverOpinion(processCmd, taskEntity, false);
			}

			Map<String, List<TaskExecutor>> executorMap = processCmd.getTaskExecutor();
			if (executorMap != null && executorMap.containsKey(nodeId)) {
				List<TaskExecutor> executorList = executorMap.get(nodeId);
				taskUserAssignService.setExecutors(executorList);
			}
		}

		if (processCmd.getVoteAgree() != null) {// 加入任务的处理结果及意见
			if (isSignTask) {// 加上会签投票
				taskSignDataService.signVoteTask(taskId, processCmd.getVoteContent(), processCmd.getVoteAgree());
			}
			processCmd.getVariables().put(BpmConst.NODE_APPROVAL_STATUS + "_" + nodeId, processCmd.getVoteAgree());
			// processCmd.getVariables().put(BpmConst.NODE_APPROVAL_CONTENT + "_" + nodeId,processCmd.getVoteContent());
		}

	}

	/**
	 * 补签或追回后重新提交时添加审批意见。
	 *
	 * @param processCmd
	 * @param taskEntity
	 * @param isRecover
	 */
	private void addRetrOrRecoverOpinion(ProcessCmd processCmd, TaskEntity taskEntity, boolean isRecover) {
		String opinion = processCmd.getVoteContent();
		Long opinionId = UniqueIdUtil.genId();
		SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();

		TaskOpinion taskOpinion = new TaskOpinion();
		taskOpinion.setOpinionId(opinionId);
		taskOpinion.setOpinion(opinion);
		taskOpinion.setTaskId(Long.valueOf(taskEntity.getId()));
		taskOpinion.setActDefId(taskEntity.getProcessDefinitionId());
		taskOpinion.setActInstId(taskEntity.getProcessInstanceId());
		taskOpinion.setStartTime(new Date());
		taskOpinion.setEndTime(new Date());
		taskOpinion.setExeUserId(sysUser.getUserId());
//		taskOpinion.setExeFullname(sysUser.getFullname());
		taskOpinion.setExeFullname(ContextSupportUtil.getUsername(sysUser));
		taskOpinion.setTaskKey(taskEntity.getTaskDefinitionKey());
		taskOpinion.setTaskName(taskEntity.getName());
		if (isRecover) {
			taskOpinion.setCheckStatus(TaskOpinion.STATUS_RESUBMIT);
		} else {
			taskOpinion.setCheckStatus(TaskOpinion.STATUS_AGREE);
		}

		// 处理审批意见的父流程ID
		this.dealTaskOpinSupId(taskEntity, taskOpinion);

		// 增加流程意见
		taskOpinionDao.add(taskOpinion);

	}

	/**
	 * 启动流程。
	 *
	 * <pre>
	 * 	1.启动流程没有主键创建一个主键。
	 * 	2.如果有关联的实例ID，也创建一个新的主键。
	 * </pre>
	 *
	 * @param processCmd
	 * @param userId
	 * @return
	 */
	private ProcessInstance startWorkFlow(ProcessCmd processCmd) {
		String businessKey = processCmd.getBusinessKey();
		String userId = ContextUtil.getCurrentUserId().toString();
		// 获取需要跳转到节点。
		String startNode = processCmd.getStartNode();
		// 取得关联运行ID。
		Long relRunId = processCmd.getRelRunId();
		ProcessInstance processInstance = null;
		if (StringUtil.isNotEmpty(businessKey)) {
			processCmd.addVariable(BpmConst.FLOW_BUSINESSKEY, businessKey);
		}
		// 如果主键为空，那么生成一个业务主键。
		// 如果传入了关联的relRunId实例ID
		// 那么启动流程是产生一个唯一的ID，避免流程引擎冲突。
		if (StringUtil.isEmpty(businessKey)  || (BeanUtils.isNotIncZeroEmpty(relRunId))) {
			businessKey = String.valueOf(UniqueIdUtil.genId());
		}

		// 设置流程变量[startUser]。
		Authentication.setAuthenticatedUserId(userId);
		if (processCmd.getActDefId() != null) {
			// 起始节点走向指定的第一个节点
			if (StringUtil.isNotEmpty(startNode)) {
				bpmService.reDrawLink(processCmd.getActDefId(), startNode);
			}
			processInstance = bpmService.startFlowById(processCmd.getActDefId(), businessKey, processCmd.getVariables());
		} else {
			// 起始节点走向指定的第一个节点
			if (StringUtil.isNotEmpty(startNode)) {
				BpmDefinition bpmDefinition = bpmDefinitionDao.getMainByDefKey(processCmd.getFlowKey());
				if (BeanUtils.isNotEmpty(bpmDefinition) && StringUtil.isNotEmpty(bpmDefinition.getActDefId())) {
					bpmService.reDrawLink(bpmDefinition.getActDefId(), startNode);
				}
			}
			processInstance = bpmService.startFlowByKey(processCmd.getFlowKey(), businessKey, processCmd.getVariables());
		}
		Authentication.setAuthenticatedUserId(null);

		return processInstance;
	}

	/**
	 * 执行前后处理器。 0 代表失败 1代表成功，-1代表不需要执行
	 *
	 * @param processCmd
	 * @param bpmNodeSet
	 * @param isBefore
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private void invokeHandler(ProcessCmd processCmd, BpmNodeSet bpmNodeSet, boolean isBefore) throws Exception {
		if (bpmNodeSet == null)
			return;
		String handler = "";
		if (isBefore) {
			handler = bpmNodeSet.getBeforeHandler();
		} else {
			handler = bpmNodeSet.getAfterHandler();
		}
		if (StringUtil.isEmpty(handler))
			return;

		String[] aryHandler = handler.split("[.]");
		if (aryHandler != null) {
			String beanId = aryHandler[0];
			String method = aryHandler[1];
			// 触发该Bean下的业务方法
			Object serviceBean = AppUtil.getBean(beanId);
			if (serviceBean != null) {
				Method invokeMethod = serviceBean.getClass().getDeclaredMethod(method, new Class[] { ProcessCmd.class });
				invokeMethod.invoke(serviceBean, processCmd);
			}
		}
	}



	/**
	 * 根据cmd 关联实例ID设置业务主键。
	 *
	 * <pre>
	 * 1.判断cmd中是否传入了关联实例ID。
	 * 2.没有再判断草稿中是否记录了关联实例ID。
	 * 3.根据实例ID取得业务主键。
	 * </pre>
	 *
	 * @param processCmd
	 */
	private void handRelRun(ProcessCmd processCmd, ProcessRun processRun) {
		Long relRunId = processCmd.getRelRunId();
		// 如果传入为空，则判断草稿中是否存在。
		if (BeanUtils.isZeroEmpty(relRunId)) {
			// 没有草稿直接返回。
			if (processRun == null)
				return;
			relRunId = processRun.getRelRunId();
		}

		if (BeanUtils.isZeroEmpty(relRunId))
			return;

		String businessKey = getBusinessKeyByRelRunId(relRunId);
		processCmd.setBusinessKey(businessKey);
		// 设置关联数据ID。
		processRun.setRelRunId(relRunId);
	}

	/**
	 * 处理业务表单数据。
	 *
	 * @param processCmd
	 * @param processRun
	 * @throws Exception
	 */
	private void handForm(ProcessCmd processCmd, ProcessRun processRun) throws Exception {
		if (processCmd.isInvokeExternal())
			return;
		BpmFormData bpmFormData = handlerFormData(processCmd, processRun, "");
		if (bpmFormData == null)
			return;

		processRun.setTableName(bpmFormData.getTableName());
		if (bpmFormData.getPkValue() != null) {
			processRun.setPkName(bpmFormData.getPkValue().getName());
			processRun.setDsAlias(bpmFormData.getDsAlias());
		}
	}

	private void checkBpmDefValid(BpmDefinition bpmDefinition) throws Exception {

		// 通过webservice启动流程时，传入的actDefId或者flowKey不正确时抛出这个异常
		if (bpmDefinition == null)
			throw new Exception("没有该流程的定义");
		// 流程状态不为启用并且不为测试状态。
		if (!BpmDefinition.STATUS_ENABLED.equals(bpmDefinition.getStatus()) && !BpmDefinition.STATUS_TEST.equals(bpmDefinition.getStatus()))
			throw new Exception("该流程已经被禁用");
	}

	private BpmDefinition getBpmDefinitionByCmd(ProcessCmd processCmd) {
		BpmDefinition bpmDefinition = (BpmDefinition) processCmd.getTransientVar(BpmConst.BPM_DEF);
		if (bpmDefinition == null) {
			if (StringUtil.isNotEmpty(processCmd.getFlowKey())) {
				bpmDefinition = bpmDefinitionDao.getByActDefKeyIsMain(processCmd.getFlowKey());
			} else if (StringUtil.isNotEmpty(processCmd.getActDefId())) {
				bpmDefinition = bpmDefinitionDao.getByActDefId(processCmd.getActDefId());
			}

			if (bpmDefinition != null) {
				processCmd.addTransientVar(BpmConst.BPM_DEF, bpmDefinition);
			}
		}

		return bpmDefinition;

	}

	/**
	 * 启动流程。<br>
	 *
	 * <pre>
	 * 步骤：
	 * 1.表单数据保存。
	 * 2.启动流程。
	 * 3.记录流程运行情况。
	 * 4.记录流程执行堆栈。
	 * 5.根据流程的实例ID，查询任务ID。
	 * 6.取得任务Id，并完成该任务。
	 * 7.记录流程堆栈。
	 * </pre>
	 *
	 * @param processCmd
	 * @return
	 * @throws Exception
	 */
	public ProcessRun startProcess(ProcessCmd processCmd) throws Exception {
		processCmd.setStartFlow(true);//标记是该任务是启动流程
		// 通过流程key获取流程定义
		BpmDefinition bpmDefinition = getBpmDefinitionByCmd(processCmd);
		// 检查流程定义是否有效。
		checkBpmDefValid(bpmDefinition);
		// 是否跳转到第一个流程节点
		Long defId = bpmDefinition.getDefId();
		// 是否跳过第一个任务节点，当为1 时，启动流程后完成第一个任务。
		Short toFirstNode = bpmDefinition.getToFirstNode();
		//是否为父流程触发新流程
		Boolean triggerFirstStep= (Boolean) processCmd.getVariables().get("triggerFirstStep");
		if (triggerFirstStep!=null&&triggerFirstStep==true){
			toFirstNode=0;
		}
		String actDefId = bpmDefinition.getActDefId();

		// 获取第一个节点
		String startNode = processCmd.getStartNode();
		String nodeId = startNode;
		if (StringUtil.isEmpty(startNode)) {
			nodeId = NodeCache.getStartNode(actDefId).getNodeId();
		}
		// 开始节点
		BpmNodeSet bpmNodeSet =bpmNodeSetService.getStartBpmNodeSet(actDefId, false);

		SysUser sysUser =ContextSupportUtil.getCurrentUser();
		ProcessRun processRun = (ProcessRun) processCmd.getProcessRun();

		// 设置跳过第一个节点配置，用户判断代理
		TaskThreadService.setToFirstNode(toFirstNode);
		// 如果第一步跳转，那么设置发起人为任务执行人。
		if (toFirstNode == 1) {
			List<TaskExecutor> excutorList = new ArrayList<TaskExecutor>();
//			excutorList.add(TaskExecutor.getTaskUser(sysUser.getUserId().toString(), sysUser.getFullname()));
			excutorList.add(TaskExecutor.getTaskUser(sysUser.getUserId().toString(), ContextSupportUtil.getUsername(sysUser)));
			taskUserAssignService.addNodeUser(nodeId, excutorList);
		}
		// 设置下一步包括分发任务的用户
		setThreadTaskUser(processCmd);
		// 在线表单数据处理
		String businessKey = "";
		// 初始流程运行记录。
		if (BeanUtils.isEmpty(processRun)) {
			processRun = initProcessRun(bpmDefinition, processCmd,bpmNodeSet);
			// 根据草稿启动流程
		} else {
			processRun.setCreatetime(new Date());
			businessKey = processRun.getBusinessKey();
		}
		// 处理关联流程实例CMD对象。
		handRelRun(processCmd, processRun);
		// 启动流程后，处理业务表单，外部调用不触发表单处理
		handForm(processCmd, processRun);



		// 调用前置处理器
		if (!processCmd.isSkipPreHandler()) {
			invokeHandler(processCmd, bpmNodeSet, true);
		}
		if (StringUtil.isEmpty(businessKey)) {
			businessKey = processCmd.getBusinessKey();
		}

		SysOrg sysOrg = (SysOrg) ContextUtil.getCurrentOrg();
		// 增加发起人组织流程变量。
		if (sysOrg != null) {
			processCmd.addVariable(BpmConst.START_ORG_ID, sysOrg.getOrgId());
		}
		// 添加主流程ID
		processCmd.addVariable(BpmConst.FLOW_MAIN_ACTDEFID, processRun.getActDefId());
		// 添加流程runId。
		processCmd.addVariable(BpmConst.FLOW_RUNID, processRun.getRunId());
		// 设置processRun对象属性。
		// 设置processRun业务主键。
		// 生成流程定义标题
		processRun.setBusinessKey(businessKey);
		processRun.setStatus(ProcessRun.STATUS_RUNNING);
		if (sysOrg != null) {
			processRun.setStartOrgId(sysOrg.getOrgId());
			processRun.setStartOrgName(sysOrg.getOrgName());
		}
		// 添加选择的节点
		if (StringUtil.isNotEmpty(startNode)) {
			processRun.setStartNode(startNode);
		}
		// 启动流程
		ProcessInstance processInstance = startWorkFlow(processCmd);

		/**
		 * 设置流程标题。
		 */
		if (BeanUtils.isEmpty(processCmd.getProcessRun())) {
			String subject = getSubject(bpmDefinition, processCmd);
			processRun.setSubject(subject);
			processCmd.addVariable(BpmConst.FLOW_RUN_SUBJECT, subject);
		}

		String processInstanceId = processInstance.getProcessInstanceId();

		processRun.setActInstId(processInstanceId);
		//增加扩展关键字段；
		this.setExtendData(processRun,processCmd);
		// 更新或添加流程实例
		if (BeanUtils.isEmpty(processCmd.getProcessRun())) {
			this.add(processRun);
		} else {
			this.update(processRun);
		}

		List<Task> taskList = TaskThreadService.getNewTasks();
		// 初始化执行的堆栈树
		executionStackService.initStack(processInstanceId, taskList, 0L);

		processCmd.setProcessRun((IProcessRun) processRun);
		// 后置处理器
		if (!processCmd.isSkipAfterHandler()) {
			invokeHandler(processCmd, bpmNodeSet, false);
		}

		// 获取下一步的任务并完成跳转。
		if (toFirstNode == 1) {
			handJumpOverFirstNode(processInstanceId, processCmd);
		}
		// 添加运行期表单，外部调用时不对表单做处理。
		if (!processCmd.isInvokeExternal()) {
			bpmFormRunService.addFormRun(actDefId, processRun.getRunId(), processInstanceId);
		}

		// 处理信息
		// String informType = processCmd.getInformType();
		// 处理信息
		if(toFirstNode!=1) {
			taskMessageService.notify(TaskThreadService.getNewTasks(), "", processRun.getSubject(), null, "", "");
		}
		// 抛出事件
		EventUtil.publishNodeSqlEvent(actDefId, nodeId, BpmNodeSql.ACTION_SUBMIT, (Map) processCmd.getTransientVar("mainData"));

		// 添加到流程运行日志
		String memo = "启动流程:" + processRun.getSubject();
		bpmRunLogService.addRunLog(processRun.getRunId(), BpmRunLog.OPERATOR_TYPE_START, memo);

		// 更新流程历史实例为 开始标记为1。
		if (toFirstNode == 1) {
			historyActivityDao.updateIsStart(Long.parseLong(processInstanceId), nodeId);
		}
		// 增加流程提交的审批历史
		else {
			addSubmitOpinion(processRun, processCmd);
		}

		//在不跳过第一个节点的时候才代理出去
		if (toFirstNode != 1) {
			// 处理代理
			handleAgentTaskExe(processCmd);
		}
		return processRun;
	}
	public ProcessRun startProcess(ProcessCmd processCmd, String account) throws Exception{

		processCmd.setStartFlow(true);//标记是该任务是启动流程
		// 通过流程key获取流程定义
		BpmDefinition bpmDefinition = getBpmDefinitionByCmd(processCmd);
		// 检查流程定义是否有效。
		checkBpmDefValid(bpmDefinition);
		// 是否跳转到第一个流程节点
		Long defId = bpmDefinition.getDefId();
		// 是否跳过第一个任务节点，当为1 时，启动流程后完成第一个任务。
		Short toFirstNode = bpmDefinition.getToFirstNode();
		//是否为父流程触发新流程
		Boolean triggerFirstStep= (Boolean) processCmd.getVariables().get("triggerFirstStep");
		if (triggerFirstStep!=null&&triggerFirstStep==true){
			toFirstNode=0;
		}
		String actDefId = bpmDefinition.getActDefId();

		// 获取第一个节点
		String startNode = processCmd.getStartNode();
		String nodeId = startNode;
		if (StringUtil.isEmpty(startNode)) {
			nodeId = NodeCache.getStartNode(actDefId).getNodeId();
		}
		// 开始节点
		BpmNodeSet bpmNodeSet =bpmNodeSetService.getStartBpmNodeSet(actDefId, false);
		//SysUser sysUser = sysUserService.getByAccount(account);
		ISysUser sysUser =ContextUtil.getCurrentUser();



		ProcessRun processRun = (ProcessRun) processCmd.getProcessRun();

		// 设置跳过第一个节点配置，用户判断代理
		TaskThreadService.setToFirstNode(toFirstNode);
		// 如果第一步跳转，那么设置发起人为任务执行人。
		if (toFirstNode == 1) {
			List<TaskExecutor> excutorList = new ArrayList<TaskExecutor>();
//			excutorList.add(TaskExecutor.getTaskUser(sysUser.getUserId().toString(), sysUser.getFullname()));
			excutorList.add(TaskExecutor.getTaskUser(sysUser.getUserId().toString(), ContextSupportUtil.getUsername((SysUser) sysUser)));
			taskUserAssignService.addNodeUser(nodeId, excutorList);
		}
		// 设置下一步包括分发任务的用户
		setThreadTaskUser(processCmd);
		// 在线表单数据处理
		String businessKey = "";
		// 初始流程运行记录。
		if (BeanUtils.isEmpty(processRun)) {
			processRun = initProcessRun(bpmDefinition, processCmd,bpmNodeSet);
			// 根据草稿启动流程
		} else {
			processRun.setCreatetime(new Date());
			businessKey = processRun.getBusinessKey();
		}
		// 处理关联流程实例CMD对象。
		handRelRun(processCmd, processRun);
		// 启动流程后，处理业务表单，外部调用不触发表单处理
		handForm(processCmd, processRun);



		// 调用前置处理器
		if (!processCmd.isSkipPreHandler()) {
			invokeHandler(processCmd, bpmNodeSet, true);
		}
		if (StringUtil.isEmpty(businessKey)) {
			businessKey = processCmd.getBusinessKey();
		}

		SysOrg sysOrg = (SysOrg) ContextUtil.getCurrentOrg();
		// 增加发起人组织流程变量。
		if (sysOrg != null) {
			processCmd.addVariable(BpmConst.START_ORG_ID, sysOrg.getOrgId());
		}
		// 添加主流程ID
		processCmd.addVariable(BpmConst.FLOW_MAIN_ACTDEFID, processRun.getActDefId());
		// 添加流程runId。
		processCmd.addVariable(BpmConst.FLOW_RUNID, processRun.getRunId());
		// 设置processRun对象属性。
		// 设置processRun业务主键。
		// 生成流程定义标题
		processRun.setBusinessKey(businessKey);
		processRun.setStatus(ProcessRun.STATUS_RUNNING);
		if (sysOrg != null) {
			processRun.setStartOrgId(sysOrg.getOrgId());
			processRun.setStartOrgName(sysOrg.getOrgName());
		}
		// 添加选择的节点
		if (StringUtil.isNotEmpty(startNode)) {
			processRun.setStartNode(startNode);
		}
		// 启动流程
		ProcessInstance processInstance = startWorkFlow(processCmd);

		/**
		 * 设置流程标题。
		 */
		if (BeanUtils.isEmpty(processCmd.getProcessRun())) {
			String subject = getSubject(bpmDefinition, processCmd);
			processRun.setSubject(subject);
			processCmd.addVariable(BpmConst.FLOW_RUN_SUBJECT, subject);
		}

		String processInstanceId = processInstance.getProcessInstanceId();

		processRun.setActInstId(processInstanceId);
		//增加扩展关键字段；
		this.setExtendData(processRun,processCmd);
		// 更新或添加流程实例
		if (BeanUtils.isEmpty(processCmd.getProcessRun())) {
			this.add(processRun);
		} else {
			this.update(processRun);
		}

		List<Task> taskList = TaskThreadService.getNewTasks();
		// 初始化执行的堆栈树
		executionStackService.initStack(processInstanceId, taskList, 0L);

		processCmd.setProcessRun((IProcessRun) processRun);
		// 后置处理器
		if (!processCmd.isSkipAfterHandler()) {
			invokeHandler(processCmd, bpmNodeSet, false);
		}

		// 获取下一步的任务并完成跳转。
		if (toFirstNode == 1) {
			handJumpOverFirstNode(processInstanceId, processCmd);
		}
		// 添加运行期表单，外部调用时不对表单做处理。
		if (!processCmd.isInvokeExternal()) {
			bpmFormRunService.addFormRun(actDefId, processRun.getRunId(), processInstanceId);
		}

		// 处理信息
		// String informType = processCmd.getInformType();
		// 处理信息
		if(toFirstNode!=1) {
			taskMessageService.notify(TaskThreadService.getNewTasks(), "", processRun.getSubject(), null, "", "");
		}
		// 抛出事件
		EventUtil.publishNodeSqlEvent(actDefId, nodeId, BpmNodeSql.ACTION_SUBMIT, (Map) processCmd.getTransientVar("mainData"));

		// 添加到流程运行日志
		String memo = "启动流程:" + processRun.getSubject();
		bpmRunLogService.addRunLog(processRun.getRunId(), BpmRunLog.OPERATOR_TYPE_START, memo);

		// 更新流程历史实例为 开始标记为1。
		if (toFirstNode == 1) {
			historyActivityDao.updateIsStart(Long.parseLong(processInstanceId), nodeId);
		}
		// 增加流程提交的审批历史
		else {
			addSubmitOpinion(processRun, processCmd);
		}

		//在不跳过第一个节点的时候才代理出去
		if (toFirstNode != 1) {
			// 处理代理
			handleAgentTaskExe(processCmd);
		}
		return processRun;

	}
	/**
	 * 添加提交意见。
	 *
	 * @param processRun
	 * @param processCmd
	 */
	private void addSubmitOpinion(ProcessRun processRun, ProcessCmd processCmd) {
		TaskOpinion opinion = new TaskOpinion();
		Long startUserId = processRun.getCreatorId();
		ISysUser startUser = sysUserServiceImpl.getById(startUserId);
		opinion.setOpinionId(UniqueIdUtil.genId());
		opinion.setCheckStatus(TaskOpinion.STATUS_SUBMIT);
		opinion.setActInstId(processRun.getActInstId());
//		opinion.setExeFullname(startUser.getFullname());
		opinion.setExeFullname(ContextSupportUtil.getUsername((SysUser) startUser));
		opinion.setExeUserId(startUserId);
		opinion.setTaskName("提交流程");
		opinion.setStartTime(processRun.getCreatetime());
		String content = "提交";
		if (BeanUtils.isNotEmpty(processCmd.getRelRunId()) && StringUtil.isNotEmpty(processCmd.getVoteContent())) {
			content = processCmd.getVoteContent();
		}
		opinion.setOpinion(content);
		opinion.setEndTime(new Date());
		opinion.setDurTime(0L);
		taskOpinionService.add(opinion);
	}

	/**
	 * 处理任务跳转。
	 *
	 * @param processInstanceId
	 * @param processCmd
	 * @throws Exception
	 */
	private void handJumpOverFirstNode(String processInstanceId, ProcessCmd processCmd) throws Exception {
		// 流程启动时跳过第一个节点，清除节点和人员的映射
		// taskUserAssignService.clearNodeUserMap();
		List<Task> taskList = TaskThreadService.getNewTasks();
		TaskThreadService.clearNewTasks();

		TaskEntity taskEntity = (TaskEntity) taskList.get(0);
		String parentNodeId = taskEntity.getTaskDefinitionKey();
		// 填写第一步意见。
		processCmd.getVariables().put(BpmConst.NODE_APPROVAL_STATUS + "_" + parentNodeId, TaskOpinion.STATUS_SUBMIT);
		processCmd.getVariables().put(BpmConst.NODE_APPROVAL_CONTENT + "_" + parentNodeId, "填写表单");
		processCmd.setVoteAgree(TaskOpinion.STATUS_SUBMIT);

		// 设置流程变量。
		setVariables(taskEntity, processCmd);

		skipTask(taskEntity, processCmd);

	}

	/**
	 * 转发办结流程
	 *
	 * @param processRun
	 * @param targetUserIds
	 * @param currUser
	 * @param informType
	 *            通知类型
	 * @throws Exception
	 */
	public int divertProcess(ProcessRun processRun, List<String> targetUserIds, SysUser currUser, String informType, String suggestion) throws Exception {
		String instanceId = processRun.getActInstId();
		String actDefId = processRun.getActDefId();
		BpmDefinition bpmDefinition = bpmDefinitionDao.getByActDefId(actDefId);
		Long defTypeId = bpmDefinition.getTypeId();
		Long currUid = currUser.getUserId();
//		String currName = currUser.getFullname();
		String currName = ContextSupportUtil.getUsername(currUser);
		if (BeanUtils.isEmpty(bpmDefinition))
			return 0;

		if (BeanUtils.isEmpty(targetUserIds))
			return 0;

		List<SysUser> userList = new ArrayList<SysUser>();
		Map<Long, Long> userCopyIdMap = new HashMap<Long, Long>();
		for (String user : targetUserIds) {
			long uid = Long.parseLong(user);
			// 当前用户不能转发给自己
			if (uid == currUid)
				return -1;
			SysUser destUser = (SysUser) sysUserServiceImpl.getById(uid);
			BpmProCopyto bpmProCopyto = new BpmProCopyto();
			bpmProCopyto.setActInstId(Long.parseLong(instanceId));
			bpmProCopyto.setCcTime(new Date());
			bpmProCopyto.setCcUid(uid);
//			bpmProCopyto.setCcUname(destUser.getFullname());
			bpmProCopyto.setCcUname(ContextSupportUtil.getUsername(destUser));
			bpmProCopyto.setCopyId(UniqueIdUtil.genId());
			bpmProCopyto.setCpType(BpmProCopyto.CPTYPE_SEND);
			bpmProCopyto.setIsReaded(0L);
			bpmProCopyto.setRunId(processRun.getRunId());
			bpmProCopyto.setSubject(processRun.getSubject());
			bpmProCopyto.setDefTypeId(defTypeId);
			bpmProCopyto.setCreateId(currUid);
			bpmProCopyto.setCreator(currName);
			bpmProCopytoService.add(bpmProCopyto);

			userList.add(destUser);
			userCopyIdMap.put(destUser.getUserId(), bpmProCopyto.getCopyId());
			//
		}
		Map<String, String> msgTempMap = sysTemplateService.getTempByFun(SysTemplate.USE_TYPE_FORWARD);
		String subject = processRun.getSubject();
		Long runId = processRun.getRunId();
		taskMessageService.sendMessage(currUser, userList, informType, msgTempMap, subject, suggestion, null, runId, userCopyIdMap);
		return 1;
	}

	/**
	 * 处理在线表单数据。
	 *
	 * @param processRun
	 * @param processCmd
	 * @return
	 * @throws Exception
	 */
	public BpmFormData handlerFormData(ProcessCmd processCmd, ProcessRun processRun, String nodeId) throws Exception {
		String json = processCmd.getFormData();

		if (StringUtils.isEmpty(json)) {
			String opinionField = (String) processCmd.getTransientVar(BpmConst.OPINION_FIELD);
			Boolean batComplte = (Boolean) processCmd.getTransientVar("batComplte");
			// TODO 下面是json为空，现在是为了处理批量审批的情况
			if (StringUtil.isNotEmpty(opinionField) && batComplte!=null && batComplte) {
				BpmFormDef bpmFormDef = bpmFormDefService.getById(processRun.getFormDefId());
				BpmFormTable bpmFormTable=bpmFormTableService.getTableById(bpmFormDef.getTableId(),1);
				BpmFormData data = bpmFormHandlerDao.getByKey(bpmFormTable, processRun.getBusinessKey(), false);
				// 把主表数据放到cmd中,主要为了待会抛出的Nodesql事件
				BpmNodeSqlService.handleData(processCmd, data);
				// 将意见数据放到映射的字段中去。
				handFieldOpinion(processCmd, data);
				bpmFormHandlerDao.handFormData(data, processRun, nodeId);
			}
			return null;
		}

		BpmFormData bpmFormData = null;
		BpmFormTable bpmFormTable = null;
		String businessKey = processCmd.getBusinessKey();
		if(StringUtil.isEmpty(businessKey)){
			businessKey = processRun.getBusinessKey();
		}
		// 判断节点Id是否为空，为空表示开始节点。
		boolean isStartFlow = StringUtil.isEmpty(nodeId) ? true : false;

		if (isStartFlow && ProcessRun.STATUS_FORM.equals(processRun.getStatus())) {
			Long formDefId = processRun.getFormDefId();
			BpmFormDef bpmFormDef = bpmFormDefService.getById(formDefId);
			bpmFormTable = bpmFormTableService.getByTableId(bpmFormDef.getTableId(), 1);
		} else {
			if (processRun.getParentId() > 0) {// 作为外部子流程
				Map<String, Object> variables = runtimeService.getVariables(processRun.getActInstId());
				String parentActDefId = (String) variables.get(BpmConst.FLOW_PARENT_ACTDEFID);
				bpmFormTable = bpmFormTableService.getByDefId(processRun.getDefId(), parentActDefId);
			} else {
				bpmFormTable = bpmFormTableService.getByDefId(processRun.getDefId(),"");
			}
		}

		// 有主键的情况,表示数据已经存在。
		if (StringUtil.isNotEmpty(businessKey)) {
			String pkName = bpmFormTable.isExtTable() ? bpmFormTable.getPkField() : TableModel.PK_COLUMN_NAME;
			PkValue pkValue = new PkValue(pkName, businessKey);
			pkValue.setIsAdd(!bpmBusLinkService.checkBusExist(businessKey));
			bpmFormData = FormDataUtil.parseJson(json, pkValue, bpmFormTable);
		} else {
			bpmFormData = FormDataUtil.parseJson(json, bpmFormTable);
		}

		if (bpmFormData != null) {
			Map<String, String> optionsMap = bpmFormData.getOptions();
			// 记录意见
			setOpinionByForm(optionsMap, processCmd);
		}

		// 将意见数据放到映射的字段中去。
		handFieldOpinion(processCmd, bpmFormData);

		processCmd.putVariables(bpmFormData.getVariables());
		// 生成的主键
		PkValue pkValue = bpmFormData.getPkValue();
		businessKey = pkValue.getValue().toString();

		String pk = processRun.getBusinessKey();

		processRun.setBusinessKey(businessKey);
		processCmd.setBusinessKey(businessKey);

		// 把主表数据放到cmd中,主要为了待会抛出的Nodesql事件
		// 设置主表数据
		BpmNodeSqlService.handleData(processCmd, bpmFormData);

		// 启动流程。
		if (isStartFlow) {
			// 保存表单数据,存取表单数据
			bpmFormHandlerDao.handFormData(bpmFormData, processRun);
		} else {
			//跳过时
			bpmFormHandlerDao.handFormData(bpmFormData, processRun, nodeId);
			if(!processCmd.isSkip()){
				this.update(processRun);
			}
			// 业务主键为空的情况，设置流程主键。设置流程变量。
			if (StringUtil.isEmpty(pk)) {
				processCmd.addVariable(BpmConst.FLOW_BUSINESSKEY, businessKey);
			}
		}
		return bpmFormData;
	}

	/**
	 * 将意见字段映射到字段中。
	 *
	 * @param processCmd
	 * @param bpmFormData
	 * @throws Exception
	 */
	private  void handFieldOpinion(ProcessCmd processCmd, BpmFormData bpmFormData) throws Exception {

		String opinionField = (String) processCmd.getTransientVar(BpmConst.OPINION_FIELD);
		Short optionHtml = (Short) processCmd.getTransientVar(BpmConst.OPINION_SUPPORTHTML);
		if (StringUtil.isEmpty(opinionField)) return;

		BpmFormTable bpmFormTable= bpmFormData.getBpmFormTable();

		if (!bpmFormData.isExternal()) {
			opinionField = TableModel.CUSTOMER_COLUMN_PREFIX + opinionField;
		}
		opinionField = opinionField.toLowerCase();


		String opinion = processCmd.getVoteContent();
		int vote = processCmd.getVoteAgree();

		Map<String, Object> mainMap = bpmFormData.getMainFields();

		Map<String,Object> map= bpmFormHandlerDao.getByKey(bpmFormTable, bpmFormData.getPkValue().getValue().toString(),new String[]{ opinionField});
		if(BeanUtils.isNotEmpty(map)){
			for(Entry<String,Object> entry:map.entrySet()){
				String val=(String) entry.getValue();
				if(StringUtil.isNotEmpty(val)){
					mainMap.put(entry.getKey().toLowerCase(), entry.getValue());
				}
			}
		}

		ISysUser sysUser = ContextUtil.getCurrentUser();
		ISkipCondition condition=(ISkipCondition) processCmd.getTransientVar("skipCondition");
		if(condition!=null){
			sysUser=condition.getExecutor();
		}

		//获取组织信息
		SysOrg sysOrg = (SysOrg) ContextUtil.getCurrentOrg();
		if (sysOrg == null&&sysUser!=null) {
			sysOrg=orgService.getByUserId(sysUser.getUserId());
		}

//		String userName = (sysUser == null) ? "系统用户" : sysUser.getFullname();
		String userName = (sysUser == null) ? "系统用户" : ContextSupportUtil.getUsername((SysUser) sysUser);
		TaskOpinion taskOpinion = new TaskOpinion();
		taskOpinion.setExeFullname(userName);
		taskOpinion.setCheckStatus((short) vote);
		taskOpinion.setCreatetime(new Date());
		taskOpinion.setOpinion(opinion);
		taskOpinion.setSysOrg(sysOrg);
		SysUser curUser= (SysUser) sysUser;
		taskOpinion.setWebSignUrl(curUser.getWebSignUrl());
		//自动跳过发起人回填意见默认同意
		/*if(processCmd.isSkip()){
			taskOpinion.setOpinion("同意");
		}*/
		//判断是否为代理，如果为代理则回填意见添加被代理人信息
		TaskEntity taskEntity = getTaskEntByCmd(processCmd);
		if(BeanUtils.isNotEmpty(taskEntity)){
			if (TaskOpinion.STATUS_AGENT.toString().equals(taskEntity.getDescription())){
				ISysUser auth = sysUserServiceImpl.getById(Long.valueOf(taskEntity.getOwner()));
				SysOrg exOrg = orgService.getByUserId(auth.getUserId());
				taskOpinion.setStatus(TaskOpinion.STATUS_AGENT.toString());
//				taskOpinion.setSuperExecution(exOrg.getOrgName()+"-"+auth.getFullname()+"(授权给)->");
				taskOpinion.setSuperExecution(exOrg.getOrgName()+"-"+ContextSupportUtil.getUsername((SysUser) auth)+"(授权给)->");
			}
		}
		boolean supportHtml = optionHtml == 1;
		String val = TaskOpinionService.getOpinion(taskOpinion, supportHtml);
		boolean batComplte = processCmd.getTransientVar("batComplte")!=null?(Boolean)processCmd.getTransientVar("batComplte"):false;//批量审批标记
		if(batComplte){
			val="(批量审批)"+val;
		}
		if(mainMap.containsKey(opinionField)){
			String str = (String) mainMap.get( opinionField);
			if (!StringUtil.isEmpty(str)) {
				val = str + "\n" + val;
			}
		}
		mainMap.put(opinionField, val);

	}

	/**
	 * 获取流程标题。
	 *
	 * @param bpmDefinition
	 * @param processCmd
	 * @return
	 */
	public String getSubject(BpmDefinition bpmDefinition, ProcessCmd processCmd) {
		// 若设置了标题，则直接返回该标题，否则按后台的标题规则返回
		if (StringUtils.isNotEmpty(processCmd.getSubject())) {
			return processCmd.getSubject();
		}

		String rule = bpmDefinition.getTaskNameRule();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", bpmDefinition.getSubject());
		SysUser user = (SysUser) ContextUtil.getCurrentUser();
//		map.put(BpmConst.StartUser, user.getFullname());
		map.put(BpmConst.StartUser, ContextSupportUtil.getUsername(user));
		map.put("startDate", TimeUtil.getCurrentDate());
		map.put("startTime", TimeUtil.getCurrentTime());
		map.put("businessKey", processCmd.getBusinessKey());
		map.putAll(processCmd.getVariables());
		rule = BpmUtil.getTitleByRule(rule, map);
		if (BpmDefinition.STATUS_TEST.equals(bpmDefinition.getStatus())) {
			if (StringUtil.isEmpty(bpmDefinition.getTestStatusTag())) {
				return BpmDefinition.TEST_TAG + "-" + rule;
			} else {
				return bpmDefinition.getTestStatusTag() + "-" + rule;
			}
		}
		return rule;
	}

	/**
	 * 初始化ProcessRun.
	 *
	 * @return
	 */
	public ProcessRun initProcessRun(BpmDefinition bpmDefinition, ProcessCmd cmd,BpmNodeSet bpmNodeSet) throws Exception {

		ProcessRun processRun = new ProcessRun();

		SysUser curUser =  ContextSupportUtil.getCurrentUser();

		if (BeanUtils.isNotEmpty(bpmNodeSet)) {
			if (BpmNodeSet.FORM_TYPE_ONLINE.equals(bpmNodeSet.getFormType())) {
				BpmFormDef bpmFormDef = bpmFormDefService.getDefaultPublishedByFormKey(bpmNodeSet.getFormKey());
				if (bpmFormDef != null) {
					processRun.setFormDefId(bpmFormDef.getFormDefId());
				}
			} else {
				processRun.setBusinessUrl(bpmNodeSet.getFormUrl());
			}
		}

//		processRun.setCreator(curUser.getFullname());
		processRun.setCreator(ContextSupportUtil.getUsername(curUser));
		processRun.setCreatorId(curUser.getUserId());
		processRun.setActDefId(bpmDefinition.getActDefId());
		processRun.setDefId(bpmDefinition.getDefId());
		processRun.setProcessName(bpmDefinition.getSubject());
		processRun.setFlowKey(bpmDefinition.getDefKey());
		// 流程实例归档后是否允许打印表单
		processRun.setIsPrintForm(bpmDefinition.getIsPrintForm());
		processRun.setCreatetime(new Date());
		processRun.setStatus(ProcessRun.STATUS_RUNNING);
		if (BpmDefinition.STATUS_TEST.equals(bpmDefinition.getStatus())) {
			processRun.setIsFormal(ProcessRun.TEST_RUNNING);
		} else {
			processRun.setIsFormal(ProcessRun.FORMAL_RUNNING);
		}
		if (BeanUtils.isNotEmpty(bpmDefinition.getTypeId())) {
			processRun.setTypeId(bpmDefinition.getTypeId());
		}

		// 判断是否从外部传入业务主键。
		BpmResult bpmResult = (BpmResult) cmd.getTransientVar("bpmResult");
		if (bpmResult != null) {
			if (bpmResult.getDataType().equals(DataType.NUMBER)) {
				processRun.setBusinessKeyNum(new Long(bpmResult.getBusinessKey()));
			} else {
				processRun.setBusinessKey(bpmResult.getBusinessKey());
			}
			processRun.setTableName(bpmResult.getTableName());
		}
		Map<String, Object> variables = cmd.getVariables();

		Long startRunId = (Long) variables.get("startRunId");
		// 在启动流程是可以从外部传入RUNID.
		if (startRunId != null && startRunId > 0) {
			processRun.setRunId(startRunId);
		} else {
			processRun.setRunId(UniqueIdUtil.genId());
		}

		processRun.setRelRunId(processRun.getRelRunId());

		// 判断是否需要增加全局流水号
		boolean rtn = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
		if (rtn) {
			if (!identityService.isAliasExisted(SysProperty.GlobalFlowNo)) {
				String message = "请设置别名为" + SysProperty.GlobalFlowNo + "的流水号";
				throw new RuntimeException(message);
			}
			String flowNo = identityService.nextId(SysProperty.GlobalFlowNo);
			processRun.setGlobalFlowNo(flowNo);
		}

		/**
		 * 添加是否从手机端发起流程。
		 */
		if(cmd.isFromMobile()){
			processRun.setStartFromMobile(1);
		}


		return processRun;
	}

	/**
	 * 获取历史实例
	 *
	 * @param queryFilter
	 * @return
	 */
	public List<ProcessRun> getAllHistory(QueryFilter queryFilter) {
		return dao.getAllHistory(queryFilter);
	}

	/**
	 * 查看我参与审批流程列表
	 *
	 * @param filter
	 * @return
	 */
	public List<ProcessRun> getMyAttend(QueryFilter filter) {
		return dao.getMyAttend(filter);
	}

	@Override
	public void delByIds(Long[] ids) {
		if (ids == null || ids.length == 0)
			return;
		for (Long uId : ids) {
			ProcessRun processRun = getById(uId);
			Short procStatus = processRun.getStatus();
			Long instanceId = Long.parseLong(processRun.getActInstId());
			String dsAlias = processRun.getDsAlias();
			String tableName = processRun.getTableName();
			String businessKey = processRun.getBusinessKey();
			String local = "LOCAL";
			// 删除流程流转状态
			bpmProTransToService.delByActInstId(instanceId);
			// 删除BPM_PRO_CPTO抄送转发
			bpmProCopytoDao.delByRunId(uId);
			if (ProcessRun.STATUS_FINISH != procStatus && ProcessRun.STATUS_FORM != procStatus) {

				// executionDao.delVariableByProcInstId(instanceId);
				// taskDao.delCandidateByInstanceId(instanceId);
				// taskDao.delByInstanceId(instanceId);
				// executionDao.delExecutionByProcInstId(instanceId);
				deleteProcessInstance(processRun);
			} else {
				// 流程操作日志
				String memo = "用户删除了流程实例[" + processRun.getProcessName() + "]。";
				if (ProcessRun.STATUS_FORM == procStatus) {
					memo = "用户删除了流程草稿【" + processRun.getProcessName() + "】。";
					bpmRunLogService.addRunLog(processRun.getRunId(), BpmRunLog.OPERATOR_TYPE_DELETEFORM, memo);
				} else {
					bpmRunLogService.addRunLog(processRun.getRunId(), BpmRunLog.OPERATOR_TYPE_DELETEINSTANCE, memo);
				}

				delById(uId);
			}
			if (StringUtil.isNotEmpty(tableName)) {
				if (StringUtil.isEmpty(dsAlias) || DataSourceUtil.DEFAULT_DATASOURCE.equals(dsAlias)|| local.equals(dsAlias)) {
					if (!tableName.startsWith(TableModel.CUSTOMER_TABLE_PREFIX)) {
						tableName = TableModel.CUSTOMER_TABLE_PREFIX + tableName;
					}
					bpmFormHandlerService.delByIdTableName(businessKey, tableName);
				} else {
					try {
						bpmFormHandlerService.delByDsAliasAndTableName(dsAlias, tableName, businessKey);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public List<ProcessRun> getMyProcessRun(Long creatorId, String subject, Short status, PageBean pb) {
		return dao.getMyProcessRun(creatorId, subject, status, pb);
	}

	@Override
	public void add(ProcessRun entity) {
		super.add(entity);
		ProcessRun history = (ProcessRun) entity.clone();
		dao.addHistory(history);
	}

	@Override
	public void update(ProcessRun entity) {
		ProcessRun history = (ProcessRun) entity.clone();
		if (ProcessRun.STATUS_MANUAL_FINISH == entity.getStatus() || ProcessRun.STATUS_FINISH == entity.getStatus()) {
			Date endDate = new Date();
			Date startDate = history.getCreatetime();
			long userId = history.getCreatorId();
			long duration = calendarAssignService.getTaskMillsTime(startDate, endDate, userId);
			history.setEndTime(endDate);
			history.setDuration(duration);
			dao.updateHistory(history);
			dao.delById(entity.getRunId());
		} else {
			dao.updateHistory(history);
			super.update(entity);
		}
	}

	public List<ProcessRun> getByActDefId(String actDefId) {
		return dao.getbyActDefId(actDefId);
	}

	/**
	 * 保存沟通信息。
	 *
	 * <pre>
	 *  1.如果任务这个任务执行人为空的情况，先将当前设置成任务执行人。
	 *  2.产生沟通任务。
	 *  3.添加意见。
	 *  4.保存任务接收人。
	 *  5.产生通知消息。
	 * </pre>
	 *
	 * @param taskEntity
	 *            任务实例
	 * @param opinion
	 *            意见
	 * @param informType
	 *            通知类型
	 * @param userIds
	 *            用户ID
	 * @param subject
	 *            主题信息
	 * @throws Exception
	 */
	public void saveCommuniCation(TaskEntity taskEntity, String opinion, String informType, String userIds, ProcessRun processRun) throws Exception {
		String taskId = taskEntity.getId();
		String[] aryUsers = userIds.split(",");

		SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();

		// 任务执行人为空设定当前人为任务执行人。
		String assignee = taskEntity.getAssignee();
		if (ServiceUtil.isAssigneeEmpty(assignee)) {
			taskDao.updateTaskAssignee(taskId, sysUser.getUserId().toString());
			// 根据任务ID获取意见。
			TaskOpinion oldOpinion = taskOpinionDao.getByTaskId(Long.parseLong(taskId));
			oldOpinion.setExeUserId(sysUser.getUserId());
//			oldOpinion.setExeFullname(sysUser.getFullname());
			oldOpinion.setExeFullname(ContextSupportUtil.getUsername(sysUser));
			taskOpinionDao.update(oldOpinion);
		}

		// 产生沟通任务
		Map<Long, Long> usrIdTaskIds = bpmService.genCommunicationTask(taskEntity, aryUsers, sysUser);

		Long opinionId = UniqueIdUtil.genId();

		TaskOpinion taskOpinion = new TaskOpinion();
		taskOpinion.setOpinionId(opinionId);
		taskOpinion.setOpinion(opinion);
		taskOpinion.setActDefId(taskEntity.getProcessDefinitionId());
		taskOpinion.setActInstId(taskEntity.getProcessInstanceId());
		taskOpinion.setStartTime(new Date());
		taskOpinion.setEndTime(new Date());
		taskOpinion.setExeUserId(sysUser.getUserId());
//		taskOpinion.setExeFullname(sysUser.getFullname());
		taskOpinion.setExeFullname(ContextSupportUtil.getUsername(sysUser));
		taskOpinion.setTaskKey(taskEntity.getTaskDefinitionKey());
		taskOpinion.setTaskName(taskEntity.getName());
		taskOpinion.setCheckStatus(TaskOpinion.STATUS_COMMUNICATION);
		// 处理审批意见的父流程ID
		this.dealTaskOpinSupId(taskEntity, taskOpinion);
		// 增加流程意见
		taskOpinionDao.add(taskOpinion);
		// 保存接收人
		commuReceiverService.saveReceiver(opinionId, usrIdTaskIds, sysUser);
		// 发送通知。
		notifyCommu(processRun, usrIdTaskIds, informType, sysUser, opinion, SysTemplate.USE_TYPE_COMMUNICATION);
	}

	/**
	 * 保存流转信息。
	 *
	 * <pre>
	 *  1.如果这个任务执行人为空的情况，先将当前设置成任务执行人。
	 *  2.保存流转状态
	 *  3.产生流转任务。
	 *  4.添加意见。
	 *  5.保存任务接收人。
	 *  6.产生通知消息。
	 * </pre>
	 *
	 * @param taskEntity
	 *            任务实例
	 * @param opinion
	 *            意见
	 * @param informType
	 *            通知类型
	 * @param userIds
	 *            用户ID
	 * @param transType
	 *            流转类型
	 * @param action
	 *            执行后动作
	 * @param subject
	 *            主题信息
	 * @throws Exception
	 */
	public void saveTransTo(TaskEntity taskEntity, String opinion, String informType, String userIds, String transType, String action, ProcessRun processRun) throws Exception {
		String taskId = taskEntity.getId();
		String actInstId = taskEntity.getProcessInstanceId();
		String[] aryUsers = userIds.split(",");

		SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();

		// 任务执行人为空设定当前人为任务执行人。
		String assignee = taskEntity.getAssignee();
		if (ServiceUtil.isAssigneeEmpty(assignee)) {
			taskDao.updateTaskAssignee(taskId, sysUser.getUserId().toString());
			// 根据任务ID获取意见。
			TaskOpinion oldOpinion = taskOpinionDao.getByTaskId(Long.parseLong(taskId));
			oldOpinion.setExeUserId(sysUser.getUserId());
//			oldOpinion.setExeFullname(sysUser.getFullname());
			oldOpinion.setExeFullname(ContextSupportUtil.getUsername(sysUser));
			taskOpinionDao.update(oldOpinion);
		}
		// 修改初始任务状态
		taskDao.updateTaskDescription(TaskOpinion.STATUS_TRANSTO_ING.toString(), taskId);
		// 删除沟通任务
		taskDao.delCommuTaskByParentTaskId(taskId);
		// 保存流转状态
		Long id = UniqueIdUtil.genId();
		BpmProTransTo bpmProTransTo = new BpmProTransTo();
		bpmProTransTo.setId(id);
		bpmProTransTo.setTaskId(Long.valueOf(taskId));
		bpmProTransTo.setTransType(Integer.valueOf(transType));
		bpmProTransTo.setAction(Integer.valueOf(action));
		bpmProTransTo.setCreatetime(new Date());
		bpmProTransTo.setActInstId(Long.valueOf(actInstId));
		bpmProTransTo.setTransResult(1);
		bpmProTransTo.setAssignee(userIds);
		if (ServiceUtil.isAssigneeEmpty(assignee)) {
			bpmProTransTo.setCreateUserId(sysUser.getUserId());
		} else {
			bpmProTransTo.setCreateUserId(Long.valueOf(assignee));
		}
		bpmProTransToService.add(bpmProTransTo);

		// 处理多级加签
		handleCascadeTransToTask(taskEntity);

		// 产生流转任务
		Map<Long, Long> usrIdTaskIds = bpmService.genTransToTask(taskEntity, aryUsers);

		Long opinionId = UniqueIdUtil.genId();
		TaskOpinion taskOpinion = new TaskOpinion();
		taskOpinion.setOpinionId(opinionId);
		taskOpinion.setOpinion(opinion);
		taskOpinion.setActDefId(taskEntity.getProcessDefinitionId());
		taskOpinion.setActInstId(actInstId);
		taskOpinion.setStartTime(new Date());
		taskOpinion.setEndTime(new Date());
		taskOpinion.setExeUserId(sysUser.getUserId());
//		taskOpinion.setExeFullname(sysUser.getFullname());
		taskOpinion.setExeFullname(ContextSupportUtil.getUsername(sysUser));
		taskOpinion.setTaskKey(taskEntity.getTaskDefinitionKey());
		taskOpinion.setTaskName(taskEntity.getName());
		taskOpinion.setCheckStatus(TaskOpinion.STATUS_TRANSTO);

		// 处理审批意见的父流程ID
		this.dealTaskOpinSupId(taskEntity, taskOpinion);

		// 增加流程意见
		taskOpinionDao.add(taskOpinion);
		// 保存接收人
		commuReceiverService.saveReceiver(opinionId, usrIdTaskIds, sysUser);
		// 发送通知。
		notifyCommu(processRun, usrIdTaskIds, informType, sysUser, opinion, SysTemplate.USE_TYPE_TRANSTO);
	}

	/**
	 * 添加已办历史。
	 *
	 * <pre>
	 * 	添加沟通或流转反馈任务到已办。
	 * </pre>
	 *
	 * @param taskEnt
	 */
	public void addActivityHistory(TaskEntity taskEnt) {
		HistoricActivityInstanceEntity ent = new HistoricActivityInstanceEntity();
		SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
		ent.setId(String.valueOf(UniqueIdUtil.genId()));
		ent.setActivityId(taskEnt.getTaskDefinitionKey());

		ent.setActivityName(taskEnt.getName());
		ent.setProcessInstanceId(taskEnt.getProcessInstanceId());
		ent.setAssignee(sysUser.getUserId().toString());
		ent.setProcessDefinitionId(taskEnt.getProcessDefinitionId());
		ent.setStartTime(taskEnt.getCreateTime());
		ent.setEndTime(new Date());
		ent.setDurationInMillis(System.currentTimeMillis() - taskEnt.getCreateTime().getTime());
		ent.setExecutionId("0");
		ent.setActivityType("userTask");
		historyActivityDao.add(ent);
	}

	/**
	 * 添加已办历史。
	 *
	 * <pre>
	 * 	添加沟通或流转反馈任务到已办。
	 * </pre>
	 *
	 * @param processTask
	 */
	public void addActivityHistory(ProcessTask processTask) {
		HistoricActivityInstanceEntity ent = new HistoricActivityInstanceEntity();
		SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
		ent.setId(String.valueOf(UniqueIdUtil.genId()));
		ent.setActivityId(processTask.getTaskDefinitionKey());

		ent.setActivityName(processTask.getName());
		ent.setProcessInstanceId(processTask.getProcessInstanceId());
		ent.setAssignee(sysUser.getUserId().toString());
		ent.setProcessDefinitionId(processTask.getProcessDefinitionId());
		ent.setStartTime(processTask.getCreateTime());
		ent.setEndTime(new Date());
		ent.setDurationInMillis(System.currentTimeMillis() - processTask.getCreateTime().getTime());
		ent.setExecutionId("0");
		ent.setActivityType("userTask");
		historyActivityDao.add(ent);
	}

	/**
	 * 添加干预数据。
	 *
	 * <pre>
	 * 	添加干预数据到到审批历史。
	 * </pre>
	 *
	 * @param processCmd
	 * @param taskEnt
	 */
	private void addInterVene(ProcessCmd processCmd, DelegateTask taskEnt) {
		if (processCmd.getIsManage() == 0)
			return;
		String assignee = taskEnt.getAssignee();
		String tmp = "";

		SysUser curUser = (SysUser) ContextUtil.getCurrentUser();
		Long userId = SystemConst.SYSTEMUSERID;
		String userName = SystemConst.SYSTEMUSERNAME;
		if (curUser != null) {
			userId = curUser.getUserId();
//			userName = curUser.getFullname();
			userName = ContextSupportUtil.getUsername(curUser);
		}

		if (ServiceUtil.isAssigneeNotEmpty(assignee)) {
			ISysUser sysUser = sysUserServiceImpl.getById(new Long(assignee));
			if (sysUser != null) {
//				tmp = "原执行人:" + ServiceUtil.getUserLink(sysUser.getUserId().toString(), sysUser.getFullname());
				tmp = "原执行人:" + ServiceUtil.getUserLink(sysUser.getUserId().toString(), ContextSupportUtil.getUsername((SysUser) sysUser));
			}
		} else {
			tmp = "原候选执行人:";
			Set<SysUser> userList = taskUserService.getCandidateUsers(taskEnt.getId());
			for (SysUser user : userList) {
				if (user != null) {
//					tmp += ServiceUtil.getUserLink(user.getUserId().toString(), user.getFullname()) + ",";
					tmp += ServiceUtil.getUserLink(user.getUserId().toString(), ContextSupportUtil.getUsername(user)) + ",";
				}
			}
		}
		Date endDate = new Date();
		TaskOpinion taskOpinion = new TaskOpinion();
		taskOpinion.setOpinionId(UniqueIdUtil.genId());
		taskOpinion.setActDefId(taskEnt.getProcessDefinitionId());
		taskOpinion.setActInstId(taskEnt.getProcessInstanceId());
		taskOpinion.setTaskKey(taskEnt.getTaskDefinitionKey());
		taskOpinion.setTaskName(taskEnt.getName());
		taskOpinion.setExeUserId(userId);
		taskOpinion.setExeFullname(userName);
		taskOpinion.setCheckStatus(TaskOpinion.STATUS_INTERVENE);
		taskOpinion.setStartTime(taskEnt.getCreateTime());
		taskOpinion.setTaskId(new Long(taskEnt.getId()));
		taskOpinion.setCreatetime(taskEnt.getCreateTime());
		taskOpinion.setEndTime(endDate);
		taskOpinion.setOpinion(tmp);
		Long duration = calendarAssignService.getRealWorkTime(taskEnt.getCreateTime(), endDate, userId);
		taskOpinion.setDurTime(duration);
		taskOpinionService.add(taskOpinion);

	}

	/**
	 * 根据流程运行Id,删除流程运行实体。<br/>
	 * 些方法不会级联删除相关信息。
	 *
	 * @see GenericService#delById(java.io.Serializable)
	 */
	@Override
	public void delById(Long id) {
		dao.delById(id);
		dao.delActHiProcessInstanceByRunId(id);
		dao.delByIdHistory(id);
	}

	/**
	 * 发送短信或者邮件通知。
	 *
	 * @param subject
	 *            标题
	 * @param usrIdTaskIds
	 *            目标任务用户与ID对应关系列表
	 * @param informTypes
	 *            通知类型。
	 * @param sysUser
	 *            发送用户
	 * @param opinion
	 *            意见(或原因)
	 * @param sysTemplate
	 *            消息模板类型
	 * @throws Exception
	 */
	public void notifyCommu(ProcessRun processRun, Map<Long, Long> usrIdTaskIds, String informTypes, SysUser sysUser, String opinion, Integer sysTemplate) throws Exception {
		Map<String, String> msgTempMap = sysTemplateService.getTempByFun(sysTemplate);
		// 如果目标节点是空，获取目标节点
		if (usrIdTaskIds == null || usrIdTaskIds.size() == 0)
			return;

		Iterator<Entry<Long, Long>> iter = usrIdTaskIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Long, Long> entry = (Entry<Long, Long>) iter.next();
			Long userId = entry.getKey();
			if (userId.equals(sysUser.getUserId()))
				continue;
			Long taskId = entry.getValue();
			SysUser receiverUser = (SysUser) sysUserServiceImpl.getById(userId);
			List<SysUser> receiverUserList = new ArrayList<SysUser>();
			receiverUserList.add(receiverUser);
			taskMessageService.sendMessage(sysUser, receiverUserList, informTypes, msgTempMap, processRun.getSubject(), opinion, taskId, processRun.getRunId(), null);

		}
	}

	/**
	 * 根据Act流程定义ID，获取流程实例
	 *
	 * @param actDefId
	 * @param pb
	 * @return
	 */
	public List<ProcessRun> getByActDefId(String actDefId, PageBean pb) {
		return dao.getByActDefId(actDefId, pb);
	}

	/**
	 * 按流程实例ID获取ProcessRun实体
	 *
	 * @param processInstanceId
	 * @return
	 */
	public ProcessRun getByActInstanceId(Long processInstanceId) {
		return dao.getByActInstanceId(processInstanceId);
	}

	/**
	 * 级联删除流程实例扩展
	 *
	 * @param processRun
	 */
	private void deleteProcessRunCasade(ProcessRun processRun) {
		List<ProcessInstance> childrenProcessInstance = runtimeService.createProcessInstanceQuery().superProcessInstanceId(processRun.getActInstId()).list();
		for (ProcessInstance instance : childrenProcessInstance) {
			ProcessRun pr = getByActInstanceId(new Long(instance.getProcessInstanceId()));
			if (pr != null) {
				deleteProcessRunCasade(pr);
			}
		}
		long procInstId = Long.parseLong(processRun.getActInstId());
		Short procStatus = processRun.getStatus();
		if (ProcessRun.STATUS_FINISH != procStatus) {
			executionDao.delVariableByProcInstId(procInstId);
			taskDao.delCandidateByInstanceId(procInstId);
			taskDao.delByInstanceId(procInstId);
			executionDao.delExecutionByProcInstId(procInstId);
		}
		String actDefId = processRun.getActDefId();
		BpmDefinition bpmDefinition = bpmDefinitionDao.getByActDefId(actDefId);
		if (!BpmDefinition.STATUS_TEST.equals(bpmDefinition.getStatus())) {
			String memo = "用户删除了流程实例[" + processRun.getProcessName() + "]。";
			bpmRunLogService.addRunLog(processRun, BpmRunLog.OPERATOR_TYPE_DELETEINSTANCE, memo);
		}
		delById(processRun.getRunId());
	}

	/**
	 * 获取流程扩展的根流程扩展（最顶层的父流程扩展）
	 *
	 * @param processRun
	 * @return
	 */
	private ProcessRun getRootProcessRun(ProcessRun processRun) {
		ProcessInstance parentProcessInstance = runtimeService.createProcessInstanceQuery().subProcessInstanceId(processRun.getActInstId()).singleResult();
		if (parentProcessInstance != null) {
			// Get parent ProcessRun
			ProcessRun parentProcessRun = getByActInstanceId(new Long(parentProcessInstance.getProcessInstanceId()));
			// Get Parent ProcessInstance sub ProcessInstance
			return getRootProcessRun(parentProcessRun);
		}
		return processRun;
	}

	/**
	 * 删除流程运行实体（ProcessRun），级联删除Act流程实例以及父流程
	 *
	 * @param processRun
	 */
	private void deleteProcessInstance(ProcessRun processRun) {
		ProcessRun rootProcessRun = getRootProcessRun(processRun);
		deleteProcessRunCasade(rootProcessRun);
		// runtimeService.deleteProcessInstance(rootProcessRun.getActInstId(),"Manual Delete");
	}

	public void delByActDefId(String actDefId) {
		List<ProcessRun> list = dao.getbyActDefId(actDefId);
		List<BpmTaskExe> bpmTaskExeList;
		for (ProcessRun processRun : list) {
			if (ProcessRun.STATUS_FORM.equals(processRun.getStatus()))
				continue;
			// 删除BPM_PRO_CPTO抄送转发,BPM_TASK_EXE代理转办数据
			bpmProCopytoDao.delByRunId(processRun.getRunId());
			// 代理转办数据BPM_TASK_EXE删除 BPM_COMMU_RECEIVER:通知接收人，BPM_TASK_READ：任务是否已读
			bpmTaskExeList = bpmTaskExeDao.getByRunId(processRun.getRunId());
			for (BpmTaskExe bpmTaskExe : bpmTaskExeList) {
				commuReceiverDao.delByTaskId(bpmTaskExe.getTaskId());
				taskReadDao.delByActInstId(bpmTaskExe.getActInstId());
			}
			bpmTaskExeDao.delByRunId(processRun.getRunId());
			deleteProcessInstance(processRun);
		}
		dao.delActHiProcessInstanceByActDefId(actDefId);
		dao.delHistroryByActDefId(actDefId);
	}

	public List<ProcessRun> getMyDraft(QueryFilter queryFilter) {
		return dao.getMyDraft(queryFilter);
	}

	/**
	 * 保存草稿
	 *
	 * @param processCmd
	 * @param formKey
	 * @param formUrl
	 * @throws Exception
	 */
	public void saveForm(ProcessCmd processCmd) throws Exception {
		String actDefId = processCmd.getActDefId();
		BpmDefinition bpmDefinition = bpmDefinitionDao.getByActDefId(actDefId);
		// 开始节点
		String nodeId = "";
		if (!NodeCache.isMultipleFirstNode(actDefId)) {
			nodeId = NodeCache.getStartNode(actDefId).getNodeId();
		}
		BpmNodeSet bpmNodeSet =bpmNodeSetService.getStartBpmNodeSet(actDefId, false);
		ProcessRun processRun = initProcessRun(bpmDefinition, processCmd,bpmNodeSet);
		String businessKey = "";
		// 保存草稿后，处理业务表单，外部调用不触发表单处理
		if (!processCmd.isInvokeExternal()) {
			BpmFormData bpmFormData = handlerFormData(processCmd, processRun, "");
			if (bpmFormData != null) {
				businessKey = processCmd.getBusinessKey();
				processRun.setTableName(bpmFormData.getTableName());
				if (bpmFormData.getPkValue() != null) {
					processRun.setPkName(bpmFormData.getPkValue().getName());
					processRun.setDsAlias(bpmFormData.getDsAlias());
				}
				BpmFormDef defaultForm = bpmFormDefService.getDefaultPublishedByFormKey(bpmNodeSet.getFormKey());
				processRun.setFormDefId(defaultForm.getFormDefId());
			}
		}
		// 调用前置处理器
		if (!processCmd.isSkipPreHandler()) {
			invokeHandler(processCmd, bpmNodeSet, true);
		}
		if (StringUtil.isEmpty(businessKey)) {
			businessKey = processCmd.getBusinessKey();
		}
		String subject = getSubject(bpmDefinition, processCmd);
		processRun.setBusinessKey(businessKey);
		processRun.setSubject(subject);
		processRun.setStatus(ProcessRun.STATUS_FORM);
		processRun.setCreatetime(new Date());
		this.add(processRun);
		// 调用前置处理器
		if (!processCmd.isSkipPreHandler()) {
			invokeHandler(processCmd, bpmNodeSet, true);
		}

		// 抛出事件
		EventUtil.publishNodeSqlEvent(actDefId, nodeId, BpmNodeSql.ACTION_SAVE, (Map) processCmd.getTransientVar("mainData"));

		// 添加到流程运行日志
		String memo = "保存草稿:" + subject;
		bpmRunLogService.addRunLog(processRun.getRunId(), BpmRunLog.OPERATOR_TYPE_SAVEFORM, memo);
	}

	/**
	 * 我的请求
	 *
	 * @return
	 */
	public List<ProcessRun> getMyRequestList(QueryFilter filter) {
		return dao.getMyRequestList(filter);
	}

	/**
	 * 我的申请根据流程进行统计数量
	 *
	 * @return
	 */
	public List<ProcessRunAmount> getFlowMyRequestCount(QueryFilter filter) {
		return (List<ProcessRunAmount>) dao.getFlowMyRequestCount(filter);
	}

	/**
	 * 我的办结
	 *
	 * @return
	 */
	public List<ProcessRun> getMyCompletedList(QueryFilter filter) {
		return dao.getMyCompletedList(filter);
	}

	/**
	 * 我的办结根据流程进行统计数量
	 *
	 * @return
	 */
	public List<ProcessRunAmount> getFlowMyCompletedCount(QueryFilter filter) {
		return (List<ProcessRunAmount>) dao.getFlowMyCompletedCount(filter);
	}

	/**
	 * 我的请求办结
	 *
	 * @return
	 */
	public List<ProcessRun> getMyRequestCompletedList(QueryFilter filter) {
		return dao.getMyRequestCompletedList(filter);
	}

	/**
	 * 已办办结事宜
	 *
	 * @return
	 */
	public List<ProcessRun> getAlreadyCompletedMattersList(QueryFilter filter) {
		return dao.getAlreadyCompletedMattersList(filter);
	}

	/**
	 * 查看我承办的事务(未结束和已经结束)
	 * @param filter
	 * @return
	 */
	public List<ProcessRun> getAlreadyMattersListAndCompletedMattersList(QueryFilter filter) {
		return dao.getAlreadyMattersListAndCompletedMattersList(filter);
	}

	/**
	 * 获取所有相关流程
	 * @param filter
	 * @return
	 */
	public List<ProcessRun> getAllFlowsList(QueryFilter filter) {
		return dao.getAllFlowsList(filter);
	}

	/**
	 * 已办事宜
	 *
	 * @return
	 */
	public List<ProcessRun> getAlreadyMattersList(QueryFilter filter) {
		return dao.getAlreadyMattersList(filter);
	}

	/**
	 * 已办事宜根据流程进行统计数量
	 *
	 * @return
	 */
	public List<ProcessRunAmount> getFlowAlreadyMattersCount(QueryFilter filter) {
		return (List<ProcessRunAmount>) dao.getFlowAlreadyMattersCount(filter);
	}

	/**
	 * 流程实例根据流程进行统计数量
	 *
	 * @return
	 */
	public List<ProcessRunAmount> getProcessInsCount(QueryFilter filter) {
		return (List<ProcessRunAmount>) dao.getProcessInsCount(filter);
	}

	/**
	 * 办结事宜
	 *
	 * @return
	 */
	public List<ProcessRun> getCompletedMattersList(QueryFilter filter) {
		return dao.getCompletedMattersList(filter);
	}

	/**
	 * 查询我发起的和我参与流程
	 *
	 * @param sqlKey
	 * @param queryFilter
	 * @return
	 */
	public List<ProcessRun> selectPro(QueryFilter queryFilter) {
		return dao.getBySqlKey("selectPro", queryFilter);
	}

	/**
	 * 批量审批。
	 *
	 * @param taskIds
	 *            任务id使用逗号进行分割。
	 * @param opinion
	 *            意见。
	 * @throws Exception
	 */
	public void nextProcessBat(String taskIds, String opinion) throws Exception {
		String[] aryTaskId = taskIds.split(",");
		for (String taskId : aryTaskId) {
			TaskEntity taskEntity = bpmService.getTask(taskId);
			ProcessRun processRun = this.getByActInstanceId(new Long(taskEntity.getProcessInstanceId()));
			String subject = processRun.getSubject();
			if (taskEntity.getExecutionId() == null && TaskOpinion.STATUS_COMMUNICATION.toString().equals(taskEntity.getDescription())) {
				MessageUtil.addMsg("<span class='red'>" + subject + ",为沟通任务!</span><br/>");
				continue;
			}
			if (taskEntity.getExecutionId() == null && TaskOpinion.STATUS_TRANSTO.toString().equals(taskEntity.getDescription())) {
				MessageUtil.addMsg("<span class='red'>" + subject + ",为流转任务!</span><br/>");
				continue;
			}
			// 是否允许批量审批
			// if (bpmDefinition.getAllowBatchApprove() == 0) {
			// MessageUtil.addMsg("<span class='red'>" + subject
			// + ",流程定义不能批量审批!</span><br/>");
			// continue;
			// }

			ProcessCmd processCmd = new ProcessCmd();
			processCmd.setVoteAgree(TaskOpinion.STATUS_AGREE);
			processCmd.setVoteContent(opinion);
			processCmd.setTaskId(taskId);
			processCmd.addTransientVar("batComplte", true);//添加批处理的标记
			nextProcess(processCmd);

			MessageUtil.addMsg("<span class='green'>" + subject + ",审批成功!</span><br>");
		}
	}

	/**
	 * 根据runid找到copyid
	 *
	 * @return
	 */
	public ProcessRun getCopyIdByRunid(Long runId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("runId", runId);
		map.put("userId",ContextUtil.getCurrentUserId());
		return (ProcessRun) dao.getOne("getCopyIdByRunid", map);
	}

	/**
	 * 保存表单数据。
	 *
	 * @param json
	 *            json数据
	 * @param formKey
	 * @param userId
	 * @param taskId
	 * @param defId
	 * @param bizKey
	 * @param requestSystemId
	 * @param businessDocumentKey
	 * @param opinion
	 * @return
	 * @throws Exception
	 */
	public void saveData(ProcessCmd processCmd) throws Exception {
		String taskId = processCmd.getTaskId();
		ProcessRun processRun = null;
		BpmNodeSet bpmNodeSet = null;
		if (StringUtil.isEmpty(taskId)) {
			processRun = (ProcessRun) processCmd.getProcessRun();
			bpmNodeSet = bpmNodeSetService.getStartBpmNodeSet(processRun.getActDefId(), false);
		} else {
			TaskEntity task = bpmService.getTask(taskId);
			String actInstId = task.getProcessInstanceId();
			String actDefId = task.getProcessDefinitionId();
			String nodeId = task.getTaskDefinitionKey();
			processRun = getByActInstanceId(Long.parseLong(actInstId));
			String opinion = processCmd.getVoteContent();
			saveOpinion(task, opinion);
			String parentActDefId = (String) taskService.getVariableLocal(taskId, BpmConst.FLOW_PARENT_ACTDEFID);

			bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId, parentActDefId);

			// 在流程审批时，更新流程变量
			setVariables(task, processCmd);
		}
		// 处理业务表单，外部调用不触发表单处理
		if (!processCmd.isInvokeExternal()) {
			handlerFormData(processCmd, processRun, bpmNodeSet.getNodeId());
		}
		// 调用前置处理器
		if (!processCmd.isSkipPreHandler()) {
			invokeHandler(processCmd, bpmNodeSet, true);
		}
	}

	/**
	 * 保存任务处理意见。
	 *
	 * @param taskEntity
	 * @param opinion
	 */
	public void saveOpinion(TaskEntity taskEntity, String opinion) {
		SysUser curUser = (SysUser) ContextUtil.getCurrentUser();
		TaskOpinion taskOpinion = taskOpinionDao.getOpinionByTaskId(new Long(taskEntity.getId()), curUser.getUserId());
		if (taskOpinion == null) {
			Long taskId = Long.parseLong(taskEntity.getId());
			Long opinionId = UniqueIdUtil.genId();
			taskOpinion = new TaskOpinion();
			taskOpinion.setOpinionId(opinionId);
			taskOpinion.setActDefId(taskEntity.getProcessDefinitionId());
			taskOpinion.setActInstId(taskEntity.getProcessInstanceId());
			taskOpinion.setTaskId(taskId);
			taskOpinion.setTaskKey(taskEntity.getTaskDefinitionKey());
			taskOpinion.setTaskName(taskEntity.getName());
			taskOpinion.setStartTime(new Date());
			taskOpinion.setCheckStatus(TaskOpinion.STATUS_OPINION);
			taskOpinion.setOpinion(opinion);
			taskOpinion.setExeUserId(curUser.getUserId());
//			taskOpinion.setExeFullname(curUser.getFullname());
			taskOpinion.setExeFullname(ContextSupportUtil.getUsername(curUser));

			// 处理审批意见的父流程ID
			this.dealTaskOpinSupId(taskEntity, taskOpinion);

			taskOpinionDao.add(taskOpinion);
		} else {
			taskOpinion.setExeUserId(curUser.getUserId());
//			taskOpinion.setExeFullname(curUser.getFullname());
			taskOpinion.setExeFullname(ContextSupportUtil.getUsername(curUser));
			taskOpinion.setOpinion(opinion);
			taskOpinionDao.update(taskOpinion);
		}

	}

	/**
	 * 根据流程任务ID，取得流程扩展实例
	 *
	 * @param taskId
	 *            流程任务ID
	 * @return
	 */
	public ProcessRun getByTaskId(String taskId) {
		ProcessTaskHistory taskHistory = taskHistoryDao.getById(Long.valueOf(taskId));
		if (taskHistory == null) {
			return null;
		}
		String actInstId = taskHistory.getProcessInstanceId();
		if (StringUtils.isEmpty(actInstId))
			return null;
		return getByActInstanceId(Long.valueOf(actInstId));
	}

	public Long getProcessDuration(ProcessRun entity) {
		Long duration = 0L;
		String actInstId = entity.getActInstId();
		Map<String, Map<Long, List<TaskOpinion>>> signTask = new HashMap<String, Map<Long, List<TaskOpinion>>>();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("actInstId", entity.getActInstId());

		List<TaskOpinion> taskOpinions = taskOpinionService.getByActInstId(actInstId);

		for (TaskOpinion opn : taskOpinions) {
			boolean isSignTask = bpmService.isSignTask(entity.getActDefId(), opn.getTaskKey());
			if (!isSignTask) {// 不是会签
				if (BeanUtils.isNotEmpty(opn.getDurTime())) {
					duration += opn.getDurTime();
				}

			} else {// 是会签，按批次，取最大值
				Map<Long, List<TaskOpinion>> taskMap = signTask.get(opn.getTaskKey());
				if (taskMap == null) {
					taskMap = new HashMap<Long, List<TaskOpinion>>();
					signTask.put(opn.getTaskKey(), taskMap);
				}

				if (opn.getTaskId() == null) {
					continue;
				}
				TaskSignData signData = taskSignDataService.getByTaskId(opn.getTaskId().toString());
				if (signData == null) {
					continue;
				}
				List<TaskOpinion> taskList = taskMap.get(signData.getGroupNo());
				if (taskList == null) {
					taskList = new ArrayList<TaskOpinion>();
					taskMap.put(signData.getGroupNo(), taskList);
				}
				taskList.add(opn);
			}
		}

		// 添加会签的节点时长
		for (Entry<String, Map<Long, List<TaskOpinion>>> entry : signTask.entrySet()) {
			Map<Long, List<TaskOpinion>> map = entry.getValue();
			for (Entry<Long, List<TaskOpinion>> ent : map.entrySet()) {
				Long maxDuration = 0L;
				List<TaskOpinion> list = ent.getValue();
				for (TaskOpinion o : list) {
					long durtime = o.getDurTime() == null ? 0 : o.getDurTime();
					if (maxDuration.longValue() > durtime) {
						maxDuration = durtime;
					}
				}
				duration += maxDuration;
			}
		}

		return duration;
	}

	public Long getProcessLastSubmitDuration(ProcessRun entity) {
		Long duration = 0L;
		String actInstId = entity.getActInstId();

		Map<String, Map<Long, List<TaskOpinion>>> signTask = new HashMap<String, Map<Long, List<TaskOpinion>>>();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("actInstId", entity.getActInstId());

		List<TaskOpinion> taskOpinions = taskOpinionService.getByActInstId(actInstId);
		List<TaskOpinion> lastTaskOpinions = new ArrayList<TaskOpinion>();
		// 以结束时间排序
		Collections.sort(taskOpinions, new Comparator<TaskOpinion>() {
			@Override
			public int compare(TaskOpinion o1, TaskOpinion o2) {
				Date startTime1 = o1.getStartTime();
				Date endTime1 = o1.getEndTime();
				Date startTime2 = o2.getStartTime();
				Date endTime2 = o2.getEndTime();

				if (endTime1 != null && endTime2 != null) {
					return endTime1.compareTo(endTime2);
				} else if (endTime1 != null && endTime2 == null) {
					return 1;
				} else if (endTime1 == null && endTime2 != null) {
					return -1;
				} else {
					return startTime1.compareTo(startTime2);
				}
			}
		});

		boolean hasResubmit = false;

		for (int i = taskOpinions.size() - 1; i >= 0; i--) {
			if (TaskOpinion.STATUS_RESUBMIT.equals(taskOpinions.get(i).getCheckStatus())) {
				hasResubmit = true;
				break;
			} else {
				lastTaskOpinions.add(taskOpinions.get(i));
			}
		}

		if (!hasResubmit) {
			if (entity.getDuration() != null) {
				return entity.getDuration();
			} else {
				return getProcessDuration(entity);
			}
		}

		for (TaskOpinion opn : lastTaskOpinions) {
			boolean isSignTask = bpmService.isSignTask(entity.getActDefId(), opn.getTaskKey());
			if (!isSignTask) {// 不是会签
				if (opn.getDurTime() != null) {
					duration += opn.getDurTime();
				}
			} else {// 是会签，按批次，取最大值
				Map<Long, List<TaskOpinion>> taskMap = signTask.get(opn.getTaskKey());
				if (taskMap == null) {
					taskMap = new HashMap<Long, List<TaskOpinion>>();
					signTask.put(opn.getTaskKey(), taskMap);
				}
				TaskSignData signData = taskSignDataService.getByTaskId(opn.getTaskId().toString());
				List<TaskOpinion> taskList = taskMap.get(signData.getGroupNo());
				if (taskList == null) {
					taskList = new ArrayList<TaskOpinion>();
					taskMap.put(signData.getGroupNo(), taskList);
				}
				taskList.add(opn);
			}
		}

		// 添加会签的节点时长
		for (Entry<String, Map<Long, List<TaskOpinion>>> entry : signTask.entrySet()) {
			Map<Long, List<TaskOpinion>> map = entry.getValue();
			for (Entry<Long, List<TaskOpinion>> ent : map.entrySet()) {
				Long maxDuration = 0L;
				List<TaskOpinion> list = ent.getValue();
				for (TaskOpinion o : list) {
					long durtime = o.getDurTime() == null ? 0 : o.getDurTime();
					if (maxDuration.longValue() > durtime) {
						maxDuration = durtime;
					}
				}
				duration += maxDuration;
			}
		}

		return duration;
	}

	/**
	 * 获取某人的流程实例列表。
	 *
	 * @param defId
	 *            流程定义ID
	 * @param creatorId
	 *            创建人
	 * @param instanceAmount
	 *            流程数量
	 * @return
	 */
	public List<ProcessRun> getRefList(Long defId, Long creatorId, Integer instanceAmount, int type) {
		List<ProcessRun> list = null;
		// 我提交的流程
		if (type == 0) {
			list = dao.getRefList(defId, creatorId, instanceAmount);
		}
		// 我审批的流程数据
		else {
			list = dao.getRefListApprove(defId, creatorId, instanceAmount);
		}

		return list;
	}

	/**
	 * 获取监控的流程实例列表。
	 *
	 * @param filter
	 * @return
	 */
	public List<ProcessRun> getMonitor(QueryFilter filter) {
		return dao.getMonitor(filter);
	}

	/**
	 * 判断是否允许撤销
	 *
	 * @param runId
	 * @param backToStart
	 * @return
	 * @throws Exception
	 */
	public ResultMessage checkRecover(Long runId) throws Exception {
		ProcessRun processRun = this.getById(runId);
		if (processRun.getParentId() != null && processRun.getParentId() != 0) {
			processRun = this.getById(processRun.getParentId());
		}

		ResultMessage messag = new ResultMessage(ResultMessage.Success, "可以撤销!");
		if (!isCanRecover(processRun,  messag)) {
			return messag;
		}
		return messag;
	}

	/**
	 * 判断是否允许追回到发起人，这个允许多实例撤销
	 *
	 * @param runId
	 * @param backToStart
	 * @return
	 * @throws Exception
	 */
	public ResultMessage checkRecoverByStart(Long runId) throws Exception {
		ProcessRun curProcessRun = this.getById(runId);
		ProcessRun processRun = this.getRootProcessRun(curProcessRun);
		BpmDefinition bpmDefinition = bpmDefinitionDao.getByActDefId(processRun.getActDefId());
		// 判断当前人是否为发起人。
		// 如果当前人为发起人那么可以撤回所有的流程。
		boolean isCreator = processRun.getCreatorId().longValue() == ContextUtil.getCurrentUserId().longValue();
		if (!isCreator) {
			return new ResultMessage(ResultMessage.Fail, "对不起,非流程发起人不能追回到开始节点!");
		}
		Short status = processRun.getStatus();
		if (status.shortValue() == ProcessRun.STATUS_FINISH) {
			return new ResultMessage(ResultMessage.Fail, "对不起,此流程实例已经结束!");
		}

		if (status.shortValue() == ProcessRun.STATUS_MANUAL_FINISH) {
			return new ResultMessage(ResultMessage.Fail, "对不起,此流程实例已经被删除!");
		}
		if (BpmDefinition.STATUS_INST_DISABLED.equals(bpmDefinition.getStatus())) {
			return new ResultMessage(ResultMessage.Fail, "对不起,此流程定义已禁用实例!");
		}

		String nodeId = "";
		// 判断初始节点后是否有多条路径
		if (NodeCache.isMultipleFirstNode(processRun.getActDefId())) {
			nodeId = processRun.getStartNode();
		} else {
			FlowNode flowNode = NodeCache.getFirstNodeId(processRun.getActDefId());
			if (flowNode == null) {
				return new ResultMessage(ResultMessage.Fail, "找不到流程起始节点!");
			}
			nodeId = flowNode.getNodeId();
		}
		List<ProcessTask> taskList = bpmService.getTasks(processRun.getActInstId());

		List<String> taskNodeIdList = getNodeIdByTaskList(taskList);
		if (taskNodeIdList.contains(nodeId)) {
			return new ResultMessage(ResultMessage.Fail, "当前流程已撤回!");
		}

		return new ResultMessage(ResultMessage.Success, "可以撤销!");
	}

	/**
	 * 将任务追回到发起人。
	 * @param runId
	 * @param informType
	 * @param memo
	 * @return
	 * @throws Exception
	 */
	public ResultMessage redo(Long runId, String informType, String memo) throws Exception {

		// TODO 删除原实例
		ProcessRun processRun = this.getById(runId);
		String actInstId = processRun.getActInstId();
		ProcessRun rootProcessRun = getRootProcessRun(processRun);
		String subject = rootProcessRun.getSubject();
		String rootActDefId=rootProcessRun.getActDefId();

		// 获取开始节点。
		FlowNode flowNode = null;
		// 判断初始节点后是否有多条路径
		if (NodeCache.isMultipleFirstNode(rootActDefId)) {
			flowNode = NodeCache.getNodeByActNodeId(rootActDefId, rootProcessRun.getStartNode());
		} else {
			flowNode = NodeCache.getFirstNodeId(rootActDefId);
		}

		Set<String> instSet = taskOpinionService.getInstListByInstId(actInstId);// 主要为了找到子流程的实例ID
		List<ProcessTask> tasks = new ArrayList<ProcessTask>();// bpmService.getTasks(actInstId);
		for (String id : instSet) {
			tasks.addAll(bpmService.getTasks(id));
		}

		List<Task> taskList = new ArrayList<Task>();
		for (ProcessTask task : tasks) {
			taskList.add(bpmService.getTask(task.getId()));
			// 更新审批意见状态。
			updOpinionByTaskId(new Long(task.getId()),memo);
		}
		// 任务追回 通知任务所属人
		Map<String, String> map = this.getTempByUseType(SysTemplate.USE_TYPE_REVOKED);

		taskMessageService.notify(taskList, informType, subject, map, memo, "");

		// 删除关联的流程变量
		delRelateVars(actInstId);
		// 手动删除分发汇总流程的关联的流程变量
		executionDao.delVarByInstIdAndVarName(Long.parseLong(actInstId), "_token_");
		// 删除分发汇总流程的令牌
		taskForkDao.delByActInstId(actInstId);

		ProcessInstance parentProcessInstance = runtimeService.createProcessInstanceQuery().processInstanceId(rootProcessRun.getActInstId()).singleResult();
		if (parentProcessInstance != null) {
			addTaskInRedo(parentProcessInstance, flowNode);
			//屏蔽追回流程时会向审批历史添加一个审批记录
			//addRedoOpinion(rootProcessRun, memo);
			ProcessRun historyProcessRun = dao.getByIdHistory(rootProcessRun.getRunId());
			rootProcessRun.setStatus(ProcessRun.STATUS_REDO);
			historyProcessRun.setStatus(ProcessRun.STATUS_REDO);
			dao.update(rootProcessRun);
			dao.updateHistory(historyProcessRun);
		}
		return new ResultMessage(ResultMessage.Success, "追回任务成功");
	}

	/**
	 * 将任务追回到发起人。
	 * @param runId
	 * @param informType
	 * @param memo
	 * @return
	 * @throws Exception
	 */
	public ResultMessage redos(Long runId, String informType, String memo) throws Exception {

		// TODO 删除原实例
		ProcessRun processRun = this.getById(runId);
		String actInstId = processRun.getActInstId();
		ProcessRun rootProcessRun = getRootProcessRun(processRun);
		String subject = rootProcessRun.getSubject();
		String rootActDefId=rootProcessRun.getActDefId();

		// 获取开始节点。
		FlowNode flowNode = null;
		// 判断初始节点后是否有多条路径
		if (NodeCache.isMultipleFirstNode(rootActDefId)) {
			flowNode = NodeCache.getNodeByActNodeId(rootActDefId, rootProcessRun.getStartNode());
		} else {
			flowNode = NodeCache.getFirstNodeId(rootActDefId);
		}

		Set<String> instSet = taskOpinionService.getInstListByInstId(actInstId);// 主要为了找到子流程的实例ID
		List<ProcessTask> tasks = new ArrayList<ProcessTask>();// bpmService.getTasks(actInstId);
		for (String id : instSet) {
			tasks.addAll(bpmService.getTasks(id));
		}

		List<Task> taskList = new ArrayList<Task>();
		for (ProcessTask task : tasks) {
			taskList.add(bpmService.getTask(task.getId()));
			// 更新审批意见状态。
			//updOpinionByTaskId(new Long(task.getId()),memo);
		}
		// 任务追回 通知任务所属人
		Map<String, String> map = this.getTempByUseType(SysTemplate.USE_TYPE_REVOKED);

		taskMessageService.notify(taskList, informType, subject, map, memo, "");

		// 删除关联的流程变量
		delRelateVars(actInstId);
		// 手动删除分发汇总流程的关联的流程变量
		executionDao.delVarByInstIdAndVarName(Long.parseLong(actInstId), "_token_");
		// 删除分发汇总流程的令牌
		taskForkDao.delByActInstId(actInstId);

		ProcessInstance parentProcessInstance = runtimeService.createProcessInstanceQuery().processInstanceId(rootProcessRun.getActInstId()).singleResult();
		if (parentProcessInstance != null) {
			addTaskInRedo(parentProcessInstance, flowNode);
			//屏蔽追回流程时会向审批历史添加一个审批记录
			//addRedoOpinion(rootProcessRun, memo);
			ProcessRun historyProcessRun = dao.getByIdHistory(rootProcessRun.getRunId());
			rootProcessRun.setStatus(ProcessRun.STATUS_REDO);
			historyProcessRun.setStatus(ProcessRun.STATUS_REDO);
			dao.update(rootProcessRun);
			dao.updateHistory(historyProcessRun);
		}
		return new ResultMessage(ResultMessage.Success, "追回任务成功");
	}

	private void updOpinionByTaskId(Long taskId,String memo) {
		SysUser curUser = (SysUser) ContextUtil.getCurrentUser();
		TaskOpinion opinion = taskOpinionDao.getByTaskId(taskId);
		opinion.setCheckStatus(TaskOpinion.STATUS_RETRIEVE);
		opinion.setEndTime(new Date());
		opinion.setExeUserId(curUser.getUserId());
//		opinion.setExeFullname(curUser.getFullname());
		opinion.setExeFullname(ContextSupportUtil.getUsername(curUser));
		opinion.setDurTime(new Date().getTime() - opinion.getStartTime().getTime());
		opinion.setOpinion(memo);
		taskOpinionDao.update(opinion);
	}

	private void delRelateVars(String actInstId) {
		Map<String, Object> varsMap = runtimeService.getVariables(actInstId);
		Iterator<String> iterator = varsMap.keySet().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();
			if (!containVars(name))	continue;

			executionDao.delVarByInstIdAndVarName(Long.parseLong(actInstId), name);
		}
	}

	private boolean containVars(String varName) {
		String[] aryNames = new String[] { "nrOfInstances", "nrOfCompletedInstances", "nrOfActiveInstances", "loopCounter", "_signUsers" };
		for (String tmp : aryNames) {
			if (varName.contains(tmp)) {
				return true;
			}
		}
		return false;
	}

	// 删除子实例及相关信息
	private void delSubInfoByProcInstId(String procInstId) {
		// 获取子流程列表
		List<ProcessExecution> processExecutionList = executionDao.getSubExecutionByProcInstId(procInstId);
		for (int i = 0; i < processExecutionList.size(); i++) {
			ProcessExecution p = processExecutionList.get(i);
			String subProcInstId = p.getId();
			Long subProcInstIdL = Long.parseLong(subProcInstId);
			// 如果不是活动，可能包含多实例的子流程
			if (p.getIsActive() == 0) {
				delSubInfoByProcInstId(subProcInstId);
			} else {
				List<ProcessExecution> subProcessExecutionList = executionDao.getSubExecutionBySuperId(subProcInstId);
				if (subProcessExecutionList != null && subProcessExecutionList.size() == 1) {
					ProcessExecution subProcessExecution = subProcessExecutionList.get(0);

					String subActinstid = subProcessExecution.getProcessInstanceId();
					delSubInfoByProcInstId(subActinstid);
					// 删除子流程根实例
					bpmProStatusDao.delByProcInstId(Long.parseLong(subActinstid));
					dao.delActHiProcessInstanceByActinstid(Long.parseLong(subActinstid));
					dao.delHistoryByActinstid(subActinstid);
					dao.delProByActinstid(subActinstid);
					taskDao.delByInstanceId(Long.parseLong(subActinstid));
					executionDao.delVariableByProcInstId(Long.parseLong(subActinstid));
					executionDao.delExecutionById(subActinstid);
				}
			}
			dao.delActHiProcessInstanceByActinstid(Long.parseLong(subProcInstId));
			dao.delHistoryByActinstid(subProcInstId);
			dao.delProByActinstid(subProcInstId);
			executionDao.delVariableByProcInstId(subProcInstIdL);
			// 删除task表数据
			taskDao.delByInstanceId(Long.parseLong(subProcInstId));
			executionDao.delExecutionByProcInstId(subProcInstIdL);

		}
		// 删除审批相关信息
		bpmProStatusDao.delByProcInstId(Long.parseLong(procInstId));
		// 根据实例Id删除子流程历史
		dao.delSubHistoryByProcInstId(procInstId);
		// 根据流程实例Id删除非主线程的流程
		dao.delSubProByProcInstId(procInstId);
		// 根据实例Id删除act子流程历史
		dao.delSubActHiProcessInstanceByActinstid(procInstId);
		taskDao.delCandidateByInstanceId(Long.parseLong(procInstId));
		// 根据流程实例删除任务。
		taskDao.delByInstanceId(Long.parseLong(procInstId));
		executionDao.delSubByProcInstId(procInstId);
		executionDao.delSubExecutionByProcInstId(Long.parseLong(procInstId));
	}

	/**
	 * 描述: 在追回中重新分配任务
	 *
	 * @param parentProcessInstance
	 *            流程实例
	 * @param flowNode
	 *            开始节点
	 * @author wzh
	 */
	private void addTaskInRedo(ProcessInstance parentProcessInstance, FlowNode flowNode) {
		String procInstId = parentProcessInstance.getProcessInstanceId();
		// 删除非主线程相关信息
		delSubInfoByProcInstId(procInstId);

		String newTaskId =String.valueOf(UniqueIdUtil.genId());
		Date curr = new Date();
		String currentId = ContextUtil.getCurrentUserId().toString();
		String defId = parentProcessInstance.getProcessDefinitionId();
		// 创建新的任务给发起人
		ProcessTask newTask = new ProcessTask();
		newTask.setId(newTaskId);
		newTask.setProcessInstanceId(procInstId);
		newTask.setProcessDefinitionId(defId);
		newTask.setExecutionId(procInstId);
		newTask.setName(flowNode.getNodeName());
		newTask.setTaskDefinitionKey(flowNode.getNodeId());
		newTask.setPriority(TaskEntity.DEFAULT_PRIORITY);
		newTask.setCreateTime(curr);
		newTask.setAssignee(currentId);
		newTask.setOwner(currentId);
		newTask.setDescription("-1");
		// 创建新的历史
		ProcessTaskHistory newTaskHistory = new ProcessTaskHistory();
		newTaskHistory.setId(newTaskId);
		newTaskHistory.setProcessDefinitionId(defId);
		newTaskHistory.setExecutionId(procInstId);
		newTaskHistory.setName(flowNode.getNodeName());
		newTaskHistory.setTaskDefinitionKey(flowNode.getNodeId());
		newTaskHistory.setStartTime(curr);
		newTaskHistory.setAssignee(currentId);
		newTaskHistory.setOwner(currentId);
		newTaskHistory.setDescription("-1");
		// BeanUtils.copyProperties(newTaskHistory, newTask);
		newTaskHistory.setStartTime(curr);

		BpmProStatus bpmProStatus = new BpmProStatus();
		bpmProStatus.setId(Long.parseLong(newTaskId));
		bpmProStatus.setActinstid(Long.parseLong(procInstId));
		bpmProStatus.setCreatetime(curr);
		bpmProStatus.setNodeid(flowNode.getNodeId());
		bpmProStatus.setNodename(flowNode.getNodeName());
		bpmProStatus.setActdefid(defId);
		bpmProStatus.setStatus(TaskOpinion.STATUS_RESUBMIT);// STATUS_RECOVER_TOSTART

		taskDao.insertTask(newTask);
		taskHistoryDao.add(newTaskHistory);
		executionDao.updateMainThread(procInstId, flowNode.getNodeId());
		bpmProStatusDao.add(bpmProStatus);

	}

	/**
	 * 描述: 添加追回原因
	 *
	 * @param processRun
	 * @param memo
	 * @author wzh
	 */
	private void addRedoOpinion(ProcessRun processRun, String memo) {
		TaskOpinion opinion = new TaskOpinion();
		Long startUserId = processRun.getCreatorId();
		ISysUser startUser = sysUserServiceImpl.getById(startUserId);
		opinion.setOpinionId(UniqueIdUtil.genId());
		opinion.setCheckStatus(TaskOpinion.STATUS_RETRIEVE);
		opinion.setActInstId(processRun.getActInstId());
//		opinion.setExeFullname(startUser.getFullname());
		opinion.setExeFullname(ContextSupportUtil.getUsername((SysUser) startUser));
		opinion.setExeUserId(startUserId);
		opinion.setOpinion(memo);
		opinion.setStartTime(processRun.getCreatetime());
		opinion.setTaskName("追回流程");
		opinion.setEndTime(new Date());
		opinion.setDurTime(0L);
		taskOpinionService.add(opinion);
	}

	/**
	 * 根据任务列表和堆栈返回历史任务。
	 * @param stackList
	 * @param taskList
	 * @return
	 */
	private List<ProcessTask> getLastTaskId(Long actInstId,ResultMessage resultMsg){
		List<ProcessTask> list=new ArrayList<ProcessTask>();
		Long curUserId=ContextUtil.getCurrentUserId();
		ProcessTaskHistory taskEntity = taskHistoryDao.getLastFinshTaskByProcId(actInstId,curUserId);
		// 根据流程实例Id获取堆栈列表。
		List<ExecutionStack> stackList=executionStackService.getByActInstId(actInstId);
		//根据流程实例获取任务列表。
		List<ProcessTask> taskList = bpmService.getTasks(actInstId.toString());
		//根据actInstId和curUserId  获取最新完成的审批记录
		TaskOpinion taskOpinion = taskOpinionService.getLatestUserOpinion(actInstId.toString(), curUserId);
		boolean checkStatus = false;
		if(taskOpinion!=null){
			//如果最新的记录中的任务状态不是同意或者重新提交的，那就不能进行撤销
			Short status = taskOpinion.getCheckStatus();
			if (!TaskOpinion.STATUS_AGREE.equals(status) && !TaskOpinion.STATUS_RESUBMIT.equals(status)){
				checkStatus= true;
			}
		}
		Map<Long,ExecutionStack> map=new HashMap<Long, ExecutionStack>();
		for(ExecutionStack stack:stackList){
			map.put(stack.getStackId(), stack);
		}
		if(!checkStatus){
			for(ProcessTask task:taskList){
				String taskId=task.getId();
				for(ExecutionStack stack:stackList){
					if(!contain(stack.getTaskIds(),taskId)) continue;

					ExecutionStack parentStack=map.get(stack.getParentId());
					if(parentStack==null )  continue;

					//最近审批任务和堆栈的节点不一致。
					if(!parentStack.getNodeId().equals(taskEntity.getTaskDefinitionKey())) continue;
					// 最近审批任务和堆栈一致。
					if(contain(parentStack.getAssignees(), curUserId.toString())  ){
						String pToken=parentStack.getTaskToken();
						String token=stack.getTaskToken();
						if((pToken!=null && token!=null  && pToken.equals(token)) || (pToken==token)){
							list.add(task);
						}
					}
				}
			}
		}
		if(list.size()==0){
			resultMsg.setResult(ResultMessage.Fail);
			resultMsg.setMessage("对不起,下一步任务已经被审批，无法撤销!");
		}
		return list;
	}

	/**
	 * 是否会签任务并且能进行撤销
	 * @param actInstId
	 * @param resultMsg
	 * @return
	 */
	private Boolean isSignTaskRecover(Long actInstId){
		Long curUserId=ContextUtil.getCurrentUserId();
		ProcessTaskHistory taskEntity = taskHistoryDao.getLastFinshTaskByProcId(actInstId,curUserId);
		String processDefinitionId = taskEntity.getProcessDefinitionId();
		String taskDefinitionKey = taskEntity.getTaskDefinitionKey();
		FlowNode flowNode = NodeCache.getByActDefId(processDefinitionId).get(taskDefinitionKey);
		boolean isSignNode = flowNode.getIsSignNode();
		return isSignNode;
	}

	//会签环节是否已经完成
	private Boolean isSignTaskComplete(Long actInstId){
		Long curUserId=ContextUtil.getCurrentUserId();
		ProcessTaskHistory taskEntity = taskHistoryDao.getLastFinshTaskByProcId(actInstId,curUserId);
		ExecutionEntity execution = bpmService.getExecution(taskEntity.getExecutionId());
		if(BeanUtils.isEmpty(execution)){
			return true;
		}
		String parentId = execution.getParentId();
		Integer nrOfCompletedInstances = (Integer)runtimeService.getVariable(parentId, "nrOfCompletedInstances");
		return nrOfCompletedInstances == null;
	}

	/**
	 * 逗号分割
	 * @param container
	 * @param contained
	 * @return
	 */
	private boolean contain(String container,String contained){
		String[] aryTmp=container.split(",") ;
		for(String tmp:aryTmp){
			if(tmp.equals(contained)){
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查流程实例是否可以撤销。
	 * <pre>
	 * 1.流程定义是否禁用实例。
	 * 2.判断流程实例是否已经结束。
	 * 3.根据流程实例ID获取任务列表，判断列表是否存在，不存在判断为外部流程。
	 * 4.判断当前用户是否参与审批了该流程实例。
	 * </pre>
	 * @param processRun
	 * @param message
	 * @return
	 */
	private Boolean isCanRecover(ProcessRun processRun,  ResultMessage message) {
		BpmDefinition bpmDefinition = bpmDefinitionDao.getByActDefId(processRun.getActDefId());
		if (BpmDefinition.STATUS_INST_DISABLED.equals(bpmDefinition.getStatus())) {
			message.setMessage("对不起,此流程定义已禁用实例!");
			message.setResult(ResultMessage.Fail);
			return false;
		}
		Short status = processRun.getStatus();
		if (status.shortValue() == ProcessRun.STATUS_MANUAL_FINISH) {
			message.setMessage("对不起,此流程实例已经人工终止!");
			message.setResult(ResultMessage.Fail);
			return false;
		}
		if (status.shortValue() == ProcessRun.STATUS_FINISH) {
			message.setMessage("对不起,此流程实例已经结束!");
			message.setResult(ResultMessage.Fail);
			return false;
		}
		String actInstIdStr = processRun.getActInstId();
		Long actInstId = Long.parseLong(actInstIdStr);
		List<ProcessTask> taskList = bpmService.getTasks(actInstIdStr);
		if(BeanUtils.isEmpty(taskList)){
			// 下一节点为子流程
			message.setMessage("下一节点为外部流程不能撤销!");
			message.setResult(ResultMessage.Fail);
			return false;
		}

		Long userId=ContextUtil.getCurrentUserId();
		//获取最近的审批任务。
		ProcessTaskHistory taskEntity = taskHistoryDao.getLastFinshTaskByProcId(actInstId, userId);
		if(taskEntity==null){
			message.setMessage("对不起,你没有参与审批此流程实例!");
			message.setResult(ResultMessage.Fail);
			return false;
		}
		Boolean signTaskRecover = isSignTaskRecover(actInstId);
		//会签任务允许撤销
		if(signTaskRecover){
			return true;
		}
		List<ProcessTask> rtnList = getLastTaskId(actInstId, message);
		if(message.getResult()==ResultMessage.Fail){
			return false;
		}
		boolean hasRead = getTaskHasRead(rtnList);
		if (hasRead) {
			message.setMessage("对不起,该任务已经被审阅!");
			message.setResult(ResultMessage.Fail);
			return false;
		}
		// 判断是否允许返回。
		boolean allowBack = getTaskAllowBack(rtnList);
		if (!allowBack) {
			message.setMessage("对不起,当前流程实例不允许撤销!");
			message.setResult(ResultMessage.Fail);
			return false;
		}
		return true;
	}

	/**
	 * 根据流程实例对流程进行撤销
	 *
	 * @param runId
	 * @param informType
	 * @param memo
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ResultMessage recover(Long runId, String informType, String memo) throws Exception {
		ProcessRun processRun = this.getById(runId);
		ResultMessage message = new ResultMessage(ResultMessage.Success, "撤销任务成功");
		if (!isCanRecover(processRun,   message)) {
			return message;
		}
		Long actInstId = new Long( processRun.getActInstId());
		List<ProcessTask> taskList = getLastTaskId(actInstId, message);
		Boolean signTaskRecover = isSignTaskRecover(actInstId);
		//会签任务执行撤销动作 而且 整个会签环节未完成时 通过其他方式撤销
		if(signTaskRecover && !isSignTaskComplete(actInstId)){
			message = recoverSignTask(runId, informType, memo);
		}
		else{
			for (int i = 0; i < taskList.size(); i++) {
				ProcessTask processTask = taskList.get(i);
				// 加上回退状态，使其通过任务完成事件中记录其是撤销的状态
				ProcessCmd processCmd = new ProcessCmd();
				processCmd.setTaskId(processTask.getId());
				// 设置为撤销
				processCmd.setRecover(true);
				processCmd.setBack(BpmConst.TASK_BACK);
				processCmd.setVoteAgree(TaskOpinion.STATUS_RECOVER);
				processCmd.setVoteContent(memo);
				processCmd.setInformType(informType);
				// 第一个任务跳转回来，其他直接完成任务。
				if (i > 0) {
					processCmd.setOnlyCompleteTask(true);
				}
				// 进行回退处理
				this.nextProcess(processCmd);
				// 判断是否为别人转办的任务 如果是从交办记录表中标记为取消记录
				bpmTaskExeService.cancel(new Long(processTask.getId()));
			}
		}
		return message;
	}

	//转换历史任务为任务实体
	private TaskEntity convertHis2Entity(ProcessTaskHistory processTaskHistory){
		TaskEntity taskEntity = new TaskEntity();
		taskEntity.setId(processTaskHistory.getId());
		taskEntity.setProcessDefinitionId(processTaskHistory.getProcessDefinitionId());
		taskEntity.setProcessInstanceId(processTaskHistory.getProcessInstanceId());
		taskEntity.setExecutionId(processTaskHistory.getExecutionId());
		taskEntity.setName(processTaskHistory.getName());
		taskEntity.setParentTaskId(processTaskHistory.getParentTaskId());
		taskEntity.setDescription(processTaskHistory.getDescription());
		taskEntity.setOwner(processTaskHistory.getOwner());
		taskEntity.setAssignee(processTaskHistory.getAssignee());
		taskEntity.setCreateTime(new Date());
		taskEntity.setTaskDefinitionKey(processTaskHistory.getTaskDefinitionKey());
		taskEntity.setPriority(processTaskHistory.getPriority());
		return taskEntity;
	}

	//撤销会签任务
	private ResultMessage recoverSignTask(Long runId, String informType, String memo) throws Exception{
		ProcessRun processRun = this.getById(runId);
		ResultMessage messag = new ResultMessage(ResultMessage.Success, "撤销任务成功");
		Long currentUserId = ContextUtil.getCurrentUserId();
		if(BeanUtils.isEmpty(currentUserId)){
			messag.setResult(ResultMessage.Fail);
			messag.setMessage("未获取到当前用户，无法撤销");
			return messag;
		}
		if (!isCanRecover(processRun, messag)) {
			return messag;
		}
		String actInstIdStr = processRun.getActInstId();
		Long actInstId = Long.parseLong(actInstIdStr);
		ProcessTaskHistory processTaskHistory = taskHistoryDao.getLastFinshTaskByProcId(actInstId, currentUserId);
		TaskEntity taskEntity = convertHis2Entity(processTaskHistory);
		String taskId = taskEntity.getId();
		ProcessTask reviveTask = new ProcessTask();
		BeanUtils.copyNotNullProperties(reviveTask, taskEntity);
		//恢复会签任务
		taskDao.insertTask(reviveTask);

		processTaskHistory.setEndTime(null);
		processTaskHistory.setDurationInMillis(null);
		processTaskHistory.setDeleteReason(null);
		//更新任务历史
		taskHistoryDao.update(processTaskHistory);

		ExecutionEntity execution = bpmService.getExecution(taskEntity.getExecutionId());
		execution.setActive(true);
		execution.setRevision(1);
		//更新execution
		ProcessExecution processExecution = new ProcessExecution(execution);
		processExecution.setId(execution.getId());
		String parentExecutionId = execution.getParentId();
		processExecution.setParentId(parentExecutionId);
		executionDao.update(processExecution);
		//更新已完成会签任务数量
		Integer nrOfCompletedInstances = (Integer)runtimeService.getVariable(parentExecutionId, "nrOfCompletedInstances");
		if(nrOfCompletedInstances > 0){
			runtimeService.setVariable(parentExecutionId, "nrOfCompletedInstances", nrOfCompletedInstances - 1);
		}
		//更新活动的会签任务数量
		Integer nrOfActiveInstances = (Integer)runtimeService.getVariable(parentExecutionId, "nrOfActiveInstances");
		if(nrOfActiveInstances != null){
			runtimeService.setVariable(parentExecutionId, "nrOfActiveInstances", nrOfActiveInstances + 1);
		}

		TaskSignData taskSignData = taskSignDataService.getByTaskId(taskId);
		taskSignData.setVoteTime(null);
		taskSignData.setIsAgree(null);
		taskSignData.setContent(null);
		//更新会签数据
		taskSignDataService.update(taskSignData);
		if (StringUtil.isNotEmpty(memo)) {
			//添加审批意见
			saveSignRecoverOpinion(taskEntity, memo);
		}
		String runLogMessage = "用户在任务["+taskEntity.getName()+"]执行了撤销操作。";
		//记录流程操作日志
		bpmRunLogService.addRunLog(processRun.getRunId(), BpmRunLog.OPERATOR_TYPE_RETRIEVE, runLogMessage);
		return messag;
	}

	//会签任务的撤销时添加审批意见
	private void saveSignRecoverOpinion(TaskEntity taskEntity, String opinion){
		ISysUser currentUser = ContextUtil.getCurrentUser();
		TaskOpinion taskOpinion = new TaskOpinion();
		taskOpinion.setOpinionId(UniqueIdUtil.genId());
		taskOpinion.setOpinion(opinion);
		taskOpinion.setActDefId(taskEntity.getProcessDefinitionId());
		taskOpinion.setActInstId(taskEntity.getProcessInstanceId());
		taskOpinion.setStartTime(new Date());
		taskOpinion.setEndTime(new Date());
		taskOpinion.setExeUserId(currentUser.getUserId());
//		taskOpinion.setExeFullname(currentUser.getFullname());
		taskOpinion.setExeFullname(ContextSupportUtil.getUsername((SysUser) currentUser));
		taskOpinion.setTaskKey(taskEntity.getTaskDefinitionKey());
		taskOpinion.setTaskName(taskEntity.getName());
		taskOpinion.setCheckStatus(TaskOpinion.STATUS_REVOKED);
		// 增加流程意见
		taskOpinionDao.add(taskOpinion);
	}

	/**
	 * 获取已完成会签任务的
	 * @param taskId
	 * @param hangTaskEntity
	 * @return
	 */
	private ExecutionEntity getExecution(String taskId, TaskEntity hangTaskEntity){
		if(StringUtils.isEmpty(taskId)) return null;
		TaskOpinion taskOpinion = taskOpinionDao.getByTaskId(Long.parseLong(taskId));
		if(BeanUtils.isEmpty(taskOpinion)) return null;
		if(TaskOpinion.STATUS_INTERVENE.equals(taskOpinion.getCheckStatus())){
			throw new RuntimeException("干预的流程任务无法撤销");
		}
		Long exeUserId = taskOpinion.getExeUserId();

		return null;
	}

	private List<String> getNodeIdByTaskList(List<ProcessTask> taskList) {
		List<String> list = new ArrayList<String>();
		for (ProcessTask task : taskList) {
			list.add(task.getTaskDefinitionKey());
		}
		return list;
	}


	private Map<String, String> getTempByUseType(Integer useType) throws Exception {
		SysTemplate temp = sysTemplateService.getDefaultByUseType(useType);
		if (temp == null)
			throw new Exception("模板中未找到内部消息的默认模板或系统模板");

		Map<String, String> map = new HashMap<String, String>();
		map.put(SysTemplate.TEMPLATE_TITLE, temp.getTitle());
		map.put(SysTemplate.TEMPLATE_TYPE_HTML, temp.getHtmlContent());
		map.put(SysTemplate.TEMPLATE_TYPE_PLAIN, temp.getPlainContent());

		return map;
	}

	/**
	 * 根据任务列表是否已读。
	 *
	 * @param list
	 * @return
	 */
	private boolean getTaskHasRead(List<ProcessTask> list) {
		boolean rtn = false;
		for (ProcessTask task : list) {
			List<TaskRead> readList = taskReadDao.getTaskRead(new Long(task.getProcessInstanceId()), new Long(task.getId()),task.getAssignee());
			if (BeanUtils.isNotEmpty(readList)) {
				rtn = true;
				break;
			}
		}
		return rtn;
	}

	/**
	 * 取得任务列表是否允许退回。
	 *
	 * @param list
	 * @return
	 */
	private boolean getTaskAllowBack(List<ProcessTask> list) {
		for (ProcessTask task : list) {
			boolean allBack = bpmActService.isTaskAllowBack(task.getId());
			if (allBack) {
				return true;
			}
		}
		return false;

	}

	public ProcessRun getByBusinessKey(String businessKey) {
		if (StringUtil.isNotEmpty(businessKey)) {
			ProcessRun processRun = dao.getByBusinessKey(businessKey);
			return processRun;
		} else {
			return null;
		}

	}

	public void copyDraft(Long runId) throws Exception {
		ProcessRun processRun = getById(runId);
		Long newRunId = UniqueIdUtil.genId();
		processRun.setRunId(newRunId);
		String flowNo = identityService.nextId(SysProperty.GlobalFlowNo);
		processRun.setGlobalFlowNo(flowNo);
		String businessKey = processRun.getBusinessKey();
		String newBusKey = Long.toString(UniqueIdUtil.genId());
		BpmFormTable formTable  = bpmFormTableService.getByAliasTableName(processRun.getDsAlias(), processRun.getTableName());
		BpmFormTable bpmFormTable=bpmFormTableService.getByTableId(formTable.getTableId(), 1);
		if (BeanUtils.isNotEmpty(bpmFormTable)) {
			// 查找源数据(未经处理的源数据 便于后面的插入数据)
			BpmFormData bpmFormData = bpmFormHandlerDao.getByKey(bpmFormTable, businessKey, false);
			// 设置新的主键
			PkValue pkValue = new PkValue();
			pkValue.setIsAdd(true);
			pkValue.setValue(newBusKey);
			pkValue.setName(bpmFormData.getPkValue().getName());
			bpmFormData.setPkValue(pkValue);

			Map<String, Object> mainData = bpmFormData.getMainFields();
			mainData.put(pkValue.getName(), newBusKey);
			bpmFormData.setMainFields(mainData);
			// 子表数据绑定 主表新主键
			List<SubTable> subTableList = bpmFormData.getSubTableList();
			List<SubTable> newSubTables = new ArrayList<SubTable>();
			for (SubTable subTable : subTableList) {
				List<Map<String, Object>> subDatas = subTable.getDataList();
				List<Map<String, Object>> newSubDatas = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> subData : subDatas) {
					subData.put(subTable.getFkName(), newBusKey);
					subData.put(subTable.getPkName(), Long.toString(UniqueIdUtil.genId()));
					newSubDatas.add(subData);
				}
				subTable.setDataList(newSubDatas);
				newSubTables.add(subTable);
			}
			bpmFormData.setSubTableList(newSubTables);
			bpmFormHandlerDao.handFormData(bpmFormData);
			processRun.setBusinessKey(newBusKey);
			processRun.setCreatetime(new Date());
			this.add(processRun);
		}
	}

	/**
	 * 流程任务 管理员更改执行人
	 *
	 * @param taskId
	 * @param userId
	 * @param voteContent
	 * @param informType
	 * @throws Exception
	 */
	public ResultMessage updateTaskAssignee(String taskId, String userId, String voteContent, String informType) throws Exception {
		TaskEntity taskEntity = bpmService.getTask(taskId);
		String originUserId = taskEntity.getAssignee();
		if (BeanUtils.isEmpty(taskEntity)) {
			return new ResultMessage(ResultMessage.Fail, "该任务已经不存在");
		}
		if (userId.equals(originUserId)) {
			return new ResultMessage(ResultMessage.Fail, "更改的执行人与原执行人相同无需更改！");
		}
		String actDefId = taskEntity.getProcessDefinitionId();
		BpmDefinition bpmDefinition = bpmDefinitionDao.getByActDefId(actDefId);
		if (BpmDefinition.STATUS_INST_DISABLED.equals(bpmDefinition.getStatus())) {
			return new ResultMessage(ResultMessage.Fail, "相关流程定义已经禁用流程实例，不能进行更改执行人操作");
		}
		bpmService.updateTaskAssignee(taskId, userId);
		// 记录日志。
		ProcessRun processRun = this.getByActInstanceId(new Long(taskEntity.getProcessInstanceId()));
		Long runId = processRun.getRunId();

		String originUserName = "";
		if (ServiceUtil.isAssigneeNotEmpty(originUserId)) {

			ISysUser originUser = sysUserServiceImpl.getById(Long.parseLong(originUserId));
//			originUserName = originUser.getFullname();
			originUserName = ContextSupportUtil.getUsername((SysUser) originUser);
		}
		SysUser user =(SysUser) sysUserServiceImpl.getById(Long.parseLong(userId));
		SysUser curUser = (SysUser) ContextUtil.getCurrentUser();

//		String memo = "流程:" + processRun.getSubject() + ", 【" + curUser.getFullname() + "】将任务【" + taskEntity.getName() + "】";
		String memo = "流程:" + processRun.getSubject() + ", 【" + ContextSupportUtil.getUsername(curUser) + "】将任务【" + taskEntity.getName() + "】";
		if (StringUtil.isNotEmpty(originUserName)) {
			memo += ",原执行人为:【" + originUserName + "】,";
		}
//		memo += "现指派给【" + user.getFullname() + "】执行。";
		memo += "现指派给【" + ContextSupportUtil.getUsername(user) + "】执行。";
		bpmRunLogService.addRunLog(runId, BpmRunLog.OPERATOR_TYPE_ASSIGN, memo);

//		memo = "流程:" + processRun.getSubject() + ", 【" + curUser.getFullname() + "】将任务【" + taskEntity.getName() + "】 ";

		memo = "流程:" + processRun.getSubject() + ", 【" + ContextSupportUtil.getUsername(curUser) + "】将任务【" + taskEntity.getName() + "】 ";
		if (StringUtil.isNotEmpty(originUserName)) {
			memo += ",原执行人为:【" + originUserName + "】,";
		}
//		memo += "指派给【" + user.getFullname() + "】执行。";
		memo += "指派给【" + ContextSupportUtil.getUsername(user) + "】执行。";
		bpmRunLogService.addRunLog(user, runId, BpmRunLog.OPERATOR_TYPE_ASSIGN, memo);
		String content = memo + " 原因为：" + voteContent;
		// 添加审批历史
		TaskOpinion taskOpinion = new TaskOpinion();
		taskOpinion.setOpinion(content);
		taskOpinion.setTaskId(Long.parseLong(taskId));
		taskOpinion.setActDefId(processRun.getActDefId());
		taskOpinion.setActInstId(processRun.getActInstId());
		taskOpinion.setStartTime(new Date());
		taskOpinion.setTaskKey(taskEntity.getTaskDefinitionKey());
		taskOpinion.setOpinionId(UniqueIdUtil.genId());
		taskOpinion.setTaskName(taskEntity.getName());
		taskOpinion.setExeUserId(curUser.getUserId());
//		taskOpinion.setExeFullname(curUser.getFullname());
		taskOpinion.setExeFullname(ContextSupportUtil.getUsername(curUser));
		taskOpinion.setCheckStatus(TaskOpinion.STATUS_CHANGE_ASIGNEE);

		// 处理审批意见的父流程ID
		this.dealTaskOpinSupId(taskEntity, taskOpinion);

		taskOpinionDao.add(taskOpinion);

		List<SysUser> receiverUser = new ArrayList<SysUser>();
		receiverUser.add(user);
		String title = "现将事项【" + processRun.getSubject() + "】指派给您,请注意查收";
		taskMessageService.sendMessage(curUser, receiverUser, informType, title, content);
		return new ResultMessage(ResultMessage.Success, "更改执行人成功！");
	}

	/**
	 * 流转任务完成时，用于更新父任务的状态
	 *
	 * @param taskId
	 * @param action
	 */
	public void updateTaskDescription(String taskId, Integer action) {
		ProcessTask task = taskDao.getByTaskId(taskId);
		String parentTaskId = task.getParentTaskId();
		Short description = 0;
		if (StringUtil.isNotEmpty(parentTaskId)) {// 多级加签
			description = TaskOpinion.STATUS_TRANSTO;
		} else {
			description = action == 1 ? TaskOpinion.STATUS_CHECKING : TaskOpinion.STATUS_COMMON_TRANSTO;
		}
		taskDao.updateTaskDescription(description.toString(), taskId);
	}

	public void delTransToTaskByParentTaskId(String parentTaskId) {
		taskDao.delTransToTaskByParentTaskId(parentTaskId);
	}

	public List<TaskEntity> getByParentTaskIdAndDesc(String parentTaskId, String description) {
		return taskDao.getByParentTaskIdAndDesc(parentTaskId, description);
	}

	/**
	 * 根据获取加签任务
	 *
	 * @param parentTaskId
	 * @return
	 */
	public List<TaskEntity> getByParentTaskId(String parentTaskId) {
		return taskDao.getByParentTaskId(parentTaskId);
	}

	public ProcessTask getTaskByParentIdDescAndUser(String parentTaskId, String description, String userId) {
		return taskDao.getTaskByParentIdDescAndUser(parentTaskId, description, userId);
	}

	public ProcessTask getTaskByParentIdAndUser(String parentTaskId, String userId) {
		return taskDao.getTaskByParentIdAndUser(parentTaskId, userId);
	}

	public List<ProcessTask> getTasksByRunId(Long runId) {
		return taskDao.getTasksByRunId(runId);
	}

	public ProcessTask getByTaskId(Long taskId) {
		return taskDao.getByTaskId(String.valueOf(taskId));
	}

	public boolean getHasRightsByTask(Long taskId, Long userId) {
		return taskDao.getHasRightsByTask(taskId, userId);
	}

	public List<ProcessRun> getTestRunsByActDefId(String actDefId) {
		return dao.getTestRunsByActDefId(actDefId);
	}

	/**
	 * 获取父流程的实例ID
	 *
	 * @param actInstId
	 *            流程的实例ID
	 * @return
	 */
	public String getSuperActInstId(String actInstId) {
		if (actInstId == null) {
			return null;
		}
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().subProcessInstanceId(actInstId).singleResult();
		if (processInstance == null) {
			return null;
		}
		String supInstId = processInstance.getProcessInstanceId();
		return supInstId;
	}

	public List<ProcessRun> getByUserIdAndDefKey(QueryFilter filter) {
		return dao.getByUserIdAndDefKey(filter);

	}

	/**
	 * 获取父流程定义ID
	 *
	 * @param runId
	 * @return
	 */
	public String getParentProcessRunActDefId(Long runId) {
		ProcessRun processRun = dao.getById(runId);
		return getParentProcessRunActDefId(processRun);
	}

	/**
	 * 获取父流程定义ID
	 *
	 * @param processRun
	 * @return
	 * @author helh
	 */
	public String getParentProcessRunActDefId(ProcessRun processRun) {
		return dao.getParentProcessRunActDefId(processRun);
	}

	/**
	 * 判断特定业务主键是否已绑定流程实例
	 *
	 * @param businessKey
	 * @return
	 * @author helh
	 */
	public boolean isProcessInstanceExisted(String businessKey) {
		if (StringUtil.isEmpty(businessKey)) {
			return false;
		}
		return dao.isProcessInstanceExisted(businessKey);
	}

	/**
	 * 處理審批意見是否添加父流程Id，如果是外部子流程則添加，否則就不添加為空
	 *
	 * @param taskEntity
	 * @param taskOpinion
	 */
	private void dealTaskOpinSupId(TaskEntity taskEntity, TaskOpinion taskOpinion) {
		// 加签会签时，获取流转的主任务
		while (taskEntity.getExecutionId() == null) {
			String parentTaskId = taskEntity.getParentTaskId();
			taskEntity = bpmService.getTask(parentTaskId);
		}

		Object isExtCall = runtimeService.getVariable(taskEntity.getExecutionId(), BpmConst.IS_EXTERNAL_CALL);
		// 外部子流程做处理
		if (isExtCall != null) {
			// 处理如果是外部子流程，运行到外部子流程节点时，审批意见添加父流程的ID
			ProcessExecution processExecution = executionDao.getById(taskEntity.getExecutionId());
			// 子流程获取主流程的实例ID
			if (StringUtil.isEmpty(processExecution.getSuperExecutionId())) {
				taskOpinion.setSuperExecution(processExecution.getParentId());
			} else {
				ProcessExecution processExecution1 = executionDao.getById(processExecution.getSuperExecutionId());
				taskOpinion.setSuperExecution(processExecution1.getParentId());
			}
		}

	}

	/**
	 * 会签补签添加意见。
	 * @param taskEntity
	 * @param userId
	 * @throws Exception
	 */
	public void addSignOpinion(ProcessTask taskEntity,Long userId) throws Exception {

		Long opinionId = UniqueIdUtil.genId();

		TaskOpinion taskOpinion = new TaskOpinion();
		taskOpinion.setOpinionId(opinionId);
		taskOpinion.setTaskId(new Long(taskEntity.getId()));
		taskOpinion.setOpinion("");
		taskOpinion.setActDefId(taskEntity.getProcessDefinitionId());
		taskOpinion.setActInstId(taskEntity.getProcessInstanceId());
		taskOpinion.setStartTime(new Date());

		taskOpinion.setExeUserId(userId);

		ISysUser user = sysUserServiceImpl.getById(userId);

//		taskOpinion.setExeFullname(user.getFullname());
		taskOpinion.setExeFullname(ContextSupportUtil.getUsername((SysUser) user));
		taskOpinion.setTaskKey(taskEntity.getTaskDefinitionKey());
		taskOpinion.setTaskName(taskEntity.getName());
		taskOpinion.setCheckStatus(TaskOpinion.STATUS_CHECKING);

		// 增加流程意见
		taskOpinionDao.add(taskOpinion);


	}

	/**
	 * 保存补签意见
	 *
	 * @param taskEntity
	 * @param opinion
	 * @param informType
	 * @param signUserIds
	 * @param subject
	 */
	public void saveAddSignOpinion(TaskEntity taskEntity, String opinion, String informType, Map<Long, Long> userTaskID, ProcessRun processRun) throws Exception {
		SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
		Long opinionId = UniqueIdUtil.genId();

		TaskOpinion taskOpinion = new TaskOpinion();
		taskOpinion.setOpinionId(opinionId);
		taskOpinion.setOpinion(opinion);
		taskOpinion.setActDefId(taskEntity.getProcessDefinitionId());
		taskOpinion.setActInstId(taskEntity.getProcessInstanceId());
		taskOpinion.setStartTime(new Date());
		taskOpinion.setEndTime(new Date());
		taskOpinion.setExeUserId(sysUser.getUserId());
//		taskOpinion.setExeFullname(sysUser.getFullname());
		taskOpinion.setExeFullname(ContextSupportUtil.getUsername(sysUser));
		taskOpinion.setTaskKey(taskEntity.getTaskDefinitionKey());
		taskOpinion.setTaskName(taskEntity.getName());
		taskOpinion.setCheckStatus(TaskOpinion.STATUS_RETROACTIVE);
		// 处理审批意见的父流程ID
		this.dealTaskOpinSupId(taskEntity, taskOpinion);

		// 增加流程意见
		taskOpinionDao.add(taskOpinion);
		// 保存接收人
		commuReceiverService.saveReceiver(opinionId, userTaskID, sysUser);
		// 发送通知。
		notifyCommu(processRun, userTaskID, informType, sysUser, opinion, SysTemplate.USE_TYPE_RETROACTIVE);

	}

	/**
	 * 处理多级加签 若父加签任务的加签类型为非会签，则删除其余流转任务，默认当前任务人为加签执行人
	 *
	 * @param taskEntity
	 */
	private void handleCascadeTransToTask(TaskEntity taskEntity) {
		String parentTaskId = taskEntity.getParentTaskId();
		if (StringUtil.isEmpty(parentTaskId))
			return;

		BpmProTransTo parentTranTo = bpmProTransToService.getByTaskId(Long.valueOf(parentTaskId));
		// 会签 直接返回
		if (parentTranTo.getTransType() != 1)
			return;
		// 非会签的情况。
		List list = getByParentTaskIdAndDesc(parentTaskId, TaskOpinion.STATUS_TRANSTO.toString());
		for (int i = 0; i < list.size(); i++) {
			ProcessTask task = (ProcessTask) list.get(i);
			if (!task.getId().equals(taskEntity.getId())) {
				taskService.deleteTask(task.getId());
			}
		}
	}

	/**
	 * 处理加签时的提交动作
	 *
	 * @param taskCmd
	 * @param sysUser
	 * @param isAgree
	 * @throws Exception
	 */
	private void handlePostAction(ProcessCmd taskCmd, SysUser sysUser) throws Exception {
		boolean isAgree = taskCmd.getVoteAgree() == 1 ? true : false;
		String taskId = taskCmd.getTaskId();
		ProcessTask parentTask = taskDao.getByTaskId(taskId);
		String parentTaskId = parentTask.getParentTaskId();
		if (StringUtil.isNotEmpty(parentTaskId)) {
			taskService.deleteTask(taskId);
			// 删除被流转任务产生的沟通任务
			taskDao.delCommuTaskByParentTaskId(taskId);
			// 添加已办历史
			this.addActivityHistory(parentTask);

			BpmProTransTo bpmProTransTo = bpmProTransToService.getByTaskId(Long.valueOf(parentTaskId));
			this.handlerTransTo(taskCmd, bpmProTransTo, sysUser, parentTaskId, isAgree);
		} else {
			this.nextProcess(taskCmd);
		}
	}

	/**
	 * 处理流转任务
	 *
	 * @param taskCmd
	 * @param bpmProTransTo
	 * @param sysUser
	 * @param parentTaskId
	 * @param isAgree
	 * @throws Exception
	 */
	public void handlerTransTo(ProcessCmd taskCmd, BpmProTransTo bpmProTransTo, SysUser sysUser, String parentTaskId, boolean isAgree) throws Exception {

		String voteContent = taskCmd.getVoteContent();
		if (StringUtil.isEmpty(voteContent)) {
//			voteContent = "【" + sysUser.getFullname() + "】代替发起流转人提交任务";
			voteContent = "【" + ContextSupportUtil.getUsername(sysUser) + "】代替发起流转人提交任务";
		}
		// 没有流转记录
		if (BeanUtils.isEmpty(bpmProTransTo))
			throw new Exception("存在流转任务，但流转状态表中无此记录！流转失败！");
		if (bpmProTransTo.getTransType() == 1) {// 非会签
			if (bpmProTransTo.getAction() == 1) {// 返回
				// 更改父执行人状态
				this.updateTaskDescription(parentTaskId, 1);
				// 删除其余流转任务
				this.delTransToTaskByParentTaskId(parentTaskId);
				// 删除流转状态
				bpmProTransToService.delById(bpmProTransTo.getId());
			} else {// 提交
				taskCmd.setTaskId(parentTaskId);
				taskCmd.setVoteAgree(isAgree ? TaskOpinion.STATUS_AGREE : TaskOpinion.STATUS_REFUSE);
				taskCmd.setVoteContent(voteContent);
				// 更改初始执行人状态为正常流转
				this.updateTaskDescription(parentTaskId, 2);
				this.delTransToTaskByParentTaskId(parentTaskId);// 删除其余流转任务
				bpmProTransToService.delById(bpmProTransTo.getId());// 删除流转状态
				this.handlePostAction(taskCmd, sysUser);
			}
		} else {// 会签
				// 记录被流转人意见
			Integer transResult = bpmProTransTo.getTransResult();
			if (transResult == 1 && !isAgree) {
				bpmProTransTo.setTransResult(2);
				bpmProTransToService.update(bpmProTransTo);
				transResult = 2;
			}

			// 根据parentTaskId和description获取剩余流转任务
			List<TaskEntity> list = this.getByParentTaskId(parentTaskId);
			// 未做完流转任务
			if (list.size() != 0)
				return;
			if (bpmProTransTo.getAction() == 1) {// 返回
				// 更改初始执行人状态
				this.updateTaskDescription(parentTaskId, 1);
				bpmProTransToService.delById(bpmProTransTo.getId());// 删除流转状态
			} else {// 提交
				taskCmd.setTaskId(parentTaskId);
				taskCmd.setVoteAgree(transResult == 1 ? TaskOpinion.STATUS_AGREE : TaskOpinion.STATUS_REFUSE);
				taskCmd.setVoteContent(voteContent);
				// 更改初始执行人状态为正常流转
				this.updateTaskDescription(parentTaskId, 2);
				bpmProTransToService.delById(bpmProTransTo.getId());// 删除流转状态
				this.handlePostAction(taskCmd, sysUser);
			}
		}
	}

	/**
	 * 级联删除流转任务（干预）
	 *
	 * @param taskId
	 */
	private void handleInterveneTransTo(String taskId) {
		// 删除流转状态
		bpmProTransToService.delByTaskId(new Long(taskId));
		// 删除被流转任务产生的沟通任务
		taskDao.delCommuTaskByParentTaskId(taskId);
		bpmProTransToService.cancelTransToTaskCascade(taskId);
	}

	/**
	 * 获取流程表单明细，
	 *
	 * @param runId
	 * @param ctxPath
	 * @return
	 * @throws Exception
	 */
	/**
	 * @param runId
	 * @param ctxPath
	 * @return
	 * @throws Exception
	 */
	public String getFormDetailByRunId(Long runId, String ctxPath) throws Exception {
		ProcessRun processRun = getById(new Long(runId));
		String businessKey = processRun.getBusinessKey();
		String formUrl = "";
		Long formDefId = 0L;
		boolean isExtForm = false;
		//流程实例的状态
		Short status=processRun.getStatus();
		String form = "";
		BpmNodeSet bpmNodeSet ;
		if (processRun.getFormDefId() == 0L && StringUtil.isNotEmpty(processRun.getBusinessUrl())) {
			isExtForm = true;
			bpmNodeSet = bpmNodeSetService.getBySetType(processRun.getActDefId(), BpmNodeSet.SetType_BpmForm);
			formUrl = bpmNodeSet.getFormUrl();
		} else {

			if(BeanUtils.isZeroEmpty(processRun.getParentId())){
				bpmNodeSet = bpmNodeSetService.getBySetType(processRun.getActDefId(), BpmNodeSet.SetType_BpmForm);
			}else{
				ProcessRun parentRun = getById(processRun.getParentId());
				bpmNodeSet = bpmNodeSetService.getBySetType(processRun.getActDefId(), BpmNodeSet.SetType_BpmForm, parentRun.getActDefId());
			}
			//优先获取流程设置中的实例表单
			if(BeanUtils.isNotEmpty(bpmNodeSet)){
				BpmFormDef formDef = bpmFormDefService.getDefaultVersionByFormKey(bpmNodeSet.getFormKey());
				if(BeanUtils.isNotEmpty(formDef)){
					formDefId = formDef.getFormDefId();
				}

			}
			//没有配置实例表单这获取流程运行实例中的表单
			if(BeanUtils.isZeroEmpty(formDefId)){
				formDefId = processRun.getFormDefId();
			}
		}
		if (!isExtForm) {// 在线表单
			BpmFormDef bpmFormDef=bpmFormDefService.getById(formDefId);
			String instanceId=processRun.getActInstId();
			//注释原来的逻辑，需要获取最新流程实例定义ID，否则表单改动时因为权限是旧的导致部分数据无法显示
			//String actDefId=processRun.getActDefId();
			//先获取最新的流程定义，然后再获取流程实例定义ID
			String actDefId=null;
			BpmDefinition definition=bpmDefinitionDao.getMainByDefKey(processRun.getFlowKey());
			if (definition==null){
				actDefId=processRun.getActDefId();
			}else {
				actDefId=definition.getActDefId();
			}
			String parentActDefId=getParentProcessRunActDefId(processRun);
			String nodeId="";
			//确保表单必须存在
			if (bpmFormDef==null){
				bpmFormDef=makeSureFormDef(actDefId,parentActDefId);
			}
			//判断流程是否结束,如果未结束,则根据流程历史查找对应的节点表单.
			if (status!=2 && status!=3) {
				//通过审批历史获取到最新审批记录
				TaskOpinion taskOpinion = taskOpinionService.getLatestUserOpinion(processRun.getActInstId(), ContextUtil.getCurrentUserId());
				if(BeanUtils.isNotEmpty(taskOpinion)){
					nodeId=taskOpinion.getTaskKey();
					//根据最新记录找到对应任务节点的设置
					bpmNodeSet = bpmNodeSetService.getFormByActDefIdNodeId(actDefId, nodeId, parentActDefId, false);
					if(bpmNodeSet != null && StringUtil.isNotEmpty(bpmNodeSet.getFormKey())){
						bpmFormDef=bpmFormDefService.getDefaultPublishedByFormKey(bpmNodeSet.getFormKey());
					}
				}
			}
			form = bpmFormHandlerService.obtainHtml(bpmFormDef,  businessKey,
					instanceId, actDefId, nodeId , ctxPath, parentActDefId, false,false,true,(short)0) ;
			//form = bpmFormHandlerService.getFormDetail(formDefId, businessKey, currUserId, processRun, ctxPath, false);
		} else {
			form = formUrl;
			if (StringUtil.isNotEmpty(businessKey)) {
				form = form.replaceFirst(BpmConst.FORM_PK_REGEX, businessKey);
			}
			// 替换主键。
			if (!formUrl.startsWith("http")) {
				form = ctxPath + form;
			}
		}
		return form;
	}

	/**
	 * 确保保单必须存在
	 * @param actDefId
	 * @param parentActDefId
	 */
	private BpmFormDef makeSureFormDef(String actDefId, String parentActDefId) {
		BpmNodeSet nodeSet=null;
		if (StringUtil.isEmpty(parentActDefId)){
			nodeSet=bpmNodeSetService.getBySetType(actDefId,BpmNodeSet.SetType_GloabalForm);
		}else {
			nodeSet=bpmNodeSetService.getBySetType(actDefId,BpmNodeSet.SetType_GloabalForm,parentActDefId);
		}
		return bpmFormDefService.getDefaultPublishedByFormKey(nodeSet.getFormKey());
	}

	/**
	 * 根据关联runId获取对应的业务主键。
	 *
	 * <pre>
	 * 1.根据关联runId获取历史实例对象。
	 * 2.则获取业务主键数据。
	 * </pre>
	 *
	 * @param relRunId
	 *            关联runId。
	 * @return 业务主键。
	 */
	public String getBusinessKeyByRelRunId(Long relRunId) {
		Long empId = 0L;
		if (relRunId == null || empId.equals(relRunId))
			return "";
		ProcessRun processRun = dao.getByHistoryId(relRunId);
		String businessKey = processRun.getBusinessKey();
		return businessKey;
	}

	/**
	 * 获取历史实例对象。
	 *
	 * @param runId
	 * @return
	 */
	public ProcessRun getByHistoryId(Long runId) {
		ProcessRun processRun = dao.getByHistoryId(runId);
		return processRun;
	}

	public void delByBusinessKey(String businessKey) {
		dao.delByBusinessKey(businessKey);
	}

	public void delByBusinessNum(Long businessKey) {
		dao.delByBusinessKeyNum(businessKey);
	}

	/**
	 * 我的办结和抄送转发给我的办结流程
	 *
	 * @return
	 */
	public List<ProcessRun> getMyCompletedAndCptoList(QueryFilter filter) {
		return dao.getMyCompletedAndCptoList(filter);
	}


	/**
	 * 处理流转操作。
	 * @param taskOpinion
	 * @param informType
	 * @param isAgree
	 * @param taskCmd
	 * @throws Exception
	 */
	public void handTransTo(TaskOpinion taskOpinion,String informType ,	boolean isAgree,ProcessCmd taskCmd  ) throws Exception{
		taskOpinion.setOpinionId(UniqueIdUtil.genId());
		SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
		taskCmd.setVoteContent(taskOpinion.getOpinion());//设置意见
			TaskEntity taskEntity = bpmService.getTask(taskOpinion.getTaskId().toString());
			ProcessRun processRun = getByActInstanceId(new Long(taskEntity.getProcessInstanceId()));

			String description = taskEntity.getDescription();
			Integer sysTemplateType = 0;
			taskOpinion.setActDefId(taskEntity.getProcessDefinitionId());
			taskOpinion.setActInstId(taskEntity.getProcessInstanceId());
			taskOpinion.setStartTime(taskEntity.getCreateTime());
			taskOpinion.setEndTime(new Date());
			Long duration = calendarAssignService.getRealWorkTime(taskEntity.getCreateTime(), new Date(), sysUser.getUserId());
			taskOpinion.setDurTime(duration);
			taskOpinion.setExeUserId(sysUser.getUserId());
//		    taskOpinion.setExeFullname(sysUser.getFullname());
		    taskOpinion.setExeFullname(ContextSupportUtil.getUsername(sysUser));
			taskOpinion.setTaskKey(taskEntity.getTaskDefinitionKey());
			taskOpinion.setTaskName(taskEntity.getName());
			if (description.equals(TaskOpinion.STATUS_TRANSTO.toString()) || description.equals(TaskOpinion.STATUS_TRANSTO_ING.toString())) {// 流转
				if (taskEntity.getAssignee().equals(sysUser.getUserId().toString())) {
					taskOpinion.setCheckStatus(TaskOpinion.STATUS_REPLACE_SUBMIT);
				} else {
					taskOpinion.setCheckStatus(TaskOpinion.STATUS_INTERVENE);
					String opinion = taskOpinion.getOpinion();
					Long userId = Long.valueOf(taskEntity.getAssignee());
					SysUser assignee = (SysUser) sysUserServiceImpl.getById(userId);
//					opinion += "(原执行人【" + assignee.getFullname() + "】)";
					opinion += "(原执行人【" + ContextSupportUtil.getUsername(assignee) + "】)";
					taskOpinion.setOpinion(opinion);
				}
				sysTemplateType = SysTemplate.USE_TYPE_TRANSTO_FEEDBACK;
				taskCmd.setVoteAgree(isAgree ? TaskOpinion.STATUS_AGREE : TaskOpinion.STATUS_REFUSE);
			} else {// 沟通
				taskOpinion.setCheckStatus(TaskOpinion.STATUS_NOTIFY);
				sysTemplateType = SysTemplate.USE_TYPE_FEEDBACK;
			}

			// 向原执行人发送任务完成提醒消息
			Map<Long, Long> usrIdTaskIds = new HashMap<Long, Long>();
			ProcessTask parentTask = getByTaskId(Long.valueOf(taskEntity.getParentTaskId()));
			usrIdTaskIds.put(Long.valueOf(parentTask.getAssignee()), Long.valueOf(taskEntity.getParentTaskId()));
			notifyCommu(processRun, usrIdTaskIds, informType, sysUser, taskOpinion.getOpinion(), sysTemplateType);

			//意见加签回填-->
			String parentActDefId = (String) taskService.getVariable(taskCmd.getTaskId(), BpmConst.FLOW_PARENT_ACTDEFID);
			String actDefId=taskEntity.getProcessDefinitionId();
			String nodeId=taskEntity.getTaskDefinitionKey();
			BpmNodeSet bpmNodeSet = null;
			if (StringUtil.isEmpty(parentActDefId)) {
				bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId,nodeId,null);
			} else {// 获取子流程节点数据
				bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId, parentActDefId);
			}
			String opinionField = bpmNodeSet.getOpinionField();
			Short opinionHtml = bpmNodeSet.getOpinionHtml();
			if (StringUtil.isNotEmpty(opinionField)) {
				taskCmd.addTransientVar(BpmConst.OPINION_FIELD, opinionField);
				taskCmd.addTransientVar(BpmConst.OPINION_SUPPORTHTML, opinionHtml);
			}
			//<--意见加签回填

			// 处理表单
			BpmFormData bpmFormData = handlerFormData(taskCmd, processRun, taskEntity.getTaskDefinitionKey());
			if (bpmFormData != null) {
				Map<String, String> optionsMap = new HashMap<String, String>();
				optionsMap.put("option", taskOpinion.getOpinion());
				// 记录意见
				updOption(optionsMap, taskCmd);
			}

			// 保存反馈信息 这里面会删除task的数据
			saveOpinion(taskEntity, taskOpinion);
			// 设置沟通人员或流转人员的状态为已反馈
			commuReceiverService.setCommuReceiverStatusToFeedBack(taskEntity, sysUser);

			// 添加已办历史
			addActivityHistory(taskEntity);

			// 判断是否流转任务
			if (description.equals(TaskOpinion.STATUS_TRANSTO.toString()) || description.equals(TaskOpinion.STATUS_TRANSTO_ING.toString())) {// 流转任务
				String parentTaskId = taskEntity.getParentTaskId();
				BpmProTransTo bpmProTransTo = bpmProTransToService.getByTaskId(Long.valueOf(parentTaskId));
				handlerTransTo(taskCmd, bpmProTransTo, sysUser, parentTaskId, isAgree);
			}

			Long runId = processRun.getRunId();

			String memo = "在:【" + processRun.getSubject() + "】,节点【" + taskEntity.getName() + "】,意见:" + taskOpinion.getOpinion();
			bpmRunLogService.addRunLog(runId, BpmRunLog.OPERATOR_TYPE_ADDOPINION, memo);
	}

	/**
	 * 更新意见。
	 *
	 * <pre>
	 * 1.首先根据任务id查询意见，应为意见在task创建时这个意见就被产生出来，所以能直接获取到该意见。
	 * 2.获取意见，注意一个节点只允许一个意见填写，不能在同一个任务节点上同时允许两个意见框的填写，比如同时科员意见，局长意见等。
	 * 3.更新意见。
	 * </pre>
	 *
	 * @param optionsMap
	 * @param taskId
	 */
	private void updOption(Map<String, String> optionsMap, ProcessCmd cmd) {
		if (BeanUtils.isEmpty(optionsMap))
			return;

		Set<String> set = optionsMap.keySet();
		String key = set.iterator().next();
		String value = optionsMap.get(key);
		cmd.setVoteFieldName(key);
		cmd.setVoteContent(value);

	}

	/**
	 * 我的流程和抄送转发给我的办结流程
	 * @param filter
	 * @return
	 */
	public List<ProcessRun> getMyFlowsAndCptoList(QueryFilter filter) {
		return dao.getMyFlowsAndCptoList(filter);
	}

	/**
	 * 通过account获得用户的UserID
	 * @param account
	 * @return
	 */
	public SysUser getUseridByAccount(String account){
		return dao.getUseridByAccount(account);
	}

	public void addExtend(JSONObject jsonObject) {
		dao.update("addExtend",jsonObject.toString());
	}

	public SysUser getUseridByAliasname(String account) {
		// TODO Auto-generated method stub
		return dao.getUseridByAliasname(account);
	}
	public List<ProcessRun> getAlreadyMattersLists(QueryFilter filter) {
		return dao.getAlreadyMattersLists(filter);
	}
}
