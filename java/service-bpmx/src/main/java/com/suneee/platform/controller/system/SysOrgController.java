package com.suneee.platform.controller.system;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.suneee.platform.model.system.*;
import com.suneee.platform.service.system.*;
import com.suneee.core.model.OnlineUser;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.core.model.OnlineUser;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.impl.OrgServiceImpl;
import com.suneee.platform.web.listener.UserSessionListener;

/**
 * 对象功能:组织架构SYS_ORG 控制器类 开发公司:广州宏天软件有限公司 开发人员:pkq 创建时间:2011-11-09 11:20:13
 */
@Controller
@RequestMapping("/platform/system/sysOrg/")
@Action(ownermodel = SysAuditModelType.USER_MANAGEMENT)
public class SysOrgController extends BaseController {
	@Resource
	private SysOrgService sysOrgService;
	@Resource
	private DemensionService demensionService;
	@Resource
	Properties configproperties;
	@Resource
	private SysUserOrgService sysUserOrgService;

	@Resource
	private SysOrgTypeService sysOrgTypeService;

	@Resource
	private SysOrgParamService sysOrgParamService;

	@Resource
	private OrgServiceImpl orgServiceImpl;
	@Resource
	private UserPositionService userPositionService;

	/**
	 * 取得维度下拉
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getDem")
	@ResponseBody
	public List<Demension> getDem(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return demensionService.getAll();
	}

	/**
	 * 组织对话框的展示
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("selector")
	@Action(description = "组织对话框的展示", execOrder = ActionExecOrder.AFTER, detail = "组织对话框的展示", exectype = "管理日志")
	public ModelAndView selector(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "sysOrgItem");
		Long orgId = RequestUtil.getLong(request, "orgId");
		Long demId = RequestUtil.getLong(request, "demId");
		String orgName = RequestUtil.getString(request, "orgName");
		String type = RequestUtil.getString(request, "type");
		String typeVal = RequestUtil.getString(request, "typeVal");

		if (StringUtil.isNotEmpty(orgName)) {
			filter.addFilter("orgName", "%" + orgName + "%");
		}
		if (demId != 0) {
			filter.addFilter("demId", demId);
		}
		if (StringUtil.isNotEmpty(type)) {
			SysOrg sysOrg = orgServiceImpl.getSysOrgByScope(type, typeVal);
			filter.addFilter("path", StringUtil.isNotEmpty(sysOrg.getPath()) ? (sysOrg.getPath() + "%") : ("%."+sysOrg.getOrgId()+".%"));
		}
		SysOrg org = sysOrgService.getById(orgId);
		if (org != null) {
			filter.addFilter("path", StringUtil.isNotEmpty(org.getPath()) ? (org.getPath() + "%") : ("%."+org.getOrgId()+".%"));
		}
		List<SysOrg> sysOrgList = sysOrgService.getAll(filter);

		String isSingle = RequestUtil.getString(request, "isSingle", "false");
		ModelAndView mv = this.getAutoView()
				.addObject("sysOrgList", sysOrgList)
				.addObject("type", type)
				.addObject("typeVal", typeVal)
				.addObject("isSingle", isSingle);
		return mv;
	}

	@RequestMapping("dialog")
	@Action(description = "组织对话框", execOrder = ActionExecOrder.AFTER, detail = "组织对话框", exectype = "管理日志")
	public ModelAndView dialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Demension> demensionList = demensionService.getAll();
		ModelAndView mv = this.getAutoView().addObject("demensionList", demensionList);

		return mv;
	}

	@RequestMapping("dialogComp")
	@Action(description = "公司对话框", execOrder = ActionExecOrder.AFTER, detail = "组织对话框", exectype = "管理日志")
	public ModelAndView dialogComp(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Demension> demensionList = demensionService.getAll();
		ModelAndView mv = this.getAutoView().addObject("demensionList", demensionList);

		return mv;
	}
	/**
	 * 取得组织框架信息
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "组织信息列表", execOrder = ActionExecOrder.AFTER, detail = "组织信息列表", exectype = "管理日志")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Demension> demensionList = demensionService.getAll();
		ModelAndView mv = this.getAutoView().addObject("demensionList", demensionList);

		return mv;
	}

	/**
	 * 组织tree选择器
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("supTree")
	@Action(description = "组织tree选择器", execOrder = ActionExecOrder.AFTER, detail = "组织tree选择器", exectype = "管理日志")
	public ModelAndView supTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Demension> demensionList = demensionService.getAll();
		ModelAndView mv = this.getAutoView().addObject("demensionList", demensionList);

		return mv;
	}

	/**
	 * 查询所有组织架构
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("search")
	public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "sysOrgItem");
		filter.getPageBean().setPagesize(10);
		List<SysOrg> sysOrgList = sysOrgService.getAll(filter);
		ModelAndView mv = this.getAutoView().addObject("sysOrgList", sysOrgList);

		return mv;
	}

	/**
	 * 取得组织架构分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("view")
	@Action(description = "组织信息展示", execOrder = ActionExecOrder.AFTER, detail = "组织信息展示", exectype = "管理日志")
	public ModelAndView view(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long orgId = RequestUtil.getLong(request, "orgId");
		SysOrg sysOrg = sysOrgService.getById(orgId);
		String path = "";
		String paramPath = "";
		if (sysOrg != null) {
			path = sysOrg.getPath();
			paramPath = path.replace(".", ",");
		}
		ModelAndView mv = this.getAutoView().addObject("orgId", orgId).addObject("path", path).addObject("paramPath", paramPath);
		return mv;
	}

	/**
	 * 取得下级组织架构分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("listById")
	@Action(description = "组织信息查询", execOrder = ActionExecOrder.AFTER, detail = "组织信息查询", exectype = "管理日志")
	public ModelAndView listById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = this.getAutoView();
		mv.addObject("action", "global");
		return getListByOrgId(request, mv);
	}

	@RequestMapping("listGradeById")
	public ModelAndView listGradeById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.addObject("action", "grade");
		mv.setViewName("/platform/system/sysOrgListById");
		return getListByOrgId(request, mv);
	}

	private ModelAndView getListByOrgId(HttpServletRequest request, ModelAndView mv) throws Exception {
		Long orgId = RequestUtil.getLong(request, "orgId");
		SysOrg sysOrg = sysOrgService.getById(orgId);

		if (sysOrg == null)
			return mv.addObject("sysOrg", sysOrg);
		else {
			String path = sysOrg.getPath();
			QueryFilter filter = new QueryFilter(request, "sysOrgItem");
			filter.getFilters().put("path", StringUtil.isNotEmpty(path) ? (path + "%") : "");
			List<SysOrg> list = sysOrgService.getOrgByOrgId(filter);
			return mv.addObject("sysOrgList", list).addObject("orgId", orgId).addObject("sysOrg", 1);
		}
	}

	@RequestMapping("edit")
	@Action(description = "编辑组织架构", execOrder = ActionExecOrder.AFTER, detail = "编辑组织架构")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		ModelAndView mv = getAutoView();
		mv.addObject("scope", "global");
		return getEditMv(request, mv);
	}

	@RequestMapping("editGrade")
	@Action(description = "编辑组织架构", execOrder = ActionExecOrder.AFTER, detail = "编辑组织架构")
	public ModelAndView editGrade(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/platform/system/sysOrgEdit");
		mv.addObject("scope", "grade");
		return getEditMv(request, mv);
	}

	/**
	 * 获取编辑界面的modelandview
	 * 
	 * @param request
	 * @param mv
	 * @return
	 */
	private ModelAndView getEditMv(HttpServletRequest request, ModelAndView mv) {
		Long demId = RequestUtil.getLong(request, "demId", 0);
		Long orgId = RequestUtil.getLong(request, "orgId");
		String action = RequestUtil.getString(request, "action");
		SysOrg sysOrg = null;
		Long parentOrgId = 0L;
		String parentCode="";
		// 当前维度
		Demension demension = demensionService.getById(demId);
		List<SysOrgType> sysOrgTypelist = sysOrgTypeService.getByDemId(demId);
		List<SysOrgType> returnSysOrgTypelist = new LinkedList<SysOrgType>();
		SysOrgType subSysOrgType = null;
		if ("add".equals(action)) {// 新增
			sysOrg = new SysOrg();
			SysOrg supSysOrg = sysOrgService.getById(orgId);

			if (supSysOrg == null) { // 从维度上新建组织
				sysOrg.setOrgSupId(demId);
				returnSysOrgTypelist = sysOrgTypelist;
			} else {
				supSysOrg = sysOrgService.getById(orgId);
				parentCode=supSysOrg.getCode();
				sysOrg.setOrgSupId(supSysOrg.getOrgId());
				sysOrg.setOrgSupName(supSysOrg.getOrgName());
				sysOrg.setOrgCode(supSysOrg.getOrgCode());
				Long supSysOrgId = supSysOrg.getOrgType();
				if (supSysOrgId != null) {
					subSysOrgType = sysOrgTypeService.getById(supSysOrg.getOrgType());
				}
			}
		} else {// 编辑
			sysOrg = sysOrgService.getById(orgId);
			SysOrg charge = sysUserOrgService.getChageNameByOrgId(orgId);
			sysOrg.setOwnUser(charge.getOwnUser());
			sysOrg.setOwnUserName(charge.getOwnUserName());
			parentOrgId = sysOrg.getOrgSupId();

//			if (sysOrg.getOrgType() != null)
//				subSysOrgType = sysOrgTypeService.getById(sysOrg.getOrgType());
//
//			if (subSysOrgType == null) {

				SysOrg parentOrg = sysOrgService.getParentWithType(sysOrg); // 取得有分类的父极节点
				if (parentOrg != null)
					subSysOrgType = sysOrgTypeService.getById(parentOrg.getOrgType());
				else
					returnSysOrgTypelist = sysOrgTypelist;
//			}
		}
		if (subSysOrgType != null && !parentOrgId.equals(1L)) {
			for (int i = 0; i < sysOrgTypelist.size(); i++) {
				if (subSysOrgType.getLevels() <= sysOrgTypelist.get(i).getLevels())
					returnSysOrgTypelist.add(sysOrgTypelist.get(i));
			}
		} else if (parentOrgId.equals(1L)) {
			returnSysOrgTypelist = sysOrgTypelist;
		}

		return mv.addObject("sysOrg", sysOrg)
				.addObject("demension", demension)
				.addObject("action", action)
				.addObject("sysOrgTypelist", returnSysOrgTypelist)
				.addObject("parentCode", parentCode);
	}

	/**
	 * 删除组织及其所有子组织<br>
	 * 非物理删除
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("orgdel")
	@Action(description = "删除组织", execOrder = ActionExecOrder.BEFORE, detail = "删除组织" + "<#list StringUtils.split(orgId,\",\") as item>" + "<#assign entity=sysOrgService.getById(Long.valueOf(item))/>" + "【${entity.orgName}】" + "</#list>", exectype = "管理日志")
	public void orgdel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "orgId");
			sysOrgService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除组织及其所有子组织成功");
			
		} catch (Exception e) {
			message = new ResultMessage(ResultMessage.Fail, "删除组织及其所有子组织失败"+e.getMessage());
		}
		writeResultMessage(response.getWriter(), message);
	}

	/**
	 * 取得组织架构明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = this.getAutoView();
		mv.addObject("action", "global").addObject("isOtherLink", 0);
		return getByOrgId(request, mv);

	}
	
	/**
	 * 取得空组织架构明细，防止js冲突
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getEmpty")
	public ModelAndView getEmpty(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getAutoView();
	}

	@Action(description = "获取组织信息", execOrder = ActionExecOrder.AFTER, detail = "获取组织信息", exectype = "管理日志")
	private ModelAndView getByOrgId(HttpServletRequest request, ModelAndView mv) throws Exception {
		long orgId = RequestUtil.getLong(request, "orgId");
		List<SysOrgType> sysOrgTypelist = null;
		String ownerName = "";
		SysOrg sysOrg = sysOrgService.getById(orgId);
		if (sysOrg != null) {
			SysOrg charge = sysUserOrgService.getChageNameByOrgId(orgId);
			Long demId = sysOrg.getDemId();
			sysOrgTypelist = sysOrgTypeService.getByDemId(demId);
			if (sysOrg.getDemId() != 0) {
				sysOrg.setDemName(demensionService.getById(demId).getDemName());
				ownerName = charge.getOwnUserName();

			}
		}

		return mv.addObject("sysOrg", sysOrg).addObject("userNameCharge", ownerName).addObject("orgId", orgId).addObject("sysOrgTypelist", sysOrgTypelist);
	}

	@RequestMapping("getGrade")
	public ModelAndView getGrade(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.addObject("action", "grade").addObject("isOtherLink", 0);
		mv.setViewName("/platform/system/sysOrgGet");
		return getByOrgId(request, mv);
	}

	/**
	 * 取得组织架构明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getByLink")
	public ModelAndView getByLink(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/platform/system/sysOrgGet");
		mv.addObject("action", "grade").addObject("isOtherLink", 1);
		return getByOrgId(request, mv);

	}

	/**
	 * 取得组织架构明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("type")
	@Action(description = "获取组织类型", execOrder = ActionExecOrder.AFTER, detail = "获取组织类型", exectype = "管理日志")
	public ModelAndView type(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String flag = RequestUtil.getString(request, "flag");
		long orgId = RequestUtil.getLong(request, "orgId");
		String userNameStr = "";
		String userNameCharge = "";

		List<SysUserOrg> userlist = sysUserOrgService.getByOrgId(orgId);
		for (SysUserOrg userOrg : userlist) {
			if (userNameStr.isEmpty()) {
				userNameStr = userOrg.getUserName();
			} else {
				userNameStr = userNameStr + "," + userOrg.getUserName();
			}
			String isCharge = "";
			if (BeanUtils.isNotEmpty(userOrg.getIsCharge())) {
				isCharge = userOrg.getIsCharge().toString();
			}
			// 为主要负责人
			if (SysUserOrg.CHARRGE_YES.equals(isCharge)) {
				if (userNameCharge.isEmpty()) {
					userNameCharge = userOrg.getUserName().toString();
				} else {
					userNameCharge = userNameCharge + "," + userOrg.getUserName();
				}
			}
		}
		SysOrg po = sysOrgService.getById(orgId);
		return this.getAutoView().addObject("sysOrg", po).addObject("userNameStr", userNameStr).addObject("userNameCharge", userNameCharge).addObject("orgId", orgId).addObject("flag", flag);

	}

	/**
	 * 将维度或全部、未分配组织等添加为组织树的一个节点
	 * 
	 * @param demId
	 * @return
	 * @throws Exception
	 */
	private SysOrg getRootSysOrg(Long demId, String orgName) throws Exception {

		SysOrg org = new SysOrg();
		org.setOrgId(demId);
		org.setOrgSupId(0L);
		org.setPath(demId.toString());
		org.setDemId(demId);
		org.setIsRoot((short) 1);
		org.setOrgName(orgName);
		org.setOrgPathname(orgName);
		return org;
	}

	/**
	 * 获取组织架构树
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getTreeData")
	@ResponseBody
	@Action(description = "获取组织树结构", execOrder = ActionExecOrder.AFTER, detail = "获取组织树结构", exectype = "管理日志")
	public List<SysOrg> getTreeData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 获取维度ID
		Long demId = RequestUtil.getLong(request, "demId", 0);
		
		String type = RequestUtil.getString(request,"type");
		String typeVal = RequestUtil.getString(request,"typeVal");
		
		// 当前维度
		List<Demension> demens = null;
		long orgId = RequestUtil.getLong(request, "orgId", 0);
		List<SysOrg> orgList = new ArrayList<SysOrg>();
		if (orgId == 0) {
			if (demId != 0) {
				demens = new ArrayList<Demension>();
				if (demId ==1 && StringUtil.isNotEmpty(type)) {
					SysOrg sysOrg= orgServiceImpl.getSysOrgByScope(type, typeVal);
					orgList = sysOrgService.getOrgByOrgSupId(sysOrg.getOrgId());
					sysOrg.setOrgPathname(sysOrg.getOrgName());
					orgList.add(sysOrg);
				}else {
					demens.add(demensionService.getById(demId));
					orgList = sysOrgService.getOrgByOrgSupIdAndLevel(demId);
				}
			} else {
				demens = demensionService.getAll();
				if (demens.size() > 0) {
					for (int i = 0; i < demens.size(); i++) {
						orgList.addAll(sysOrgService.getOrgByOrgSupIdAndLevel(demens.get(i).getDemId()));
					}
				}

			}
			for (Demension demension : demens) {
				orgList.add(getRootSysOrg(demension.getDemId(), demension.getDemName()));
			}
		} else {
			orgList = sysOrgService.getOrgByOrgSupId(orgId);
		}
		
		return orgList;
	}

	/**
	 * 获取组织架构树
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getPosTreeData")
	@ResponseBody
	@Action(description = "获取组织树结构", execOrder = ActionExecOrder.AFTER, detail = "获取组织树结构", exectype = "管理日志")
	public List<SysOrg> getPosTreeData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 获取维度ID
		Long demId = RequestUtil.getLong(request, "demId", 0);
		// 当前维度
		List<Demension> demens = null;
		long orgId = RequestUtil.getLong(request, "orgId", 0);

		List<SysOrg> orgList = new ArrayList<SysOrg>();
		if (orgId == 0) {
			if (demId != 0) {
				demens = new ArrayList<Demension>();
				demens.add(demensionService.getById(demId));
				orgList = sysOrgService.getOrgByOrgSupIdAndLevel(demId);
			} else {
				demens = demensionService.getAll();
				if (demens.size() > 0) {
					for (int i = 0; i < demens.size(); i++) {
						orgList.addAll(sysOrgService.getOrgByOrgSupIdAndLevel(demens.get(i).getDemId()));
					}
				}

			}
			for (Demension demension : demens) {
				orgList.add(getRootSysOrg(demension.getDemId(), demension.getDemName()));
			}
		} else {
			orgList = sysOrgService.getOrgByOrgSupId(orgId);
		}
		return orgList;
	}

	/**
	 * 获取组织架构在线用户树
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("getTreeOnlineData")
	@ResponseBody
	@Action(description = "获取在线组织树结构", execOrder = ActionExecOrder.AFTER, detail = "获取在线组织树结构", exectype = "管理日志")
	public List<SysOrg> getTreeOnlineData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String type = RequestUtil.getString(request, "type");
		String typeVal = RequestUtil.getString(request, "typeVal");
		Map<Long, OnlineUser> onlineUsers = UserSessionListener.getOnLineUsers();
		List<OnlineUser> onlineList = new ArrayList<OnlineUser>();
		for (Long userId : onlineUsers.keySet()) {
			OnlineUser onlineUser = onlineUsers.get(userId);
			SysOrg sysOrg = sysOrgService.getPrimaryOrgByUserId(onlineUser.getUserId());
			if (sysOrg != null) {
				onlineUser.setOrgId(sysOrg.getOrgId());
				onlineUser.setOrgName(sysOrg.getOrgName());
			}
			onlineList.add(onlineUser);
		}
		String depTreeRootId = configproperties.getProperty("depTreeRootId");
		Long demId = RequestUtil.getLong(request, "demId", 0);
		Demension demension = demensionService.getById(demId);
		Map<Long, SysOrg> orgMap = sysOrgService.getOrgMapByDemId(demId);
		orgMap.put(0L, getRootSysOrg(-1L, "未分配组织"));// 添加一个未分配组织的项
		for (OnlineUser onlineUser : onlineList) {
			Long onlineUserId = onlineUser.getOrgId();
			SysOrg sysOrg = orgMap.get(onlineUserId);
			if (sysOrg != null) {
				int onlineNum = sysOrg.getOnlineNum();
				orgMap.get(onlineUserId).setOnlineNum(onlineNum + 1);
				
				// 把它父组织全部+1
				SysOrg pso = orgMap.get(sysOrg.getOrgSupId());
				while (pso != null) {
					pso.setOnlineNum(pso.getOnlineNum() + 1);
					pso = orgMap.get(pso.getOrgSupId());
				}
			} else {
				orgMap.get(0L).setOnlineNum(orgMap.get(0L).getOnlineNum() + 1);
			}
		}
		List<SysOrg> orgList = new ArrayList<SysOrg>();
		List<SysOrg> orgListTemp = new ArrayList<SysOrg>();
		for (SysOrg sysOrg : orgMap.values()) {
			String newName = sysOrg.getOrgName() + "(" + sysOrg.getOnlineNum() + ")";
			sysOrg.setOrgName(newName);
			orgListTemp.add(sysOrg);
		}
		
		if (demId==1) {
			SysOrg sysOrg = orgServiceImpl.getSysOrgByScope(type, typeVal);
			String path = sysOrg.getPath();
			List<SysOrg> orgList1 = sysOrgService.getByOrgPath(path+"%");
			for (SysOrg sysOrg2 : orgListTemp) {
				for (SysOrg sysOrg3 : orgList1) {
					if (sysOrg2.getOrgId().longValue()==sysOrg3.getOrgId().longValue()) {
						orgList.add(sysOrg2);
					}
				}
				if (sysOrg2.getDemId()==-1L && sysOrg.getOrgId()==1L) {
					orgList.add(sysOrg2);
				}
			}
		}else {
			orgList=orgListTemp;
		}

		// 指定根节点
		if (!StringUtils.isEmpty(depTreeRootId)) {
			SysOrg org = getRootSysOrg(demId, "全部");
			if (demension != null)
				org.setOrgName(demension.getDemName());
			orgList.add(org);
		}
		return orgList;
	}

	/**
	 * 移动分类数据。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("move")
	@Action(description = "转移分类", execOrder = ActionExecOrder.AFTER, detail = "转移分类", exectype = "管理日志")
	public void move(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultMessage resultMessage = null;
		PrintWriter out = response.getWriter();
		long targetId = RequestUtil.getLong(request, "targetId", 0);
		long dragId = RequestUtil.getLong(request, "dragId", 0);
		String moveType = RequestUtil.getString(request, "moveType");
		try {
			sysOrgService.move(targetId, dragId, moveType);

			resultMessage = new ResultMessage(ResultMessage.Success, "转移分类完成");
		} catch (Exception ex) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				resultMessage = new ResultMessage(ResultMessage.Fail, "转移分类失败:" + str);
				response.getWriter().print(resultMessage);
			} else {
				String message = ExceptionUtil.getExceptionMessage(ex);
				resultMessage = new ResultMessage(ResultMessage.Fail, message);
				response.getWriter().print(resultMessage);
			}
		}
		out.print(resultMessage);
	}

	/**
	 * 取得组织参数列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("paramList")
	@Action(description = "取得组织参数表", execOrder = ActionExecOrder.AFTER, detail = "取得组织参数表", exectype = "管理日志")
	public ModelAndView paramList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = this.getAutoView();
		mv.addObject("scope", "global");
		return getParamMv(request, mv);
	}

	/**
	 * 取得组织参数表。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("paramGradeList")
	@Action(description = "取得组织参数表", execOrder = ActionExecOrder.AFTER, detail = "取得组织参数表", exectype = "管理日志")
	public ModelAndView paramGradeList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/platform/system/sysOrgParamList");
		mv.addObject("scope", "grade");
		return getParamMv(request, mv);
	}

	/**
	 * 组织树排序列表。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("sortList")
	@Action(description = "组织树排序列表", execOrder = ActionExecOrder.AFTER, detail = "组织树排序列表", exectype = "管理日志")
	public ModelAndView sortList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long orgId = RequestUtil.getLong(request, "orgId", -1);
		List<SysOrg> list = new ArrayList<SysOrg>();
		List<SysOrg> SysOrgList = new ArrayList<SysOrg>();
		SysOrg sysOrg = sysOrgService.getById(orgId);
		if (sysOrg != null) {
			list = sysOrgService.getByOrgPath(sysOrg.getPath());
			if (list.size() > 0) {
				for (SysOrg SysOrg : list) {
					int SysOrgLength = SysOrg.getPath().split("\\.").length;
					int sysOrgLength = sysOrg.getPath().split("\\.").length + 1;
					if (SysOrgLength == sysOrgLength) {
						SysOrgList.add(SysOrg);
					}
				}
			}
		} else {
			// 获取维度ID
			Long demId = RequestUtil.getLong(request, "demId", 0);
			list = sysOrgService.getOrgsByDemIdOrAll(demId);
			if (list.size() > 0) {
				for (SysOrg SysOrg : list) {
					int SysOrgLength = SysOrg.getPath().split("\\.").length;
					if (SysOrgLength == 2) {
						SysOrgList.add(SysOrg);
					}
				}
			}
		}
		return getAutoView().addObject("SysOrgList", SysOrgList);
	}

	/**
	 * 组织树排序。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("sort")
	@Action(description = "组织树排序", execOrder = ActionExecOrder.AFTER, detail = "组织树排序", exectype = "管理日志")
	public void sort(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultMessage resultObj = null;
		PrintWriter out = response.getWriter();
		Long[] lAryId = RequestUtil.getLongAryByStr(request, "orgIds");
		if (BeanUtils.isNotEmpty(lAryId)) {
			for (int i = 0; i < lAryId.length; i++) {
				Long orgId = lAryId[i];
				long sn = i + 1;
				sysOrgService.updSn(orgId, sn);
			}
		}
		resultObj = new ResultMessage(ResultMessage.Success, "排序分类完成");
		out.print(resultObj);
	}

	/**
	 * 获取参数列表mv。
	 * 
	 * @param request
	 * @param mv
	 * @return
	 */
	private ModelAndView getParamMv(HttpServletRequest request, ModelAndView mv) {
		Long orgId = RequestUtil.getLong(request, "orgId");
		if (orgId == 0) {
			mv.addObject("sysOrg", null);
		} else {
			SysOrg sysOrg = sysOrgService.getById(orgId);
			List<SysOrgParam> list = sysOrgParamService.getListByOrgId(orgId);
			mv.addObject("userParamList", list).addObject("orgId", orgId).addObject("sysOrg", sysOrg);
		}
		return mv;
	}
	
	@RequestMapping("updCompany")
	@Action(description = "更新组织分公司", execOrder = ActionExecOrder.AFTER, detail = "更新组织分公司", exectype = "管理日志")
	public void updCompany(HttpServletResponse response) throws IOException {
		ResultMessage resultObj = new ResultMessage(ResultMessage.Success, "更新组织分公司成功");
		PrintWriter out = response.getWriter();
		try{
			sysOrgService.updCompany();
			
		}
		catch(Exception ex){
			resultObj = new ResultMessage(ResultMessage.Fail, "更新组织分公司失败");
		}
		out.print(resultObj);
	}
	
	/**
	 * 获取组织架构树
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getOrgUserTreeData")
	@ResponseBody
	@Action(description = "获取组织树结构", execOrder = ActionExecOrder.AFTER, detail = "获取组织树结构", exectype = "管理日志")
	public List<SysOrg> getOrgUserTreeData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 获取维度ID
		Long demId = RequestUtil.getLong(request, "demId", 0);
		
		String type = RequestUtil.getString(request,"type");
		String typeVal = RequestUtil.getString(request,"typeVal");
		
		// 当前维度
		List<Demension> demens = null;
		long orgId = RequestUtil.getLong(request, "orgId", 0);
		List<SysOrg> orgList = new ArrayList<SysOrg>();
		if (orgId == 0) {
			if (demId != 0) {
				demens = new ArrayList<Demension>();
				if (demId ==1 && StringUtil.isNotEmpty(type)) {
					SysOrg sysOrg= orgServiceImpl.getSysOrgByScope(type, typeVal);
					orgList = sysOrgService.getOrgByOrgSupId(sysOrg.getOrgId());
					sysOrg.setOrgPathname(sysOrg.getOrgName());
					orgList.add(sysOrg);
				}else {
					demens.add(demensionService.getById(demId));
					orgList = sysOrgService.getOrgByOrgSupIdAndLevel(demId);
				}
			} else {
				demens = demensionService.getAll();
				if (demens.size() > 0) {
					for (int i = 0; i < demens.size(); i++) {
						orgList.addAll(sysOrgService.getOrgByOrgSupIdAndLevel(demens.get(i).getDemId()));
					}
				}

			}
			for (Demension demension : demens) {
				orgList.add(getRootSysOrg(demension.getDemId(), demension.getDemName()));
			}
		} else {
			orgList = sysOrgService.getOrgByOrgSupId(orgId);
		}
		
		//获取
		List  treeList=new ArrayList();
		if(0!=orgId){
			List<SysOrg> ll = sysOrgService.getUserListByOrgId(orgId);
			treeList.addAll(ll);
			for(SysOrg o :orgList){
				Long id = o.getOrgId();
				List<SysOrg> l = sysOrgService.getUserListByOrgId(id);
				o.setIsParent("true");
			}
		}
		treeList.addAll(orgList);
		
		return treeList;
	}


	/**
	 * 获取组织架构树
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getCompTreeData")
	@ResponseBody
	@Action(description = "获取组织树结构", execOrder = ActionExecOrder.AFTER, detail = "获取组织树结构", exectype = "管理日志")
	public List<SysOrg> getCompTreeData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 获取维度ID
		Long demId = RequestUtil.getLong(request, "demId", 0);

		String type = RequestUtil.getString(request,"type");
		String typeVal = RequestUtil.getString(request,"typeVal");

		// 当前维度
		List<Demension> demens = null;
		long orgId = RequestUtil.getLong(request, "orgId", 0);
		List<SysOrg> orgList = new ArrayList<SysOrg>();
		if (orgId == 0) {
			if (demId != 0) {
				demens = new ArrayList<Demension>();
				if (demId ==1 && StringUtil.isNotEmpty(type)) {
					SysOrg sysOrg= orgServiceImpl.getSysOrgByScope(type, typeVal);
					orgList = sysOrgService.getOrgByOrgSupId(sysOrg.getOrgId());
					sysOrg.setOrgPathname(sysOrg.getOrgName());
					orgList.add(sysOrg);
				}else {
					demens.add(demensionService.getById(demId));
					orgList = sysOrgService.getOrgByOrgSupIdAndLevel(demId);
				}
			} else {
				demens = demensionService.getAll();
				if (demens.size() > 0) {
					for (int i = 0; i < demens.size(); i++) {
						orgList.addAll(sysOrgService.getOrgByOrgSupIdAndLevel(demens.get(i).getDemId()));
					}
				}

			}
			for (Demension demension : demens) {
				orgList.add(getRootSysOrg(demension.getDemId(), demension.getDemName()));
			}
		} else {
			orgList = sysOrgService.getOrgByOrgSupId(orgId);
		}
		//把所有公司以下类型的删除
		List<SysOrg> newList = new ArrayList<SysOrg>();
		for (SysOrg org:orgList){
			if(null==org.getOrgType()||org.getOrgType()<3){
				newList.add(org);
			}
		}
		for (SysOrg org:newList){
			org.setIsParent("false");
			List<SysOrg> list = sysOrgService.getOrgByOrgSupId(org.getOrgId());
			if(list.size()>0){
				for (SysOrg o:list){
					if(o.getOrgType()<3){
						org.setIsParent("true");
						break;
					}
				}
			}
		}
		return newList;
	}

	/**
	 * 组织对话框的展示
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("selectorComp")
	@Action(description = "组织对话框的展示", execOrder = ActionExecOrder.AFTER, detail = "组织对话框的展示", exectype = "管理日志")
	public ModelAndView selectorComp(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "sysOrgList");
		Long orgId = RequestUtil.getLong(request, "orgId");
		Long demId = RequestUtil.getLong(request, "demId");
		String orgName = RequestUtil.getString(request, "orgName");
		String type = RequestUtil.getString(request, "type");
		String typeVal = RequestUtil.getString(request, "typeVal");

		if (StringUtil.isNotEmpty(orgName)) {
			filter.addFilter("orgName", "%" + orgName + "%");
		}
		if (demId != 0) {
			filter.addFilter("demId", demId);
		}
		if (StringUtil.isNotEmpty(type)) {
			SysOrg sysOrg = orgServiceImpl.getSysOrgByScope(type, typeVal);
			filter.addFilter("path", StringUtil.isNotEmpty(sysOrg.getPath()) ? (sysOrg.getPath() + "%") : ("%."+sysOrg.getOrgId()+".%"));
		}
		SysOrg org = sysOrgService.getById(orgId);
		if (org != null) {
			filter.addFilter("path", StringUtil.isNotEmpty(org.getPath()) ? (org.getPath() + "%") : ("%."+org.getOrgId()+".%"));
		}
		List<SysOrg> sysOrgList = sysOrgService.getAllComp(filter);
		//把所有公司一下类型的删除
		for(int i=sysOrgList.size()-1;i>=0;i--){
			if(sysOrgList.get(i).getOrgType()>=3){
				sysOrgList.remove(i);
			}
		}

		String isSingle = RequestUtil.getString(request, "isSingle", "false");
		ModelAndView mv = this.getAutoView()
				.addObject("sysOrgList", sysOrgList)
				.addObject("type", type)
				.addObject("typeVal", typeVal)
				.addObject("isSingle", isSingle);
		return mv;
	}

	@RequestMapping("selectorCompJson")
	@ResponseBody
	public ResultVo selectorCompJson(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json = new JSONObject();
		QueryFilter filter = new QueryFilter(request,false);
		Long orgId = RequestUtil.getLong(request, "orgId");
		Long demId = RequestUtil.getLong(request, "demId");
		String orgName = RequestUtil.getString(request, "orgName");
		String type = RequestUtil.getString(request, "type");
		String typeVal = RequestUtil.getString(request, "typeVal");

		if (StringUtil.isNotEmpty(orgName)) {
			filter.addFilter("orgName", "%" + orgName + "%");
		}
		if (demId != 0) {
			filter.addFilter("demId", demId);
		}
		if (StringUtil.isNotEmpty(type)) {
			SysOrg sysOrg = orgServiceImpl.getSysOrgByScope(type, typeVal);
			filter.addFilter("path", StringUtil.isNotEmpty(sysOrg.getPath()) ? (sysOrg.getPath() + "%") : ("%."+sysOrg.getOrgId()+".%"));
		}
		SysOrg org = sysOrgService.getById(orgId);
		if (org != null) {
			filter.addFilter("path", StringUtil.isNotEmpty(org.getPath()) ? (org.getPath() + "%") : ("%."+org.getOrgId()+".%"));
		}
		try {
			List<SysOrg> sysOrgList = sysOrgService.getAllComp(filter);
			//把所有公司一下类型的删除
			for(int i=sysOrgList.size()-1;i>=0;i--){
				if(sysOrgList.get(i).getOrgType()>=3){
					sysOrgList.remove(i);
				}
			}
			String isSingle = RequestUtil.getString(request, "isSingle", "false");
			json.put("sysOrgList",sysOrgList);
			json.put("type",type);
			json.put("typeVal",typeVal);
			json.put("isSingle",isSingle);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取组织信息成功",json);
		}catch (Exception e){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取组织此信息失败",e.getMessage());
		}
	}

	/**
	 * 获取组织人员架构树(手机端)
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getTreeDataForMobile")
	@ResponseBody
	@Action(description = "获取组织树结构", execOrder = ActionExecOrder.AFTER, detail = "获取组织树结构", exectype = "管理日志")
	public Object getTreeDataForMobile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 获取维度ID
		Long demId = RequestUtil.getLong(request, "demId", 0);

		String type = RequestUtil.getString(request,"type");
		String typeVal = RequestUtil.getString(request,"typeVal");
		JSONObject json = new JSONObject();
		List<SysOrg> userList = new ArrayList<>();

		// 当前维度
		List<Demension> demens = null;

		long orgId = RequestUtil.getLong(request, "orgId", 0);
		List<SysOrg> orgList = new ArrayList<SysOrg>();
		if (orgId == 0) {
			if (demId != 0) {
				demens = new ArrayList<Demension>();
				if (demId ==1 && StringUtil.isNotEmpty(type)) {
					SysOrg sysOrg= orgServiceImpl.getSysOrgByScope(type, typeVal);
					orgList = sysOrgService.getOrgByOrgSupId(sysOrg.getOrgId());
					sysOrg.setOrgPathname(sysOrg.getOrgName());
					orgList.add(sysOrg);
					for (SysOrg org:orgList) {
						if (org.getIsParent().equals("false")) {
							List<SysOrg> userListByOrgId = sysOrgService.getUserListByOrgId(org.getOrgId());
							if (userListByOrgId.size() > 0) {
								org.setIsParent("true");
							}
						}
					}
				}else {
					demens.add(demensionService.getById(demId));
					orgList = sysOrgService.getOrgByOrgSupIdAndLevel(demId);
				}
			} else {
				demens = demensionService.getAll();
				if (demens.size() > 0) {
					for (int i = 0; i < demens.size(); i++) {
						orgList.addAll(sysOrgService.getOrgByOrgSupIdAndLevel(demens.get(i).getDemId()));
					}
				}

			}
			for (Demension demension : demens) {
				orgList.add(getRootSysOrg(demension.getDemId(), demension.getDemName()));
			}
		} else {
			orgList = sysOrgService.getOrgByOrgSupId(orgId);
			for (SysOrg org:orgList) {
				if (org.getIsParent().equals("false")) {
					List<SysOrg> userListByOrgId = sysOrgService.getUserListByOrgId(org.getOrgId());
					if (userListByOrgId.size() > 0) {
						org.setIsParent("true");
					}
				}
			}
			userList = sysOrgService.getUserListByOrgId(orgId);
			for (SysOrg user : userList) {
				//获取某用户的组织列表字符串（可能多个组织）
				String orgNames = userPositionService.getOrgnamesByUserId(user.getOrgId());
				user.setOrgPathname(orgNames.toString());
			}
		}
		json.put("orgList",orgList);
		json.put("userList",userList);
		return json;
	}

}
