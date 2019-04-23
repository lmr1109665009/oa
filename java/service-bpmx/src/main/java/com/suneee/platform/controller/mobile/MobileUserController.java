package com.suneee.platform.controller.mobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.suneee.platform.annotion.Action;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysRole;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysRoleService;
import com.suneee.platform.service.system.SysUserService;

@Controller
@RequestMapping("/platform/mobile/mobileUser/")
public class MobileUserController extends MobileBaseController{
	
	@Resource
	private SysUserService sysUserService;
	
	@Resource
	private SysRoleService sysRoleService;
	
	@RequestMapping("list")
	@Action(description = "查看用户表分页列表",ownermodel=SysAuditModelType.USER_MANAGEMENT,exectype="管理日志")
	@ResponseBody
	public Object list(HttpServletRequest request,HttpServletResponse response) throws Exception{
	
		QueryFilter filter = new QueryFilter(request, true);

		List<SysUser> list = sysUserService.getAllMobile(filter);

		return getPageList(list, filter);
	}
	
	@RequestMapping("get")
	@Action(description = "查看用户信息",ownermodel=SysAuditModelType.USER_MANAGEMENT,exectype="管理日志")
	@ResponseBody
	public Object get(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		try{
			Long userId= RequestUtil.getLong(request,"userId");
			SysUser sysUser  =sysUserService.getById(userId);
			map.put("success", true);
			map.put("sysUser", sysUser);
		} catch (Exception e) {
			e.printStackTrace();
			return getError("");
		}
		return map;	
	}
	
	
	@RequestMapping("roleList")
	@Action(description = "查看角色表分页列表",ownermodel=SysAuditModelType.USER_MANAGEMENT,exectype="管理日志")
	@ResponseBody
	public Object roleList(HttpServletRequest request,HttpServletResponse response) throws Exception{
		QueryFilter filter = new QueryFilter(request, true);

		List<SysRole> list = sysRoleService.getRoleList(filter);

		return getPageList(list, filter);
	}
	

}
