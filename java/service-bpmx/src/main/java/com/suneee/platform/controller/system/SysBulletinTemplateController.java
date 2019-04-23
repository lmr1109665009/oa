package com.suneee.platform.controller.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.platform.annotion.Action;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.system.SysBulletinTemplate;
import com.suneee.platform.service.system.SysBulletinTemplateService;
/**
 * 对象功能:公告模板 控制器类
 */
@Controller
@RequestMapping("/platform/system/sysBulletinTemplate/")
public class SysBulletinTemplateController extends BaseController
{
	@Resource
	private SysBulletinTemplateService sysBulletinTemplateService;
	
	/**
	 * 添加或更新公告模板。
	 * @param request
	 * @param response
	 * @param csbd 添加或更新的实体
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新公告模板")
	public void save(HttpServletRequest request, HttpServletResponse response,SysBulletinTemplate sysbulletintemplate) throws Exception
	{
		String resultMsg=null;		
		try{
			if(sysbulletintemplate.getId()==null){
				sysBulletinTemplateService.save(sysbulletintemplate);
				resultMsg=getText("添加","公告模板");
			}else{
			    sysBulletinTemplateService.save(sysbulletintemplate);
				resultMsg=getText("更新","公告模板");
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(DuplicateKeyException ex){
			writeResultMessage(response.getWriter(),"该栏目别名已存在.",ResultMessage.Fail);
			}
		catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	/**
	 * 取得公告模板分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看公告模板分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		
		QueryFilter filter = new QueryFilter(request, "sysbulletintemplateItem");
		List<SysBulletinTemplate> list=sysBulletinTemplateService.getAllList(filter);
		ModelAndView mv=this.getAutoView().addObject("sysbulletintemplateList",list);
		return mv;
	}
	
	/**
	 * 删除公告模板
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除公告模板")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[]  lAryId=RequestUtil.getLongAryByStr(request,"id");
			sysBulletinTemplateService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除公告模板成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑公告模板
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑公告模板")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		
		String returnUrl=RequestUtil.getPrePage(request);
		SysBulletinTemplate sysbulletintemplate=sysBulletinTemplateService.getById(id);
		return getAutoView().addObject("sysbulletintemplate",sysbulletintemplate)
							.addObject("returnUrl",returnUrl);
	}

	/**
	 * 取得公告模板明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看公告模板明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		SysBulletinTemplate sysbulletintemplate=sysBulletinTemplateService.getById(id);
		return getAutoView().addObject("sysbulletintemplate", sysbulletintemplate);
	}
	
	
	/**
	 * ajax请求
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("selector")
	@Action(description="ajax请求")
	@ResponseBody
	public Map<String, SysBulletinTemplate> selector(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		Map<String, SysBulletinTemplate> map = new HashMap<String, SysBulletinTemplate>();
		Long id=RequestUtil.getLong(request,"id");
		SysBulletinTemplate sysbulletintemplate=sysBulletinTemplateService.getById(id);
		map.put("sysbulletintemplate", sysbulletintemplate);
		return map;
	}
}