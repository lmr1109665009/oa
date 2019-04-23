package com.suneee.platform.controller.system;

import java.util.HashMap;
import java.util.Map;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.suneee.platform.annotion.Action;
import org.springframework.web.servlet.ModelAndView;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.util.StringUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
import com.suneee.platform.model.system.SysUser;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import com.suneee.core.util.StringUtil;

import com.suneee.platform.model.system.SysKnowledgeMark;
import com.suneee.platform.service.system.SysKnowMarkRelService;
import com.suneee.platform.service.system.SysKnowledgeMarkService;
import com.suneee.core.web.ResultMessage;
/**
 *<pre>
 * 对象功能:书签 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-07-28 10:23:42
 *</pre>
 */
@Controller
@RequestMapping("/platform/system/sysKnowledgeMark/")
public class SysKnowledgeMarkController extends BaseController
{
	@Resource
	private SysKnowledgeMarkService sysKnowledgeMarkService;
	@Resource
	private SysKnowMarkRelService sysKnowMarkRelService;
	
	
	/**
	 * 添加或更新书签。
	 * @param request
	 * @param response
	 * @param sysKnowledgeMark 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("save")
	@Action(description="添加或更新书签")
	public ResultMessage save(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		ResultMessage resultMessage = null;	
		SysKnowledgeMark sysKnowledgeMark =new SysKnowledgeMark();
		Long id = RequestUtil.getLong(request, "markId", 0L);
		String bookmark = RequestUtil.getString(request, "bookmark");
		try{
			sysKnowledgeMark.setBookmark(bookmark);
			sysKnowledgeMark.setUserId(ContextUtil.getCurrentUserId());
			sysKnowledgeMark.setId(id);
			sysKnowledgeMarkService.save(sysKnowledgeMark);
			resultMessage = new ResultMessage(ResultMessage.Success,
					"保存书签成功:");
		}catch(Exception e){
			resultMessage = new ResultMessage(ResultMessage.Fail,
					"保存书签失败:");
		}
		return resultMessage;
	}
	
	
	/**
	 * 取得书签分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看书签分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<SysKnowledgeMark> list=sysKnowledgeMarkService.getAll(new QueryFilter(request,"sysKnowledgeMarkItem"));
		boolean isSuperAdmin = ContextUtil.isSuperAdmin();
		ModelAndView mv=this.getAutoView().addObject("sysKnowledgeMarkList",list).addObject("isSuperAdmin", isSuperAdmin);
		return mv;
	}
	
	/**
	 * 删除书签
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除书签")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			sysKnowledgeMarkService.delByIds(lAryId);
			sysKnowMarkRelService.deleteByMarkIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除书签成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	

	/**
	 * 取得书签明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看书签明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		SysKnowledgeMark sysKnowledgeMark = sysKnowledgeMarkService.getById(id);	
		return getAutoView().addObject("sysKnowledgeMark", sysKnowledgeMark);
	}
	
}

