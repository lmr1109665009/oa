package com.suneee.platform.controller.bpm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.HttpUtil;
import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.util.JSONUtils;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.bpm.AgentCondition;
import com.suneee.platform.model.bpm.AgentDef;
import com.suneee.platform.model.bpm.AgentSetting;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.AgentSettingService;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.form.BpmFormDefService;

import freemarker.ext.beans.BeansWrapper;
/**
 *<pre>
 * 对象功能:代理设定 控制器类
 * 开发公司:宏天
 * 开发人员:Raise
 * 创建时间:2013-04-29 11:15:10
 *</pre>
 */
@Controller
@RequestMapping("/platform/bpm/agentSetting/")
@Action(ownermodel=SysAuditModelType.PROCESS_AUXILIARY)
public class AgentSettingController extends BaseController
{
	@Resource
	private AgentSettingService agentSettingService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private BpmFormDefService bpmFormDefService;
	/**
	 * 添加或更新代理设定。
	 * @param request
	 * @param response
	 * @param agentSetting 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新代理设定",
			detail="<#if isAdd>添加代理设定<#else>更新代理设定</#if>" +
					"${SysAuditLinkService.getAgentSettingLink(agentSetting)}"
			)
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		boolean isSuccess=false;
		boolean isAdd=false;
		String resultMsg="";
		String flowkey="";
		String flowname="";
		AgentSetting agentSetting=getFormObject(request);
		List<AgentDef> agentDefList = agentSetting.getAgentDefList();
		try{
			if(agentSetting.getId()==null||agentSetting.getId()==0){
				if(agentDefList.size()>1) {
					for(int i=0;i<agentDefList.size();i++){
						if(i==agentDefList.size()-1){
							flowkey += agentDefList.get(i).getFlowkey();
							flowname += agentDefList.get(i).getFlowname();
						}else{
							flowkey += agentDefList.get(i).getFlowkey()+",";
							flowname += agentDefList.get(i).getFlowname()+",";
						}
						agentSetting.setFlowkey(flowkey);
						agentSetting.setFlowname(flowname);
					}
					agentSetting.setId(UniqueIdUtil.genId());
					agentSettingService.addAll(agentSetting);
				}else{
					if(agentSetting.getAuthtype()==1) {
						flowkey = agentDefList.get(0).getFlowkey();
						flowname = agentDefList.get(0).getFlowname();
						agentSetting.setFlowkey(flowkey);
						agentSetting.setFlowname(flowname);
					}
					agentSetting.setId(UniqueIdUtil.genId());
					agentSettingService.addAll(agentSetting);
				}
				resultMsg="添加代理设定成功!";
				isAdd=true;
			}else{
				for (AgentDef list : agentDefList) {
					flowkey += list.getFlowkey()+",";
					flowname += list.getFlowname()+",";
					agentSetting.setFlowkey(flowkey);
					agentSetting.setFlowname(flowname);
				}
				agentSettingService.updateAll(agentSetting);
				resultMsg="更新代理设定成功!";
			}
			HttpUtil.writeResponseJson(response, ResponseMessage.success(resultMsg));
			isSuccess=true;
		}catch(Exception e){
			HttpUtil.writeResponseJson(response, ResponseMessage.fail(resultMsg+","+e.getMessage()));
		}
		
		try {
			if(isSuccess){
				SysAuditThreadLocalHolder.setResult(SysAuditThreadLocalHolder.RESULT_SUCCESS);
			}else{
				SysAuditThreadLocalHolder.setResult(SysAuditThreadLocalHolder.RESULT_FAIL);
			}
			SysAuditThreadLocalHolder.putParamerter("agentSetting", agentSetting);
			SysAuditThreadLocalHolder.putParamerter("isAdd", isAdd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//**添加 系统日志信息** end//	
	}
	
	
	
	

	/**
	 * 更新流程代理状态
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("updStatus")
	@Action(description="更新流程代理状态",
			detail="设置流程代理${SysAuditLinkService.getAgentSettingLink(Long.valueOf(id))} 为 " +
					"<#if AgentSetting.ENABLED_YES.equals(Long.valueOf(status))>启用状态" +
					"<#else>禁用状态" +
					"</#if>")
	public void updStatus(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		try {
			SysAuditThreadLocalHolder.putParamerter(AgentSetting.class.getSimpleName(),BeansWrapper.getDefaultInstance().getStaticModels().get(AgentSetting.class.getName()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		
		Long id=RequestUtil.getLong(request,"id",0L);
		
		short status = RequestUtil.getShort(request, "status", (short) -1);
		
		try{
			if(id.equals(0L)){
				throw new Exception("没有输入ID");
			}else if(status==-1){
				throw new Exception("输入无效状态");
			}else{
				AgentSetting agentSetting=agentSettingService.getById(id);
				agentSetting.setEnabled(status);
				agentSettingService.update(agentSetting);
			    if(AgentSetting.ENABLED_NO.equals(status)){
			    	 message=new ResultMessage(ResultMessage.Success, "更新代理状态禁用成功!");
			    }else{
			    	 message=new ResultMessage(ResultMessage.Success, "更新代理状态启用成功!"); 	
			    }
			}
		}catch(Exception e){
			message=new ResultMessage(ResultMessage.Fail, ","+e.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	
	
	/**
	 * 取得 AgentSetting 实体 
	 * @param request
	 * @return
	 * @throws Exception
	 */
    protected AgentSetting getFormObject(HttpServletRequest request) throws Exception {
    
    	JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher((new String[] { "yyyy-MM-dd" })));
    
		String json=RequestUtil.getString(request, "json");
		int agentType=RequestUtil.getInt(request, "agentType",0);
//		JSONObject obj = JSONObject.fromObject(json);
//		Map<String,  Class<?>> paraTypeMap=new HashMap<String,  Class<?>>();
//		paraTypeMap.put("agentDefList", AgentDef.class);
//		paraTypeMap.put("agentConditionList", AgentCondition.class);
//		AgentSetting agentSetting = (AgentSetting)JSONObject.toBean(obj, AgentSetting.class,paraTypeMap);
		/***
		 * 使用JSON-Lib解析时出错
		 */
		com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(json);
		AgentSetting agentSetting  =jsonObject.parseObject(json, AgentSetting.class);
		SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
		if(agentType!=1){   //为普通入口的 受权 （0 受权人为自己）  否则为管理入口（1 受权人为页面所选择的人员）
			agentSetting.setAuthid(sysUser.getUserId());
//			agentSetting.setAuthname(sysUser.getFullname());
			agentSetting.setAuthname(ContextSupportUtil.getUsername(sysUser));
		}
		if(AgentSetting.AUTHTYPE_GENERAL ==agentSetting.getAuthtype()){
			agentSetting.getAgentDefList().clear();
			agentSetting.getAgentConditionList().clear();
			agentSetting.setFlowkey(null);
			agentSetting.setFlowname(null);
		}else if(AgentSetting.AUTHTYPE_PARTIAL ==agentSetting.getAuthtype()){
			agentSetting.getAgentConditionList().clear();
			agentSetting.setFlowkey(null);
			agentSetting.setFlowname(null);
		}else if(AgentSetting.AUTHTYPE_CONDITION ==agentSetting.getAuthtype()){
			agentSetting.getAgentDefList().clear();
			agentSetting.setAgentid(null);
			agentSetting.setAgent(null);
		}else{
			throw new Exception("无效代理设定");
		}
		
		return agentSetting;
    }
	
	/**
	 * 取得代理设定分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看代理设定分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		QueryFilter filter = new QueryFilter(request,"agentSettingItem");
		Long curUserId = ContextUtil.getCurrentUserId();
		filter.addFilter("authid", curUserId);
		List<AgentSetting> list=agentSettingService.getAll(filter);
		ModelAndView mv=this.getAutoView().addObject("agentSettingList",list);
		
		return mv;
	}



	/**
	 * 取得代理设定分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("manageList")
	@Action(description="查看代理设定分页列表")
	public ModelAndView manageList(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		QueryFilter filter = new QueryFilter(request,"agentSettingItem");
		List<AgentSetting> list=agentSettingService.getAll(filter);
		ModelAndView mv=this.getAutoView().addObject("agentSettingList",list);		
		return mv;
	}
	
	
	
	/**
	 * 删除代理设定
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除代理设定",
			execOrder=ActionExecOrder.BEFORE,
			detail="删除流程代理设定" +
					"<#list StringUtils.split(id, \",\") as item>" +
					"<#assign entity=agentSettingService.getById(Long.valueOf(item))/>" +
					"【entity.】"+
					"</#list>"
			)
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			agentSettingService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail,"删除失败," + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	
	/**
	 * 	编辑代理设定（个人入口）
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑代理设定")
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv =this.getAutoView();
		return getToStartView(request, response,mv,0);
	}
	
	/**
	 * 	编辑代理设定(管理入口)
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("editAll")
	public ModelAndView doNext(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv= new ModelAndView("/platform/bpm/agentSettingEdit.jsp");
		mv = getToStartView(request, response,mv,1);		
		return mv;
	}
	
	
	public ModelAndView getToStartView(HttpServletRequest request,HttpServletResponse response,ModelAndView mv,int agentType) throws Exception{		
		Long id=RequestUtil.getLong(request,"id");
		//String returnUrl=RequestUtil.getPrePage(request);
		AgentSetting agentSetting=agentSettingService.getById(id);
		if(agentSetting!=null){
			if(agentSetting.getAuthtype()==AgentSetting.AUTHTYPE_CONDITION){
				List<AgentCondition> agentConditionList=agentSettingService.getAgentConditionList(id);
				if(BeanUtils.isNotEmpty(agentConditionList)){
					AgentCondition condition = agentConditionList.get(0);
					String condJsonStr = condition.getCondition();
					com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(condJsonStr);
					Long tableId = jsonObject.getLong("tableId");
					
					BpmDefinition bpmDefinition = bpmDefinitionService.getMainDefByActDefKey(agentSetting.getFlowkey());
					if(BeanUtils.isNotEmpty(bpmDefinition)){
						Long currentTableId = bpmFormDefService.getTableIdByDefId(bpmDefinition.getDefId());
						
						if(currentTableId==null || ( currentTableId!=null && !currentTableId.equals(tableId))){
							agentConditionList.clear();
						}
					}
				}
				agentSetting.setAgentConditionList(agentConditionList);
			}else{
				List<AgentDef> agentDefList=agentSettingService.getAgentDefList(id);
				agentSetting.setAgentDefList(agentDefList);
			}
		}
		return mv.addObject("agentSetting",agentSetting).addObject("agentType",agentType);    // 0 为 自己入口   1为管理入口 
	}

	/**
	 * 取得代理设定明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看代理设定明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		long id=RequestUtil.getLong(request,"id");
		AgentSetting agentSetting = agentSettingService.getById(id);	
		if(agentSetting!=null){
			if(agentSetting.getAuthtype()==AgentSetting.AUTHTYPE_CONDITION){
				List<AgentCondition> agentConditionList=agentSettingService.getAgentConditionList(id);
				if(BeanUtils.isNotEmpty(agentConditionList)){
					AgentCondition condition = agentConditionList.get(0);
					String condJsonStr = condition.getCondition();
					com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(condJsonStr);
					Long tableId = jsonObject.getLong("tableId");
					
					BpmDefinition bpmDefinition = bpmDefinitionService.getMainDefByActDefKey(agentSetting.getFlowkey());
					Long currentTableId = bpmFormDefService.getTableIdByDefId(bpmDefinition.getDefId());
					
					if(!currentTableId.equals(tableId)){
						agentConditionList.clear();
					}
				}
				agentSetting.setAgentConditionList(agentConditionList);
			}else{
				List<AgentDef> agentDefList=agentSettingService.getAgentDefList(id);
				agentSetting.setAgentDefList(agentDefList);
			}
		}
		return getAutoView().addObject("agentSetting", agentSetting);
	}
	
	
	@RequestMapping("validateSettingComplictByFlow")
	@Action(description="验证流程是否可以代理")
	@ResponseBody
	public Map<String,Object> validateSettingComplictByFlow(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		int status = 0;
		Map<String,String> msgMap = new HashMap<String, String>();
		String [] flowKeys = RequestUtil.getStringAryByStr(request, "flowKeys");
		List<String> flowKeyList = new ArrayList<String>(Arrays.asList(flowKeys));
		Long id = RequestUtil.getLong(request, "agentSettingId", 0L);
		Long authid = RequestUtil.getLong(request,"authid");
		if(null==authid||authid==0){
			authid=ContextUtil.getCurrentUserId();
		}
		Date startDate = RequestUtil.getDate(request, "startDate");
		Date endDate = RequestUtil.getDate(request, "endDate");
		if(id!=0L){
			for(String flowKey:flowKeyList){
				boolean flag = agentSettingService.isComplictAgainstPartialOrConditionByFlow(flowKey,startDate,endDate,authid,id);
				if(flag){
					msgMap.put(flowKey, "此流程已经设置代理!");
					status=-1;
					continue;
				}
			}
		}else{
			for(String flowKey:flowKeyList){
				boolean flag = agentSettingService.isComplictAgainstPartialOrConditionByFlow(flowKey,startDate,endDate,authid);
				if(flag){
					msgMap.put(flowKey,"此流程已经设置代理!");
					status=-1;
					continue;
				}
			}
		}
		
		map.put("status", status);
		map.put("msgMap", msgMap);
		
		return map;
	}
	
	@RequestMapping("validateSettingComplictAgainstGeneral")
	@Action(description="验证是否与存在的有效全权代理代理冲突")
	@ResponseBody
	public Map<String,Object> validateSettingComplictAgainstGeneral(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		int status = 0;
		String msg = null;
		Long authid = RequestUtil.getLong(request,"authid");
		if(null==authid||authid==0){
			authid=ContextUtil.getCurrentUserId();
		}
		Date startDate = RequestUtil.getDate(request, "startDate");
		Date endDate = RequestUtil.getDate(request, "endDate");
		Long id = RequestUtil.getLong(request, "agentSettingId", 0L);
		if(id!=0L){
			boolean flag = agentSettingService.isComplictAgainstGeneral(startDate, endDate, authid,id);
			if(flag){
				//此时间段内与已有的有效全权代理冲突！
				msg = "此时间段内与已有的有效全权代理冲突！";
				status=-1;
			}
		}else{
			boolean flag = agentSettingService.isComplictAgainstGeneral(startDate, endDate, authid);
			if(flag){
				//此时间段内与已有的有效全权代理冲突！
				msg =  "此时间段内与已有的有效全权代理冲突！";
				status=-1;
			}
		}
		map.put("status", status);
		map.put("msg", msg);
		
		return map;
	}
	
	@RequestMapping("validateSettingComplictAgainstAll")
	@Action(description="验证是否与存在的其它代理冲突")
	@ResponseBody
	public Map<String,Object> validateSettingComplictAgainstAll(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		int status = 0;
		String msg = null;
		Long authid = RequestUtil.getLong(request,"authid");
		if(null==authid||authid==0){
			authid=ContextUtil.getCurrentUserId();
		}
		Date startDate = RequestUtil.getDate(request, "startDate");
		Date endDate = RequestUtil.getDate(request, "endDate");
		Long id = RequestUtil.getLong(request, "agentSettingId", 0L);
		if(id!=0L){
			boolean flag = agentSettingService.validateSettingComplictAgainstAll(startDate, endDate, authid,id);
			if(flag){
				//此时间段内与已有的有效代理冲突
				msg =  "此时间段内与已有的有效代理冲突";
				status=-1;
			}
		}else{
			boolean flag = agentSettingService.validateSettingComplictAgainstAll(startDate, endDate, authid);
			if(flag){
				msg =   "此时间段内与已有的有效代理冲突";
				status=-1;
			}
		}
		map.put("status", status);
		map.put("msg", msg);
		
		return map;
	}
	
	/**
	 * 获取部分代理的代理的流程
	 * @param id
	 * @return
	 */
	@RequestMapping("getAgentDefList")
	@Action(description="获取部分代理的代理的流程")
	@ResponseBody
	public List<AgentDef> getAgentDefList(HttpServletRequest request,HttpServletResponse response){		
		Long id=RequestUtil.getLong(request,"id");
		return agentSettingService.getAgentDefList(id);
	}
}
