package com.suneee.oa.controller.bpm;

import com.suneee.core.page.PageList;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.bpm.BpmRunLog;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.bpm.BpmRunLogService;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 对象功能:流程运行日志 控制器类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2018-1-16 15:15:15
 */
@Controller
@RequestMapping("/api/bpm/bpmRunLog/")
@Action(ownermodel=SysAuditModelType.PROCESS_MANAGEMENT)
public class BpmRunLogApiController extends BaseController
{
	@Resource
	private BpmRunLogService bpmRunLogService;
	
	/**
	 * 取得流程运行日志分页列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@ResponseBody
	@Action(description="查看流程运行日志分页列表")
	public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long runId=RequestUtil.getLong(request, "runId");
		QueryFilter filter=new QueryFilter(request,true);
		if(runId>0)filter.addFilter("runid", runId);
		if(null!=filter.getFilters().get("operatortype")&&filter.getFilters().get("operatortype").equals("-1")){
			filter.getFilters().put("operatortype",null);
		}
		PageList<BpmRunLog> list= (PageList<BpmRunLog>) bpmRunLogService.getAll(filter);
		Map<String,Object> map = new HashMap<>();
		map.put("bpmRunLogList", PageUtil.getPageVo(list));
		map.put("runId",runId);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流程运行日志分页列表数据成功！",map);
	}

	/**
	 * 删除流程运行日志
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@ResponseBody
	@Action(description="删除流程运行日志",
			execOrder=ActionExecOrder.BEFORE,
	        detail="删除流程<#list StringUtils.split(id,\",\") as item>" +
					  "<#assign entity=bpmRunLogService.getById(Long.valueOf(item))/>" +
					  "【${entity.processSubject}】" +
				   "</#list>的运行日志"
    )
	public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		ResultVo resultMessage=null;
		try{
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			bpmRunLogService.delByIds(lAryId);
			resultMessage = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除流程运行日志成功!");
		}
		catch(Exception ex){
			resultMessage = new ResultVo(ResultVo.COMMON_STATUS_FAILED,"删除流程运行日志失败:" + ex.getMessage());
		}
		return resultMessage;
	}

	/**
	 * 取得流程运行日志明细
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@ResponseBody
	@Action(description="查看流程运行日志明细")
	public ResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		long id=RequestUtil.getLong(request,"id");
		BpmRunLog bpmRunLog = bpmRunLogService.getById(id);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流程运行日志明细表数据成功！",bpmRunLog);
	}

}
