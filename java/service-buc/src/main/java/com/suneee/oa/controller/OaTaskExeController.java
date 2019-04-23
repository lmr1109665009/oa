package com.suneee.oa.controller;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.page.PageList;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.BpmRunLog;
import com.suneee.platform.model.bpm.BpmTaskExe;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysProperty;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmRunLogService;
import com.suneee.platform.service.bpm.BpmTaskExeService;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * <pre>
 * 对象功能:任务转办代理 控制器类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2017-12-14 14:57:00
 * </pre>
 */
@Controller
@RequestMapping("/oa/oaTaskExe/")
@Action(ownermodel= SysAuditModelType.PROCESS_MANAGEMENT)
public class OaTaskExeController extends BaseController{

	@Resource
	private BpmTaskExeService bpmTaskExeService;
	@Resource
	private BpmRunLogService bpmRunLogService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;

	/**
	 * 获取代理交办事宜列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("accordingMattersList")
	@ResponseBody
	@Action(description = " 代理交办事宜")
	public ResultVo accordingMattersList(HttpServletRequest request,
										 HttpServletResponse response) throws Exception {
		try{
			QueryFilter filter = new QueryFilter(request,true);
			String orgCode = CookieUitl.getCurrentEnterpriseCode();
			filter.addFilter("orgCode",orgCode);
			if(!ContextUtil.isSuperAdmin()){
				Long userId = ContextUtil.getCurrentUserId();
				filter.addFilter("ownerId", userId);
			}
			Long typeId = (Long) filter.getFilters().get("typeId");
			if(typeId != null ){
				QueryFilter filter1 = new QueryFilter(request, false);
				List<BpmDefinition> defList = bpmDefinitionService.getMyDefList(filter1, typeId);
				List<Long> list = new ArrayList<>();
				for(BpmDefinition bd:defList){
					if(bd.getTypeId() != null){
						list.add(bd.getTypeId());
					}
				}
				//list去重
				List<Long> newList = new ArrayList(new TreeSet(list));
				filter.addFilter("typeIds",newList);
			}
			//按照查询条件查询数据
			List<BpmTaskExe> list = bpmTaskExeService.accordingMattersListOrgCode(filter);
			PageList<BpmTaskExe> resultList = (PageList<BpmTaskExe>) list;
			// 是否有全局流水号
			boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
			Map<String,Object> map = new HashMap<>();
			map.put("bpmTaskExeList", PageUtil.getPageVo(resultList));
			map.put("hasGlobalFlowNo",hasGlobalFlowNo);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取代理交办事宜数据成功！",map);
		}catch (Exception e){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取代理交办事宜数据失败！");
		}
	}

	/**
	 * 查看交办历史明细
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getAssignDetail")
	@ResponseBody
	@Action(description = "交办历史明细")
	public ResultVo getAssignDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long taskId = RequestUtil.getLong(request, "taskId", 0);
		List<BpmTaskExe> list = bpmTaskExeService.getByTaskId(taskId);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取交办历史明细数据成功！",list);
	}


	/**
	 * 批量取消交办
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("cancelBat")
	@ResponseBody
	@Action(description = "批量取消交办")
	public ResultVo cancelBat(HttpServletRequest request,
						  HttpServletResponse response) throws Exception {
		try {
			SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
			String idStr = RequestUtil.getString(request, "ids");
			String opinion=RequestUtil.getString(request, "opinion");
			String informType=RequestUtil.getString(request, "informType");
			if(StringUtil.isEmpty(idStr)){
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"请选择需要取消交办的项目!");
			}
			List<BpmTaskExe> list= bpmTaskExeService.cancelBat(idStr, opinion, informType, sysUser);
			//添加日志。
			addRunLong(list);

			//String message= MessageUtil.getMessage();
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"批量取消交办成功!");
		} catch (Exception e) {
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"批量取消交办失败！");
		}
	}

	private void addRunLong(List<BpmTaskExe> list){
		SysUser sysUser=(SysUser) ContextUtil.getCurrentUser();
		for(BpmTaskExe bpmTaskExe:list){
			String memo = "【" + sysUser.getFullname() + "】将交办任务【" + bpmTaskExe.getSubject()+ "】 取消";
			bpmRunLogService.addRunLog(bpmTaskExe.getRunId(), BpmRunLog.OPERATOR_TYPE_BACK, memo);
		}
	}
}
