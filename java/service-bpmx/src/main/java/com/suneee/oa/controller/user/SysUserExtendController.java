/**
 * 
 */
package com.suneee.oa.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.page.PageList;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.model.user.UserEnterprise;
import com.suneee.oa.model.user.UserSynclog;
import com.suneee.oa.service.user.*;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.*;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.platform.service.system.UserRoleService;
import com.suneee.platform.service.system.UserUnderService;
import com.suneee.platform.service.system.impl.OrgServiceImpl;
import com.suneee.ucp.base.common.ResultConst;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.service.system.SysUserExtService;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.weixin.model.ListModel;
import com.suneee.weixin.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 用户信息SysUser扩展Controller类
 * 
 * @author xiongxianyun
 *
 */
@Controller
@RequestMapping("/oa/user/sysUserExtend/")
public class SysUserExtendController extends UcpBaseController{
	@Resource
	private SysUserExtendService sysUserExtendService;
	@Resource
	private SysOrgExtendService sysOrgExtendService;
	@Resource
	private SysUserExtService sysUserExtService;
	@Resource
	private SysUserService sysUserService;
	@Resource
	private UserRoleService userRoleService;
	@Resource
	private UserPositionService userPositionService;
    @Resource
    private OrgServiceImpl orgServiceImpl;
    @Resource
    private UserPositionExtendService userPositionExtendService;
	@Resource
	private UserSynclogService userSynclogService;
	@Resource
	private UserUnderService userUnderService;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;
	@Resource
	private UserEnterpriseService userEnterpriseService;
	/**
	 * 获取系统用户列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("list")
	@Action(description="获取系统用户列表", detail="获取系统用户列表", execOrder=ActionExecOrder.AFTER, exectype="操作日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo list(HttpServletRequest request, HttpServletResponse response){
		// 构造查询对象
		QueryFilter filter = new QueryFilter(request);
		
		// 根据部门查找时，需要查找到包括子部门的用户
		Long orgId = RequestUtil.getLong(request, "Q_orgId_L");
		if(orgId != 0){
			filter.addFilter("orgId", null);
			SysOrg sysOrg = sysOrgExtendService.getById(orgId);
			if(sysOrg != null){
				filter.addFilter("orgPath", sysOrg.getPath());
			}
		}
		String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
		filter.addFilter("enterpriseCode", enterpriseCode);
		try {
			// 查询当前用户的会议室预定信息列表
			PageList<SysUser> list = (PageList<SysUser>)sysUserExtendService.getAllUser(filter);
			return new ResultVo(ResultConst.COMMON_STATUS_SUCCESS, "获取系统用户列表成功!", CommonUtil.getListModel(list));
		} catch (Exception e) {
			logger.error("获取系统用户列表失败", e);
			return new ResultVo(ResultConst.COMMON_STATUS_FAILED, "获取系统用户列表失败：" + e.getMessage());
		}
	}
	
	/**
	 * 获取系统用户列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("delList")
	@Action(description="获取已删除用户列表", detail="获取已删除用户列表", execOrder=ActionExecOrder.AFTER, exectype="操作日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo delList(HttpServletRequest request, HttpServletResponse response){
		// 构造查询对象
		QueryFilter filter = new QueryFilter(request);
		
		// 查询状态为删除的用户
		filter.addFilter("status", SysUser.STATUS_Del);
		// 查询用户当前企业下的已删用户
		String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
		filter.addFilter("enterpriseCode", enterpriseCode);
		try {
			// 查询用户信息列表
			PageList<SysUser> list = (PageList<SysUser>)sysUserExtendService.getAllDelUser(filter);
			return new ResultVo(ResultConst.COMMON_STATUS_SUCCESS, "获取系统已删除用户列表成功!", CommonUtil.getListModel(list));
		} catch (Exception e) {
			logger.error("获取系统用户列表失败", e);
			return new ResultVo(ResultConst.COMMON_STATUS_FAILED, "获取系统已删除用户列表失败：" + e.getMessage());
		}
	}
	
	/**
	 * 判断账号是否已经注册
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("isAccountExist")
	@Action(description="判断账号是否已注册", detail="判断账号<#if account??>【${account}】</#if>是否已注册", 
		execOrder=ActionExecOrder.AFTER, exectype="操作日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo isAccountExist(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String account = RequestUtil.getString(request, "account");
		if(StringUtils.isBlank(account)){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "账号不能为空！");
		}
		Long userId = RequestUtil.getLong(request, "userId");
		try {
			// 判断账号在本系统中是否存在
			boolean isExist = sysUserExtendService.isAccountExist(account, userId);
			if(isExist){
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "该账号已注册，请重新输入！");
			}
			// 账号不存在
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "该账号未注册！");
		} catch (Exception e) {
			logger.error("账号验证失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "账号验证失败，请稍后再试！");
		}
	}
	
	/**
	 * 判断手机号是否已注册
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("isMobileExist")
	@Action(description="判断手机号是否已注册", detail="判断手机号<#if mobile??>【${mobile}】</#if>是否已注册", 
		execOrder=ActionExecOrder.AFTER, exectype="操作日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo isMobileExist(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String mobile = RequestUtil.getString(request, "mobile");
		if(StringUtils.isBlank(mobile)){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "手机号码不能为空！");
		}
		Long userId = RequestUtil.getLong(request, "userId");
		try {
			// 判断该手机号在本系统中是否存在
			boolean isExist = sysUserExtendService.isMobileExist(mobile, userId);
			if(isExist){
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "该手机号码已注册，请重新输入！");
			}
			
			// 根据手机号从用户中心获取用户信息
			SysUser user = sysUserExtService.getUserFromUserCenter(mobile, null, null, null);
			// 该手机号在用户中心已经存在，在提示用户该手机号码已注册并返回用户中心的用户信息
			if(user != null){
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "该手机号码已注册，是否将其转为B用户？", user);
			}
			
			// 手机号在本系统和用户中心都不存在
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "该手机号码未注册！");
		} catch (Exception e) {
			logger.error("手机号验证失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "手机号验证失败，请稍后再试！");
		}
	}
	
	/**
	 * 判断邮箱是否已注册
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("isEmailExist")
	@Action(description="判断邮箱是否已注册", detail="判断邮箱<#if email??>【${email}】</#if>是否已注册", 
		execOrder=ActionExecOrder.AFTER, exectype="操作日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo isEmailExist(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String email = RequestUtil.getString(request, "email");
		if(StringUtils.isBlank(email)){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "邮箱不能为空！");
		}
		Long userId = RequestUtil.getLong(request, "userId");
		try {
			// 判断该邮箱在本系统中是否存在
			boolean isExist = sysUserExtendService.isEmailExist(email, userId);
			if(isExist){
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "该邮箱已注册，请重新输入！");
			}
			
			// 根据邮箱从用户中心获取用户信息
			SysUser user = sysUserExtService.getUserFromUserCenter(null, email, null, null);
			// 该邮箱在用户中心已经存在，在提示用户该邮箱已注册并返回用户中心的用户信息
			if(user != null){
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "该邮箱已注册，是否将其转为B用户？", user);
			}
			
			// 邮箱在本系统和用户中心都不存在
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "该邮箱未注册！");
		} catch (Exception e) {
			logger.error("邮箱验证失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "邮箱验证失败，请稍后再试！");
		}
	}
	
	@RequestMapping("save")
	@Action(description="保存用户信息", detail="<#if isAdd>添加<#else>更新</#if>用户信息<#if userId??>"
			+ "【${SysAuditLinkService.getSysUserLink(Long.valueOf(userId))}】</#if><#if isSuccessed>成功<#else>失败</#if>", 
			execOrder=ActionExecOrder.AFTER, exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo save(HttpServletRequest request, HttpServletResponse response, SysUser sysUser) throws Exception{
		SysAuditThreadLocalHolder.putParamerter("isAdd", sysUser.getUserId() == null);
		SysAuditThreadLocalHolder.putParamerter("isSuccessed", false);
		try {
			Long userId = sysUser.getUserId();
			// 判断账号是否已经存在
			boolean isAccountExist = sysUserExtendService.isAccountExist(sysUser.getAccount(), userId);
			if(isAccountExist){
				logger.error("保存用户信息失败：账号【" + sysUser.getAccount() + "】已注册");
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "该账号已注册，请重新输入！");
			}
			
			// 判断手机号是否已经存在
			boolean isMobileExist = sysUserExtendService.isMobileExist(sysUser.getMobile(), userId);
			if(isMobileExist){
				logger.error("保存用户信息失败：手机号【" + sysUser.getMobile() + "】已注册");
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "该手机号已注册，请重新输入！");
			}
			
			// 判断邮箱是否已经存在
			boolean isEmailExist = sysUserExtendService.isEmailExist(sysUser.getEmail(), userId);
			if(isEmailExist){
				logger.error("保存用户信息失败： 邮箱【" + sysUser.getEmail() + "】已注册");
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "该邮箱已注册，请重新输入！");
			}
			// 用户所属角色
			Long[] roleIds = RequestUtil.getLongAryByStr(request, "roleId");
			if(roleIds.length == 0){
				logger.error("保存用户信息失败：未给用户分配角色");
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请为用户分配角色！");
			}
			// 用户所属岗位
			String positions = RequestUtil.getString(request, "position");
			if(StringUtils.isBlank(positions)){
				logger.error("保存用户信息失败：未给用户设置职务");
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请为用户设置职务！");
			}
			
			// 用户关联的企业信息
			String[] userEnterprise = RequestUtil.getStringAryByStr(request, "userEnterprise");
			if(userEnterprise == null){
				logger.error("保存用户信息失败：用户未关联企业");
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请为用户关联企业！");
			}
			sysUser.setUserEnterprises(userEnterprise);
			
			// 用户上级设置
			Long[] superiorIds =  RequestUtil.getLongAryByStr(request, "superiorId");
			sysUser.setSuperiorIds(superiorIds);
			
			sysUserExtendService.save(sysUser, roleIds, positions);
			SysAuditThreadLocalHolder.putParamerter("userId", sysUser.getUserId());
			SysAuditThreadLocalHolder.putParamerter("isSuccessed", true);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "保存用户信息成功！");
		} catch (Exception e) {
			logger.error("保存用户信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "保存用户信息失败：" + e.getMessage());
		}
	}
	
	/**
	 * 获取用户详情
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("details")
	@Action(description="获取用户详情",detail="获取用户<#if userId??>【${SysAuditLinkService.getSysUserLink(Long.valueOf(userId))}】"
			+ "</#if>详情", execOrder=ActionExecOrder.AFTER, exectype="操作日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo details(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long userId = RequestUtil.getLong(request, "userId");
		if(userId == 0){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请求参数错误！");
		}
		try {
			// 获取用户基本信息
			SysUser sysUser = sysUserExtendService.getById(userId);
			// 获取用户角色
			List<UserRole> roleList = userRoleService.getByUserId(userId);
			// 获取用户所属岗位
			List<UserPosition> posList = userPositionService.getByUserId(userId);
			// 获取用户上级
			List<SysUser> userUnders = userUnderService.getMyLeaders(userId);
			// 获取用户关联企业信息
			List<Enterpriseinfo> userEnterprises = enterpriseinfoService.getByUserId(userId);
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("user", sysUser);
			result.put("userRoles", roleList);
			result.put("userPositions", posList);
			result.put("userUnders", userUnders);
			result.put("userEnterprises", userEnterprises);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取用户详情成功！", result);
		} catch (Exception e) {
			logger.error("获取用户详情失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户详情失败！");
		}
	}



	/**
	 * 批量激活/禁用用户
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("changeStatus")
	@Action(description="批量激活/禁用用户", detail="批量<#if status==1>激活<#else>禁用</#if>用户<#if userId??>"
			+ "<#list StringUtils.split(userId,\",\") as item>"
		+ "【${SysAuditLinkService.getSysUserLink(Long.valueOf(item))}】</#list></#if>", 
		execOrder=ActionExecOrder.AFTER, exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo changeStatus(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long[] userIds = RequestUtil.getLongAryByStr(request, "userId");
		Short status = RequestUtil.getShort(request, "status");
		String message = "禁用用户";
		if(SysUser.STATUS_OK.equals(status)){
			message = "激活用户";
		}
		try {
			if(userIds == null){
				logger.error(message + "失败：用户ID为空");
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message + "失败：请求参数错误！");
			}
			for(Long userId : userIds){
				sysUserService.updStatus(userId, status, null);
			}
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, message + "成功！");
		} catch (Exception e) {
			logger.error(message + "失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message + "失败！");
		}
	}
	
	@RequestMapping("getUserByAccount")
	@Action(description="获取用户信息", detail="根据账号<#if account??>【${account}】</#if>获取用户信息<#if account??>"
		+ "【SysAuditLinkService.getSysUserLink(account)】</#if>", execOrder=ActionExecOrder.AFTER, exectype="操作日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo getUserByAccount(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String account = RequestUtil.getString(request, "account");
		if(StringUtils.isBlank(account)){
			logger.error("根据账号获取用户信息失败：账号为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户信息失败：请求参数错误！");
		}
		try {
			SysUser user = sysUserService.getByAccount(account);
			if(user == null){
				logger.error("根据账号获取用户信息失败：账号为【" + account + "】的用户不存在！");
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户信息失败：用户不存在！");
			}
			// 不返回用户密码
			user.setPassword(null);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取用户信息成功！", user);
		} catch (Exception e) {
			logger.error("根据账号获取用户信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户信息失败：" + e.getMessage());
		}
	}

	/**
	 * 人员选择器接口
	 */
	@RequestMapping("selectorJson")
	@ResponseBody
	@Action(description = "用户选择器Json列表", execOrder = ActionExecOrder.AFTER, detail = "用户选择器Json", exectype = "管理日志")
	public ResultVo getSelectorJson(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json = new JSONObject();
		PageList<SysUser> list = null;
		String searchBy = RequestUtil.getString(request, "searchBy");
		String type = RequestUtil.getString(request, "type");
		String typeVal = RequestUtil.getString(request, "typeVal");
		int includSub = RequestUtil.getInt(request, "includSub", 0);
		String isSingle = RequestUtil.getString(request, "isSingle", "false");
		QueryFilter queryFilter = new QueryFilter(request);
		//查询条件乱码问题
		Map<String, Object> filter = queryFilter.getFilters();
		/*if(StringUtil.isNotEmpty((String) filter.get("fullname"))){
			String fullname = new String(request.getParameter("fullname").getBytes("iso-8859-1"), "utf-8");
			String fullname = new String(request.getParameter("fullname"));
			fullname = changceFilter(fullname);
			filter.put("fullname", fullname);
		}*/

		/*if(StringUtil.isNotEmpty((String) filter.get("aliasname"))){
			String aliasname = new String(request.getParameter("aliasname").getBytes("iso-8859-1"), "utf-8");
			aliasname = changceFilter(aliasname);
			filter.put("aliasname", aliasname);
		}*/

		if(StringUtil.isNotEmpty((String) filter.get("fullname"))){
			String fullname = changceFilter((String) filter.get("fullname"));
			filter.put("fullname", fullname);
		}

		if(StringUtil.isNotEmpty((String) filter.get("aliasname"))){
			String aliasname = changceFilter((String) filter.get("aliasname"));
			filter.put("aliasname", aliasname);
		}

		// 加载用户当前所属企业下的用户信息
		queryFilter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		try {
			if (SystemConst.SEARCH_BY_ONL.equals(searchBy)) {
				String demId = RequestUtil.getString(request, "path");
				if (demId.equals("-1")) {//未分配组织的或者没有主组织的用户
					list = (PageList<SysUser>)sysUserService.getUserNoOrg(queryFilter);
				} else {
					queryFilter.addFilter("isPrimary", 1);
					list =(PageList<SysUser>) sysUserService.getDistinctUserByOrgPath(queryFilter);
				}
				list = (PageList<SysUser>)sysUserService.getOnlineUser(list);
				//按组织
			} else if (SystemConst.SEARCH_BY_ORG.equals(searchBy)) {
				if (includSub == 0) {
					list = (PageList<SysUser>)sysUserService.getDistinctUserByOrgId(queryFilter);
				} else {
					list = (PageList<SysUser>)sysUserService.getDistinctUserByOrgPath(queryFilter);
				}
				//按岗位
			} else if (SystemConst.SEARCH_BY_POS.equals(searchBy)) {
				if (includSub == 0) {
					list = (PageList<SysUser>)sysUserService.getDistinctUserByPosId(queryFilter);
				} else {
					list = (PageList<SysUser>)sysUserService.getDistinctUserByPosPath(queryFilter);
				}
				//按角色
			} else if (SystemConst.SEARCH_BY_ROL.equals(searchBy)) {
				list =(PageList<SysUser>) sysUserService.getUserByRoleId(queryFilter);
			} else {
				SysOrg sysOrg = (SysOrg) ContextUtil.getCurrentOrg();
				if (StringUtil.isNotEmpty(type) && !"all".equals(typeVal) && BeanUtils.isNotEmpty(sysOrg)) {
					String path = orgServiceImpl.getSysOrgByScope(type, typeVal).getPath();
					queryFilter.addFilter("path", path + "%");
					list =(PageList<SysUser>) sysUserService.getDistinctUserByOrgPath(queryFilter);
				} else {
					list = (PageList<SysUser>)sysUserService.getUserByQuery(queryFilter);
				}

			}
			StringBuffer orgNames = null;
			StringBuffer path = null;
			//循环用户
			for (SysUser user : list) {
				//获取某用户的组织列表字符串（可能多个组织）
				orgNames = new StringBuffer();
				path = new StringBuffer();
				//获取每个用户的所有组织，组织不重复
				List<UserPosition> userPositionList = userPositionService.getByUserId(user.getUserId());
				if(BeanUtils.isNotEmpty(userPositionList)){
					//循环组织，组织名拼接
					for(UserPosition userPosition:userPositionList){
						if(userPosition.getIsPrimary()!=null&&userPosition.getIsPrimary()==UserPosition.PRIMARY_YES){
							orgNames.append(userPosition.getOrgName()).append("(主) ");
							path.append(userPosition.getOrgPathName()).append("(主) ");
						}else{
							orgNames.append(userPosition.getOrgName()).append("  ");
							path.append(userPosition.getOrgPathName()).append("  ");
						}
					}
				}
				user.setOrgName(orgNames.toString());
				user.setPath(path.toString());
			}
			ListModel model = CommonUtil.getListModel(list);
			json.put("userList",model);
			json.put("isSingle", isSingle);
			json.put("type", type);
			json.put("typeVal", typeVal);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"用户接口调用成功",json);
		}catch (Exception e){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"用户接口调用失败",e.getMessage());
		}
	}
	/**
	 * 更改查询字段(如：某某某->%某%某%某%)
	 * @param param
	 * @return
	 */
	public String changceFilter(String param){
		String paramNew = "";
		String[] arr = new String[param.length()];
		for(int i = 0; i < param.length(); i++) {
			arr[i] = param.substring(i, i + 1);
			if (i < param.length()) {
				paramNew += "%" + arr[i];
			}
		}
		return paramNew+"%";
	}

	/**
	 * 根据行政等级获取用户信息
	 * @param code 企业编码
	 * @param level 用户行政等级(高层=10、中层=30、基层=50)
	 * @return
	 */
	@RequestMapping("getUserByLevel")
    @ResponseBody
	public ResultVo getUserByLevel(HttpServletRequest request,String code,Integer level){
		ResultVo resultVo=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		if (StringUtil.isEmpty(code)){
			resultVo.setMessage("企业编码不允许为空！");
			return resultVo;
		}
		if (level==null){
			resultVo.setMessage("行政等级不能为空！");
			return resultVo;
		}
		QueryFilter filter=new QueryFilter(request,false);
		filter.addFilter("code",code);
		processUserLevel(filter,level);
		List<SysUser> userList=sysUserExtService.getListByLevel(filter);
		resultVo.setData(userList);
		resultVo.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
		resultVo.setMessage("获取数据成功");
		return resultVo;
	}
	
	/** 
	 * 同步用户的企业关联关系
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("syncUserEnterpriseRelation")
	@ResponseBody
	public ResultVo syncUserEnterpriseRelation(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long userId = RequestUtil.getLong(request, "userId");
		if(userId == 0){
			logger.error("同步用户企业关系失败：用户ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "同步用户企业关系失败！");
		}
		
		// 获取用户信息
		SysUser user = sysUserExtService.getById(userId);
		if(user == null){
			logger.error("同步用户企业关系失败：用户ID为【" + userId + "】的用户信息不存在");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "同步用户企业关系失败！");
		}
		// 获取用户当前的用户岗位关系
		Map<String, String> enterpriseCodes = enterpriseinfoService.getCodeMapByUserId(userId);
		
		try {
			// 获取已删除的用户岗位关系
			List<UserEnterprise> list = userEnterpriseService.getDelByUserId(userId);
			Set<String> delCode = new HashSet<String>();
			for(UserEnterprise userEnterprise : list){
				if(!enterpriseCodes.keySet().contains(userEnterprise.getEnterpriseCode())){
					delCode.add(userEnterprise.getEnterpriseCode());
				}
			}
			
			// 删除不存在的用户企业关系
			if(delCode.size() != 0){
				sysUserExtService.delUserOrgFromUserCenter(user, delCode);	
			}
		} catch (Exception e) {
			// 删除用户企业关系失败，记录失败日志
			userSynclogService.save(userId, UserSynclog.OPTYPE_DEL_USER_ORG, e.getMessage(), user.toString());
			logger.error("同步用户企业关系失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "同步用户企业关系失败！");
		}
		
		try {
			// 更新用户岗位关系
			sysUserExtService.updateToUserCenter(user, enterpriseCodes);
		} catch (Exception e) {
			// 删除用户企业关系失败，记录失败日志
			userSynclogService.save(userId, UserSynclog.OPTYPE_UPD_USER_ORG, e.getMessage(), user.toString());
			logger.error("同步用户企业关系失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "同步用户企业关系失败！");
		}
		
		// 删除用户同步日志
		userSynclogService.deleteByUserId(userId);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "同步用户企业关系成功！");
			
	}
	
	/** 
	 * 根据手机号/邮箱/账号查询用户信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("searchDetails")
	@ResponseBody
	public ResultVo searchDetails(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String mobile = RequestUtil.getString(request, "mobile");
		String email = RequestUtil.getString(request, "email");
		String account = RequestUtil.getString(request, "account");
		String message = "";
		if(StringUtils.isBlank(mobile) && StringUtils.isBlank(email) && StringUtils.isBlank(account)){
			message = "查询用户信息失败：查询条件为空！";
			logger.error(message);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message);
		}
		SysUser sysUser = null;
		try {
			// 根据手机号查询用户信息
			if(StringUtils.isNotBlank(mobile)){
				sysUser = sysUserService.getByMobile(mobile);
				if(sysUser == null){
					message = "查询用户信息失败：手机号【" + mobile + "】在系统中不存在！";
					logger.error(message);
					return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message);
				}
			}
			// 根据邮箱查询用户信息
			if(StringUtils.isNotBlank(email)){
				sysUser = sysUserService.getByMail(email);
				if(sysUser == null){
					message = "查询用户信息失败：邮箱【" + email + "】在系统中不存在！";
					logger.error(message);
					return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message);
				}
			}
			
			// 根据账号查询
			if(StringUtils.isNotBlank(account)){
				sysUser = sysUserService.getByAccount(account);
				if(sysUser == null){
					message = "查询用户信息失败：账号【" + account + "】在系统中不存在！";
					logger.error(message);
					return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message);
				}
			}
			
			Long userId = sysUser.getUserId();
			// 获取用户角色
			List<UserRole> roleList = userRoleService.getByUserId(userId);
			// 获取用户所属岗位
			List<UserPosition> posList = userPositionService.getByUserId(userId);
			// 获取用户上级
			List<SysUser> userUnders = userUnderService.getMyLeaders(userId);
			for(SysUser user : userUnders){
				user.setFullname(user.getAliasName());
			}
			// 获取用户关联企业信息
			List<Enterpriseinfo> userEnterprises = enterpriseinfoService.getByUserId(userId);
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("user", sysUser);
			result.put("userRoles", roleList);
			result.put("userPositions", posList);
			result.put("userUnders", userUnders);
			result.put("userEnterprises", userEnterprises);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "查询用户信息成功！", result);
		} catch (Exception e) {
			message = "查询用户信息失败：" + e.getMessage();
			logger.error(message, e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message);
		}
	}

	/**
	 * 处理用户行政等级
	 * 高层=10、中层=30、基层=50
	 * @param filter
	 */
	private void processUserLevel(QueryFilter filter,Integer level){
		filter.addFilter("grade",level);
	}
}
