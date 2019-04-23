package com.suneee.kaoqin.controller.kaoqin;

import java.util.ArrayList;
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
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.kaoqin.model.kaoqin.AnnualVacationSetting;
import com.suneee.kaoqin.service.kaoqin.AnnualVacationSettingService;
import com.suneee.platform.annotion.Action;
/**
 *<pre>
 * 对象功能:年假设置 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 11:30:12
 *</pre>
 */
@Controller
@RequestMapping("/kaoqin/kaoqin/annualVacationSetting/")
public class AnnualVacationSettingController extends BaseController
{
	@Resource
	private AnnualVacationSettingService annualVacationSettingService;
	

	/**
	 * 添加或更新年假设置。
	 * @param request
	 * @param response
	 * @param annualVacationSetting 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新年假设置")
	public void save(HttpServletRequest request, HttpServletResponse response,AnnualVacationSetting annualVacationSetting) throws Exception
	{
		String resultMsg=null;
		Long id = RequestUtil.getLong(request, "id");
		try{
			annualVacationSettingService.save(annualVacationSetting);
			if(id==0){
				resultMsg=getText("添加成功","年假设置");
			}else{
				resultMsg=getText("更新成功","年假设置");
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	/**
	 * 添加或更新年假设置。
	 * @param request
	 * @param response
	 * @param annualVacationSetting 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("batchSave")
	@Action(description="添加或更新年假设置")
	public void batchSave(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String resultMsg=null;
		Integer[] yearLimitArray = RequestUtil.getIntAry(request, "yearLimit");		
		Integer[] days = RequestUtil.getIntAry(request, "days");	
		String edit = RequestUtil.getString(request, "edit");
		try{
			if(edit != "" && edit != null){
				annualVacationSettingService.emptyTable();
				resultMsg=getText("更新成功","年假设置");
			}else{
				resultMsg=getText("添加成功","年假设置");
			}
			for (int i = 0; i < yearLimitArray.length; i++) {
				AnnualVacationSetting annualVacationSetting = new AnnualVacationSetting();
				annualVacationSetting.setYearLimit(yearLimitArray[i].shortValue());
				annualVacationSetting.setDays(days[i].shortValue());
				annualVacationSettingService.save(annualVacationSetting);
			}
			writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得年假设置分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看年假设置分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<AnnualVacationSetting> list=annualVacationSettingService.getAll(new QueryFilter(request,"annualVacationSettingItem"));
		ModelAndView mv=this.getAutoView().addObject("annualVacationSettingList",list);
		return mv;
	}
	
	/**
	 * 删除年假设置
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除年假设置")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			annualVacationSettingService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除年假设置成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑年假设置
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑年假设置")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		AnnualVacationSetting annualVacationSetting=annualVacationSettingService.getById(id);
		
		return getAutoView().addObject("annualVacationSetting",annualVacationSetting)
							.addObject("returnUrl",returnUrl);
	}
	
	/**
	 * 	编辑年假设置
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("batchEdit")
	@Action(description="编辑年假设置")
	public ModelAndView batchEdit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		ModelAndView autoView = getAutoView();
		List<AnnualVacationSetting> annualVacationSettingList =  new ArrayList<AnnualVacationSetting>();
		if(id != 0L){
			annualVacationSettingList=annualVacationSettingService.getAll();
			autoView.addObject("edit", "edit");
			
		}
		
		return autoView	.addObject("returnUrl",returnUrl)
				.addObject("annualVacationSettingList",annualVacationSettingList);
	}

	/**
	 * 取得年假设置明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看年假设置明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		AnnualVacationSetting annualVacationSetting = annualVacationSettingService.getById(id);	
		return getAutoView().addObject("annualVacationSetting", annualVacationSetting);
	}
	
}

