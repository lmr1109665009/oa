package com.suneee.platform.controller.oa;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import net.sf.json.JSONArray;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.dao.oa.OaLinkmanDao;
import com.suneee.platform.model.mail.OutMailLinkman;
import com.suneee.platform.model.oa.OaLinkman;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.mail.OutMailLinkmanService;
import com.suneee.platform.service.oa.OaLinkmanService;
import com.suneee.platform.service.system.SysUserService;
/**
 *<pre>
 * 对象功能:联系人 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-07-14 09:13:58
 *</pre>
 */
@Controller
@RequestMapping("/platform/oa/oaLinkman/")
public class OaLinkmanController extends BaseController
{
	@Resource
	private OaLinkmanService oaLinkmanService;
	@Resource
	private SysUserService sysUserservice;
	@Resource
	private OaLinkmanDao oaLinkmanDao;
	@Resource
	private OutMailLinkmanService outMailLinkmanService;
	
	/**
	 * 添加或更新联系人。
	 * @param request
	 * @param response
	 * @param oaLinkman 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更联系人表")
	public void save(HttpServletRequest request, HttpServletResponse response,OaLinkman oaLinkman) throws Exception
	{
		String resultMsg=null;
		Long userId = ContextUtil.getCurrentUserId();
		try{
			if(oaLinkman.getId()==null||oaLinkman.getId()==0){
				oaLinkmanService.save(oaLinkman);
				resultMsg=getText("添加","添加联系人成功");
			}else{
			    oaLinkmanService.save(oaLinkman);
				resultMsg=getText("更新","更新联系人成功");
			}
			//看最近联系人中是否已经给此人发送过信息   有 则更新out_mail_linkman中相应的linkname
			OutMailLinkman man = outMailLinkmanService.findLinkMan(oaLinkman.getEmail(), userId);
			if(man != null){
				man.setLinkName(oaLinkman.getName());
				outMailLinkmanService.update(man);
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得联系人分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看联系人分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		Long id = ContextUtil.getCurrentUserId();
		QueryFilter queryFilter= new QueryFilter(request,"oaLinkmanItem");
		List<OaLinkman> list=oaLinkmanService.getByUserId(queryFilter,id);
		ModelAndView mv=this.getAutoView().addObject("oaLinkmanList",list);
		return mv;
	}
	
	/**
	 * 删除联系人
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除联系人")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			oaLinkmanService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除联系人成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑联系人
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑联系人")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		OaLinkman oaLinkman = new OaLinkman();
		oaLinkman=oaLinkmanService.getById(id);
		return getAutoView().addObject("oaLinkman",oaLinkman)
							.addObject("returnUrl",returnUrl);
	}

	/**
	 * 取得联系人明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看联系人明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		OaLinkman oaLinkman = oaLinkmanService.getById(id);	
		return getAutoView().addObject("oaLinkman", oaLinkman);
	}
	/**
	 * 取得联系人明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("selector")
	@Action(description="联系人选择框")
	public ModelAndView selector(HttpServletRequest request, HttpServletResponse response) throws Exception
	{  
		Long id = ContextUtil.getCurrentUserId();
		QueryFilter queryFilter= new QueryFilter(request,"oaLinkmanItem");
		List<OaLinkman> list =  oaLinkmanService.getSelectorList(queryFilter,id);
		ModelAndView mv=this.getAutoView().addObject("oaLinkmanList",list);
		return mv;
	}
	
	/**
	 * 添加新联系人
	 * @param request   
	 * @param response
	 * @return 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("addNew")
	@Action(description="添加新联系人")
	public ModelAndView addNew(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String email = RequestUtil.getString(request,"emailName");
		Long userId = ContextUtil.getCurrentUserId();
		OaLinkman oaLinkman = new OaLinkman();
		if(!oaLinkmanService.isOaLinkExist(userId, email)){//添加新联系人
			oaLinkman.setEmail(email);
			oaLinkman.setUserid(userId);
			Date date=new Date(System.currentTimeMillis());
			oaLinkman.setCreatetime(date);
		}else{
			oaLinkman = oaLinkmanDao.getByUserEmail(userId, email).get(0);
		}
		ModelAndView mv=getAutoView();
		return mv.addObject("oaLinkman",oaLinkman);
	}

}
