package com.suneee.kaoqin.controller.kaoqin;

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

import com.suneee.kaoqin.model.kaoqin.ShiftDaySetting;
import com.suneee.kaoqin.service.kaoqin.ShiftDaySettingService;
import com.suneee.core.web.ResultMessage;
/**
 *<pre>
 * 对象功能:单日排班设置 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:08:00
 *</pre>
 */
@Controller
@RequestMapping("/kaoqin/kaoqin/shiftDaySetting/")
public class ShiftDaySettingController extends BaseController
{
	@Resource
	private ShiftDaySettingService shiftDaySettingService;
	
	
	/**
	 * 添加或更新单日排班设置。
	 * @param request
	 * @param response
	 * @param shiftDaySetting 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新单日排班设置")
	public void save(HttpServletRequest request, HttpServletResponse response,ShiftDaySetting shiftDaySetting) throws Exception
	{
		String resultMsg=null;
		Long id = RequestUtil.getLong(request, "id");
		try{
			shiftDaySettingService.save(shiftDaySetting);
			if(id==0){
				resultMsg=getText("添加成功","单日排班设置");
			}else{
				resultMsg=getText("更新成功","单日排班设置");
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得单日排班设置分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看单日排班设置分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<ShiftDaySetting> list=shiftDaySettingService.getAll(new QueryFilter(request,"shiftDaySettingItem"));
		ModelAndView mv=this.getAutoView().addObject("shiftDaySettingList",list);
		return mv;
	}
	
	/**
	 * 删除单日排班设置
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除单日排班设置")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			shiftDaySettingService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除单日排班设置成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑单日排班设置
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑单日排班设置")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		ShiftDaySetting shiftDaySetting=shiftDaySettingService.getById(id);
		
		return getAutoView().addObject("shiftDaySetting",shiftDaySetting)
							.addObject("returnUrl",returnUrl);
	}

	/**
	 * 取得单日排班设置明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看单日排班设置明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		ShiftDaySetting shiftDaySetting = shiftDaySettingService.getById(id);	
		return getAutoView().addObject("shiftDaySetting", shiftDaySetting);
	}
	
}

