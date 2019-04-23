package com.suneee.platform.controller.bpm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.bpm.BpmDefVar;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.service.bpm.BpmDefVarService;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmService;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.form.BpmFormFieldService;
import com.suneee.platform.model.system.SysAuditModelType;

/**
 * 对象功能:流程变量定义 控制器类 开发公司:广州宏天软件有限公司 开发人员:phl 创建时间:2011-12-01 16:50:07
 */
@Controller
@RequestMapping("/platform/bpm/bpmDefVar/")
@Action(ownermodel=SysAuditModelType.PROCESS_MANAGEMENT)
public class BpmDefVarController extends BaseController {
	@Resource
	private BpmDefVarService bpmDefVarService;
	@Resource
	private BpmService bpmService;
	@Resource
	public BpmDefinitionService bpmDefinitionService;
	@Resource
	private BpmFormFieldService bpmFormFieldService;

	/**
	 * 取得流程变量定义分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看流程变量定义分页列表")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long defId = RequestUtil.getLong(request, "defId");
		BpmDefinition bpmDefinition = bpmDefinitionService.getById(defId);
		String actDefId = bpmDefinition.getActDefId();
		Long actDeployId = bpmDefinition.getActDeployId();
		QueryFilter q = new QueryFilter(request, "bpmDefVarItem", false);
		if (defId != 0) {
			q.getFilters().put("defId", defId);
		}

		List<BpmDefVar> list = bpmDefVarService.getAll(q);
		ModelAndView mv = this.getAutoView().addObject("bpmDefVarList", list)
				.addObject("defId", defId)
				.addObject("actDeployId", actDeployId)
				.addObject("actDefId", actDefId)
				.addObject("bpmDefinition", bpmDefinition);

		return mv;
	}

	/**
	 * 删除流程变量定义
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除流程变量定义",
			execOrder=ActionExecOrder.BEFORE,
			detail="<#list StringUtils.split(varId,\",\") as item>" +
						"<#assign entity = bpmDefVarService.getById(Long.valueOf(item))/>" +
						"<#if item_index==0>" +
							"删除流程定义【${SysAuditLinkService.getBpmDefinitionLink(entity.defId)}】" +
						"</#if>" +
						"节点"+
						"<#if !StringUtil.isEmpty(entity.nodeName)>" +
							"【${entity.nodeName}】" +
						"</#if>" +
						"的变量:【 ${entity.varName}】、" +
				   "</#list>"
	)
	public void del(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ResultMessage resultMessage = null;
		String preUrl = RequestUtil.getPrePage(request);
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "varId");
			bpmDefVarService.delByIds(lAryId);
			resultMessage = new ResultMessage(ResultMessage.Success, "删除流程变量成功!");
		} catch (Exception ex) {
			resultMessage = new ResultMessage(ResultMessage.Fail, "流程变量删除失败:"+ex.getMessage());
		}
		addMessage(resultMessage, request);
		response.sendRedirect(preUrl);
	}

	@RequestMapping("getVars")
	@ResponseBody
	public List<BpmDefVar> getVars(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long defId = RequestUtil.getLong(request, "defId");
		List<BpmDefVar> list = bpmDefVarService.getVarsByFlowDefId(defId);
		return list;
	}

	@RequestMapping("edit")
	@Action(description = "编辑流程变量定义")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		Long defId = RequestUtil.getLong(request, "defId");
		Long varId = RequestUtil.getLong(request, "varId");
		BpmDefinition bpmDefinition = bpmDefinitionService.getById(defId);
		String actDefId = bpmDefinition.getActDefId();
		Long actDeployId = bpmDefinition.getActDeployId();

		String returnUrl = RequestUtil.getPrePage(request);
		BpmDefVar bpmDefVar = null;
		if (varId != 0) {
			bpmDefVar = bpmDefVarService.getById(varId);
		} else {
			bpmDefVar = new BpmDefVar();
		}
		Map<String, String> nodeMap = bpmService.getExecuteNodesMap(actDefId,
				true);
		return getAutoView().addObject("bpmDefVar", bpmDefVar)
				.addObject("returnUrl", returnUrl).addObject("defId", defId)
				.addObject("nodeMap", nodeMap)
				.addObject("actDeployId", actDeployId)
				.addObject("actDefId", actDefId);
	}

	/**
	 * 取得流程变量定义明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description = "查看流程变量定义明细")
	public ModelAndView get(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String actDefId = RequestUtil.getString(request, "actDefId");
		long id = RequestUtil.getLong(request, "varId");
		BpmDefVar bpmDefVar = bpmDefVarService.getById(id);
		return getAutoView().addObject("bpmDefVar", bpmDefVar).addObject(
				"actDefId", actDefId);
	}

	/**
	 * 根据流程的deployId和节点ID获取流程变量。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getByDeployNode")
	public ModelAndView getByDeployNode(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String deployId = RequestUtil.getString(request, "deployId");
		String nodeId = RequestUtil.getString(request, "nodeId");
		List<BpmDefVar> varList = null;
		Long defId = RequestUtil.getLong(request, "defId");
		if (defId != 0) {
			varList = bpmDefVarService.getVarsByFlowDefId(defId);
		} else {
			varList = bpmDefVarService.getByDeployAndNode(deployId, nodeId);
		}
		return getAutoView().addObject("bpmDefVarList", varList);
	}

	/**
	 * 获取流程变量树
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getTree")
	@ResponseBody
	public String getTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long defId = RequestUtil.getLong(request, "defId");
		JSONArray jarray = new JSONArray();
		List<BpmDefVar> list = bpmDefVarService.getVarsByFlowDefId(defId);
		if(BeanUtils.isNotEmpty(list)){
			JSONObject jobject = new JSONObject().accumulate("name", "自定义变量")
												 .accumulate("children", convertList2Json(list));
			jarray.add(jobject);
		}
		
		List<BpmDefVar> list2 = getFormVars(defId);
		if(BeanUtils.isNotEmpty(list2)){
			JSONObject jobject = new JSONObject().accumulate("name", "表单变量")
												 .accumulate("children", convertList2Json(list2));
			jarray.add(jobject);
		}
		return jarray.toString();
	}
	
	private JSONArray convertList2Json(List<BpmDefVar> list){
		JSONArray jarray = new JSONArray();
		String name;
		JSONObject jobject;
		for (BpmDefVar bpmDefVar : list) {
			name = bpmDefVar.getVarName();
			if (StringUtil.isNotEmpty(bpmDefVar.getVarDataType())) {
				name += "(" + bpmDefVar.getVarDataType() + ")";
			}
			jobject = new JSONObject().accumulate("id", bpmDefVar.getVarId())
									  .accumulate("varName", bpmDefVar.getVarName())
									  .accumulate("varKey", bpmDefVar.getVarKey())
									  .accumulate("type", bpmDefVar.getVarDataType())
									  .accumulate("name", name);
			jarray.add(jobject);
		}
		return jarray;
	}
	
	/**
	 * 获取表单变量
	 * @param defId
	 * @return
	 */
	private List<BpmDefVar> getFormVars(Long defId){
		List<BpmFormField> fieldList= bpmFormFieldService.getFlowVarByFlowDefId(defId);
		List<BpmDefVar> list = new ArrayList<BpmDefVar>();
		
		for(BpmFormField bpmFormField:fieldList){
			BpmDefVar bpmDefVar = new BpmDefVar();
			bpmDefVar.setVarId(bpmFormField.getFieldId());
			bpmDefVar.setVarName(bpmFormField.getFieldDesc());
			bpmDefVar.setVarKey(bpmFormField.getFieldName());
			bpmDefVar.setVarDataType(bpmFormField.getFieldType());
			list.add(bpmDefVar);
		}
		return list;
	}

}
