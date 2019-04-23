package com.suneee.weixin.controller;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.page.PageList;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.controller.GenericController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.HttpUtil;
import com.suneee.oa.service.scene.MobileSceneService;
import com.suneee.oa.service.scene.SubProcessService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE;
import com.suneee.platform.model.bpm.*;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.form.BpmMobileFormDef;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.*;
import com.suneee.platform.service.bpm.impl.BpmActService;
import com.suneee.platform.service.bpm.util.BpmUtil;
import com.suneee.platform.service.form.*;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysPropertyService;
import com.suneee.weixin.model.ListModel;
import com.suneee.weixin.service.IWeixinFormService;
import com.suneee.weixin.util.CommonUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/weixin/bpm/")
public class BpmController extends GenericController {
	
	@Resource 
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private BpmDefAuthorizeService bpmDefAuthorizeService; 
	@Resource
	private IWeixinFormService iWeixinFormService; 
	@Resource
	private BpmMobileFormDefService bpmMobileFormDefService;
	@Resource
	private BpmFormTableService bpmFormTableService;
	@Resource
	private BpmFormRightsService bpmFormRightsService;
	@Resource
	private ITaskService taskServiceImpl;
	@Resource
	private BpmService bpmService;
	@Resource 
	private TaskReadService taskReadService;
	@Resource
	private CommuReceiverService commuReceiverService;
	@Resource 
	private TaskService taskService;
	@Resource
	private BpmNodeSetService bpmNodeSetService;
	@Resource
	private ProcessRunService processRunService;
	@Resource
	private GlobalTypeService globalTypeService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private BpmTaskExeService bpmTaskExeService;
	@Resource
	private BpmActService bpmActService;
	@Resource
	private SysPropertyService sysPropertyService;
	@Resource
	private BpmNodeButtonService bpmNodeButtonService;
	@Resource
	private MobileSceneService mobileSceneService;
	@Resource
	private SubProcessService subProcessService;
	@Resource
	BpmFormHandlerService formHandlerService;

	//文件访问URL地址
	@Value("#{configProperties['user.webSign.url']}")
	private String staticUrl;

	private String getStaticUrl() {
		if (staticUrl==null){
			return "";
		}
		return staticUrl;
	}
	
	
	/**
	 * 返回我的流程定义列表。
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getMyDef")
	@ResponseBody
	public List<BpmDefinition> getMyDef(HttpServletRequest request, HttpServletResponse response){
		QueryFilter filter = new QueryFilter(request, false);
		Long typeId = RequestUtil.getLong(request, "typeId", 0);
		// 增加流程分管授权的启动权限分配查询判断
		Long userId = ContextUtil.getCurrentUserId();
		String isNeedRight = "";
		if (!ContextUtil.isSuperAdmin()) {
			isNeedRight = "yes";
			// 获得流程分管授权与用户相关的信息
			Map<String, Object> actRightMap = bpmDefAuthorizeService.getActRightByUserMap(userId,
							BPMDEFAUTHORIZE_RIGHT_TYPE.START, false, false);
			// 获得流程分管授权与用户相关的信息集合的流程KEY
			String actRights = (String) actRightMap.get("authorizeIds");
			filter.addFilter("actRights", actRights);
		}
		filter.addFilter("isNeedRight", isNeedRight);
		filter.addFilter("allowMobile", 1);
		List<BpmDefinition> list = bpmDefinitionService.getMyDefList(filter,typeId);
		return list;
	}
	
	/**
	 * 获取发起流程表单模版。
	 * <pre>
	 * 返回数据如下：
	 * {result:true,get:true,formKey:"",version:1,template:"",data:""};
	 * result:获取表单数据结果
	 * get：表示是否获取表单。
	 * 	true：表示获取表单。
	 *  false：表示客户端的表单和服务端一致。
	 * 		那么不需要之后的json数据。
	 * 		formKey:表单key
	 * 		version:表单版本
	 * 		template:angularjs表单模版。
	 * data:表单数据
	 * permission:表单权限
	 * 
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getStartForm")
	public void getStartForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long defId=RequestUtil.getLong(request, "defId",0L);
		String formKey=RequestUtil.getString(request, "formKey", "");
		String pk=RequestUtil.getString(request, "pk", "");
		String isSubProcess = RequestUtil.getString(request,"isSubProcess");
		Long sceneId = RequestUtil.getLong(request,"sceneId");
		int version=RequestUtil.getInt(request, "version",0);
		Long runId = RequestUtil.getLong(request,"runId");
		JSONObject json=new JSONObject();
		try{
			BpmDefinition bpmDefinition=bpmDefinitionService.getById(defId);
			
			BpmMobileFormDef formDef= iWeixinFormService.getByDefId(defId, formKey, version);
			if(formDef==null){
				json.accumulate("result", false);
				json.accumulate("msg", "没有找到表单");
				HttpUtil.writeResponseJson(response,json.toString());
				return;
			}
			Long tableId=formDef.getTableId();
			BpmFormTable formTable=bpmFormTableService.getByTableId(tableId, 1);
			
			BpmNodeSet nodeSet=formDef.getBpmNodeSet();
			String nodeId="";
			if(StringUtil.isNotEmpty(nodeSet.getNodeId())){
				nodeId=nodeSet.getNodeId();
			}
			
			//计算表单权限。
			JSONObject permission= bpmFormRightsService.getByFormKeyAndUserId(formDef.getPcFormKey(), formTable, bpmDefinition.getActDefId(), nodeId, "", false);
			//获取数据
			JSONObject data=null;
			
			if(StringUtil.isEmpty(pk)){
				data=iWeixinFormService.getByFormTable(formTable, true);
				//如果启动关联流程
				if(isSubProcess.equals("true")){
					//获取字段设置的json数据
					String jsonmaping = subProcessService.getBySceneIdAndDefId(sceneId, defId).getJsonmaping();
					if(StringUtil.isNotEmpty(jsonmaping)){
						ProcessRun processRun = processRunService.getById(runId);
						com.alibaba.fastjson.JSONArray jsonMapping = com.alibaba.fastjson.JSONArray.parseArray(jsonmaping);
						//获取父流程的数据
						String fromDataStr = formHandlerService.getBpmFormDataJson(processRun, processRun.getBusinessKey(), processRun.getStartNode());
						JSONObject fromObject = JSONObject.fromObject(fromDataStr).getJSONObject("main");
						Iterator iterator = fromObject.keys();
						while (iterator.hasNext()){
							String key = (String)iterator.next();
							String o =fromObject.get(key).toString();
							if(StringUtil.isNotEmpty(o)){
								data.getJSONObject("main").put(key,fromObject.get(key));
							}
						}
					}
				}
			}
			else{
				data=iWeixinFormService.getDraftData(formTable, pk, true);
			}
			//计算返回数据。
			getRtnJson(json, formDef, permission, data);
		}
		catch(Exception ex){
			json.accumulate("result", false);
			json.accumulate("msg", ex.getMessage());
			ex.printStackTrace();
		}
		HttpUtil.writeResponseJson(response,json.toString());
	}

	/**
	 * 计算返回数据，返回数据包括表单，权限和实际的数据。
	 * @param rtnJson
	 * @param formDef		表单定义
	 * @param permission	权限
	 * @param data			数据
	 */
	private void getRtnJson(JSONObject rtnJson, BpmMobileFormDef formDef, JSONObject permission, JSONObject data) {
		if(StringUtil.isEmpty(formDef.getFormHtml())){
			rtnJson.accumulate("get", false);
		}
		else{
			rtnJson.accumulate("get", true);
			rtnJson.accumulate("formKey", formDef.getFormKey());
			rtnJson.accumulate("version", formDef.getVersion());
			String template=formDef.getFormHtml();
			rtnJson.accumulate("template", template);
		}
		//将数据进行权限计算。
		calcData(data,permission);
		//数据
		rtnJson.accumulate("data", data);
		//权限
		rtnJson.accumulate("permission", permission);
		
		rtnJson.accumulate("result", true);
	}
	
	/**
	 * 根据表单权限和数据计算，有权限的数据，排除没有权限的数据。
	 * 1.主表数据。
	 * 2.子表不显示删除子表的数据。
	 * 3.子表字段数据。
	 * @param data
	 * @param permission
	 */
	private void calcData(JSONObject data,JSONObject permission){
		//主表计算
		JSONObject jsonMainData= data.getJSONObject("main");
		JSONObject jsonMainPerm= permission.getJSONObject("main");
		Iterator itMainPerm= jsonMainPerm.keys();
		while(itMainPerm.hasNext()){
			String key=(String) itMainPerm.next();
			String right=(String) jsonMainPerm.get(key);
			if(!right.equals("n")) continue;
			//删除没有权限的数据
			jsonMainData.remove(key);
			jsonMainData.remove(key+"id");
		}
		
		//子表计算
		JSONObject jsonSubTableData= data.getJSONObject("sub");
		
		JSONObject jsonSubTablePermission= permission.getJSONObject("table");
		
		//子表为空则返回。
		if(BeanUtils.isEmpty(jsonSubTablePermission)) return;
		
		//子表字段权限
		JSONObject fieldsTablePerm=permission.getJSONObject("fields");

		Iterator itSubTablePermission= jsonSubTablePermission.keys();
		//遍历子表。
		while(itSubTablePermission.hasNext()){
			String tbName=(String) itSubTablePermission.next();
			JSONObject rightSubTable=(JSONObject) jsonSubTablePermission.get(tbName);
			//如果子表为隐藏，则不再计算行数据，直接移除子表的数据。
			if(rightSubTable.getBoolean("hidden")){
				jsonSubTableData.remove(tbName);
				continue;
			}
			//处理子表字段
			JSONObject fieldsPerm=fieldsTablePerm.getJSONObject(tbName);
			//获取子表数据
			JSONObject subTableData= (JSONObject) jsonSubTableData.get(tbName);
			JSONArray rows=subTableData.getJSONArray("rows");
			//遍历子表字段。
			Iterator itFieldKeys= fieldsPerm.keys();
			while(itFieldKeys.hasNext()){
				String field=(String) itFieldKeys.next();
				String fieldPerm=fieldsPerm.getString(field);
				if(!fieldPerm.equals("n")) continue;
				//删除子表列数据。
				for(Object obj:rows){
					JSONObject row=(JSONObject) obj;
					row.remove(field);
					jsonMainData.remove(field+"id");
				}
			}
		}
		
	}
	
	/**
	 * 获取预览表单的permission/data。
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getFormPreviewData")
	@ResponseBody
	public JSONObject getFormPreviewData(HttpServletRequest request, HttpServletResponse response) {
		Long tableId = RequestUtil.getLong(request, "tableId");
		BpmFormTable formTable=bpmFormTableService.getByTableId(tableId, 1);

		JSONObject permission = bpmFormRightsService.getByFormKeyAndUserId("", formTable, "", "", "", false);
		JSONObject data= iWeixinFormService.getByFormTable(formTable, true);
		
		JSONObject returnData = new JSONObject();
		returnData.put("permission", permission);
		returnData.put("data", data);
		return returnData;
	}
	
	/**
	 * 获取我的待办任务。
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getMyTask")
	@ResponseBody
	public ListModel getMyTask(HttpServletRequest request, HttpServletResponse response) {
		QueryFilter queryFilter=new QueryFilter(request, true);
		addDefIdFilter(request, queryFilter);
		flowCateFilter(queryFilter);
		List list= taskServiceImpl.getMyMobileTasks(queryFilter);
		ListModel listModel=CommonUtil.getListModel((PageList) list);
		return listModel;
	}
	
	/**
	 * 获取手机表单。
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getTaskForm")
	@ResponseBody
	public com.alibaba.fastjson.JSONObject getTaskForm(HttpServletRequest request, HttpServletResponse response) {
 		String taskId = RequestUtil.getString(request, "taskId");
		String formKey=RequestUtil.getString(request, "formKey", "");
		int version=RequestUtil.getInt(request, "version",0);
		
		SysUser sysUser=(SysUser) ContextUtil.getCurrentUser();
		TaskEntity taskEntity = bpmService.getTask(taskId);
		
		String instanceId=taskEntity.getProcessInstanceId();
		
		String actDefId=taskEntity.getProcessDefinitionId();
		String nodeId=taskEntity.getTaskDefinitionKey();
		BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(actDefId);
		// 更新任务为已读。
		taskReadService.saveReadRecord(Long.parseLong(instanceId), Long.parseLong(taskId));
		// 设置沟通人员或流转人员查看状态。
		commuReceiverService.setCommuReceiverStatus(taskEntity, sysUser);
		
		ProcessRun processRun= processRunService.getByActInstanceId(new Long( instanceId));
		
		Map<String, Object> variables = taskService.getVariables(taskId);
		
		String parentActDefId="";
		if (variables.containsKey(BpmConst.FLOW_PARENT_ACTDEFID)) {// 判断当前是否属于子流程任务
			parentActDefId = variables.get(BpmConst.FLOW_PARENT_ACTDEFID).toString();
		}
		//主键
		String businessKey= (String) variables.get("businessKey");
		
		JSONObject json=new JSONObject(); 
		try{
			
			BpmMobileFormDef formDef= iWeixinFormService.getByNodeId(actDefId, parentActDefId, nodeId, formKey, version);
			
			if(formDef==null){
				json.accumulate("result", false);
				json.accumulate("msg", "没有找到表单");
				return com.alibaba.fastjson.JSONObject.parseObject(json.toString());
			}
			
			Long tableId=formDef.getTableId();
			BpmFormTable formTable=bpmFormTableService.getByTableId(tableId, 1);
			
			JSONObject permission= bpmFormRightsService.getByFormKeyAndUserId(formDef.getPcFormKey(), formTable, actDefId,nodeId, parentActDefId, false);
			
			JSONObject data=iWeixinFormService.getApproveData(formTable, actDefId, nodeId, businessKey);
			
			getRtnJson(json, formDef, permission, data);
			
			//设置任务主题。
			json.accumulate("subject", processRun.getSubject());
			//获取是否会签。
			boolean isSignTask = bpmService.isSignTask(taskEntity);
			boolean isCanBack = bpmActService.isTaskAllowBack(taskId);
			//不支持回退时判断同步条件中是否可以自由回退
			boolean isCanFreeback=false;
			if (!isCanBack){
				Map<String,FlowNode> preCacheMap=new HashMap<String,FlowNode>();
				Map<String,FlowNode> nextCacheMap=new HashMap<String,FlowNode>();
				FlowNode node= NodeCache.getFlowNode(taskEntity.getProcessDefinitionId(),taskEntity.getTaskDefinitionKey());
				boolean isSyncNode=NodeCache.isPreParallelGateway(preCacheMap,node)&&NodeCache.isNextParallelGateway(nextCacheMap,node);
				if (isSyncNode){
					isCanBack=false;
				}
				if (isSyncNode&&preCacheMap.size()>1){
					isCanFreeback=true;
				}
			}
			BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId, parentActDefId);
			boolean isFirstNode = NodeCache.isFirstNode(processRun.getActDefId(), nodeId);
			// 是否执行隐藏路径
			boolean isHidePath = getIsHidePath(bpmNodeSet.getIsHidePath());
			Map<String,Object> flowParam = new HashMap<String, Object>();
			flowParam.put("isSignTask",isSignTask);
			flowParam.put("isFirstNode", isFirstNode);
			flowParam.put("isCanBack", isCanBack);
			flowParam.put("isCanFreeback", isCanBack||isCanFreeback);
			flowParam.put("isHidePath", isHidePath);
			//正常跳转
			boolean isNormalPath=false;
			//选择路径跳转
			boolean isSelectPath=false;
			//自由跳转
			boolean isFreePath=false;
			if (StringUtil.isNotEmpty(bpmNodeSet.getJumpType())){
				if (bpmNodeSet.getJumpType().indexOf("1")>-1){
					isNormalPath=true;
				}
				if (bpmNodeSet.getJumpType().indexOf("2")>-1){
					isSelectPath=true;
				}
				if (bpmNodeSet.getJumpType().indexOf("3")>-1){
					isFreePath=true;
				}
			}
			flowParam.put("isNormalPath", isNormalPath);
			flowParam.put("isSelectPath", isSelectPath);
			flowParam.put("isFreePath", isFreePath);
			json.element("flowParam", flowParam);
			// 获取页面显示的按钮
			Map<String, List<BpmNodeButton>> mapButton = bpmNodeButtonService  .getMapByDefNodeId(bpmDefinition.getDefId(), nodeId);
			List<Map<String,Object>> buttonList=new ArrayList<Map<String, Object>>();
			if (mapButton.get("button")!=null){
				for (BpmNodeButton button:mapButton.get("button")){
					Map<String,Object> buttonItem=new HashMap<String, Object>();
					buttonItem.put("operatorType",button.getOperatortype());
					buttonItem.put("name",button.getBtnname());
					buttonList.add(buttonItem);
				}
			}
			json.element("buttonList",buttonList);
		}
		catch(Exception ex){
			json.accumulate("result", false);
			json.accumulate("msg", ex.getMessage());
			ex.printStackTrace();
		}
		json.element("staticUrl",getStaticUrl());
		return com.alibaba.fastjson.JSONObject.parseObject(json.toString());
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
	
	
	@RequestMapping("getInstForm")
	@Action(description = "获取实例表单")
	@ResponseBody
	public com.alibaba.fastjson.JSONObject getInstForm(HttpServletRequest request, HttpServletResponse response) {
		Long runId = RequestUtil.getLong(request,  "runId");
		String formKey=RequestUtil.getString(request, "formKey", "");
		int version=RequestUtil.getInt(request, "version",0);
		
		ProcessRun processRun=processRunService.getById(runId);
		String actDefId=processRun.getActDefId();
		String nodeId="";
		String parentActDefId="";
		if(processRun.getStatus()==ProcessRun.STATUS_RUNNING){
			Map<String, Object> variables = runtimeService.getVariables(processRun.getActInstId());
			if (variables.containsKey(BpmConst.FLOW_PARENT_ACTDEFID)) {// 判断当前是否属于子流程任务
				parentActDefId = variables.get(BpmConst.FLOW_PARENT_ACTDEFID).toString();
			}
		}
		
		String businessKey= (String) processRun.getBusinessKey();
		
		JSONObject json=new JSONObject(); 
		try{
			BpmMobileFormDef formDef= iWeixinFormService.getInstByDefId(actDefId, parentActDefId, formKey, version);
			if(formDef==null){
				json.accumulate("result", false);
				json.accumulate("msg", "没有获取到实例表单");
			}
			else{
				Long tableId=formDef.getTableId();
				BpmFormTable formTable=bpmFormTableService.getByTableId(tableId, 1);
				
				JSONObject permission= bpmFormRightsService.getByFormKeyAndUserId(formDef.getPcFormKey(), formTable, actDefId,nodeId, parentActDefId, true);
				
				JSONObject data=iWeixinFormService.getApproveData(formTable, actDefId, nodeId, businessKey);
				
				getRtnJson(json, formDef, permission, data);
				
				json.accumulate("subject", processRun.getSubject());
			}
		}
		catch(Exception ex){
			json.accumulate("result", false);
			json.accumulate("msg", "系统内部错误！");
			ex.printStackTrace();
		}
		return com.alibaba.fastjson.JSONObject.parseObject(json.toString());
	} 
	
	 
	@RequestMapping("myRequestListJson")
	@Action(description = "查看我的请求还未结束列表")
	@ResponseBody
	public ListModel myRequestListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, true);
		filter.addFilter("creatorId", ContextUtil.getCurrentUserId());
		filter.addFilter("startFromMobile", 1);
		addDefIdFilter(request, filter);
		flowCateFilter(filter);
		List<ProcessRun> list = processRunService.getMyRequestList(filter);
		ListModel model=CommonUtil. getListModel((PageList)list);
		return model;
	
	}
	
	@RequestMapping("getMyCompletedListJson")
	@Action(description = "查看我的发起流程已经结束列表")
	@ResponseBody
	public ListModel getMyCompletedListJson(HttpServletRequest request, HttpServletResponse response){
		QueryFilter filter = new QueryFilter(request, true);
		filter.addFilter("creatorId", ContextUtil.getCurrentUserId());
		filter.addFilter("startFromMobile", 1);
		addDefIdFilter(request, filter);
		flowCateFilter(filter);
		List<ProcessRun> list = processRunService.getMyCompletedList(filter);
		ListModel model=CommonUtil. getListModel((PageList)list);
		return model;
	}
	
	@RequestMapping("getMyRequestCompletedListJson")
	@Action(description = "查看我的发起流程未结束和已经结束列表")
	@ResponseBody
	public ListModel getMyRequestCompletedListJson(HttpServletRequest request, HttpServletResponse response){
		QueryFilter filter = new QueryFilter(request, true);
		filter.addFilter("creatorId", ContextUtil.getCurrentUserId());
		filter.addFilter("startFromMobile", 1);
		addDefIdFilter(request, filter);
		flowCateFilter(filter);
		List<ProcessRun> list = processRunService.getMyRequestCompletedList(filter);
		ListModel model=CommonUtil. getListModel((PageList)list);
		return model;
	}
	
	
	@RequestMapping("getAlreadyMattersList")
	@Action(description = "查看我的审批的事务(未结束)")
	@ResponseBody
	public ListModel getAlreadyMattersList(HttpServletRequest request, HttpServletResponse response){
		QueryFilter filter = new QueryFilter(request, true);
		filter.addFilter("assignee", ContextUtil.getCurrentUserId().toString());// 用户id
		filter.addFilter("fromMobile", 1);
		List<ProcessRun> list = processRunService.getAlreadyMattersList(filter);
		
		ListModel model=CommonUtil. getListModel((PageList)list);
		return model;
	}
	
	@RequestMapping("getCompletedMattersList")
	@Action(description = "查看我的审批的事务(已经结束)")
	@ResponseBody
	public ListModel getCompletedMattersList(HttpServletRequest request, HttpServletResponse response){
		QueryFilter filter = new QueryFilter(request, true);
		filter.addFilter("fromMobile", 1);
		filter.addFilter("assignee", ContextUtil.getCurrentUserId().toString());
		List<ProcessRun> list = processRunService.getCompletedMattersList(filter);
		ListModel model=CommonUtil. getListModel((PageList)list);
		return model;
	}
	
	@RequestMapping("getflowToMeList")
	@Action(description = "查看我承办的事务(未结束和已经结束)")
	@ResponseBody
	public ListModel getflowToMeList(HttpServletRequest request, HttpServletResponse response){
		QueryFilter filter = new QueryFilter(request, true);
		filter.addFilter("fromMobile", 1);
		filter.addFilter("assignee", ContextUtil.getCurrentUserId().toString());
		List<ProcessRun> alreadyMatterslist = processRunService.getAlreadyMattersListAndCompletedMattersList(filter);

		ListModel model=CommonUtil. getListModel((PageList)alreadyMatterslist);
		return model;
	}
	
	
	
	/**
	 * 查看我承办的事务(未结束和已经结束)
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getflowToMeListFront")
	@ResponseBody
	public ListModel myTaskListFront(HttpServletRequest request, HttpServletResponse response) {
		QueryFilter filter = new QueryFilter(request, true);
		filter.addFilter("fromMobile", 1);
		filter.addFilter("assignee", ContextUtil.getCurrentUserId().toString());
		List<ProcessRun> alreadyMatterslist = processRunService.getAlreadyMattersListAndCompletedMattersList(filter);
		
		ListModel listModel=CommonUtil.getListModel((PageList) alreadyMatterslist);
		return listModel;
	}
	
	
	
	@RequestMapping("getMyTurnOutJson")
	@Action(description = "查看我转出去的事情")
	@ResponseBody
	public ListModel getMyTurnOutJson(HttpServletRequest request, HttpServletResponse response){
		QueryFilter filter = new QueryFilter(request, true);
		
		filter.addFilter("ownerId", ContextUtil.getCurrentUserId().toString());
		List<BpmTaskExe> list = bpmTaskExeService.getMobileAccordingMattersList(filter);
		ListModel model=CommonUtil. getListModel((PageList)list);
		return model;
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
	 * 获取系统流程实例总数（不包括草稿实例和被禁用流程定义的实例）
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getInstanceList")
	@ResponseBody
	@Action(description = "获取系统流程实例总数")
	public ListModel getInstanceList(HttpServletRequest request, HttpServletResponse response){
		QueryFilter filter = new QueryFilter(request);
		// 过滤掉草稿实例
		filter.addFilter("exceptStatus", 4);
		// 过滤被禁用流程定义的实例
		filter.addFilter("exceptDefStatus", 3);
		PageList<ProcessRun> list = (PageList<ProcessRun>)processRunService.getAll(filter);
		ListModel listModel = CommonUtil.getListModel(list);
		return listModel;
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
