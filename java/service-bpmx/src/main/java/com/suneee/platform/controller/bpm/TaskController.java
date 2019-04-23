package com.suneee.platform.controller.bpm;

import com.suneee.core.api.bpm.model.IProcessRun;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.bpm.model.*;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.bpm.util.BpmUtil;
import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.model.ForkTaskReject;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.table.TableModel;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.HttpConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.HttpUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.bpm.*;
import com.suneee.platform.model.bpm.BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysFile;
import com.suneee.platform.model.system.SysProperty;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.util.WarningSetting;
import com.suneee.platform.service.bpm.*;
import com.suneee.platform.service.bpm.BpmNodeSignService.BpmNodePrivilegeType;
import com.suneee.platform.service.bpm.impl.BpmActService;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.form.*;
import com.suneee.platform.service.system.IdentityService;
import com.suneee.platform.service.system.*;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.platform.service.worktime.CalendarAssignService;
import com.suneee.ucp.base.common.ResultConst;
import com.suneee.ucp.base.vo.ResultVo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.activiti.engine.*;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 后台任务管理控制类
 * 
 * @author csx
 * 
 */
@Controller
@RequestMapping("/platform/bpm/task/")
@Action(ownermodel = SysAuditModelType.PROCESS_MANAGEMENT)
public class TaskController extends BaseController {
	protected Logger logger = LoggerFactory.getLogger(TaskController.class);
	@Resource
	private BpmService bpmService;
	@Resource
	private TaskService taskService;
	@Resource
	private ProcessRunService processRunService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private BpmNodeSetService bpmNodeSetService;
	@Resource
	private TaskSignDataService taskSignDataService;
	@Resource
	private BpmNodeSignService bpmNodeSignService;
	@Resource
	private ExecutionStackService executionStackService;
	@Resource
	private SysUserService sysUserService;
	@Resource
	private BpmFormHandlerService bpmFormHandlerService;
	@Resource
	private BpmFormDefService bpmFormDefService;
	@Resource
	private BpmNodeUserService bpmNodeUserService;
	@Resource
	private TaskUserService taskUserService;
	@Resource
	private BpmFormRunService bpmFormRunService;
	@Resource
	private TaskApprovalItemsService taskAppItemService;
	@Resource
	private BpmFormTableService bpmFormTableService;
	@Resource
	private BpmNodeButtonService bpmNodeButtonService;
	@Resource
	private BpmRunLogService bpmRunLogService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskReadService taskReadService;
	@Resource
	private CommuReceiverService commuReceiverService;
	@Resource
	private BpmGangedSetService bpmGangedSetService;
	@Resource
	private BpmTaskExeService bpmTaskExeService;
	@Resource
	private TaskOpinionService taskOpinionService;
	@Resource
	private SysFileService sysFileService;
	@Resource
	private SysErrorLogService sysErrorLogService;
	@Resource
	private TaskHistoryService taskHistoryService;
	@Resource
	private BpmProTransToService bpmProTransToService;
	@Resource
	private CalendarAssignService calendarAssignService;

	@Resource
	private BpmDefAuthorizeService bpmDefAuthorizeService;
	@Resource
	private BpmActService bpmActService;
	@Resource
	private SysPropertyService sysPropertyService;
	@Resource
	private TaskReminderService reminderService;
	@Resource
	private BpmFormRightsService bpmFormRightsService;
	@Resource
	private HistoryService historyService;
	@Resource
	private RepositoryService repositoryService;
	@Resource
	private IdentityService identityService;
	
	/**
	 * 取得起始表单。
	 * 
	 * @param bpmNodeSet
	 * @param
	 * @return
	 * @throws Exception
	 */
	private FormModel getStartForm(BpmNodeSet bpmNodeSet, String businessKey, String actDefId, String ctxPath, boolean isReCalc,boolean isCopy) throws Exception {
		FormModel formModel = new FormModel();
		if (bpmNodeSet == null || bpmNodeSet.getFormType() == -1)
			return formModel;
		if (bpmNodeSet.getFormType() == BpmConst.OnLineForm) {
			String formKey = bpmNodeSet.getFormKey();
			if (StringUtil.isNotEmpty(formKey)) {
				BpmFormDef bpmFormDef = bpmFormDefService.getDefaultPublishedByFormKey(formKey);
				if (bpmFormDef != null) {
					//String formHtml = bpmFormHandlerService.obtainHtml(bpmFormDef, businessKey, "", actDefId, bpmNodeSet.getNodeId(), ctxPath, "", isReCalc, false, false);
					
					//--->测试新解释器
					String formHtml = bpmFormDefService.parseHtml(bpmFormDef, businessKey, "", actDefId, bpmNodeSet.getNodeId(), "", isReCalc, true, false,isCopy,(short) 0);
					//<---测试新解释器

					formModel.setFormHtml(formHtml);
				}
			}
		} else {
			String formUrl = bpmNodeSet.getFormUrl();
			// 替换主键。
			formUrl = formUrl.replaceFirst(BpmConst.FORM_PK_REGEX, businessKey);
			if (!formUrl.startsWith("http")) {
				formUrl = ctxPath + formUrl;
			}
			formModel.setFormType(BpmConst.UrlForm);
			formModel.setFormUrl(formUrl);
		}
		return formModel;
	}

	private Map getPageParam(HttpServletRequest request) {
		Map paraMap = RequestUtil.getParameterValueMap(request, false, false);
		paraMap.remove("businessKey");
		paraMap.remove("defId");
		return paraMap;
	}

	private ModelAndView getNotValidView(BpmDefinition bpmDefinition, String businessKey) {
		if (BeanUtils.isEmpty(bpmDefinition))
			return ServiceUtil.getTipInfo("该流程定义已经被删除!");
		if (bpmDefinition.getStatus().equals(BpmDefinition.STATUS_DISABLED) || bpmDefinition.getStatus().equals(BpmDefinition.STATUS_INST_DISABLED))
			return ServiceUtil.getTipInfo("该流程定义已经被禁用!");
		// 判断该业务主键是否已绑定流程实例
		boolean isProcessInstanceExisted = processRunService.isProcessInstanceExisted(businessKey);
		if (isProcessInstanceExisted) {
			return ServiceUtil.getTipInfo("对不起，该流程实例已存在，不需要再次启动!");
		}
		return null;
	}

	/**
	 * 单个流程访问地址权限控制
	 * 
	 * @return
	 */
	@RequestMapping("toStartFlow/{flowKey}")
	public ModelAndView toStartFlow(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "flowKey") String flowKey) throws Exception {
		BpmDefinition def = bpmDefinitionService.getMainByDefKey(flowKey);
		if (def == null)
			return ServiceUtil.getTipInfo("该流程定义已经被删除!");

		return startFlowForm(request, response, def.getDefId());
	}

	/**
	 * 跳转到启动流程页面。<br/>
	 * 
	 * <pre>
	 * 传入参数流程定义id：defId。 
	 * 实现方法： 
	 * 1.根据流程对应ID查询流程定义。 
	 * 2.获取流程定义的XML。
	 * 3.获取流程定义的第一个任务节点。
	 * 4.获取任务节点的流程表单定义。 
	 * 5.显示启动流程表单页面。
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("startFlowForm")
	@Action(description = "跳至启动流程页面")
	public ModelAndView startFlowForm(HttpServletRequest request, HttpServletResponse response, Long defId) throws Exception {
		String businessKey = RequestUtil.getString(request, "businessKey");
		// 复制表单 启动流程
		String copyKey = RequestUtil.getString(request, "copyKey", "");
		ISysUser sysUser = ContextUtil.getCurrentUser();
		String ctxPath = request.getContextPath();
		// 获取流程类型
		String flowType = RequestUtil.getString(request, "flowType");
		ModelAndView mv = new ModelAndView("/platform/bpm/taskStartFlowForm");

		// 流程草稿传入
		Long runId = RequestUtil.getLong(request, "runId", 0L);
		// 从已经完成的流程实例启动流程。
		Long relRunId = RequestUtil.getLong(request, "relRunId", 0L);

		// 构建参数到到JSP页面。
		Map paraMap = getPageParam(request);

		ProcessRun processRun = null;
		BpmDefinition bpmDefinition = null;
		if (StringUtils.isNotEmpty(businessKey) && runId == 0) {
			processRun = processRunService.getByBusinessKey(businessKey);
			if (BeanUtils.isEmpty(processRun)) {//业务数据模板新增表单后，在列表启动流程，没有流程实例
				defId = RequestUtil.getLong(request, "defId");
			} else {
				defId = processRun.getDefId();
				runId = processRun.getRunId();
			}
		}

		if (runId != 0) {
			processRun = processRunService.getById(runId);
			defId = processRun.getDefId();
		}
		
		if (StringUtils.isNotBlank(flowType) && (defId == null || defId == 0L)) {
			// 根据流程类型获取流程实例ID
			defId = sysPropertyService.getLongByAlias(flowType);
		}

		if (defId != null && defId != 0L)
			bpmDefinition = bpmDefinitionService.getById(defId);

		ModelAndView tmpView = getNotValidView(bpmDefinition, businessKey);
		if (tmpView != null)
			return tmpView;

		// 根据已经完成的流程实例取得业务主键。
		String pk = processRunService.getBusinessKeyByRelRunId(relRunId);
		if (StringUtil.isNotEmpty(pk)) {
			businessKey = pk;
		}
		Boolean isFormEmpty = false;
		Boolean isExtForm = false;
		String form = "";
		String actDefId = "";
		// 通过草稿启动流程
		if (BeanUtils.isNotEmpty(processRun) && processRun.getStatus().equals(ProcessRun.STATUS_FORM)||BeanUtils.isNotEmpty(processRun) && processRun.getStatus().equals(ProcessRun.STATUS_SUBPROCESS_FORM)) {
			mv.addObject("isDraft", false);
			businessKey = processRun.getBusinessKey();
			Long formDefId = processRun.getFormDefId();
			actDefId = processRun.getActDefId();
			//是否使用新版本，草稿启动后会记录表单ID,如果
			int isNewVersion = RequestUtil.getInt(request, "isNewVersion", 0);
			if (formDefId != 0L) {
				String tableName = processRun.getTableName();
				if (!tableName.startsWith(TableModel.CUSTOMER_TABLE_PREFIX)) {
					tableName = TableModel.CUSTOMER_TABLE_PREFIX + tableName;
				}
				boolean isExistsData = bpmFormHandlerService.isExistsData(processRun.getDsAlias(), tableName, processRun.getPkName(), processRun.getBusinessKey());
				if (!isExistsData)
					return new ModelAndView("redirect:noData.ht");
			}

			if (StringUtil.isNotEmpty(processRun.getBusinessUrl())) {
				isExtForm = true;
				form = processRun.getBusinessUrl();
				// 替换主键。
				form = processRun.getBusinessUrl().replaceFirst(BpmConst.FORM_PK_REGEX, businessKey);
				if (!form.startsWith("http")) {
					form = ctxPath + form;
				}
			} else {
				if (isNewVersion == 1) {
					BpmFormDef defaultFormDef = bpmFormDefService.getById(formDefId);
					formDefId = bpmFormDefService.getDefaultPublishedByFormKey(defaultFormDef.getFormKey()).getFormDefId();
				}
				String nodeId = "";// 流程第一个节点
				FlowNode flowNode = NodeCache.getFirstNodeId(actDefId);
				if (flowNode != null) {
					nodeId = flowNode.getNodeId();
				}
				BpmFormDef bpmFormDef = bpmFormDefService.getById(formDefId);
				form = bpmFormHandlerService.obtainHtml(bpmFormDef, businessKey, "", actDefId, nodeId, ctxPath, "", true, false, false,processRun.getStatus());
				//下面调用新的解释器
				form = bpmFormDefService.parseHtml(bpmFormDef, businessKey, "", actDefId, nodeId, "", true, true, false,isExtForm, processRun.getStatus());
			}
			// 流程定义里面的启动
		} else {
			boolean isReCalcuate = false;
			if (StringUtil.isNotEmpty(copyKey)) {
				businessKey = copyKey;
				isReCalcuate = true;
			}
			mv.addObject("isDraft", true);
			actDefId = bpmDefinition.getActDefId();

			// 获取表单节点
			BpmNodeSet bpmNodeSet = bpmNodeSetService.getStartBpmNodeSet(actDefId, false);

			FormModel formModel = getStartForm(bpmNodeSet, businessKey, actDefId, ctxPath, isReCalcuate,StringUtil.isNotEmpty(copyKey));
			// 是外部表单
			isFormEmpty = formModel.isFormEmpty();
			isExtForm = formModel.getFormType() > 0;

			if (isExtForm) {
				form = formModel.getFormUrl();
			} else if (formModel.getFormType() == 0) {
				form = formModel.getFormHtml();
			}
			if (BeanUtils.isNotEmpty(bpmNodeSet)) {
				mv.addObject("formKey", bpmNodeSet.getFormKey());
			}
		}
		// 获取按钮
		Map<String, List<BpmNodeButton>> mapButton = bpmNodeButtonService.getMapByStartForm(defId);
		// 帮助文档
		SysFile sysFile = null;
		if (BeanUtils.isNotEmpty(bpmDefinition.getAttachment()))
			sysFile = sysFileService.getById(bpmDefinition.getAttachment());

		// 通过defid和nodeId获取联动设置
		List<BpmGangedSet> bpmGangedSets = bpmGangedSetService.getByDefIdAndNodeId(defId, BpmGangedSet.START_NODEID);
		JSONArray gangedSetJarray = (JSONArray) JSONArray.fromObject(bpmGangedSets);

		if (NodeCache.isMultipleFirstNode(actDefId)) {
			mv.addObject("flowNodeList", NodeCache.getFirstNode(actDefId)).addObject("isMultipleFirstNode", true);
		}
		
		// 消息类型
		Map<String, IMessageHandler> handlersMap = this.getMessageType(bpmDefinition);

		mv.addObject("bpmDefinition", bpmDefinition)
			.addObject("isExtForm", isExtForm)
			.addObject("isFormEmpty", isFormEmpty)
			.addObject("mapButton", mapButton)
			.addObject("defId", defId)
			.addObject("paraMap", paraMap)
			.addObject("form", form)
			.addObject("runId", runId)
			.addObject("businessKey", StringUtil.isEmpty(copyKey) ? businessKey : "")
			.addObject("sysFile", sysFile)
			.addObject("bpmGangedSets", gangedSetJarray)
			.addObject("curUserId", sysUser.getUserId().toString())
//			.addObject("curUserName", sysUser.getFullname())
             .addObject("curUserName", ContextSupportUtil.getUsername((SysUser) sysUser))
			.addObject("handlersMap", handlersMap);
		return mv;
	}

	/**
	 * 启动流程。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "startFlow", method = RequestMethod.POST)
	@ResponseBody
	@Action(description = "启动流程")
	public ResultVo startFlow(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultVo result=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		Long runId = RequestUtil.getLong(request, "runId", 0L);
		try {
			ProcessCmd processCmd = BpmUtil.getProcessCmd(request);
			Long userId = ContextUtil.getCurrentUserId();
			processCmd.setCurrentUserId(userId.toString());
			if (runId != 0L) {
				ProcessRun processRun = processRunService.getById(runId);
				if (BeanUtils.isEmpty(processRun)) {
					result.setMessage("流程草稿不存在或已被清除");
					return result;
				}
				processCmd.setProcessRun((IProcessRun) processRun);
			}
			processRunService.startProcess(processCmd);
			//List<Task> taskList = TaskThreadService.getNewTasks();
			List<TaskOpinion> lists = taskOpinionService.getByRunId(processCmd.getProcessRun().getRunId());
			List<TaskOpinion> newlist = new ArrayList<>();
			for (TaskOpinion taskopin:lists
					) {
				if(taskopin.getEndTime()==null){
					newlist.add(taskopin);
				}
			}

			//流程表单流水号根据规则变化
			Long defId = processCmd.getProcessRun().getFormDefId();
			if(null!=defId){
				BpmFormDef bpmFormDef = bpmFormDefService.getById(defId);
				BpmFormTable formTable = bpmFormDef.getBpmFormTable();
				List<BpmFormField> fieldList = formTable.getFieldList();
				for (BpmFormField file:fieldList){
					if (file.getValueFrom() == BpmFormField.VALUE_FROM_IDENTITY) {
						// 如果actDefId=#dataTem 则说明是业务数据模板调用数据,需要立刻获取流水号
						identityService.nextId(file.getIdentity());
					}
				}
			}
			String str = "";
			if(newlist.size()>0){
				str=newlist.get(0).getTaskName();
			}
			//BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(processCmd.getActDefId(), processCmd.getDestTask(), "");
			//String str = bpmNodeSet.getNodeName();
			//提示流程提交至否一节点

			result.setMessage("启动流程成功!"+"成功流转至<br><p style='color:red'>"+str+"</p>节点");
			result.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
			return result;
		} catch (Exception ex) {
			logger.debug("startFlow:" + ex.getMessage());
			ex.printStackTrace();
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				result.setMessage("启动流程失败:\r\n" + str);
			} else {
				String message = ex.getMessage();
				if(StringUtil.isEmpty(message)){
					Throwable tt = ex.getCause();
					message = tt.getMessage();
				}
				result.setMessage(message);
			}
			return result;
		}
	}

	/**
	 * 保存草稿
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("saveForm")
	@ResponseBody
	@Action(description = "保存草稿")
	public ResultVo saveForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ProcessCmd processCmd = BpmUtil.getProcessCmd(request);
		Long userId = ContextSupportUtil.getCurrentUserId();
		processCmd.setCurrentUserId(userId.toString());
		try {
			processRunService.saveForm(processCmd);
			return new ResultVo(ResultConst.COMMON_STATUS_SUCCESS,"保存草稿成功！");
		} catch (Exception e) {
			String message = ExceptionUtil.getExceptionMessage(e);
			return new ResultVo(ResultConst.COMMON_STATUS_FAILED,message);
		}
	}

	/**
	 * 保存表单数据。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("saveData")
	@Action(description = "保存表单数据", detail = "<#if StringUtils.isNotEmpty(taskId)>" + "保存流程【${SysAuditLinkService.getProcessRunLink(taskId)}】的表单数据" + "<#elseif StringUtils.isNotEmpty(defId)>" + "保存流程定义【${SysAuditLinkService.getBpmDefinitionLink(Long.valueOf(defId))}】至草稿" + "</#if>")
	public void saveData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String userId = ContextUtil.getCurrentUserId().toString();
		ProcessCmd processCmd = BpmUtil.getProcessCmd(request);
		Long runId = RequestUtil.getLong(request, "runId", 0L);
		if (runId != 0L) {
			ProcessRun processRun = processRunService.getById(runId);
			processCmd.setProcessRun((IProcessRun) processRun);
		}
		processCmd.setCurrentUserId(userId);
		ResponseMessage resultMessage = null;
		try {
			processRunService.saveData(processCmd);
			//需要删除的附件id
			Long[] delFileIds = RequestUtil.getLongAryByStr(request, "delFileIds");
			//删除附件
			if (null != delFileIds && delFileIds.length > 0) {
				sysFileService.delSysFileByIds(delFileIds);
			}
			resultMessage = ResponseMessage.success("保存表单数据成功！");
		} catch (Exception ex) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				resultMessage = ResponseMessage.fail("保存表单数据失败！:" + str);
			} else {
				String message = ExceptionUtil.getExceptionMessage(ex);
				resultMessage = ResponseMessage.fail(message);
			}
		}
		HttpUtil.writeResponseJson(response,resultMessage);
	}

	@RequestMapping("saveOpinion")
	@ResponseBody
	@Action(description = "保存流程沟通或流转意见")
	public String saveOpinion(HttpServletRequest request, HttpServletResponse response, TaskOpinion taskOpinion) throws Exception {
		JSONObject jobject = new JSONObject();
		String informType = RequestUtil.getString(request, "informType");
		boolean isAgree = RequestUtil.getBoolean(request, "isAgree");
		ProcessCmd taskCmd = BpmUtil.getProcessCmd(request);
		taskCmd.setVoteContent(taskOpinion.getOpinion());//设置意见
		try {
			processRunService.handTransTo(taskOpinion, informType, isAgree, taskCmd);
			jobject.accumulate("result", ResultMessage.Success).accumulate("message", "添加意见成功!");
			return jobject.toString();
		} catch (Exception ex) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				jobject.accumulate("result", ResultMessage.Fail).accumulate("message", "添加意见失败:" + str);
				return jobject.toString();
			} else {
				ex.printStackTrace();
				String message = ExceptionUtil.getExceptionMessage(ex);
				jobject.accumulate("result", ResultMessage.Fail).accumulate("message", message);
				return jobject.toString();
			}
		}
	}

	@RequestMapping("saveOpinionFront")
	@ResponseBody
	@Action(description = "保存流程沟通或流转意见")
	public ResultVo saveOpinionFront(HttpServletRequest request, HttpServletResponse response, TaskOpinion taskOpinion) throws Exception {
		String informType = RequestUtil.getString(request, "informType");
		boolean isAgree = RequestUtil.getBoolean(request, "isAgree");
		ProcessCmd taskCmd = BpmUtil.getProcessCmd(request);
		taskCmd.setVoteContent(taskOpinion.getOpinion());//设置意见
		try {
			processRunService.handTransTo(taskOpinion, informType, isAgree, taskCmd);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"提交成功！");
		} catch (Exception ex) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"提交失败！",str);
			} else {
				ex.printStackTrace();
				String message = ExceptionUtil.getExceptionMessage(ex);
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"提交失败！",message);
			}
		}
	}

	/**
	 * 显示任务回退的执行路径
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("back")
	public ModelAndView back(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String taskId = request.getParameter("taskId");

		if (StringUtils.isEmpty(taskId))
			return getAutoView();

		TaskEntity taskEntity = bpmService.getTask(taskId);
		String taskToken = (String) taskService.getVariableLocal(taskEntity.getId(), TaskFork.TAKEN_VAR_NAME);
		ExecutionStack executionStack = executionStackService.getLastestStack(taskEntity.getProcessInstanceId(), taskEntity.getTaskDefinitionKey(), taskToken);
		if (executionStack != null && executionStack.getParentId() != null && executionStack.getParentId() != 0) {
			ExecutionStack parentStack = executionStackService.getById(executionStack.getParentId());
			String assigneeNames = "";
			if (StringUtils.isNotEmpty(parentStack.getAssignees())) {
				String[] uIds = parentStack.getAssignees().split("[,]");
				int i = 0;
				for (String uId : uIds) {
					SysUser sysUser = sysUserService.getById(new Long(uId));
					if (sysUser == null)
						continue;
					if (i++ > 0) {
						assigneeNames += ",";
					}
//					assigneeNames += sysUser.getFullname();
					assigneeNames += ContextSupportUtil.getUsername(sysUser);
				}
			}
			request.setAttribute("assigneeNames", assigneeNames);
			request.setAttribute("parentStack", parentStack);
		}

		request.setAttribute("taskId", taskId);

		return getAutoView();
	}

	/**
	 * 任务回退
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("jumpBack")
	public ModelAndView jumpBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ProcessCmd processCmd = BpmUtil.getProcessCmd(request);
		processCmd.setCurrentUserId(ContextUtil.getCurrentUserId().toString());
		processRunService.nextProcess(processCmd);
		return new ModelAndView("redirect:list.ht");
	}

	/**
	 * 跳至会签页
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("toSign")
	public ModelAndView toSign(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String taskId = request.getParameter("taskId");
		ModelAndView modelView = getAutoView();

		if (StringUtils.isNotEmpty(taskId)) {
			TaskEntity taskEntity = bpmService.getTask(taskId);
			String nodeId = bpmService.getExecution(taskEntity.getExecutionId()).getActivityId();
			String actInstId = taskEntity.getProcessInstanceId();

			List<TaskSignData> signDataList = taskSignDataService.getByNodeAndInstanceId(actInstId, nodeId);

			// 获取会签规则
			BpmNodeSign bpmNodeSign = bpmNodeSignService.getByDefIdAndNodeId(taskEntity.getProcessDefinitionId(), nodeId);

			modelView.addObject("signDataList", signDataList);
			modelView.addObject("task", taskEntity);
			modelView.addObject("curUser", ContextUtil.getCurrentUser());
			modelView.addObject("bpmNodeSign", bpmNodeSign);
		}

		return modelView;
	}

	/**
	 * 补签
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("addSign")
	@ResponseBody
	public String addSign(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String taskId = request.getParameter("taskId");
		String signUserIds = request.getParameter("signUserIds");
		String opinion = request.getParameter("opinion");
		String informType = RequestUtil.getString(request, "informType");
		if (StringUtils.isNotEmpty(taskId) && StringUtils.isNotEmpty(signUserIds) && StringUtils.isNotEmpty(opinion)) {
			// 保存意见
			taskSignDataService.addSign(signUserIds, taskId, opinion, informType);
		}
		return SUCCESS;
	}

	/**
	 * 任务自由跳转
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("jump")
	@ResponseBody
	public String jump(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String taskId = request.getParameter("taskId");
		String destTask = request.getParameter("destTask");
		TaskEntity taskEnt = bpmService.getTask(taskId);
		bpmService.transTo(taskEnt, destTask);

		return SUCCESS;
	}

	/**
	 * 跳至会签页
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("saveSign")
	@ResponseBody
	public String saveSign(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String taskId = request.getParameter("taskId");
		String isAgree = request.getParameter("isAgree");
		String content = request.getParameter("content");

		taskSignDataService.signVoteTask(taskId, content, new Short(isAgree));

		return SUCCESS;
	}

	@SuppressWarnings("unused")
	@RequestMapping("list")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "taskItem");

		// 增加按新的流程分管授权中任务类型的权限获取流程的任务
		Long userId = ContextUtil.getCurrentUserId();
		String isNeedRight = "";

		Map<String, AuthorizeRight> authorizeRightMap = null;
		if (!ContextUtil.isSuperAdmin()) {
			isNeedRight = "yes";
			// 获得流程分管授权与用户相关的信息
			Map<String, Object> actRightMap = bpmDefAuthorizeService.getActRightByUserMap(userId, BPMDEFAUTHORIZE_RIGHT_TYPE.TASK, false, false);
			// 获得流程分管授权与用户相关的信息集合的流程KEY
			String actRights = (String) actRightMap.get("authorizeIds");
			filter.addFilter("actRights", actRights);
		}
		filter.addFilter("isNeedRight", isNeedRight);

		List<TaskEntity> list = bpmService.getTasks(filter);

		request.getSession().setAttribute("isAdmin", true);

		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);

		ModelAndView mv = getAutoView().addObject("taskList", list).addObject("hasGlobalFlowNo", hasGlobalFlowNo);

		return mv;
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping("forMe")
	public ModelAndView forMe(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "taskItem");
		List<?> list = bpmService.getMyTasks(filter);
		Map<String, String> candidateMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		if (BeanUtils.isNotEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				ProcessTask task = (ProcessTask) list.get(i);
				if (i == 0) {
					sb.append(task.getId());
				} else {
					sb.append("," + task.getId());
				}
			}
			List<Map> userMapList = bpmService.getHasCandidateExecutor(sb.toString());
			for (Iterator<Map> it = userMapList.iterator(); it.hasNext();) {
				Map map = it.next();
				candidateMap.put(map.get("TASKID").toString(), "1");
			}
		}
		ModelAndView mv = getAutoView().addObject("taskList", list).addObject("candidateMap", candidateMap);
		return mv;
	}

	/**
	 * 待办事项 flex 返回格式eg: [ { "id":"10000005210157", // 项id "type":"1", // 类型，如任务、消息 "startTime":"12/07/2012 00:00:00 AM", // 开始时间 "endTime":"12/08/2012 00:00:00 AM", // 结束时间 "title":"测试流程变量-admin-2012-10-17 11:55:07", // 标题 } ]
	 * 
	 * @throws Exception
	 */
	@RequestMapping("myEvent")
	public void myEvent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> param = new HashMap<String, String>();
		param.put("mode", RequestUtil.getString(request, "mode"));
		param.put("startDate", RequestUtil.getString(request, "startDate"));
		param.put("endDate", RequestUtil.getString(request, "endDate"));
		response.getWriter().print(bpmService.getMyEvents(param));
	}

	@RequestMapping("detail")
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long taskId = RequestUtil.getLong(request, "taskId");
		Task task = taskService.createTaskQuery().taskId(taskId.toString()).singleResult();
		if (task == null) {
			return new ModelAndView("redirect:notExist.ht");
		}
		String returnUrl = RequestUtil.getPrePage(request);
		// get the process run instance from task
		ProcessRun processRun = processRunService.getByActInstanceId(new Long(task.getProcessInstanceId()));

		BpmDefinition processDefinition = bpmDefinitionService.getByActDefId(processRun.getActDefId());
		ModelAndView modelView = getAutoView();
		modelView.addObject("taskEntity", task).addObject("processRun", processRun).addObject("processDefinition", processDefinition).addObject("returnUrl", returnUrl);
		if (StringUtils.isNotEmpty(processRun.getBusinessUrl())) {
			String businessUrl = StringUtil.formatParamMsg(processRun.getBusinessUrl(), processRun.getBusinessKey()).toString();
			modelView.addObject("businessUrl", businessUrl);
		}
		return modelView;
	}

	/**
	 * 启动任务界面。 根据任务ID获取流程实例，根据流程实例获取表单数据。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("toStart")
	public ModelAndView toStart(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = this.getAutoView();
		return getToStartView(request, response, mv, 0);
	}

	/**
	 * 管理员使用的页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("doNext")
	public ModelAndView doNext(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("/platform/bpm/taskToStart.jsp");
		mv = getToStartView(request, response, mv, 1);

		return mv;
	}

	/**
	 * 流程启动页面（修改这个方法请修改手机的页面MobileTaskController.getMyTaskForm()）
	 * 
	 * @param request
	 * @param response
	 * @param mv
	 * @param isManage
	 * @return
	 * @throws Exception
	 */
	private ModelAndView getToStartView(HttpServletRequest request, HttpServletResponse response, ModelAndView mv, int isManage) throws Exception {
		String ctxPath = request.getContextPath();
		SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
		String taskId = RequestUtil.getString(request, "taskId");
		String instanceId = RequestUtil.getString(request, "instanceId");

		if (StringUtil.isEmpty(taskId) && StringUtil.isEmpty(instanceId)) {
			return ServiceUtil.getTipInfo("没有输入任务或实例ID!");
		}

		// 根据流程实例获取流程任务。
		if (StringUtil.isNotEmpty(instanceId)) {
			List<ProcessTask> list = bpmService.getTasks(instanceId);
			if (BeanUtils.isNotEmpty(list)) {
				taskId = list.get(0).getId();
			}
		}
		// 查找任务节点
		TaskEntity taskEntity = bpmService.getTask(taskId);

		if (taskEntity == null) {
			ProcessTaskHistory taskHistory = taskHistoryService.getById(Long.valueOf(taskId));
			if (taskHistory == null) {
				if (StringUtil.isEmpty(taskId) && StringUtil.isEmpty(instanceId)) {
					return ServiceUtil.getTipInfo("任务ID错误!");
				}
			}
			String actInstId = taskHistory.getProcessInstanceId();
			if (StringUtils.isEmpty(actInstId) && taskHistory.getDescription().equals(TaskOpinion.STATUS_COMMUNICATION.toString())) {
				return ServiceUtil.getTipInfo("此任务为沟通任务,并且此任务已经处理!");
			}
			ProcessRun processRun = processRunService.getByActInstanceId(new Long(actInstId));
			if (processRun == null) {// 实例数据已被删除
				return ServiceUtil.getTipInfo("任务不存在!");
			}
			String url = request.getContextPath() + "/platform/bpm/processRun/info.ht?link=1&runId=" + processRun.getRunId();
			response.sendRedirect(url);
			return null;
		}

		if (TaskOpinion.STATUS_TRANSTO_ING.toString().equals(taskEntity.getDescription()) && taskEntity.getAssignee().equals(sysUser.getUserId().toString())) {
			return ServiceUtil.getTipInfo("对不起,这个任务正在流转中,不能处理此任务!");
		}

		instanceId = taskEntity.getProcessInstanceId();

		if (isManage == 0) {
			boolean hasRights = processRunService.getHasRightsByTask(new Long(taskEntity.getId()), sysUser.getUserId());
			if (!hasRights) {
				return ServiceUtil.getTipInfo("对不起,你不是这个任务的执行人,不能处理此任务!");
			}
		}
		// 更新任务为已读。
		taskReadService.saveReadRecord(Long.parseLong(instanceId), Long.parseLong(taskId));
		// 设置沟通人员或流转人员查看状态。
		commuReceiverService.setCommuReceiverStatus(taskEntity, sysUser);

		String nodeId = taskEntity.getTaskDefinitionKey();
		String actDefId = taskEntity.getProcessDefinitionId();
		Long userId = ContextUtil.getCurrentUserId();

		BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(actDefId);
		ProcessRun processRun = processRunService.getByActInstanceId(new Long(instanceId));

		Long defId = bpmDefinition.getDefId();

		/**
		 * 使用API调用的时候获取表单的url进行跳转。
		 */
		if (bpmDefinition.getIsUseOutForm() == 1) {
			String formUrl = bpmFormDefService.getFormUrl(taskId, actDefId, nodeId, processRun.getBusinessKey(), ctxPath);
			if (StringUtils.isEmpty(formUrl)) {
				ModelAndView rtnModel = ServiceUtil.getTipInfo("请设置API调用时表单的url!");
				return rtnModel;
			}
			response.sendRedirect(formUrl);
		}

		// 通过defid和nodeId获取联动设置
		List<BpmGangedSet> bpmGangedSets = bpmGangedSetService.getByDefIdAndNodeId(defId, nodeId);
		JSONArray gangedSetJarray = (JSONArray) JSONArray.fromObject(bpmGangedSets);

		Map<String, Object> variables = taskService.getVariables(taskId);

		String parentActDefId = "";
		if (variables.containsKey(BpmConst.FLOW_PARENT_ACTDEFID)) {// 判断当前是否属于子流程任务
			parentActDefId = variables.get(BpmConst.FLOW_PARENT_ACTDEFID).toString();
		}
		BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId, parentActDefId);

		String toBackNodeId = "";
		if (StringUtil.isNotEmpty(processRun.getStartNode())) {
			toBackNodeId = processRun.getStartNode();
		} else {
			toBackNodeId = NodeCache.getFirstNodeId(actDefId).getNodeId();
		}
		String form = "";

		Long tempLaunchId = userId;
		// 在沟通和加签的时候 把当前用户对于当前表单的权限设置为传递者的权限。
		if (StringUtils.isEmpty(taskEntity.getExecutionId())) {
			if (taskEntity.getDescription().equals(TaskOpinion.STATUS_TRANSTO.toString())) {
				List<TaskOpinion> taskOpinionList = taskOpinionService.getByActInstId(instanceId);
				if (BeanUtils.isNotEmpty(taskOpinionList)) {
					TaskOpinion taskOpinion = taskOpinionList.get(taskOpinionList.size() - 1);
					List<CommuReceiver> commuReceiverList = commuReceiverService.getByOpinionId(taskOpinion.getOpinionId());
					if (BeanUtils.isNotEmpty(commuReceiverList)) {
						tempLaunchId = taskOpinion.getExeUserId();
					}
				}
			}
		}

		FormModel formModel = bpmFormDefService.getNodeForm(processRun, nodeId, tempLaunchId, ctxPath, variables, false);
		// 如果是沟通任务 那么不允许沟通者有编辑表单的权限
		if (taskEntity.getDescription().equals(TaskOpinion.STATUS_COMMUNICATION.toString())) {
			BpmFormDef bpmFormDef = null;
			if(StringUtil.isNotEmpty(bpmNodeSet.getFormKey())){
				 bpmFormDef=bpmFormDefService.getDefaultPublishedByFormKey(bpmNodeSet.getFormKey());
			}
			form = bpmFormHandlerService.obtainHtml(bpmFormDef,  processRun.getBusinessKey(), 
					instanceId, actDefId, nodeId , ctxPath, parentActDefId, false,false,true,(short)0) ;
			formModel.setFormHtml(form);
		}
		if (!formModel.isValid()) {
			ModelAndView rtnModel = ServiceUtil.getTipInfo("流程定义的流程表单发生了更改,数据无法显示!");
			return rtnModel;
		}

		String detailUrl = formModel.getDetailUrl();

		Boolean isExtForm = (Boolean) (formModel.getFormType() > 0);

		if (formModel.getFormType() == 0)
			form = formModel.getFormHtml();
		else
			form = formModel.getFormUrl();

		Boolean isEmptyForm = formModel.isFormEmpty();

		// 是否会签任务
		boolean isSignTask = bpmService.isSignTask(taskEntity);
		if (isSignTask) {
			handleSignTask(mv, instanceId, nodeId, actDefId, userId);
		}

		// 是否支持回退
		boolean isCanBack = bpmActService.isTaskAllowBack(taskId);
		//不支持回退时判断同步条件中是否可以自由回退
		boolean isCanFreeback=false;
		if (isCanBack){
			Map<String,FlowNode> preCacheMap=new HashMap<String,FlowNode>();
			Map<String,FlowNode> nextCacheMap=new HashMap<String,FlowNode>();
			FlowNode node=NodeCache.getFlowNode(taskEntity.getProcessDefinitionId(),taskEntity.getTaskDefinitionKey());
			boolean isSyncNode=NodeCache.isPreParallelGateway(preCacheMap,node)&&NodeCache.isNextParallelGateway(nextCacheMap,node);
			if (isSyncNode){
				isCanBack=false;
			}
			if (isSyncNode&&preCacheMap.size()>1){
				isCanFreeback=true;
			}
		}
		// 是否汇总节点
		boolean gatherNode = bpmActService.isGatherNode(taskId);
		// 是否转办
		boolean isCanAssignee = bpmTaskExeService.isAssigneeTask(taskEntity, bpmDefinition);

		// 是否执行隐藏路径
		boolean isHidePath = getIsHidePath(bpmNodeSet.getIsHidePath());

		// 是否是执行选择路径跳转
		boolean isHandChoolse = false;
		if (!isHidePath) {
			boolean canChoicePath = bpmService.getCanChoicePath(actDefId, taskId);
			Long startUserId = ContextUtil.getCurrentUserId();
			List<NodeTranUser> nodeTranUserList = bpmService.getNodeTaskUserMap(taskId, startUserId, canChoicePath);
			if (nodeTranUserList.size() > 1) {
				isHandChoolse = true;
			}
		}

		// 获取页面显示的按钮
		Map<String, List<BpmNodeButton>> mapButton = bpmNodeButtonService.getMapByDefNodeId(defId, nodeId);

		// 取常用语
		List<String> taskAppItems = taskAppItemService.getApprovalByDefKeyAndTypeId(bpmDefinition.getDefKey(), bpmDefinition.getTypeId());
		// 获取保存的意见
		TaskOpinion taskOpinion = taskOpinionService.getOpinionByTaskId(Long.parseLong(taskId), userId);

		// 帮助文档
		SysFile sysFile = null;
		if (BeanUtils.isNotEmpty(bpmDefinition.getAttachment())) {
			sysFile = sysFileService.getById(bpmDefinition.getAttachment());
		}

		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
		
		// 消息类型
		Map<String, IMessageHandler> handlersMap = this.getMessageType(bpmDefinition);

		return mv.addObject("task", taskEntity)
				 .addObject("taskId", taskId)
				 .addObject("bpmNodeSet", bpmNodeSet)
				 .addObject("processRun", processRun)
				 .addObject("bpmDefinition", bpmDefinition)
				 .addObject("isSignTask", isSignTask)
				 .addObject("isCanBack", isCanBack)
				 .addObject("isCanFreeback", isCanFreeback)
				 .addObject("gatherNode",gatherNode)
				 .addObject("isCanAssignee", isCanAssignee)
				 .addObject("isHidePath", isHidePath)
				 .addObject("toBackNodeId", toBackNodeId)
				 .addObject("form", form)
				 .addObject("isExtForm", isExtForm)
				 .addObject("isEmptyForm", isEmptyForm)
				 .addObject("taskAppItems", taskAppItems)
				 .addObject("mapButton", mapButton)
				 .addObject("detailUrl", detailUrl)
				 .addObject("isManage", isManage)
				 .addObject("bpmGangedSets", gangedSetJarray)
				 .addObject("sysFile", sysFile)
				 .addObject("taskOpinion", taskOpinion)
				 .addObject("isHandChoolse", isHandChoolse)
				 .addObject("curUserId", sysUser.getUserId().toString())
//				 .addObject("curUserName", sysUser.getFullname())
				.addObject("curUserName", ContextSupportUtil.getUsername(sysUser))
				 .addObject("hasGlobalFlowNo", hasGlobalFlowNo)
				 .addObject("formKey", bpmNodeSet.getFormKey())
				 .addObject("handlersMap", handlersMap);
	}

	/**
	 * 产生的沟通意见任务，并发送到沟通人
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("toStartCommunication")
	public void toStartCommunication(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType(HttpConstant.CONTENT_TYPE_JSON);
		PrintWriter out = response.getWriter();
		ResultMessage resultMessage = null;
		String cmpIds = request.getParameter("cmpIds");
		if (StringUtil.isEmpty(cmpIds)) {
			resultMessage = new ResultMessage(ResultMessage.Fail, "请输入通知人!");
			out.print(resultMessage);
			return;
		}
		try {
			String taskId = request.getParameter("taskId");
			String opinion = request.getParameter("opinion");
			String informType = RequestUtil.getString(request, "informType");
			// 保存意见
			TaskEntity taskEntity = bpmService.getTask(taskId);
			ProcessRun processRun = processRunService.getByActInstanceId(new Long(taskEntity.getProcessInstanceId()));

			processRunService.saveCommuniCation(taskEntity, opinion, informType, cmpIds, processRun);
			ProcessCmd taskCmd = BpmUtil.getProcessCmd(request);
			processRunService.handlerFormData(taskCmd, processRun, taskEntity.getTaskDefinitionKey());

			Long runId = processRun.getRunId();

			String memo = "在:【" + processRun.getSubject() + "】,节点【" + taskEntity.getName() + "】,意见:" + opinion;
			bpmRunLogService.addRunLog(runId, BpmRunLog.OPERATOR_TYPE_ADDOPINION, memo);

			resultMessage = new ResultMessage(ResultMessage.Success, "沟通成功!");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			resultMessage = new ResultMessage(ResultMessage.Fail, "沟通失败!");
		}
		out.print(resultMessage);
	}

	/**
	 * 产生的流转任务，并发送到流转人
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("toStartTransTo")
	public void toStartTransTo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType(HttpConstant.CONTENT_TYPE_JSON);
		PrintWriter out = response.getWriter();
		ResultMessage resultMessage = null;
		String cmpIds = request.getParameter("cmpIds");
		if (StringUtil.isEmpty(cmpIds)) {
			resultMessage = new ResultMessage(ResultMessage.Fail, "请输入通知人!");
			out.print(resultMessage);
			return;
		}
		try {
			String taskId = request.getParameter("taskId");
			String opinion = request.getParameter("opinion");
			String informType = RequestUtil.getString(request, "informType");
			String transType = request.getParameter("transType");
			String action = request.getParameter("action");
			// 保存意见
			TaskEntity taskEntity = bpmService.getTask(taskId);
			ProcessRun processRun = processRunService.getByActInstanceId(new Long(taskEntity.getProcessInstanceId()));

			processRunService.saveTransTo(taskEntity, opinion, informType, cmpIds, transType, action, processRun);

			// 同时保存表单数据。
			ProcessCmd taskCmd = BpmUtil.getProcessCmd(request);
			taskCmd.setVoteContent(opinion);
			processRunService.handlerFormData(taskCmd, processRun, taskEntity.getTaskDefinitionKey());

			Long runId = processRun.getRunId();

			String memo = "在:【" + processRun.getSubject() + "】,节点【" + taskEntity.getName() + "】,意见:" + opinion;
			bpmRunLogService.addRunLog(runId, BpmRunLog.OPERATOR_TYPE_ADDOPINION, memo);

			resultMessage = new ResultMessage(ResultMessage.Success, "成功完成了该任务!");
		} catch (Exception e) {
			resultMessage = new ResultMessage(ResultMessage.Fail, "完成任务失败!");
			e.printStackTrace();
		}
		//out.print(resultMessage);
		com.suneee.eas.common.utils.HttpUtil.writeResponseJson(response,ResponseMessage.success(resultMessage.getMessage()));
	}

	/**
	 * 是否执行隐藏路径
	 * 
	 * @param isHidePath
	 * @return
	 */
	private boolean getIsHidePath(Short isHidePath) {
		if (BeanUtils.isEmpty(isHidePath))
			return false;
		if (BpmNodeSet.HIDE_PATH.shortValue() == isHidePath.shortValue())
			return true;
		return false;
	}

	/**
	 * 处理会签
	 * 
	 * @param mv
	 * @param instanceId
	 * @param nodeId
	 * @param actDefId
	 * @param userId
	 *            当前用户
	 */
	private void handleSignTask(ModelAndView mv, String instanceId, String nodeId, String actDefId, Long userId) {

		List<TaskSignData> signDataList = taskSignDataService.getByNodeAndInstanceId(instanceId, nodeId);
		// 获取会签规则
		BpmNodeSign bpmNodeSign = bpmNodeSignService.getByDefIdAndNodeId(actDefId, nodeId);

		mv.addObject("signDataList", signDataList);
		mv.addObject("bpmNodeSign", bpmNodeSign);
		mv.addObject("curUser", ContextUtil.getCurrentUser());
		// 获取当前组织
		Long orgId = ContextUtil.getCurrentOrgId();

		// "允许直接处理"特权
		boolean isAllowDirectExecute = bpmNodeSignService.checkNodeSignPrivilege(actDefId, nodeId, BpmNodePrivilegeType.ALLOW_DIRECT, userId, orgId);
		// "允许补签"特权
		boolean isAllowRetoactive = bpmNodeSignService.checkNodeSignPrivilege(actDefId, nodeId, BpmNodePrivilegeType.ALLOW_RETROACTIVE, userId, orgId);
		// "一票决断"特权
		boolean isAllowOneVote = bpmNodeSignService.checkNodeSignPrivilege(actDefId, nodeId, BpmNodePrivilegeType.ALLOW_ONE_VOTE, userId, orgId);
		mv.addObject("isAllowDirectExecute", isAllowDirectExecute).addObject("isAllowRetoactive", isAllowRetoactive).addObject("isAllowOneVote", isAllowOneVote);

	}

	@RequestMapping("getForm")
	public ModelAndView getForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String ctxPath = request.getContextPath();
		String taskId = RequestUtil.getString(request, "taskId");
		String returnUrl = RequestUtil.getPrePage(request);
		// 查找任务节点
		TaskEntity taskEntity = bpmService.getTask(taskId);
		String action = RequestUtil.getString(request, "action", "");
		if (taskEntity == null) {
			return new ModelAndView("redirect:notExist.ht");
		}
		String instanceId = taskEntity.getProcessInstanceId();
		String taskName = taskEntity.getTaskDefinitionKey();
		String actDefId = taskEntity.getProcessDefinitionId();
		Long userId = ContextUtil.getCurrentUserId();

		BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(actDefId);

		ProcessRun processRun = processRunService.getByActInstanceId(new Long(instanceId));

		String form = "";
		Boolean isExtForm = false;
		Boolean isEmptyForm = false;

		Map<String, Object> variables = taskService.getVariables(taskId);

		if (bpmDefinition != null) {
			FormModel formModel = bpmFormDefService.getNodeForm(processRun, taskName, userId, ctxPath, variables, true);

			isExtForm = formModel.getFormType() > 0;
			if (formModel.getFormType() == 0) { // 在线表单
				form = formModel.getFormHtml();
			} else if (formModel.getFormType() == 1) { // url表单
				form = formModel.getFormUrl();
			} else if (formModel.getFormType() == 2) { // 有明细url
				form = formModel.getDetailUrl();
			}

			isEmptyForm = formModel.isFormEmpty();
		}

		return getAutoView().addObject("task", taskEntity).addObject("form", form).addObject("bpmDefinition", bpmDefinition).addObject("isExtForm", isExtForm).addObject("isEmptyForm", isEmptyForm).addObject("action", action).addObject("processRun", processRun).addObject("returnUrl", returnUrl);
	}

	/**
	 * 完成任务
	 * 
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("complete")
	@ResponseBody
	public ResultVo complete(HttpServletRequest request) throws Exception {
		logger.debug("任务完成跳转....");
		int fromMobile = RequestUtil.getInt(request,"fromMobile");
		SysUser curUser = (SysUser) ContextUtil.getCurrentUser();
		ResultVo resultVo =new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		String taskId = RequestUtil.getString(request, "taskId");
		TaskEntity task = bpmService.getTask(taskId);
		if (task == null) {
			resultVo.setMessage("此任务已经执行了!");
			return resultVo;
		}
		String actDefId = task.getProcessDefinitionId();
		BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(actDefId);
		if (BpmDefinition.STATUS_INST_DISABLED.equals(bpmDefinition.getStatus())) {
			resultVo.setMessage("流程实例已经禁止，该任务不能办理！");
			return resultVo;
		}
		Long userId = curUser.getUserId();

		ProcessCmd taskCmd = BpmUtil.getProcessCmd(request);


		//测试用，验证是否可以自由退回
//		taskCmd.setStartNode("UserTask4");
//		taskCmd.setBack(3);
//		System.out.println(taskCmd.getStartNode()+"..................."+taskCmd.isBack()+"..........."+taskCmd.isRecover());


		taskCmd.setCurrentUserId(userId.toString());

		String assignee = task.getAssignee();
		// 非管理员,并且没有任务的权限。
		boolean isAdmin = taskCmd.getIsManage().shortValue() == 1;
		if (!isAdmin) {
			boolean rtn = processRunService.getHasRightsByTask(new Long(taskId), userId);
			if (!rtn) {
				resultVo.setMessage("对不起,你不是这个任务的执行人,不能处理此任务!");
				return resultVo;
			}
		}

		// 记录日志。
		logger.info(taskCmd.toString());
		if (ServiceUtil.isAssigneeNotEmpty(assignee) && !task.getAssignee().equals(userId.toString()) && !isAdmin) {
			resultVo.setMessage("该任务已被其他人锁定!");
		} else {
			String errorUrl = RequestUtil.getErrorUrl(request);
			String ip = RequestUtil.getIpAddr(request);
			try {
				processRunService.nextProcess(taskCmd);
				//需要删除的附件id
				Long[] delFileIds = RequestUtil.getLongAryByStr(request, "delFileIds");
				//删除附件
				if (null != delFileIds && delFileIds.length > 0) {
					sysFileService.delSysFileByIds(delFileIds);
				}
				List<TaskOpinion> lists = taskOpinionService.getByRunId(taskCmd.getProcessRun().getRunId());
				List<TaskOpinion> newlist = new ArrayList<>();
				for (TaskOpinion taskopin:lists
					 ) {
					if(taskopin.getEndTime()==null){
						newlist.add(taskopin);
					}
				}
		        String str = "";
				if(newlist.size()==1) {
					if(task.getName().equals(newlist.get(0).getTaskName())){
						str="等待相同节点其他审批人审批";
						if(fromMobile==1){
							resultVo.setMessage(str);
						}else {
							resultVo.setMessage("<p style='color:red'>" + str + "</p>");
						}
					}else {
						str = newlist.get(0).getTaskName();
						if(fromMobile==1){
							resultVo.setMessage("成功流转至" + str + "节点");
						}else {
							resultVo.setMessage("成功流转至<p style='color:red'>" + str + "</p>节点");
						}
					}
					resultVo.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
				}else if(newlist.size()>1){
					boolean hasdif = hasDifNode(newlist);
					if(hasdif) {
						str = "流转至多任务节点审批";
						if(fromMobile==1) {
							resultVo.setMessage(str);
						}else{
							resultVo.setMessage("<p style='color:red'>" + str + "</p>");
							}
					}else{
						str="流转至多人审批";
						if(fromMobile==1) {
							resultVo.setMessage(str);
						}else{
							resultVo.setMessage("<p style='color:red'>"+str+"</p>");
						}
					}
					resultVo.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
				}else{
					str="审批成功，流程结束。";
					resultVo.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
					resultVo.setMessage(str);
				}
			} catch (ActivitiVarNotFoundException ex) {
				resultVo.setMessage("请检查变量是否存在:"+ ex.getMessage());
				// 添加错误消息到日志
				sysErrorLogService.addError(curUser.getAccount(), ip, ex.getMessage(), errorUrl);
			} catch (ActivitiInclusiveGateWayException ex) {
				resultVo.setMessage(ex.getMessage());
				// 添加错误消息到日志
				sysErrorLogService.addError(curUser.getAccount(), ip, ex.getMessage(), errorUrl);
			} catch (Exception ex) {
				ex.printStackTrace();
				String str = MessageUtil.getMessage();
				if (StringUtil.isNotEmpty(str)) {
					//兼容手机版没有选择人员时，返回给用户的提示语改为“请选择签批人”
					if(RequestUtil.getLong(request,"fromMobile")==1&&str.contains(",没有设置执行人")){
						str="请选择签批人";
					}
					resultVo.setMessage(str);
					// 添加错误消息到日志
					sysErrorLogService.addError(curUser.getAccount(), ip, str, errorUrl);
				} else {
					String message = ExceptionUtil.getExceptionMessage(ex);
					resultVo.setMessage(message);
					// 添加错误消息到日志
					sysErrorLogService.addError(curUser.getAccount(), ip, message, errorUrl);
				}
			}
		}
		return resultVo;
	}

	public boolean hasDifNode(List<TaskOpinion> list)throws Exception{
		if(list.size()==0||list==null){
			return false;
		}
		String nodeName = list.get(0).getTaskName();
		for(int i=1;i<=list.size()-1;i++){
			if(!list.get(i).getTaskName().equals(nodeName)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 锁定任务
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("claim")
	@Action(description = "锁定任务")
	public void claim(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String taskId = RequestUtil.getString(request, "taskId");
		// int isAgent = RequestUtil.getInt(request, "isAgent");
		String preUrl = RequestUtil.getPrePage(request);
		String assignee = ContextUtil.getCurrentUserId().toString();
		// 代理任务，则设置锁定的assignee为代理人

		try {
			TaskEntity taskEntity = bpmService.getTask(taskId);
			ProcessRun processRun = processRunService.getByActInstanceId(new Long(taskEntity.getProcessInstanceId()));
			Long runId = processRun.getRunId();
			taskService.claim(taskId, assignee);
			String memo = "流程:" + processRun.getSubject() + ",锁定任务，节点【" + taskEntity.getName() + "】";
			bpmRunLogService.addRunLog(runId, BpmRunLog.OPERATOR_TYPE_LOCK, memo);
			saveSuccessResultMessage(request.getSession(), "成功锁定任务!");
		} catch (Exception ex) {
			saveSuccessResultMessage(request.getSession(), "任务已经完成或被其他用户锁定!");
		}
		response.sendRedirect(preUrl);
	}

	/**
	 * 解锁任务
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("unlock")
	@Action(description = "解锁任务")
	public ModelAndView unlock(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String taskId = request.getParameter("taskId");

		if (StringUtils.isNotEmpty(taskId)) {
			TaskEntity taskEntity = bpmService.getTask(taskId);
			ProcessRun processRun = processRunService.getByActInstanceId(new Long(taskEntity.getProcessInstanceId()));
			Long runId = processRun.getRunId();
			bpmService.updateTaskAssigneeNull(taskId);
			String memo = "流程:" + processRun.getSubject() + ",解锁任务，节点【" + taskEntity.getName() + "】";
			bpmRunLogService.addRunLog(runId, BpmRunLog.OPERATOR_TYPE_UNLOCK, memo);
			saveSuccessResultMessage(request.getSession(), "任务已经成功解锁!");
		}
		return new ModelAndView("redirect:forMe.ht");
	}

	/**
	 * 任务跳转窗口显示
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("freeJump")
	public ModelAndView freeJump(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String taskId = RequestUtil.getString(request, "taskId");
		// 获取当前节点的选择器限定配置
		ExecutionEntity execution = bpmService.getExecutionByTaskId(taskId);
		String superExecutionId = execution.getSuperExecutionId();
		String parentActDefId = "";
		if (StringUtil.isNotEmpty(superExecutionId)) {
			ExecutionEntity supExecution = bpmService.getExecution(superExecutionId);
			parentActDefId = supExecution.getProcessDefinitionId();
		}
		String nodeId = execution.getActivityId();
		String processDefinitionId = execution.getProcessDefinitionId();
		BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(processDefinitionId, nodeId, parentActDefId);
		String scope = bpmNodeSet.getScope();

		Map<String, Map<String, String>> jumpNodesMap = bpmService.getJumpNodes(taskId);
		ModelAndView view = this.getAutoView();
		view.addObject("jumpNodeMap", jumpNodesMap).addObject("scope", scope);
		return view;
	}


	/**
	 * 自由退回窗口显示
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("freeBack")
	public ModelAndView freeBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String isRequired = RequestUtil.getString(request, "isRequired");
		String actDefId = RequestUtil.getString(request, "actDefId");
		String taskId = RequestUtil.getString(request, "taskId");
		String reject = RequestUtil.getString(request, "reject", "0");
		BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(actDefId);
		List<ForkTaskReject> forkTaskExecutor = bpmActService.forkTaskExecutor(taskId);
		// 获取常用语
		List<String> taskAppItems = taskAppItemService.getApprovalByDefKeyAndTypeId(bpmDefinition.getDefKey(), bpmDefinition.getTypeId());
		ModelAndView view = this.getAutoView();
		view.addObject("isRequired", isRequired).addObject("reject",reject).addObject("taskAppItems", taskAppItems).addObject("forkTaskExecutor", forkTaskExecutor);

		List<ExecutionStack> stackListData = getFreebackStacks(taskId);
		view.addObject("stackList",stackListData);
		return view;
	}

	/**
	 * 获取可以驳回节点
	 * @param taskId
	 * @return
	 */
	private List<ExecutionStack> getFreebackStacks(String taskId) {
		//获取可以自由退回节点列表
		TaskEntity task = bpmService.getTask(taskId);
		List<ExecutionStack> stackList=executionStackService.getByActInstId(Long.valueOf(task.getProcessInstanceId()));
		List<ExecutionStack> stackListData=new CopyOnWriteArrayList<ExecutionStack>();
		List<String> containTaskKey=new ArrayList<String>();
		for (ExecutionStack stackItem:stackList){
			//如果任务栈与当前任务相同，则不加入自由退回列表中
			if (task.getTaskDefinitionKey().equals(stackItem.getNodeId())){
				continue;
			}
			if (!containTaskKey.contains(stackItem.getNodeId())){
				containTaskKey.add(stackItem.getNodeId());
				stackListData.add(stackItem);
			}
		}
		//获取审批历史,存在审批历史中则可以退回
		List<TaskOpinion> opinionList = taskOpinionService.getByActInstId(task.getProcessInstanceId());

		FlowNode node=NodeCache.getFlowNode(task.getProcessDefinitionId(),task.getTaskDefinitionKey());
		Map<String,FlowNode> preCacheMap=new HashMap<String, FlowNode>();
		Map<String,FlowNode> nextCacheMap=new HashMap<String, FlowNode>();
		boolean isSyncNode=NodeCache.isPreParallelGateway(preCacheMap,node)&&NodeCache.isNextParallelGateway(nextCacheMap,node);

		if (isSyncNode){
			//同步节点时，获取同步条件中可以驳回的流程
			for (ExecutionStack stackTemp:stackListData){
				boolean isFind=false;
				if (preCacheMap.get(stackTemp.getNodeId())!=null){
					isFind=true;
				}
				if (!isFind){
					stackListData.remove(stackTemp);
				}
			}
		}else {
			for (ExecutionStack stackTemp:stackListData){
				boolean isFind=false;
				for (TaskOpinion opinion:opinionList){
					if (stackTemp.getNodeId().equals(opinion.getTaskKey())&&!NodeCache.isSyncNode(opinion.getActDefId(),opinion.getTaskKey())){
						isFind=true;
						break;
					}
				}
				if (!isFind){
					stackListData.remove(stackTemp);
				}
			}
		}

		if (stackListData.size()>1){
			Collections.sort(stackListData, new Comparator<ExecutionStack>() {
				@Override
				public int compare(ExecutionStack stack1, ExecutionStack stack2) {
					if (stack1.getDepth()<stack2.getDepth()){
						return 1;
					}else if (stack1.getDepth()>stack2.getDepth()){
						return -1;
					}else {
						return 0;
					}
				}
			});
		}
		return stackListData;
	}


	/**
	 * 验证是否为会签节点
	 * @param stack
	 * @return
	 */
	private boolean isSignNode(ExecutionStack stack){
		return bpmService.isSignTask(stack.getActDefId(),stack.getNodeId());
	}


	/**
	 * 动态创建任务加载显示页
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("dynamicCreate")
	public ModelAndView dynamicCreate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String taskId = request.getParameter("taskId");
		TaskEntity task = bpmService.getTask(taskId);
		return this.getAutoView().addObject("task", task);
	}

	/**
	 * 获取某个流程实例上某个节点的配置执行人员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getTaskUsers")
	@ResponseBody
	public List<TaskExecutor> getTaskUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 任务Id
		String taskId = request.getParameter("taskId");

		TaskEntity taskEntity = bpmService.getTask(taskId);

		String nodeId = RequestUtil.getString(request, "nodeId"); // 所选择的节点Id

		if (StringUtil.isEmpty(nodeId)) {
			nodeId = taskEntity.getTaskDefinitionKey(); // 目标节点Id
		}

		String actDefId = taskEntity.getProcessDefinitionId();

		String actInstId = taskEntity.getProcessInstanceId();

		Map<String, Object> vars = runtimeService.getVariables(taskEntity.getExecutionId());

		String startUserId = vars.get(BpmConst.StartUser).toString();

		@SuppressWarnings("unchecked")
		List<TaskExecutor> taskExecutorList = bpmNodeUserService.getExeUserIds(actDefId, actInstId, nodeId, startUserId, "", vars);
		//根据runId获取审批历史，并把相同节点的审批人做处理
		Long runId =  Long.parseLong(request.getParameter("runId"));
		ProcessRun processRun = processRunService.getById(runId);
		//取得关联的流程实例ID
		List<TaskOpinion> list = taskOpinionService.getByRunId(runId);
		//设置代码执行人
		list = taskOpinionService.setTaskOpinionExecutor(list);
		//去除审批历史多余数据
		list=bpmService.setNewOpinionList(list);

		List<TaskExecutor> newList = new ArrayList<TaskExecutor>();
		for(TaskOpinion opinion:list){
			if(nodeId.equals(opinion.getTaskKey())){
				TaskExecutor executor = new TaskExecutor();
				SysUser user = sysUserService.getById(opinion.getExeUserId());
				executor.setType("user");
//				executor.setExecutor(user.getFullname());
				executor.setExecutor(ContextSupportUtil.getUsername(user));
				executor.setExecuteId(user.getUserId().toString());
				executor.setExactType(0);
				newList.add(executor);
			}
		}
		if(newList.size()>0){
			return newList;
		}else{
			return taskExecutorList;
		}
	}

	/**
	 * 指派任务所属人
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@Action(description = "任务指派所属人", detail = "<#if StringUtils.isNotEmpty(taskIds)>" + "流程" + "<#list StringUtils.split(taskIds,\",\") as item>" + "【${SysAuditLinkService.getProcessRunLink(item)}】" + "</#list>" + "的任务指派给【${SysAuditLinkService.getSysUserLink(Long.valueOf(userId))}】" + "</#if>")
	@RequestMapping("assign")
	public ModelAndView assign(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String taskIds = request.getParameter("taskIds");
		String userId = request.getParameter("userId");

		if (StringUtils.isNotEmpty(taskIds)) {
			String[] tIds = taskIds.split("[,]");
			if (tIds != null) {
				for (String tId : tIds) {
					bpmService.assignTask(tId, userId);
				}
			}
		}
		saveSuccessResultMessage(request.getSession(), "成功为指定任务任务分配执行人员!");
		return new ModelAndView("redirect:list.ht");
	}

	/**
	 * 任务交办设置任务的执行人。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("delegate")
	@Action(description = "任务交办", detail = "<#if StringUtils.isNotEmpty(taskId) && StringUtil.isNotEmpty(userId)>" + "交办流程【${SysAuditLinkService.getProcessRunLink(taskId)}】的任务【${taskName}】" + "给用户【${SysAuditLinkService.getSysUserLink(Long.valueOf(userId))}】" + "</#if>")
	public void delegate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter writer = response.getWriter();
		String taskId = request.getParameter("taskId");
		String userId = request.getParameter("userId");
		// String delegateDesc = request.getParameter("memo");
		ResultMessage message = null;
		// TODO ZYG 任务交办
		if (StringUtils.isNotEmpty(taskId) && StringUtil.isNotEmpty(userId)) {
			SysAuditThreadLocalHolder.putParamerter("taskName", bpmService.getTask(taskId).getName());
			// processRunService.delegate(taskId, userId, delegateDesc);
			message = new ResultMessage(ResultMessage.Success, "任务交办成功!");
		} else {
			message = new ResultMessage(ResultMessage.Fail, "没有传入必要的参数");
		}
		writer.print(message);
	}

	@RequestMapping("changeAssignee")
	@Action(description = "更改任务执行人")
	public ModelAndView changeAssignee(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String taskId = request.getParameter("taskId");
		TaskEntity taskEntity = bpmService.getTask(taskId);
		SysUser curUser = (SysUser) ContextUtil.getCurrentUser();
		Map<String, IMessageHandler> handlersMap = ServiceUtil.getHandlerMap();

		return getAutoView().addObject("taskEntity", taskEntity).addObject("curUser", curUser).addObject("handlersMap", handlersMap);
	}

	@RequestMapping("setAssignee")
	@Action(description = "任务指派")
	public void setAssignee(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter writer = response.getWriter();
		String taskId = request.getParameter("taskId");
		String userId = request.getParameter("userId");

		String voteContent = RequestUtil.getString(request, "voteContent");
		String informType = RequestUtil.getString(request, "informType");
		ResultMessage message = null;
		try {
			message = processRunService.updateTaskAssignee(taskId, userId, voteContent, informType);
		} catch (Exception e) {
			message = new ResultMessage(ResultMessage.Fail, e.getMessage());
		}
		writer.print(message);
	}

	/**
	 * 设置任务的执行人
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("setDueDate")
	@Action(description = "设置任务到期时间")
	public ModelAndView setDueDate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String taskIds = request.getParameter("taskIds");
		String dueDates = request.getParameter("dueDates");
		if (StringUtils.isNotEmpty(taskIds) && StringUtils.isNotEmpty(dueDates)) {
			String[] tIds = taskIds.split("[,]");
			String[] dates = dueDates.split("[,]");
			if (tIds != null) {
				for (int i = 0; i < dates.length; i++) {
					if (StringUtils.isNotEmpty(dates[i])) {
						Date dueDate = DateUtils.parseDate(dates[i], new String[] { "yyyy-MM-dd HH:mm:ss" });
						bpmService.setDueDate(tIds[i], dueDate);
					}
				}
			}
		}
		return new ModelAndView("redirect:list.ht");
	}

	/**
	 * 删除任务
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("delete")
	@Action(description = "删除任务", execOrder = ActionExecOrder.BEFORE, detail = "<#if StringUtils.isNotEmpty(taskId)>" + "<#assign entity1=bpmService.getTask(taskId)/>" + "用户删除了任务【${entity1.name}】,该任务属于流程【${SysAuditLinkService.getProcessRunLink(taskId)}】" + "</#elseif StringUtils.isNotEmpty(id)>" + "<#list StringUtils.split(id,\",\") as item>" + "<#assign entity2=bpmService.getTask(item)/>" + "用户删除了任务【${entity2.name}】,该任务属于流程【${SysAuditLinkService.getProcessRunLink(item)}】" + "</#list>" + "</#if>")
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultMessage message = null;
		try {
			String taskId = request.getParameter("taskId");
			String[] taskIds = request.getParameterValues("id");
			if (StringUtils.isNotEmpty(taskId)) {
				bpmService.deleteTask(taskId);

				TaskEntity task = bpmService.getTask(taskId);
				ProcessRun processRun = processRunService.getByActInstanceId(new Long(task.getProcessInstanceId()));
				String memo = "用户删除了任务[" + task.getName() + "],该任务属于[" + processRun.getProcessName() + "]流程。";
				bpmRunLogService.addRunLog(processRun.getRunId(), BpmRunLog.OPERATOR_TYPE_DELETETASK, memo);
				taskService.deleteTask(taskId);

			} else if (taskIds != null && taskIds.length != 0) {
				bpmService.deleteTasks(taskIds);
				for (int i = 0; i < taskIds.length; i++) {
					String id = taskIds[i];
					TaskEntity task = bpmService.getTask(id);
					ProcessRun processRun = processRunService.getByActInstanceId(new Long(task.getProcessInstanceId()));
					String memo = "用户删除了任务[" + task.getName() + "],该任务属于[" + processRun.getProcessName() + "]流程。";
					bpmRunLogService.addRunLog(processRun.getRunId(), BpmRunLog.OPERATOR_TYPE_DELETETASK, memo);
					taskService.deleteTask(id);
				}
			}
			message = new ResultMessage(ResultMessage.Success, "删除任务成功");
		} catch (Exception e) {
			message = new ResultMessage(ResultMessage.Fail, "删除任务失败");
		}
		addMessage(message, request);
		return new ModelAndView("redirect:list.ht");
	}

	/**
	 * 返回某个某个用户代理给当前用户的任务列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	// @RequestMapping("forAgent")
	// public ModelAndView forAgent(HttpServletRequest request,
	// HttpServletResponse response) throws Exception {
	// ModelAndView mv = getAutoView();
	// Long userId = RequestUtil.getLong(request, "userId");
	// QueryFilter filter = new QueryFilter(request, "taskItem");
	// Calendar cal = Calendar.getInstance();
	// Date curTime = cal.getTime();
	// cal.add(Calendar.DATE, -1);
	// Date yesterday = cal.getTime();
	//
	// filter.addFilter("curTime", curTime);
	// filter.addFilter("yesterday", yesterday);
	// List<TaskEntity> list = null;
	// SysUserAgent sysUserAgent = null;
	// // 具体人员的代理
	// if (userId != 0) {
	// sysUserAgent = sysUserAgentService.getById(userId);
	// }
	// if (sysUserAgent != null) {
	//
	// // 代理设置是否过期
	// if (sysUserAgent.getStarttime() != null) {
	// int result = sysUserAgent.getStarttime().compareTo(curTime);
	// if (result > 0) {
	// list = new ArrayList<TaskEntity>();
	// mv = getAutoView().addObject("taskList", list).addObject(
	// "userId", userId);
	// return mv;
	// }
	// }
	// if (sysUserAgent.getEndtime() != null) {
	// cal.add(Calendar.DATE, -1);
	// int result = sysUserAgent.getEndtime().compareTo(yesterday);
	// if (result <= 0) {
	// list = new ArrayList<TaskEntity>();
	// mv = getAutoView().addObject("taskList", list).addObject(
	// "userId", userId);
	// return mv;
	// }
	// }
	// if (sysUserAgent.getIsall().intValue() == SysUserAgent.IS_ALL_FLAG) {// 全部代理
	// list = bpmService.getTaskByUserId(
	// sysUserAgent.getAgentuserid(), filter);
	// } else {// 部分代理
	// StringBuffer actDefId = new StringBuffer("");
	// List<String> notInBpmAgentlist = bpmAgentService
	// .getNotInByAgentId(sysUserAgent.getAgentid());
	// for (String ba : notInBpmAgentlist) {
	// actDefId.append("'").append(ba).append("',");
	// }
	// if (notInBpmAgentlist.size() > 0) {
	// actDefId.deleteCharAt(actDefId.length() - 1);
	// }
	// list = bpmService.getAgentTasks(sysUserAgent.getAgentuserid(),
	// actDefId.toString(), filter);
	// }
	// } else {
	// list = bpmService.getAllAgentTask(ContextUtil.getCurrentUserId(),
	// filter);
	// }
	// mv = getAutoView().addObject("taskList", list).addObject("userId",
	// userId);
	//
	// return mv;
	// }

	/**
	 * 返回目标节点及其节点的处理人员映射列表。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("tranTaskUserMap")
	public ModelAndView tranTaskUserMap(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int isStart = RequestUtil.getInt(request, "isStart", 0);
		String taskId = request.getParameter("taskId");
		String actDefId = request.getParameter("actDefId");

		Long defId=null;
		BpmDefinition bpmDefinition=null;

		String scope = "";
		if (StringUtil.isEmpty(taskId)) {
			List<FlowNode> firstNode = NodeCache.getFirstNode(actDefId);
			bpmDefinition=bpmDefinitionService.getByActDefId(actDefId);
			if (BeanUtils.isNotEmpty(firstNode)) {
				FlowNode flowNode = firstNode.get(0);
				String nodeId = flowNode.getNodeId();
				BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId, "");
				defId=bpmNodeSet.getDefId();
				if (BeanUtils.isNotEmpty(bpmNodeSet))
					scope = bpmNodeSet.getScope();
			}
		} else {
			// 获取当前节点的选择器限定配置
			ExecutionEntity execution = null;
			TaskEntity taskEntity = bpmService.getTask(taskId);
			if (taskEntity.getDescription().equals(TaskOpinion.STATUS_TRANSTO.toString())) {// 加签任务
				execution = bpmService.getExecutionByTaskId(taskEntity.getParentTaskId());// 获取它的parentTaskId，这里是放着的加签任务生成的源任务
			} else {
				// 获取当前节点的选择器限定配置
				execution = bpmService.getExecutionByTaskId(taskId);
			}

			bpmDefinition=bpmDefinitionService.getByActDefId(execution.getProcessDefinitionId());
			String superExecutionId = execution.getSuperExecutionId();
			String parentActDefId = "";
			if (StringUtil.isNotEmpty(superExecutionId)) {
				ExecutionEntity supExecution = bpmService.getExecution(superExecutionId);
				parentActDefId = supExecution.getProcessDefinitionId();
			}
			String nodeId = execution.getActivityId();
			String processDefinitionId = execution.getProcessDefinitionId();
			BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(processDefinitionId, nodeId, parentActDefId);
			if (BeanUtils.isNotEmpty(bpmNodeSet))
				scope = bpmNodeSet.getScope();
		}
		if (bpmDefinition!=null){
			defId=bpmDefinition.getDefId();
		}
		int selectPath = RequestUtil.getInt(request, "selectPath", 1);

		boolean canChoicePath = bpmService.getCanChoicePath(actDefId, taskId);

		Long startUserId = ContextUtil.getCurrentUserId();
		List<NodeTranUser> nodeTranUserList = null;
		if (isStart == 1) {
			Map<String, Object> vars = new HashMap<String, Object>();
			nodeTranUserList = bpmService.getStartNodeUserMap(actDefId, startUserId, vars);
		} else {
			nodeTranUserList = bpmService.getNodeTaskUserMap(taskId, startUserId, canChoicePath);
			Long runId =  Long.parseLong(request.getParameter("runId"));
			//取得关联的流程实例ID
			List<TaskOpinion> list = taskOpinionService.getByRunId(runId);
			//设置代码执行人
			list = taskOpinionService.setTaskOpinionExecutor(list);
			//处理审批历史中相同的审批数据
			list=bpmService.setNewOpinionList(list);

			//把已审批节点的人员信息放入要选择的节点中
			nodeTranUserList=bpmService.getTaskerByStack(nodeTranUserList,list);
		}
		nodeSetSort(defId,nodeTranUserList);
		return getAutoView().addObject("nodeTranUserList", nodeTranUserList).addObject("selectPath", selectPath).addObject("scope", scope).addObject("canChoicePath", canChoicePath);
	}

    /**
     * 执行路径节点排序
     * @param defId
     * @param nodeTranUserList
     */
	private void nodeSetSort(Long defId,List<NodeTranUser> nodeTranUserList){
	    if (defId==null||nodeTranUserList==null||nodeTranUserList.size()<2){
	        return;
        }
        List<BpmNodeSet> nodeSetList = bpmNodeSetService.getByDefId(defId);
        for (NodeTranUser nodeItem:nodeTranUserList){
            for (BpmNodeSet nodeSet:nodeSetList){
                if (nodeSet.getNodeId().equals(nodeItem.getNodeId())){
                    nodeItem.setSort(nodeSet.getNodeOrder());
                    if(nodeItem.getNodeName().contains("）")) {
						int index =nodeItem.getNodeName().indexOf("）");
						String nodeName = nodeItem.getNodeName().substring(index+1);
						nodeItem.setNodeName(nodeName);
					}else if(nodeItem.getNodeName().contains(")")) {
						int index = nodeItem.getNodeName().indexOf(")");
						String nodeName = nodeItem.getNodeName().substring(index + 1);
						nodeItem.setNodeName(nodeName);
					}
                    break;
                }
            }
        }
        Collections.sort(nodeTranUserList, new Comparator<NodeTranUser>() {
            @Override
            public int compare(NodeTranUser o1, NodeTranUser o2) {
                return o1.getSort()-o2.getSort();
            }
        });
    }

	/**
	 * 结合前台任务管理列表，点击某行任务时，显示的任务简单明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("miniDetail")
	public ModelAndView miniDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String taskId = request.getParameter("taskId");

		TaskEntity taskEntity = bpmService.getTask(taskId);

		if (taskEntity == null) {
			return new ModelAndView("/platform/bpm/taskNotExist.jsp");
		}

		// 取到任务的侯选人员
		Set<TaskExecutor> candidateUsers = taskUserService.getCandidateExecutors(taskId);

		ProcessRun processRun = processRunService.getByActInstanceId(new Long(taskEntity.getProcessInstanceId()));

		BpmDefinition definition = bpmDefinitionService.getByActDefId(taskEntity.getProcessDefinitionId());

		List<ProcessTask> curTaskList = bpmService.getTasks(taskEntity.getProcessInstanceId());

		return getAutoView().addObject("taskEntity", taskEntity).addObject("processRun", processRun).addObject("candidateUsers", candidateUsers).addObject("processDefinition", definition).addObject("curTaskList", curTaskList);
	}

	/**
	 * 准备更新任务的路径
	 * 
	 * @param request
	 * @param response
	 * @returngetTaskUsers
	 * @throws Exception
	 */
	@RequestMapping("changePath")
	public ModelAndView changePath(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String taskId = request.getParameter("taskId");
		TaskEntity taskEntity = bpmService.getTask(taskId);
		Map<String, String> taskNodeMap = bpmService.getTaskNodes(taskEntity.getProcessDefinitionId(), taskEntity.getTaskDefinitionKey());
		Map<String, IMessageHandler> handlersMap = ServiceUtil.getHandlerMap();

		return this.getAutoView().addObject("taskEntity", taskEntity).addObject("taskNodeMap", taskNodeMap).addObject("curUser", ContextUtil.getCurrentUser()).addObject("handlersMap", handlersMap);
	}

	/**
	 * 保存变更路径
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("saveChangePath")
	@ResponseBody
	public String saveChangePath(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ProcessCmd processCmd = BpmUtil.getProcessCmd(request);
		processRunService.nextProcess(processCmd);
		saveSuccessResultMessage(request.getSession(), "更改任务执行的路径!");
		return SUCCESS;
	}

	/**
	 * 结束流程任务
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("end")
	@Action(description = "结束流程任务", detail = "结束流程【${SysAuditLinkService.getProcessRunLink(taskId)}】的任务")
	public void end(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultMessage resultMessage = null;
		try {
			String taskId = request.getParameter("taskId");
			TaskEntity taskEntity = bpmService.getTask(taskId);
			ProcessRun processRun = processRunService.getByActInstanceId(new Long(taskEntity.getProcessInstanceId()));
//			String voteContent = "由" + ContextUtil.getCurrentUser().getFullname() + "进行完成操作！";
			String voteContent = "由" + ContextSupportUtil.getUsername((SysUser) ContextUtil.getCurrentUser()) + "进行完成操作！";
			ProcessCmd cmd = new ProcessCmd();
			cmd.setTaskId(taskId);
			cmd.setVoteAgree((short) 0);
			cmd.setVoteContent(voteContent);
			cmd.setOnlyCompleteTask(true);
			processRunService.nextProcess(cmd);
			Long runId = processRun.getRunId();
			String memo = "结束了:" + processRun.getSubject();
			bpmRunLogService.addRunLog(runId, BpmRunLog.OPERATOR_TYPE_ENDTASK, memo);
			resultMessage = new ResultMessage(ResultMessage.Success, "成功完成了该任务!");
		} catch (Exception ex) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				resultMessage = new ResultMessage(ResultMessage.Fail, "完成任务失败:" + str);
			} else {
				String message = ExceptionUtil.getExceptionMessage(ex);
				resultMessage = new ResultMessage(ResultMessage.Fail, message);
			}
		}
		response.getWriter().print(resultMessage);
	}

	/**
	 * 根据任务结束流程实例。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("endProcess")
	@Action(description = "根据任务结束流程实例")
	public void endProcess(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();

		Long taskId = RequestUtil.getLong(request, "taskId");
		String memo = RequestUtil.getString(request, "memo");
		// Long curUserId=ContextUtil.getCurrentUserId();
		TaskEntity taskEnt = bpmService.getTask(taskId.toString());
		if (taskEnt == null) {
			writeResultMessage(out, "此任务已经完成!", ResultMessage.Fail);
		}

		String instanceId = taskEnt.getProcessInstanceId();
		ResultMessage message = null;
		try {
			String nodeId = taskEnt.getTaskDefinitionKey();
			ProcessRun processRun = bpmService.endProcessByInstanceId(new Long(instanceId), nodeId, memo);
			memo = "结束流程:" + processRun.getSubject() + ",结束原因:" + memo;
			bpmRunLogService.addRunLog(processRun.getRunId(), BpmRunLog.OPERATOR_TYPE_ENDTASK, memo);
			message = new ResultMessage(ResultMessage.Success, "结束流程实例成功!");
			writeResultMessage(out, message);
		} catch (Exception ex) {
			ex.printStackTrace();
			message = new ResultMessage(ResultMessage.Fail, ExceptionUtil.getExceptionMessage(ex));
			writeResultMessage(out, message);
		}
	}

	/**
	 * 待办事宜
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("pendingMattersList")
	public ModelAndView pendingMattersList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "taskItem");
		String nodePath = RequestUtil.getString(request, "nodePath");
		if (StringUtils.isNotEmpty(nodePath))
			filter.getFilters().put("nodePath", nodePath + "%");
		List<TaskEntity> list = bpmService.getMyTasks(filter);
		Map<Integer, WarningSetting> warningSetMap = reminderService.getWaringSetMap();
		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);

		return getAutoView().addObject("taskList", list).addObject("warningSet", warningSetMap).addObject("hasGlobalFlowNo", hasGlobalFlowNo);

	}

	/**
	 * 批量审批任务.
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("batComplte")
	public void batComplte(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		ResultMessage resultMessage = null;
		String taskIds = RequestUtil.getString(request, "taskIds");
		String opinion = RequestUtil.getString(request, "opinion");
		try {
			processRunService.nextProcessBat(taskIds, opinion);
			String message = MessageUtil.getMessage();
			resultMessage = new ResultMessage(ResultMessage.Success, message);
		} catch (Exception ex) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				resultMessage = new ResultMessage(ResultMessage.Fail, str);
			} else {
				String message = ExceptionUtil.getExceptionMessage(ex);
				resultMessage = new ResultMessage(ResultMessage.Fail, message);
			}
		}
		out.print(resultMessage);
	}

	/**
	 * 检测任务是否存在。
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("isTaskExsit")
	public void isTaskExsit(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long taskId = RequestUtil.getLong(request, "taskId");
		PrintWriter out = response.getWriter();
		TaskEntity taskEnt = bpmService.getTask(taskId.toString());
		if (taskEnt == null) {
			writeResultMessage(out, "此任务已经完成!", ResultMessage.Fail);
		} else {
			writeResultMessage(out, "任务存在!", ResultMessage.Success);
		}
	}

	@RequestMapping("dialog")
	@Action(description = "编辑流程抄送转发")
	public ModelAndView forward(HttpServletRequest request) throws Exception {
		Map<String, IMessageHandler> handlersMap = ServiceUtil.getHandlerMap();
		return getAutoView().addObject("handlersMap", handlersMap);

	}

	@RequestMapping("toStartCommunicate")
	@Action(description = "编辑流程抄送转发")
	public ModelAndView toStartCommunicate(HttpServletRequest request) throws Exception {
		Long setId = RequestUtil.getLong(request, "setId");
		BpmNodeSet bpmNodeSet = bpmNodeSetService.getById(setId);
		String communicate =  bpmNodeSet.getCommunicate();
		if(StringUtil.isNotEmpty(communicate)){
			communicate = communicate.replace("#@", "\"");
		}
		Map<String,String> communicateUser = bpmNodeSetService.getUserByCommunicate(communicate);
		String cmpIds ="";
		String cmpNames ="";
		if(communicateUser.size()!=0){
		 cmpIds = communicateUser.get("cmpIds").toString();
		cmpNames = communicateUser.get("cmpNames").toString();}
		Map<String, IMessageHandler> handlersMap = ServiceUtil.getHandlerMap();
		return getAutoView().addObject("handlersMap", handlersMap)
				.addObject("cmpNames", cmpNames)
				.addObject("cmpIds",cmpIds);

	}

	@RequestMapping("toTransTo")
	@Action(description = "编辑流程抄送转发")
	public ModelAndView toTransTo(HttpServletRequest request) throws Exception {
		Map<String, IMessageHandler> handlersMap = ServiceUtil.getHandlerMap();
		return getAutoView().addObject("handlersMap", handlersMap);

	}

	@RequestMapping("transToOpinionDialog")
	@Action(description = "编辑流程抄送转发")
	public ModelAndView transToOpinionDialog(HttpServletRequest request) throws Exception {
		Map<String, IMessageHandler> handlersMap = ServiceUtil.getHandlerMap();
		return getAutoView().addObject("handlersMap", handlersMap);

	}

	@RequestMapping("selExecutors")
	@Action(description = "启动流程时可以配置下一个执行人获取第一个节点的配置")
	public ModelAndView selExecutors(HttpServletRequest request) throws Exception {
		String actDefId = RequestUtil.getString(request, "actDefId");
		List<FlowNode> firstNode = NodeCache.getFirstNode(actDefId);
		String scope = "";
		if (BeanUtils.isNotEmpty(firstNode)) {
			FlowNode flowNode = firstNode.get(0);
			String nodeId = flowNode.getNodeId();
			BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId, "");
			if (BeanUtils.isNotEmpty(bpmNodeSet))
				scope = bpmNodeSet.getScope();
		}
		return getAutoView().addObject("scope", scope);
	}

	@RequestMapping("opDialog")
	@Action(description = "填写意见")
	public ModelAndView opDialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String isRequired = RequestUtil.getString(request, "isRequired");
		String actDefId = RequestUtil.getString(request, "actDefId");
		String taskId = RequestUtil.getString(request, "taskId");
		String reject = RequestUtil.getString(request, "reject", "0");
		BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(actDefId);
		List<ForkTaskReject> forkTaskExecutor = bpmActService.forkTaskExecutor(taskId);
		// 获取常用语
		List<String> taskAppItems = taskAppItemService.getApprovalByDefKeyAndTypeId(bpmDefinition.getDefKey(), bpmDefinition.getTypeId());
		return getAutoView().addObject("isRequired", isRequired)
							.addObject("reject",reject)
						    .addObject("taskAppItems", taskAppItems)
						    .addObject("forkTaskExecutor", forkTaskExecutor);
	}

	@RequestMapping("getMyTaskByRunId")
	@ResponseBody
	@Action(description = "根据流程runId获取流程任务")
	public String getMyTaskByRunId(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String runId = RequestUtil.getString(request, "runId", "0");
		QueryFilter filter = new QueryFilter(request);
		filter.addFilter("runId", runId);
		List<TaskEntity> list = bpmService.getMyTasks(filter);
		if (list != null && list.size() > 0) {
			return list.get(0).getId();
		}
		return "0";
	}
	
	/**
	 * 跳转到启动流程页面。（象翌的启动界面修改）<br/>
	 * 
	 * <pre>
	 * 传入参数流程定义id：defId。 
	 * 实现方法： 
	 * 1.根据流程对应ID查询流程定义。 
	 * 2.获取流程定义的XML。
	 * 3.获取流程定义的第一个任务节点。
	 * 4.获取任务节点的流程表单定义。 
	 * 5.显示启动流程表单页面。
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("startFlowFormForSe")
	@Action(description = "跳至启动流程页面")
	public ModelAndView startFlowFormForSe(HttpServletRequest request, HttpServletResponse response, Long defId) throws Exception {
		String businessKey = RequestUtil.getString(request, "businessKey");
		// 复制表单 启动流程
		String copyKey = RequestUtil.getString(request, "copyKey", "");
		ISysUser sysUser = ContextUtil.getCurrentUser();
		String ctxPath = request.getContextPath();
		ModelAndView mv = new ModelAndView("/platform/bpm/taskStartFlowFormForSe.jsp");

		// 流程草稿传入
		Long runId = RequestUtil.getLong(request, "runId", 0L);
		// 从已经完成的流程实例启动流程。
		Long relRunId = RequestUtil.getLong(request, "relRunId", 0L);

		// 构建参数到到JSP页面。
		Map paraMap = getPageParam(request);

		ProcessRun processRun = null;
		BpmDefinition bpmDefinition = null;
		if (StringUtils.isNotEmpty(businessKey) && runId == 0) {
			processRun = processRunService.getByBusinessKey(businessKey);
			if (BeanUtils.isEmpty(processRun)) {//业务数据模板新增表单后，在列表启动流程，没有流程实例
				defId = RequestUtil.getLong(request, "defId");
			} else {
				defId = processRun.getDefId();
				runId = processRun.getRunId();
			}
		}

		if (runId != 0) {
			processRun = processRunService.getById(runId);
			defId = processRun.getDefId();
		}

		if (defId != null && defId != 0L)
			bpmDefinition = bpmDefinitionService.getById(defId);

		ModelAndView tmpView = getNotValidView(bpmDefinition, businessKey);
		if (tmpView != null)
			return tmpView;

		// 根据已经完成的流程实例取得业务主键。
		String pk = processRunService.getBusinessKeyByRelRunId(relRunId);
		if (StringUtil.isNotEmpty(pk)) {
			businessKey = pk;
		}
		Boolean isFormEmpty = false;
		Boolean isExtForm = false;
		String form = "";
		String actDefId = "";
		// 通过草稿启动流程
		if (BeanUtils.isNotEmpty(processRun) && processRun.getStatus().equals(ProcessRun.STATUS_FORM)) {
			mv.addObject("isDraft", false);
			businessKey = processRun.getBusinessKey();
			Long formDefId = processRun.getFormDefId();
			actDefId = processRun.getActDefId();
			//是否使用新版本，草稿启动后会记录表单ID,如果
			int isNewVersion = RequestUtil.getInt(request, "isNewVersion", 0);
			if (formDefId != 0L) {
				String tableName = processRun.getTableName();
				if (!tableName.startsWith(TableModel.CUSTOMER_TABLE_PREFIX)) {
					tableName = TableModel.CUSTOMER_TABLE_PREFIX + tableName;
				}
				boolean isExistsData = bpmFormHandlerService.isExistsData(processRun.getDsAlias(), tableName, processRun.getPkName(), processRun.getBusinessKey());
				if (!isExistsData)
					return new ModelAndView("redirect:noData.ht");
			}

			if (StringUtil.isNotEmpty(processRun.getBusinessUrl())) {
				isExtForm = true;
				form = processRun.getBusinessUrl();
				// 替换主键。
				form = processRun.getBusinessUrl().replaceFirst(BpmConst.FORM_PK_REGEX, businessKey);
				if (!form.startsWith("http")) {
					form = ctxPath + form;
				}
			} else {
				if (isNewVersion == 1) {
					BpmFormDef defaultFormDef = bpmFormDefService.getById(formDefId);
					formDefId = bpmFormDefService.getDefaultPublishedByFormKey(defaultFormDef.getFormKey()).getFormDefId();
				}
				String nodeId = "";// 流程第一个节点
				FlowNode flowNode = NodeCache.getFirstNodeId(actDefId);
				if (flowNode != null) {
					nodeId = flowNode.getNodeId();
				}
				BpmFormDef bpmFormDef = bpmFormDefService.getById(formDefId);
				form = bpmFormHandlerService.obtainHtml(bpmFormDef, businessKey, "", actDefId, nodeId, ctxPath, "", true, false, false,(short)0);
				//下面调用新的解释器
				form = bpmFormDefService.parseHtml(bpmFormDef, businessKey, "", actDefId, nodeId, "", true, true, false,false,(short) 0);
			}
			// 流程定义里面的启动
		} else {
			boolean isReCalcuate = false;
			if (StringUtil.isNotEmpty(copyKey)) {
				businessKey = copyKey;
				isReCalcuate = true;
			}
			mv.addObject("isDraft", true);
			actDefId = bpmDefinition.getActDefId();

			// 获取表单节点
			BpmNodeSet bpmNodeSet = bpmNodeSetService.getStartBpmNodeSet(actDefId, false);

			FormModel formModel = getStartForm(bpmNodeSet, businessKey, actDefId, ctxPath, isReCalcuate,StringUtil.isNotEmpty(copyKey));
			// 是外部表单
			isFormEmpty = formModel.isFormEmpty();
			isExtForm = formModel.getFormType() > 0;

			if (isExtForm) {
				form = formModel.getFormUrl();
			} else if (formModel.getFormType() == 0) {
				form = formModel.getFormHtml();
			}
			if (BeanUtils.isNotEmpty(bpmNodeSet)) {
				mv.addObject("formKey", bpmNodeSet.getFormKey());
			}
		}
		// 获取按钮
		Map<String, List<BpmNodeButton>> mapButton = bpmNodeButtonService.getMapByStartForm(defId);
		// 帮助文档
		SysFile sysFile = null;
		if (BeanUtils.isNotEmpty(bpmDefinition.getAttachment()))
			sysFile = sysFileService.getById(bpmDefinition.getAttachment());

		// 通过defid和nodeId获取联动设置
		List<BpmGangedSet> bpmGangedSets = bpmGangedSetService.getByDefIdAndNodeId(defId, BpmGangedSet.START_NODEID);
		JSONArray gangedSetJarray = (JSONArray) JSONArray.fromObject(bpmGangedSets);

		if (NodeCache.isMultipleFirstNode(actDefId)) {
			mv.addObject("flowNodeList", NodeCache.getFirstNode(actDefId)).addObject("isMultipleFirstNode", true);
		}

		mv.addObject("bpmDefinition", bpmDefinition)
		.addObject("isExtForm", isExtForm).
		addObject("isFormEmpty", isFormEmpty)
		.addObject("mapButton", mapButton)
		.addObject("defId", defId)
		.addObject("paraMap", paraMap)
		.addObject("form", form)
		.addObject("runId", runId)
		.addObject("businessKey", StringUtil.isEmpty(copyKey) ? businessKey : "")
		.addObject("sysFile", sysFile)
		.addObject("bpmGangedSets", gangedSetJarray)
		.addObject("curUserId", sysUser.getUserId().toString())
//		.addObject("curUserName", sysUser.getFullname());
		.addObject("curUserName", ContextSupportUtil.getUsername((SysUser) sysUser));
		return mv;
	}
	
	/**
	 * 启动任务界面。 根据任务ID获取流程实例，根据流程实例获取表单数据。
	 * （象翌需要的流程审批界面）
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("toStartForSe")
	public ModelAndView toStartForSe(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = this.getAutoView();
		return getToStartView(request, response, mv, 1);
	}
	
	/** 
	 * 获取前端可显示的消息类型
	 * @param bpmDefinition
	 * @return
	 */
	private Map<String, IMessageHandler> getMessageType(BpmDefinition bpmDefinition){
		// 消息类型
		Map<String, IMessageHandler> sysHandlersMap=ServiceUtil.getHandlerMap();
		Map<String, IMessageHandler> handlersMap = new TreeMap<String, IMessageHandler>();
		// 流程定义未设置在审批节点显示的消息类型，则清空消息类型
		if(!StringUtils.isBlank(bpmDefinition.getInformShowInFront())){
			// 流程定义设置了在审批节点显示的消息类型，则只保留设置的消息类型
			Iterator<String> keyIterator = sysHandlersMap.keySet().iterator();
			String key = null;
			while(keyIterator.hasNext()){
				key = keyIterator.next();
				if(bpmDefinition.getInformShowInFront().contains(key)){
					handlersMap.put(key, sysHandlersMap.get(key));
				}
			}
		}
		return handlersMap;
	}
}