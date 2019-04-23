/**
 * 
 */
package com.suneee.oa.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.page.PageList;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.Demension;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysOrgType;
import com.suneee.platform.service.system.SysOrgTypeService;
import com.suneee.weixin.util.CommonUtil;
import com.suneee.oa.service.user.DemensionExtendService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;

/**
 * 维度信息Demension扩展Controller类
 * 
 * @author xiongxianyun
 *
 */
@Controller
@RequestMapping("/oa/user/demensionExtend/")
public class DemensionExtendController extends UcpBaseController{
	@Resource
	private DemensionExtendService demensionExtendService;
	@Resource
	private SysOrgTypeService sysOrgTypeService;
	
	@RequestMapping("list")
	@Action(description="维度信息列表", detail="获取维度信息列表", execOrder=ActionExecOrder.AFTER, exectype="操作日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo list(HttpServletRequest request, HttpServletResponse response){
		try {
			PageList<Demension> demensionList = (PageList<Demension>)demensionExtendService.getAllDemension(new QueryFilter(request));
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取维度信息列表成功！", CommonUtil.getListModel(demensionList));
		} catch (Exception e) {
			logger.error("获取维度信息列表失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取维度信息列表失败！");
		}
	}
	
	@RequestMapping("getAll")
	@Action(description="获取系统所有维度信息", detail="获取系统所有维度信息", execOrder=ActionExecOrder.AFTER, exectype="操作日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo getAll(HttpServletRequest request, HttpServletResponse response){
		try {
			List<Demension> demensionList = (List<Demension>)demensionExtendService.getAllDemension(new QueryFilter(request, false));
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取所有维度信息成功！", demensionList);
		} catch (Exception e) {
			logger.error("获取所有维度信息列表失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取所有维度信息列表失败！");
		}
	}
	
	@RequestMapping("save")
	@Action(description="保存维度信息", detail="<#if isAdd>添加<#else>更新</#if>维度信息<#if demId??>"
			+ "【${SysAuditLinkService.getDemensionLink(Long.valueOf(demId))}】</#if><#if isSuccessed>成功<#else>失败</#if>", 
			execOrder=ActionExecOrder.AFTER, exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo save(HttpServletRequest request, HttpServletResponse response, Demension demension){
		SysAuditThreadLocalHolder.putParamerter("isAdd", demension.getDemId() == null);
		SysAuditThreadLocalHolder.putParamerter("isSuccessed", false);
		// 获取组织类型
		String orgTypeStr = RequestUtil.getString(request, "orgTypes");
		if(StringUtils.isBlank(orgTypeStr)){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请添加组织类型！");
		}
		
		try {
			demensionExtendService.save(demension, orgTypeStr);
			SysAuditThreadLocalHolder.putParamerter("demId", demension.getDemId().toString());
			SysAuditThreadLocalHolder.putParamerter("isSuccessed", true);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "保存维度信息成功！");
		} catch (Exception e) {
			logger.error("保存维度信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "保存维度信息失败！");
		}
	}
	
	@RequestMapping("details")
	@Action(description="获取维度信息", detail="获取维度<#if demId??>【${SysAuditLinkService.getDemensionLink(Long.valueOf(demId))}】</#if>详情", 
		execOrder=ActionExecOrder.AFTER, exectype="操作日志",  ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo details(HttpServletRequest request, HttpServletResponse response){
		Long demId = RequestUtil.getLong(request, "demId");
		if(demId == 0){
			logger.error("获取维度信息失败：demId为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请求参数错误！");
		}
		
		try {
			// 根据维度ID获取维度信息
			Demension demension = demensionExtendService.getById(demId);
			// 根据维度ID获取组织类型信息
			List<SysOrgType> orgTypeList = sysOrgTypeService.getByDemId(demId);
			
			// 组装返回数据
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("demension", demension);
			data.put("orgTypes", orgTypeList);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取维度信息成功！", data);
		} catch (Exception e) {
			logger.error("" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取维度信息失败！");
		}
	}
	
	@RequestMapping("del")
	@Action(description="删除维度信息", detail="删除维度信息<#if demId??><#list StringUtils.split(demId, \",\") as item>"
		+ "<#assign entity=demensionService.getById(Long.valueOf(item))/><#if entity!=''>【${entity.demName}】</#if></#list></#if>", 
		execOrder=ActionExecOrder.BEFORE, exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo del(HttpServletRequest request, HttpServletResponse response){
		Long[] demIds = RequestUtil.getLongAryByStr(request, "demId");
		Boolean isForce = RequestUtil.getBoolean(request, "isForce", false);
		if(demIds == null || demIds.length == 0){
			logger.error("删除维度信息失败：demId为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除维度信息失败！");
		}
		// 删除成功标识
		boolean deleteFlag = false;
		StringBuffer message = new StringBuffer();
		Demension demension = null;
		for(Long demId : demIds){
			deleteFlag = demensionExtendService.del(demId, isForce);
			// 删除失败的维度信息需要给出提示
			if(!deleteFlag){
				demension = demensionExtendService.getById(demId);
				if(demension != null){
					message.append(demension.getDemName()).append("、");
				}
			}
		}
		
		if(message.length() == 0){
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除维度信息成功！");
		}else{
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message.substring(0, message.length() - 2) + "已与组织关联，未能删除!");	
		}
	}
}
