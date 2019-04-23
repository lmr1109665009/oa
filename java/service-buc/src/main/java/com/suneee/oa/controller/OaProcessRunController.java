package com.suneee.oa.controller;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.bpm.graph.ShapeMeta;
import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.bpm.util.BpmUtil;
import com.suneee.core.db.datasource.DataSourceUtil;
import com.suneee.core.table.TableModel;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.bpm.*;
import com.suneee.platform.model.system.SysProperty;
import com.suneee.platform.service.bpm.*;
import com.suneee.platform.service.form.BpmFormHandlerService;
import com.suneee.ucp.base.vo.ResultVo;
import net.sf.json.JSONArray;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 对象功能:流程实例扩展控制器类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2017-12-16 10:36:00
 * </pre>
 */
@Controller
@RequestMapping("/oa/oaProcessRun/")
public class OaProcessRunController extends BaseController {

	@Resource
	private ProcessRunService processRunService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private BpmProCopytoService bpmProCopytoService;
	@Resource
	private BpmGangedSetService bpmGangedSetService;
	@Resource
	private TaskOpinionService taskOpinionService;
	@Resource
	private BpmService bpmService;
	@Resource
	private BpmFormHandlerService bpmFormHandlerService;
	@Resource
	private BpmRunLogService bpmRunLogService;

	/**
	 * 返回流程的详细信息
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("info")
	@ResponseBody
	public ResultVo info(HttpServletRequest request, HttpServletResponse response) throws Exception {
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

		Map<String,Object> map = new HashMap<>();
		map.put("bpmDefinition",bpmDefinition);
		map.put("processRun",processRun);
		map.put("form",form);
		map.put("isPrintForm",isPrintForm);
		map.put("isExtForm",isExtForm);
		map.put("isFirst",isFirst);
		map.put("isCanRedo",isCanRedo);
		map.put("isFinishedDiver",isFinishedDiver);
		map.put("isCopy",isCopy);
		map.put("returnUrl",preUrl);
		map.put("hasGlobalFlowNo",hasGlobalFlowNo);
		map.put("bpmGangedSets",gangedSetJarray);

		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取详细信息成功！",map);
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


	private boolean isCopy(BpmDefinition bpmDefinition) {
		Short status = bpmDefinition.getStatus();
		if (BpmDefinition.STATUS_DISABLED.equals(status))
			return false;
		if (BpmDefinition.STATUS_INST_DISABLED.equals(status))
			return false;
		return true;
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
	 * 任务办理页面的 流程示意图对话框
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("processImage")
	@ResponseBody
	public ResultVo processImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> map = new HashMap<>();

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
			map.put("superInstanceId",superExecutionEntity.getProcessInstanceId());
		}

		ShapeMeta shapeMeta = BpmUtil.transGraph(defXml);

		map.put("notShowTopBar",request.getParameter("notShowTopBar"));
		map.put("defXml",defXml);
		map.put("processInstanceId",actInstanceId);
		map.put("shapeMeta",shapeMeta);
		map.put("processRun",processRun);
		map.put("action",action);
		map.put("definitionId",processRun.getActDefId());
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流程图数据成功！",map);
	}

	/**
	 * 删除流程草稿同时删除业务数据
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("delDraft")
	@ResponseBody
	@Action(description = "删除流程草稿", execOrder = ActionExecOrder.BEFORE, detail = "删除流程草稿" + "<#list StringUtils.split(runId,\",\") as item>" + "<#assign entity=processRunService.getById(Long.valueOf(item))/>" + "【${entity.subject}】" + "</#list>")
	public ResultVo delDraft(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultVo message = null;
		Long[] runIdAry = RequestUtil.getLongAryByStr(request, "runId");
		Boolean isFromMobile =RequestUtil.getBoolean(request,"isMobile", false);
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
			message = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			message = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败：" + e.getMessage());
		}
		return message;
	}
}
