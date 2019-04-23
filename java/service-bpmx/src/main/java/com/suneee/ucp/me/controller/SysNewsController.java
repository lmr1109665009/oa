package com.suneee.ucp.me.controller;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.SysPropertyConstants;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysPropertyService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.me.model.SysNews;
import com.suneee.ucp.me.service.SysNewsServcie;

/**
 *
 * @ClassName: SysNewsController
 * @Description: 新闻动态controller
 * @author 游刃
 * @date 2017年4月25日 下午2:11:55
 *
 */

@Controller
@RequestMapping("/me/sysNews/")
public class SysNewsController extends UcpBaseController{

	@Resource
	private SysNewsServcie sysNewsServcie;

	@Resource
	private SysPropertyService sysPropertyService;

	//图片文件访问URL地址
	@Value("#{configProperties['file.picture.url']}")
	private String staticUrl;
	//文件路径，绝对路径=basePath+contextPath
	@Value("#{configProperties['file.picture.context']}")
	private String contextPath;

	private String getStaticUrl() {
		if (staticUrl==null){
			return "";
		}
		return staticUrl;
	}

	@RequestMapping("list")
	@Action(description = "查看新闻动态列表", execOrder = ActionExecOrder.AFTER, detail = "查看新闻动态列表", exectype = "管理日志")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {

		boolean isSupportWeixin = PropertyUtil.getBooleanByAlias(SysPropertyConstants.WX_IS_SUPPORT, false);
		QueryFilter queryFilter = new QueryFilter(request, "sysNewsItem");
		Boolean isSuperAdmin = ContextUtil.isSuperAdmin();
		queryFilter.addFilter("isSuperAdmin", isSuperAdmin);
		List<SysNews> list = sysNewsServcie.getAll(queryFilter);
		ModelAndView mv = this.getAutoView().addObject("sysNewsList", list).addObject("isSupportWeixin",
				isSupportWeixin).addObject("isSuperAdmin", isSuperAdmin);

		return mv;
	}
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
			QueryFilter queryFilter = new QueryFilter(request, "sysNewsItem");
			List<SysNews> list = sysNewsServcie.getAll(queryFilter);
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
			List<SysNews> list = sysNewsServcie.getTopNews(top);
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
			//SysNews sysNews = sysNewsServcie.getById(id);
			//解决定子链首页新闻详情加载不出最新内容和图片问题
			SysNews sysNews = sysNewsServcie.getNewsById(id);
			SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
			String serverUrl= sysPropertyService.getByAlias("serverUrl");
			if (serverUrl!=null){
				serverUrl=serverUrl.replaceAll(request.getContextPath(),"");
			}
			//组装前缀start
			String newsContent = sysNews.getContent();
			if(null!=sysNews.getContent()){
				newsContent = newsContent.replaceAll("src=\""+contextPath,"src=\""+serverUrl+contextPath);
			}
			sysNews.setContent(newsContent);
			//组装前缀end
			data.put("sysNews", sysNews);
			message = "查询成功";
			addMessage(ResultMessage.Success, message, "200", data, response);
		} catch (Exception e) {
			message = "查询出错";
			addMessage(ResultMessage.Fail, message, "", response);
		}
	}

	/**
	 * 删除新闻动态
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除新闻动态")
	public void del(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			sysNewsServcie.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除新闻动态成功!");
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败"
					+ ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	/**
	 * 编辑新闻动态
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description = "编辑新闻动态")
	public ModelAndView edit(HttpServletRequest request) throws Exception {

		boolean isAddByFlow = RequestUtil.getBoolean(request, "addByFlow",false);
		boolean isSupportWeixin = PropertyUtil.getBooleanByAlias(SysPropertyConstants.WX_IS_SUPPORT,false);
		String returnUrl = RequestUtil.getPrePage(request);
		Long id = RequestUtil.getLong(request, "id");
		SysNews sysNews = sysNewsServcie.getById(id);

		return getAutoView().addObject("sysNews", sysNews)
				.addObject("returnUrl", returnUrl)
				.addObject("isAddByFlow", isAddByFlow)
				.addObject("isSupportWeixin", isSupportWeixin).addObject("staticUrl",getStaticUrl());
	}

	/**
	 * 取得新闻动态明细
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description = "查看新闻动态明细")
	public ModelAndView get(HttpServletRequest request,
							HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		SysNews sysNews = sysNewsServcie.getById(id);

		return getAutoView().addObject("sysNews", sysNews);
	}



}