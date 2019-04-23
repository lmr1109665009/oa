package com.suneee.platform.controller.system;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.model.CurrentUser;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.model.CurrentUser;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysKnowledgePer;
import com.suneee.platform.model.system.UserPosition;
import com.suneee.platform.service.system.CurrentUserService;
import com.suneee.platform.service.system.SysKnowledgePerService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.platform.service.system.UserPositionService;

import net.sf.json.JSONArray;
/**
 *<pre>
 * 对象功能:知识库权限 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:dyg
 * 创建时间:2015-07-28 10:23:07
 *</pre>
 */
@Controller
@RequestMapping("/platform/system/sysKnowledgePer/")
public class SysKnowledgePerController extends BaseController
{
	@Resource
	private SysKnowledgePerService sysKnowledgePerService;
	@Resource
	private CurrentUserService currentUserService;
	@Resource
	private SysUserService sysUserService;
	@Resource
	private UserPositionService userPositionService;
	
	/**
	 * 添加或更新权限。
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新权限表")
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String sysKnowObj = RequestUtil.getString(request, "sysKnowObj");
		Long id = RequestUtil.getLong(request, "id");
		JSONArray knowObjArray = JSONArray.fromObject(sysKnowObj);
		String resultMsg=null;	
		try{
			if(id == 0){
				for (Object obj : knowObjArray) {
					SysKnowledgePer sysKnowledgePer = JSONObjectUtil.toBean(obj.toString(), SysKnowledgePer.class);
					Long newId = UniqueIdUtil.genId();
					sysKnowledgePer.setId(newId);
					sysKnowledgePerService.add(sysKnowledgePer);
				}
				resultMsg=getText("添加","权限");
			}else{
				for (Object obj : knowObjArray) {
					SysKnowledgePer sysKnowledgePer = JSONObjectUtil.toBean(obj.toString(), SysKnowledgePer.class);
					sysKnowledgePer.setId(id);
					sysKnowledgePerService.update(sysKnowledgePer);
				}
				resultMsg=getText("更新","权限");
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+",保存失败",ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得权限分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看权限分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<SysKnowledgePer>  list=sysKnowledgePerService.getAllList(new QueryFilter(request,"sysKnowledgePerItem"));
		ModelAndView mv=this.getAutoView();
		mv.addObject("sysKnowledgePerList",list);
		return mv;
	}
	
	
	/**
	 * 删除权限
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除权限表")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			sysKnowledgePerService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑权限
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑权限")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		SysKnowledgePer sysKnowledgePer=sysKnowledgePerService.getById(id);
		return getAutoView().addObject("sysKnowledgePer",sysKnowledgePer);
	}

	/**
	 * 取得权限明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看权限明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		SysKnowledgePer sysKnowledgePer = sysKnowledgePerService.getPerById(id);	
		return getAutoView().addObject("sysKnowledgePer", sysKnowledgePer);
	}
	/**
	 * 查询用户权限
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("searchUserPer")
	@ResponseBody
	@Action(description="查询用户权限")
	public List<SysKnowledgePer> searchUserPer(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long userId = RequestUtil.getLong(request, "userId", 0L);
		List<UserPosition> userPositionList =  userPositionService.getByUserId(userId);
		List<SysKnowledgePer> sysKnowList = new ArrayList<SysKnowledgePer>();
		if(userPositionList.size() == 0) {//没有分配岗位的情况，只有user关系
			sysKnowList = sysKnowledgePerService.getByOnlyUser(userId);
		}else{//分配了组织岗位，则手动拼凑一个CurrentUser
			CurrentUser currentUser = new CurrentUser();
			currentUser.setAccount(userPositionList.get(0).getAccount());
			currentUser.setName(userPositionList.get(0).getUserName());
			currentUser.setOrgId(userPositionList.get(0).getOrgId());
			currentUser.setPosId(userPositionList.get(0).getPosId());
			currentUser.setUserId(userPositionList.get(0).getUserId());
			sysKnowList = sysKnowledgePerService.getByUserRelation(currentUser);
		}
		return sysKnowList; 
	}
}

