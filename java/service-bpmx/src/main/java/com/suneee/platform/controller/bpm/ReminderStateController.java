package com.suneee.platform.controller.bpm;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.suneee.platform.annotion.Action;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.bpm.ReminderState;
import com.suneee.platform.service.bpm.ReminderStateService;
import com.suneee.platform.service.bpm.thread.MessageUtil;

/**
 * 对象功能:任务催办执行情况 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-02-17 17:17:37
 */
@Controller
@RequestMapping("/platform/bpm/reminderState/")
public class ReminderStateController extends BaseController
{
	@Resource
	private ReminderStateService reminderStateService;
	
	/**
	 * 取得任务催办执行情况分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看任务催办执行情况分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<ReminderState> list=reminderStateService.getAll(new QueryFilter(request,"reminderStateItem"));
		ModelAndView mv=this.getAutoView().addObject("reminderStateList",list);
		
		return mv;
	}
	
	/**
	 * 删除任务催办执行情况
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除任务催办执行情况")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage resultMessage=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			reminderStateService.delByIds(lAryId);
			resultMessage=new ResultMessage(ResultMessage.Success, "删除任务催办执行情况成功!");
		}
		catch(Exception ex){
			resultMessage = new ResultMessage(ResultMessage.Fail,"删除任务催办执行情况失败:" + ex.getMessage());
		}
		addMessage(resultMessage, request);
		response.sendRedirect(preUrl);
	}

	@RequestMapping("edit")
	@Action(description="编辑任务催办执行情况")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		String returnUrl=RequestUtil.getPrePage(request);
		ReminderState reminderState=null;
		if(id!=0){
			 reminderState= reminderStateService.getById(id);
		}else{
			reminderState=new ReminderState();
		}
		return getAutoView().addObject("reminderState",reminderState).addObject("returnUrl", returnUrl);
	}

	/**
	 * 取得任务催办执行情况明细
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看任务催办执行情况明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		long id=RequestUtil.getLong(request,"id");
		ReminderState reminderState = reminderStateService.getById(id);		
		return getAutoView().addObject("reminderState", reminderState);
	}

}
