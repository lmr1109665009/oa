package com.suneee.platform.controller.bpm;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.bpm.graph.ShapeMeta;
import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.bpm.model.ProcessTask;
import com.suneee.core.bpm.util.BpmUtil;
import com.suneee.core.db.datasource.DataSourceUtil;
import com.suneee.core.encrypt.Base64;
import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.page.PageList;
import com.suneee.core.table.TableModel;
import com.suneee.core.util.*;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.bpm.*;
import com.suneee.platform.model.bpm.BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.form.BpmPrintTemplate;
import com.suneee.platform.model.system.*;
import com.suneee.platform.service.bpm.*;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.form.*;
import com.suneee.platform.service.system.*;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import net.sf.json.JSONArray;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;

/**
 * 对象功能:流程实例扩展控制器类 开发公司:广州宏天软件有限公司 开发人员:csx 创建时间:2011-12-03 09:33:06
 */
@Controller
@RequestMapping("/platform/bpm/processRun/")
@Action(ownermodel = SysAuditModelType.PROCESS_MANAGEMENT)
public class ProcessRunController extends BaseController {
	protected Logger log = LoggerFactory.getLogger(ProcessRunService.class);

	@Resource
	private ProcessRunService processRunService;
	@Resource
	private BpmService bpmService;
	@Resource
	private TaskOpinionService taskOpinionService;
	@Resource
	private TaskService taskService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private SysFileService sysFileService;
	 
	@Resource
	private BpmFormDefService bpmFormDefService;
	@Resource
	private SysTemplateService sysTemplateService;
	@Resource
	private HistoryService historyService;
	@Resource
	private TaskReadService taskReadService;
	@Resource
	private TaskUserService taskUserService;
	@Resource
	private SysUserService sysUserService;
	@Resource
	private BpmFormHandlerService bpmFormHandlerService;
	@Resource
	private BpmProCopytoService bpmProCopytoService;
	@Resource
	private BpmRunLogService bpmRunLogService;
	@Resource
	private BpmPrintTemplateService bpmPrintTemplateService;
	@Resource
	private SysUserOrgService sysUserOrgService;
	@Resource
	private TaskMessageService taskMessageService;
	@Resource
	private BpmFormTableService bpmFormTableService;
	
	@Resource
	private BpmDefAuthorizeService bpmDefAuthorizeService;
	@Resource
	private BpmGangedSetService bpmGangedSetService;
	@Autowired
	private BpmFormTableService formTableService;
	@Autowired
	private BpmFormFieldService formFieldService;
	@Autowired
	private GlobalTypeService globalTypeService;
	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 取得流程实例扩展分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("list")
	@Action(description = "查看流程实例扩展分页列表")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "processRunItem");
		// 过滤掉草稿实例
		filter.addFilter("exceptStatus", 4);
		// 过滤流程定义状态为"禁用实例" 的流程实例
		filter.addFilter("exceptDefStatus", 3);

		// 增加按新的流程分管授权中任务类型的权限获取流程的任务
		Long userId = ContextUtil.getCurrentUserId();
		String isNeedRight = "";
		Map<String, AuthorizeRight> authorizeRightMap = null;
		if (!ContextUtil.isSuperAdmin()) {
			isNeedRight = "yes";
			// 获得流程分管授权与用户相关的信息
			Map<String, Object> actRightMap = bpmDefAuthorizeService.getActRightByUserMap(userId, BPMDEFAUTHORIZE_RIGHT_TYPE.INSTANCE, true, false);
			// 获得流程分管授权与用户相关的信息集合的流程KEY
			String actRights = (String) actRightMap.get("authorizeIds");
			filter.addFilter("actRights", actRights);
			// 获得流程分管授权与用户相关的信息集合的流程权限内容
			authorizeRightMap = (Map<String, AuthorizeRight>) actRightMap.get("authorizeRightMap");
		}
		filter.addFilter("isNeedRight", isNeedRight);

		List<ProcessRun> list = processRunService.getAll(filter);

		// 把前面获得的流程分管授权的权限内容设置到流程管理列表
		if (authorizeRightMap != null) {
			for (ProcessRun processRun : list) {
				processRun.setAuthorizeRight(authorizeRightMap.get(processRun.getFlowKey()));
			}
		} else {
			// 如果需要所有权限的就直接虚拟一个有处理权限的对象
			AuthorizeRight authorizeRight = new AuthorizeRight();
			authorizeRight.setRightByAuthorizeType("Y", BPMDEFAUTHORIZE_RIGHT_TYPE.INSTANCE);
			for (ProcessRun processRun : list) {
				processRun.setAuthorizeRight(authorizeRight);
			}
		}

		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
		
		ModelAndView mv = this.getAutoView().addObject("processRunList", list).addObject("hasGlobalFlowNo", hasGlobalFlowNo);
		return mv;
	}

	/**
	 * 是否允许撤销
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("checkRecover")
	@ResponseBody
	public ResponseMessage checkRecover(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResponseMessage responseMessage = new ResponseMessage();
		Long runId = RequestUtil.getLong(request, "runId");
		ResultMessage result = processRunService.checkRecover(runId);
		responseMessage.setStatus(result.getResult());
		responseMessage.setMessage(result.getMessage());
		return responseMessage;
	}

	/**
	 * 检查是否允许撤销到发起人。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("checkRedo")
	@ResponseBody
	public ResponseMessage checkRedo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResponseMessage responseMessage = new ResponseMessage();
		Long runId = RequestUtil.getLong(request, "runId");
		// 子流程是否撤销由父流程决定
		// ProcessRun processRun= processRunService.getById(runId);
		ResultMessage result= processRunService.checkRecoverByStart(runId);
		responseMessage.setStatus(result.getResult());
		responseMessage.setMessage(result.getMessage());
		return responseMessage;
	}

	@RequestMapping("monitor")
	@Action(description = "流程监控")
	public ModelAndView monitor(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "processRunItem");
		filter.addFilter("curUser", ContextUtil.getCurrentUserId());
		List<ProcessRun> list = processRunService.getMonitor(filter);
		return this.getAutoView().addObject("processRunList", list);
	}

	/**
	 * 显示撤销对话框。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("recoverDialog")
	public ModelAndView recoverDialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long runId = RequestUtil.getLong(request, "runId", 0);
		int backToStart = RequestUtil.getInt(request, "backToStart", 0);
		ModelAndView mv = getAutoView();
		mv.addObject("runId", runId);
		Map<String, IMessageHandler> handlersMap=ServiceUtil.getHandlerMap();
		mv.addObject("backToStart", backToStart).addObject("handlersMap", handlersMap);

		return mv;
	}

	/**
	 * 显示追回对话框。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("redoDialog")
	public ModelAndView redoDialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long runId = RequestUtil.getLong(request, "runId", 0);
		int backToStart = RequestUtil.getInt(request, "backToStart", 0);
		ModelAndView mv = getAutoView();
		mv.addObject("runId", runId);
		Map<String, IMessageHandler> handlersMap=ServiceUtil.getHandlerMap();
		mv.addObject("backToStart", backToStart).addObject("handlersMap", handlersMap);

		return mv;
	}

	/**
	 * 任务追回,检查当前正在运行的任务是否允许进行追回。
	 * 
	 * <pre>
	 * 需要传入的参数：
	 * runId:任务执行Id。
	 * backToStart:追回到发起人。
	 * memo:追回原因。
	 *  任务能够被追回的条件：
	 *  1.流程实例没有结束。
	 *  
	 * 	任务追回包括两种情况。
	 *  1.追回到发起人。
	 *  4.如果这个流程实例有多个流程实例的情况，那么第一个跳转到退回节点，其他的只完成当前任务，不进行跳转。
	 * 
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("recover")
	public void recover(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		Long runId = RequestUtil.getLong(request, "runId");
		String informType = RequestUtil.getStringValues(request, "informType");
		String memo = RequestUtil.getString(request, "opinion");
		int backToStart = RequestUtil.getInt(request, "backToStart");
		ResultMessage resultMessage = null;
		try {
			// 追回到发起人
			if (backToStart == 1) {
				resultMessage = processRunService.redo(runId, informType, memo);
			} else {
				// 追回
				resultMessage = processRunService.recover(runId, informType, memo);
			}
		} catch (Exception ex) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				resultMessage = new ResultMessage(ResultMessage.Fail, str);
			} else {
				String message = ExceptionUtil.getExceptionMessage(ex);
				resultMessage = new ResultMessage(ResultMessage.Fail, message);
			}
		}
		//writeResultMessage(out, resultMessage);
		com.suneee.eas.common.utils.HttpUtil.writeResponseJson(response,ResponseMessage.success(resultMessage.getMessage()));
	}

	/**
	 * 查看流程实例历史列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("history")
	@Action(description = "查看流程实例历史列表")
	public ModelAndView history(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter queryFilter = new QueryFilter(request, "processRunItem");
		List<ProcessRun> list = processRunService.getAllHistory(queryFilter);
		ModelAndView mv = this.getAutoView().addObject("processRunList", list);
		return mv;
	}

	/**
	 * 取得流程实例扩展分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("myStart")
	@Action(description = "查看我发起的流程列表")
	public ModelAndView myStart(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "processRunItem");
		filter.addFilter("creatorId", ContextUtil.getCurrentUserId());
		//查询流程实例列表，过滤掉草稿状态的流程
		//filter.addFilter("isForm", ProcessRun.STATUS_FORM);
		List<ProcessRun> list = processRunService.getAll(filter);

		if (BeanUtils.isNotEmpty(list)) {
			for (ProcessRun processRun : list) {
				BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(processRun.getActDefId());
				if (bpmDefinition.getIsPrintForm() == 1) {
					processRun.setIsPrintForm(bpmDefinition.getIsPrintForm());
				}
			}
		}

		ModelAndView mv = this.getAutoView().addObject("processRunList", list);

		return mv;
	}

	/**
	 * 催促执行人、所属人（优先催促执行人，没有执行人就催促所属人）
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("urgeOwner")
	@Action(description = "打开催办界面")
	public ModelAndView urgeOwner(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String actInstId = RequestUtil.getString(request, "actInstId");
		String inner = "";
		String mail = "";
		String shortmsg = "";
		ProcessRun processRun = processRunService.getByActInstanceId(new Long(actInstId));

		SysTemplate temp = sysTemplateService.getDefaultByUseType(SysTemplate.USE_TYPE_URGE);
		if (BeanUtils.isNotEmpty(temp)) {
			inner = temp.getHtmlContent();
			mail = temp.getHtmlContent();
			shortmsg = temp.getPlainContent();
		}
		ModelAndView mv = this.getAutoView().addObject("processSubject", processRun.getSubject()).addObject("actInstId", actInstId).addObject("inner", inner).addObject("mail", mail).addObject("shortMsg", shortmsg);
		return mv;
	}

	/**
	 * 执行催办动作
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("urgeSubmit")
	@Action(description = "执行催办")
	public void urgeSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			String instanceId = RequestUtil.getString(request, "actInstId");
			String messgeType = RequestUtil.getStringAry(request, "messgeType");
			ProcessRun processRun = processRunService.getByActInstanceId(Long.parseLong(instanceId));
			String parentActDefId = processRunService.getParentProcessRunActDefId(processRun);
			String subject = RequestUtil.getString(request, "subject");
			String processSubject = RequestUtil.getString(request, "processSubject");
			String opinion = RequestUtil.getString(request, "opinion");
			BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(processRun.getActDefId());
			if (BpmDefinition.STATUS_INST_DISABLED.equals(bpmDefinition.getStatus())) {
				com.suneee.eas.common.utils.HttpUtil.writeResponseJson(response,ResponseMessage.fail("对不起,此流程定义已禁用实例!"));
				return;
			}
			Boolean userProcessSubject = RequestUtil.getBoolean(request, "userProcessSubject");
			if (userProcessSubject || StringUtils.isEmpty(subject))
				subject = processSubject;
			Map<String, String> map = sysTemplateService.getTempByFun(SysTemplate.USE_TYPE_URGE);

			List<Task> taskList = taskService.createTaskQuery().processInstanceId(instanceId).list();
			taskMessageService.notify(taskList, messgeType, subject, map, opinion, parentActDefId);
			com.suneee.eas.common.utils.HttpUtil.writeResponseJson(response,ResponseMessage.success("催办信息已发送成功!"));
		} catch (Exception ex) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				String msg = "催办信息已发送失败";
				ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, msg + ":" + str);
				//response.getWriter().print(resultMessage);
				com.suneee.eas.common.utils.HttpUtil.writeResponseJson(response,ResponseMessage.fail(resultMessage.getMessage()));
			} else {
				String message = ExceptionUtil.getExceptionMessage(ex);
				ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, message);
				//response.getWriter().print(resultMessage);
				com.suneee.eas.common.utils.HttpUtil.writeResponseJson(response,ResponseMessage.fail(resultMessage.getMessage()));
			}
		}
	}

	/**
	 * 查看我参与审批流程列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("myAttend")
	@Action(description = "查看我参与审批流程列表")
	public ModelAndView myAttend(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "processRunItem");
		filter.addFilter("assignee", ContextUtil.getCurrentUserId().toString());
		List<ProcessRun> list = processRunService.getMyAttend(filter);
		for (ProcessRun processRun : list) {
			if (processRun.getStatus().shortValue() != ProcessRun.STATUS_FINISH.shortValue()) {
				// 1.查找当前用户是否有最新审批的任务
				TaskOpinion taskOpinion = taskOpinionService.getLatestUserOpinion(processRun.getActInstId(), ContextUtil.getCurrentUserId());
				if (BeanUtils.isNotEmpty(taskOpinion))
					processRun.setRecover(ProcessRun.RECOVER);
			}
		}
		ModelAndView mv = this.getAutoView().addObject("processRunList", list);
		return mv;
	}

	/**
	 * 删除流程实例扩展
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除流程实例扩展", execOrder = ActionExecOrder.BEFORE, detail = "删除流程实例" + "<#list StringUtils.split(runId,\",\") as item>" + "<#assign entity=processRunService.getById(Long.valueOf(item))/>" + "【${entity.subject}】" + "</#list>")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultMessage message = null;
		String preUrl = RequestUtil.getPrePage(request);
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "runId");
			processRunService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除流程实例成功");
		} catch (Exception e) {
			message = new ResultMessage(ResultMessage.Fail, "删除流程实例失败");
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	/**
	 * 删除流程实例扩展
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("delByRunId")
	@ResponseBody
	@Action(description = "删除流程实例扩展", execOrder = ActionExecOrder.BEFORE, detail = "删除流程实例" + "<#list StringUtils.split(runId,\",\") as item>" + "<#assign entity=processRunService.getById(Long.valueOf(item))/>" + "【${entity.subject}】" + "</#list>")
	public ResultVo delByRunId(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			String preUrl = RequestUtil.getPrePage(request);
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "runId");
			processRunService.delByIds(lAryId);
			Map data = new HashMap();
			data.put("preUrl",preUrl);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"删除流程实例成功",data);
		} catch (Exception e) {
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"删除流程实例失败",e.getMessage());
		}
	}
	
	/**
	 * 异步删除流程实例扩展
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("delAjax")
	@ResponseBody
	@Action(description = "删除流程实例扩展", execOrder = ActionExecOrder.BEFORE, detail = "删除流程实例" + "<#list StringUtils.split(runId,\",\") as item>" + "<#assign entity=processRunService.getById(Long.valueOf(item))/>" + "【${entity.subject}】" + "</#list>")
	public ResultMessage delAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "runId");
			processRunService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除流程实例成功");
		} catch (Exception e) {
			message = new ResultMessage(ResultMessage.Fail, "删除流程实例失败");
		}
		return message;
	}

	/**
	 * 编辑流程实例
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description = "编辑流程实例扩展")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		Long runId = RequestUtil.getLong(request, "runId");
		String returnUrl = RequestUtil.getPrePage(request);
		ProcessRun processRun = null;
		if (runId != 0) {
			processRun = processRunService.getById(runId);
		} else {
			processRun = new ProcessRun();
		}
		return getAutoView().addObject("processRun", processRun).addObject("returnUrl", returnUrl);
	}

	/**
	 * 取得流程实例扩展明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description = "查看流程实例扩展明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long runId = RequestUtil.getLong(request, "runId", 0L);
		String taskId = RequestUtil.getString(request, "taskId");
		String preUrl = RequestUtil.getPrePage(request);
		ProcessRun processRun = null;
		// String actInstId="";
		if (runId != 0L) {
			processRun = processRunService.getById(runId);
		} else {
			TaskEntity task = bpmService.getTask(taskId);
			processRun = processRunService.getByActInstanceId(new Long(task.getProcessInstanceId()));
		}
	
		if (processRun == null)
			throw new Exception("实例不存在");
		List<HistoricTaskInstance> hisTasks = bpmService.getHistoryTasks(processRun.getActInstId());
		return getAutoView().addObject("processRun", processRun)
				.addObject("isReturn", request.getParameter("isReturn"))
				.addObject("hisTasks", hisTasks).addObject("returnUrl", preUrl);
	}

	/**
	 * 取得流程实例扩展明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("detail")
	@Action(description = "查看流程实例扩展明细")
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long runId = RequestUtil.getLong(request, "runId", 0L);
		String preUrl = RequestUtil.getPrePage(request);
		ProcessRun processRun = null;
		// String actInstId="";
		if (runId != 0L) {
			processRun = processRunService.getById(runId);
		}

		// List<HistoricTaskInstance> hisTasks=bpmService.getHistoryTasks(processRun.getActInstId());
		return getAutoView().addObject("processRun", processRun).addObject("returnUrl", preUrl);
	}

	/**
	 * 任务办理页面的 流程示意图对话框
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("processImage")
	public ModelAndView processImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = getAutoView();

		String action = request.getParameter("action");
		String runId = request.getParameter("runId");
		String actInstanceId = null;
		ProcessRun processRun = null;
		if (StringUtils.isNotEmpty(runId)) {
			processRun = processRunService.getById(new Long(runId));
			actInstanceId = processRun.getActInstId();
		} else {
			actInstanceId = request.getParameter("actInstId");
			processRun = processRunService.getByActInstanceId(new Long(actInstanceId));

		}
		String defXml = bpmService.getDefXmlByProcessDefinitionId(processRun.getActDefId());
		ExecutionEntity executionEntity = bpmService.getExecution(actInstanceId);

		if (executionEntity != null && executionEntity.getSuperExecutionId() != null) {
			ExecutionEntity superExecutionEntity = bpmService.getExecution(executionEntity.getSuperExecutionId());
			modelAndView.addObject("superInstanceId", superExecutionEntity.getProcessInstanceId());
		}

		String key= DigestUtils.md5Hex(defXml.getBytes());
		ShapeMeta shapeMeta= (ShapeMeta) redisTemplate.opsForValue().get(BpmDefinition.PREFIX_TRANSGRAPH+key);
		if (shapeMeta==null){
			shapeMeta = BpmUtil.transGraph(defXml);
			redisTemplate.opsForValue().set(BpmDefinition.PREFIX_TRANSGRAPH+key,shapeMeta);
		}
		modelAndView.addObject("notShowTopBar", request.getParameter("notShowTopBar"))
		.addObject("defXml", defXml)
		.addObject("processInstanceId", actInstanceId)
		.addObject("shapeMeta", shapeMeta).addObject("processRun", processRun).addObject("action", action).addObject("definitionId",processRun.getActDefId());
		return modelAndView;

	}

	/**
	 * 查看子流程。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("subFlowImage")
	public ModelAndView subFlowImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String subProcessDefinitionId = null;
		List<String> subProcessInstanceId = new ArrayList<String>();
		String subDefXml = null;
		String actInstanceId = request.getParameter("processInstanceId");
		String processDefinitionId = request.getParameter("processDefinitionId");
		String nodeId = request.getParameter("nodeId");
		// 子流程是否已经运行 0-未运行，1-已运行
		int subProcessRun = 0;
		// 取得子外部子流程的key.
		String subFlowKey = null;
		String actDefId = null;
		if (StringUtil.isNotEmpty(actInstanceId)) {
			actDefId = processRunService.getByActInstanceId(new Long(actInstanceId)).getActDefId();
		} else if (StringUtil.isNotEmpty(processDefinitionId)) {
			actDefId = processDefinitionId;
		}

		Map<String, FlowNode> flowNodeMap = NodeCache.getByActDefId(actDefId);
		Iterator<Entry<String, FlowNode>> entrySet = flowNodeMap.entrySet().iterator();
		while (entrySet.hasNext()) {
			Entry<String, FlowNode> entry = entrySet.next();
			String flowNodeId = entry.getKey();
			if (flowNodeId.equals(nodeId)) {
				FlowNode flowNode = entry.getValue();
				subFlowKey = flowNode.getAttribute("subFlowKey");
				break;
			}
		}
		// 取得外部子流程的定义
		BpmDefinition subBpmDefinition = bpmDefinitionService.getMainDefByActDefKey(subFlowKey);
		if (subBpmDefinition.getActDeployId() != null) {
			subDefXml = bpmService.getDefXmlByDeployId(subBpmDefinition.getActDeployId().toString());
		} else {
			subDefXml = BpmUtil.transform(subBpmDefinition.getDefKey(), subBpmDefinition.getSubject(), subBpmDefinition.getDefXml());
		}

		// 取得所有的子流程实例
		List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery().superProcessInstanceId(actInstanceId).list();
		if (BeanUtils.isNotEmpty(historicProcessInstances)) {
			// 筛选当选节点的子流程
			for (HistoricProcessInstance instance : historicProcessInstances) {
				String procDefId = instance.getProcessDefinitionId();
				BpmDefinition bpmDef = bpmDefinitionService.getByActDefId(procDefId);
				if (bpmDef.getDefKey().equals(subFlowKey)) {
					subProcessInstanceId.add(instance.getId());
					subProcessRun = 1;
				}
			}
		}
		if (subProcessRun == 0) {
			subProcessDefinitionId = subBpmDefinition.getActDefId();
		}

		ShapeMeta subShapeMeta = BpmUtil.transGraph(subDefXml);
		ModelAndView modelAndView = getAutoView();
		modelAndView.addObject("defXml", subDefXml);
		modelAndView.addObject("subProcessRun", subProcessRun);
		if (subProcessRun == 0) {
			modelAndView.addObject("processDefinitionId", subProcessDefinitionId);
		} else {
			modelAndView.addObject("processInstanceIds", subProcessInstanceId);
		}
		modelAndView.addObject("shapeMeta", subShapeMeta);
		return modelAndView;

	}

	/**
	 * 根据流程实例id获取流程的状态。
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("getFlowStatusByInstanceId")
	@ResponseBody
	public List<TaskNodeStatus> getFlowStatusByInstanceId(HttpServletRequest request) {
		String instanceId = RequestUtil.getString(request, "instanceId");
		List<TaskNodeStatus> tnss = bpmService.getNodeCheckStatusInfo(instanceId);
		List<TaskNodeStatus> list=new ArrayList<TaskNodeStatus>();
		//当时子流程的时候，父节点的数据也会带出来的，因为有时父节点的ID跟子节点的ID会一样的（都是UseTask1之类），会导致数据错误
		for(TaskNodeStatus tns:tnss){
			if(tns.getActInstId().equals(instanceId)){
				list.add(tns);
			}
		}
		return list;
	}

	/**
	 * 显示流程实例任务中某个任务节点上的执行人员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	/**
	 * 显示流程实例任务中某个任务节点上的执行人员。
	 * 
	 * <pre>
	 * 根据节点ID和流程实例ID获取节点的执行人。
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("taskUser")
	public ModelAndView taskUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 当前用户执行人员ID
		Set<TaskExecutor> assignees = new HashSet<TaskExecutor>();
		// 当前节点的候选执行人员ID
		Set<TaskExecutor> candidateUsers = new HashSet<TaskExecutor>();

		String nodeId = request.getParameter("nodeId");
		String processInstanceId = request.getParameter("processInstanceId");

		ModelAndView modelAndView = getAutoView();
		modelAndView.addObject("assignees", assignees).addObject("candidateUsers", candidateUsers);
		boolean found = false;

		// 1.检查该流程实例中的当前任务列表，若nodeId对应的任务存在，即直接从该任务里获取指定的人员或候选用户
		List<ProcessTask> taskList = bpmService.getTasks(processInstanceId);
		// nodeId对应的任务节点是否有任务实例
		for (ProcessTask task : taskList) {
			if (task.getTaskDefinitionKey().equals(nodeId)) {
				found = true;
				if (ServiceUtil.isAssigneeNotEmpty(task.getAssignee())) {// 存在指定的用户
					Long assignee = Long.parseLong(task.getAssignee());
					SysUser sysUser = sysUserService.getById(assignee);
//					assignees.add(TaskExecutor.getTaskUser(task.getAssignee(), sysUser.getFullname()));
					assignees.add(TaskExecutor.getTaskUser(task.getAssignee(), ContextSupportUtil.getUsername(sysUser)));
				} else {// 获取该任务的候选用户列表
					Set<TaskExecutor> cUIds = taskUserService.getCandidateExecutors(task.getId());
					for (TaskExecutor taskExecutor : cUIds) {
						candidateUsers.add(taskExecutor);
					}
				}
			}
		}
		if (found)
			return modelAndView;
		// 2.检查审批历史表里是否存在该节点的审核人
		List<TaskOpinion> list = taskOpinionService.getByActInstIdTaskKey(new Long(processInstanceId), nodeId);
		for (TaskOpinion taskOpinion : list) {
			if (taskOpinion.getExeUserId() != null) {
				assignees.add(TaskExecutor.getTaskUser(taskOpinion.getExeUserId().toString(), taskOpinion.getExeFullname()));
			}
		}
		return modelAndView;
	}



	/**
	 * 我的流程草稿列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("myForm")
	@Action(description = "我的流程草稿列表")
	public ModelAndView myForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "processRunItem");
		List<ProcessRun> list = getMyDraft(filter);
		ModelAndView mv = this.getAutoView().addObject("processRunList", list);
		return mv;
	}
	
	/**
	 * 获取草稿列表JSON。
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("myDraftJson")
	@ResponseBody
	public ResultVo myDraftJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request,true);
		try {
			flowCateFilter(filter);
			//处理表单自定义字段查询
			processFormQuery(request,filter);
			PageList<ProcessRun> list = (PageList<ProcessRun>) getMyDraft(filter);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "草稿列表查询成功", PageUtil.getPageVo(list));
		}catch (Exception e){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"草稿列表查询失败",e);
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
			Set<Long> typeIdList = com.suneee.platform.service.bpm.util.BpmUtil.getTypeIdList(globalTypeList);
			filter.addFilter("typeIds", typeIdList);
		}
	}
	
	
	private List<ProcessRun> getMyDraft(QueryFilter filter){
		Long userId = ContextUtil.getCurrentUserId();
		filter.addFilter("userId", userId);
		PageList<ProcessRun> list = (PageList<ProcessRun>) processRunService.getMyDraft(filter);
		return list;
	}

	/**
	 * 删除流程草稿同时删除业务数据
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("delDraft")
	@Action(description = "删除流程草稿", execOrder = ActionExecOrder.BEFORE, detail = "删除流程草稿" + "<#list StringUtils.split(runId,\",\") as item>" + "<#assign entity=processRunService.getById(Long.valueOf(item))/>" + "【${entity.subject}】" + "</#list>")
	public void delDraft(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultMessage message = null;
		Long[] runIdAry = RequestUtil.getLongAryByStr(request, "runId");
		Boolean isFromMobile =RequestUtil.getBoolean(request,"isMobile", false);
		String preUrl = RequestUtil.getPrePage(request);
		try {
			for (long runId : runIdAry) {
				ProcessRun processRun = processRunService.getById(runId);
				String dsAlias = processRun.getDsAlias();
				String tableName = processRun.getTableName();
				String businessKey = processRun.getBusinessKey();
				if (StringUtil.isNotEmpty(tableName)) {
					if (StringUtil.isEmpty(dsAlias) || DataSourceUtil.DEFAULT_DATASOURCE.equals(dsAlias)) {
						if(!tableName.startsWith(TableModel.CUSTOMER_TABLE_PREFIX)){
							tableName=TableModel.CUSTOMER_TABLE_PREFIX+tableName;
						}
						bpmFormHandlerService.delByIdTableName(businessKey, tableName);
					} else {
						bpmFormHandlerService.delByDsAliasAndTableName(dsAlias, tableName, businessKey);
					}
				}
				bpmRunLogService.addRunLog(runId, BpmRunLog.OPERATOR_TYPE_DELETEFORM, "删除草稿");
				processRunService.delById(runId);
			}
			message = new ResultMessage(ResultMessage.Success, "删除流程草稿成功");
		} catch (Exception e) {
			e.printStackTrace();
			message = new ResultMessage(ResultMessage.Fail, "删除流程草稿失败：" + e.getMessage());
		}
		if(isFromMobile){
			writeResultMessage(response.getWriter(), message); 
			return;
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	@RequestMapping("checkForm")
	@Action(description = "检查草稿表单是否发生变化")
	@ResponseBody
	public boolean checkForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long runId = RequestUtil.getLong(request, "runId");
		ProcessRun processRun = processRunService.getById(runId);
		Long formDefId = processRun.getFormDefId();
		if (formDefId == 0L)
			return false;
		BpmFormDef bpmFormDef = bpmFormDefService.getById(formDefId);
		BpmFormDef defaultFormDef = bpmFormDefService.getDefaultPublishedByFormKey(bpmFormDef.getFormKey());
		if (defaultFormDef.getFormDefId().equals(formDefId)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 复制我的草稿
	 */
	@RequestMapping("copyDraft")
	@Action(description = "复制草稿", execOrder = ActionExecOrder.AFTER, detail = "复制草稿" + "<#assign entity=processRunService.getById(Long.valueOf(runId))/>" + "【${entity.subject}】" + "<#if isSuccess>成功<#else>失败</#if>")
	public void copyDraft(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long runId = RequestUtil.getLong(request, "runId");
		boolean isSuccess = true;
		try {
			processRunService.copyDraft(runId);
			writeResultMessage(response.getWriter(), new ResultMessage(ResultMessage.Success, "复制草稿成功！"));
			isSuccess = true;
		} catch (Exception e) {
			isSuccess = false;
			e.printStackTrace();
			writeResultMessage(response.getWriter(), new ResultMessage(ResultMessage.Fail, "复制草稿失败！" + e.getMessage()));
		}
		SysAuditThreadLocalHolder.putParamerter("isSuccess", isSuccess);
	}

	/**
	 * 我的请求列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("myRequestList")
	@Action(description = "查看我的请求列表")
	public ModelAndView myRequestList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "processRunItem");
		filter.addFilter("creatorId", ContextUtil.getCurrentUserId());
		String nodePath = RequestUtil.getString(request, "nodePath");
		if (StringUtils.isNotEmpty(nodePath))
			filter.getFilters().put("nodePath", nodePath + "%");
		List<ProcessRun> list = processRunService.getMyRequestList(filter);
		
		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
		
		ModelAndView mv = this.getAutoView().addObject("processRunList", list).addObject("hasGlobalFlowNo", hasGlobalFlowNo);
		return mv;
	}

	/**
	 * 我的请求办结列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("myRequestCompletedList")
	@Action(description = "查看我的请求办结列表")
	public ModelAndView myRequestCompletedList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "processRunItem");
		filter.addFilter("creatorId", ContextUtil.getCurrentUserId());
		String nodePath = RequestUtil.getString(request, "nodePath");
		if (StringUtils.isNotEmpty(nodePath))
			filter.getFilters().put("nodePath", nodePath + "%");
		List<ProcessRun> list = processRunService.getMyRequestCompletedList(filter);

		ModelAndView mv = this.getAutoView().addObject("processRunList", list);
		return mv;
	}
	
	/**
	 * 已办办结事宜列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("alreadyCompletedMattersList")
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
		return mv;
	}

	
	
	
	/**
	 * 我的请求列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("myRequest")
	@Action(description = "查看我的请求列表")
	public ModelAndView myRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long porIndex = RequestUtil.getLong(request, "porIndex");
		Long tabIndex = RequestUtil.getLong(request, "tabIndex");
		ModelAndView mv = this.getAutoView().addObject("porIndex", porIndex).addObject("tabIndex", tabIndex);
		return mv;
	}

	/**
	 * 我的办结
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("myCompletedList")
	@Action(description = "查看我的办结列表")
	public ModelAndView myCompletedList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "processRunItem");
		filter.addFilter("creatorId", ContextUtil.getCurrentUserId());
		String nodePath = RequestUtil.getString(request, "nodePath");
		if (StringUtils.isNotEmpty(nodePath))
			filter.getFilters().put("nodePath", nodePath + "%");
		List<ProcessRun> list = processRunService.getMyCompletedList(filter);
		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
		
		return this.getAutoView().addObject("processRunList", list).addObject("hasGlobalFlowNo", hasGlobalFlowNo);
	}

	/**
	 * 查看已办事宜流程列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("alreadyMattersList")
	@Action(description = "查看已办事宜流程列表")
	public ModelAndView alreadyMattersList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "processRunItem");
		String nodePath = RequestUtil.getString(request, "nodePath");// 右侧流程分类
		if (StringUtils.isNotEmpty(nodePath))
			filter.getFilters().put("nodePath", nodePath + "%");

		filter.addFilter("assignee", ContextUtil.getCurrentUserId().toString());// 用户id
		List<ProcessRun> list = processRunService.getAlreadyMattersList(filter);

		for (ProcessRun processRun : list) {
			if (processRun.getStatus().shortValue() != ProcessRun.STATUS_FINISH.shortValue()) {
				// 1.查找当前用户是否有最新审批的任务
				TaskOpinion taskOpinion = taskOpinionService.getLatestUserOpinion(processRun.getActInstId(), ContextUtil.getCurrentUserId());
				if (BeanUtils.isNotEmpty(taskOpinion))
					processRun.setRecover(ProcessRun.STATUS_RECOVER);
			}
		}
		
		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);

		return this.getAutoView().addObject("processRunList", list).addObject("hasGlobalFlowNo", hasGlobalFlowNo);
	}

	/**
	 * 查看办结事宜流程列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("completedMattersList")
	@Action(description = "查看办结事宜流程列表")
	public ModelAndView completedMattersList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "processRunItem");

		String nodePath = RequestUtil.getString(request, "nodePath");
		if (StringUtils.isNotEmpty(nodePath))
			filter.getFilters().put("nodePath", nodePath + "%");

		filter.addFilter("assignee", ContextUtil.getCurrentUserId().toString());

		List<ProcessRun> list = processRunService.getCompletedMattersList(filter);

		for (ProcessRun processRun : list) {
			BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(processRun.getActDefId());
			if (BeanUtils.isNotEmpty(bpmDefinition) && bpmDefinition.getIsPrintForm() == 1) {
				processRun.setIsPrintForm(bpmDefinition.getIsPrintForm());
			}
		}
		
		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);

		ModelAndView mv = this.getAutoView().addObject("processRunList", list).addObject("hasGlobalFlowNo", hasGlobalFlowNo);
		return mv;
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

		// 通过defid和nodeId获取联动设置
		List<BpmGangedSet> bpmGangedSets = bpmGangedSetService.getByDefId(processRun.getDefId());
		JSONArray gangedSetJarray = (JSONArray) JSONArray.fromObject(bpmGangedSets);

		return this.getAutoView().addObject("bpmDefinition", bpmDefinition).addObject("processRun", processRun)
					.addObject("form", form).addObject("isPrintForm", isPrintForm).addObject("isExtForm", isExtForm)
					.addObject("isFirst", isFirst).addObject("isCanRedo", isCanRedo)
					.addObject("isFinishedDiver", isFinishedDiver).addObject("isCopy", isCopy).addObject(RequestUtil.RETURNURL, preUrl)
					.addObject("hasGlobalFlowNo", hasGlobalFlowNo).addObject("bpmGangedSets",gangedSetJarray);

	}

	/**
	 * 需要导出流程的详细信息
	 *
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("flowInfo")
	public ModelAndView flowInfo(HttpServletRequest request) throws Exception {
		Long runId = RequestUtil.getLong(request,"runId");
		String ctxPath = request.getContextPath();
		ProcessRun processRun = processRunService.getById(runId);
		String form = processRunService.getFormDetailByRunId(runId, ctxPath);
		boolean isExtForm = false;
		if (processRun.getFormDefId() == 0L && StringUtil.isNotEmpty(processRun.getBusinessUrl())) {
			isExtForm = true;
		}

		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);

		//获取审批历史
		//取得关联的流程实例ID
		List<TaskOpinion> list = taskOpinionService.getByRunId(runId);
		//设置代码执行人
		list = taskOpinionService.setTaskOpinionExecutor(list);
		JSONArray jsonArray=formatTaskOpinionList(list);
		String authId= java.util.Base64.getEncoder().encodeToString(request.getSession().getId().getBytes());

		return this.getAutoView().addObject("processRun", processRun)
				.addObject("form", form).addObject("isExtForm", isExtForm).addObject("hasGlobalFlowNo",hasGlobalFlowNo).addObject("taskOpinionList",jsonArray).addObject("authId",authId);

	}

	private static String PDF_ROOT=File.separator+"pdf";
	/**
	 * 导出pdf文档
	 * @param request
	 * @return
	 */
	@RequestMapping("downloadToPdf")
	@ResponseBody
	@Action(description = "导出流程成PDF文档", exectype = "管理日志", detail = "导出流程【${subject}】")
	public ResultVo downloadToPdf(HttpServletRequest request) throws Exception {
		Map<String,Object> data=new HashMap<String, Object>();
		Long[] runIds = RequestUtil.getLongAryByStr(request,"runId");
		boolean isPendingMatter=RequestUtil.getBoolean(request,"isPendingMatter",false);
		if (runIds.length == 0) {
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "runId参数为空");
		}
		ZipArchiveOutputStream zipArchiveOutputStream = null;
		String zipPath = PDF_ROOT + File.separator + System.currentTimeMillis()+ RandomUtils.nextInt(1000,9999) + ".zip";
		File zipFile = new File(AppUtil.getRealPath(zipPath));
		if (!zipFile.getParentFile().exists()){
			zipFile.getParentFile().mkdirs();
		}
		if (!zipFile.createNewFile()){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "系统没有可写权限，无法创建zip文件！");
		}
		zipArchiveOutputStream = new ZipArchiveOutputStream(zipFile);
		zipArchiveOutputStream.setUseZip64(Zip64Mode.AsNeeded);
		String tempPath = AppUtil.getRealPath(PDF_ROOT + File.separator+"temp"+File.separator + ContextUtil.getCurrentUserId() + "_" + System.currentTimeMillis());
		File destTempDir = new File(tempPath);
		if (!destTempDir.exists()) {
			destTempDir.mkdirs();
		}
		request.setAttribute("tempPath",tempPath);
		request.setAttribute("pdfWebUrl","/platform/bpm/processRun/flowInfo.ht");
		boolean isOpenEntry=false;
		try {
			for (Long runId:runIds){
				ProcessRun processRun=null;
				if (isPendingMatter){
					processRun=processRunService.getByTaskId(runId.toString());
				}else {
					processRun= processRunService.getById(runId);
				}
				if (processRun == null) {
					log.error("runId("+runId+")参数不存在!");
					continue;
				}
				Map<String,Object> pdfResult=exportPdfFile(request,processRun);
				if ((Boolean) pdfResult.get("isSuccess")==false){
					log.error("无法导出PDF文件,runId为"+runId);
					continue;
				}
				File pdfFile=new File((String) pdfResult.get("outputPath"));
				ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(pdfFile,(String) pdfResult.get("dir")+File.separator+(String) pdfResult.get("filename"));
				zipArchiveOutputStream.putArchiveEntry(zipArchiveEntry);
				FileInputStream pdfStream=new FileInputStream(pdfFile);
				writeZipData(pdfStream,zipArchiveOutputStream);
				if (pdfStream!=null){
					pdfStream.close();
				}
				isOpenEntry=true;
				//获取流程表单内容
				String form=processRunService.getFormDetailByRunId(processRun.getRunId(), request.getContextPath());
				Document formDoc = Jsoup.parse(form);
				String dir= (String) pdfResult.get("dir");
				exportAttachFile(formDoc,zipArchiveOutputStream,dir);
				exportOfficeFile(formDoc,zipArchiveOutputStream,dir);
			}
		}catch (Exception e){
			e.printStackTrace();
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "导出pdf文件失败");
		}finally {
			if (isOpenEntry){
				zipArchiveOutputStream.closeArchiveEntry();
			}
			zipArchiveOutputStream.finish();
			//关闭输出流
			if (null != zipArchiveOutputStream) {
				try {
					zipArchiveOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			FileUtils.deleteDirectory(destTempDir);
		}
		data.put("url",zipPath);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"导出PDF成功！",data);
	}

	private Map<String,Object> exportPdfFile(HttpServletRequest request,ProcessRun processRun){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		StringBuilder cmd = new StringBuilder("wkhtmltopdf ");
		cmd.append(" --javascript-delay 2000");
		cmd.append(" --custom-header runtimeEnv "+ com.suneee.eas.common.utils.ContextUtil.getRuntimeEnv());
		cmd.append(" --cookie SESSION " + java.util.Base64.getEncoder().encodeToString(request.getSession().getId().getBytes()));
		cmd.append(" --post runId " + processRun.getRunId());
		cmd.append(" " + getServerUrl(request) + request.getAttribute("pdfWebUrl"));

		String pdfName = processRun.getGlobalFlowNo() + "_" + processRun.getSubject() + ".pdf";
		String dir = processRun.getGlobalFlowNo() + "_" + processRun.getProcessName();
		String tempPath= (String) request.getAttribute("tempPath");
		String outputPath = tempPath+File.separator+processRun.getRunId() + ".pdf";
		cmd.append(" " + outputPath);
		logger.info("执行导出pdf操作：" + cmd.toString());
		boolean isSuccess = false;
		try {
			Process ps=Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",cmd.toString()});
			//windows平台pdf导出支持
			//Process ps=Runtime.getRuntime().exec(new String[]{"cmd.exe","/C",cmd.toString()});
			ps.waitFor();
			SequenceInputStream sis = new SequenceInputStream(ps.getInputStream(), ps.getErrorStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(sis, "gbk"));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.replaceAll("\"", "").trim().equals("Done")) {
					isSuccess = true;
				}
				sb.append(line + "\n");
			}
			String result = sb.toString();
			logger.info("导出pdf文件执行结果：" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("isSuccess",isSuccess);
		if (isSuccess){
			resultMap.put("dir",dir);
			resultMap.put("filename",pdfName);
			resultMap.put("outputPath",outputPath);
		}
		return resultMap;
	}
	private Map<String,Object> exportPdfFile(HttpServletRequest request,ProcessTask processTask){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		StringBuilder cmd = new StringBuilder("wkhtmltopdf ");
		cmd.append(" --javascript-delay 2000");
		cmd.append(" --custom-header runtimeEnv "+ com.suneee.eas.common.utils.ContextUtil.getRuntimeEnv());
		cmd.append(" --cookie SESSION " + java.util.Base64.getEncoder().encodeToString(request.getSession().getId().getBytes()));
		cmd.append(" --post runId " + processTask.getRunId());
		cmd.append(" " + getServerUrl(request) + request.getAttribute("pdfWebUrl"));

		String pdfName = processTask.getGlobalFlowNo() + "_" + processTask.getSubject() + ".pdf";
		String dir = processTask.getGlobalFlowNo() + "_" + processTask.getProcessName();
		String tempPath= (String) request.getAttribute("tempPath");
		String outputPath = tempPath+File.separator+processTask.getRunId() + ".pdf";
		cmd.append(" " + outputPath);
		logger.info("执行导出pdf操作：" + cmd.toString());
		boolean isSuccess = false;
		try {
//			Process ps=Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",cmd.toString()});
			//windows平台pdf导出支持
			Process ps=Runtime.getRuntime().exec(new String[]{"cmd.exe","/C",cmd.toString()});
			ps.waitFor();
			SequenceInputStream sis = new SequenceInputStream(ps.getInputStream(), ps.getErrorStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(sis, "gbk"));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.replaceAll("\"", "").trim().equals("Done")) {
					isSuccess = true;
				}
				sb.append(line + "\n");
			}
			String result = sb.toString();
			logger.info("导出pdf文件执行结果：" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("isSuccess",isSuccess);
		if (isSuccess){
			resultMap.put("dir",dir);
			resultMap.put("filename",pdfName);
			resultMap.put("outputPath",outputPath);
		}
		return resultMap;
	}

	/**
	 * 写入数据到zip文件中
	 * @param inputStream
	 * @param zipArchiveOutputStream
	 * @throws IOException
	 */
	private void writeZipData(InputStream inputStream,ZipArchiveOutputStream zipArchiveOutputStream) throws IOException {
		byte[] buffer = new byte[1024 * 5];
		int len = -1;
		while((len = inputStream.read(buffer)) != -1) {
			//把缓冲区的字节写入到ZipArchiveEntry
			zipArchiveOutputStream.write(buffer, 0, len);
		}
	}

	/**
	 * 导出附件文件
	 * @param formDoc
	 * @param zipArchiveOutputStream
	 */
	private void exportAttachFile(Document formDoc, ZipArchiveOutputStream zipArchiveOutputStream,String dir) throws Exception {
		Elements attachs=formDoc.select("[ctltype=attachment]");
		for (Element attachItem:attachs){
			String val=attachItem.val();
			if (StringUtil.isEmpty(val)){
				continue;
			}
			val=val.replaceAll("￥@@￥","\"");
			com.alibaba.fastjson.JSONArray jsonArr= com.alibaba.fastjson.JSONArray.parseArray(val);
			for (int i=0;i<jsonArr.size();i++){
				JSONObject jsonItem=jsonArr.getJSONObject(i);
				SysFile sysFile=sysFileService.getById(jsonItem.getLong("id"));
				if (BeanUtils.isEmpty(sysFile)){
					continue;
				}
				ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(dir+File.separator+"附件"+File.separator+sysFile.getFileName()+"."+sysFile.getExt());
				zipArchiveOutputStream.putArchiveEntry(zipArchiveEntry);
				sysFileService.downloadFile(sysFile, zipArchiveOutputStream,false);
			}
		}
	}

	/**
	 * 导出office文件
	 * @param formDoc
	 * @param zipArchiveOutputStream
	 * @throws Exception
	 */
	private void exportOfficeFile(Document formDoc, ZipArchiveOutputStream zipArchiveOutputStream,String dir) throws Exception {
		Elements offices=formDoc.select("[controltype=office]");
		for (Element officeItem:offices){
			String val=officeItem.val();
			if (StringUtil.isEmpty(val)){
				continue;
			}
			SysFile sysFile=sysFileService.getById(Long.valueOf(val));
			if (BeanUtils.isEmpty(sysFile)){
				continue;
			}
			ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(dir+File.separator+"office"+File.separator+sysFile.getFileName()+"."+sysFile.getExt());
			zipArchiveOutputStream.putArchiveEntry(zipArchiveEntry);
			sysFileService.downloadFile(sysFile, zipArchiveOutputStream,false);
		}
	}

	/**
	 * <pre>
	 * 把taskOpinion封装成jsonArray对象，组成结构:
	 * 思路:
	 * 按照时间排序，如果i上的taskName跟i+1上的一致就放在同一个json上
	 * eg:
	 * json:{taskName:name,list:[taskopinion1,taskopinion2,...]}
	 * </pre>
	 * @param list
	 * @return JSONArray
	 * @exception
	 * @since 1.0.0
	 */
	private JSONArray formatTaskOpinionList(List<TaskOpinion> list) {
		JSONArray jsonArray = new JSONArray();
		for (TaskOpinion to : list) {
			String tn = "";
			List<net.sf.json.JSONObject> tos = null;
			net.sf.json.JSONObject jsonObject = null;
			String taskName = to.getTaskName();
			if(StringUtil.isNotEmpty(to.getSuperExecution())){
				taskName=taskName+"【子流程】";
			}

			if (!jsonArray.isEmpty()) {
				jsonObject = jsonArray.getJSONObject(jsonArray.size() - 1);
				tn = jsonObject.getString("taskName");
				tos = (List<net.sf.json.JSONObject>) jsonObject.get("list");
			}

			if (tn.equals(taskName)) {
				net.sf.json.JSONObject jsonObj=convertOpinion(to);
				tos.add(jsonObj);
			} else {
				jsonObject = new net.sf.json.JSONObject();
				tos = new ArrayList<net.sf.json.JSONObject>();
				net.sf.json.JSONObject jsonObj=convertOpinion(to);
				tos.add(jsonObj);
				jsonObject.put("taskName", taskName);
				jsonObject.put("list", tos);
				jsonArray.add(jsonObject);
			}
		}
		return jsonArray;
	}
	private net.sf.json.JSONObject convertOpinion(TaskOpinion opinion){
		net.sf.json.JSONObject jsonObj=new net.sf.json.JSONObject();

		jsonObj.accumulate("opinionId", opinion.getOpinionId()) ;
		String taskName = opinion.getTaskName();
		if(StringUtil.isNotEmpty(opinion.getSuperExecution())){
			taskName=taskName+"【子流程】";
		}
		jsonObj.accumulate("taskName",taskName ) ;
		jsonObj.accumulate("exeFullname", opinion.getExeFullname()) ;
		jsonObj.accumulate("exeUserId", opinion.getExeUserId()) ;
		jsonObj.accumulate("status", opinion.getStatus()) ;
		jsonObj.accumulate("checkStatus", opinion.getCheckStatus()) ;
		jsonObj.accumulate("startTimeStr", opinion.getStartTimeStr()) ;
		jsonObj.accumulate("durTimeStr", opinion.getDurTimeStr()) ;
		jsonObj.accumulate("endTimeStr", opinion.getEndTimeStr()) ;
		jsonObj.accumulate("opinion", opinion.getOpinion()==null?"":opinion.getOpinion()) ;

		List<SysUser> users= opinion.getCandidateUsers();
		if(BeanUtils.isNotEmpty(users)){
			JSONArray ary=new JSONArray();
			for(SysUser user:users){
				net.sf.json.JSONObject userJson=new net.sf.json.JSONObject();
				userJson.accumulate("userId",user.getUserId());
//				userJson.accumulate("fullname",user.getFullname());
				userJson.accumulate("fullname",ContextSupportUtil.getUsername(user));
				ary.add(userJson);
			}
			jsonObj.accumulate("candidateUsers", ary) ;
		}


		return jsonObj;
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

	/**
	 * 逻辑删除流程实例。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("logicDelete")
	@Action(description = "逻辑删除流程实例")
	public void logicDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultMessage message = null;
		Long instanceId = RequestUtil.getLong(request, "instanceId");
		PrintWriter writer = response.getWriter();
		String memo = RequestUtil.getString(request, "opinion");
		if (instanceId != null && instanceId > 0) {
			try {
				ProcessRun processRun = bpmService.delProcessByInstanceId(instanceId, memo);
				Long runId = processRun.getRunId();
				String tmp = "逻辑删除了:" + processRun.getSubject() + ",删除原因!";
				bpmRunLogService.addRunLog(runId, BpmRunLog.OPERATOR_TYPE_DELETEINSTANCE, tmp);
				message = new ResultMessage(ResultMessage.Success, getText("controller.processRun.del.success"));
			} catch (Exception ex) {
				String msg = ExceptionUtil.getExceptionMessage(ex);
				message = new ResultMessage(ResultMessage.Fail, msg);
			}
		} else {
			message = new ResultMessage(ResultMessage.Fail, getText("controller.processRun.del.fail"));
		}
		writeResultMessage(writer, message);
	}

	/**
	 * update 2013-1-1 start 打印流程表单
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("printForm")
	public ModelAndView printForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long runId = RequestUtil.getLong(request, "runId");
		String printAlias=RequestUtil.getString(request,"printAlias");
		ProcessRun processRun = processRunService.getByHistoryId(runId);
		Long formId=processRun.getFormDefId();
		BpmFormDef formDef=bpmFormDefService.getById(formId);
		String formKey=formDef.getFormKey();
		String ctx = request.getContextPath();
		BpmPrintTemplate bpmPrintTemplate = null;
		if (StringUtil.isNotEmpty(printAlias)){
			bpmPrintTemplate=bpmPrintTemplateService.getByFormKeyAndAlias(formKey,printAlias);
		}
		if(BeanUtils.isEmpty(bpmPrintTemplate)){
			bpmPrintTemplate = bpmPrintTemplateService.getDefaultByFormKey(formKey);
		}
		if (BeanUtils.isEmpty(bpmPrintTemplate)) {
			bpmPrintTemplate = bpmPrintTemplateService.getDefaultPrintTemplateByFormKey(formKey);
			Long tableId = bpmPrintTemplate.getTableId();
			BpmFormTable bpmFormTable = bpmFormTableService.getById(tableId);
			String tableName = bpmFormTable.getTableName();
			String tableComment = bpmFormTable.getTableDesc();
			String template = FormUtil.getPrintFreeMarkerTemplate(bpmPrintTemplate.getHtml(), tableName, tableComment);
			bpmPrintTemplate.setTemplate(template);
			bpmPrintTemplate.setId(UniqueIdUtil.genId());
			bpmPrintTemplate.setIsDefault((short) 1);
			bpmPrintTemplateService.add(bpmPrintTemplate);
		}
		String html = bpmPrintTemplateService.parseTempalte(bpmPrintTemplate, processRun, ctx);
		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
		
		return getAutoView().addObject("html", html).addObject("hasGlobalFlowNo", hasGlobalFlowNo)
			.addObject("processRun", processRun);
	}

	@RequestMapping("getRefList")
	@Action(description = "流程参考")
	public ModelAndView getRefList(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Long defId = RequestUtil.getLong(request, "defId", 0);
		Integer type = RequestUtil.getInt(request, "type", 1);
		BpmDefinition bpmDefinition = bpmDefinitionService.getById(defId);
		Long creatorId = ContextUtil.getCurrentUserId();
		Integer instanceAmount = bpmDefinition.getInstanceAmount();
		if (instanceAmount == null || instanceAmount <= 0) {
			instanceAmount = 5;
		}

		List<ProcessRun> list = processRunService.getRefList(defId, creatorId, instanceAmount, type);
		return this.getAutoView().addObject("processRunList", list).addObject("type", type);

	}

	/**
	 * 根据流程实例id获取流程的状态。
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("getFlowStatusByInstanceIdAndNodeId")
	@ResponseBody
	public TaskNodeStatus getFlowStatusByInstanceIdAndNodeId(HttpServletRequest request) {
		String instanceId = RequestUtil.getString(request, "instanceId");
		String nodeId = RequestUtil.getString(request, "nodeId");
		TaskNodeStatus taskNodeStatus = bpmService.getNodeCheckStatusInfo(instanceId, nodeId);
		if (BeanUtils.isNotEmpty(taskNodeStatus.getTaskExecutorList())) {
			List<TaskExecutor> tempTaskExecutors = taskNodeStatus.getTaskExecutorList();
			for (TaskExecutor taskExecutor : tempTaskExecutors) {
				taskExecutor.setMainOrgName(getMainOrgName(taskExecutor.getExecuteId()));
			}
		}
		for (TaskOpinion taskOpinion : taskNodeStatus.getTaskOpinionList()) {
			if (!TaskOpinion.STATUS_CHECKING.equals(taskOpinion.getCheckStatus()))
				continue;
			Long taskId = taskOpinion.getTaskId();
			ProcessTask processTask = processRunService.getByTaskId(taskId);
			if(BeanUtils.isEmpty(processTask)){
				continue;
			}
			String assignee = processTask.getAssignee();
			if (ServiceUtil.isAssigneeNotEmpty(assignee)) {
				TaskExeStatus taskExeStatus = new TaskExeStatus();
				String fullname = ContextSupportUtil.getUsername(sysUserService.getById(new Long(processTask.getAssignee())));
				taskExeStatus.setExecutor(fullname);
				taskExeStatus.setExecutorId(assignee);
				taskExeStatus.setMainOrgName(getMainOrgName(assignee));
				boolean isRead = taskReadService.isTaskRead(Long.valueOf(processTask.getId()), Long.valueOf(assignee));
				taskExeStatus.setRead(isRead);
				taskOpinion.setTaskExeStatus(taskExeStatus);
			}
			// 获取候选人
			else {
				Set<TaskExecutor> set = taskUserService.getCandidateExecutors(processTask.getId());
				List<TaskExeStatus> candidateUserStatusList = new ArrayList<TaskExeStatus>();
				for (Iterator<TaskExecutor> it = set.iterator(); it.hasNext();) {
					TaskExecutor taskExe = it.next();
					TaskExeStatus taskExeStatus = new TaskExeStatus();
					taskExeStatus.setExecutorId(taskExe.getExecuteId().toString());
					taskExeStatus.setMainOrgName(getMainOrgName(taskExeStatus.getExecutorId()));
					taskExeStatus.setType(taskExe.getType());
					taskExeStatus.setCandidateUser(taskExe.getExecutor());
					String executorId = taskExe.getExecuteId();
					if (TaskExecutor.USER_TYPE_USER.equals(taskExe.getType())) {
						boolean isRead = taskReadService.isTaskRead(Long.valueOf(processTask.getId()), new Long(executorId));
						taskExeStatus.setRead(isRead);
					}
					candidateUserStatusList.add(taskExeStatus);
				}
				taskOpinion.setCandidateUserStatusList(candidateUserStatusList);
			}
		}
		return taskNodeStatus;
	}

	private String getMainOrgName(String userId) {
		List<SysUserOrg> orgs = sysUserOrgService.getOrgByUserId(Long.parseLong(userId));
		String mainOrgName = "";
		for (SysUserOrg org : orgs) {
			if (org.getIsPrimary().equals(SysUserOrg.PRIMARY_YES)) {
				mainOrgName = org.getOrgName();
				break;
			}
		}
		return mainOrgName;
	}

	/**
	 * 导出成word文档
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("downloadToWord")
	@Action(description = "导出流程成word文档", exectype = "管理日志", detail = "导出流程【${subject}】")
	public void downloadToWord(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.reset();
		response.setCharacterEncoding("utf-8");
		String agent = (String) request.getHeader("USER-AGENT");
		String form = RequestUtil.getString(request, "form");
		long runId = RequestUtil.getLong(request, "runId");
		ProcessRun processRun = processRunService.getById(runId);
		String subject = processRun.getSubject().replaceAll("<(?:(?:/([^>]+)>)|(?:!--([\\S|\\s]*?)-->)|(?:([^\\s/>]+)\\s*((?:(?:\"[^\"]*\")|(?:'[^']*')|[^\"'<>])*)/?>))", "");

		String attachPath = ServiceUtil.getBasePath();
		String name = UniqueIdUtil.genId() + ".doc";

		String fileName = createFilePath(attachPath + File.separator + "tmpdownload", name);

		boolean isIe = true;
		if (agent != null && agent.indexOf("MSIE") == -1 && agent.indexOf("Trident") == -1) {
			isIe = false;
		}
		// 生成文件
		genFile(form, fileName);

		// 下载文件
		donwload(fileName, response, subject, isIe);
		// 删除文件
		File file = new File(fileName);
		file.delete();

	}

	/**
	 * 生成文件夹
	 * 
	 * @param dir
	 * @param name
	 * @return
	 */
	private String createFilePath(String dir, String name) {
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return dir + File.separator + name;
	}

	/**
	 * 生成文件。
	 * 
	 * @param content
	 * @param fileName
	 */
	@SuppressWarnings("unused")
	private void genFile(String content, String fileName) {
		ByteArrayInputStream bais = null;
		FileOutputStream fos = null;
		POIFSFileSystem poifs = null;
		byte b[];
		try {
			String resultString = content.replaceAll("(?si)<script[\\s]*[^>]*>.*?</script>", "");
			b = resultString.getBytes("UTF-8");
			bais = new ByteArrayInputStream(b);
			poifs = new POIFSFileSystem();
			DirectoryEntry directory = poifs.getRoot();
			DocumentEntry documentEntry = directory.createDocument("WordDocument", bais);
			fos = new FileOutputStream(fileName);
			poifs.writeFilesystem(fos);

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bais.close();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 下载文件。
	 * 
	 * @param filePath
	 * @param response
	 * @param fileName
	 * @param isIE
	 */
	private void donwload(String filePath, HttpServletResponse response, String fileName, boolean isIE) {
		String filedisplay = fileName + ".doc";

		response.reset();
		response.setContentType("application/x-msdownload;charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		try {

			if (!isIE) {
				String enableFileName = "=?UTF-8?B?" + (new String(Base64.getBase64(filedisplay))) + "?=";
				response.setHeader("Content-Disposition", "attachment;filename=" + enableFileName);
			} else {
				filedisplay = URLEncoder.encode(filedisplay, "utf-8");
				response.addHeader("Content-Disposition", "attachment;filename=" + filedisplay);
			}

			OutputStream sops = response.getOutputStream();// 不同类型的文件对应不同的MIME类型
			InputStream ips = new FileInputStream(filePath);
			byte[] content = new byte[ips.available()];
			int length = -1;
			while ((length = ips.read(content)) != -1) {
				sops.write(content, 0, length); // 读入流,保存在Byte数组中
			}
			ips.close();
			sops.flush();
			sops.close();
		} catch (UnsupportedEncodingException e) {
			log.debug(e.getMessage(), e);

		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}

	}
	
	
	@RequestMapping("getForm")
	public ModelAndView getForm(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Long runId = RequestUtil.getLong(request, "runId", 0L);
		String ctxPath = request.getContextPath();
	
		String form = processRunService.getFormDetailByRunId(runId, ctxPath);
		ProcessRun processRun = processRunService.getById(runId);
		boolean isExtForm = false;
		if (processRun.getFormDefId() == 0L && StringUtil.isNotEmpty(processRun.getBusinessUrl())) {
			isExtForm = true;
		}

		// 通过defid和nodeId获取联动设置
		List<BpmGangedSet> bpmGangedSets = bpmGangedSetService.getByDefId(processRun.getDefId());
		JSONArray gangedSetJarray = (JSONArray) JSONArray.fromObject(bpmGangedSets);

		ModelAndView view = this.getAutoView().addObject("processRun", processRun)
				.addObject("isFormEmpty", StringUtil.isEmpty(form))
				.addObject("isExtForm", isExtForm)
				.addObject("form", form).addObject("bpmGangedSets",gangedSetJarray);
		return view;
	}

	/**
	 * 处理表单自定义查询字段
	 * @param request
	 * @param filter
	 */
	private void processFormQuery(HttpServletRequest request,QueryFilter filter){
		int isExt=RequestUtil.getInt(request,"isExt",0);
		if (isExt==0){
			return;
		}
		Long defId=RequestUtil.getLong(request,"defId");
		if (defId==null||defId==0){
			return;
		}
		List<BpmFormField> fieldList=formFieldService.getQueryVarByFlowDefId(defId);
		if (fieldList.size()==0){
			return;
		}
		BpmFormTable formTable=formTableService.getTableById(fieldList.get(0).getTableId());
		if (formTable==null){
			return;
		}
		Map<String,Object> params=new HashMap<String, Object>();
		for (BpmFormField formField:fieldList){
			String val=RequestUtil.getString(request,formField.getFieldName());
			if (StringUtil.isNotEmpty(val)){
				params.put(formField.getDbFieldName(),val);
			}
		}
		if (params.size()>0){
			filter.addFilter("formTable",formTable.getFactTableName());
			filter.addFilter("formParams",params);
		}
	}

}
