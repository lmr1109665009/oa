package com.suneee.platform.controller.bpm;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.bpm.model.ProcessTask;
import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.constant.HttpConstant;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.BpmProTransTo;
import com.suneee.platform.model.bpm.BpmProTransToAssignee;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysProperty;
import com.suneee.platform.service.bpm.BpmProTransToService;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.bpm.model.ProcessTask;
import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * <pre>
 * 对象功能:加签（转办） 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:helh
 * 创建时间:2014-04-04 17:05:35
 * </pre>
 */
@Controller
@RequestMapping("/platform/bpm/bpmProTransTo/")
@Action(ownermodel=SysAuditModelType.PROCESS_MANAGEMENT)
public class BpmProTransToController extends BaseController {
	@Resource
	private BpmProTransToService bpmProTransToService;

	@Resource
	private ProcessRunService processRunService;
	
	/**
	 * 流转事宜列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("mattersList")
	@Action(description = "流转事宜列表")
	public ModelAndView mattersList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "bpmProTransToItem");
		String nodePath = RequestUtil.getString(request, "nodePath");
		if(StringUtils.isNotEmpty(nodePath))
			filter.getFilters().put("nodePath",nodePath + "%");
		filter.addFilter("exceptDefStatus", 3);
		Long userId = ContextUtil.getCurrentUserId();
		filter.addFilter("createUserId", userId);
		List<BpmProTransTo> list = bpmProTransToService.mattersList(filter);
		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
		ModelAndView mv = this.getAutoView().addObject("bpmProTransToList", list)
				.addObject("curUserId", userId).addObject("hasGlobalFlowNo", hasGlobalFlowNo);

		return mv;
	}

	/**
	 * 查看流转人
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("showAssignee")
	@Action(description = "查看流转人")
	public ModelAndView showAssignee(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String taskId  = RequestUtil.getString(request, "taskId");
		BpmProTransTo bpmProTransTo = bpmProTransToService.getByTaskId(new Long(taskId));
		if(BeanUtils.isEmpty(bpmProTransTo)){
			return ServiceUtil.getTipInfo("流转过程已结束,毋须再添加流转人!");
		}
		List<BpmProTransToAssignee> list = bpmProTransToService.getAssignee(taskId, bpmProTransTo.getAssignee());
		ModelAndView mv = this.getAutoView().addObject("bpmProTransToAssigneeList", list);
		return mv;
	}
	
	/**
	 * 添加流转人
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("add")
	@Action(description = "添加流转人")
	@ResponseBody
	public void add(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType(HttpConstant.CONTENT_TYPE_JSON);
		PrintWriter out=response.getWriter();
		ResultMessage resultMessage=null;
		String userIds = request.getParameter("cmpIds");
		try{
			Long taskId = RequestUtil.getLong(request, "taskId");
			String opinion = request.getParameter("opinion");
			String informType=RequestUtil.getString(request, "informType");
			BpmProTransTo bpmProTransTo = bpmProTransToService.getByTaskId(taskId);
			if(BeanUtils.isEmpty(bpmProTransTo)){
				resultMessage=new ResultMessage(ResultMessage.Fail, "流转过程已结束,毋须再添加流转人!");
			}else {
				//避免添加重复流转人
				//获取所有流转人id
				List<String> assigneeIds = getAssigneeIdsByTaskId(taskId, bpmProTransTo);
				String[] cmpIds = userIds.split(",");
				String newUserIds = "";
				for (String cmpId : cmpIds) {
					if (!assigneeIds.contains(cmpId)) {
						newUserIds += cmpId + ",";
					}
				}
				if(newUserIds.length() > 0){
					bpmProTransToService.addTransTo(bpmProTransTo, taskId.toString(), opinion, newUserIds.substring(0,newUserIds.length()-1), informType);
				}
				resultMessage = new ResultMessage(ResultMessage.Success, "添加流转人成功!");
			}
		}catch (Exception e) {
			resultMessage=new ResultMessage(ResultMessage.Fail, "添加流转人失败!");
			e.printStackTrace();
		}
		out.print(resultMessage);
	}

	/**
	 * 获取所有流转人id
	 * @param taskId
	 * @param bpmProTransTo
	 * @return
	 * @throws Exception
	 */
	public List<String> getAssigneeIdsByTaskId(Long taskId,BpmProTransTo bpmProTransTo) throws Exception {
		//获取所有流转人
		List<BpmProTransToAssignee> list = bpmProTransToService.getAssignee(String.valueOf(taskId), bpmProTransTo.getAssignee());
		List<String> resultList = new ArrayList<>();
		for(BpmProTransToAssignee bptta : list){
			resultList.add(bptta.getUserId().toString());
		}
		return resultList;
	}

	@RequestMapping("addDialog")
	@Action(description="批量取消")
	public ModelAndView addDialog(HttpServletRequest request) throws Exception
	{
		Long taskId = RequestUtil.getLong(request, "taskId");
		BpmProTransTo bpmProTransTo = bpmProTransToService.getByTaskId(taskId);
		if(BeanUtils.isEmpty(bpmProTransTo)){
			return ServiceUtil.getTipInfo("流转过程已结束,不能继续添加了!");
		}
		Map<String, IMessageHandler> handlersMap=ServiceUtil.getHandlerMap();
		
		return getAutoView().addObject("handlersMap",handlersMap)
				.addObject("bpmProTransTo", bpmProTransTo);
				
	}
	
	/**
	 * 取消流转任务
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("cancel")
	@Action(description = "取消流转任务")
	@ResponseBody
	public void cancel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out=response.getWriter();
		ResultMessage resultMessage=null;
		String taskId = RequestUtil.getString(request, "taskId");
		String opinion = RequestUtil.getString(request, "opinion");
		String userId = RequestUtil.getString(request, "userId");
		String informType = RequestUtil.getString(request, "informType");
		try{
			ProcessTask processTask = processRunService.getTaskByParentIdAndUser(taskId, userId);
			if(processTask==null){
				resultMessage=new ResultMessage(ResultMessage.Fail, "此流转任务已被审批!");
			}else{
				bpmProTransToService.cancel(processTask, opinion, informType);
				resultMessage=new ResultMessage(ResultMessage.Success, "取消流转任务成功!");
			}
		}catch(Exception e){
			resultMessage=new ResultMessage(ResultMessage.Fail, "取消流转任务失败!");
			e.printStackTrace();
		}
		out.print(resultMessage);
	}
	
	/**
	 * 获取流转人及任务情况
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getByAssignee")
	@Action(description = "获取流转人及任务情况")
	@ResponseBody
	public List<BpmProTransToAssignee> getByAssignee(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String parentTaskId  = RequestUtil.getString(request, "parentTaskId");
		String assignee = RequestUtil.getString(request, "assignee");
		return bpmProTransToService.getAssignee(parentTaskId, assignee);
	}
	
	@RequestMapping("cancelDialog")
	@Action(description="批量取消")
	public ModelAndView cancelDialog(HttpServletRequest request) throws Exception
	{
		Map<String, IMessageHandler> handlersMap=ServiceUtil.getHandlerMap();
		return getAutoView()
				.addObject("handlersMap",handlersMap);
				
	}
	@RequestMapping("del")
	@Action(description = "刪除任務")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultMessage message = null;
		String preUrl = RequestUtil.getPrePage(request);
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			if(!BeanUtils.isEmpty(lAryId)) {
				for (Long p : lAryId) {
					bpmProTransToService.delByTaskId(p);
				}
			}
			message = new ResultMessage(ResultMessage.Success, "删除流程实例成功");
		} catch (Exception e) {
			message = new ResultMessage(ResultMessage.Fail, "删除流程实例失败");
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
}