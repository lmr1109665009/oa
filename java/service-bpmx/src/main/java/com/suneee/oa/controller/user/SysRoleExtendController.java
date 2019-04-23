/**
 * 
 */
package com.suneee.oa.controller.user;

import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.page.PageList;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.service.user.SysRoleExtendService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysRole;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 角色信息SysRole扩展Controller类
 * 
 * @author xiongxianyun
 *
 */
@Controller
@RequestMapping("/oa/user/sysRoleExtend/")
public class SysRoleExtendController extends UcpBaseController{
	@Resource
	private SysRoleExtendService sysRoleExtendService;
	
	@RequestMapping("getAll")
	@Action(description="获取系统所有角色信息", detail="获取系统所有角色信息", execOrder=ActionExecOrder.AFTER, exectype="管理日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo getAll(HttpServletRequest request, HttpServletResponse response) throws Exception{
		QueryFilter queryFilter = new QueryFilter(request, false);
		Long subSystemId = RequestUtil.getLong(request, "subSystemId", 1L);
		if(subSystemId == 0){
			queryFilter.addFilter("subSystemId", subSystemId);
		}
		queryFilter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		try {
			List<SysRole> roleList = sysRoleExtendService.getAll(queryFilter);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取系统所有角色信息成功！", roleList);
		} catch (Exception e) {
			logger.error("获取系统所有角色信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取系统所有角色信息失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("list")
	@Action(description="获取系统角色信息分页列表", detail="根据查询条件获取系统角色信息分页列表", execOrder=ActionExecOrder.AFTER, 
		exectype="操作日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			QueryFilter filter = new QueryFilter(request, true);
			filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
			PageList<SysRole> roleList = (PageList<SysRole>)sysRoleExtendService.getRoleList(filter);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取系统角色信息分页列表成功", PageUtil.getPageVo(roleList));
		} catch (Exception e) {
			logger.error("获取系统角色信息分页列表失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取系统角色信息分页列表失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("save")
	@Action(description="添加或更新角色信息", detail="<#if isAdd>添加<#else>更新</#if>角色<#if roleId??>"
			+ "<#assign entity=sysRoleService.getById(Long.valueOf(roleId)) /><#if entity!=''>"
			+ "【${SysAuditLinkService.getSysRoleLink(entity)}】</#if></#if>", 
			execOrder=ActionExecOrder.AFTER, exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo save(HttpServletRequest request, HttpServletResponse response, SysRole sysRole) throws Exception{
		SysAuditThreadLocalHolder.putParamerter("isAdd", sysRole.getRoleId() == null);
		Long roleId = RequestUtil.getLong(request, "roleId");
		String message = "更新";
		if(roleId == 0){
			message = "添加";
		}
		// 角色名称必填
		String roleName = RequestUtil.getString(request, "roleName");
		if(StringUtils.isBlank(roleName)){
			logger.error(message + "角色信息失败：角色名称为空");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message + "角色信息失败：请完善角色信息！");
		}
		
		try {
			// 角色所属企业为创建用户的当前企业
			String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
			sysRole.setEnterpriseCode(enterpriseCode);
			// 验证角色名称的唯一性
			SysRole sysRoleDb = sysRoleExtendService.getByRoleName(roleName, roleId, sysRole.getSystemId(), enterpriseCode);
			if(sysRoleDb != null){
				logger.error(message + "角色信息失败：角色名称【" + roleName + "】已经存在！");
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "角色信息失败：角色名称【" + roleName + "】已经存在！");
			}
			
			// 保存角色信息
			sysRoleExtendService.save(sysRole);
			SysAuditThreadLocalHolder.putParamerter("roleId", sysRole.getRoleId().toString());
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, message + "角色信息成功！");
		} catch (Exception e) {
			logger.error(message + "角色信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message + "角色信息失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("del")
	@Action(description="删除角色信息", detail="删除角色信息<#if roleId??><#list StringUtils.split(roleId, \",\") as item>"
			+ "<#assign entity = sysRoleService.getById(Long.valueOf(item))/><#if entity!=''>【${entity.roleName}】"
			+ "</#if></#list></#if>", 
			execOrder=ActionExecOrder.BEFORE, exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long[] roleIds = RequestUtil.getLongAryByStr(request, "roleId");
		if(roleIds == null){
			logger.error("删除角色信息失败：角色ID为空");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除角色信息失败：请求参数错误！");
		}
		
		try {
			sysRoleExtendService.delByIds(roleIds);
			String message = MessageUtil.getMessage();
			if(StringUtil.isEmpty(message)){
				return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除角色信息成功！");
			}
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message.replaceAll("\r\n", "") + "角色中挂有用户，请先清空角色中所有用户！");
		} catch (Exception e) {
			logger.error("删除角色信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除角色信息失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("details")
	@Action(description="获取角色信息详情", detail="获取角色<#if roleId??><#assign entity = sysRoleService.getById(Long.valueOf(roleId))/>"
			+ "<#if entity!=''>【${SysAuditLinkService.getSysRoleLink(entity)}】</#if></#if>详情", 
		execOrder=ActionExecOrder.AFTER, exectype="查询日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo details(HttpServletRequest request, HttpServletResponse response)throws Exception{
		Long roleId = RequestUtil.getLong(request, "roleId");
		if(roleId == 0){
			logger.error("获取角色信息详情失败：角色ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取角色信息详情失败：请求参数错误！");
		}
		
		try {
			SysRole sysRole = sysRoleExtendService.getById(roleId);
			if(sysRole == null){
				logger.error("获取角色信息详情失败：根据角色ID【" + roleId + "】未查询到角色信息！");
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取角色信息详情失败：该角色不存在！");
			}
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取角色信息详情成功！", sysRole);
		} catch (Exception e) {
			logger.error("获取角色信息详情失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取角色信息详情失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("allCategory")
	@Action(description="获取所有角色分类信息", detail="获取所有角色分类信息", execOrder=ActionExecOrder.AFTER, 
	exectype="查询日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo allCategory(HttpServletRequest request, HttpServletResponse response)throws Exception{
		try {
			QueryFilter filter = new QueryFilter();
			filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
			List<String> categoryList = sysRoleExtendService.getAllCategory(filter);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取所有角色分类信息成功！", categoryList);
		} catch (Exception e) {
			logger.error("获取所有角色分类信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取所有角色分类信息失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("enabledRole")
	@Action(description="角色禁用/启用", detail="<#if enabled == 1>启用<#else>禁用</#if>角色<#if roleId??>"
			+ "<#assign entity=sysRoleService.getById(Long.valueOf(roleId)) /><#if entity!=''>"
			+ "【${SysAuditLinkService.getSysRoleLink(entity)}】</#if></#if>", 
		execOrder=ActionExecOrder.AFTER, exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo enabledRole(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long roleId = RequestUtil.getLong(request, "roleId");
		Short enabled = RequestUtil.getShort(request, "enabled");
		String message = "禁用";
		if(enabled == 1){
			message = "启用";
		}
		if(roleId == 0){
			logger.error(message + "角色失败：角色ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message + "角色失败：请求参数错误！");
		}
		try {
			sysRoleExtendService.updEnabled(roleId, enabled);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, message + "角色成功！");
		} catch (Exception e) {
			logger.error(message + "角色失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message + "角色失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("copyRole")
	@Action(description="复制角色信息", detail="", execOrder=ActionExecOrder.AFTER,
			exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo copyRole(HttpServletRequest request, HttpServletResponse response) throws Exception{
		return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "复制角色信息成功！");
	}
}
