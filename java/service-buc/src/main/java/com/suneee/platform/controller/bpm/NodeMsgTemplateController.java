package com.suneee.platform.controller.bpm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.MapUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.platform.annotion.Action;
import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.table.TableModel;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.MapUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.BpmNodeSet;
import com.suneee.platform.model.bpm.NodeMsgTemplate;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmNodeSetService;
import com.suneee.platform.service.bpm.NodeMsgTemplateService;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.form.BpmFormTableService;

/**
 * <pre>
 * 对象功能:bpm_nodemsg_template 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-05-27 18:03:14
 * </pre>
 */
@Controller
@RequestMapping("/platform/bpm/nodeMsgTemplate/")
public class NodeMsgTemplateController extends BaseController {
	@Resource
	private NodeMsgTemplateService nodeMsgTemplateService;
	@Resource
	private BpmFormDefService bpmFormDefService;
	@Resource
	private BpmFormTableService bpmFormTableService;
	@Resource
	private FreemarkEngine freemarkEngine;
	@Resource
	private BpmNodeSetService bpmNodeSetService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;

	/**
	 * 添加或更新bpm_nodemsg_template。
	 * 
	 * @param request
	 * @param response
	 * @param nodeMsgTemplate
	 *            添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "添加或更新bpm_nodemsg_template")
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String resultMsg = null;
		String json = FileUtil.inputStream2String(request.getInputStream());
		NodeMsgTemplate nodeMsgTemplate = JSONObjectUtil.toBean(json, NodeMsgTemplate.class);
		try {
			if (nodeMsgTemplate.getId() == null || nodeMsgTemplate.getId() == 0) {
				resultMsg = getText("添加成功", "bpm_nodemsg_template");
			} else {
				resultMsg = getText("更新成功", "bpm_nodemsg_template");
			}
			nodeMsgTemplateService.save(nodeMsgTemplate);
			writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
		} catch (Exception e) {
			writeResultMessage(response.getWriter(), resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}
	}

	/**
	 * 取得bpm_nodemsg_template分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看bpm_nodemsg_template分页列表")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<NodeMsgTemplate> list = nodeMsgTemplateService.getAll(new QueryFilter(request, "nodeMsgTemplateItem"));
		
		// 获取nodeName信息，为了展现通俗易懂点
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		for (NodeMsgTemplate nmt : list) {
			Map<String, Object> map = MapUtil.transBean2Map(nmt);
			BpmDefinition bpmDefinition = bpmDefinitionService.getById(nmt.getDefId());
			FlowNode flowNode= NodeCache.getNodeByActNodeId(bpmDefinition.getActDefId(),  nmt.getNodeId());
			map.put("nodeName", flowNode.getNodeName());
			
			mapList.add(map);
		}
		return this.getAutoView().addObject("nodeMsgTemplateList", mapList);
	}

	/**
	 * 删除bpm_nodemsg_template
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除bpm_nodemsg_template")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			nodeMsgTemplateService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除bpm_nodemsg_template成功!");
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	@RequestMapping("ngjs")
	@Action(description = "ngjs的请求，分为参数和action，action是说明这次请求的目的")
	@ResponseBody
	public Object ngjs(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String action = RequestUtil.getString(request, "action");
		if (action.equals("getTableByDefId")) {// 获取流程表
			Long defId = RequestUtil.getLong(request, "defId");
			return bpmFormTableService.getByDefId(defId,"");
		}
		if (action.equals("getObject")) {// 初始化对象
			Long defId = RequestUtil.getLong(request, "defId", null);
			String nodeId = RequestUtil.getString(request, "nodeId", null);
			Long parentDefId = RequestUtil.getLong(request, "parentDefId", null);
			return nodeMsgTemplateService.getObject(nodeId, defId, parentDefId);
		}
		if (action.equals("getById")) {
			Long id = RequestUtil.getLong(request, "id");
			return nodeMsgTemplateService.getById(id);
		}
		if (action.equals("getInitTemp")) {// 获取初始化html 是通过模板生成的
			Long defId = RequestUtil.getLong(request, "defId");
			int columnNum = RequestUtil.getInt(request, "columnNum", 1);
			Map<String, Object> map = new HashMap<String, Object>();
			BpmFormTable table = bpmFormTableService.getByDefId(defId,"");
			map.put("table", table);
			map.put("columnNum", columnNum);
			String html = freemarkEngine.mergeTemplateIntoString("msg/nodeMsgTemp_html.ftl", map);
			String text = freemarkEngine.mergeTemplateIntoString("msg/nodeMsgTemp_text.ftl", map);
			JSONObject json = new JSONObject();
			json.put("html", html);
			json.put("text", text);
			return json;
		}
		if (action.equals("getSubTableTemp")) {// 返回这张子表的所有模板
			Long defId = RequestUtil.getLong(request, "defId");
			BpmFormTable table = bpmFormTableService.getByDefId(defId,"");
			JSONObject json = new JSONObject();
			for (BpmFormTable subTable : table.getSubTableList()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("subTable", subTable);
				map.put("table", table);
				String output = freemarkEngine.mergeTemplateIntoString("msg/nodeMsgTemp_subTable.ftl", map);
				json.put(subTable.getTableName(), output);
			}
			return json;
		}
		if (action.equals("getNode")) {// 获取节点信息
			String nodeId = RequestUtil.getString(request, "nodeId", null);
			Long defId = RequestUtil.getLong(request, "defId");
			BpmDefinition bpmDefinition = bpmDefinitionService.getById(defId);
			FlowNode flowNode=NodeCache.getNodeByActNodeId(bpmDefinition.getActDefId(), nodeId);
			JSONObject json = new JSONObject();
			json.put("nodeName", flowNode.getNodeName());
			
			return json;
		}

		return null;
	}

}
