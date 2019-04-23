package com.suneee.platform.controller.system;

import java.util.HashMap;
import java.util.Map;
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

import com.suneee.platform.model.system.SysKnowMarkRel;
import com.suneee.platform.service.system.SysKnowMarkRelService;
import com.suneee.core.web.ResultMessage;
/**
 *<pre>
 * 对象功能:SYS_KNOW_MARK_REL 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-12-29 14:25:15
 *</pre>
 */
@Controller
@RequestMapping("/platform/system/sysKnowMarkRel/")
public class SysKnowMarkRelController extends BaseController
{
	@Resource
	private SysKnowMarkRelService sysKnowMarkRelService;
	
	
	/**
	 * 添加或更新SYS_KNOW_MARK_REL。
	 * @param request
	 * @param response
	 * @param sysKnowMarkRel 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新SYS_KNOW_MARK_REL")
	public void save(HttpServletRequest request, HttpServletResponse response,SysKnowMarkRel sysKnowMarkRel) throws Exception
	{
		String resultMsg=null;		
		try{
			if(sysKnowMarkRel.getId()==null||sysKnowMarkRel.getId()==0){
				sysKnowMarkRelService.save(sysKnowMarkRel);
				resultMsg=getText("添加","SYS_KNOW_MARK_REL");
			}else{
			    sysKnowMarkRelService.save(sysKnowMarkRel);
				resultMsg=getText("更新","SYS_KNOW_MARK_REL");
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得SYS_KNOW_MARK_REL分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看SYS_KNOW_MARK_REL分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<SysKnowMarkRel> list=sysKnowMarkRelService.getAll(new QueryFilter(request,"sysKnowMarkRelItem"));
		ModelAndView mv=this.getAutoView().addObject("sysKnowMarkRelList",list);
		return mv;
	}
	
	/**
	 * 删除SYS_KNOW_MARK_REL
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除SYS_KNOW_MARK_REL")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			sysKnowMarkRelService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除SYS_KNOW_MARK_REL成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑SYS_KNOW_MARK_REL
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑SYS_KNOW_MARK_REL")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		
		SysKnowMarkRel sysKnowMarkRel=sysKnowMarkRelService.getById(id);
		
		return getAutoView().addObject("sysKnowMarkRel",sysKnowMarkRel)
							.addObject("returnUrl",returnUrl);
	}

	/**
	 * 取得SYS_KNOW_MARK_REL明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看SYS_KNOW_MARK_REL明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		SysKnowMarkRel sysKnowMarkRel = sysKnowMarkRelService.getById(id);	
		return getAutoView().addObject("sysKnowMarkRel", sysKnowMarkRel);
	}
	
}

