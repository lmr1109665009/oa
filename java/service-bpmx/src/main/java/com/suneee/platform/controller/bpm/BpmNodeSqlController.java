package com.suneee.platform.controller.bpm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.util.*;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.constant.HttpConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.MapUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.BpmNodeSet;
import com.suneee.platform.model.bpm.BpmNodeSql;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmNodeSetService;
import com.suneee.platform.service.bpm.BpmNodeSqlService;
import com.suneee.platform.service.form.BpmFormTableService;

/**
 * <pre>
 * 对象功能:bpm_node_sql 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-03-05 09:59:30
 * </pre>
 */
@Controller
@RequestMapping("/platform/bpm/bpmNodeSql/")
public class BpmNodeSqlController extends BaseController {
	@Resource
	private BpmNodeSqlService bpmNodeSqlService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private BpmNodeSetService bpmNodeSetService;

	/**
	 * 添加或更新bpm_node_sql。
	 * 
	 * @param request
	 * @param response
	 * @param bpmNodeSql
	 *            添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "添加或更新bpm_node_sql")
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType(HttpConstant.CONTENT_TYPE_JSON);
		String resultMsg = null;
		String json = FileUtil.inputStream2String(request.getInputStream());
		BpmNodeSql bpmNodeSql = JSONObjectUtil.toBean(json, BpmNodeSql.class);
		try {
			if (bpmNodeSql.getId() == null || bpmNodeSql.getId() == 0) {
				bpmNodeSql.setId(UniqueIdUtil.genId());
				bpmNodeSqlService.add(bpmNodeSql);
				resultMsg = getText("添加成功", "bpm_node_sql");
			} else {
				bpmNodeSqlService.update(bpmNodeSql);
				resultMsg = getText("更新成功", "bpm_node_sql");
			}
			writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
		} catch (Exception e) {
			writeResultMessage(response.getWriter(), resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}
	}

	/**
	 * 取得bpm_node_sql分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看bpm_node_sql分页列表")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter queryFilter = new QueryFilter(request, "bpmNodeSqlItem");
		Long defId = RequestUtil.getLong(request, "defId");
		BpmDefinition bpmDefinition = bpmDefinitionService.getById(defId);
		String actDefId=bpmDefinition.getActDefId();
		queryFilter.addFilter("actdefId", actDefId);
		List<BpmNodeSql> list = bpmNodeSqlService.getAll(queryFilter);

		// 获取nodeName信息，为了展现通俗易懂点
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		for (BpmNodeSql bnq : list) {
			Map<String, Object> map = MapUtil.transBean2Map(bnq);
			FlowNode flowNode= NodeCache.getNodeByActNodeId(actDefId, bnq.getNodeId());
			map.put("nodeName", flowNode.getNodeName());
			
			mapList.add(map);
		}

		ModelAndView mv = this.getAutoView().addObject("bpmNodeSqlList", mapList);
		return mv;
	}

	/**
	 * 删除bpm_node_sql
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除bpm_node_sql")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			bpmNodeSqlService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除bpm_node_sql成功!");
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	/**
	 * 编辑bpm_node_sql
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description = "编辑bpm_node_sql")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		String returnUrl = RequestUtil.getPrePage(request);
		return getAutoView().addObject("returnUrl", returnUrl);
	}

	/**
	 * 取得bpm_node_sql明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description = "查看bpm_node_sql明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		BpmNodeSql bpmNodeSql = bpmNodeSqlService.getById(id);
		return getAutoView().addObject("bpmNodeSql", bpmNodeSql);
	}

	@RequestMapping("getObject")
	@Action(description = "按各种参数查询bpmNodeSql")
	@ResponseBody
	public BpmNodeSql getObject(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id", null);
		if (id != null) {
			return bpmNodeSqlService.getById(id);
		}
		String nodeId = RequestUtil.getString(request, "nodeId", "");
		String actdefId = RequestUtil.getString(request, "actdefId", "");
		if (StringUtil.isNotEmpty(actdefId) && StringUtil.isNotEmpty(nodeId)) {
			return bpmNodeSqlService.getByNodeIdAndActdefId(nodeId, actdefId);
		}

		return null;
	}

	// 获取表，主要是为了主表字段
	@RequestMapping("getTable")
	@ResponseBody
	public Object getTable(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			String actdefId = RequestUtil.getString(request, "actdefId", "");
			Long id = RequestUtil.getLong(request, "id", null);

			if (StringUtil.isEmpty(actdefId)) {
				actdefId = bpmNodeSqlService.getById(id).getActdefId();
			}
			BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(actdefId);
			BpmFormTable bpmFormTable = AppUtil.getBean(BpmFormTableService.class).getByDefId(bpmDefinition.getDefId(),"");
			return bpmFormTable;
		}
		catch(Exception ex){
			return new  ResultMessage(ResultMessage.Fail,ex.getMessage());
		}
	}

	// 获取节点信息
	@RequestMapping("getNodeType")
	@ResponseBody
	public Object getNodeType(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String nodeId = RequestUtil.getString(request, "nodeId", "");
		String actdefId = RequestUtil.getString(request, "actdefId", "");
		FlowNode flowNode = NodeCache.getByActDefId(actdefId).get(nodeId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("nodeType", flowNode.getNodeType());
		return map;
	}
}
