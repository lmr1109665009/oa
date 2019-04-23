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
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.service.bpm.TaskOpinionService;
import com.suneee.platform.service.bpm.util.SqlUtil;

import org.springframework.web.servlet.ModelAndView;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;

import com.suneee.kaoqin.model.kaoqin.GoOutApply;
import com.suneee.kaoqin.service.kaoqin.GoOutApplyService;
import com.suneee.core.web.ResultMessage;
/**
 *<pre>
 * 对象功能:外出申请 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 16:30:42
 *</pre>
 */
@Controller
@RequestMapping("/kaoqin/kaoqin/goOutApply/")
public class GoOutApplyController extends BaseController
{
	@Resource
	private GoOutApplyService goOutApplyService;
	@Resource
	private TaskOpinionService taskOpinionService;
	
	
	/**
	 * 添加或更新外出申请。
	 * @param request
	 * @param response
	 * @param goOutApply 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新外出申请")
	public void save(HttpServletRequest request, HttpServletResponse response,GoOutApply goOutApply) throws Exception
	{
		String resultMsg=null;
		Long id = RequestUtil.getLong(request, "id");
		try{
			goOutApplyService.save(goOutApply);
			if(id==0){
				resultMsg=getText("添加成功","外出申请");
			}else{
				resultMsg=getText("更新成功","外出申请");
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得外出申请分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看外出申请分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<GoOutApply> list = new ArrayList<GoOutApply>();
		QueryFilter filter = new QueryFilter(request,"goOutApplyItem");
		filter.addFilter("AllSql", SqlUtil.getAll("w_gooutapply"));
		list = goOutApplyService.getAllApply(filter);
		ModelAndView mv=this.getAutoView().addObject("goOutApplyList",list);
		return mv;
	}
	
	/**
	 * 删除外出申请
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除外出申请")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			goOutApplyService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除外出申请成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑外出申请
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑外出申请")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		GoOutApply goOutApply=goOutApplyService.getById(id);
		
		return getAutoView().addObject("goOutApply",goOutApply)
							.addObject("returnUrl",returnUrl);
	}

	/**
	 * 取得外出申请明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看外出申请明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		GoOutApply goOutApply = goOutApplyService.getById(id);	
		String actInstId = RequestUtil.getString(request,"instanceId");
		List<TaskOpinion> taskOpinionList = taskOpinionService.getByActInstId(actInstId);
		return getAutoView().addObject("goOutApply", goOutApply).addObject("taskOpinionList", taskOpinionList);
	}
	
}

