package com.suneee.kaoqin.controller.kaoqin;

import java.util.ArrayList;
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

import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.TaskOpinionService;
import com.suneee.platform.service.bpm.util.SqlUtil;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import com.suneee.core.util.StringUtil;

import com.suneee.kaoqin.model.kaoqin.OvertimeApply;
import com.suneee.kaoqin.service.kaoqin.OvertimeApplyService;
import com.suneee.core.web.ResultMessage;
/**
 *<pre>
 * 对象功能:加班申请流程 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-05 10:20:27
 *</pre>
 */
@Controller
@RequestMapping("/kaoqin/kaoqin/overtimeApply/")
public class OvertimeApplyController extends BaseController
{
	@Resource
	private OvertimeApplyService overtimeApplyService;
	@Resource
	private TaskOpinionService taskOpinionService;
	
	/**
	 * 添加或更新加班申请。
	 * @param request
	 * @param response
	 * @param overtimeApply 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新加班申请")
	public void save(HttpServletRequest request, HttpServletResponse response,OvertimeApply overtimeApply) throws Exception
	{
		String resultMsg=null;
		Long id = RequestUtil.getLong(request, "id");
		try{
			overtimeApplyService.save(overtimeApply);
			if(id==0){
				resultMsg=getText("添加成功","加班申请");
			}else{
				resultMsg=getText("更新成功","加班申请");
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得加班申请分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看加班申请分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<OvertimeApply> list = new ArrayList<OvertimeApply>();
		QueryFilter filter = new QueryFilter(request,"overtimeApplyItem");
		filter.addFilter("AllSql", SqlUtil.getAll("w_overtime"));
		list = overtimeApplyService.getAllApply(filter);
		ModelAndView mv=this.getAutoView().addObject("overtimeApplyList",list);
		return mv;
	}
	
	/**
	 * 删除加班申请
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除加班申请")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			overtimeApplyService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除加班申请成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑加班申请
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑加班申请")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		OvertimeApply overtimeApply=overtimeApplyService.getById(id);
		
		return getAutoView().addObject("overtimeApply",overtimeApply)
							.addObject("returnUrl",returnUrl);
	}

	/**
	 * 取得加班申请明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看加班申请明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		String instanceId = RequestUtil.getString(request, "instanceId");
		OvertimeApply overtimeApply = overtimeApplyService.getById(id);	
		List<TaskOpinion> taskOpinionList = taskOpinionService.getByActInstId(instanceId);
		return getAutoView().addObject("overtimeApply", overtimeApply).addObject("taskOpinionList", taskOpinionList);
	}
	
}

