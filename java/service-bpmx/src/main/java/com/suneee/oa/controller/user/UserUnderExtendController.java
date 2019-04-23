/**
 * @Title: UserUnderExtendController.java 
 * @Package com.suneee.oa.controller.user 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.controller.user;

import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.page.PageList;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.system.UserUnderService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.weixin.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @ClassName: UserUnderExtendController 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-05-02 13:08:30 
 *
 */
@Controller
@RequestMapping("/oa/user/userUnderExtend/")
public class UserUnderExtendController extends UcpBaseController{
	@Resource
	private UserUnderService userUnderService;
	
	/** 
	 * 获取用户下属列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@ResponseBody
	public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long userId = RequestUtil.getLong(request, "userId");
		if(userId == 0){
			logger.error("获取用户下属列表失败：用户ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户下属列表失败：请求参数错误！");
		}
		QueryFilter filter = new QueryFilter(request, true);
		filter.addFilter("userId", userId);
		filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		try {
			PageList<Map<String, Object>> list = (PageList<Map<String, Object>>)userUnderService.getUnderUser(filter);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取用户下属列表成功！", CommonUtil.getListModel(list));
		} catch (Exception e) {
			logger.error("获取用户下属列表失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取用户下属列表失败：" + e.getMessage());
		}
	}
	
	/** 
	 * 添加下属
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("add")
	@Action(description="添加下属", detail="为用户【${SysAuditLinkService.getSysUserLink(Long.valueOf(userId))}】添加" +
		"<#list StringUtils.split(userIds,\",\") as item> 【${SysAuditLinkService.getSysUserLink(Long.valueOf(item))}】"+
		"</#list> 等下属", execOrder=ActionExecOrder.AFTER, exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo add(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String userIds= RequestUtil.getSecureString(request, "userIds");
		String userNames=RequestUtil.getSecureString(request, "userNames");
		Long userId = RequestUtil.getLong(request, "userId");
		if(userId==0 || StringUtils.isBlank(userIds) || StringUtils.isBlank(userNames)){
			logger.error("添加下属失败：请求参数为空");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "添加下属失败：请求参数错误！");
		}
		try {
			userUnderService.addMyUnderUser(userId, userIds, userNames);
			SysAuditThreadLocalHolder.putParamerter("userId", userId);
			SysAuditThreadLocalHolder.putParamerter("userIds", userIds);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "添加下属成功！");
		} catch (Exception e) {
			logger.error("添加下属失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "添加下属失败：" + e.getMessage());
		}
	}
	
	/** 
	 * 删除用户下属
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除用户下属", detail="<#list StringUtils.split(id,\",\") as item>"
			+ "<#assign entity=userUnderService.getById(Long.valueOf(item))/>"
			+ "删除用户【${SysAuditLinkService.getSysUserLink(Long.valueOf(entity.userid))}】的下属"
			+ "【${SysAuditLinkService.getSysUserLink(Long.valueOf(entity.underuserid))}】,"
			+ "</#list>",  execOrder=ActionExecOrder.AFTER, exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long[] userUnderIds = RequestUtil.getLongAryByStr(request, "id");
		if(userUnderIds == null){
			logger.error("删除用户下属失败：ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除用户下属失败：请求参数错误！");
		}
		try {
			userUnderService.delByIds(userUnderIds);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除用户下属成功！");
		} catch (Exception e) {
			logger.error("删除用户下属失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除用户下属失败：" + e.getMessage());
		}
	}
}
