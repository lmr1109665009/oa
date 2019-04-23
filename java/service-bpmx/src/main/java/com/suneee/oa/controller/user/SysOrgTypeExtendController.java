/**
 * 
 */
package com.suneee.oa.controller.user;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysOrgType;
import com.suneee.platform.service.system.SysOrgTypeService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;

/**
 * 组织类型SysOrgType扩展Controller类
 * 
 * @author xiongxianyun
 *
 */
@Controller
@RequestMapping("/oa/user/sysOrgTypeExtend")
public class SysOrgTypeExtendController extends UcpBaseController{
	@Resource
	private SysOrgTypeService sysOrgTypeService;
	
	@RequestMapping("getAll")
	@Action(description="获取组织类型信息列表", detail="根据维度ID获取组织类型信息", execOrder=ActionExecOrder.AFTER, exectype="", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo getAll(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long demId = RequestUtil.getLong(request, "demId");
		if(demId == 0){
			logger.error("获取组织类型信息列表失败：请求参数不能为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织类型信息列表失败！");
		}
		try {
			List<SysOrgType> sysOrgTypeList = sysOrgTypeService.getByDemId(demId);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取组织类型信息列表成功！", sysOrgTypeList);
		} catch (Exception e) {
			logger.error("获取组织类型信息列表失败：" + e.getMessage(), e);;
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织类型信息列表失败！");
		}
	}
}
