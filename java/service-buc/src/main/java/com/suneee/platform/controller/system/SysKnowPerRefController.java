package com.suneee.platform.controller.system;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import net.sf.json.JSONArray;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysKnowPerRef;
import com.suneee.platform.model.system.SysKnowledgePer;
import com.suneee.platform.service.system.SysKnowPerRefService;
import com.suneee.platform.service.system.SysKnowledgePerService;
/**
 *<pre>
 * 对象功能:权限关联 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:dyg
 * 创建时间:2015-12-31 16:05:42
 *</pre>
 */
@Controller
@RequestMapping("/platform/system/sysKnowPerRef/")
public class SysKnowPerRefController extends BaseController
{
	@Resource
	private SysKnowPerRefService sysKnowPerRefService;
	@Resource
	private SysKnowledgePerService sysKnowledgePerService;
	
	
	/**
	 * 添加或更新权限关联。
	 * @param request
	 * @param response
	 * @param sysKnowPerRef 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新权限关联")
	public void save(HttpServletRequest request, HttpServletResponse response,SysKnowPerRef sysKnowPerRef) throws Exception
	{
		String sysKnowObj = RequestUtil.getString(request, "sysKnowObj");
		String resultMsg=null;		
		try{
			String msg = sysKnowPerRefService.save(sysKnowPerRef,sysKnowObj);
			resultMsg=getText(msg,"权限关联");
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			e.printStackTrace();
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得权限关联分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看权限关联分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<SysKnowPerRef> list=sysKnowPerRefService.getAll(new QueryFilter(request,"sysKnowPerRefItem"));
		ModelAndView mv=this.getAutoView().addObject("sysKnowPerRefList",list);
		return mv;
	}
	
	/**
	 * 删除权限关联
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除权限关联")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			sysKnowPerRefService.delByIds(lAryId);
			sysKnowledgePerService.delByRefIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除权限关联成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑权限关联
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑权限关联")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/platform/system/sysKnowledgePerEdit");
		Long id=RequestUtil.getLong(request,"id",0L);
		List<SysKnowledgePer> perList =  sysKnowledgePerService.getByRefId(id);
		JSONArray perObjList = JSONArray.fromObject(perList);
		SysKnowPerRef sysKnowPerRef=sysKnowPerRefService.getById(id);
		return mv.addObject("sysKnowPerRef",sysKnowPerRef)
				.addObject("perObjList", perObjList);
	}

	/**
	 * 取得权限关联明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看权限关联明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/platform/system/sysKnowledgePerGet");
		Long id=RequestUtil.getLong(request,"id",0L);
		List<SysKnowledgePer> perList =  sysKnowledgePerService.getByRefId(id);
		JSONArray perObjList = JSONArray.fromObject(perList);
		SysKnowPerRef sysKnowPerRef=sysKnowPerRefService.getById(id);
		return mv.addObject("sysKnowPerRef", sysKnowPerRef)
				.addObject("perObjList",perObjList);
	}
	
}

