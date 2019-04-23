/**
 * 
 */
package com.suneee.oa.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.page.PageList;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.oa.service.user.SysOrgExtendService;
import com.suneee.oa.service.user.UserPositionExtendService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.UserPosition;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.platform.service.system.impl.OrgServiceImpl;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.service.system.SysOrgExtService;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.weixin.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 组织信息SysOrg扩展Controller类
 * 
 * @author xiongxianyun
 *
 */
@Controller
@RequestMapping("/oa/user/sysOrgExtend/")
public class SysOrgExtendController extends UcpBaseController{
	@Resource
	private SysOrgExtendService sysOrgExtendService;
	@Resource
	private SysOrgService sysOrgService;
	@Resource
	private SysOrgExtService sysOrgExtService;
	@Resource
	private UserPositionExtendService userPositionExtendService;
	@Resource
	private OrgServiceImpl orgServiceImpl;
	@Resource
	private UserPositionService userPositionService;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;
	
	/**
	 * 获取系统所有组织信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getAll")
	@Action(description="获取系统所有组织信息", detail="获取系统所有组织信息", execOrder=ActionExecOrder.AFTER, exectype="操作日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo getAll(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			Long demId = RequestUtil.getLong(request, "demId");
			if(demId == 0){
				logger.error("获取系统组织信息失败：请求参数为空！");
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取系统组织信息失败：请求参数错误！");
			}
			QueryFilter filter = new QueryFilter(request,false);
			// 查询当前登录用户当前所属企业下的组织
			String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
			filter.addFilter("enterpriseCode", enterpriseCode);
			
			List<SysOrg> list = sysOrgExtendService.getByCondition(filter);
			boolean needCharger = RequestUtil.getBoolean(request, "needCharger", true);
			if(needCharger){
				List<UserPosition> userPosList = null;
				// 获取组织负责人
				StringBuffer chargerName = null;
				for(SysOrg sysOrg : list){
					chargerName = new StringBuffer();
					userPosList = userPositionExtendService.getOrgCharger(sysOrg.getOrgId());
					for(UserPosition userPos : userPosList){
						chargerName.append(userPos.getUserName()).append(Constants.SEPARATOR_COMMA);
					}
					if(!StringUtils.isBlank(chargerName.toString())){
						sysOrg.setOwnUserName(chargerName.substring(0, chargerName.length()-1));
					}
				}
			}
			
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取系统组织信息成功！", list);
		} catch (Exception e) {
			logger.error("获取系统组织信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取系统组织信息失败：" + e.getMessage());
		}
	}
	
	
	
	/**
	 * 获取系统组织信息分页列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="获取系统组织信息分页列表", detail="获取系统组织信息分页列表", execOrder=ActionExecOrder.AFTER, exectype="操作日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long demId = RequestUtil.getLong(request, "demId");
		if(demId == 0){
			logger.error("获取系统组织信息分页列表失败：请求参数为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取系统组织信息分页列表失败：请求参数错误！");
		}
		try {
			QueryFilter filter = new QueryFilter(request);
			// 查询当前登录用户当前所属企业下的组织
			String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
			filter.addFilter("enterpriseCode", enterpriseCode);
			// 获取组织信息列表
			PageList<SysOrg> list = (PageList<SysOrg>)sysOrgExtendService.getByCondition(filter);
			List<UserPosition> userPosList = null;
			
			// 获取组织负责人
			StringBuffer chargerName = null;
			for(SysOrg sysOrg : list){
				chargerName = new StringBuffer();
				userPosList = userPositionExtendService.getOrgCharger(sysOrg.getOrgId());
				for(UserPosition userPos : userPosList){
					chargerName.append(userPos.getUserName()).append(Constants.SEPARATOR_COMMA);
				}
				if(!StringUtils.isBlank(chargerName.toString())){
					sysOrg.setOwnUserName(chargerName.substring(0, chargerName.length()-1));
				}
			}
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取系统组织信息列表成功！", CommonUtil.getListModel(list));
		} catch (Exception e) {
			logger.error("获取系统组织信息列表失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取系统组织信息列表失败：" + e.getMessage());
		}
	}
	
	/** 
	 * 获取组织列表：根据组织ID获取组织的直属下级组织
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getTreeList")
	@ResponseBody
	public ResultVo getTreeList(HttpServletRequest request, HttpServletResponse response)throws Exception{
		Long demId = RequestUtil.getLong(request, "demId");
		if(demId == 0){
			logger.error("获取组织列表失败：维度ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织列表失败：请求参数错误！");
		}
		
		QueryFilter filter = new QueryFilter(request, false);
		// 获取上级组织ID
		Long orgSupId = RequestUtil.getLong(request, "orgId", null);
		filter.addFilter("orgSupId", orgSupId);
		// 获取用户当前所属企业的组织
		String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
		filter.addFilter("enterpriseCode", enterpriseCode);
		
		try {
			List<SysOrg> sysOrgList = sysOrgExtendService.getSimpleByCondition(filter);
			// 获取组织负责人
			StringBuffer chargerName = null;
			List<UserPosition> userPosList = null;
			for(SysOrg sysOrg : sysOrgList){
				chargerName = new StringBuffer();
				userPosList = userPositionExtendService.getOrgCharger(sysOrg.getOrgId());
				for(UserPosition userPos : userPosList){
					chargerName.append(userPos.getUserName()).append(Constants.SEPARATOR_COMMA);
				}
				if(!StringUtils.isBlank(chargerName.toString())){
					sysOrg.setOwnUserName(chargerName.substring(0, chargerName.length()-1));
				}
			}
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取组织列表成功！", sysOrgList);
		} catch (Exception e) {
			logger.error("获取组织列表失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织列表失败！");
		}
	}
	
	/**
	 * 保存组织信息
	 * @param request
	 * @param response
	 * @param sysOrg
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="保存组织信息", detail="<#if isAdd>添加<#else>更新</#if>组织信息<#if orgId??>"
			+ "【${SysAuditLinkService.getSysOrgLink(Long.valueOf(orgId))}】</#if><#if isSuccessed>成功<#else>失败</#if>", 
			execOrder=ActionExecOrder.AFTER, exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo save(HttpServletRequest request, HttpServletResponse response, SysOrg sysOrg) throws Exception{
		SysAuditThreadLocalHolder.putParamerter("isAdd", sysOrg.getOrgId() == null);
		SysAuditThreadLocalHolder.putParamerter("isSuccessed", false);
		// 根据父级组织ID查询组织信息
		Long orgSupId = RequestUtil.getLong(request, "orgSupId");
		SysOrg supOrg = sysOrgExtendService.getById(orgSupId);
		
		// 拼接组织名称路径
		String orgPathname = "";
		if(supOrg != null){
			orgPathname = supOrg.getOrgPathname();
		}
		orgPathname += Constants.SEPARATOR_BACK_SLANT + sysOrg.getOrgName();
		
		// 根据组织名称路径，查询组织信息
		SysOrg orgDb = sysOrgExtService.getByOrgPathName(orgPathname, sysOrg.getDemId(), sysOrg.getOrgId());
		if(orgDb != null){
			logger.error("保存组织信息失败：组织【" + orgPathname + "】已经存在");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "组织【" + orgPathname + "】已经存在，请勿重复创建！");
		}
		sysOrg.setOrgPathname(orgPathname);
		
		boolean isAdd = false;
		Long orgId = sysOrg.getOrgId();
		if(orgId == null || orgId == 0){
			isAdd = true;
			orgId = UniqueIdUtil.genId();
			sysOrg.setOrgId(orgId);
		}
		
		// 组织路径
		String path = null;
		if(supOrg == null){
			sysOrg.setOrgSupId(sysOrg.getDemId());
			path = sysOrg.getDemId() + Constants.SEPARATOR_PERIOD;
		} else {
			path = supOrg.getPath();
		}
		sysOrg.setPath(path + orgId + Constants.SEPARATOR_PERIOD);
		
		try {
			sysOrgExtendService.save(sysOrg, isAdd);
			SysAuditThreadLocalHolder.putParamerter("orgId",orgId.toString());
			SysAuditThreadLocalHolder.putParamerter("isSuccessed", true);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "保存组织信息成功！");
		} catch (Exception e) {
			logger.error("保存组织信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "保存组织信息失败：" + e.getMessage());
		}
	}
	
	/**
	 * 获取组织信息详情
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("details")
	@Action(description="获取组织信息详情", detail="获取组织<#if orgId??>【${SysAuditLinkService.getSysOrgLink(Long.valueOf(orgId))}】</#if>详情",
		execOrder=ActionExecOrder.AFTER, exectype="操作日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo details(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long orgId = RequestUtil.getLong(request, "orgId");
		if(orgId == 0){
			logger.error("获取组织信息详情失败：请求参数不能为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织信息详情失败：请求参数错误");
		}
		try {
			SysOrg sysOrg = sysOrgExtendService.getByOrgId(orgId);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取组织信息详情成功！", sysOrg);
		} catch (Exception e) {
			logger.error("获取组织信息详情失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织信息详情失败：" + e.getMessage());
		}
	}
	
	/**
	 * 删除组织信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除组织信息", detail="删除组织信息<#if orgId??><#list StringUtils.split(orgId, \",\") as item>"
			+ "<#assign entity=sysOrgService.getById(Long.valueOf(item))/><#if entity!=''>"
			+ "【${entity.orgName}】</#if></#list></#if>", 
			execOrder=ActionExecOrder.BEFORE, exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long[] orgIds = RequestUtil.getLongAryByStr(request, "orgId");
		if(orgIds == null || orgIds.length == 0){
			logger.error("删除组织信息失败：请求参数不能为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除组织信息失败：请求参数错误！");
		}
		try {
			sysOrgExtendService.delByIds(orgIds);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除组织信息成功！");
		} catch (Exception e) {
			logger.error("删除组织信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除组织信息失败：" + e.getMessage());
		}
	}
	
	/**
	 * 获取同级组织列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("siblingOrg")
	@Action(description="获取同级组织列表", detail="获取组织<#if orgId??>【${SysAuditLinkService.getSysOrgLink(Long.valueOf(orgId))}】"
		+ "</#if>的同级组织列表",execOrder=ActionExecOrder.AFTER,exectype="操作日志",ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo siblingOrg(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long orgId = RequestUtil.getLong(request, "orgId");
		Short isRoot = RequestUtil.getShort(request, "isRoot", (short)0);
		if(orgId == 0){
			logger.error("获取同级组织列表失败：请求参数不能为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取同级组织列表失败：请求参数错误！");
		}
		
		try {
			// 根据组织ID获取组织信息
			SysOrg sysOrg = sysOrgExtendService.getById(orgId);
			if(sysOrg == null){
				logger.error("获取同级组织列表失败：该组织" + orgId +"不存在！");
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取同级组织列表失败：组织不存在！");
			}
			QueryFilter filter = new QueryFilter();
			if(SysOrg.IS_ROOT_N.equals(isRoot)){
				filter.addFilter("orgSupId", sysOrg.getOrgSupId());
			}
			filter.addFilter("demId", sysOrg.getDemId());
			String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
			filter.addFilter("enterpriseCode", enterpriseCode);
			List<SysOrg> orgList = sysOrgExtendService.getSimpleByCondition(filter);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取同级组织列表成功！", orgList);
		} catch (Exception e) {
			logger.error("获取同级组织列表失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取同级组织列表失败：" + e.getMessage());
		}
	}
	
	/**
	 * 组织排序
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("sort")
	@Action(description="组织排序", detail="组织排序", execOrder=ActionExecOrder.AFTER, exectype="管理日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo sort(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long[] orgIds = RequestUtil.getLongAryByStr(request, "orgIds");
		if(orgIds == null){
			logger.error("组织排序失败：请求参数不能为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "组织排序失败：请求参数错误！");
		}
		
		try {
			sysOrgExtendService.sort(orgIds);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "组织排序成功！");
		} catch (Exception e) {
			logger.error("组织排序失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "组织排序失败：" + e.getMessage());
		}
	}
	
	/**
	 * 获取企业信息列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("enterpriseinfoList")
	@Action(description="获取企业信息列表", detail="获取企业信息列表", execOrder=ActionExecOrder.AFTER, exectype="管理日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo enterpriseinfoList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			PageList<Enterpriseinfo> list = (PageList<Enterpriseinfo>)enterpriseinfoService.getAll(new QueryFilter(request));
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取企业信息列表成功！", CommonUtil.getListModel(list));
		} catch (Exception e) {
			logger.error("获取企业信息列表失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取企业信息列表失败：" + e.getMessage());
		}
	}
	/**
	 * 获取用户所属企业信息列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("userEnterpriseinfoList")
	@Action(description="获取用户所属企业信息列表", detail="从权限中心获取用户所属企业信息列表", execOrder=ActionExecOrder.AFTER, exectype="管理日志",
			ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo userEnterpriseinfoList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			Long userId=ContextUtil.getCurrentUserId();
			List<Enterpriseinfo> list = enterpriseinfoService.getByUserId(userId);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取用户所属企业信息列表成功！" , list);
		} catch (Exception e) {
			logger.error("获取企业信息列表失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户所属企业信息列表失败：" + e.getMessage());
		}
	}
	/**
	 * 组织对话框数据接口
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("selectorJson")
	@ResponseBody
	@Action(description = "组织对话框数据接口", execOrder = ActionExecOrder.AFTER, detail = "组织对话框数据接口", exectype = "管理日志")
	public ResultVo getSelectorJson(HttpServletRequest request,HttpServletResponse response) throws  Exception{
		QueryFilter filter = new QueryFilter(request);
		JSONObject json = new JSONObject();
		Long orgId = RequestUtil.getLong(request, "orgId");
		Long demId = RequestUtil.getLong(request, "demId");
		String orgName = RequestUtil.getString(request, "orgName");
		String type = RequestUtil.getString(request, "type");
		String typeVal = RequestUtil.getString(request, "typeVal");
        try {
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
		
		// 查询当前登录用户当前所属企业下的组织
		String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
		filter.addFilter("enterpriseCode", enterpriseCode);
		PageList<SysOrg> sysOrgList = (PageList<SysOrg>)sysOrgService.getAll(filter);
		String isSingle = RequestUtil.getString(request, "isSingle", "false");
        json.put("sysOrgList", CommonUtil.getListModel(sysOrgList));
		json.put("type", type);
		json.put("typeVal", typeVal);
		json.put("isSingle", isSingle);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"查询组织列表成功",json);
		}catch (Exception e){
        	return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"查询组织列表失败",e.getMessage());
		}
	}
	
	/** 
	 * 获取组织树：根据组织ID获取组织的直属下级组织
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getTreeData")
	@ResponseBody
	public ResultVo getTreeData(HttpServletRequest request, HttpServletResponse response)throws Exception{
		Long demId = RequestUtil.getLong(request, "demId");
		if(demId == 0){
			logger.error("获取组织树失败：维度ID为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织树失败：请求参数错误！");
		}
		
		QueryFilter filter = new QueryFilter(request, false);
		// 获取上级组织ID
		Long orgSupId = RequestUtil.getLong(request, "orgId", null);
		filter.addFilter("orgSupId", orgSupId);
		// 获取用户当前所属企业的组织
		String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
		filter.addFilter("enterpriseCode", enterpriseCode);
		
		try {
			List<SysOrg> sysOrgList = sysOrgExtendService.getSimpleByCondition(filter);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取组织树成功！", sysOrgList);
		} catch (Exception e) {
			logger.error("获取组织树失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织树失败！");
		}
	}
}
