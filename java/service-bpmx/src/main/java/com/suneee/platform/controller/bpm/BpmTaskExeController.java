package com.suneee.platform.controller.bpm;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.HttpConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.HttpUtil;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.bpm.BpmRunLog;
import com.suneee.platform.model.bpm.BpmTaskExe;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysProperty;
import com.suneee.platform.model.system.SysTemplate;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmRunLogService;
import com.suneee.platform.service.bpm.BpmService;
import com.suneee.platform.service.bpm.BpmTaskExeService;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.SysTemplateService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.platform.service.util.ServiceUtil;

/**
 * <pre>
 * 对象功能:任务转办代理 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2013-03-27 12:02:35
 * </pre>
 */
@Controller
@RequestMapping("/platform/bpm/bpmTaskExe/")
@Action(ownermodel=SysAuditModelType.PROCESS_MANAGEMENT)
public class BpmTaskExeController extends BaseController {
	@Resource
	private BpmTaskExeService bpmTaskExeService;
	@Resource
	private BpmService bpmService;
	@Resource
	private ProcessRunService processRunService;
	@Resource
	private  BpmDefinitionService bpmDefinitionService;
	@Resource
	private BpmRunLogService bpmRunLogService;
	@Resource
	private SysTemplateService sysTemplateService;
	@Resource
	private SysUserService sysUserService;
	
	/**
	 * 添加或更新任务转办代理。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("assignSave")
	@Action(description = "添加任务转办代理",execOrder=ActionExecOrder.AFTER,
			detail=
					"<#if StringUtil.isNotEmpty(isSuccess)>" +
					"添加任务【${SysAuditLinkService.getProcessRunDetailLink(taskId)}】转办代理给【${SysAuditLinkService.getSysUserLink(Long.valueOf(assigneeId))}】" +
					"成功" +
					"<#else>添加任务转办代理失败</#if>")
	public void assignSave(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String resultMsg = null;
		try {
			String taskId = RequestUtil.getString(request, "taskId");
			Long assigneeId = RequestUtil.getLong(request, "assigneeId");
			String assigneeName = RequestUtil.getString(request, "assigneeName");
			String opinion = RequestUtil.getString(request, "opinion");
			
			String informType = RequestUtil.getStringAry(request, "informType");
		
			
			SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
			TaskEntity taskEntity = bpmService.getTask(taskId);
			if (BeanUtils.isEmpty(taskEntity)){
				writeResultMessage(response.getWriter(), "任务已经被处理！", ResultMessage.Fail);
				return;
			}
			String assignee = taskEntity.getAssignee();
			//任务人不为空且和当前用户不同。
			if(ServiceUtil.isAssigneeNotEmpty(assignee) && assignee.equals(assigneeId)){
				writeResultMessage(response.getWriter(), "不能交办给任务执行人！",ResultMessage.Fail);
				return;
			}
			if(ServiceUtil.isAssigneeNotEmpty(assignee) ){	
				if(!assignee.equals(sysUser.getUserId().toString())){
					writeResultMessage(response.getWriter(),"对不起，交办失败。您（已）不是任务的执行人。",ResultMessage.Fail);
					return;
				}
			}
			
			
			ProcessRun processRun = processRunService.getByActInstanceId(new Long( taskEntity.getProcessInstanceId()));
			
			String actDefId=processRun.getActDefId();
			
			boolean rtn= bpmDefinitionService.allowDivert(actDefId);
			if(!rtn){
				writeResultMessage(response.getWriter(), "任务不允许进行交办！",ResultMessage.Fail);
				return;
			}
			
			/*boolean isAssign=bpmTaskExeService.getByIsAssign(new Long(taskId));
			if(isAssign){
				writeResultMessage(response.getWriter(), "已经转办,不能重复提交！",ResultMessage.Fail);
				return;
			}*/
			
			
			BpmTaskExe bpmTaskExe = new BpmTaskExe();
			bpmTaskExe.setId(UniqueIdUtil.genId());
			bpmTaskExe.setTaskId(new Long(taskId));
			bpmTaskExe.setAssigneeId(assigneeId);
			bpmTaskExe.setAssigneeName(assigneeName);
			bpmTaskExe.setOwnerId(sysUser.getUserId());
//			bpmTaskExe.setOwnerName(sysUser.getFullname());
			bpmTaskExe.setOwnerName(ContextSupportUtil.getUsername(sysUser));
			bpmTaskExe.setSubject(processRun.getSubject());
			bpmTaskExe.setStatus(BpmTaskExe.STATUS_INIT);
			bpmTaskExe.setMemo(opinion);
			bpmTaskExe.setCratetime(new Date());
			bpmTaskExe.setActInstId(new Long(taskEntity.getProcessInstanceId()));
			bpmTaskExe.setTaskDefKey(taskEntity.getTaskDefinitionKey());
			bpmTaskExe.setTaskName(taskEntity.getName());
			bpmTaskExe.setAssignType(BpmTaskExe.TYPE_TRANSMIT);
			bpmTaskExe.setRunId(processRun.getRunId());
			bpmTaskExe.setTypeId(processRun.getTypeId());
			bpmTaskExe.setInformType(informType);
			bpmTaskExeService.assignSave(bpmTaskExe);
			SysAuditThreadLocalHolder.putParamerter("isSuccess", "true");
	//		writeResultMessage(response.getWriter(), "交办成功！",ResultMessage.Success);
			com.suneee.eas.common.utils.HttpUtil.writeResponseJson(response,ResponseMessage.success("交办成功"));
		} 
		catch (Exception e) {
			e.printStackTrace();
			writeResultMessage(response.getWriter(),resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}
		
	}
	
	/**
	 * 查看转办历史明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getAssignDetail")
	@Action(description = "转办历史明细")
	public ModelAndView getAssignDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long taskId = RequestUtil.getLong(request, "taskId", 0);
		List<BpmTaskExe> list = bpmTaskExeService.getByTaskId(taskId);
		return this.getAutoView().addObject("bpmTaskExeList", list);
	}
	

	/**
	 * 转办事宜
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = " 转办事宜")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "bpmTaskExeItem");

		String nodePath = RequestUtil.getString(request, "nodePath");
		if(StringUtils.isNotEmpty(nodePath))
			filter.getFilters().put("nodePath",nodePath + "%");
		filter.addFilter("exceptDefStatus", 3);
		List<BpmTaskExe> list = bpmTaskExeService.accordingMattersList(filter);
		ModelAndView mv = this.getAutoView().addObject("bpmTaskExeList", list);

		return mv;
	}
	
	
	/**
	 * 转办事宜
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("accordingMattersList")
	@Action(description = " 转办事宜")
	public ModelAndView accordingMattersList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "bpmTaskExeItem");
		if(filter.getFilters().size()>0) {
			Object status = filter.getFilters().get("status");
			if (status!=null&&status.equals("-1")){
				status="";
				filter.addFilter("status",status);
			}
		}
		Long userId = ContextUtil.getCurrentUserId();
		filter.addFilter("ownerId", userId);

		String nodePath = RequestUtil.getString(request, "nodePath");
		if(StringUtils.isNotEmpty(nodePath))
			filter.getFilters().put("nodePath",nodePath + "%");
		
		List<BpmTaskExe> list = bpmTaskExeService.accordingMattersList(filter);
		
		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
		ModelAndView mv = this.getAutoView().addObject("bpmTaskExeList", list).addObject("hasGlobalFlowNo", hasGlobalFlowNo);;

		return mv;
	}

	/**
	 * 取得我收到的任务分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("myTaskList")
	@Action(description = "查看我收到的任务")
	public ModelAndView myTaskList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "bpmTaskExeItem");
		filter.addFilter("assigneeId", ContextUtil.getCurrentUserId());
		 filter.addFilter("assignType",BpmTaskExe.TYPE_TRANSMIT);
		List<BpmTaskExe> list = bpmTaskExeService.getAll(filter);
		ModelAndView mv = this.getAutoView().addObject("bpmTaskExeList", list);

		return mv;
	}
	
	/**
	 * 取消转办
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("cancel")
	@Action(description = "取消交办/代理")
	public void cancel(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setContentType(HttpConstant.CONTENT_TYPE_JSON);
		Long id = RequestUtil.getLong(request, "id");
		String opinion=RequestUtil.getString(request, "opinion");
		String informType=RequestUtil.getString(request, "informType");
//		SysUser sysUser = ContextUtil.getCurrentUser();

		String bpmTaskExeType=null;
		
		
		try {
			BpmTaskExe bpmTaskExe = bpmTaskExeService.getById(id);
			
			if(BpmTaskExe.TYPE_ASSIGNEE == bpmTaskExe.getAssignType()){
				bpmTaskExeType="代理";
			}else if(BpmTaskExe.TYPE_TRANSMIT == bpmTaskExe.getAssignType()){
				bpmTaskExeType="交办";
			}
			Long taskId= bpmTaskExe.getTaskId();
			TaskEntity taskEntity=bpmService.getTask(taskId.toString());
			if(taskEntity==null){
				writeResultMessage(response.getWriter(), new ResultMessage(ResultMessage.Fail,"任务已经结束!"));
				return ;
			}
			Long ownerId = bpmTaskExe.getOwnerId();
			if(ownerId==null){
				writeResultMessage(response.getWriter(), new ResultMessage(ResultMessage.Fail, "找不到原来的任务执行人!"));
				return ;
			}
			SysUser sysUser=sysUserService.getById(ownerId);
			if(sysUser==null){
				writeResultMessage(response.getWriter(), new ResultMessage(ResultMessage.Fail, "找不到原来的任务执行人!"));
				return ;
			}
			//取消转办/代理。
			bpmTaskExeService.cancel(bpmTaskExe, sysUser,opinion,informType);
			// 记录日志
			ProcessRun processRun = processRunService.getByActInstanceId(bpmTaskExe.getActInstId());
//			String memo = "流程:" + processRun.getSubject() + ", 【" + sysUser.getFullname() + "】将交办任务【" + bpmTaskExe.getSubject()+ "】 取消";
			String memo = "流程:" + processRun.getSubject() + ", 【" + ContextSupportUtil.getUsername(sysUser) + "】将交办任务【" + bpmTaskExe.getSubject()+ "】 取消";
			bpmRunLogService.addRunLog(processRun.getRunId(),BpmRunLog.OPERATOR_TYPE_BACK, memo);
			writeResultMessage(response.getWriter(), new ResultMessage(ResultMessage.Success, "任务取消"+bpmTaskExeType+"成功"));
		} catch (Exception e) {
			e.printStackTrace();
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				ResultMessage resultMessage = new ResultMessage(
						ResultMessage.Fail, "任务取消"+bpmTaskExeType+"失败:" + str);
				response.getWriter().print(resultMessage);
			} else {
				String message = ExceptionUtil.getExceptionMessage(e);
				ResultMessage resultMessage = new ResultMessage(
						ResultMessage.Fail, message);
				response.getWriter().print(resultMessage);
			}
		}

	}
	
	private void addRunLong(List<BpmTaskExe> list){
		SysUser sysUser=(SysUser) ContextUtil.getCurrentUser();
		for(BpmTaskExe bpmTaskExe:list){
//			String memo = "【" + sysUser.getFullname() + "】将转办任务【" + bpmTaskExe.getSubject()+ "】 取消";
			String memo = "【" + ContextSupportUtil.getUsername(sysUser) + "】将转办任务【" + bpmTaskExe.getSubject()+ "】 取消";
			bpmRunLogService.addRunLog(bpmTaskExe.getRunId(),BpmRunLog.OPERATOR_TYPE_BACK, memo);
		}
	}

	/**
	 * 批量取消转办
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("cancelBat")
	@Action(description = "批量取消转办")
	public void cancelBat(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
			String idStr = RequestUtil.getString(request, "ids");
			String opinion=RequestUtil.getString(request, "opinion");
			String informType=RequestUtil.getString(request, "informType");
			if(StringUtil.isEmpty(idStr)){
				HttpUtil.writeResponseJson(response,ResponseMessage.fail("请选择需要取消转办的项目!"));
				return;
			}
			List<BpmTaskExe> list= bpmTaskExeService.cancelBat(idStr, opinion, informType, sysUser);
			//添加日志。
			addRunLong(list);

			String message=MessageUtil.getMessage();
			HttpUtil.writeResponseJson(response,ResponseMessage.success(message));
		} catch (Exception e) {
			HttpUtil.writeResponseJson(response,ResponseMessage.fail("批量取消转办失败"));
		}
	}

	/**
	 * 删除任务转办代理
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除任务转办代理")
	public void del(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			bpmTaskExeService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success,  "删除任务转办代理成功!");
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败:"+ ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	/**
	 * 编辑任务转办代理
	 * 
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description = "编辑任务转办代理")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		String returnUrl = RequestUtil.getPrePage(request);
		BpmTaskExe bpmTaskExe = bpmTaskExeService.getById(id);

		return getAutoView().addObject("bpmTaskExe", bpmTaskExe).addObject(
				"returnUrl", returnUrl);
	}

	/**
	 * 取得任务转办代理明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description = "查看任务转办代理明细")
	public ModelAndView get(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		long id = RequestUtil.getLong(request, "id");
		BpmTaskExe bpmTaskExe = bpmTaskExeService.getById(id);
		return getAutoView().addObject("bpmTaskExe", bpmTaskExe);
	}

	/**
	 * 转办窗口
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("assign")
	@Action(description = "转办窗口")
	public ModelAndView assign(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String taskId = RequestUtil.getString(request, "taskId");
		SysTemplate sysTemplate = sysTemplateService.getDefaultByUseType(SysTemplate.USE_TYPE_DELEGATE);
		
		Map<String, IMessageHandler> handlersMap=ServiceUtil.getHandlerMap();
		return this.getAutoView()
				.addObject("taskId", taskId)
				.addObject("sysTemplate", sysTemplate)
				.addObject("handlersMap",handlersMap);
	}
	
	
	/**
	 * 是否允许转办
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("isAssigneeTask")
	@Action(description = "是否允许转办")
	public void isAssigneeTask(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long taskId = RequestUtil.getLong(request, "taskId");
		ResultMessage result  = null;
		if(!bpmTaskExeService.isAssigneeTask(taskId))
			result = new ResultMessage(ResultMessage.Success, "允许!");
		else
			result = new ResultMessage(ResultMessage.Fail, "不允许!");
	
		writeResultMessage(response.getWriter(), result);
	}
	
	@RequestMapping("batCancel")
	@Action(description="批量取消")
	public ModelAndView batCancel(HttpServletRequest request) throws Exception
	{
		Map<String, IMessageHandler> handlersMap=ServiceUtil.getHandlerMap();
		return getAutoView()
				.addObject("handlersMap",handlersMap);
				
	}
	
	@RequestMapping("cancelDialog")
	@Action(description="批量取消")
	public ModelAndView cancelDialog(HttpServletRequest request) throws Exception
	{
		Map<String, IMessageHandler> handlersMap=ServiceUtil.getHandlerMap();
		return getAutoView()
				.addObject("handlersMap",handlersMap);
				
	}
}
