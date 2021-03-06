package com.suneee.platform.controller.bpm;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.bpm.BpmRunLog;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.bpm.BpmRunLogService;
/**
 * 对象功能:流程运行日志 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:heyifan
 * 创建时间:2012-08-06 13:56:42
 */
@Controller
@RequestMapping("/platform/bpm/bpmRunLog/")
@Action(ownermodel=SysAuditModelType.PROCESS_MANAGEMENT)
public class BpmRunLogController extends BaseController
{
	@Resource
	private BpmRunLogService bpmRunLogService;
	
	/**
	 * 取得流程运行日志分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看流程运行日志分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{
		Long runId= RequestUtil.getLong(request, "runId");
		QueryFilter filter=new QueryFilter(request,"bpmRunLogItem");
		if(runId>0)filter.addFilter("runid", runId);
		if(null!=filter.getFilters().get("operatortype")&&filter.getFilters().get("operatortype").equals("-1")){
			filter.getFilters().put("operatortype",null);
		}
		List<BpmRunLog> list=bpmRunLogService.getAll(filter);
		ModelAndView mv=this.getAutoView().addObject("bpmRunLogList",list).addObject("runId",runId);		
		return mv;
	}
	
	/**
	 * 取得当前用户的流程运行日志分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("mylist")
	@Action(description="查看流程运行日志分页列表")
	public ModelAndView mylist(HttpServletRequest request,HttpServletResponse response) throws Exception
	{
		Long userId= ContextUtil.getCurrentUserId();
		QueryFilter filter=new QueryFilter(request,"bpmRunLogItem");
		filter.addFilter("userid", userId);
		List<BpmRunLog> list=bpmRunLogService.getAll(filter);
		ModelAndView mv=this.getAutoView().addObject("bpmRunLogList",list);
		return mv;
	}

	/**
	 * 删除流程运行日志
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除流程运行日志",
			execOrder=ActionExecOrder.BEFORE,
	        detail="删除流程<#list StringUtils.split(id,\",\") as item>" +
					  "<#assign entity=bpmRunLogService.getById(Long.valueOf(item))/>" +
					  "【${entity.processSubject}】" +
				   "</#list>的运行日志"
    )
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage resultMessage=null;
		try{
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			bpmRunLogService.delByIds(lAryId);
			resultMessage = new ResultMessage(ResultMessage.Success, "删除流程运行日志成功!");
		}
		catch(Exception ex){
			resultMessage = new ResultMessage(ResultMessage.Fail,"删除流程运行日志失败:" + ex.getMessage());
		}
		addMessage(resultMessage, request);
		response.sendRedirect(preUrl);
	}

	/**
	 * 取得流程运行日志明细
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看流程运行日志明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		long id=RequestUtil.getLong(request,"id");
		long canReturn=RequestUtil.getLong(request,"canReturn",0);
		BpmRunLog bpmRunLog = bpmRunLogService.getById(id);		
		return getAutoView().addObject("bpmRunLog", bpmRunLog).addObject("canReturn", canReturn);
	}

}
