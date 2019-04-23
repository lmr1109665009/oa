package com.suneee.platform.controller.system;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysLogSwitch;
import com.suneee.platform.service.system.SysLogSwitchService;

import freemarker.ext.beans.BeansWrapper;
/**
 *<pre>
 * 对象功能:日志开关 控制器类
 * 开发公司:广州宏天
 * 开发人员:Raise
 * 创建时间:2013-06-24 11:12:27
 *</pre>
 */
@Controller
@RequestMapping("/platform/system/sysLogSwitch/")
@Action(ownermodel=SysAuditModelType.SYSTEM_SETTING)
public class SysLogSwitchController extends BaseController
{
	@Resource
	private SysLogSwitchService sysLogSwitchService;
	
	
	/**
	 * 添加或更新日志开关。
	 * @param request
	 * @param response
	 * @param sysLogSwitch 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新日志开关",
			execOrder=ActionExecOrder.AFTER,
			detail="修改系统日志开关，模块【${sysLogSwitch.model}】的状态为：<#if SysLogSwitch.STATUS_OPEN==sysLogSwitch.status>开启<#else>关闭</#if>"
	)
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String resultMsg=null;		
		SysLogSwitch sysLogSwitch=getFormObject(request);
		//添加系统日志信息 -B
		try {
			SysAuditThreadLocalHolder.putParamerter("sysLogSwitch", sysLogSwitch);
			SysAuditThreadLocalHolder.putParamerter(SysLogSwitch.class.getSimpleName(), BeansWrapper.getDefaultInstance().getStaticModels().get(SysLogSwitch.class.getName()));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		//添加系统日志信息 -E
		try{
			if(sysLogSwitch.getId()==null||sysLogSwitch.getId()==0){
				sysLogSwitch.setId(UniqueIdUtil.genId());
//				sysLogSwitch.setCreatetime(new Date());
				sysLogSwitchService.add(sysLogSwitch);
				resultMsg="添加日志开关成功";
			}else{
				SysLogSwitch iSysLogSwitch = sysLogSwitchService.getById(sysLogSwitch.getId());
				iSysLogSwitch.setStatus(sysLogSwitch.getStatus());
				iSysLogSwitch.setMemo(sysLogSwitch.getMemo());
			    sysLogSwitchService.update(iSysLogSwitch);
				resultMsg="更新日志开关成功";
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	/**
	 * 取得 SysLogSwitch 实体 
	 * @param request
	 * @return
	 * @throws Exception
	 */
    protected SysLogSwitch getFormObject(HttpServletRequest request) throws Exception {
    
    	JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher((new String[] { "yyyy-MM-dd" })));
    
		String json= RequestUtil.getString(request, "json");
		JSONObject obj = JSONObject.fromObject(json);
		
		SysLogSwitch sysLogSwitch = (SysLogSwitch)JSONObject.toBean(obj, SysLogSwitch.class);
		
		return sysLogSwitch;
    }
	
	/**
	 * 取得日志开关分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看日志开关分页列表",detail="查看日志开关分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<SysLogSwitch> list=sysLogSwitchService.getAll(new QueryFilter(request,"sysLogSwitchItem"));
		ModelAndView mv=this.getAutoView().addObject("sysLogSwitchList",list);
		
		return mv;
	}
	
	
	/**
	 * 系统日志开关管理
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("management")
	@Action(description="系统日志开关管理",ownermodel=SysAuditModelType.SYSTEM_SETTING,detail="系统日志开关管理")
	public ModelAndView management(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<SysLogSwitch> list=sysLogSwitchService.getAll();
		ModelAndView mv=this.getAutoView().addObject("sysLogSwitchList",list);
		return mv;
	}
	
	
	/**
	 * 删除日志开关
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除日志开关",
			execOrder=ActionExecOrder.BEFORE,
			detail="删除日志开关" +
					"<#assign entity=sysLogSwitchService.getById(Long.valueOf(id))/>" +
					"【${entity.model}】"
					)
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			sysLogSwitchService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除日志开关成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑日志开关
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑日志开关",
			detail="编辑日志开关" +
					"<#assign entity=sysLogSwitchService.getById(Long.valueOf(id))/>" +
					"【${entity.model}】"
					)
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		String returnUrl=RequestUtil.getPrePage(request);
		SysLogSwitch sysLogSwitch=sysLogSwitchService.getById(id);
		
		return getAutoView().addObject("sysLogSwitch",sysLogSwitch).addObject("returnUrl", returnUrl);
	}

	/**
	 * 取得日志开关明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看日志开关明细",detail="查看日志开关明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		long id=RequestUtil.getLong(request,"id");
		SysLogSwitch sysLogSwitch = sysLogSwitchService.getById(id);	
		return getAutoView().addObject("sysLogSwitch", sysLogSwitch);
	}
	
}

