package com.suneee.platform.controller.bpm;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.platform.annotion.Action;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.bpm.BpmSubtableRights;
import com.suneee.platform.service.bpm.BpmSubtableRightsService;
/**
 * 对象功能:子表权限 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:wwz
 * 创建时间:2013-01-16 10:13:31
 */
@Controller
@RequestMapping("/platform/bpm/BpmSubtableRights/")
public class BpmSubtableRightsController extends BaseController
{
	@Resource
	private BpmSubtableRightsService bpmSubtableRightsService;
	
	/**
	 * 添加或更新子表权限。
	 * @param request
	 * @param response
	 * @param BpmSubtableRights 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新子表权限")
	public void save(HttpServletRequest request, HttpServletResponse response, BpmSubtableRights bpmSubtableRights) throws Exception
	{
		String resultMsg=null;		
//		BpmSubtableRights bpmSubtableRights1=getFormObject(request);
		try{
			String parentActDefId = RequestUtil.getString(request, "parentActDefId", "");
			BpmSubtableRights bpmSubtableRights2 = null;
			Long tableId=bpmSubtableRights.getTableid();
			String nodeId=bpmSubtableRights.getNodeid();
			String actDefId=bpmSubtableRights.getActdefid();
			if(StringUtil.isNotEmpty(parentActDefId)){
				bpmSubtableRights2 = bpmSubtableRightsService.getByDefIdAndNodeId(actDefId, nodeId, tableId,parentActDefId);
			}
			if(bpmSubtableRights2==null){
				bpmSubtableRights2 = bpmSubtableRightsService.getByDefIdAndNodeId(actDefId, nodeId, tableId, "");
			}
			
			
			if(bpmSubtableRights2==null){
				bpmSubtableRights.setId(UniqueIdUtil.genId());
				bpmSubtableRightsService.add(bpmSubtableRights);
				resultMsg="添加子表权限成功";
			}else{
				bpmSubtableRights.setId(bpmSubtableRights2.getId());
				bpmSubtableRightsService.update(bpmSubtableRights);
				resultMsg="更新子表权限成功";
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	

	
	/**
	 * 取得子表权限分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看子表权限分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<BpmSubtableRights> list=bpmSubtableRightsService.getAll(new QueryFilter(request,"BpmSubtableRightsItem"));
		ModelAndView mv=this.getAutoView().addObject("BpmSubtableRightsList",list);
		
		return mv;
	}
	
	/**
	 * 删除子表权限
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除子表权限")
	public void del(HttpServletRequest request, HttpServletResponse response, BpmSubtableRights bpmSubtableRights) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			
			bpmSubtableRightsService.delById(bpmSubtableRights.getId());
			
			message=new ResultMessage(ResultMessage.Success, "删除子表权限成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
//		addMessage(message, request);
//		response.sendRedirect(preUrl);
//		writeResultMessage(response.getWriter(),message,ResultMessage.Success);
		response.getWriter().append(message.toString());
	}
	
	/**
	 * 	编辑子表权限
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑子表权限")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		String returnUrl=RequestUtil.getPrePage(request);
		BpmSubtableRights BpmSubtableRights=bpmSubtableRightsService.getById(id);
		
		return getAutoView().addObject("BpmSubtableRights",BpmSubtableRights).addObject("returnUrl", returnUrl);
	}

	/**
	 * 取得子表权限明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看子表权限明细")
	public void get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String actdefid = RequestUtil.getString(request,"actdefid");
		String nodeid = RequestUtil.getString(request,"nodeid");
		long tableid=RequestUtil.getLong(request,"tableid");
		String parentActDefId = RequestUtil.getString(request, "parentActDefId", "");
		BpmSubtableRights bpmSubtableRights = null;
		if(StringUtil.isNotEmpty(parentActDefId)){
			bpmSubtableRights = bpmSubtableRightsService.getByDefIdAndNodeId(actdefid, nodeid, tableid,parentActDefId);
		}
		if(bpmSubtableRights==null){
			bpmSubtableRights = bpmSubtableRightsService.getByDefIdAndNodeId(actdefid, nodeid, tableid, "");
		}
		StringBuffer sb = new StringBuffer("{success:true");
		if(bpmSubtableRights!=null){
			sb.append(",\"id\":\"").append(bpmSubtableRights.getId()).append("\",")
			.append("\"permissiontype\":").append(bpmSubtableRights.getPermissiontype()).append(",")
			.append("\"permissionseting\":\"")
			.append(bpmSubtableRights.getPermissionseting()!=null?
					bpmSubtableRights.getPermissionseting().replaceAll("\n", "<br>").replaceAll("[\"]", "<032>"):"")
			.append("\"");
		}
		sb.append("}");
		response.getWriter().print(sb.toString());
	}
}
