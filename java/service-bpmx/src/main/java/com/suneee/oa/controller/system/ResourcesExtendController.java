/**
 * @Title: ResourcesExtendController.java 
 * @Package com.suneee.oa.controller.system 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.controller.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.page.PageList;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.*;
import com.suneee.platform.service.system.ResourcesService;
import com.suneee.platform.service.system.SubSystemService;
import com.suneee.oa.service.system.ResourcesExtendService;
import com.suneee.oa.service.user.SysRoleExtendService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @ClassName: ResourcesExtendController 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-01-16 19:18:44 
 *
 */
@Controller
@RequestMapping("/oa/system/resourcesExtend/")
public class ResourcesExtendController extends UcpBaseController{
	@Resource
	private ResourcesExtendService resourcesExtendService;
	@Resource
	private ResourcesService resourcesService;
	@Resource
	private SysRoleExtendService sysRoleExtendService;
	@Resource
	private SubSystemService subSystemService;
	
	@RequestMapping("save")
	@Action(description="保存资源信息", detail="<#if isAdd>添加<#else>更新</#if>子系统资源<#if resId??>" +
			"【${SysAuditLinkService.getResourcesLink(Long.valueOf(resId))}】</#if><#if isSuccessed>成功<#else>失败</#if>", 
			execOrder=ActionExecOrder.AFTER, exectype="管理日志", ownermodel=SysAuditModelType.SYSTEM_SETTING)
	@ResponseBody
	public ResultVo save(HttpServletRequest request, HttpServletResponse response, Resources resources)throws Exception{
		SysAuditThreadLocalHolder.putParamerter("isAdd", resources.getResId() == null);
		SysAuditThreadLocalHolder.putParamerter("isSuccessed", false);
		// 判断别名的唯一性
		Long systemId = RequestUtil.getLong(request, "systemId", 1L);
		String alias = RequestUtil.getString(request, "alias");
		Resources resourcesDb = resourcesExtendService.getByAliasForCheck(resources.getResId(), alias, systemId);
		if(resourcesDb != null){
			logger.error("保存资源信息失败：资源别名【" + alias + "】已经存在！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "保存资源信息失败：资源别名【" + alias + "】已经存在！");
		}
		try {
			resourcesExtendService.save(resources);
			SysAuditThreadLocalHolder.putParamerter("resId", resources.getResId().toString());
			SysAuditThreadLocalHolder.putParamerter("isSuccessed", true);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "保存资源信息成功！");
		} catch (Exception e) {
			logger.error("保存资源信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "保存资源信息失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("list")
	@Action(description="获取资源信息分页列表", detail="获取资源信息分页列表", execOrder=ActionExecOrder.AFTER, 
		exectype="操作日志", ownermodel=SysAuditModelType.SYSTEM_SETTING)
	@ResponseBody
	public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			QueryFilter filter = new QueryFilter(request);
			filter.addFilter("fromType", Resources.FROMTYPE_CLIENT);
			PageList<Resources> resourcesList = (PageList<Resources>)resourcesExtendService.getAll(filter);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取资源信息分页列表成功！", PageUtil.getPageVo(resourcesList));
		} catch (Exception e) {
			logger.error("获取资源信息分页列表失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取资源信息分页列表失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("getAll")
	@Action(description="获取系统所有资源信息", detail="获取系统所有资源信息", execOrder=ActionExecOrder.AFTER, 
		exectype="操作日志", ownermodel=SysAuditModelType.SYSTEM_SETTING)
	@ResponseBody
	public ResultVo getAll(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long systemId = RequestUtil.getLong(request, "systemId");
		QueryFilter filter = new QueryFilter(request, false);
		if(systemId == 0){
			filter.addFilter("systemId", 1L);
		}
		filter.addFilter("fromType", Resources.FROMTYPE_CLIENT);
		try {
			List<Resources> resourcesList = resourcesExtendService.getAll(filter);		
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取系统所有资源信息成功！", resourcesList);
		} catch (Exception e) {
			logger.error("获取系统所有资源信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取系统所有资源信息失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("details")
	@Action(description="获取系统资源详情", execOrder=ActionExecOrder.AFTER, 
		detail="获取系统资源<#if resId??>【${SysAuditLinkService.getResourcesLink(Long.valueOf(resId))}】</#if>详情", 
		exectype="操作日志", ownermodel=SysAuditModelType.SYSTEM_SETTING)
	@ResponseBody
	public ResultVo details(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long resId = RequestUtil.getLong(request, "resId");
		if(resId == 0){
			logger.error("获取系统资源详情失败：资源ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取系统资源详情失败：请求参数错误！");
		}
		
		try {
			Resources resources = resourcesExtendService.getById(resId);
			if(resources == null){
				logger.error("获取系统资源详情失败：ID为【" + resId + "】的系统资源不存在");
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取系统资源详情失败：系统资源不存在！");
			}
			// 获取父资源名称
			Resources parentRes = resourcesExtendService.getById(resources.getParentId());
			if(parentRes != null){
				resources.setParentName(parentRes.getResName());
			}
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取系统资源详情成功！", resources);
		} catch (Exception e) {
			logger.error("获取系统资源详情失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取系统资源详情失败：" + e.getMessage());
		}
	}
	
	@RequestMapping(value="del")
	@Action(description="删除系统资源信息", detail= "删除子系统资源<#if resId??><#list StringUtils.split(resId,\",\") as item>"
		+ "<#assign entity=resourcesService.getById(Long.valueOf(item))/><#if entity!=''>【${entity.resName}】</#if></#list></#if>", 
		execOrder=ActionExecOrder.BEFORE, exectype="管理日志", ownermodel=SysAuditModelType.SYSTEM_SETTING)
	@ResponseBody
	public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long[] resIds = RequestUtil.getLongAryByStr(request, "resId");
		if(resIds == null){
			logger.error("删除系统资源信息失败：资源ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除系统资源信息失败：请求参数错误！");
		}
		
		try {
			resourcesExtendService.delByIds(resIds);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除系统资源信息成功！");
		} catch (Exception e) {
			logger.error("删除系统资源信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除系统资源信息失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("setDisplay")
	@Action(description="设置资源显示/隐藏", detail="设置资源<#if resId??>【${SysAuditLinkService.getResourcesLink(Long.valueOf(resId))}】</#if>"
		+ "<#if isDisplayInMenu == 1>显示<#else>隐藏</#if>", execOrder=ActionExecOrder.AFTER, 
		exectype="管理日志", ownermodel=SysAuditModelType.SYSTEM_SETTING)
	@ResponseBody
	public ResultVo setDisplay(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long resId = RequestUtil.getLong(request, "resId");
		Short isDisplayInMenu = RequestUtil.getShort(request, "isDisplayInMenu");
		String message = "设置资源隐藏";
		if(Resources.IS_DISPLAY_IN_MENU_Y.equals(isDisplayInMenu)){
			message = "设置资源显示";
		}
		if(resId == 0){
			logger.error(message + "失败：资源ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message + "设置失败：请求参数错误！");
		}
		
		try {
			resourcesExtendService.updDisplay(resId, isDisplayInMenu);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, message + "成功！");
		} catch (Exception e) {
			logger.error(message + "失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message + "失败：" + e.getMessage());
		}
		
	}
	
	@RequestMapping("siblingResources")
	@Action(description="获取同级资源列表", detail="获取资源<#if resId??>【${SysAuditLinkService.getResourcesLink(Long.valueOf(resId))}】"
		+ "</#if>的同级资源列表", 
		execOrder=ActionExecOrder.AFTER, exectype="操作日志", ownermodel=SysAuditModelType.SYSTEM_SETTING)
	@ResponseBody
	public ResultVo siblingResources(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long resId = RequestUtil.getLong(request, "resId");
		if(resId == 0){
			logger.error("获取同级资源列表失败：资源ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取同级资源列表失败：请求参数错误！");
		}
		
		try {
			Resources resources = resourcesExtendService.getById(resId);
			if(resources == null){
				logger.error("获取同级资源列表失败：系统不存在ID为【" + resId + "】的资源！");
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取同级资源列表失败：资源不存在！");
			}
			
			List<Resources> resourcesList = resourcesService.getByParentId(resources.getParentId(), Resources.FROMTYPE_CLIENT);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取同级资源列表成功！", resourcesList);
		} catch (Exception e) {
			logger.error("获取同级资源列表失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取同级资源列表失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("sort")
	@Action(description="资源排序", detail="资源排序", execOrder=ActionExecOrder.AFTER, 
		exectype="管理日志", ownermodel=SysAuditModelType.SYSTEM_SETTING)
	@ResponseBody
	public ResultVo sort(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long[] resIds = RequestUtil.getLongAryByStr(request, "resId");
		if(resIds == null){
			logger.error("资源排序失败：资源ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "资源排序失败：请求参数错误！");
		}
		
		try {
			resourcesExtendService.sort(resIds);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "资源排序成功！");
		} catch (Exception e) {
			logger.error("资源排序失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "资源排序失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("authList")
	@Action(description="获取角色的授权资源列表", detail="获取角色授权资源列表", execOrder=ActionExecOrder.AFTER, exectype="操作日志", ownermodel=SysAuditModelType.SYSTEM_SETTING)
	@ResponseBody
	public ResultVo authList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long roleId = RequestUtil.getLong(request, "roleId");
		if(roleId == 0){
			logger.error("获取角色的授权资源列表失败：角色ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取角色的授权资源列表失败：请求参数错误！");
		}
		SysRole role = sysRoleExtendService.getById(roleId);
		if(role == null){
			logger.error("获取角色的授权资源列表失败：角色ID为【" + roleId + "】的角色不存在");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取角色的授权资源列表失败：角色不存在！");
		}
		
		try {
			Long systemId = role.getSystemId() == 0 ? 1 : role.getSystemId();
			List<Resources> resources = resourcesService.getBySysRolResChecked(systemId , roleId, Resources.FROMTYPE_CLIENT);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取角色的授权资源列表成功！", resources);
		} catch (Exception e) {
			logger.error("获取角色的授权资源列表失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取角色的授权资源列表失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("getRoleResources")
	@Action(description="获取用户的资源权限信息", detail="获取用户的资源权限信息", execOrder=ActionExecOrder.AFTER, exectype="操作日志", ownermodel=SysAuditModelType.SYSTEM_SETTING)
	@ResponseBody
	public ResultVo getRoleResources(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			SubSystem currentSystem = subSystemService.getById(1L);
			List<Resources> resourcesList=resourcesService.getSysMenu(currentSystem,(SysUser) ContextUtil.getCurrentUser(), Resources.FROMTYPE_CLIENT);
			ResourcesService.addIconCtxPath(resourcesList, request.getContextPath());
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取用户的资源权限信息成功！", resourcesList);
		} catch (Exception e) {
			logger.error("获取用户的资源权限信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户的资源权限信息失败：" + e.getMessage());
		}
	}
}
