package com.suneee.ucp.mh.controller;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.page.PageList;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.NodeTranUser;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysProperty;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.util.WarningSetting;
import com.suneee.platform.service.bpm.*;
import com.suneee.platform.service.bpm.util.BpmUtil;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysPropertyService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.weixin.model.ListModel;
import com.suneee.weixin.util.CommonUtil;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 
 *  象翌办公
 * @author sibo
 * @time 2017年5月10日 下午1:30:07
 *
 */
@Controller
@RequestMapping("/mh/sework/")
public class SuneeeWorkController extends UcpBaseController {
	
	@Resource
	private ProcessRunService processRunService;
	@Resource
	private TaskOpinionService taskOpinionService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private GlobalTypeService globalTypeService;
	@Resource
	private BpmProCopytoService bpmProCopytoService;
	@Resource
	private TaskService taskService;
	@Resource
	private BpmService bpmService;
	@Resource
	private TaskReminderService reminderService;
	@Resource
	private SysPropertyService sysPropertyService;
	
	/**
	 * 与我相关的全部流程列表（包括我发起的和我审核的）
	 *  
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("aboutMeFlowList")
	@Action(description="与我相关的流程列表")
	public ModelAndView aboutMeFlowList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "processRunItem");
		filter.addFilter("assignee", ContextUtil.getCurrentUserId().toString());
		String type = RequestUtil.getString(request, "type");
		
		List<ProcessRun> list = processRunService.getAlreadyCompletedMattersList(filter);
		
		// 如果要查询处理中的流程
		if (StringUtils.isNotEmpty(type) && type.equals("handling")) {
			list = processRunService.getAlreadyMattersList(filter);
			for (ProcessRun processRun : list) {
				if (processRun.getStatus().shortValue() != ProcessRun.STATUS_FINISH.shortValue()) {
					// 1.查找当前用户是否有最新审批的任务
					TaskOpinion taskOpinion = taskOpinionService.getLatestUserOpinion(processRun.getActInstId(), ContextUtil.getCurrentUserId());
					if (BeanUtils.isNotEmpty(taskOpinion))
						processRun.setRecover(ProcessRun.STATUS_RECOVER);
				}
			}
		}
		
		// 查询已办结的流程
		if (StringUtils.isNotEmpty(type) && type.equals("handled")) {
			list = processRunService.getCompletedMattersList(filter);
			for (ProcessRun processRun : list) {
				BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(processRun.getActDefId());
				if (BeanUtils.isNotEmpty(bpmDefinition) && bpmDefinition.getIsPrintForm() == 1) {
					processRun.setIsPrintForm(bpmDefinition.getIsPrintForm());
				}
			}
		}
		
		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("mh/seworkFlowList");
		
		return mv.addObject("processRunList", list)
				.addObject("hasGlobalFlowNo", hasGlobalFlowNo)
				.addObject("backlog", false);
	}
	
	/**
	 * 获取我的所有待办
	 *  
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("myTaskList")
	public ModelAndView myTaskList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "taskItem");
		addDefIdFilter(request, filter);
		List<TaskEntity> list = bpmService.getMyTasks(filter);
		
		Map<Integer, WarningSetting> warningSetMap = reminderService.getWaringSetMap();
		
		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("mh/myTaskList");
		
		return mv.addObject("taskList", list)
				.addObject("warningSet", warningSetMap)
				.addObject("hasGlobalFlowNo", hasGlobalFlowNo)
				.addObject("backlog", true);
	}
	
	/**
	 * 获取我的待办任务。
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("myTaskListFront")
	@ResponseBody
	public ListModel myTaskListFront(HttpServletRequest request, HttpServletResponse response) {
		QueryFilter queryFilter=new QueryFilter(request, true);
		addDefIdFilter(request, queryFilter);
		flowCateFilter(queryFilter);
		List<TaskEntity> list = bpmService.getMyTasks(queryFilter);
		ListModel listModel=CommonUtil.getListModel((PageList) list);
		return listModel;
	}
	
	/**
	 * 前端已办办结事宜列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("flowToMe")
	@Action(description = "已办办结事宜列表")
	public ModelAndView alreadyCompletedMattersList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "processRunItem");
		filter.addFilter("assignee", ContextUtil.getCurrentUserId().toString());// 用户id
		String nodePath = RequestUtil.getString(request, "nodePath");
		if (StringUtils.isNotEmpty(nodePath))
			filter.getFilters().put("nodePath", nodePath + "%");
		List<ProcessRun> list = processRunService.getAlreadyCompletedMattersList(filter);
		
		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
		ModelAndView mv = this.getAutoView().addObject("processRunList", list).addObject("hasGlobalFlowNo", hasGlobalFlowNo);
		mv.setViewName("mh/flowToMe");
		return mv;
	}
	
	/**
	 * 我的申请列表
	 *  
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("myApplyList")
	@Action(description = "我的申请列表")
	public ModelAndView myApplyList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "processRunItem");
		filter.addFilter("creatorId", ContextUtil.getCurrentUserId());
		addDefIdFilter(request, filter);
		List<ProcessRun> list = processRunService.getMyRequestList(filter);
		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("mh/myApplyList");
		mv.addObject("processRunList", list).addObject("hasGlobalFlowNo", hasGlobalFlowNo);
		
		return mv;
	}
	
	
	/**
	 * 我的申请列表-接口
	 *  
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("myApplyListFront")
	public List<ProcessRun> myApplyListFront(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "processRunItem");
		filter.addFilter("creatorId", ContextUtil.getCurrentUserId());
		addDefIdFilter(request, filter);
		List<ProcessRun> list = processRunService.getMyRequestList(filter);
		return list;
	}
	
	/**
	 * 我的办结列表（我发起，且已办结的）
	 *  
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("myCompletedList")
	@Action(description = "我的办结列表")
	public ModelAndView myCompletedList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "processRunItem");
		filter.addFilter("creatorId", ContextUtil.getCurrentUserId());
		addDefIdFilter(request, filter);
		List<ProcessRun> list = processRunService.getMyCompletedList(filter);
		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("mh/myCompletedList");
		mv.addObject("processRunList", list).addObject("hasGlobalFlowNo", hasGlobalFlowNo);
		
		return mv;
	}
	
	/**
	 * 我的某个流程的请求和办结列表
	 *  
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("myApplyCompletedList")
	@Action(description = "我的请求和办结列表")
	public ModelAndView myApplyCompletedList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "processRunItem");
		filter.addFilter("creatorId", ContextUtil.getCurrentUserId());
		// 获取流程类型
		String flowType = RequestUtil.getString(request, "flowType");
		if(StringUtils.isNotBlank(flowType)){
			// 根据流程类型获取流程实例ID
			Long defId = sysPropertyService.getLongByAlias(flowType);
			filter.addFilter("defId", defId == null ? 0L : defId);
		}
		
		addDefIdFilter(request, filter);
		List<ProcessRun> list = processRunService.getMyRequestCompletedList(filter);
		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("mh/myApplyList");
		mv.addObject("processRunList", list).addObject("hasGlobalFlowNo", hasGlobalFlowNo);
		
		return mv;
	}
	
	/**
	 *  快捷审批功能（待完善）
	 *  
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("doNext")
	public ModelAndView doNext(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SysUser user = (SysUser) ContextUtil.getCurrentUser();
		String taskId = request.getParameter("taskId");
		String voteAgree = request.getParameter("voteAgree");
		String voteContent = request.getParameter("voteContent");
		
		// TO-DO
		if (StringUtil.isEmpty(taskId)) {
			System.out.println("流程任务ID必须填写");
			return null;
		}
		
		ProcessCmd processCmd = new ProcessCmd();
		processCmd.setTaskId(taskId);
		
		if (user == null || StringUtil.isEmpty(user.getAccount())) {
			System.out.println("用户名必须填写");
			return null;
		}
		
		processCmd.setUserAccount(user.getAccount());
		
		if (StringUtil.isNotEmpty(voteAgree)) {
			processCmd.setVoteAgree(Short.parseShort(voteAgree));
		}

		if (StringUtil.isNotEmpty(voteContent)) {
			processCmd.setVoteContent(voteContent);
		}

		String destTask = null;
		List<NodeTranUser> nodeTranUserList = null;
		boolean canChoicePath = bpmService.getCanChoicePath(null, taskId);
		nodeTranUserList = bpmService.getNodeTaskUserMap(taskId, user.getUserId(), canChoicePath);
		if (nodeTranUserList!=null&&nodeTranUserList.size()>0){
			destTask=nodeTranUserList.get(0).getNodeId();
		}
		if (StringUtil.isNotEmpty(destTask)) {
			processCmd.setDestTask(destTask);
		}

		processRunService.nextProcess(processCmd);
		
		QueryFilter filter = new QueryFilter(request, "taskItem");
		List<TaskEntity> list = bpmService.getMyTasks(filter);
		
		Map<Integer, WarningSetting> warningSetMap = reminderService.getWaringSetMap();
		
		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("mh/seworkFlowList");
		
		return mv.addObject("taskList", list)
				.addObject("warningSet", warningSetMap)
				.addObject("hasGlobalFlowNo", hasGlobalFlowNo)
				.addObject("backlog", true);
	}
	
	/**
	 * 返回流程的详细信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("info")
	public ModelAndView info(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long curUserId = ContextUtil.getCurrentUserId();
		String runId = request.getParameter("runId");
		String copyId = request.getParameter("copyId");
		String prePage = RequestUtil.getString(request, "prePage");
		String preUrl = RequestUtil.getPrePage(request);
		String ctxPath = request.getContextPath();
		ProcessRun processRun = processRunService.getById(Long.parseLong(runId));
		String form = processRunService.getFormDetailByRunId(Long.parseLong(runId), ctxPath);
		boolean isExtForm = false;
		if (processRun.getFormDefId() == 0L && StringUtil.isNotEmpty(processRun.getBusinessUrl())) {
			isExtForm = true;
		}

		BpmDefinition bpmDefinition = bpmDefinitionService.getById(processRun.getDefId());
		// 是否是第一个节点
		boolean isFirst = this.isFirst(processRun);
		// 是否允许办结转发
		boolean isFinishedDiver = this.isFinishedDiver(bpmDefinition, processRun);
		// 是否允许追回
		boolean isCanRedo = this.isCanRedo(processRun, isFirst, curUserId);

		boolean isCopy = this.isCopy(bpmDefinition);

		if (null == prePage || "".equals(prePage)) {// 抄送事宜修改状态
			try {
				// 适用于冒泡不能传copyId
				if (null == copyId || "".equals(copyId)) {
					ProcessRun run = processRunService.getCopyIdByRunid(Long.parseLong(runId));
					copyId = run.getCopyId().toString();
				}
				bpmProCopytoService.markCopyStatus(copyId);
			} catch (Exception e) {
				logger.info("update copy matter state failed!");
			}
		}

		// 是否允许打印
		boolean isPrintForm = this.isPrintForm(processRun, bpmDefinition, prePage, copyId);
		
		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
		
		ModelAndView mv = new ModelAndView("platform/bpm/processRunInfo.jsp");
		return mv.addObject("bpmDefinition", bpmDefinition).addObject("processRun", processRun)
					.addObject("form", form).addObject("isPrintForm", isPrintForm).addObject("isExtForm", isExtForm)
					.addObject("isFirst", isFirst).addObject("isCanRedo", isCanRedo)
					.addObject("isFinishedDiver", isFinishedDiver).addObject("isCopy", isCopy).addObject(RequestUtil.RETURNURL, preUrl)
					.addObject("hasGlobalFlowNo", hasGlobalFlowNo);

	}
	
	/**
	 * 是否允许打印表单
	 * 
	 * <pre>
	 * 	1.允许打印表单
	 * 		是我的办结
	 * 	2.允许
	 * 		办结打印
	 * 	3 允许
	 * 		抄送打印
	 * 
	 * </pre>
	 * 
	 * @param processRun
	 * @param bpmDefinition
	 * @param prePage
	 * @param copyId
	 * @return
	 */
	private boolean isPrintForm(ProcessRun processRun, BpmDefinition bpmDefinition, String prePage, String copyId) {
		if (bpmDefinition.getIsPrintForm() == null || bpmDefinition.getIsPrintForm().intValue() != 1 || processRun.getStatus().shortValue() != ProcessRun.STATUS_FINISH.shortValue()) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean isCopy(BpmDefinition bpmDefinition) {
		Short status = bpmDefinition.getStatus();
		if (BpmDefinition.STATUS_DISABLED.equals(status))
			return false;
		if (BpmDefinition.STATUS_INST_DISABLED.equals(status))
			return false;
		return true;
	}
	
	/**
	 * 是否允许办结转办
	 * 
	 * @param bpmDefinition
	 * @param processRun
	 * @return
	 */
	private boolean isFinishedDiver(BpmDefinition bpmDefinition, ProcessRun processRun) {
		if (BpmDefinition.STATUS_INST_DISABLED.equals(bpmDefinition.getStatus()))
			return false;
		if (BeanUtils.isNotEmpty(bpmDefinition.getAllowFinishedDivert()))
			return bpmDefinition.getAllowFinishedDivert().shortValue() == BpmDefinition.ALLOW.shortValue() && processRun.getStatus().shortValue() == ProcessRun.STATUS_FINISH.shortValue();

		return false;
	}
	
	/**
	 * 是否可以追回
	 * 
	 * <pre>
	 * 是否可以追回，有以下几个判定条件：
	 *  1、流程正在运行；
	 *  2、流程非第一个节点；
	 *  3、上一步执行人是当前用户；
	 *  4、上一步操作是同意；
	 *  5、目前该实例只有一个任务。
	 *  6、 对应流程定义不能为禁用流程实例状态
	 *  
	 *  1.根据流程获取当前的流程任务列表。
	 *  2.根据任务列表去堆栈查找执行人，如果堆栈中有当前人，才可以撤销。
	 *  
	 * </pre>
	 * 
	 * @param processRun
	 * @param isFirst
	 * @param curUserId
	 * @return
	 * @throws Exception 
	 */
	private boolean isCanRedo(ProcessRun processRun, boolean isFirst, Long curUserId) throws Exception {
		if (!processRun.getStatus().equals(ProcessRun.STATUS_RUNNING) || isFirst) return false;
		String actDefId = processRun.getActDefId();
		BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(actDefId);
		if (BpmDefinition.STATUS_INST_DISABLED.equals(bpmDefinition.getStatus())) return false;
		
		String instanceId = processRun.getActInstId();
		TaskOpinion taskOpinion = taskOpinionService.getLatestUserOpinion(instanceId, curUserId);
		if(taskOpinion == null) return false;
		
		Short checkStatus = taskOpinion.getCheckStatus();
		
		if (!TaskOpinion.STATUS_AGREE.equals(checkStatus) && !TaskOpinion.STATUS_REFUSE.equals(checkStatus)) return false;
		
		
		ResultMessage resultMsg =processRunService.checkRecover(processRun.getRunId());
		
		return ResultMessage.Success== resultMsg.getResult();
		
	}
	
	/**
	 * 是否是第一个节点
	 * 
	 * @param processRun
	 * @return
	 * @throws Exception
	 */
	private boolean isFirst(ProcessRun processRun) throws Exception {
		boolean isFirst = false;
		if (BeanUtils.isEmpty(processRun))
			return isFirst;
		Long instId = Long.parseLong(processRun.getActInstId());
		String actDefId = processRun.getActDefId();
		List<TaskOpinion> taskOpinionList = taskOpinionService.getCheckOpinionByInstId(instId);
		String nodeId = "";
		// 判断起始节点后是否有多个节点
		if (NodeCache.isMultipleFirstNode(actDefId)) {
			nodeId = processRun.getStartNode();
		} else {
			FlowNode flowNode = NodeCache.getFirstNodeId(actDefId);
			if (flowNode == null)
				return isFirst;
			nodeId = flowNode.getNodeId();
		}
		for (TaskOpinion taskOpinion : taskOpinionList) {
			isFirst = nodeId.equals(taskOpinion.getTaskKey());
			if (isFirst)
				break;
		}
		return isFirst;
	}
	
	/**
	 * 获取所有的流程申请（待完善）
	 *  
	 * @param request
	 * @param response
	 */
	@RequestMapping("getAllFlowDefinitions")
	@ResponseBody
	public List<Map<String,Object>> getAllFlowDefinitions(HttpServletRequest request, HttpServletResponse response) {
		List<GlobalType> list= globalTypeService.getByCatKey(GlobalType.CAT_FLOW, false);
		List<Map<String,Object>> flowList = new ArrayList<Map<String,Object>>();
		for (GlobalType globalType : list) {
			Map<String,Object> typeMap = new HashMap<String, Object>();
			Long typeId = globalType.getTypeId();
			typeMap.put("title", globalType.getTypeName());
			List<BpmDefinition> bpmDefList = bpmDefinitionService.getByTypeId(typeId);
			if (!bpmDefList.isEmpty()) {
				typeMap.put("flowList", bpmDefList);
			}
			flowList.add(typeMap);
		}
		
		return flowList;
	}

	/**
	 * 增加流程实例ID的查询条件
	 * @param request
	 * @param filter
	 */
	private void addDefIdFilter(HttpServletRequest request, QueryFilter filter){
		// 获取流程类型
		String flowType = RequestUtil.getString(request, "flowType");
		if(StringUtils.isNotBlank(flowType)){
			// 根据流程类型获取流程实例ID
			Long defId = sysPropertyService.getLongByAlias(flowType);
			filter.addFilter("defId", defId == null ? 0L : defId);
		}
	}

	/**
	 * 流程分类条件过滤
	 * @param filter
	 */
	private void flowCateFilter(QueryFilter filter) {
		Long typeId = (Long) filter.getFilters().get("typeId");
		if (typeId != null) {
			//通过typeId获取该分类下所有子类数据（流程定义），存放于list
			List<Long> typeIds = globalTypeService.subTypeIdByParent(typeId);
			if (typeIds.size() == 0) {
				typeIds.add(0L);
			}
			filter.addFilter("typeIds", typeIds);
		} else {
			List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, false);
			Set<Long> typeIdList = BpmUtil.getTypeIdList(globalTypeList);
			filter.addFilter("typeIds", typeIdList);
		}
	}
}
