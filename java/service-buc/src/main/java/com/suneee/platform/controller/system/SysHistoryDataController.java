package com.suneee.platform.controller.system;

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
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysHistoryData;
import com.suneee.platform.service.system.SysHistoryDataService;
/**
 *<pre>
 * 对象功能:历史数据 控制器类
 * 开发公司:宏天软件
 * 开发人员:ray
 * 创建时间:2015-06-26 22:47:29
 *</pre>
 */
@Controller
@RequestMapping("/platform/system/sysHistoryData/")
public class SysHistoryDataController extends BaseController
{
	@Resource
	private SysHistoryDataService sysHistoryDataService;
	
	
	
	
	
	/**
	 * 取得历史数据分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看历史数据分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		Long objId= RequestUtil.getLong(request, "objId");
		QueryFilter filter = new QueryFilter(request);
		filter.addFilter("objId", objId);
		List<SysHistoryData> list =sysHistoryDataService.getAll(filter);
		
	//	List<SysHistoryData> list=sysHistoryDataService.getByObjId(objId);
		ModelAndView mv=this.getAutoView().addObject("sysHistoryDataList",list);
		return mv;
	}
	
	/**
	 * 删除历史数据
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除历史数据")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			sysHistoryDataService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除历史数据成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑历史数据
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑历史数据")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		SysHistoryData sysHistoryData=sysHistoryDataService.getById(id);
		
		return getAutoView().addObject("sysHistoryData",sysHistoryData)
							.addObject("returnUrl",returnUrl);
	}

	/**
	 * 取得历史数据明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看历史数据明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		SysHistoryData sysHistoryData = sysHistoryDataService.getById(id);	
		return getAutoView().addObject("sysHistoryData", sysHistoryData);
	}
	
	@RequestMapping("getContent")
	@Action(description="查看历史数据明细")
	public void getContent(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		SysHistoryData sysHistoryData = sysHistoryDataService.getById(id);	
		response.getWriter().print(sysHistoryData.getContent());
	}
	
}

