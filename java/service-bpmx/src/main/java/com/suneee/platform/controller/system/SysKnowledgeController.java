package com.suneee.platform.controller.system;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.model.CurrentUser;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.model.CurrentUser;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysKnowledge;
import com.suneee.platform.model.system.SysKnowledgeMark;
import com.suneee.platform.service.system.SysKnowledgeMarkService;
import com.suneee.platform.service.system.SysKnowledgePerService;
import com.suneee.platform.service.system.SysKnowledgeService;
import com.suneee.platform.service.util.ServiceUtil;
/**
 *<pre>
 * 对象功能:知识库 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:dyg
 * 创建时间:2015-07-28 10:15:59
 *</pre>
 */
@Controller
@RequestMapping("/platform/system/sysKnowledge/")
public class SysKnowledgeController extends BaseController
{
	@Resource
	private SysKnowledgeService sysKnowledgeService;
	@Resource
	private SysKnowledgePerService sysKnowledgePerService;
	@Resource
	private SysKnowledgeMarkService sysKnowledgeMarkService;
	
	
	/**
	 * 添加或更新知识库。
	 * @param request
	 * @param response
	 * @param sysKnowledge 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新知识库")
	public void save(HttpServletRequest request, HttpServletResponse response,SysKnowledge sysKnowledge) throws Exception
	{
		String resultMsg=null;
		String markStr = RequestUtil.getString(request, "markStr");
		try{
			resultMsg = sysKnowledgeService.save(sysKnowledge,markStr);
			resultMsg = getText(resultMsg,"知识库");
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得知识库分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看知识库分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		Long typeId = RequestUtil.getLong(request, "typeId",0);
		ModelAndView mv=this.getAutoView();
		boolean isSuperAdmin = ContextUtil.isSuperAdmin();
		mv.addObject("isSuperAdmin",isSuperAdmin);
		mv.addObject("typeId",typeId);
		List<SysKnowledge> sysKnowledgeList = new ArrayList<SysKnowledge>();
		if(isSuperAdmin){//如果是超级管理员，则全部返回
			sysKnowledgeList=sysKnowledgeService.getByTypeId(new QueryFilter(request,"sysKnowledgeItem"),typeId);
			mv.addObject("sysKnowledgeList",sysKnowledgeList);
			return mv;
		}else{
			CurrentUser currentUser = ServiceUtil.getCurrentUser();
			Map<Long,String> map = sysKnowledgePerService.getUserTypePer(currentUser);
			if(map.containsKey(typeId)){//如果不是超级管理员，则判断是否有权限
				if(BeanUtils.isEmpty(map.get(typeId))){
					return mv;
				}
				String[] perArray =  map.get(typeId).split(",");
				sysKnowledgeList=sysKnowledgeService.getByTypeId(new QueryFilter(request,"sysKnowledgeItem"),typeId);
				mv.addObject("sysKnowledgeList",sysKnowledgeList);
				mv.addObject("permissionList",Arrays.asList(perArray));
				return mv;
			}else{
				ResultMessage message=new ResultMessage(ResultMessage.Fail, "没有访问权限");
				addMessage(message, request);
				return mv;
			}
		}
	}

	/**
	 * 删除知识库
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除知识库")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			sysKnowledgeService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除知识库成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	
	/**
	 * 删除知识库
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("delByType")
	@Action(description="根据分类删除知识库")
	public void delByType(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long  typeId =  RequestUtil.getLong(request, "typeId", 0L);
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			sysKnowledgeService.delByType(typeId);
			message=new ResultMessage(ResultMessage.Success, "删除知识库成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	/**
	 * 	编辑知识库
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑知识库")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long typeId = RequestUtil.getLong(request, "typeId",0);
		Long id=RequestUtil.getLong(request,"id",0L);
		SysKnowledge sysKnowledge=sysKnowledgeService.getById(id);
		List<SysKnowledgeMark> markList = sysKnowledgeMarkService.getByKnowId(id);//获取书签
		List<String> martStrList = new ArrayList<String>();
		for(SysKnowledgeMark sysKnowledgeMark:markList){
			martStrList.add(sysKnowledgeMark.getBookmark());
		}
		String markStr = StringUtils.join(martStrList.toArray(), ",");
		if(BeanUtils.isEmpty(sysKnowledge)){
			sysKnowledge = new SysKnowledge();
			sysKnowledge.setTypeid(typeId);
		}
		return getAutoView().addObject("sysKnowledge",sysKnowledge)
							.addObject("markStr",markStr);
	}

	/**
	 * 取得知识库明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看知识库明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		SysKnowledge sysKnowledge = sysKnowledgeService.getById(id);	
		List<SysKnowledgeMark> knowMarkList = sysKnowledgeMarkService.getByKnowId(id);
		return getAutoView().addObject("sysKnowledge", sysKnowledge)
							.addObject("knowMarkList", knowMarkList);
	}
	
	/**
	 * 设置分类
	 * @param request
	 * @param respons
	 * @throws Exception
	 */
	@RequestMapping("setCategory")
	@Action(description="设置分类")
	public void setCategory(HttpServletRequest request, HttpServletResponse respons)throws Exception
	{
		PrintWriter out =  respons.getWriter();
		String knowKeys = RequestUtil.getString(request, "knowKeys");
		Long typeId =  RequestUtil.getLong(request, "typeId");
		if (typeId == 0L) {
			writeResultMessage(
					out,
					new ResultMessage(
							ResultMessage.Fail,
							getText("controller.sysKnowledge.setCategory.notCategoryId")));
			return;
		}
		if(knowKeys.equals("")){
			writeResultMessage(
					out,
					new ResultMessage(
							ResultMessage.Fail,
							getText("controller.sysKnowledge.setCategory.notknowKeys")));
			return;
		}
		String arayKey[] = knowKeys.split(",");
		List<Long> list = new ArrayList<Long>();
		for(String knowKey :arayKey ){
			list.add(Long.valueOf(knowKey));
		}
		try{
			sysKnowledgeService.updateType(typeId,list);
			writeResultMessage(
					out,
					new ResultMessage(
							ResultMessage.Success,
							getText("controller.sysKnowledge.setCategory.success")));
		}catch(Exception e){
			e.printStackTrace();
			String msg = ExceptionUtil.getExceptionMessage(e);
			writeResultMessage(out, new ResultMessage(ResultMessage.Fail,
					msg));
		}
	}
	/**
	 * 拥有相同书签的文章列表
	 * @param request
	 * @param respons
	 * @throws Exception
	 */
	@RequestMapping("knowDialog")
	@Action(description="拥有相同书签的文章列表")
	public ModelAndView knowDialog(HttpServletRequest request, HttpServletResponse respons)throws Exception
	{
		Long markId = RequestUtil.getLong(request, "markId", 0L);
		List<SysKnowledge> knowList = sysKnowledgeService.getByMarkId(markId);
		return getAutoView().addObject("knowList", knowList);
	}
	
}

