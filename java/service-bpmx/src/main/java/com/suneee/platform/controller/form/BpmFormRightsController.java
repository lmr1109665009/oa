package com.suneee.platform.controller.form;

import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.BpmNodeSet;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.bpm.BpmNodeSetService;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.form.BpmFormRightsService;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.ucp.base.vo.ResultVo;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/platform/form/bpmFormRights/")
@Action(ownermodel = SysAuditModelType.FORM_MANAGEMENT)
public class BpmFormRightsController extends BaseController {

	@Resource
	private BpmFormRightsService bpmFormRightsService;
	@Resource
	private BpmNodeSetService bpmNodeSetService;
	@Resource
	private BpmFormDefService bpmFormDefService;
	@Resource
	private BpmFormTableService bpmFormTableService;

	@RequestMapping("getJson")
	public void getJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String actDefId = RequestUtil.getString(request, "actDefId");
		String nodeId = RequestUtil.getString(request, "nodeId");
		String formKey = RequestUtil.getString(request, "formKey");
		String parentActDefId = RequestUtil.getString(request, "parentActDefId", "");
		BpmFormDef formDef = bpmFormDefService.getDefaultPublishedByFormKey(formKey);
		BpmFormTable formTable = bpmFormTableService.getTableById(formDef.getTableId(), 1);
		Map<String, List<JSONObject>> permission = bpmFormRightsService.getByFormKeyAndUserId(formKey, formTable,
				actDefId, nodeId, parentActDefId, false);
		response.getWriter().println(permission);
	}

	@ResponseBody
	@RequestMapping("getPermissionSetting")
	public Map<String, List<JSONObject>> getPermissionByFormNode(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String actDefId = RequestUtil.getString(request, "actDefId");
		String nodeId = RequestUtil.getString(request, "nodeId");
		String formKey = RequestUtil.getString(request, "formKey");
		String parentActDefId = RequestUtil.getString(request, "parentActDefId", "");
		Map<String, List<JSONObject>> permission  =  bpmFormRightsService.getPermission(formKey, actDefId, nodeId,parentActDefId,false);
		return permission;
	}

	/**
	 * 保存表单权限。
	 * 
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("savePermission")
	@Action(description = "保存表单权限", detail = "保存表单【${SysAuditLinkService.getBpmFormDefLink(formKey)}】的表单权限")
	@ResponseBody
	public ResultVo savePermission(HttpServletRequest request) throws Exception {
		String permission = request.getParameter("permission");
		String actDefId = RequestUtil.getString(request, "actDefId");
		String nodeId = RequestUtil.getString(request, "nodeId");
		String formKey = RequestUtil.getString(request, "formKey");
		String parentActDefId = RequestUtil.getString(request, "parentActDefId", "");
		JSONObject jsonObject = JSONObject.fromObject(permission);
		ResultVo resultMessage = null;
		try {
			if (StringUtil.isNotEmpty(nodeId)) {
				// 设置节点权限。
				bpmFormRightsService.save(actDefId, nodeId, formKey, jsonObject, parentActDefId);
			}else if(StringUtil.isNotEmpty(actDefId)){
				// 设置流程全局权限。
				//String actDefId, String nodeId,String formKey,JSONObject permission,  String parentActDefId
				bpmFormRightsService.save(actDefId, null, formKey, jsonObject, parentActDefId);
			}else {
				// 根据表单key保存权限。
				bpmFormRightsService.save(formKey, jsonObject);
			}

			resultMessage = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,
					"表单权限保存成功!");
		} catch (Exception ex) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				resultMessage = new ResultVo(ResultVo.COMMON_STATUS_FAILED,
						"表单权限保存失败:" + str);
			} else {
				String message = ExceptionUtil.getExceptionMessage(ex);
				resultMessage = new ResultVo(ResultVo.COMMON_STATUS_FAILED, message);
			}
		}
		return resultMessage;
	}

	/**
	 * 初始化表单权限设置
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("initRights")
	@Action(description = "初始化表单权限设置", detail = "初始化表单【${SysAuditLinkService.getBpmFormDefLink(formkey)}】权限设置")
	public ResultMessage initRights(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultMessage resultMessage = null;
		try {
			String formKey = RequestUtil.getString(request, "formKey");
			String actDefId = RequestUtil.getString(request, "actDefId");
			String nodeId = RequestUtil.getString(request, "nodeId");
			String parentActDefId = RequestUtil.getString(request, "parentActDefId", "");

			bpmFormRightsService.delFormRights(formKey, actDefId, nodeId, parentActDefId);
			resultMessage = new ResultMessage(ResultMessage.Success, "重置表单权限设置成功!");
		} catch (Exception ex) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				resultMessage = new ResultMessage(ResultMessage.Fail, "重置表单权限设置失败:" + str);
				response.getWriter().print(resultMessage);
			} else {
				String message = ExceptionUtil.getExceptionMessage(ex);
				resultMessage = new ResultMessage(ResultMessage.Fail, message);
				response.getWriter().print(resultMessage);
			}
		}
		return resultMessage;
	}

	/**
	 * 流程表单授权
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("dialog")
	@Action(description = "流程表单授权")
	public ModelAndView rightsDialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String actDefId = RequestUtil.getString(request, "actDefId");
		Long defId = RequestUtil.getLong(request, "defId");
		String nodeId = RequestUtil.getString(request, "nodeId");
		String formKey = RequestUtil.getString(request, "formKey");
		String parentActDefId = RequestUtil.getString(request, "parentActDefId", "");

		boolean isNodeRights = false;

		ModelAndView mv = this.getAutoView();
		// 是否针对流程节点授权。
		if (StringUtil.isNotEmpty(nodeId)) {
			List<BpmNodeSet> bpmNodeSetList = new ArrayList<BpmNodeSet>();
			if (StringUtil.isEmpty(parentActDefId)) {
				bpmNodeSetList = bpmNodeSetService.getByDefId(defId);
			} else {
				bpmNodeSetList = bpmNodeSetService.getByDefId(defId, parentActDefId);
			}

			mv.addObject("bpmNodeSetList", bpmNodeSetList);
			mv.addObject("nodeId", nodeId);
			isNodeRights = true;
		}
		mv.addObject("formKey", formKey).addObject("actDefId", actDefId).addObject("isNodeRights", isNodeRights)
				.addObject("parentActDefId", parentActDefId);
		return mv;
	}

}
