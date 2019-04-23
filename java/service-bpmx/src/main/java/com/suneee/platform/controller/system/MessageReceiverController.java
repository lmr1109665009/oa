package com.suneee.platform.controller.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.page.PageList;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.MessageReceiver;
import com.suneee.platform.model.system.MessageSend;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.system.MessageReceiverService;
import com.suneee.platform.service.system.MessageSendService;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.page.PageList;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.ucp.base.common.ResultConst;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象功能:消息接收者 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2012-01-14 15:13:00
 */
@Controller
@RequestMapping("/platform/system/messageReceiver/")
@Action(ownermodel=SysAuditModelType.USER_MANAGEMENT)
public class MessageReceiverController extends BaseController
{
	@Resource
	private MessageReceiverService receiverService;
	@Resource
	private MessageSendService sendService;
	
	/**
	 * 取得消息接收者分页列表
	 * @param request
	 * @param response
	 * @return   
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看消息接收者分页列表",detail="查看消息接收者分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		QueryFilter queryFilter=new QueryFilter(request,"messageReceiverItem");
		queryFilter.addFilter("receiverId", ContextUtil.getCurrentUserId());
		List<MessageSend> list=sendService.getReceiverByUser(queryFilter);
		ModelAndView mv=this.getAutoView().addObject("messageReceiverList",list).addObject("pam", queryFilter.getFilters());
		
		return mv;
	}

	/**
	 * 获取未读消息分页列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("listByPage")
	@ResponseBody
	public Object listByPage(HttpServletRequest request,HttpServletResponse response) throws Exception
	{
		QueryFilter queryFilter=new QueryFilter(request,true);
		queryFilter.addFilter("receiverId", ContextUtil.getCurrentUserId());
		List<MessageSend> list=sendService.getReceiverByUser(queryFilter);
		PageList<MessageSend> resultList = (PageList<MessageSend>) list;
		return PageUtil.getPageVo(resultList);
	}
	


	/**
	 * 删除消息接收者
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除消息接收者",
			execOrder=ActionExecOrder.BEFORE,
			detail="删除消息接收者"+
					"<#list StringUtils.split(id,\",\") as item>" +
					"<#assign entity=messageReceiverService.getById(Long.valueOf(item))/>" +
					"【${entity.receiver}】"+
					"</#list>")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			receiverService.delByIdsCascade(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除消息接收者成功!");
		}
		catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败:" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	@RequestMapping("edit")
	@Action(description="添加或编辑消息接收者",detail="<#if isAdd>添加消息接收者<#else>编辑消息接收者" +
			"<#assign entity=messageReceiverService.getById(Long.valueOf(id))/>" +
			"【${entity.receiver}】</#if>")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		String returnUrl=RequestUtil.getPrePage(request);
		MessageReceiver messageReceiver=null;
		boolean isadd=true;
		if(id!=0){
			 messageReceiver= receiverService.getById(id);
			 isadd=false;
		}else{
			messageReceiver=new MessageReceiver();
		}
		SysAuditThreadLocalHolder.putParamerter("isAdd", isadd);
		return getAutoView().addObject("messageReceiver",messageReceiver).addObject("returnUrl", returnUrl);
	}

	/**
	 * 取得消息接收者明细
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看消息接收者明细",detail="查看消息接收者明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		long id=RequestUtil.getLong(request,"id");
		long canReturn=RequestUtil.getLong(request,"canReturn",0);
		MessageReceiver messageReceiver = receiverService.getById(id);		
		return getAutoView().addObject("messageReceiver", messageReceiver).addObject("canReturn", canReturn);
	}
	
	/**
	 * 标记消息为已读
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("mark")
	@ResponseBody
	@Action(description="标记消息为已读",detail="标记消息为已读")
	public String mark(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long[] lAryId =RequestUtil.getLongAryByStr(request, "ids");
		JSONObject jobject = new JSONObject();
		try{
			receiverService.updateReadStatus(lAryId);
			jobject.accumulate("result", true)
			   .accumulate("message", "成功标记为已读!");
		}
		catch(Exception ex){
			jobject.accumulate("result", false)
			   .accumulate("message", "删除失败:" + ex.getMessage());
		
		}
		return jobject.toString();
	}

	/**
	 * 获取站内消息列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getMessageList")
	@ResponseBody
	public ResultVo getMessageList(HttpServletRequest request, HttpServletResponse response){
		// 构造查询条件
		JSONObject json = new JSONObject();
		json.accumulate("currentPage", RequestUtil.getString(request, "currentPage", "1"));
		json.accumulate("pageSize", RequestUtil.getString(request, "pageSize", "20"));
		json.accumulate("receiverId", ContextUtil.getCurrentUserId());
		try {
			PageList<MessageSend> list = (PageList<MessageSend>)sendService.getReceiverByUser(new QueryFilter(json));
			return new ResultVo(ResultConst.COMMON_STATUS_SUCCESS, "获取站内消息列表成功!", PageUtil.getPageVo(list));
		} catch (Exception e) {
			return new ResultVo(ResultConst.COMMON_STATUS_FAILED, "获取站内消息列表失败!");
		}
	}
}
