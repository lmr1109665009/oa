package com.suneee.platform.controller.system;

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
import com.suneee.platform.model.system.MessageLinkmanGroup;
import com.suneee.platform.service.system.MessageLinkmanGroupService;
/**
 *<pre>
 * 对象功能:常用联系人组 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ouxb
 * 创建时间:2015-07-29 10:29:57
 *</pre>
 */
@Controller
@RequestMapping("/platform/system/messageLinkmanGroup/")
public class MessageLinkmanGroupController extends BaseController
{
	@Resource
	private MessageLinkmanGroupService messageLinkmanGroupService;
	
	
	/**
	 * 添加或更新常用联系人组。
	 * @param request
	 * @param response
	 * @param messageLinkmanGroup 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新常用联系人组")
	public void save(HttpServletRequest request, HttpServletResponse response,MessageLinkmanGroup messageLinkmanGroup) throws Exception
	{
		String resultMsg=null;		
		try{
			if(messageLinkmanGroup.getId()==null||messageLinkmanGroup.getId()==0){
				messageLinkmanGroupService.save(messageLinkmanGroup);
				resultMsg=getText("添加","常用联系人组");
			}else{
			    messageLinkmanGroupService.save(messageLinkmanGroup);
				resultMsg=getText("更新","常用联系人组");
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得常用联系人组分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看常用联系人组分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		QueryFilter queryFilter = new QueryFilter(request,"messageLinkmanGroupItem");
		queryFilter.addFilter("creatorId", ContextUtil.getCurrentUserId());
		List<MessageLinkmanGroup> list=messageLinkmanGroupService.getAll(queryFilter);
		ModelAndView mv=this.getAutoView().addObject("messageLinkmanGroupList",list);
		return mv;
	}
	
	/**
	 * 删除常用联系人组
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除常用联系人组")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			messageLinkmanGroupService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除常用联系人组成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑常用联系人组
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑常用联系人组")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		MessageLinkmanGroup messageLinkmanGroup=messageLinkmanGroupService.getById(id);
		
		return getAutoView().addObject("messageLinkmanGroup",messageLinkmanGroup)
							.addObject("returnUrl",returnUrl);
	}

	/**
	 * 取得常用联系人组明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看常用联系人组明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		MessageLinkmanGroup messageLinkmanGroup = messageLinkmanGroupService.getById(id);	
		return getAutoView().addObject("messageLinkmanGroup", messageLinkmanGroup);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("selector")
	@Action(description="查看常用联系人组分页列表")
	public ModelAndView selector(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		QueryFilter queryFilter = new QueryFilter(request,"messageLinkmanGroupItem");
		queryFilter.addFilter("creatorId", ContextUtil.getCurrentUserId());
		List<MessageLinkmanGroup> list=messageLinkmanGroupService.getAll(queryFilter);
		ModelAndView mv=this.getAutoView().addObject("messageLinkmanGroupList",list);
		return mv;
	}
	
}

