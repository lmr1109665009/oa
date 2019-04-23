package com.suneee.oa.controller;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.page.PageList;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.AgentCondition;
import com.suneee.platform.model.bpm.AgentDef;
import com.suneee.platform.model.bpm.AgentSetting;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.bpm.AgentSettingService;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <pre>
 * 对象功能:代理设定 控制器类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2017-12-14 17:05:00
 * </pre>
 */
@Controller
@RequestMapping("/oa/oaAgentSetting/")
@Action(ownermodel= SysAuditModelType.PROCESS_MANAGEMENT)
public class OaAgentSettingController extends BaseController {

	@Resource
	private AgentSettingService agentSettingService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private BpmFormDefService bpmFormDefService;

	/**
	 * 获取代理设定分页列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@ResponseBody
	@Action(description="查看代理设定分页列表")
	public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		try{
			QueryFilter filter = new QueryFilter(request,true);
			Long curUserId = ContextUtil.getCurrentUserId();
			String orgCode = ContextSupportUtil.getCurrentEnterpriseCode();
			filter.addFilter("orgCode",orgCode);
			if(!ContextUtil.isSuperAdmin()){
				filter.addFilter("authid", curUserId);
			}
			filter.addFilter("orderField","CREATETIME");
			filter.addFilter("orderSeq","desc");
			PageList<AgentSetting> list= (PageList<AgentSetting>) agentSettingService.getAll(filter);

			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取代理设定分页列表数据成功！", PageUtil.getPageVo(list));
		}catch (Exception e){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取代理设定分页列表数据失败！");
		}
	}

	/**
	 * 更新代理设置状态  是启用还是禁用
	 * @return
	 */
	@RequestMapping("updateEnabled")
	@ResponseBody
	public ResultVo updateEnabled(HttpServletRequest request){
		Short enabled = RequestUtil.getShort(request, "enabled");
		long id = RequestUtil.getLong(request, "id");
		try {
			if(enabled == 0){
				enabled = 1;
			}else if(enabled == 1){
				enabled = 0;
			}
			boolean flag = agentSettingService.updateEnabled(id, enabled);
			if(flag){
				return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"更新成功！");
			}else{
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"更新失败！");
			}
		}catch (Exception e){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"更新失败！");
		}
	}

	/**
	 * 删除代理设定
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@ResponseBody
	@Action(description="删除代理设定")
	public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			agentSettingService.delByIds(lAryId);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"删除成功!");
		}catch(Exception ex){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"删除失败!");
		}
	}

	/**
	 * 编辑代理设定
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@ResponseBody
	@Action(description = "编辑代理设定")
	public ResultVo edit(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long id=RequestUtil.getLong(request,"id");
		AgentSetting agentSetting=agentSettingService.getById(id);
		if(agentSetting!=null){
			if(agentSetting.getAuthtype()==AgentSetting.AUTHTYPE_CONDITION){
				List<AgentCondition> agentConditionList=agentSettingService.getAgentConditionList(id);
				if(BeanUtils.isNotEmpty(agentConditionList)){
					AgentCondition condition = agentConditionList.get(0);
					String condJsonStr = condition.getCondition();
					com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(condJsonStr);
					Long tableId = jsonObject.getLong("tableId");

					BpmDefinition bpmDefinition = bpmDefinitionService.getMainDefByActDefKey(agentSetting.getFlowkey());
					if(BeanUtils.isNotEmpty(bpmDefinition)){
						Long currentTableId = bpmFormDefService.getTableIdByDefId(bpmDefinition.getDefId());

						if(currentTableId==null || ( currentTableId!=null && !currentTableId.equals(tableId))){
							agentConditionList.clear();
						}
					}
				}
				agentSetting.setAgentConditionList(agentConditionList);
			}else{
				List<AgentDef> agentDefList=agentSettingService.getAgentDefList(id);
				agentSetting.setAgentDefList(agentDefList);
			}
		}
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取代理设定数据成功！",agentSetting);
	}
}
