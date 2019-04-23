package com.suneee.platform.controller.system;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.platform.annotion.Action;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.system.SysRole;
import com.suneee.platform.model.system.UserRole;
import com.suneee.platform.service.system.SysRoleService;
import com.suneee.platform.service.system.UserRoleService;

/**
 * 对象功能:用户角色映射表 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2011-12-16 15:47:55
 */
@Controller
@RequestMapping("/platform/system/userRole/")
public class UserRoleController extends BaseController
{
	@Resource
	private UserRoleService userRoleService;
	@Resource
	SysRoleService sysRoleService;
	
	
	/**
	 * 删除用户角色映射表
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除用户角色映射表")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		ResultMessage message=null;
		String preUrl= RequestUtil.getPrePage(request);
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "userRoleId");
			userRoleService.delByUserRoleId(lAryId);
			message=new ResultMessage(ResultMessage.Success,"删除用户成功");
					
		}catch (Exception e) {
			message=new ResultMessage(ResultMessage.Fail,"删除用户失败："+e.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	@RequestMapping("edit")
	@Action(description="编辑用户角色映射表")
	public ModelAndView edit(HttpServletRequest request) throws Exception{
		ModelAndView mv=this.getAutoView();
		Long roleId=RequestUtil.getLong(request, "roleId");
		String roleName = "未知角色";
		if(BeanUtils.isNotEmpty(roleId)){
			SysRole sysRole = sysRoleService.getById(roleId);
			if(BeanUtils.isNotEmpty(sysRole)){
				roleName = sysRole.getRoleName();
			}
		}
		QueryFilter queryFilter=new QueryFilter(request,"userRoleItem",true);
		queryFilter.addFilter("roleId", roleId);
		List<UserRole> userRoleList=userRoleService.getAll(queryFilter);
		mv.addObject("userRoleList",userRoleList)
		.addObject("roleId", roleId).addObject("roleName",roleName);
		return mv;
	}

	
	/**
	 * 添加或更新用户岗位表。
	 * @param request
	 * @param response
	 * @param userPosition 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("add")
	@Action(description="添加或更新用户角色")
	public void add(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long roleId=RequestUtil.getLong(request, "roleId",0);
		Long[] userIds =RequestUtil.getLongAryByStr(request, "userIds");
		userRoleService.add(roleId, userIds);
		
		String returnUrl=RequestUtil.getString(request, "returnUrl", RequestUtil.getPrePage(request));
		response.sendRedirect(RequestUtil.getPrePage(request));
	}
	
	@RequestMapping("getUserListByRoleId")
	@ResponseBody
	@Action(description="根据角色ID取得用户List")
	public List<UserRole> getUserListByRoleId(HttpServletRequest request) throws Exception
	{
		String roleId=RequestUtil.getString(request,"roleId");
		return userRoleService.getUserByRoleIds(roleId);
	}
}
