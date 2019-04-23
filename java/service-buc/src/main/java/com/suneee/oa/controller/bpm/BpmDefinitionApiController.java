package com.suneee.oa.controller.bpm;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.graph.ShapeMeta;
import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.bpm.util.BpmUtil;
import com.suneee.core.page.PageList;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.controller.mobile.MobileBaseController;
import com.suneee.platform.model.bpm.*;
import com.suneee.platform.model.bpm.BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysTypeKey;
import com.suneee.platform.service.bpm.*;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysTypeKeyService;
import com.suneee.weixin.util.CommonUtil;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * <pre>
 * 对象功能:流程定义接口控制器类
 * 开发公司:深圳象翌
 * 开发人员:子华
 * 创建时间:2017-12-26 10:56:13
 * </pre>
 */
@Controller
@RequestMapping("/api/bpm/bpmDefinition/")
@Action(ownermodel = SysAuditModelType.PROCESS_MANAGEMENT)
public class BpmDefinitionApiController extends MobileBaseController {

	@Resource
	private GlobalTypeService globalTypeService;
	@Resource
	private BpmDefAuthorizeService bpmDefAuthorizeService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private BpmNodeButtonService bpmNodeButtonService;
	@Resource
	private BpmUserConditionService bpmUserConditionService;
	@Resource
	private BpmNodeSetService bpmNodeSetService;
	@Resource
	private BpmNodeRuleService bpmNodeRuleService;
	@Resource
	private BpmNodeScriptService bpmNodeScriptService;
	@Resource
	private TaskReminderService taskReminderService;
	@Resource
	private BpmService bpmService;
	@Resource
	private BpmReferDefinitionService bpmReferDefinitionService;
	@Resource
	private ProcessRunService processRunService;
	@Resource
	private SysTypeKeyService sysTypeKeyService;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;

	/**
	 * 获取流程定义列表
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看流程定义分页列表,含按分类查询所有流程")
	@ResponseBody
	public ResultVo list(HttpServletRequest request) throws Exception {
		QueryFilter filter = new QueryFilter(request, true);
		Long typeId = RequestUtil.getLong(request, "typeId", 0);
		if (typeId==0){
			List<Long> typeIdList=globalTypeService.getTypeIdsByEcode(CookieUitl.getCurrentEnterpriseCode());
			com.suneee.platform.service.bpm.util.BpmUtil.typeIdFilter(typeIdList);
			filter.addFilter("typeIdList", typeIdList);
		}else {
			GlobalType globalType = globalTypeService.getById(typeId);
			if (globalType != null) {
				filter.getFilters().put("nodePath",
						globalType.getNodePath() + "%");
			}
		}

		// 增加流程分管授权查询判断
		Long userId = ContextUtil.getCurrentUserId();
		String isNeedRight = "";
		Map<String, AuthorizeRight> authorizeRightMap = null;
		if (!ContextUtil.isSuperAdmin()) {
			isNeedRight = "yes";
			// 获得流程分管授权与用户相关的信息
			Map<String, Object> actRightMap = bpmDefAuthorizeService
					.getActRightByUserMap(userId,
							BPMDEFAUTHORIZE_RIGHT_TYPE.MANAGEMENT, true, true);
			// 获得流程分管授权与用户相关的信息集合的流程KEY
			String actRights = (String) actRightMap.get("authorizeIds");
			filter.addFilter("actRights", actRights);
			// 获得流程分管授权与用户相关的信息集合的流程权限内容
			authorizeRightMap = (Map<String, AuthorizeRight>) actRightMap
					.get("authorizeRightMap");
		}
		filter.addFilter("isNeedRight", isNeedRight);
		if(typeId == 0){
			List<Long> typeIdList=globalTypeService.getTypeIdsByEcode(CookieUitl.getCurrentEnterpriseCode());
			com.suneee.platform.service.bpm.util.BpmUtil.typeIdFilter(typeIdList);
			filter.addFilter("typeIdList", typeIdList);
		}
		// 查询流程列表
		List<BpmDefinition> list = bpmDefinitionService.getAll(filter);

		// 把前面获得的流程分管授权的权限内容设置到流程管理列表
		if (authorizeRightMap != null) {
			for (BpmDefinition bpmDefinition : list) {
				bpmDefinition.setAuthorizeRight(authorizeRightMap
						.get(bpmDefinition.getDefKey()));
			}
		} else {
			// 如果需要所有权限的就直接虚拟一个有处理权限的对象
			AuthorizeRight authorizeRight = new AuthorizeRight();
			authorizeRight.setRightByAuthorizeType("Y",
					BPMDEFAUTHORIZE_RIGHT_TYPE.MANAGEMENT);
			for (BpmDefinition bpmDefinition : list) {
				bpmDefinition.setAuthorizeRight(authorizeRight);
			}
		}

		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流程定义列表成功",getPageList(list,filter));
	}

	/**
	 * 取得流程定义明细
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("detail")
	@Action(description = "查看流程定义明细")
	@ResponseBody
	public ResultVo detail(HttpServletRequest request,
							   HttpServletResponse response) throws Exception {
		long id = RequestUtil.getLong(request, "defId");
		BpmDefinition po = bpmDefinitionService.getById(id);
		Map<String,Object> data=new HashMap<String, Object>();
		// 保存查看明细之前的流程定义ID，用于返回
		long defIdForReturn = RequestUtil.getLong(request, "defIdForReturn", 0);
		if (defIdForReturn != 0) {
			data.put("defIdForReturn",defIdForReturn);
		}
		if (po.getTypeId() != null) {
			GlobalType globalType = globalTypeService.getById(po.getTypeId());
			data.put("globalType",globalType);
		}
		data.put("bpmDefinition",po);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"流程定义详情获取成功",data);
	}

	/**
	 * 节点概要
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("nodeSummary")
	@Action(description = "节点概要")
	@ResponseBody
	public ResultVo nodeSummary(HttpServletRequest request,
									HttpServletResponse response) throws Exception {
		Long defId = RequestUtil.getLong(request, "defId");
		String parentActDefId = RequestUtil.getString(request,
				"parentActDefId", "");
		BpmDefinition bpmDefinition = bpmDefinitionService.getById(defId);
		String actDefId = bpmDefinition.getActDefId();
		List<BpmNodeSet> nodeSetList = null;
		BpmNodeSet globalNodeSet = null;
		if (StringUtil.isEmpty(parentActDefId)) {
			nodeSetList = bpmNodeSetService.getByDefId(defId);
			globalNodeSet = bpmNodeSetService.getBySetType(actDefId,BpmNodeSet.SetType_GloabalForm);
		} else {
			nodeSetList = bpmNodeSetService.getByDefId(defId, parentActDefId);
			globalNodeSet = bpmNodeSetService.getBySetType(actDefId,BpmNodeSet.SetType_GloabalForm, parentActDefId);
		}
		for (BpmNodeSet nodeSet : nodeSetList) {
			if (nodeSet.getNodeId() == null) {
				nodeSetList.remove(nodeSet);
				break;
			}
		}


		FlowNode startFlowNode = NodeCache.getStartNode(actDefId);
		List<FlowNode> endFlowNodeList = NodeCache.getEndNode(actDefId);

		Map<String, Boolean> startScriptMap = new HashMap<String, Boolean>();
		Map<String, Boolean> endScriptMap = new HashMap<String, Boolean>();
		Map<Long, Boolean> preScriptMap = new HashMap<Long, Boolean>();
		Map<Long, Boolean> afterScriptMap = new HashMap<Long, Boolean>();

		Map<Long, Boolean> assignScriptMap = new HashMap<Long, Boolean>();
		Map<Long, Boolean> nodeRulesMap = new HashMap<Long, Boolean>();

		Map<Long, Boolean> bpmFormMap = new HashMap<Long, Boolean>();
		Map<String, Boolean> buttonMap = new HashMap<String, Boolean>();
		Map<Long, Boolean> nodeButtonMap = new HashMap<Long, Boolean>();
		Map<Long, Boolean> taskReminderMap = new HashMap<Long, Boolean>();
		Map<Long, Boolean> nodeUserMap = new HashMap<Long, Boolean>();
		Map<String, Boolean> formMap = new HashMap<String, Boolean>();
		Map<Long, Boolean> taskApprovalItemsMap = new HashMap<Long, Boolean>();
		Map<String, Boolean> globalApprovalMap = new HashMap<String, Boolean>();
		// 用户设置
		this.getNodeUserMap(nodeSetList, actDefId, nodeUserMap, parentActDefId);

		// 流程事件(脚本)
		this.getNodeScriptMap(nodeSetList, actDefId, startScriptMap,
				endScriptMap, preScriptMap, afterScriptMap, assignScriptMap);
		// 流程节点规则
		this.getNodeRulesMap(nodeSetList, actDefId, nodeRulesMap);

		// 操作按钮
		this.getNodeButtonMap(nodeSetList, defId, buttonMap, nodeButtonMap);

		// 催办信息
		this.getTaskReminderMap(nodeSetList, actDefId, taskReminderMap);
		// 手机，表单
		this.getNodeSetMap(nodeSetList, bpmFormMap);

		// 全局
		if (checkForm(globalNodeSet))
			formMap.put("global", true);
		Map<String,Object> data=new HashMap<String, Object>();
		data.put("bpmDefinition",bpmDefinition);
		data.put("defId",defId);
		data.put("actDefId",actDefId);
		data.put("nodeSetList",nodeSetList);
		data.put("startScriptMap",startScriptMap);
		data.put("endScriptMap",endScriptMap);
		data.put("preScriptMap",preScriptMap);
		data.put("afterScriptMap",afterScriptMap);
		data.put("assignScriptMap",assignScriptMap);
		data.put("nodeRulesMap",nodeRulesMap);
		data.put("nodeUserMap",nodeUserMap);
		data.put("bpmFormMap",bpmFormMap);
		data.put("formMap",formMap);
		data.put("buttonMap",buttonMap);
		data.put("nodeButtonMap",nodeButtonMap);
		data.put("taskReminderMap",taskReminderMap);
		data.put("taskApprovalItemsMap",taskApprovalItemsMap);
		data.put("globalApprovalMap",globalApprovalMap);
		data.put("startFlowNode",startFlowNode);
		data.put("endFlowNodeList",endFlowNodeList);
		data.put("parentActDefId",parentActDefId);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"节点概要数据获取成功",data);

	}

	/**
	 * 操作按钮
	 *
	 * @param nodeSetList
	 * @param defId
	 * @param buttonMap
	 * @param nodeButtonMap
	 */
	private void getNodeButtonMap(List<BpmNodeSet> nodeSetList, Long defId,
								  Map<String, Boolean> buttonMap, Map<Long, Boolean> nodeButtonMap) {
		List<BpmNodeButton> nodeButtonList = bpmNodeButtonService
				.getByDefId(defId);
		for (BpmNodeButton bpmNodeButton : nodeButtonList) {
			buttonMap.put(bpmNodeButton.getNodeid(), true);
		}

		for (BpmNodeSet nodeSet : nodeSetList) {
			for (BpmNodeButton bpmNodeButton : nodeButtonList) {
				if (nodeSet.getNodeId().equals(bpmNodeButton.getNodeid()))
					nodeButtonMap.put(nodeSet.getSetId(), true);
			}
		}
	}

	/**
	 * 用户设置
	 *
	 * @param nodeSetList
	 * @param actDefId
	 * @param nodeUserMap
	 */
	private void getNodeUserMap(List<BpmNodeSet> nodeSetList, String actDefId,
								Map<Long, Boolean> nodeUserMap, String parentActDefId) {
		List<BpmUserCondition> bpmUserConditionList = null;
		if (StringUtil.isEmpty(parentActDefId)) {
			bpmUserConditionList = bpmUserConditionService
					.getByActDefId(actDefId);
		} else {
			bpmUserConditionList = bpmUserConditionService
					.getByActDefIdWithParent(actDefId, parentActDefId);
		}
		for (BpmNodeSet nodeSet : nodeSetList) {

			for (BpmUserCondition bpmUserCondition : bpmUserConditionList) {
				if (nodeSet.getNodeId().equals(bpmUserCondition.getNodeid()))
					nodeUserMap.put(nodeSet.getSetId(), true);
			}

		}
	}

	/**
	 * 节点规则
	 *
	 * @param nodeSetList
	 * @param actDefId
	 * @param nodeRulesMap
	 * @return
	 */
	private void getNodeRulesMap(List<BpmNodeSet> nodeSetList, String actDefId,
								 Map<Long, Boolean> nodeRulesMap) {
		List<BpmNodeRule> nodeRuleList = bpmNodeRuleService
				.getByActDefId(actDefId);
		for (BpmNodeSet nodeSet : nodeSetList) {
			for (BpmNodeRule bpmNodeRule : nodeRuleList) {
				if (nodeSet.getNodeId().equals(bpmNodeRule.getNodeId()))
					nodeRulesMap.put(nodeSet.getSetId(), true);
			}
		}
	}

	/**
	 * 流程事件(脚本)
	 *
	 * @param nodeSetList
	 * @param actDefId
	 * @param startScriptMap
	 * @param endScriptMap
	 * @param preScriptMap
	 * @param afterScriptMap
	 * @return
	 */
	private void getNodeScriptMap(List<BpmNodeSet> nodeSetList,
								  String actDefId, Map<String, Boolean> startScriptMap,
								  Map<String, Boolean> endScriptMap, Map<Long, Boolean> preScriptMap,
								  Map<Long, Boolean> afterScriptMap,
								  Map<Long, Boolean> assignScriptMap) {
		// 这个逻辑有问题，暂时这样处理，前置脚本和开始节点脚本为同一种类型，后置脚本跟结束节点脚本为同一种类型。
		List<BpmNodeScript> bpmNodeScriptList = bpmNodeScriptService
				.getByNodeScriptId(null, actDefId);
		// 处理开始和结束
		for (BpmNodeScript bpmNodeScript : bpmNodeScriptList) {
			if (StringUtil.isNotEmpty(bpmNodeScript.getNodeId())) {
				if (bpmNodeScript.getScriptType().intValue() == BpmNodeScript.SCRIPT_TYPE_1
						.intValue() || bpmNodeScript.getScriptType().intValue() == BpmNodeScript.SCRIPT_TYPE_5
						.intValue())
					startScriptMap.put(bpmNodeScript.getNodeId(), true);
				else if (bpmNodeScript.getScriptType().intValue() == BpmNodeScript.SCRIPT_TYPE_2
						.intValue() || bpmNodeScript.getScriptType().intValue() == BpmNodeScript.SCRIPT_TYPE_6
						.intValue())
					endScriptMap.put(bpmNodeScript.getNodeId(), true);
			}
		}
		// 处理前置、后置，分配
		for (BpmNodeSet nodeSet : nodeSetList) {
			for (BpmNodeScript bpmNodeScript : bpmNodeScriptList) {
				if (StringUtil.isNotEmpty(bpmNodeScript.getNodeId())
						&& nodeSet.getNodeId()
						.equals(bpmNodeScript.getNodeId())) {
					if (bpmNodeScript.getScriptType().intValue() == BpmNodeScript.SCRIPT_TYPE_1
							.intValue() || bpmNodeScript.getScriptType().intValue() == BpmNodeScript.SCRIPT_TYPE_5
							.intValue())
						preScriptMap.put(nodeSet.getSetId(), true);

					else if (bpmNodeScript.getScriptType().intValue() == BpmNodeScript.SCRIPT_TYPE_2
							.intValue() || bpmNodeScript.getScriptType().intValue() == BpmNodeScript.SCRIPT_TYPE_6
							.intValue() )
						afterScriptMap.put(nodeSet.getSetId(), true);

					else if (bpmNodeScript.getScriptType().intValue() == BpmNodeScript.SCRIPT_TYPE_4
							.intValue())
						assignScriptMap.put(nodeSet.getSetId(), true);
				}

			}
		}
	}
	private void getNodeSetMap(List<BpmNodeSet> nodeSetList,
							   Map<Long, Boolean> bpmFormMap) {
		for (BpmNodeSet nodeSet : nodeSetList) {

			if (checkForm(nodeSet))
				bpmFormMap.put(nodeSet.getSetId(), true);
		}
	}

	private void getTaskReminderMap(List<BpmNodeSet> nodeSetList,
									String actDefId, Map<Long, Boolean> taskReminderMap) {
		List<TaskReminder> taskReminderList = taskReminderService
				.getByActDefId(actDefId);
		for (BpmNodeSet nodeSet : nodeSetList) {
			for (TaskReminder taskReminder : taskReminderList) {
				if (nodeSet.getNodeId().equals(taskReminder.getNodeId()))
					taskReminderMap.put(nodeSet.getSetId(), true);
			}
		}
	}

	/**
	 * 判断是否设置表单
	 *
	 * @param bpmNodeSet
	 * @return
	 */
	private Boolean checkForm(BpmNodeSet bpmNodeSet) {
		if (BeanUtils.isEmpty(bpmNodeSet))
			return false;
		if (bpmNodeSet.getFormType().shortValue() == BpmNodeSet.FORM_TYPE_ONLINE
				.shortValue()) {
			String formKey=bpmNodeSet.getFormKey();
			if(StringUtil.isNotEmpty(formKey)){
				return true;
			}

		} else if (bpmNodeSet.getFormType().shortValue() == BpmNodeSet.FORM_TYPE_URL
				.shortValue()) {
			if (StringUtil.isNotEmpty(bpmNodeSet.getFormUrl()))
				return true;
		}
		return false;
	}

	/**
	 * 删除流程定义
	 *
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("del")
	@ResponseBody
	@Action(description = "删除流程定义", execOrder = ActionExecOrder.BEFORE, detail = "删除流程<#list StringUtils.split(defId,\",\") as item>"
			+ "【${SysAuditLinkService.getBpmDefinitionLink(Long.valueOf(item))}】"
			+ "</#list>的流程定义")
	public ResultVo del(HttpServletRequest request)
			throws Exception {
		ResultVo resultMessage = null;
		// 是否删除
		String isOnlyVersion = request.getParameter("isOnlyVersion");
		boolean onlyVersion = "true".equals(isOnlyVersion) ? onlyVersion = true
				: false;
		try {
			String lAryId = RequestUtil.getString(request, "defId");
			if (StringUtil.isEmpty(lAryId)) {
				resultMessage = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,
						"没有传入流程定义ID!");
			} else {
				String[] aryDefId = lAryId.split(",");
				for (String defId : aryDefId) {
					Long lDefId = Long.parseLong(defId);
					// 级联删除流程定义
					bpmDefinitionService.delDefbyDeployId(lDefId, onlyVersion);
				}
				resultMessage = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,
						"删除成功！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMessage = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败:"
					+ e.getMessage());
		}
		return resultMessage;
	}

	/**
	 * 流程节点设置
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("nodeSet")
	@ResponseBody
	@Action(description = "流程节点设置")
	public ResultVo nodeSet(HttpServletRequest request,
								HttpServletResponse response) throws Exception {
		long defId = RequestUtil.getLong(request, "defId");
		String parentActDefId = RequestUtil.getString(request,
				"parentActDefId", "");
		BpmDefinition po = bpmDefinitionService.getById(defId);
		Map<String,Object> data=new HashMap<String,Object>();
		if (po.getActDeployId() != null) {
			String defXml = bpmService.getDefXmlByDeployId(po.getActDeployId()
					.toString());
			data.put("defXml",defXml);
			ShapeMeta shapeMeta = BpmUtil.transGraph(defXml);
			data.put("shapeMeta",shapeMeta);
		}
		List<FlowNode> flowNodeList = NodeCache.getFirstNode(po.getActDefId());
		data.put("bpmDefinition",po);
		data.put("parentActDefId",parentActDefId);
		data.put("flowNodeList",flowNodeList);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流程节点设置成功",data);
	}

	@RequestMapping("actInstSelectorJson")
	@ResponseBody
	@Action(description = "流程选择器接口")
	public ResultVo getActInstSelectorJson(HttpServletRequest request,HttpServletResponse response) throws Exception{
		boolean hasRefer = RequestUtil.getBoolean(request, "hasRefer",false);
		String referDefKey = RequestUtil.getString(request, "referDefKey");
		Long defId = RequestUtil.getLong(request, "defId");
		List<ProcessRun> processRunList = null;
		JSONObject json = new JSONObject();
		QueryFilter filter = new QueryFilter(request);
		try {
		filter.addFilter("creatorId", ContextUtil.getCurrentUserId());
		if(hasRefer){	//	是否有设置流程引用
			if(StringUtil.isEmpty(referDefKey)){
				List<String> referDefKeyList = bpmReferDefinitionService.getDefKeysByDefId(defId);
				filter.addFilter("referDefKeyList", referDefKeyList);
			}else{
				filter.addFilter("referDefKey", referDefKey);
			}
			processRunList = processRunService.getMyFlowsAndCptoList(filter);

		}else{
			processRunList = processRunService.getMyCompletedAndCptoList(filter);
		}
		PageList<ProcessRun> runList = (PageList<ProcessRun>) processRunList;
		json.put("processRunList", CommonUtil.getListModel(runList));
		json.put("isSingle",RequestUtil.getInt(request, "isSingle"));
		json.put("hasRefer", hasRefer);
		json.put("defId", defId);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流程选择器接口成功",json);
		}catch (Exception e){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取流程选择器接口失败",e.getMessage());
		}
	}

	/**
	 * 用于对话框选择流程。
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("selector")
	@ResponseBody
	public ResultVo selector(HttpServletRequest request,
								 HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, true);
		Long typeId = RequestUtil.getLong(request, "typeId", 0);
		filter.addFilter("typeIdList",getFlowTypeIds());
		// 是否显示全部流程
		int showAll = RequestUtil.getInt(request, "showAll", 0);
		boolean isSingle = RequestUtil.getBoolean(request, "isSingle");
		List<BpmDefinition> list = null;
		// 显示所有的流程，或者不忽略管理员的情况
		if (showAll == 1) {
			if (typeId != 0) {
				GlobalType globalType = globalTypeService.getById(typeId);
				if (BeanUtils.isNotEmpty(globalType))
					filter.getFilters().put("nodePath",
							globalType.getNodePath() + "%");
			}
			list = bpmDefinitionService.getAll(filter);
		} else {
			// 增加流程分管授权的启动权限分配查询判断
			Long userId = ContextUtil.getCurrentUserId();
			String isNeedRight = "";
			@SuppressWarnings("unused")
			Map<String, AuthorizeRight> authorizeRightMap = null;
			if (!ContextUtil.isSuperAdmin()) {
				isNeedRight = "yes";
				// 获得流程分管授权与用户相关的信息
				Map<String, Object> actRightMap = bpmDefAuthorizeService
						.getActRightByUserMap(userId,
								BPMDEFAUTHORIZE_RIGHT_TYPE.START, false, false);
				// 获得流程分管授权与用户相关的信息集合的流程KEY
				String actRights = (String) actRightMap.get("authorizeIds");
				filter.addFilter("actRights", actRights);
			}
			filter.addFilter("isNeedRight", isNeedRight);

			list = bpmDefinitionService.getMyDefList(filter, typeId);

			// list = bpmDefinitionService.getList(filter, typeId);
		}
		PageList<BpmDefinition> resultList = (PageList<BpmDefinition>) list;
		Map<String,Object> map = new HashMap<>();
		map.put("bpmDefinitionList", PageUtil.getPageVo(resultList));
		map.put("isSingle",isSingle);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流程数据成功！",map);
	}

	/**
	 * 流程分类条件过滤
	 */
	private Set<Long> getFlowTypeIds() {
		List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, false);
		Set<Long> typeIdList = com.suneee.platform.service.bpm.util.BpmUtil.getTypeIdList(globalTypeList);
		return typeIdList;
	}

	/**
	 * 查找我的新建流程列表接口（去除所有分类中数据为0项）
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("myListWithoutEmpty")
	@ResponseBody
	public ResultVo myListWithoutEmpty(HttpServletRequest request,
												  HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "bpmDefinitionItem", false);
		Long typeId=null;
		if(request.getAttribute("typeId")!=null){
			typeId= (Long) request.getAttribute("typeId");
		}else {
			typeId=RequestUtil.getLong(request, "typeId", 0);
		}

		// 增加流程分管授权的启动权限分配查询判断
		Long userId = ContextUtil.getCurrentUserId();
		String isNeedRight = "";
		if (!ContextUtil.isSuperAdmin()) {
			isNeedRight = "yes";
			// 获得流程分管授权与用户相关的信息
			Map<String, Object> actRightMap = bpmDefAuthorizeService
					.getActRightByUserMap(userId,
							BPMDEFAUTHORIZE_RIGHT_TYPE.START, false, false);
			// 获得流程分管授权与用户相关的信息集合的流程KEY
			String actRights = (String) actRightMap.get("authorizeIds");
			filter.addFilter("actRights", actRights);
		}
		filter.addFilter("isNeedRight", isNeedRight);
		//根据权限获取当前用户所能启动的流程
		List<BpmDefinition> list = bpmDefinitionService.getMyDefList(filter,
				typeId);
		//获取所有流程分类
		Set<GlobalType> globalTypes = getByCatKeyForBpmNewPro(request);
		//组合所有流程分类和当前用户所能启动的流程
		Map<Object,Object> map = new HashMap<>();
		for(GlobalType gt:globalTypes){
			List<BpmDefinition> newList = new ArrayList<>();
			for(BpmDefinition bd:list){
				if(gt.getTypeId().longValue()==bd.getTypeId().longValue()){
					newList.add(bd);
				}
			}
			map.put(gt,newList);
		}
		//去掉流程分类中数据为空的类别
		List<Map<String,Object>> resultList = new ArrayList<>();
		for(Object key:map.keySet()){
			List<BpmDefinition> entryList = (List<BpmDefinition>) map.get(key);
			Map<String,Object> resultMap = new HashMap<String, Object>();
			if(entryList.size()!=0){
				resultMap.put("catKey",key);
				resultMap.put("processList",entryList);
				resultList.add(resultMap);
			}
		}
		Collections.sort(resultList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				GlobalType type1= (GlobalType) o1.get("catKey");
				GlobalType type2= (GlobalType) o2.get("catKey");
				if (type1.getSn()>type2.getSn()){
					return 1;
				}else if (type1.getSn()<type2.getSn()){
					return -1;
				}else {
					return 0;
				}
			}
		});
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取新建流程列表数据成功！",resultList);
	}


	/**
	 * 根据catKey获取新建流程流程分类。
	 *
	 * @Methodname: getByCatKeyForBpmNewPro
	 * @Discription:
	 * @param request
	 * @return
	 */
	public Set<GlobalType> getByCatKeyForBpmNewPro(HttpServletRequest request) {

		//List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, true);
		Long glTypeId = RequestUtil.getLong(request, "typeId", 0);
		if(glTypeId == 0){
			List<GlobalType> parentGlobalTypes = globalTypeService.getByParentId(3L, GlobalType.CAT_FLOW);
			glTypeId = parentGlobalTypes.get(0).getTypeId();
		}
		List<GlobalType> globalTypeList = globalTypeService.getByParentId(glTypeId, GlobalType.CAT_FLOW);
		globalTypeList.add(globalTypeService.getById(glTypeId));
		QueryFilter filter = new QueryFilter(request, false);
		//filter.addFilter("status", BpmDefinition.STATUS_ENABLED);
		Long start = System.currentTimeMillis();

		//增加流程分管授权的启动权限分配查询判断
		Long userId = ContextUtil.getCurrentUserId();
		String isNeedRight = "";
		Map<String, AuthorizeRight> authorizeRightMap = null;
		if (!ContextUtil.isSuperAdmin()) {
			isNeedRight = "yes";
			//获得流程分管授权与用户相关的信息
			Map<String, Object> actRightMap = bpmDefAuthorizeService.getActRightByUserMap(userId, BPMDEFAUTHORIZE_RIGHT_TYPE.START, false, false);
			//获得流程分管授权与用户相关的信息集合的流程KEY
			String actRights = (String) actRightMap.get("authorizeIds");
			filter.addFilter("actRights", actRights);
		}
		filter.addFilter("isNeedRight", isNeedRight);
		//filter.addFilter("allowMobile", 1);

		List<BpmDefinition> list = bpmDefinitionService.getMyDefList(filter, glTypeId);

		Map<Long, Integer> typeMap = new HashMap<Long, Integer>();
		// 空类型的数量
		int emptyTypeCount = 0;
		for (BpmDefinition bpmDefinition : list) {
			Long typeId = bpmDefinition.getTypeId();
			if (BeanUtils.isNotEmpty(typeId)) {
				this.mapCount(typeMap, typeId, 1);
			} else {
				emptyTypeCount++;
			}
		}
		Set<GlobalType> globalTypeSet = this.setGlobalTypeSet(globalTypeList, typeMap, emptyTypeCount);

		return globalTypeSet;
	}

	/**
	 * 计算map的数量
	 *
	 *            当前map
	 * @param key
	 * @param num
	 */
	private void mapCount(Map<Long, Integer> map, Long key, Integer num) {
		Integer count = map.get(key);
		if (count != null)
			map.put(key, count + num);
		else
			map.put(key, num);
	}

	/**
	 * 设置当前GlobalTypeSet
	 *
	 * @param globalTypeList
	 *            当前分类list
	 * @param typeMap
	 *            分类Map
	 * @param emptyTypeCount
	 * @return
	 */
	private Set<GlobalType> setGlobalTypeSet(List<GlobalType> globalTypeList, Map<Long, Integer> typeMap, Integer emptyTypeCount) {
		SysTypeKey sysTypeKey = sysTypeKeyService.getByKey(GlobalType.CAT_FLOW);
		// 计算流程分类
		Map<Long, Integer> typeCountMap = new HashMap<Long, Integer>();

		for (GlobalType globalType : globalTypeList) {
			Long typeId = globalType.getTypeId();
			Integer count = typeMap.get(typeId);
			if (count == null)
				continue;
			// 计算当前级别分类数量
			this.mapCount(typeCountMap, typeId, count);

			// 计算上级的分类数量(先移除当前的分类)
			Long[] globalTypeIds = this.removeElementToLong(globalType.getNodePath().split("[.]"), typeId);
			for (GlobalType type : globalTypeList) {
				for (Long globalTypeId : globalTypeIds) {
					if (type.getTypeId().longValue() == globalTypeId.longValue())
						this.mapCount(typeCountMap, globalTypeId, count);
				}
			}
		}
		Set<GlobalType> globalTypeSet = new LinkedHashSet<GlobalType>();
		// 拼接出来最后的结果
		for (GlobalType globalType : globalTypeList) {
			Long typeId = globalType.getTypeId();
			Integer count = typeCountMap.get(typeId);
			if (count == null)
				continue;
			String typeName = globalType.getTypeName();

			globalType.setTypeName(typeName + "(" + count + ")");
			globalTypeSet.add(globalType);
		}

		// 设置空的类型
		this.setEmptyGlobalTypeSet(globalTypeSet, GlobalType.CAT_FLOW, "");
		return globalTypeSet;
	}

	/**
	 * 移除当前数组的元素，并转换Long
	 *
	 * @param array
	 * @param element
	 * @return
	 */
	private Long[] removeElementToLong(String[] array, Long element) {
		if (ArrayUtils.isEmpty(array))
			return null;
		Long[] l = new Long[array.length];
		for (int i = 0; i < array.length; i++) {
			l[i] = Long.parseLong(array[i]);
		}
		return (Long[]) ArrayUtils.removeElement(l, element);
	}

	/**
	 * 设置空的类型
	 *
	 * @param globalTypeSet
	 * @param catKey
	 * @param name
	 *            如果为空 设置为 (0)
	 */
	private void setEmptyGlobalTypeSet(Set<GlobalType> globalTypeSet, String catKey, String name) {
		if (BeanUtils.isNotEmpty(globalTypeSet))
			return;
		GlobalType globalType = globalTypeService.getRootByCatKey(catKey);
		if (StringUtils.isEmpty(name))
			name = globalType.getTypeName() + "(0)";
		else
			name = globalType.getTypeName() + name;
		globalType.setTypeName(name);
		globalTypeSet.add(globalType);

	}

	/**
	 * 流程定义发布
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("deploy")
	@ResponseBody
	@Action(description = "发布流程", detail = "发布流程定义【${SysAuditLinkService.getBpmDefinitionLink(Long.valueOf(defId))}】")
	public ResultVo deploy(HttpServletRequest request)
			throws Exception {
		Long defId = RequestUtil.getLong(request, "defId");
		BpmDefinition bpmDefinition = bpmDefinitionService.getById(defId);
		String defXml = bpmDefinition.getDefXml();
		String actDefXml = BpmUtil.transform(bpmDefinition.getDefKey(),
				bpmDefinition.getSubject(), defXml);
		bpmDefinitionService.deploy(bpmDefinition, actDefXml);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"发布流程成功!");
	}
}
