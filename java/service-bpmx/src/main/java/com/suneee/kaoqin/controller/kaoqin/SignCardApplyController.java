package com.suneee.kaoqin.controller.kaoqin;

import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.kaoqin.model.kaoqin.SignCardApply;
import com.suneee.kaoqin.service.kaoqin.SignCardApplyService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.service.bpm.TaskOpinionService;
import com.suneee.platform.service.bpm.util.SqlUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
/**
 *<pre>
 * 对象功能:签卡申请 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-03 14:37:01
 *</pre>
 */
@Controller
@RequestMapping("/kaoqin/kaoqin/signCardApply/")
public class SignCardApplyController extends BaseController
{
	@Resource
	private SignCardApplyService signCardApplyService;
	@Resource
	private TaskOpinionService taskOpinionService;
	
	/**
	 * 添加或更新签卡申请。
	 * @param request
	 * @param response
	 * @param signCardApply 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新签卡申请")
	public void save(HttpServletRequest request, HttpServletResponse response,SignCardApply signCardApply) throws Exception
	{
		String resultMsg=null;
		Long id = RequestUtil.getLong(request, "id");
		try{
			signCardApplyService.save(signCardApply);
			if(id==0){
				resultMsg=getText("添加成功","签卡申请");
			}else{
				resultMsg=getText("更新成功","签卡申请");
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得签卡申请分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看签卡申请分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<SignCardApply> list = new ArrayList<SignCardApply>();
		QueryFilter filter = new QueryFilter(request,"signCardApplyItem");
		filter.addFilter("AllSql", SqlUtil.getAll("w_sign_card_apply"));
		list = signCardApplyService.getAllApply(filter);
		ModelAndView mv=this.getAutoView().addObject("signCardApplyList",list);
		return mv;
	}
	
	/**
	 * 删除签卡申请
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除签卡申请")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			signCardApplyService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除签卡申请成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑签卡申请
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑签卡申请")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		SignCardApply signCardApply=signCardApplyService.getById(id);
		
		return getAutoView().addObject("signCardApply",signCardApply)
							.addObject("returnUrl",returnUrl);
	}

	/**
	 * 取得签卡申请明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看签卡申请明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		String instanceId = RequestUtil.getString(request, "instanceId");
		List<TaskOpinion> taskOpinionList = taskOpinionService.getByActInstId(instanceId);
		SignCardApply signCardApply = signCardApplyService.getById(id);	
		return getAutoView().addObject("signCardApply", signCardApply).addObject("taskOpinionList", taskOpinionList);
	}
	
}

