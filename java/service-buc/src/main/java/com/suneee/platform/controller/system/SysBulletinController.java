package com.suneee.platform.controller.system;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysBulletin;
import com.suneee.platform.model.system.SysBulletinColumn;
import com.suneee.platform.model.system.SysBulletinTemplate;
import com.suneee.platform.model.system.SysPropertyConstants;
import com.suneee.platform.model.system.SysReadRecode;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysBulletinColumnService;
import com.suneee.platform.service.system.SysBulletinService;
import com.suneee.platform.service.system.SysBulletinTemplateService;
import com.suneee.platform.service.system.SysReadRecodeService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.me.model.SysNews;

/**
 * 对象功能:公告表 控制器类
 */
@Controller
@RequestMapping("/platform/system/sysBulletin/")
public class SysBulletinController extends UcpBaseController {
	@Resource
	private SysBulletinService sysBulletinService;
	@Resource
	private SysBulletinColumnService sysBulletinColumnService;
	@Resource
	private SysBulletinTemplateService sysBulletinTemplateService;
	@Resource
	private SysReadRecodeService sysReadRecodeService;

	

	/**
	 * 列表数据
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看公告表分页列表")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long companyId = ContextUtil.getCurrentCompanyId();
		Boolean isSuperAdmin = ContextUtil.isSuperAdmin();
		
		QueryFilter filter = new QueryFilter(request, "sysBulletinItem");
		filter.addFilter("companyId", companyId);
		filter.addFilter("isSuperAdmin", isSuperAdmin);
		List<SysBulletin> list = sysBulletinService.getAll(filter);
		// 有权限的栏目
		List<SysBulletinColumn> columnList = sysBulletinColumnService
				.getColumn(companyId, isSuperAdmin);
		Long test = ContextUtil.getCurrentUserId();
		ModelAndView mv = this.getAutoView().addObject("sysBulletinList", list)
				.addObject("columnList", columnList)
				.addObject("isSuperAdmin", isSuperAdmin)
				.addObject("currentUserId", ContextUtil.getCurrentUserId());
		return mv;
	}
	
	@RequestMapping("getMylist")
	@Action(description = "查看我的公告表分页列表")
	public ModelAndView getMylist(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long companyId = ContextUtil.getCurrentCompanyId();
		Boolean isSuperAdmin = ContextUtil.isSuperAdmin();
		
		QueryFilter filter = new QueryFilter(request, "sysBulletinItem");
		filter.addFilter("status", 1);
		filter.addFilter("companyId", companyId);
		filter.addFilter("isSuperAdmin", isSuperAdmin);
		List<SysBulletin> list = sysBulletinService.getAll(filter);
		// 有权限的栏目
		List<SysBulletinColumn> columnList = sysBulletinColumnService
				.getColumn(companyId, isSuperAdmin);
		ModelAndView mv = new ModelAndView("/platform/system/sysBulletinList.jsp").addObject("sysBulletinList", list)
				.addObject("columnList", columnList);
		return mv;
	} 

	/**
	 * 删除公告表
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除公告表")
	public void del(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			sysBulletinService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除公告表成功!");
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败"
					+ ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	/**
	 * 编辑公告
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description = "编辑公告")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
	
		boolean isAddByFlow = RequestUtil.getBoolean(request, "addByFlow",false);
		boolean isSupportWeixin = PropertyUtil.getBooleanByAlias(SysPropertyConstants.WX_IS_SUPPORT,false);
		String returnUrl = RequestUtil.getPrePage(request);
		Long id = RequestUtil.getLong(request, "id");
		SysBulletin sysBulletin = sysBulletinService.getById(id);
		Long companyId = ContextUtil.getCurrentCompanyId();
		//公告模板
		List<SysBulletinTemplate> templateList  = sysBulletinTemplateService.getAll();
		// 有权限的栏目
		List<SysBulletinColumn> columnList = sysBulletinColumnService
				.getColumn(companyId, ContextUtil.isSuperAdmin());
		
		boolean canSelect=companyId==0 || ContextUtil.isSuperAdmin();
		
		return getAutoView().addObject("sysBulletin", sysBulletin)
				.addObject("returnUrl", returnUrl)
				.addObject("canSelect", canSelect)
				.addObject("columnList", columnList)
				.addObject("columnList", columnList)
				.addObject("isAddByFlow", isAddByFlow)
		        .addObject("templateList", templateList)
		        .addObject("isSupportWeixin", isSupportWeixin);
	}

	/**
	 * 取得公告表明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description = "查看公告表明细")
	public ModelAndView get(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		SysBulletin sysBulletin = sysBulletinService.getById(id);
		
		if(sysBulletin != null){
			long userId = ContextUtil.getCurrentUserId();
			if(!sysReadRecodeService.hasRead(id,userId)){
				sysReadRecodeService.add(new SysReadRecode(id,userId,"Bulletin",sysBulletin.getColumnid()));
			}
		}
		return getAutoView().addObject("sysBulletin", sysBulletin);
	}
	
	/**
	 * 在首页上面点击更多按钮的时候跳转到这里
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("more")
	public ModelAndView more(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String alias = RequestUtil.getString(request, "alias");
		QueryFilter filter = new QueryFilter(request, "sysBulletinItem");
		filter.addFilter("alias", alias);
		filter.addFilter("userId", ContextUtil.getCurrentUserId());
		List<SysBulletin> list = sysBulletinService.getAllByAlias(filter);
		return this.getAutoView().addObject("alias", alias).addObject("sysBulletinList", list);
	}
	
	
	/**
	 * 前端接口
	 */
	
	/**
	 * 前端，获取更多。。
	 * @param request
	 * @param response
	 */
	@RequestMapping("frontList")
	public void frontList(HttpServletRequest request, HttpServletResponse response) {
		JSONObject data = new JSONObject();
		String message;
		try {
			QueryFilter queryFilter = new QueryFilter(request, "sysBulletinItem");
			List<SysBulletin> list = sysBulletinService.getAll(queryFilter);
			data.put("varList", list);
			message = "查询成功";
			addMessage(ResultMessage.Success, message, "200", data, response);
		} catch (Exception e) {
			message = "查询出错";
			addMessage(ResultMessage.Fail, message, "", response);
		}
	}	
	
	/**
	 * 前端，获取前几个
	 * @param request
	 * @param response
	 */
	@RequestMapping("topNews")
	public void topNews(HttpServletRequest request, HttpServletResponse response,int top) {
		JSONObject data = new JSONObject();
		String message;
		try {
			List<SysBulletin> list = sysBulletinService.getTopNews(top);
			data.put("varList", list);
			message = "查询成功";
			addMessage(ResultMessage.Success, message, "200", data, response);
		} catch (Exception e) {
			message = "查询出错";
			addMessage(ResultMessage.Fail, message, "", response);
		}
	}	
	
	/**
	 * 取得新闻动态明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("frontGet")
	@Action(description = "查看新闻动态明细")
	public void frontGet(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JSONObject data = new JSONObject();
		String message;
		try {
			Long id = RequestUtil.getLong(request, "id");
			SysBulletin sysBulletin = sysBulletinService.getById(id);
			//组装前缀start
			String httplujing = SysPropertyConstants.HTTP_URL!=null?SysPropertyConstants.HTTP_URL:(SysPropertyConstants.HTTP_URL =request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort());
			String newsContent = sysBulletin.getContent();
			if(null!=sysBulletin.getContent()){
				newsContent = newsContent.replaceAll("src=\""+request.getContextPath(),"src=\""+httplujing+""+request.getContextPath());
			}
			sysBulletin.setContent(newsContent);
			//组装前缀end
			data.put("sysBulletin", sysBulletin);
			message = "查询成功";
			addMessage(ResultMessage.Success, message, "200", data, response);
		} catch (Exception e) {
			message = "查询出错";
			addMessage(ResultMessage.Fail, message, "", response);
		}
	}
	
}