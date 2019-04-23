/**
 * @Title: RoleResourcesExtendController.java 
 * @Package com.suneee.oa.controller.system 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.controller.system;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.Resources;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.system.RoleResourcesService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;

/**
 * @ClassName: RoleResourcesExtendController 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-01-18 15:19:28 
 *
 */
@RequestMapping("/oa/system/roleResourcesExtend/")
@Controller
public class RoleResourcesExtendController extends UcpBaseController{
	@Resource
	private RoleResourcesService roleResourcesService;
	
	@RequestMapping("save")
	@Action(description="功能授权", detail="功能授权", execOrder=ActionExecOrder.AFTER, 
		exectype="", ownermodel=SysAuditModelType.SYSTEM_SETTING)
	@ResponseBody
	public ResultVo save(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long roleId = RequestUtil.getLong(request, "roleId");
		Long[] resIds = RequestUtil.getLongAryByStr(request, "resId");
		Long systemId = RequestUtil.getLong(request, "systemId", 1L);
		if(roleId == 0){
			logger.error("功能授权失败：角色ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "功能授权失败：请求参数错误！");
		}
		
		try {
			roleResourcesService.update(systemId, roleId, resIds, Resources.FROMTYPE_CLIENT);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "功能授权成功！");
		} catch (Exception e) {
			logger.error("功能授权失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "功能授权失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("saveBatch")
	@Action(description="批量功能授权", detail="批量功能授权", execOrder=ActionExecOrder.AFTER, 
		exectype="", ownermodel=SysAuditModelType.SYSTEM_SETTING)
	@ResponseBody
	public ResultVo saveBatch(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long[] roleIds = RequestUtil.getLongAryByStr(request, "roleId");
		Long[] resIds = RequestUtil.getLongAryByStr(request, "resId");
		Long systemId = RequestUtil.getLong(request, "systemId", 1L);
		if(roleIds == null || resIds == null){
			logger.error("批量功能授权失败：角色ID或资源ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "批量功能授权失败：请求参数错误！");
		}
		
		try {
			roleResourcesService.saveBatch(systemId, roleIds, resIds, Resources.FROMTYPE_CLIENT);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "批量功能授权成功！");
		} catch (Exception e) {
			logger.error("批量功能授权失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "批量功能授权失败：" + e.getMessage());
		}
	}
}
