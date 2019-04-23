/**
 * @Title: SubSystemExtendController.java 
 * @Package com.suneee.oa.controller.user 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.controller.user;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.suneee.core.page.PageList;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.SubSystem;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.oa.service.user.SubSystemExtendService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;

/**
 * @ClassName: SubSystemExtendController 
 * @Description: 子系统SubSystem的扩展Controller类
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-01-12 10:26:28 
 *
 */
@Controller
@RequestMapping("/oa/user/subSystemExtend/")
public class SubSystemExtendController extends UcpBaseController{
	@Resource
	private SubSystemExtendService subSystemExtendService;
	
	@RequestMapping("getAll")
	@Action(description="获取所有子系统信息", detail="根据查询条件获取子系统信息", execOrder=ActionExecOrder.AFTER, exectype="操作日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo getAll(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			List<SubSystem> subSystemList = subSystemExtendService.getAll(new QueryFilter(request, false));
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取所有子系统信息成功！", subSystemList);
		} catch (Exception e) {
			logger.error("获取所有子系统信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取所有子系统信息失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("list")
	@Action(description="获取子系统信息分页列表", detail="根据查询条件获取子系统信息分页列表", execOrder=ActionExecOrder.AFTER, exectype="操作日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			PageList<SubSystem> subSystemList = 
					(PageList<SubSystem>)subSystemExtendService.getAll(new QueryFilter(request, true));
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取子系统信息分页列表成功！", PageUtil.getPageVo(subSystemList));
		} catch (Exception e) {
			logger.error("获取子系统信息分页列表失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取子系统信息分页列表失败：" + e.getMessage());
		}
	}
}
