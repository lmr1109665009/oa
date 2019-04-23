package com.suneee.platform.controller.system;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.datasource.JdbcTemplateUtil;
import com.suneee.core.util.*;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.datasource.JdbcTemplateUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysPopupRemind;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysPopupRemindService;
import com.suneee.platform.service.system.SysUserService;

/**
 * <pre>
 * 对象功能:sys_popup_remind 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-03-18 11:36:25
 * </pre>
 */
@Controller
@RequestMapping("/platform/system/sysPopupRemind/")
public class SysPopupRemindController extends BaseController {
	@Resource
	private SysPopupRemindService sysPopupRemindService;

	/**
	 * 添加或更新sys_popup_remind。
	 * 
	 * @param request
	 * @param response
	 * @param sysPopupRemind
	 *            添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "添加或更新sys_popup_remind")
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String json = FileUtil.inputStream2String(request.getInputStream());
		SysPopupRemind sysPopupRemind = JSONObjectUtil.toBean(json, SysPopupRemind.class);
		String resultMsg = null;
		try {
			if (sysPopupRemind.getId() == null || sysPopupRemind.getId() == 0) {
				sysPopupRemind.setId(UniqueIdUtil.genId());
				sysPopupRemindService.add(sysPopupRemind);
				resultMsg = getText("添加成功", "sys_popup_remind");
			} else {
				sysPopupRemindService.update(sysPopupRemind);
				resultMsg = getText("更新成功", "sys_popup_remind");
			}
			writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
		} catch (Exception e) {
			writeResultMessage(response.getWriter(), resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}
	}

	@RequestMapping("setEnabled")
	@ResponseBody
	public JSONObject setEnabled(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject result = new JSONObject();

		String[] ids = RequestUtil.getStringAryByStr(request, "ids");
		Short enabled = RequestUtil.getShort(request, "enabled", null);
		if (enabled == null || ids.length == 0) {
			result.put("status", "0");
			result.put("msg", "ids,enabled参数错误");
			return result;
		}
		
		try {
			sysPopupRemindService.updateEnabled(ids, enabled);
			result.put("status", "1");
			result.put("msg", "操作成功");
		} catch (Exception e) {
			result.put("status", "0");
			result.put("msg", "更新错误:" + ExceptionUtil.getExceptionMessage(e));
		}

		return result;

	}

	/**
	 * 取得sys_popup_remind分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看sys_popup_remind分页列表")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<SysPopupRemind> list = sysPopupRemindService.getAll(new QueryFilter(request, "sysPopupRemindItem"));
		ModelAndView mv = this.getAutoView().addObject("sysPopupRemindList", list);
		mv.addObject("cls", SysPopupRemind.class.getName());
		return mv;
	}

	/**
	 * 删除sys_popup_remind
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除sys_popup_remind")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			sysPopupRemindService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除sys_popup_remind成功!");
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	@RequestMapping("getObject")
	@Action(description = "按各种参数查询bpmNodeSql")
	@ResponseBody
	public SysPopupRemind getObject(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id", null);
		if (id != null) {
			return sysPopupRemindService.getById(id);
		}
		return null;
	}

	@RequestMapping("show")
	@Action(description = "展示某个用户的弹框，默认当前用户")
	public ModelAndView show(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = this.getAutoView();
		String userId = RequestUtil.getString(request, "userId", "");
		SysUser user = (SysUser) ContextUtil.getCurrentUser();
		String[] sprsIds=RequestUtil.getStringAryByStr(request, "sprsIds");
		String isTest= RequestUtil.getString(request,"isTest","");
		if (StringUtil.isNotEmpty(userId)) {
			user = AppUtil.getBean(SysUserService.class).getById(Long.parseLong(userId));
		}
		
		List<SysPopupRemind> reminds = new ArrayList<SysPopupRemind>();
		if(isTest.equals("1") && sprsIds!=null){
			reminds = sysPopupRemindService.getByIds(sprsIds);
		}else if(!isTest.equals("1")){// 获取用户能生效的列表
			reminds = sysPopupRemindService.getByUser(user);
		}
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		//sysPopupRemindShow.jsp页面提示信息
		String massage = "";
		try
		{
			for (SysPopupRemind spr : reminds) {
				String sql = spr.getSql().replace("{curUserId}", ContextUtil.getCurrentUserId().toString());
				int count = JdbcTemplateUtil.getNewJdbcTemplate(spr.getDsalias()).queryForObject(sql,Integer.class);
				if (count <= 0) {// 没有数据
					continue;
				}
				JSONObject json = new JSONObject();
				String countStr = ""+count;
				String str =  spr.getDesc().replace("{count}",countStr);
				json.put("msg", str);
				json.put("url", spr.getUrl());
				json.put("subject", spr.getSubject());
				json.put("popupType", spr.getPopupType());
				JSONObject jsonObject = JSONObject.fromObject(spr.getReserve());
				if("dialog".equals(spr.getPopupType())){
					json.put("height",jsonObject.get("dialogHeight") );
					json.put("width",jsonObject.get("dialogWidth") );
				}
				jsonList.add(json);
			}
		} catch (Exception e){
			e.printStackTrace();
			massage = "检查功能提醒中sql是否按要求编写或联系管理员";
		}finally{
			mv.addObject("jsonList", jsonList).addObject("massage", massage);
		}
		return mv;
	}

	@RequestMapping("showSize")
	@Action(description = "获取userId的弹框数据的大小，用来判断是否需要弹框")
	@ResponseBody
	public JSONObject showSize(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject jsonObject = new JSONObject();
		String userId = RequestUtil.getString(request, "userId", "");
		String isTest= RequestUtil.getString(request,"isTest","");
		String[] sprsIds =  RequestUtil.getStringAryByStr(request, "sprsIds");
		SysUser user = (SysUser) ContextUtil.getCurrentUser();
		if (StringUtil.isNotEmpty(userId)) {
			user = AppUtil.getBean(SysUserService.class).getById(Long.parseLong(userId));
		}
		List<SysPopupRemind> reminds = new ArrayList<SysPopupRemind>();
		if(isTest.equals("1") && sprsIds!=null){//获取所有启动状态的提示列表
			reminds = sysPopupRemindService.getByIds(sprsIds);
		}else if(!isTest.equals("1")){// 获取用户能生效的列表
			reminds = sysPopupRemindService.getByUser(user);
		}
		int size = 0;
		try
		{
			for (SysPopupRemind spr : reminds) {
				String sql = spr.getSql().replace("{curUserId}", ContextUtil.getCurrentUserId().toString());
				int count = JdbcTemplateUtil.getNewJdbcTemplate(spr.getDsalias()).queryForObject(sql,Integer.class);
				if (count <= 0) {// 没有数据
					continue;
				}
				size++;
			}
		} catch (Exception e){
			e.printStackTrace();
		}finally{
			jsonObject.put("size", size);
		}
		return jsonObject;
	}

}
