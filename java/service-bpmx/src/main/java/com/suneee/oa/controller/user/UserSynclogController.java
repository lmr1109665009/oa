/**
 * @Title: UserSynclogController.java 
 * @Package com.suneee.oa.controller.user 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.controller.user;

import com.suneee.core.page.PageList;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.service.user.UserSynclogService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.UserPosition;
import com.suneee.platform.model.system.UserRole;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.platform.service.system.UserRoleService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.weixin.util.CommonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: UserSynclogController 
 * @Description: 用户同步日志UserSynclog的控制器类 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-01-04 10:58:28 
 *
 */
@Controller
@RequestMapping("/oa/user/userSynclog/")
public class UserSynclogController extends UcpBaseController{
	@Resource
	private UserSynclogService userSynclogService;
	@Resource
	private UserRoleService userRoleService;
	@Resource
	private UserPositionService userPositionService;
	
	/** 
	 * 获取同步日志分页列表 （接口）
	 * 
	 * @param request
	 * @param response
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="获取同步日志分页列表（接口）", detail="获取同步日志分页列表（接口）", execOrder=ActionExecOrder.AFTER, exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			QueryFilter filter = new QueryFilter(request);
			filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
			PageList<Map<String, Object>> userSynclogList = (PageList<Map<String, Object>>)userSynclogService.getList(filter);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取同步日志分页列表成功！", CommonUtil.getListModel(userSynclogList));
		} catch (Exception e) {
			logger.error("获取同步日志分页列表失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取同步日志分页列表失败：" + e.getMessage());
		}
	}
	
	/** 
	 * 获取同步日志详情
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("details")
	@Action(description="获取用户同步失败日志记录详情", detail="获取用户同步失败日志记录详情", execOrder=ActionExecOrder.AFTER, exectype="查询日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo details(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long logId = RequestUtil.getLong(request, "logId");
		if(logId == 0){
			logger.error("获取用户同步失败日志记录详细信息失败：请求参数为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户同步失败日志记录详情失败：请求参数错误！");
		}
		
		try {
			// 获取用户基本信息和失败日志信息
			Map<String, Object> userSynclog = userSynclogService.getByLogId(logId);
			if(userSynclog == null){
				logger.error("获取用户同步失败日志记录详细信息失败：同步失败日志记录不存在！");
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户同步失败日志记录详情失败：同步失败日志记录不存在！");
			}
			Long userId = (Long)userSynclog.get("userId");
			// 获取用户角色信息
			List<UserRole> userRoleList = userRoleService.getByUserId(userId);
			// 获取用户岗位信息
			List<UserPosition> userPosList = userPositionService.getByUserId(userId);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("userSynclog", userSynclog);
			data.put("userRoles", userRoleList);
			data.put("userPositions", userPosList);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取用户同步失败日志记录详情成功", data);
		} catch (Exception e) {
			logger.error("获取用户同步失败日志记录详细信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户同步失败日志记录详情失败：" + e.getMessage());
		}
	}
	
	/** 
	 * 删除同步失败日志
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除同步失败日志", detail="根据日志ID删除同步失败日志", execOrder=ActionExecOrder.AFTER, exectype="", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long[] logIds = RequestUtil.getLongAryByStr(request, "logId");
		if(logIds == null){
			logger.error("删除同步失败日志失败：请求参数不能为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除同步失败日志失败：请求参数错误！");
		}
		
		try {
			userSynclogService.delByIds(logIds);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除同步失败日志成功！");
		} catch (Exception e) {
			logger.error("删除同步失败日志失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除同步失败日志失败：" + e.getMessage());
		}
	}
	
	
	/** 
	 * 获取同步日志分页列表（页面）
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("jspList")
	@Action(description="获取同步日志分页列表（页面）", detail="获取同步日志分页列表（页面）", execOrder=ActionExecOrder.AFTER, exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	public ModelAndView jspList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<Map<String, Object>> userSynclogList = userSynclogService.getList(new QueryFilter(request, "userSynclogItem"));
		return new ModelAndView("/oa/user/userSynclogList.jsp").addObject("userSynclogList", userSynclogList);
	}
}
