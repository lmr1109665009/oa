package com.suneee.platform.webservice.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.model.ProcessTask;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageList;
import com.suneee.core.util.*;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.bpm.TaskDao;
import com.suneee.platform.model.bpm.*;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.*;
import com.suneee.platform.service.bpm.impl.BpmActService;
import com.suneee.platform.service.bpm.util.BpmUtil;
import com.suneee.platform.service.form.BpmFormHandlerService;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.platform.webservice.api.FlowService;
import com.suneee.platform.webservice.impl.util.BpmNodeUtil;
import com.suneee.platform.webservice.impl.util.GsonUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.displaytag.util.ParamEncoder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.suneee.core.api.bpm.model.IProcessRun;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.model.ProcessTask;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.db.datasource.DataSourceUtil;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageList;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.ArrayUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.dao.bpm.TaskDao;
import com.suneee.platform.model.bpm.BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE;
import com.suneee.platform.model.bpm.AgentSetting;
import com.suneee.platform.model.bpm.AssignUsers;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.BpmNodeSet;
import com.suneee.platform.model.bpm.BpmProCopyto;
import com.suneee.platform.model.bpm.BpmProTransTo;
import com.suneee.platform.model.bpm.BpmRunLog;
import com.suneee.platform.model.bpm.BpmTaskExe;
import com.suneee.platform.model.bpm.BpmUserCondition;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.AgentSettingService;
import com.suneee.platform.service.bpm.AssignUsersService;
import com.suneee.platform.service.bpm.BpmDefAuthorizeService;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmNodeSetService;
import com.suneee.platform.service.bpm.BpmNodeUserService;
import com.suneee.platform.service.bpm.BpmProCopytoService;
import com.suneee.platform.service.bpm.BpmProTransToService;
import com.suneee.platform.service.bpm.BpmRunLogService;
import com.suneee.platform.service.bpm.BpmService;
import com.suneee.platform.service.bpm.BpmTaskExeService;
import com.suneee.platform.service.bpm.BpmUserConditionService;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.service.bpm.TaskOpinionService;
import com.suneee.platform.service.bpm.impl.BpmActService;
import com.suneee.platform.service.bpm.util.BpmUtil;
import com.suneee.platform.service.form.BpmFormHandlerService;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.platform.webservice.api.FlowService;
import com.suneee.platform.webservice.impl.util.AgentSettingUtil;
import com.suneee.platform.webservice.impl.util.BpmNodeUtil;
import com.suneee.platform.webservice.impl.util.DefRevocationUtil;
import com.suneee.platform.webservice.impl.util.GsonUtil;

public class FlowServiceImpl implements FlowService {
	@Resource
    SysUserService sysUserService;
	@Resource
	private ProcessRunService processRunService;
	@Resource
	private TaskOpinionService taskOpinionService;
	@Resource
    BpmActService bpmActService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private BpmDefAuthorizeService bpmDefAuthorizeService;
	@Resource
	private BpmRunLogService bpmRunLogService;
	@Resource
	private BpmFormHandlerService bpmFormHandlerService;
	@Resource
	private TaskDao taskDao;
	@Resource
	private GlobalTypeService globalTypeService;
	@Resource
	private BpmUserConditionService bpmUserConditionService;
	@Resource
	private BpmTaskExeService bpmTaskExeService;
	@Resource
	private BpmProCopytoService bpmProCopytoService;
	@Resource
	private BpmProTransToService bpmProTransToService;
	@Resource
	private AgentSettingService agentSettingService;
	@Resource
	private AssignUsersService assignUsersService;
	@Resource
	private BpmNodeSetService bpmNodeSetService;
	@Resource
	private BpmService bpmService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private BpmNodeUserService bpmNodeUserService;
	
	private ParamEncoder paramEncoder;

	/**
	 * 获取指定useraccount用户的已办事宜
	 */
	@Override
	public String getAlreadyMattersListJson(String json) {

		JSONObject result = new JSONObject();
		try {
			JSONObject jsonObject = JSONObject.fromObject(json);

			String account = jsonObject.getString("account");
			String classType = jsonObject.get("classType") != null ? jsonObject.getString("classType") : "";

			QueryFilter filter = new QueryFilter(jsonObject);
			if (StringUtil.isEmpty(account)) {// 没传用户就报错
				throw new Exception("必须传入用户账号(account)");
			}
			SysUser user = sysUserService.getByAccount(account);
			if (BeanUtils.isEmpty(user)) {
				throw new Exception("该账号的用户不存在");
			}
			ContextUtil.setCurrentUser((ISysUser) user);

			filter.addFilter("assignee", user.getUserId());// 用户id
			if (StringUtil.isNotEmpty(classType)) {
				StringBuffer sb = new StringBuffer();
				for(String str:classType.split(",")){
					if(sb.length()>0){
						sb.append(",");
					}
					sb.append("'"+str+"'");
				}
				classType=sb.toString();
				
				boolean inOrNot = jsonObject.get("inOrNot") != null ? jsonObject.getBoolean("inOrNot") : true;// 包含还是排除\
				if (inOrNot) {
					filter.addFilter("nodeKey", classType);
				} else {
					filter.addFilter("notNodeKey", classType);
				}
			}

			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
			List<ProcessRun> list = processRunService.getAlreadyMattersList(filter);
			for (ProcessRun processRun : list) {
				if (processRun.getStatus().shortValue() != ProcessRun.STATUS_FINISH.shortValue()) {
					// 1.查找当前用户是否有最新审批的任务
					TaskOpinion taskOpinion = taskOpinionService.getLatestUserOpinion(processRun.getActInstId(), new Long(user.getUserId()));
					if (BeanUtils.isNotEmpty(taskOpinion))
						processRun.setRecover(ProcessRun.STATUS_RECOVER);
				}

				Map<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("obj", processRun);
				GlobalType globalType = globalTypeService.getById(processRun.getTypeId());
				map.put("nodeKey", globalType != null ? globalType.getNodeKey() : "");
				mapList.add(map);
			}

			result.put("totalCount", ((PageList<ProcessRun>) list).getTotalCount());
			result.put("state", "0");
			result.put("msg", "操作成功");
			result.put("list", GsonUtil.toJsonTree(mapList).toString());
		} catch (Exception e) {
			result.put("state", "-1");
			result.put("msg", "操作失败：" + ExceptionUtils.getRootCauseMessage(e));
		}

		return result.toString();
	}
	
	/**
	 * 获取指定account账户的待办事宜
	 */
	public String getMyTaskByAccount(String json){
		JSONObject result = new JSONObject();
		try {
			JSONObject jsonObject = JSONObject.fromObject(json);
			//传入的账户
			String account = jsonObject.getString("account");
			String taskNodeName = jsonObject.getString("taskNodeName");
			String subject = jsonObject.getString("subject");
			String processName = jsonObject.getString("processName");
			String orderField = jsonObject.getString("orderField");
			String orderSeq = jsonObject.getString("orderSeq");
			String currentPage = jsonObject.get("currentPage") != null ? jsonObject.getString("currentPage") : "";
			String pageSize = jsonObject.get("pageSize") != null ? jsonObject.getString("pageSize") : "";

			if (StringUtil.isEmpty(account)) {// 没传用户就报错
				throw new Exception("必须传入用户账号(account)");
			}
			SysUser user = sysUserService.getByAccount(account);
			if (BeanUtils.isEmpty(user)) {
				throw new Exception("该账号的用户不存在!");
			}
			
			PageBean pb = new PageBean();

			if (StringUtil.isNotEmpty(pageSize)) {
				pb.setPagesize(Integer.parseInt(pageSize));
			}
			if (StringUtil.isNotEmpty(currentPage)) {
				pb.setCurrentPage(Integer.parseInt(currentPage));
			}
			
			List<ProcessTask> processTasks = taskDao.getTasks(user.getUserId(), taskNodeName, subject, processName, orderField, orderSeq, pb);

			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
			for (ProcessTask obj : processTasks) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("obj", obj);
				mapList.add(map);
			}

			result.put("totalCount", ((PageList<ProcessTask>) processTasks).getTotalCount());
			result.put("state", "0");
			result.put("msg", "操作成功");
			result.put("list", GsonUtil.toJsonTree(mapList).toString());
		} catch (Exception e) {
			result.put("state", "-1");
			result.put("msg", "操作失败：" + ExceptionUtils.getRootCauseMessage(e));
		}
		return result.toString();
	}

	/**
	 * 获取指定UserAccount用户我的办结事宜
	 */
	@Override
	public String getMyCompletedListJson(String json) {
		JSONObject result = new JSONObject();
		try {
			JSONObject jsonObject = JSONObject.fromObject(json);

			String account = jsonObject.getString("account");
			String classType = jsonObject.get("classType") != null ? jsonObject.getString("classType") : "";

			QueryFilter filter = new QueryFilter(jsonObject);
			if (StringUtil.isEmpty(account)) {// 没传用户就报错
				throw new Exception("必须传入用户账号(account)");
			}
			SysUser user = sysUserService.getByAccount(account);
			if (BeanUtils.isEmpty(user)) {
				throw new Exception("该账号的用户不存在");
			}
			filter.addFilter("creatorId", user.getUserId());// 用户id
			if (StringUtil.isNotEmpty(classType)) {
				StringBuffer sb = new StringBuffer();
				for(String str:classType.split(",")){
					if(sb.length()>0){
						sb.append(",");
					}
					sb.append("'"+str+"'");
				}
				classType=sb.toString();
				
				boolean inOrNot = jsonObject.get("inOrNot") != null ? jsonObject.getBoolean("inOrNot") : true;// 包含还是排除\
				if (inOrNot) {
					filter.addFilter("nodeKey", classType);
				} else {
					filter.addFilter("notNodeKey", classType);
				}
			}
			List<ProcessRun> list = processRunService.getMyCompletedList(filter);
			int totalCount = list.size();
			PageBean pageBean =filter.getPageBean();
			if(BeanUtils.isNotEmpty(pageBean)){
				totalCount=pageBean.getTotalCount();
			}
			
			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
			for (ProcessRun obj : list) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("obj", obj);
				GlobalType globalType = globalTypeService.getById(obj.getTypeId());
				map.put("nodeKey", globalType != null ? globalType.getNodeKey() : "");
				mapList.add(map);
			}

			result.put("totalCount", totalCount);
			result.put("state", "0");
			result.put("msg", "操作成功");
			result.put("list", GsonUtil.toJsonTree(mapList).toString());
		} catch (Exception e) {
			result.put("state", "-1");
			result.put("msg", "操作失败：" + ExceptionUtils.getRootCauseMessage(e));
		}
		return result.toString();
	}

	@Override
	public String getMyFlowListJson(String json) {
		JSONObject result = new JSONObject();
		try {
			JSONObject jsonObject = JSONObject.fromObject(json);

			String account = jsonObject.getString("account");
			String classType = jsonObject.get("classType") != null ? jsonObject.getString("classType") : "";
			QueryFilter filter = new QueryFilter(jsonObject);
			if (account == null) {// 没传用户就报错
				throw new Exception("必须传入用户账号(account)");
			}
			SysUser user = sysUserService.getByAccount(account);
			if (BeanUtils.isEmpty(user))
				throw new Exception("该账号的用户不存在");
			ContextUtil.setCurrentUserAccount(account);
			filter.addFilter("ownerId", user.getUserId());// 用户id

			// 处理分类 多个分类以,分割<---------------
			if (StringUtil.isNotEmpty(classType)) {
				StringBuffer sb = new StringBuffer();
				for(String str:classType.split(",")){
					if(sb.length()>0){
						sb.append(",");
					}
					sb.append("'"+str+"'");
				}
				classType=sb.toString();
				
				boolean inOrNot = jsonObject.get("inOrNot") != null ? jsonObject.getBoolean("inOrNot") : true;// 包含还是排除\
				if (inOrNot) {
					filter.addFilter("nodeKey", classType);
				} else {
					filter.addFilter("notNodeKey", classType);
				}
			}
			// <----------------

			String isNeedRight = "";
			if (!ContextUtil.isSuperAdmin((ISysUser) user)) {
				isNeedRight = "yes";
				// 获得流程分管授权与用户相关的信息
				Map<String, Object> actRightMap = bpmDefAuthorizeService.getActRightByUserMap(user.getUserId(), BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE.START, false, false);
				// 获得流程分管授权与用户相关的信息集合的流程KEY
				String actRights = (String) actRightMap.get("authorizeIds");
				filter.addFilter("actRights", actRights);
			}
			filter.addFilter("isNeedRight", isNeedRight);
			List<BpmDefinition> list = bpmDefinitionService.getMyDefList(filter, 0L);

			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
			// 把nodeKey也包含结果中
			for (BpmDefinition obj : list) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("obj", obj);
				GlobalType globalType = globalTypeService.getById(obj.getTypeId());
				map.put("nodeKey", globalType != null ? globalType.getNodeKey() : "");
				mapList.add(map);
			}

			result.put("totalCount", ((PageList<BpmDefinition>) list).getTotalCount());
			result.put("state", "0");
			result.put("msg", "操作成功");
			result.put("list", GsonUtil.toJsonTree(mapList).toString());
		} catch (Exception e) {
			result.put("state", "-1");
			result.put("msg", "操作失败：" + ExceptionUtils.getRootCauseMessage(e));
		}
		return result.toString();
	}

	@Override
	public String getMyRequestListJson(String json) {
		JSONObject result = new JSONObject();
		try {
			JSONObject jsonObject = JSONObject.fromObject(json);

			String account = jsonObject.getString("account");
			String classType = jsonObject.get("classType") != null ? jsonObject.getString("classType") : "";

			QueryFilter filter = new QueryFilter(jsonObject);
			if (account == null) // 没传用户就报错
				throw new Exception("必须传入用户账号(account)");
			SysUser user = sysUserService.getByAccount(account);
			if (BeanUtils.isEmpty(user))
				throw new Exception("该账号的用户不存在");
			filter.addFilter("creatorId", user.getUserId());

			if (StringUtil.isNotEmpty(classType)) {
				StringBuffer sb = new StringBuffer();
				for(String str:classType.split(",")){
					if(sb.length()>0){
						sb.append(",");
					}
					sb.append("'"+str+"'");
				}
				classType=sb.toString();
				
				boolean inOrNot = jsonObject.get("inOrNot") != null ? jsonObject.getBoolean("inOrNot") : true;// 包含还是排除\
				if (inOrNot) {
					filter.addFilter("nodeKey", classType);
				} else {
					filter.addFilter("notNodeKey", classType);
				}
			}

			List<ProcessRun> list = processRunService.getMyRequestList(filter);

			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
			for (ProcessRun processRun : list) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("obj", processRun);
				GlobalType globalType = globalTypeService.getById(processRun.getTypeId());
				map.put("nodeKey", globalType != null ? globalType.getNodeKey() : "");
				mapList.add(map);
			}

			result.put("totalCount", ((PageList<ProcessRun>) list).getTotalCount());
			result.put("state", "0");
			result.put("msg", "操作成功");
			result.put("list", GsonUtil.toJsonTree(mapList).toString());
		} catch (Exception e) {
			result.put("state", "-1");
			result.put("msg", "操作失败：" + ExceptionUtils.getRootCauseMessage(e));
		}
		return result.toString();
	}

	@Override
	public String getMyDraftListJson(String json) {
		JSONObject result = new JSONObject();
		
		try {
			JSONObject jsonObject = JSONObject.fromObject(json);

			String account = jsonObject.getString("account");
			QueryFilter filter = new QueryFilter(jsonObject);
			if (account == null) {// 没传用户就报错
				throw new Exception("必须传入用户账号(account)");
			}

			SysUser user = sysUserService.getByAccount(account);
			if (BeanUtils.isEmpty(user)) {
				throw new Exception("该账号的用户不存在");
			}
			ContextUtil.setCurrentUser((ISysUser) user);
			
			filter.addFilter("userId", user.getUserId());
			List<ProcessRun> list = processRunService.getMyDraft(filter);
			
			result.put("totalCount", ((PageList<ProcessRun>) list).getTotalCount());
			result.put("state", "0");
			result.put("msg", "操作成功");
			result.put("list", GsonUtil.toJsonTree(list).toString());
		} catch (Exception e) {
			result.put("state", "-1");
			result.put("msg", "操作失败：" + ExceptionUtils.getRootCauseMessage(e));
		}
		return result.toString();
	}

	@Override
	public String delDraftByRunIds(String runIds) {
		try {
			if (StringUtil.isEmpty(runIds)) {
				throw new Exception("请输入需要删除的草稿id");
			}
			Long[] runIdAry = ArrayUtil.convertArray(runIds.split(","));
			for (long runId : runIdAry) {
				ProcessRun processRun = processRunService.getById(runId);
				String dsAlias = processRun.getDsAlias();
				String tableName = processRun.getTableName();
				String businessKey = processRun.getBusinessKey();
				if (StringUtil.isNotEmpty(tableName)) {
					if(ServiceUtil.isLocalDataSource(dsAlias)){
						bpmFormHandlerService.delByIdTableName(businessKey, "W_" + tableName);
					} else {
						bpmFormHandlerService.delByDsAliasAndTableName(dsAlias, tableName, businessKey);
					}
				}
				bpmRunLogService.addRunLog(runId, BpmRunLog.OPERATOR_TYPE_DELETEFORM, "删除草稿");
				processRunService.delById(runId);
			}
			return getReturn(true, null);
		} catch (Exception e) {
			return getReturn(false, ExceptionUtils.getRootCauseMessage(e));
		}
	}

	@Override
	public String saveDraftByForm(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonObject = JSONObject.fromObject(json != null ? json : "{}");
		try {
			String account = jsonObject.getString("account");
			ProcessCmd processCmd = BpmUtil.getProcessCmd(jsonObject);
			SysUser user = this.getUserByAccount(account);
		
			ContextUtil.setCurrentUser((ISysUser) user);
			processCmd.setCurrentUserId(user.getUserId().toString());
			// 更新
			if (StringUtil.isNotEmpty(processCmd.getBusinessKey())) {
				ProcessRun processRun = processRunService.getById(jsonObject.getLong("runId"));
				processCmd.setProcessRun((IProcessRun) processRun);
				processRunService.saveData(processCmd);
			} else {// 添加
				processRunService.saveForm(processCmd);
			}
			result.put("state", "0");
			result.put("msg", "操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "-1");
			result.put("msg", "操作失败：" + e.getMessage());
		}

		return result.toString();
	}

	/**
	 * 返回接口调用结果
	 * 
	 * @param success
	 *            接口调用是否成功
	 * @param message
	 *            消息内容
	 * @return
	 */
	private String getReturn(Boolean success, String message) {
		JsonObject json = new JsonObject();
		json.addProperty("result", success ? "true" : "false");
		if (StringUtil.isNotEmpty(message)) {
			json.addProperty("message", message);
		}
		return json.toString();
	}

	@Override
	public String getBpmAllNode(String defId) {
		JSONObject result = new JSONObject();

		Long id = null;
		try {
			id = new Long(defId);
		} catch (Exception e) {
			result.put("state", "-1");
			result.put("msg", "请正确输入流程定义ID");
			return result.toString();
		}
		BpmDefinition bpmDefinition = bpmDefinitionService.getById(id);
		String actDefId = bpmDefinition.getActDefId();
		List<BpmNodeSet> nodeSetList = bpmNodeSetService.getByDefId(id);
		FlowNode startFlowNode = NodeCache.getStartNode(actDefId);
		BpmNodeUtil.removeListInFlowNode(startFlowNode);
		List<FlowNode> endFlowNodeList = NodeCache.getEndNode(actDefId);
		for (FlowNode flowNode : endFlowNodeList) {
			BpmNodeUtil.removeListInFlowNode(flowNode);
		}

		result.put("nodeSetList", nodeSetList);
		result.put("startFlowNode", startFlowNode);
		result.put("endFlowNodeList", endFlowNodeList);

		result.put("state", "0");
		result.put("msg", "请求成功");
		return result.toString();
	}

	@Override
	public String saveNode(String json) {
		JSONObject result = new JSONObject();
		try {
			JSONObject jsonObject = JSONObject.fromObject(json);

			String conditionId = jsonObject.getString("conditionId");// 人员设置条件的id
			String actDefId = jsonObject.getString("actDefId");//
			String nodeId = jsonObject.getString("nodeId");
			String condition = jsonObject.get("condition") != null ? jsonObject.getString("condition") : "";
			String users = jsonObject.get("users") != null ? jsonObject.getString("users") : "";
			String conditionShow = jsonObject.get("conditionShow") != null ? jsonObject.getString("conditionShow") : "";// 在列表中的展示内容

			int conditionType = jsonObject.getInt("conditionType");
			Long sn = jsonObject.getLong("sn");
			String formIdentity = jsonObject.get("formIdentity")!= null ? jsonObject.getString("formIdentity") : "";
			String parentActDefId = jsonObject.get("parentActDefId") != null ? jsonObject.getString("parentActDefId") : "";

			BpmUserCondition bpmUserCondition = null;

			bpmUserCondition = bpmUserConditionService.getById(new Long(conditionId));
			if (bpmUserCondition == null) {
				bpmUserCondition = new BpmUserCondition();
			}
			bpmUserCondition.setNodeid(nodeId);
			bpmUserCondition.setActdefid(actDefId);
			bpmUserCondition.setSn(sn);
			bpmUserCondition.setCondition(condition);
			bpmUserCondition.setFormIdentity(formIdentity);

			// 如果节点不为空,获取setId设置到用户条件当中。
			if (StringUtil.isNotEmpty(nodeId)) {
				BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId, parentActDefId);
				
				if (BeanUtils.isNotEmpty(bpmNodeSet)) {
					bpmUserCondition.setSetId(bpmNodeSet.getSetId());
				}
			}

			bpmUserCondition.setConditionShow(conditionShow);
			bpmUserCondition.setConditionType(conditionType);
			bpmUserCondition.setParentActDefId(parentActDefId);
			bpmUserConditionService.saveConditionAndUser(bpmUserCondition, users);

			result.put("state", "0");
			result.put("msg", "操作成功");
		} catch (Exception e) {
			result.put("state", "-1");
			result.put("msg", "操作失败：" + e.getMessage());
		}

		// System.out.println(result.toString());
		return result.toString();
	}


	/**
	 * 获取某用户待办事项
	 */
	@Override
	public String getPendingMattersList(String account, String json) {
		JSONObject result = new JSONObject();
		try {
			if (StringUtil.isEmpty(account)) {// 没传用户就报错
				throw new Exception("请输入需要查找的账号!");
			}

			JSONObject jsonObject = JSONObject.fromObject(json != null ? json : "{}");
			QueryFilter filter = new QueryFilter(jsonObject);
			if (jsonObject.get("nodePath") != null) {
				filter.getFilters().put("nodePath", jsonObject.getString("nodePath") + "%");
			}

			ContextUtil.setCurrentUserAccount(account);
			List<TaskEntity> list = bpmService.getMyTasks(filter);
			result.put("state", "0");
			result.put("msg", "操作成功");
			result.put("list", list);
		} catch (Exception e) {
			result.put("state", "-1");
			result.put("msg", "操作失败：" + e.getMessage());
		}
		return result.toString();
	}

	@Override
	public String getAccordingMattersList(String userId, String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonObject = JSONObject.fromObject(json != null ? json : "{}");
		try {

			QueryFilter filter = new QueryFilter(jsonObject);
			if (StringUtil.isEmpty(userId)) {// 没传用户就报错
				throw new Exception("请输入需要查找的用户id");
			}
			filter.addFilter("ownerId", userId);// 用户id

			if (jsonObject.get("nodePath") != null) {
				filter.getFilters().put("nodePath", jsonObject.getString("nodePath") + "%");
			}

			List<BpmTaskExe> list = bpmTaskExeService.accordingMattersList(filter);

			result.put("state", "0");
			result.put("msg", "操作成功");
			result.put("list", list);
		} catch (Exception e) {
			result.put("state", "-1");
			result.put("msg", "操作失败：" + e.getMessage());
		}
		return result.toString();
	}

	@Override
	public String getProCopyList(String account, String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonObject = JSONObject.fromObject(json != null ? json : "{}");
		try {

			QueryFilter filter = new QueryFilter(jsonObject);	
			SysUser user  = this.getUserByAccount(account);
			filter.addFilter("ccUid", user.getUserId().toString());// 用户id
			if (jsonObject.get("nodePath") != null) {
				filter.getFilters().put("nodePath", jsonObject.getString("nodePath") + "%");
			}

			List<BpmProCopyto> list = bpmProCopytoService.getMyList(filter);

			result.put("state", "0");
			result.put("msg", "操作成功");
			result.put("list", list);

		} catch (Exception e) {
			result.put("state", "-1");
			result.put("msg", "操作失败：" + e.getMessage());
		}
		return result.toString();
	}

	@Override
	public String getProTransMattersList(String account, String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonObject = JSONObject.fromObject(json != null ? json : "{}");
		try {

			QueryFilter filter = new QueryFilter(jsonObject);
			if (jsonObject.get("nodePath") != null) {
				filter.getFilters().put("nodePath", jsonObject.getString("nodePath") + "%");
			}

			filter.addFilter("exceptDefStatus", 3);

			SysUser user = this.getUserByAccount(account);
			
			filter.addFilter("createUserId", user.getUserId().toString());// 用户id

			List<BpmProTransTo> list = bpmProTransToService.mattersList(filter);

			result.put("state", "0");
			result.put("msg", "操作成功");
			result.put("list", list);
		} catch (Exception e) {
			result.put("state", "-1");
			result.put("msg", "操作失败：" + e.getMessage());
		}
		return result.toString();
	}

	@Override
	public String setAgent(String json) {
		JSONObject result = new JSONObject();
		try {
			JSONObject jsonObject = JSONObject.fromObject(json);
			// 获取授权人信息
			SysUser auther = getSysUser(jsonObject);
			// 获取受理人信息
			String agentAccount = jsonObject.getString("agentAccount");
			SysUser agenter = sysUserService.getByAccount(agentAccount);

			jsonObject.put("authid", auther.getUserId());
			jsonObject.put("authname", auther.getFullname());
			jsonObject.put("agentid", agenter.getUserId());
			jsonObject.put("agent", agenter.getFullname());

			// 处理流程key
			String agentDefList = jsonObject.get("agentDefList") != null ? jsonObject.getString("agentDefList") : "";
			if (StringUtil.isNotEmpty(agentDefList)) {
				JSONArray ja = JSONArray.fromObject(agentDefList);
				for (int i = 0; i < ja.size(); i++) {
					JSONObject jo = ja.getJSONObject(i);
					String fkey = jo.getString("flowkey").trim();
					String fname = bpmDefinitionService.getMainByDefKey(fkey).getSubject();
					jo.put("flowname", fname);
				}
				jsonObject.put("agentDefList", ja.toString());
			}

			result = JSONObject.fromObject(setAgentJson(jsonObject.toString()));
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "-1");
			result.put("msg", "操作失败：" + e.getMessage());
		}

		return result.toString();
	}
	
	/**
	 * 解析代理授权json
	 * @param json
	 * @return
	 */
	private String setAgentJson(String json) {
		if (agentSettingService == null) {
			agentSettingService = AppUtil.getBean(AgentSettingService.class);
		}

		JSONObject result = new JSONObject();
		try {
			AgentSetting agentSetting = AgentSettingUtil.createAgenSettingByJson(json);
			if (agentSetting.getId() == null || agentSetting.getId() == 0) {
				agentSetting.setId(UniqueIdUtil.genId());
				agentSettingService.addAll(agentSetting);
			} else {
				agentSettingService.updateAll(agentSetting);
			}
			result.put("state", "0");
			result.put("msg", "操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "-1");
			result.put("msg", "操作失败：" + e.getMessage());
		}

		return result.toString();
	}
	
	@Override
	public String getBpmDefinitionByDefId(String defId) {
		JSONObject result = new JSONObject();
		try {
			BpmDefinition bpmDefinition = bpmDefinitionService.getById(new Long(defId));
			result.put("bpmDefinition", bpmDefinition);
			result.put("state", "0");
			result.put("msg", "操作成功");
		} catch (Exception e) {
			result.put("state", "-1");
			result.put("msg", "操作失败：" + e.getMessage());
		}

		return result.toString();
	}

	@Override
	public String setDefOtherParam(String defId,String json) {
		JSONObject result = new JSONObject();
		try {
			JSONObject jsonObject = JSONObject.fromObject(json);

			BpmDefinition bpmDefinition = bpmDefinitionService.getById(Long.valueOf(defId));

			// 表单变量
			Object obj = jsonObject.get("taskNameRule");
			if (obj != null) {
				bpmDefinition.setTaskNameRule(obj.toString());
			}
			// 跳过第一个任务
			obj = jsonObject.get("toFirstNode");
			if (obj != null) {
				bpmDefinition.setToFirstNode(Short.valueOf(obj.toString()));
			}
			// 流程启动选择执行人
			obj = jsonObject.get("showFirstAssignee");
			if (obj != null) {
				bpmDefinition.setShowFirstAssignee(Short.valueOf(obj.toString()));
			}
			// 表单明细Url
			obj = jsonObject.get("formDetailUrl");
			if (obj != null) {
				bpmDefinition.setFormDetailUrl(obj.toString());
			}
			// 提交是否需要确认
			obj = jsonObject.get("submitConfirm");
			if (obj != null) {
				bpmDefinition.setSubmitConfirm(Integer.valueOf(obj.toString()));
			}
			// 是否允许转办
			obj = jsonObject.get("allowDivert");
			if (obj != null) {
				bpmDefinition.setAllowDivert(Integer.valueOf(obj.toString()));
			}
			// 是否允许我的办结转发
			obj = jsonObject.get("allowFinishedDivert");
			if (obj != null) {
				bpmDefinition.setAllowFinishedDivert(Integer.valueOf(obj.toString()));
			}
			// 归档时发送消息给发起人
			obj = jsonObject.get("informStart");
			if (obj != null) {
				bpmDefinition.setInformStart(obj.toString());
			}
			// 通知类型
			obj = jsonObject.get("informType");
			if (obj != null) {
				bpmDefinition.setInformType(obj.toString());
			}
			// 是否允许办结抄送
			obj = jsonObject.get("allowFinishedCc");
			if (obj != null) {
				bpmDefinition.setAllowFinishedCc(Integer.valueOf(obj.toString()));
			}
			// 流程实例归档后是否允许打印表单
			obj = jsonObject.get("isPrintForm");
			if (obj != null) {
				bpmDefinition.setIsPrintForm(Short.valueOf(obj.toString()));
			}
			// 流程帮助 的附件
			obj = jsonObject.get("attachment");
			if (obj != null) {
				bpmDefinition.setAttachment(Long.valueOf(obj.toString()));
			}
			// 流程状态
			obj = jsonObject.get("status");
			if (obj != null) {
				bpmDefinition.setStatus(Short.valueOf(obj.toString()));
			}
			// 相邻任务节点人员相同是自动跳过
			obj = jsonObject.get("sameExecutorJump");
			if (obj != null) {
				bpmDefinition.setSameExecutorJump(Short.valueOf(obj.toString()));
			}
			// 允许API调用
			obj = jsonObject.get("isUseOutForm");
			if (obj != null) {
				bpmDefinition.setIsUseOutForm(Short.valueOf(obj.toString()));
			}
			// 流程参考
			obj = jsonObject.get("allowRefer");
			if (obj != null) {
				bpmDefinition.setAllowRefer(Integer.valueOf(obj.toString()));
			}
			// 参考条数
			obj = jsonObject.get("instanceAmount");
			if (obj != null) {
				bpmDefinition.setInstanceAmount(Integer.valueOf(obj.toString()));
			}
			// 直接启动流程。
			obj = jsonObject.get("directstart");
			if (obj != null) {
				bpmDefinition.setDirectstart(Integer.valueOf(obj.toString()));
			}
			// 抄送消息类型
			obj = jsonObject.get("ccMessageType");
			if (obj != null) {
				bpmDefinition.setCcMessageType((String) obj);
			}

			// 测试标签
			obj = jsonObject.get("testStatusTag");
			if (obj != null) {
				bpmDefinition.setTestStatusTag((String) obj);
			}

			// 是否手机审批
			obj = jsonObject.get("allowMobile");
			if (obj != null) {
				bpmDefinition.setAllowMobile(Integer.valueOf(obj.toString()));
			}

			int count = bpmDefinitionService.saveParam(bpmDefinition);
			if (count <= 0) {
				throw new Exception("参数设置失败");
			}

			result.put("state", "0");
			result.put("msg", "操作成功");
		} catch (Exception e) {
			result.put("state", "-1");
			result.put("msg", "操作失败：" + e.getMessage());
		}

		return result.toString();
	}

	@Override
	public String taskAssign(String json) {
		JSONObject result = new JSONObject();

		try {

			JSONObject jsonObject = JSONObject.fromObject(json);

			SysUser sysUser = sysUserService.getById(jsonObject.getLong("userId"));
			if (sysUser == null) {
				throw new Exception("用户不存在");
			}
			ContextUtil.setCurrentUser((ISysUser) sysUser);

			String taskId = jsonObject.getString("taskId");
			Long assigneeId = jsonObject.getLong("assigneeId");
			String assigneeName = jsonObject.getString("assigneeName");
			String memo = jsonObject.getString("memo");
			String informType = jsonObject.getString("informType");

			TaskEntity taskEntity = bpmService.getTask(taskId);
			if (BeanUtils.isEmpty(taskEntity)) {
				throw new Exception("任务已经被处理");
			}
			String assignee = taskEntity.getAssignee();
			// 任务人不为空且和当前用户不同。
			if (ServiceUtil.isAssigneeNotEmpty(assignee) && assignee.equals(assigneeId)) {
				throw new Exception("不能转办给任务执行人");
			}
			if (ServiceUtil.isAssigneeNotEmpty(assignee)) {
				if (!assignee.equals(sysUser.getUserId().toString())) {
					throw new Exception("对不起，转办失败。您（已）不是任务的执行人");
				}
			}

			ProcessRun processRun = processRunService.getByActInstanceId(new Long(taskEntity.getProcessInstanceId()));
			String actDefId = processRun.getActDefId();
			boolean rtn = bpmDefinitionService.allowDivert(actDefId);
			if (!rtn) {
				throw new Exception("任务不允许进行转办");
			}

			BpmTaskExe bpmTaskExe = new BpmTaskExe();
			bpmTaskExe.setId(UniqueIdUtil.genId());
			bpmTaskExe.setTaskId(new Long(taskId));
			bpmTaskExe.setAssigneeId(assigneeId);
			bpmTaskExe.setAssigneeName(assigneeName);
			bpmTaskExe.setOwnerId(sysUser.getUserId());
			bpmTaskExe.setOwnerName(sysUser.getFullname());
			bpmTaskExe.setSubject(processRun.getSubject());
			bpmTaskExe.setStatus(BpmTaskExe.STATUS_INIT);
			bpmTaskExe.setMemo(memo);
			bpmTaskExe.setCratetime(new Date());
			bpmTaskExe.setActInstId(new Long(taskEntity.getProcessInstanceId()));
			bpmTaskExe.setTaskDefKey(taskEntity.getTaskDefinitionKey());
			bpmTaskExe.setTaskName(taskEntity.getName());
			bpmTaskExe.setAssignType(BpmTaskExe.TYPE_TRANSMIT);
			bpmTaskExe.setRunId(processRun.getRunId());
			bpmTaskExe.setTypeId(processRun.getTypeId());
			bpmTaskExe.setInformType(informType);
			
			bpmTaskExeService.assignSave(bpmTaskExe);

			result.put("state", "0");
			result.put("msg", "操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "-1");
			result.put("msg", "操作失败：" + e.getMessage());
		}

		return result.toString();
	}

	@Override
	public String taskCountersign(String userId, String json) {
		JSONObject result = new JSONObject();

		try {
			SysUser sysUser = sysUserService.getById(Long.valueOf(userId));
			if (sysUser == null) {
				throw new Exception("找不到指定用户");
			}
			ContextUtil.setCurrentUser((ISysUser) sysUser);
			JSONObject jsonObject = JSONObject.fromObject(json);
			String cmpIds = jsonObject.getString("cmpIds");
			String taskId = jsonObject.getString("taskId");
			String opinion = jsonObject.getString("opinion");
			String informType = jsonObject.getString("informType");
			String transType = jsonObject.getString("transType");
			String action = jsonObject.getString("action");
			// 保存意见
			TaskEntity taskEntity = bpmService.getTask(taskId);
			ProcessRun processRun = processRunService.getByActInstanceId(new Long(taskEntity.getProcessInstanceId()));
			processRunService.saveTransTo(taskEntity, opinion, informType, cmpIds, transType, action, processRun);
			result.put("state", "0");
			result.put("msg", "操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "-1");
			result.put("msg", "操作失败：" + e.getMessage());
		}

		return result.toString();
	}
	
	@Override
	public String defRecover(String userId,String json) {
		JSONObject result = new JSONObject();
		try {
			SysUser sysUser =sysUserService.getById(Long.valueOf(userId));
			if(sysUser==null){
				throw new Exception("找不到指定用户");
			}
			ContextUtil.setCurrentUser((ISysUser) sysUser);
			
			JSONObject jsonObject = JSONObject.fromObject(json);
			
			Long runId = jsonObject.getLong("runId");
			String informType = jsonObject.getString("informType");
			String memo = jsonObject.getString("opinion");
			int backToStart = jsonObject.getInt("backToStart");

			ResultMessage resultMessage = null;

			// 先检查是否可以撤销
			resultMessage = DefRevocationUtil.checkRecover(runId);
			if (resultMessage.getResult() == ResultMessage.Fail) {
				throw new Exception(resultMessage.getMessage());
			}

			if (backToStart == 1) {
				// 撤销
				resultMessage = processRunService.recover(runId, informType, memo);
			} else {
				// 追回
				resultMessage = processRunService.redo(runId, informType, memo);
			}

			if (resultMessage.getResult() == ResultMessage.Fail) {
				throw new Exception(resultMessage.getMessage());
			}

			result.put("state", "0");
			result.put("msg", "操作成功");
		} catch (Exception e) {
			result.put("state", "-1");
			result.put("msg", "操作失败：" + e.getMessage());
		}

		return result.toString();
	}

	@Override
	public String taskEndProcess(String userId, String json) {
		JSONObject result = new JSONObject();
		try {
			SysUser sysUser = sysUserService.getById(Long.valueOf(userId));
			if (sysUser == null) {
				throw new Exception("用户id不存在");
			}
			ContextUtil.setCurrentUser((ISysUser) sysUser);

			JSONObject jsonObject = JSONObject.fromObject(json);
			Long taskId = jsonObject.getLong("taskId");
			TaskEntity taskEnt = bpmService.getTask(taskId.toString());
			if (taskEnt == null) {
				throw new Exception("任务已不存在");
			}
			String memo = jsonObject.getString("memo");

			String instanceId = taskEnt.getProcessInstanceId();
			String nodeId = taskEnt.getTaskDefinitionKey();
			ProcessRun processRun = bpmService.endProcessByInstanceId(new Long(instanceId), nodeId, memo);
			memo = "结束流程:" + processRun.getSubject() + ",结束原因:" + memo;
			bpmRunLogService.addRunLog(processRun.getRunId(), BpmRunLog.OPERATOR_TYPE_ENDTASK, memo);

			result.put("state", "0");
			result.put("msg", "操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "-1");
			result.put("msg", "操作失败：" + e.getMessage());
		}

		return result.toString();
	}

	@Override
	public String getFreeJump(String taskId) {
		JSONObject result = new JSONObject();
		try {
			Map<String, Map<String, String>> jumpNodesMap = bpmService.getJumpNodes(taskId);
			result.put("jumpNodesMap", jumpNodesMap);
			result.put("state", "0");
			result.put("msg", "操作成功");
		} catch (Exception e) {
			result.put("state", "-1");
			result.put("msg", "操作失败：" + e.getMessage());
		}

		return result.toString();
	}

	@Override
	public String assignUsers(String json) {
		JsonObject result = new JsonObject();
		try {
			JsonElement jsonElement = GsonUtil.parse(json);
			if(!jsonElement.isJsonArray()||jsonElement.isJsonNull()){
				throw new Exception("请输入json格式：[{defKey :\"defkey\",nodeId:\"node1\",userId:\"id1,id2\",startTime:\"yyyy-MM-dd HH:mm:ss\",endTime:\"yyyy-MM-dd HH:mm:ss\"}]");
			}
			JsonArray jsonArray = jsonElement.getAsJsonArray();
			Map<String, String> nodeMap = new HashMap<String, String>();
			List<AssignUsers> assignUserslList = new ArrayList<AssignUsers>();
			for (JsonElement jsonElement2 : jsonArray) {
				JsonObject asJsonObject = jsonElement2.getAsJsonObject();
				//获取一个json对象里面个各个数据
				JsonElement defKeyJson = asJsonObject.get("defKey");
				JsonElement nodeIdJson = asJsonObject.get("nodeId");
				JsonElement userIdJson = asJsonObject.get("userId");
				JsonElement startTimeJson = asJsonObject.get("startTime");
				JsonElement endTimeJson = asJsonObject.get("endTime");
				//验证输入json数据不能为空
				if (!defKeyJson.isJsonPrimitive()) {
					throw new Exception("defKey不能为空");
				}else if (!nodeIdJson.isJsonPrimitive()) {
					throw new Exception("nodeId不能为空");
				}else if (!userIdJson.isJsonPrimitive()) {
					throw new Exception("userId不能为空");
				}else if (!startTimeJson.isJsonPrimitive()) {
					throw new Exception("startTime不能为空");
				}else if (!endTimeJson.isJsonPrimitive()) {
					throw new Exception("endTime不能为空");
				}
				
				//把数据转换类型
				String defKey = defKeyJson.getAsString();
				String nodeId = nodeIdJson.getAsString();
				String userId = userIdJson.getAsString();
				Date startTime = new Date();
				Date endTime = new Date();
				try {
					startTime = GsonUtil.toBean(startTimeJson.toString(), Date.class);
					endTime = GsonUtil.toBean(endTimeJson.toString(), Date.class);
				} catch (Exception e) {
					throw new Exception("请检查startTime或者endTime的格式是否为：yyyy-MM-dd HH:mm:00形式");
				}
				
				//判断输入的各个用户Id都存在着对象
				String[] userIdAry = userId.split(",");
				Map<Long, String>  userMap = new HashMap<Long, String>();
				for (int i =0; i<userIdAry.length;i++) {
					SysUser sysuser = sysUserService.getById(new Long(userIdAry[i]));
					if (BeanUtils.isEmpty(sysuser)) {
						throw new Exception("userId："+userIdAry[i]+",不存在对应用户！");
					}
					userMap.put(sysuser.getUserId(), sysuser.getFullname());
				}
				
				//根据defKey获取对应的流程是否存心
				BpmDefinition mainByDefKey = bpmDefinitionService.getMainByDefKey(defKey);
				if (BeanUtils.isEmpty(mainByDefKey)) {
					throw new Exception("defKey："+defKey+",不存在对应的流程！");
				}
				//根据流程获取对应的节点，保存一份
				if (BeanUtils.isEmpty(nodeMap)) {
					List<BpmNodeSet> bpmNodeSetList = bpmNodeSetService.getByDefId(mainByDefKey.getDefId());
					for (BpmNodeSet bpmNodeset : bpmNodeSetList) {
						nodeMap.put(bpmNodeset.getNodeId(),bpmNodeset.getNodeName());
					}
				}
				//判断输入的nodeId是否是存在流程中的节点
				Boolean isExist = nodeMap.containsKey(nodeId);
				if (!isExist) {
					throw new Exception("defKey："+defKey+"的流程不存在nodeId为："+nodeId+"!");
				}
				
				for (Long userid : userMap.keySet()) {
					AssignUsers assignUsers = new AssignUsers();
					assignUsers.setDefKey(defKey);
					assignUsers.setNodeId(nodeId);
					assignUsers.setNodeName(nodeMap.get(nodeId));
					assignUsers.setUserId(userid);
					assignUsers.setUserName(userMap.get(userid));
					assignUsers.setStartTime(startTime);
					assignUsers.setEndTime(endTime);
					assignUserslList.add(assignUsers);
				}
				
			}
			Long runId = assignUsersService.addAssignUser(assignUserslList);
			result.addProperty("result", "true");
			result.addProperty("runId", runId);
		} catch (Exception e) {
			e.printStackTrace();
			result.addProperty("result", "false");
			result.addProperty("message", ExceptionUtils.getRootCauseMessage(e));
		}
		return result.toString();
	}
	
	

	@Override
	public String getNodesByDefKey(String defKey) {
		BpmDefinition mainByDefKey = bpmDefinitionService.getMainByDefKey(defKey);
		Map<String, FlowNode> nodeMap = NodeCache.getByActDefId(mainByDefKey.getActDefId());
		return this.getNodElement("",nodeMap).toString();
	}
	
	private  JsonElement getNodElement(String supNodeId,Map<String, FlowNode> nodeMap) {
		JsonElement jsonArray = new JsonArray();
		for (String nodeId : nodeMap.keySet()) {
			FlowNode flowNode = nodeMap.get(nodeId);
			if (flowNode.getIsSubProcess()||flowNode.getIsCallActivity()) {
				Map<String, FlowNode> subNodeMap=flowNode.getSubProcessNodes();
				jsonArray.getAsJsonArray().add(this.getNodElement(nodeId,subNodeMap));
			}else {
				String supNodeIdTemp = StringUtil.isNotEmpty(supNodeId)?supNodeId:nodeId; 
				String nodeName = flowNode.getNodeName();
				String nodeType = flowNode.getNodeType();
				String preNode = this.getPreOrNextNaodeId(flowNode.getPreFlowNodes());
				String nextNode = this.getPreOrNextNaodeId(flowNode.getNextFlowNodes());
				JsonElement jsonObj = new JsonObject();
				jsonObj.getAsJsonObject().addProperty("nodeId", nodeId);
				jsonObj.getAsJsonObject().addProperty("nodeName", nodeName);
				jsonObj.getAsJsonObject().addProperty("nodeType", nodeType);
				jsonObj.getAsJsonObject().addProperty("preNode", StringUtil.isNotEmpty(preNode)?preNode:supNodeIdTemp);
				jsonObj.getAsJsonObject().addProperty("nextNode",StringUtil.isNotEmpty(nextNode)?nextNode:supNodeIdTemp);
				jsonArray.getAsJsonArray().add(jsonObj);
			}
		}
		return jsonArray;
	}

	private String getPreOrNextNaodeId(List<FlowNode> flowNodes) {
		if (BeanUtils.isEmpty(flowNodes)) return "";
		List<String> nodeId = new ArrayList<String>();
		for (FlowNode flowNode : flowNodes) {
			nodeId.add(flowNode.getNodeId());
		}
		return "["+StringUtil.getArrayAsString(nodeId)+"]";
	}
	
	/**
	 * 根据用户账户获取用户
	 * @param account
	 * @return
	 * @throws Exception 
	 */
	private SysUser getUserByAccount(String account) throws Exception{
		if (StringUtil.isEmpty(account)) {// 没传用户就报错
			throw new Exception("必须传入用户账号(account)!");
		}
		SysUser user = sysUserService.getByAccount(account);
		if (BeanUtils.isEmpty(user)) {
			throw new Exception("该账号的用户不存在,请确认传入的账户（account）信息是否匹配!");
		}
		return user;
	}
	
	
	/**
	 * 根据json数组获取账户信息
	 * @param jsonObject
	 * @return
	 * @throws Exception
	 */
	private SysUser getSysUser(JSONObject jsonObject) throws Exception {
		if (BeanUtils.isEmpty(jsonObject)) {// 没传用户就报错
			throw new Exception("必须传入用户账号(account)");
		}
		String account = (String) this.getByKey(jsonObject, "account", "");
		if (StringUtil.isEmpty(account)) {// 没传用户就报错
			throw new Exception("必须传入用户账号(account)");
		}
		SysUser user = sysUserService.getByAccount(account);
		if (BeanUtils.isEmpty(user)) {
			throw new Exception("该账号的用户不存在");
		}
		return user;
	}
	
	private Object getByKey(JSONObject jsonObject, String key, Object defaults) {
		if (BeanUtils.isEmpty(jsonObject))
			return defaults;
		return jsonObject.get(key) != null ? jsonObject.get(key) : defaults;

	}

	@Override
	public String getNextTaskUsers(String taskId,String nodeId) {
		TaskEntity taskEntity = bpmService.getTask(taskId);
		if (StringUtil.isEmpty(nodeId)) {
			nodeId = taskEntity.getTaskDefinitionKey(); // 目标节点Id
		}
		String actDefId = taskEntity.getProcessDefinitionId();
		String actInstId = taskEntity.getProcessInstanceId();

		Map<String, Object> vars = runtimeService.getVariables(taskEntity.getExecutionId());

		String startUserId = vars.get(BpmConst.StartUser).toString();

		@SuppressWarnings("unchecked")
		List<TaskExecutor> taskExecutorList = bpmNodeUserService.getExeUserIds(actDefId, actInstId, nodeId, startUserId, "", vars);

		JsonArray jarray = new JsonArray();
		for (TaskExecutor taskExecutor : taskExecutorList) {
			JsonObject jobject = new JsonObject();
			String type = taskExecutor.getType();
			String executeId = taskExecutor.getExecuteId();
			String executor = taskExecutor.getExecutor();
			jobject.addProperty("type", type);
			jobject.addProperty("executeId", executeId);
			jobject.addProperty("executor", executor);
			jarray.add(jobject);
		}
		return jarray.toString();
	}

	@Override
	public String changeAssignee(String json) {
		JsonObject result = new JsonObject();
		try{
			JsonElement jsonElement = GsonUtil.parse(json);
			if(!jsonElement.isJsonObject()||jsonElement.isJsonNull()){
				throw new Exception("请按照以下格式传入JSON参数：{taskId:10035820313,assignee:\"zhangsan\",account:\"admin\",voteContent:\"原处理人已离职\",informType:\"1,2,3\"}");
			}
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			JsonElement taskIdObj = jsonObject.get("taskId");
			JsonElement assigneeObj = jsonObject.get("assignee");
			JsonElement accountObj = jsonObject.get("account");
			if(taskIdObj==null||!taskIdObj.isJsonPrimitive()){
				throw new RuntimeException("必须传入taskId参数");
			}
			if(assigneeObj==null||!assigneeObj.isJsonPrimitive()){
				throw new RuntimeException("必须传入assignee参数");
			}
			if(taskIdObj==null||!accountObj.isJsonPrimitive()){
				throw new RuntimeException("必须传入account参数");
			}
			String taskId = taskIdObj.getAsString();
			String assignee = assigneeObj.getAsString();
			String account = accountObj.getAsString();
			SysUser assigneeUser = sysUserService.getByAccount(assignee);
			if(BeanUtils.isEmpty(assigneeUser)){
				throw new RuntimeException("不存在指定assignee的用户");
			}
			SysUser curUser = sysUserService.getByAccount(account);
			if(BeanUtils.isEmpty(curUser)){
				throw new RuntimeException("不存在指定account的用户");
			}
			ContextUtil.setCurrentUser(curUser);
			String voteContent = "";
			String informType = "";
			JsonElement voteContentObj = jsonObject.get("voteContent");
			JsonElement informTypeObj = jsonObject.get("informType");
			if(voteContentObj!=null&&voteContentObj.isJsonPrimitive()){
				voteContent = voteContentObj.getAsString();
			}
			if(informTypeObj!=null&&informTypeObj.isJsonPrimitive()){
				informType = informTypeObj.getAsString();
			}
			processRunService.updateTaskAssignee(taskId, assigneeUser.getUserId().toString(), voteContent, informType);
			result.addProperty("result", "true");
			result.addProperty("message", String.format("任务处理人已更换为%s", assigneeUser.getFullname()));
		}
		catch(Exception e){
			e.printStackTrace();
			result.addProperty("result", "false");
			result.addProperty("message", ExceptionUtils.getRootCauseMessage(e));
		}
		return result.toString();
	}
}
