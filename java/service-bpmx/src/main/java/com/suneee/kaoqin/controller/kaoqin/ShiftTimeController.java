package com.suneee.kaoqin.controller.kaoqin;

import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.kaoqin.model.kaoqin.ShiftTime;
import com.suneee.kaoqin.service.kaoqin.ShiftTimeService;
import com.suneee.platform.annotion.Action;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 *<pre>
 * 对象功能:班次时间段 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:09:27
 *</pre>
 */
@Controller
@RequestMapping("/kaoqin/kaoqin/shiftTime/")
public class ShiftTimeController extends BaseController
{
	@Resource
	private ShiftTimeService shiftTimeService;
	
	
	/**
	 * 添加或更新班次时间段。
	 * @param request
	 * @param response
	 * @param shiftTime 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新班次时间段")
	public void save(HttpServletRequest request, HttpServletResponse response,ShiftTime shiftTime) throws Exception
	{
		String resultMsg=null;
		Long id = RequestUtil.getLong(request, "id");
		try{
			shiftTimeService.save(shiftTime);
			if(id==0){
				resultMsg=getText("添加成功","班次时间段");
			}else{
				resultMsg=getText("更新成功","班次时间段");
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得班次时间段分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看班次时间段分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<ShiftTime> list=shiftTimeService.getAll(new QueryFilter(request,"shiftTimeItem"));
		ModelAndView mv=this.getAutoView().addObject("shiftTimeList",list);
		return mv;
	}
	
	/**
	 * 删除班次时间段
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除班次时间段")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			shiftTimeService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除班次时间段成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑班次时间段
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑班次时间段")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		ShiftTime shiftTime=shiftTimeService.getById(id);
		
		return getAutoView().addObject("shiftTime",shiftTime)
							.addObject("returnUrl",returnUrl);
	}

	/**
	 * 取得班次时间段明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看班次时间段明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		ShiftTime shiftTime = shiftTimeService.getById(id);	
		return getAutoView().addObject("shiftTime", shiftTime);
	}
	
}

