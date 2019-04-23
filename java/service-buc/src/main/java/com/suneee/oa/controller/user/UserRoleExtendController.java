/**
 * @Title: UserRoleExtendController.java 
 * @Package com.suneee.oa.controller.user 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.controller.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.suneee.core.page.PageList;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.UserRole;
import com.suneee.oa.service.user.UserRoleExtendService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;

/**
 * @ClassName: UserRoleExtendController 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-01-13 15:09:37 
 *
 */
@RequestMapping("/oa/user/userRoleExtend/")
@Controller
public class UserRoleExtendController extends UcpBaseController{
	@Resource
	private UserRoleExtendService userRoleExtendService;
	
	@RequestMapping("save")
	@Action(description="保存用户角色关系信息", detail="将用户<#if userId??><#list StringUtils.split(userId,\",\") as item>"
			+ "【${SysAuditLinkService.getSysUserLink(Long.valueOf(item))}】</#list></#if>加入角色<#if roleId??>"
			+ "<#assign role=sysRoleService.getById(Long.valueOf(roleId))/><#if role!=''>"
			+ "【${SysAuditLinkService.getSysRoleLink(role)}】</#if></#if>下", execOrder=ActionExecOrder.AFTER, 
		exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo save(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long roleId = RequestUtil.getLong(request, "roleId");
		Long[] userIds = RequestUtil.getLongAryByStr(request, "userId");
		if(roleId == 0 || userIds == null){
			logger.error("保存用户角色关系信息失败：角色ID或用户ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "保存用户角色关系信息失败：请求参数错误!");
		}
		try {
			userRoleExtendService.save(roleId, userIds);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "保存用户角色关系信息成功！");
		} catch (Exception e) {
			logger.error("保存用户角色关系信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "保存用户角色关系信息失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("list")
	@Action(description="获取用户角色关系信息分页列表", detail="获取用户角色关系信息分页列表", execOrder=ActionExecOrder.AFTER, 
		exectype="操作日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long roleId = RequestUtil.getLong(request, "roleId");
		if(roleId == 0){
			logger.error("获取用户角色关系信息列表失败：角色ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户角色关系信息列表失败：请求参数错误");
		}
		try {
			PageList<UserRole> userRoleList = (PageList<UserRole>)userRoleExtendService.getAll(new QueryFilter(request, true));
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取用户角色关系信息列表成功！", PageUtil.getPageVo(userRoleList));
		} catch (Exception e) {
			logger.error("获取角色用户信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户角色关系信息列表失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("del")
	@Action(description="删除用户角色关系", detail="将用户<#if userRoleId??><#list StringUtils.split(userRoleId,\",\") as item>"
			+ "<#assign userRole=userRoleService.getById(Long.valueOf(item))/><#if userRole!=''><#assign roleId=userRole.roleId/>"
			+ "【${SysAuditLinkService.getSysUserLink(Long.valueOf(userRole.userId))}】</#if></#list>从角色<#if roleId??>"
			+ "<#assign role = sysRoleService.getById(Long.valueOf(roleId))/><#if role!=''>【${SysAuditLinkService.getSysRoleLink(role)}】"
			+ "</#if></#if>下移除<#else>从角色下移除</#if>", 
		execOrder=ActionExecOrder.BEFORE, exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long[] userRoleIds = RequestUtil.getLongAryByStr(request, "userRoleId");
		if(userRoleIds == null){
			logger.error("删除用户角色关系失败：用户角色ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除用户角色关系失败：请求参数错误！");
		}
		
		try {
			userRoleExtendService.delByIds(userRoleIds);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除用户角色关系成功！");
		} catch (Exception e) {
			logger.error("删除用户角色关系失败：" + e.getMessage());
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除用户角色关系失败：" + e.getMessage());
		}
	}
}
