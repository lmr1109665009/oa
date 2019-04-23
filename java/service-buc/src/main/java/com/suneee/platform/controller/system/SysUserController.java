package com.suneee.platform.controller.system;


import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.encrypt.EncryptUtil;
import com.suneee.core.ldap.model.LdapUser;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.model.OnlineUser;
import com.suneee.core.page.PageList;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.event.def.EventUtil;
import com.suneee.platform.event.def.UserEvent;
import com.suneee.platform.model.system.*;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.ldap.LdapUserService;
import com.suneee.platform.service.ldap.SysOrgSyncService;
import com.suneee.platform.service.ldap.SysUserSyncService;
import com.suneee.platform.service.ldap.UserHelper;
import com.suneee.platform.service.system.*;
import com.suneee.platform.service.system.impl.OrgServiceImpl;
import com.suneee.platform.web.listener.UserSessionListener;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.encrypt.EncryptUtil;
import com.suneee.core.ldap.model.LdapUser;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.model.OnlineUser;
import com.suneee.core.page.PageList;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.ucp.base.service.system.SysUserExtService;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import net.sf.json.JSONObject;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


/**
 * 对象功能:用户表 控制器类 开发公司:广州宏天软件有限公司 开发人员:csx 创建时间:2011-11-28 10:17:09
 */
@Controller
@RequestMapping("/platform/system/sysUser/")
@Action(ownermodel = SysAuditModelType.USER_MANAGEMENT)
public class SysUserController extends BaseController {
	@Resource
	private SysRoleService sysRoleService;
	@Resource
	private SysOrgService sysOrgService;
	@Resource
	private SysUserService sysUserService;
	@Resource
	private DemensionService demensionService;
	@Resource
	private SubSystemService subSystemService;
	@Resource
	private SysUserParamService sysUserParamService;
	@Resource
	private SysUserOrgService sysUserOrgService;
	@Resource
	private UserRoleService userRoleService;
	@Resource
	private UserPositionService userPositionService;
	@Resource
	private LdapUserService ldapUserService;
	@Resource
	private SysOrgSyncService sysOrgSyncService;
	@Resource
	private SysUserSyncService sysUserSyncService;
	@Resource
	private UserSyncService userSyncService;
	@Resource
	private OrgAuthService orgAuthService;
	@Resource
	private OrgServiceImpl orgServiceImpl;
	@Resource
	private UserUnderService userUnderService;
	@Resource
	private PwdStrategyService pwdStrategyService;
	@Resource
	private DictionaryService dictionaryService;
	@Resource
	private SysUserExtService sysUserExtService;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;
	private final String defaultUserImage = "commons/image/default_image_male.jpg";

	/**
	 * 取得用户表分页列表
	 *
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看用户表分页列表", execOrder = ActionExecOrder.AFTER, detail = "查看用户表分页列表", exectype = "管理日志")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long userId = RequestUtil.getLong(request, "userId");
		boolean isSupportWeixin = PropertyUtil.getBooleanByAlias(SysPropertyConstants.WX_IS_SUPPORT,false);
		QueryFilter queryFilter = new QueryFilter(request, "sysUserItem");
		Map<String, Object> filter = queryFilter.getFilters();
		if(StringUtil.isNotEmpty((String) filter.get("fullname"))){
			String fullname = changceFilter((String) filter.get("fullname"));
			filter.put("fullname", fullname);
		}

		if(StringUtil.isNotEmpty((String) filter.get("aliasname"))){
			String aliasname = changceFilter((String) filter.get("aliasname"));
			filter.put("aliasname", aliasname);
		}
		if(StringUtil.isNotEmpty((String) filter.get("orderField"))&&filter.get("orderField").equals("syncToUc")){
			filter.put("orderField", "sync_to_uc");
		}
		List<SysUser> list = sysUserService.getUsersByQuery(queryFilter);

		ModelAndView mv = this.getAutoView()
				.addObject("sysUserList", list)
				.addObject("userId", userId)
				.addObject("isSupportWeixin",isSupportWeixin);
		return mv;
	}


	/**
	 * 取得用户表分页列表
	 *
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("listJson")
	@ResponseBody
	@Action(description = "查看用户表分页列表", execOrder = ActionExecOrder.AFTER, detail = "查看用户表分页列表", exectype = "管理日志")
	public ResultVo listJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long userId = RequestUtil.getLong(request, "userId");
        Long orgId = RequestUtil.getLong(request, "orgId");

		QueryFilter queryFilter = new QueryFilter(request, true);
		Map<String, Object> filter = queryFilter.getFilters();

		if(StringUtil.isNotEmpty((String) filter.get("fullname"))){
			String fullname = changceFilter((String) filter.get("fullname"));
			filter.put("fullname", fullname);
		}

       if(StringUtil.isNotEmpty((String) filter.get("aliasname"))){
			String aliasname = changceFilter((String) filter.get("aliasname"));
			filter.put("aliasname", aliasname);
		}
		if(StringUtil.isNotEmpty((String) filter.get("orderField"))&&filter.get("orderField").equals("syncToUc")){
			filter.put("orderField", "sync_to_uc");
		}
		PageList<SysUser> list1= (PageList<SysUser>) sysUserService.getUsersByQueryFilter(queryFilter);
		/*ListModel model = CommonUtil.getListModel((PageList)list1);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("sysUserList1", model);*/
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "成功", PageUtil.getPageVo(list1));

	}


	/**
	 * 更改查询字段(如：某某某->某%某%某)
	 * @param param
	 * @return
	 */
   public String changceFilter(String param){
	   String paramNew = "";
	   String[] arr = new String[param.length()];
	   for(int i = 1; i < param.length(); i++){
			 arr[i] = param.substring(i, i+1);
			
			 if(i>0&&i<param.length()-1){
				 paramNew +="%"+arr[i];
			 }else{
				 paramNew+=arr[i];
			 }
		 }		
	   return paramNew;
   }
	/**
	 * 删除用户表
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除用户表", execOrder = ActionExecOrder.BEFORE, detail = "删除用户表" + "<#list StringUtils.split(userId,\",\") as item>" + "<#assign entity=sysUserService.getById(Long.valueOf(item))/>" + "【${entity.fullname}】" + "</#list>", exectype = "管理日志")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultMessage message = null;
		String preUrl = RequestUtil.getPrePage(request);
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "userId");
			delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除用户成功");
		} catch (Exception e) {
			message = new ResultMessage(ResultMessage.Fail, "删除用户失败:"+e.getMessage());
			e.printStackTrace();
		} 
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	private void delByIds(Long[] lAryId) {
		if (BeanUtils.isEmpty(lAryId))
			return;
		for (Long id : lAryId) {
			SysUser user = sysUserService.getById(id);
			// 获取用户企业编码
			Set<String> enterpriseCodes = enterpriseinfoService.getCodeSetByUserId(id);
			user.setUserEnterprises(enterpriseCodes.toArray(new String[enterpriseCodes.size()]));
			EventUtil.publishUserEvent(user, UserEvent.ACTION_DEL, true);
			try {
				sysUserService.delById(id);
			} catch (IOException e) {
				e.printStackTrace();
			}
			sysUserOrgService.delByUserId(id, UserPosition.DELFROM_USER_DEL);
			userPositionService.delByUserId(id, UserPosition.DELFROM_USER_DEL);
			userRoleService.delByUserId(id);
			orgAuthService.delByUserId(id);
		}
	}
	
	/**
	 * 删除用户表
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("syncUserToWx")
	@Action(description = "同步用户至微信")
	public void syncUserToWx(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultMessage message = null;
		try {
			
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "userId");
			sysUserService.syncUserToWx(lAryId);
			message = new ResultMessage(ResultMessage.Success, "同步用户成功");
		} catch (Exception e) {
			message = new ResultMessage(ResultMessage.Fail, "同步用户失败",e.getMessage());
			e.printStackTrace();
		}
		writeResultMessage(response.getWriter(), message);
	}

	@RequestMapping("edit")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		ModelAndView mv = getAutoView();
		mv.addObject("action", "global");
		List<Demension> demensionList = demensionService.getAll();
		Long userId = RequestUtil.getLong(request, "userId");
		//获取所属组织角色
	    Map<SysOrg, List<SysRole>> roles=getOrgRoles(userId);
		mv.addObject("sysOrgRoles", roles);
		mv.addObject("userId",userId);
		return getEditMv(request, mv).addObject("demensionList", demensionList);
	}

	/**
	 * 普通用户修改个人信息
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("editCommon")
	public ModelAndView editCommon(HttpServletRequest request) throws Exception {
		String returnUrl = RequestUtil.getPrePage(request);
		SysUser sysUser = sysUserService.getById(ContextUtil.getCurrentUserId());
		String pictureLoad = defaultUserImage;
		if (sysUser != null) {
			if (StringUtil.isNotEmpty(sysUser.getPicture())) {
				pictureLoad = sysUser.getPicture();
			}
		}
		return getAutoView().addObject("sysUser", sysUser).addObject("returnUrl", returnUrl).addObject("pictureLoad", pictureLoad);
	}

	@RequestMapping("editGrade")
	public ModelAndView editGrade(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		// 获取当前用户所能管理的组
		long userId = ContextUtil.getCurrentUserId();
		List<OrgAuth> orgAuthList = orgAuthService.getByUserId(userId);

		mv.setViewName("/platform/system/sysUserEdit");
		mv.addObject("action", "grade");
		mv.addObject("orgAuthList", orgAuthList);
		return getEditMv(request, mv);
	}

	public ModelAndView getEditMv(HttpServletRequest request, ModelAndView mv) {
		boolean isSupportWeixin = PropertyUtil.getBooleanByAlias(SysPropertyConstants.WX_IS_SUPPORT,false);
		String returnUrl = RequestUtil.getPrePage(request);
		Long userId = RequestUtil.getLong(request, "userId");
		int bySelf = RequestUtil.getInt(request, "bySelf");
		SysUser sysUser = null;
		if (userId != 0) {
			sysUser = sysUserService.getById(userId);
			List<UserRole> roleList = userRoleService.getByUserId(userId);
			List<UserPosition> userPosList = userPositionService.getByUserId(userId);
			List<SysUser> userUnders = userUnderService.getMyLeaders(userId);

			List<String> orgIdList = new ArrayList<String>();
			String orgIds = "";
			for (UserPosition up : userPosList) {
				orgIdList.add(up.getOrgId().toString());
			}
			orgIds = StringUtil.getArrayAsString(orgIdList);

			mv.addObject("roleList", roleList).addObject("userPosList", userPosList).addObject("orgIds", orgIds).addObject("userUnders", userUnders);
		} else {//新增用户界面
			sysUser = new SysUser();
			//密码策略的初始化密码
			sysUser.setPassword(pwdStrategyService.getUsingInitPwd());
		}
		String pictureLoad = defaultUserImage;
		if (sysUser != null) {
			if (StringUtil.isNotEmpty(sysUser.getPicture())) {
				pictureLoad = sysUser.getPicture();
			}
		}
		// 查询地区数据字典
		List<Dictionary> dicList = dictionaryService.getByNodeKey("dq");
		return mv.addObject("sysUser", sysUser).addObject("userId", userId)
				.addObject("returnUrl", returnUrl).addObject("pictureLoad", pictureLoad)
				.addObject("bySelf", bySelf).addObject("isSupportWeixin", isSupportWeixin)
				.addObject("dicList", dicList);
	}

	@RequestMapping("modifyPwdView")
	public ModelAndView modifyPwdView(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long userId = RequestUtil.getLong(request, "userId");
		SysUser sysUser = sysUserService.getById(userId);
		return getAutoView().addObject("sysUser", sysUser).addObject("userId", userId);
	}

	@RequestMapping("modifyPwd")
	@Action(description = "修改密码", execOrder = ActionExecOrder.AFTER, detail = "<#assign entity=sysUserService.getById(Long.valueOf(userId))/>" + "【${entity.fullname}】修改密码<#if isSuccess> 成功<#else>失败</#if>", exectype = "管理日志")
	public void modifyPwd(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		String primitivePassword = RequestUtil.getString(request, "primitivePassword");
		String enPassword = EncryptUtil.encrypt32MD5(primitivePassword);
		String newPassword = RequestUtil.getString(request, "newPassword");
		Long userId = RequestUtil.getLong(request, "userId");
		SysUser sysUser = sysUserService.getById(userId);
		String password = sysUser.getPassword();
		boolean issuccess = false;
		if (StringUtil.isEmpty(newPassword) || StringUtil.isEmpty(primitivePassword)) {
			writeResultMessage(response.getWriter(), "输入的密码不能为空", ResultMessage.Fail);
		} else if (!enPassword.equals(password)) {
			writeResultMessage(response.getWriter(), "你输入的原始密码不正确", ResultMessage.Fail);
		} else if (primitivePassword.equals(newPassword)) {
			writeResultMessage(response.getWriter(), "你修改的密码和原始密码相同", ResultMessage.Fail);
		} else {
			try {
				sysUserExtService.updPwd(sysUser, newPassword);
				writeResultMessage(response.getWriter(), "修改密码成功", ResultMessage.Success);
				issuccess = true;
			} catch (Exception ex) {
				logger.error("修改密码失败：" + ex.getMessage(), ex);
				String str = MessageUtil.getMessage();
				if(StringUtil.isEmpty(str)){
					str = ex.getMessage();
				}
				ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, "修改密码失败:" + str);
				response.getWriter().print(resultMessage);
//				if (StringUtil.isNotEmpty(str)) {
//					ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, "修改密码失败:" + str);
//					response.getWriter().print(resultMessage);
//				} else {
//					String message = ExceptionUtil.getExceptionMessage(ex);
//					ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, message);
//					response.getWriter().print(resultMessage);
//				}
			}
		}
		SysAuditThreadLocalHolder.putParamerter("isSuccess", issuccess);
	}

	@RequestMapping("resetPwdView")
	public ModelAndView resetPwdView(HttpServletRequest request) throws Exception {
		String returnUrl = RequestUtil.getPrePage(request);
		Long userId = RequestUtil.getLong(request, "userId");
		if (userId == 0) {
			userId = ContextUtil.getCurrentUserId();
		}
		SysUser sysUser = sysUserService.getById(userId);
		return getAutoView().addObject("sysUser", sysUser).addObject("userId", userId).addObject("returnUrl", returnUrl);
	}

	@RequestMapping("resetPwd")
	@ResponseBody
	@Action(description = "重置密码", execOrder = ActionExecOrder.AFTER, detail = "<#assign entity=sysUserService.getById(Long.valueOf(userId))/>" + "【${entity.fullname}】重置密码<#if isSuccess> 成功<#else>失败</#if>", exectype = "管理日志")
	public ResultVo resetPwd(HttpServletRequest request) throws Exception {
		String password = RequestUtil.getString(request, "password");
		Long userId = RequestUtil.getLong(request, "userId");
		boolean issuccess = true;
		ResultVo result=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"重置密码成功!");
		try {
			//检验新密码是否通过策略
			SysUser sysUser = sysUserService.getById(userId);
			JSONObject json = pwdStrategyService.checkUser(sysUser, password);
			short status = Short.parseShort(json.getString("status"));
			if (status != PwdStrategy.Status.SUCCESS) {
				String msg = json.getString("msg");
				MessageUtil.addMsg(msg);
				throw new Exception(msg);
			}
			sysUserExtService.updPwd(sysUser, password);
		} catch (Exception ex) {
			logger.error("重置密码失败：" + ex.getMessage(), ex);
			String str = MessageUtil.getMessage();
			if(StringUtil.isEmpty(str)){
				str = ex.getMessage();
			}
			result.setStatus(ResultVo.COMMON_STATUS_FAILED);
			result.setMessage("重置密码失败:" + str);
//			if (StringUtil.isNotEmpty(str)) {
//				ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, "重置密码失败:" + str);
//				response.getWriter().print(resultMessage);
//			} else {
//				String message = ExceptionUtil.getExceptionMessage(ex);
//				ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, message);
//				response.getWriter().print(resultMessage);
//			}
			issuccess = false;
		}
		SysAuditThreadLocalHolder.putParamerter("isSuccess", issuccess);
		return result;
	}

	@RequestMapping("commonResetPwd")
	@Action(description = "重置密码", execOrder = ActionExecOrder.AFTER, detail = "<#assign entity=sysUserService.getByAccount(Long.valueOf(userId))/>" + "【${entity.fullname}】重置密码<#if isSuccess> 成功<#else>失败</#if>", exectype = "管理日志")
	public void commonResetPwd(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject result = new JSONObject();

		boolean issuccess = true;
		String account = RequestUtil.getString(request, "account");
		String oldPassword = RequestUtil.getString(request, "oldPassword");
		String newPassword = RequestUtil.getString(request, "newPassword");
		SysUser sysUser = sysUserService.getByAccount(account);
		if (sysUser == null) {
			result.put("msg", "账号不存在");
			result.put("state", "-1");
			issuccess = false;
		} else if (!sysUser.getPassword().equals(EncryptUtil.encrypt32MD5(oldPassword))) {
			result.put("msg", "旧密码错误");
			result.put("state", "-2");
			issuccess = false;
		} else {
			//先验证密码是否符合密码规则
			JSONObject json = pwdStrategyService.checkUser(sysUser, newPassword);
			short status = Short.parseShort(json.getString("status"));
			if (status != PwdStrategy.Status.SUCCESS) {
				String msg = json.getString("msg");
				result.put("msg", msg);
				result.put("state", "-3");
			} else {
				//更新密码
				sysUserService.updPwd(sysUser.getUserId(), newPassword);
				result.put("msg", "修改密码成功,跳转回登录页面");
				result.put("state", "0");
			}
		}
		response.getWriter().print(result);
		SysAuditThreadLocalHolder.putParamerter("isSuccess", issuccess);
	}

	@RequestMapping("editStatusView")
	public ModelAndView editStatusView(HttpServletRequest request) throws Exception {
		String returnUrl = RequestUtil.getPrePage(request);
		Long userId = RequestUtil.getLong(request, "userId");
		SysUser sysUser = sysUserService.getById(userId);
		return getAutoView().addObject("sysUser", sysUser).addObject("userId", userId).addObject("returnUrl", returnUrl);
	}

	@RequestMapping("editStatus")
	@Action(description = "设置用户状态", execOrder = ActionExecOrder.AFTER, detail = "设置用户状态", exectype = "管理日志")
	public void editStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long userId = RequestUtil.getLong(request, "userId");
		int isLock = RequestUtil.getInt(request, "isLock");
		int status = RequestUtil.getInt(request, "status");
		try {
			sysUserService.updStatus(userId, (short) status, (short) isLock);
			writeResultMessage(response.getWriter(), "修改用户状态成功!", ResultMessage.Success);
		} catch (Exception ex) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, "修改用户状态失败:" + str);
				response.getWriter().print(resultMessage);
			} else {
				String message = ExceptionUtil.getExceptionMessage(ex);
				ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, message);
				response.getWriter().print(resultMessage);
			}
		}
	}

//	/**
//	 * 取得用户表明细
//	 *
//	 * @param request
//	 * @param response
//	 * @return
//	 * @throws Exception
//	 */
	@RequestMapping("get")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = this.getAutoView();
		return getView(request, response, mv, 0);
}









	/**
	 * 取得用户表明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getByUserId")
	public ModelAndView getByUserId(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("/platform/system/sysUserGet.jsp");
		mv = getView(request, response, mv, 1);
		return mv;
	}

	/**
	 * 取得用户表明细
	 * 
	 * @param request
	 * @param response
	 * @param isOtherLink
	 * @return
	 * @throws Exception
	 */
	@Action(description = "查看用户表明细", execOrder = ActionExecOrder.AFTER, detail = "查看用户表明细", exectype = "管理日志")
	public ModelAndView getView(HttpServletRequest request, HttpServletResponse response, ModelAndView mv, int isOtherLink) throws Exception {
		long userId = RequestUtil.getLong(request, "userId");
		long canReturn = RequestUtil.getLong(request, "canReturn", 0);
		//查看的类型
		String openType = RequestUtil.getString(request, "openType", "");
		//获取用户基本信息
		SysUser sysUser = sysUserService.getById(userId);
		String pictureLoad = defaultUserImage;
		if (sysUser != null) {
			if (StringUtil.isNotEmpty(sysUser.getPicture())) {
				pictureLoad = sysUser.getPicture();
			}
		}
		//获取所属组织角色
		Map<SysOrg, List<SysRole>> roles=getOrgRoles(userId);
		//获取用户角色
		List<UserRole> roleList = userRoleService.getByUserId(userId);
		//获取用户所属岗位
		List<UserPosition> posList = userPositionService.getByUserId(userId);
		//获取所属组织
		List<UserPosition> userPosList = userPositionService.getOrgListByUserId(userId);
		//获取参数属性
		List<SysUserParam> userParamList = sysUserParamService.getByUserId(userId);
		String returnUrl = RequestUtil.getPrePage(request);
		return mv.addObject("sysUser", sysUser).addObject("roleList", roleList).addObject("posList", posList).addObject("orgList", userPosList)
				.addObject("pictureLoad", pictureLoad).addObject("userParamList", userParamList).addObject("canReturn", canReturn)
				.addObject("returnUrl", returnUrl).addObject("isOtherLink", isOtherLink).addObject("openType", openType).addObject("sysOrgRoles",roles);
	}

	/**
	 * 取得用户表分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("dialog")
	public ModelAndView dialog(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ModelAndView mv = this.getAutoView();

		List<Demension> demensionList = demensionService.getAll();
		mv.addObject("demensionList", demensionList);
		List<SubSystem> subSystemList = subSystemService.getAll();
		mv.addObject("subSystemList", subSystemList);

		String isSingle = RequestUtil.getString(request, "isSingle", "false");
		mv.addObject("isSingle", isSingle);
		handelUserSoruce(mv);
		return mv;
	}

	private void handelUserSoruce(ModelAndView mv) {
		boolean isShowPos = PropertyUtil.getBooleanByAlias("userDialog.showPos", true);
		boolean isShowRole = PropertyUtil.getBooleanByAlias("userDialog.showRole", true);
		boolean isShowOnlineUser = PropertyUtil.getBooleanByAlias("userDialog.showOnlineUser", true);

		mv.addObject("isShowPos", isShowPos);
		mv.addObject("isShowRole", isShowRole);
		mv.addObject("isShowOnlineUser", isShowOnlineUser);
	}

	/**
	 * 取得用户表分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("gradeDialog")
	public ModelAndView GradeDialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = this.getAutoView();

		long userId = ContextUtil.getCurrentUserId();
		List<OrgAuth> orgAuthList = orgAuthService.getByUserId(userId);
		mv.addObject("orgAuthList", orgAuthList);

		String isSingle = RequestUtil.getString(request, "isSingle", "false");
		mv.addObject("isSingle", isSingle);
		handelUserSoruce(mv);
		return mv;
	}

	@RequestMapping("flowDialog")
	public ModelAndView flowDialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = this.getAutoView();
		List<Demension> demensionList = demensionService.getAll();
		mv.addObject("demensionList", demensionList);
		List<SubSystem> subSystemList = subSystemService.getAll();

		mv.addObject("isSingle", "false");
		mv.addObject("subSystemList", subSystemList);

		handelUserSoruce(mv);
		return mv;
	}

	@RequestMapping("selector")
	@Action(description = "用户选择器", execOrder = ActionExecOrder.AFTER, detail = "用户选择器", exectype = "管理日志")
	public ModelAndView selector(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<SysUser> list = null;
		ModelAndView result = getAutoView();
		String searchBy = RequestUtil.getString(request, "searchBy");
		String type = RequestUtil.getString(request, "type");
		String typeVal = RequestUtil.getString(request, "typeVal");
		int includSub = RequestUtil.getInt(request, "includSub", 0);
		String isSingle = RequestUtil.getString(request, "isSingle", "false");
		QueryFilter queryFilter = new QueryFilter(request, "sysUserItem");		
		//姓名字号条件筛选时修改姓名样式		 		
		Map<String, Object> filter = queryFilter.getFilters();
		if(StringUtil.isNotEmpty((String) filter.get("fullname"))){
			String fullname = changceFilter((String) filter.get("fullname"));
			filter.put("fullname", fullname);
		}
		if(StringUtil.isNotEmpty((String) filter.get("aliasname"))){
			String aliasname = changceFilter((String) filter.get("aliasname"));
			filter.put("aliasname", aliasname);
		}
		if (SystemConst.SEARCH_BY_ONL.equals(searchBy)) {
			String demId = RequestUtil.getString(request, "path");
			if (demId.equals("-1")) {//未分配组织的或者没有主组织的用户
				list = sysUserService.getUserNoOrg(queryFilter);
			} else {
				queryFilter.addFilter("isPrimary", 1);
				list = sysUserService.getDistinctUserByOrgPath(queryFilter);
			}
			list = sysUserService.getOnlineUser(list);
			//按组织
		} else if (SystemConst.SEARCH_BY_ORG.equals(searchBy)) {
			if (includSub == 0) {
				list = sysUserService.getDistinctUserByOrgId(queryFilter);
			} else {
				list = sysUserService.getDistinctUserByOrgPath(queryFilter);
			}
			//按岗位
		} else if (SystemConst.SEARCH_BY_POS.equals(searchBy)) {
			if (includSub == 0) {
				list = sysUserService.getDistinctUserByPosId(queryFilter);
			} else {
				list = sysUserService.getDistinctUserByPosPath(queryFilter);
			}
			//按角色
		} else if (SystemConst.SEARCH_BY_ROL.equals(searchBy)) {
			list = sysUserService.getUserByRoleId(queryFilter);
		} else {
			SysOrg sysOrg = (SysOrg) ContextUtil.getCurrentOrg();
			if (StringUtil.isNotEmpty(type) && !"all".equals(typeVal) && BeanUtils.isNotEmpty(sysOrg)) {
				String path = orgServiceImpl.getSysOrgByScope(type, typeVal).getPath();
				queryFilter.addFilter("path", path + "%");
				list = sysUserService.getDistinctUserByOrgPath(queryFilter);
			} else {
				list = sysUserService.getUserByQuery(queryFilter);
			}
		}
		List<SysUser> userList = new ArrayList<SysUser>();
		String orgNames = "";
		//循环用户
		for (SysUser user : list) {
			//获取某用户的组织列表字符串（可能多个组织）
			orgNames = userPositionService.getOrgnamesByUserId(user.getUserId());
			user.setOrgName(orgNames.toString());
			userList.add(user);
		}

		result.addObject("sysUserList", userList);
		result.addObject("isSingle", isSingle);
		result.addObject("type", type);
		result.addObject("typeVal", typeVal);

		return result;
	}

	/**
	 * 获取在线用户树
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getTreeData")
	@ResponseBody
	@Action(description = "用户选择器查询", execOrder = ActionExecOrder.AFTER, detail = "用户选择器查询", exectype = "管理日志")
	public List<OnlineUser> getTreeData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<Long, OnlineUser> onlineUsers = UserSessionListener.getOnLineUsers();
		List<OnlineUser> onlineList = new ArrayList<OnlineUser>();
		for (Long sessionId : onlineUsers.keySet()) {
			OnlineUser onlineUser = new OnlineUser();
			onlineUser = onlineUsers.get(sessionId);
			onlineList.add(onlineUser);
		}
		return onlineList;
	}

	/**
	 * 获取系统中，来自Ad的用户数据。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("syncList")
	@Action(description = "用户选择器显示", execOrder = ActionExecOrder.AFTER, detail = "用户选择器显示", exectype = "管理日志")
	public ModelAndView ldapUserList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean connectable = isLdapConnnectable();
		ModelAndView mv = getAutoView();
		mv.addObject("connectable", connectable);
		if (!connectable) {
			return mv;
		}
		QueryFilter queryFilter = new QueryFilter(request, "sysUserMapItem");
		queryFilter.addFilter("fromType", SystemConst.FROMTYPE_AD);
		List<SysUser> sysUserList = sysUserService.getUserByQuery(queryFilter);
		//		queryFilter.addFilter("fromType", SysUser.FROMTYPE_AD_SET);
		//		sysUserList.addAll(sysUserService.getUserByQuery(queryFilter));

		List<LdapUser> ldapUserList;

		ldapUserList = ldapUserService.getAll();
		if (ldapUserList == null) {
			ldapUserList = new ArrayList<LdapUser>();
		}
		List<Map<String, Object>> userMapList = new ArrayList<Map<String, Object>>();
		for (SysUser sysUser : sysUserList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("sysUser", sysUser);
			LdapUser ldapUser = UserHelper.getEqualUserByAccount(sysUser, ldapUserList);
			if (ldapUser != null) {//AD未删除
				boolean sync = UserHelper.isSysLdapUsrEqualIngoreOrg(sysUser, ldapUser);
				if (sync) {
					map.put("sync", 0);//DB与AD同步
				} else {
					map.put("sync", 1);//DB与AD不同步
				}
			} else {//AD已删除
				map.put("sync", -1);
			}
			userMapList.add(map);
		}
		mv.addObject("sysUserMapList", userMapList);
		return mv;
	}

	/**
	 * 将指定的系统用户在AD中数据同步到系统中，用户在Ad中数据将覆盖用户在系统中的数据。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("syncLdap")
	@Action(description = "用户选择器显示", execOrder = ActionExecOrder.AFTER, detail = "用户选择器显示", exectype = "管理日志")
	public void syncLdap(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultMessage message = null;
		String preUrl = RequestUtil.getPrePage(request);
		Long userId = RequestUtil.getLong(request, "userId");
		SysUser sysUser = sysUserService.getById(userId);
		if (sysUser == null) {
			message = new ResultMessage(ResultMessage.Fail, "在数据库中未找到用户数据！");
			addMessage(message, request);
			response.sendRedirect(preUrl);
			return;
		}
		String account = sysUser.getAccount();
		LdapUser ldapUser = ldapUserService.getByAccount(account);
		if (ldapUser == null) {
			message = new ResultMessage(ResultMessage.Fail, "在AD中未找到用户数据！");
			addMessage(message, request);
			response.sendRedirect(preUrl);
			return;
		}
		if (!UserHelper.isSysLdapUsrEqualIngoreOrg(sysUser, ldapUser)) {
			sysUser.setAccount(ldapUser.getsAMAccountName());
			sysUser.setEmail(ldapUser.getMail());
			String givenName = ldapUser.getGivenName();
			String sn = ldapUser.getSn();
			String fullname = (sn == null ? "" : sn) + (givenName == null ? "" : givenName);
			fullname = fullname.equals("") ? null : fullname;
			sysUser.setFullname(fullname);
			sysUser.setPhone(ldapUser.getHomePhone());
			sysUser.setMobile(ldapUser.getTelephoneNumber());
			sysUser.setStatus(ldapUser.isAccountDisable() ? SystemConst.STATUS_NO : SystemConst.STATUS_OK);
		}
		sysUserService.update(sysUser);
		message = new ResultMessage(ResultMessage.Success, "用户数据与AD同步成功！");
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	/**
	 * 查看用户在系统与Ad中的数据差异
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getLdapComp")
	@Action(description = "查看用户在系统与Ad中的数据差异", execOrder = ActionExecOrder.AFTER, detail = "查看用户在系统与Ad中的数据差异", exectype = "管理日志")
	public ModelAndView getLdapComp(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long userId = RequestUtil.getLong(request, "userId");
		SysUser sysUser = sysUserService.getById(userId);
		String account = sysUser.getAccount();
		LdapUser ldapUser = ldapUserService.getByAccount(account);
		ModelAndView mv = getAutoView();
		mv.addObject("sysUser", sysUser);
		mv.addObject("ldapUser", ldapUser);
		return mv;
	}

	@RequestMapping("syncToLdap")
	@Action(description = "查看用户同步", execOrder = ActionExecOrder.AFTER, detail = "查看用户同步", exectype = "管理日志")
	public ModelAndView syncToLdap(HttpServletRequest request, HttpServletResponse response) throws Exception {
		sysUserSyncService.reset();
		sysOrgSyncService.syncLodapToDb();
		int syncNum = sysUserSyncService.syncLodapToDb();
		ModelAndView mv = getAutoView();
		mv.addObject("syncNum", syncNum);
		mv.addObject("lastSyncTakeTime", sysUserSyncService.getLastSyncTakeTime());
		mv.addObject("lastSyncTime", sysUserSyncService.getLastSyncTime());
		mv.addObject("newFromLdapUserList", sysUserSyncService.getNewFromLdapUserList());
		mv.addObject("deleteLocalUserList", sysUserSyncService.getDeleteLocalUserList());
		mv.addObject("updateLocalUserList", sysUserSyncService.getUpdateLocalUserList());
		return mv;
	}

	public boolean isLdapConnnectable() {
		try {
			LdapTemplate ldapTemplate = (LdapTemplate) AppUtil.getBean(LdapTemplate.class);
			if (ldapTemplate == null) {
				return false;
			} else {
				DistinguishedName base = new DistinguishedName();
				ldapTemplate.list(base);
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 同步AD用户。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("syncUser")
	public void syncUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultMessage message = new ResultMessage(ResultMessage.Success, "用户数据与AD同步成功！");
		PrintWriter writer = response.getWriter();
		boolean isAvaible = isLdapConnnectable();
		if (!isAvaible) {
			message = new ResultMessage(ResultMessage.Fail, "活动目录连接失败,请检查配置是否正确！");
		} else {
			try {
				userSyncService.syncUsers();
			} catch (Exception ex) {
				String msg = ExceptionUtil.getExceptionMessage(ex);
				message = new ResultMessage(ResultMessage.Fail, msg);
			}
		}
		writer.print(message);
	}

	@RequestMapping("getUserListByJobId")
	@ResponseBody
	@Action(description = "根据职务ID取得用户List")
	public List<SysUser> getUserListByJobId(HttpServletRequest request) throws Exception {
		Long jobId = RequestUtil.getLong(request, "jobId");
		List<SysUser> list = sysUserService.getUserListByJobId(jobId);
		return list;
	}

	@RequestMapping("getUserListByPosId")
	@ResponseBody
	@Action(description = "根据岗位ID取得用户List")
	public List<SysUser> getUserListByPosId(HttpServletRequest request) throws Exception {
		Long posId = RequestUtil.getLong(request, "posId");
		List<SysUser> list = sysUserService.getUserListByPosId(posId);
		return list;
	}
	
	/**
	 * 
	 * 获取所属组织角色
	 * @param userId
	 * @return 
	 * Map&lt;SysOrg,List&lt;SysRole>>
	 */
	public Map<SysOrg, List<SysRole>> getOrgRoles(Long userId) {
		List<SysOrg> sysOrgs=sysOrgService.getOrgsByUserId(userId);
		Map<SysOrg, List<SysRole>> roles=new HashMap<SysOrg, List<SysRole>>();
		if(BeanUtils.isNotEmpty(sysOrgs)){
			for(SysOrg sysOrg:sysOrgs){
				Long orgId=sysOrg.getOrgId();
				List<String> roleList=sysRoleService.getOrgRoles(orgId);
				List<SysRole> sysRoles=new ArrayList<SysRole>();
				for(String role:roleList){
					SysRole sysRole=sysRoleService.getByRoleAlias(role);
					sysRoles.add(sysRole);
					roles.put(sysOrg, sysRoles);
				}
			}
		}
		return roles;
	}




}
