package com.suneee.platform.controller.form;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.db.datasource.DbContextHolder;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageList;
import com.suneee.core.table.BaseTableMeta;
import com.suneee.core.table.IDbView;
import com.suneee.core.table.TableModel;
import com.suneee.core.table.impl.TableMetaFactory;
import com.suneee.core.util.*;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.form.BpmFormDialog;
import com.suneee.platform.model.form.BpmFormDialogCombinate;
import com.suneee.platform.model.form.DialogField;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysDataSource;
import com.suneee.platform.service.form.BpmFormDialogCombinateService;
import com.suneee.platform.service.form.BpmFormDialogService;
import com.suneee.platform.service.system.SysDataSourceService;
import com.suneee.platform.xml.util.MsgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 对象功能:通用表单对话框 控制器类 开发公司:广州宏天软件有限公司 开发人员:ray 创建时间:2012-06-25 11:05:09
 */
@Controller
@RequestMapping("/platform/form/bpmFormDialog/")
@Action(ownermodel = SysAuditModelType.FORM_MANAGEMENT)
public class BpmFormDialogController extends BaseController {
	protected Logger logger = LoggerFactory.getLogger(BpmFormDialogController.class);
	@Resource
	private BpmFormDialogService bpmFormDialogService;
	@Resource
	private BpmFormDialogCombinateService bpmFormDialogCombinateService;

	@Resource
	private FreemarkEngine freemarkEngine;

	private static String DEFAULT_ORDER_SEQ = "DESC";

	/**
	 * 取得通用表单对话框分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看通用表单对话框分页列表")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<BpmFormDialog> list = bpmFormDialogService.getAll(new QueryFilter(request, "bpmFormDialogItem"));
		ModelAndView mv = this.getAutoView().addObject("bpmFormDialogList", list);
		
		return mv;
	}

	/**
	 * 删除通用表单对话框
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除通用表单对话框", execOrder = ActionExecOrder.BEFORE, detail = "删除通用表单对话框" + "<#list StringUtils.split(id,\",\") as item>" + "<#assign entity=bpmFormDialogService.getById(Long.valueOf(item))/>" + "【${entity.name}】" + "</#list>")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			bpmFormDialogService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除通用表单对话框成功!");
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败:" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	@RequestMapping("edit")
	@Action(description = "编辑通用表单对话框")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		String returnUrl = RequestUtil.getPrePage(request);
		BpmFormDialog bpmFormDialog = null;
		if (id != 0) {
			bpmFormDialog = bpmFormDialogService.getById(id);
		} else {
			bpmFormDialog = new BpmFormDialog();
		}
		List<SysDataSource> dsList = AppUtil.getBean(SysDataSourceService.class).getAll();

		return getAutoView().addObject("bpmFormDialog", bpmFormDialog).addObject("returnUrl", returnUrl).addObject("dsList", dsList);
	}

	/**
	 * 取得通用表单对话框明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description = "查看通用表单对话框明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long id = RequestUtil.getLong(request, "id");
		long canReturn = RequestUtil.getLong(request, "canReturn", 0);
		BpmFormDialog bpmFormDialog = bpmFormDialogService.getById(id);
		return getAutoView().addObject("bpmFormDialog", bpmFormDialog).addObject("canReturn", canReturn);
	}

	@RequestMapping("dialogObj")
	@Action(description = "查看通用表单对话框明细")
	@ResponseBody
	public Map<String, Object> dialogObj(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String alias = RequestUtil.getString(request, "alias");
		Map<String, Object> map = new HashMap<String, Object>();
		BpmFormDialog bpmFormDialog = bpmFormDialogService.getByAlias(alias);
		
		if (bpmFormDialog != null) {
			JSONObject jsonObject= convertToJson(bpmFormDialog);
			map.put("bpmFormDialog", jsonObject);
			map.put("success", 1);
		} else {
			map.put("success", 0);
		}

		return map;
	}
	
	private JSONObject convertToJson(BpmFormDialog dialog){
		JSONObject object=new JSONObject();
		object.put ("name", dialog.getName());
		object.put("width", dialog.getWidth());
		object.put("height", dialog.getHeight());
		String resultfield=dialog.getResultfield();
		String conditionfield=dialog.getConditionfield();
		String displayfield=dialog.getDisplayfield();
		resultfield=resultfield.replace("\\", "");
		conditionfield=conditionfield.replace("\\", "");
		displayfield=displayfield.replace("\\", "");
		object.put("resultfield", resultfield);
		object.put("conditionfield", conditionfield);
		object.put("displayfield", displayfield);
		
		
		return object;
	}

	/**
	 * 根据数据源，输入的对象类型，对象名称获取对象列表。
	 * 
	 * <pre>
	 *  1.对象类型为表。
	 *  	返回表的map对象。
	 *  2.对象为视图
	 *  	返回视图列表对象。
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("getByDsObjectName")
	@Action(description = "根据对象名称对象类型")
	@ResponseBody
	public Map getByDsObjectName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String dsName = RequestUtil.getString(request, "dsName");
		String objectName = RequestUtil.getString(request, "objectName");
		int istable = RequestUtil.getInt(request, "istable");
		Map map = new HashMap();
		try {
			if (istable == 1) {
				BaseTableMeta meta = TableMetaFactory.getMetaData(dsName);
				Map<String, String> tableMap = meta.getTablesByName(objectName);
				map.put("tables", tableMap);
			} else {
				IDbView dbView = TableMetaFactory.getDbView(dsName);
				List<String> views = dbView.getViews(objectName);
				map.put("views", views);
			}
			DbContextHolder.clearDataSource();
			map.put("success", "true");
		} catch (Exception ex) {
			logger.info("getByDsObjectName:" + ex.getMessage());
			ex.printStackTrace();
			map.put("success", "false");
		}
		return map;
	}

	/**
	 * 取得表或者视图的元数据对象。
	 * 
	 * <pre>
	 * 	根据数据源，对象名称，是否视图获取表或者视图的元数据对象。
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("getObjectByDsObjectName")
	@Action(description = "取得表或者视图的元数据对象")
	@ResponseBody
	public Map getObjectByDsObjectName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String dsName = RequestUtil.getString(request, "dsName");
		String objectName = RequestUtil.getString(request, "objectName");
		int istable = RequestUtil.getInt(request, "istable");
		Map map = new HashMap();
		TableModel tableModel;
		try {
			// 加载表
			if (istable == 1) {
				BaseTableMeta meta = TableMetaFactory.getMetaData(dsName);
				tableModel = meta.getTableByName(objectName);
			} else {
				IDbView dbView = TableMetaFactory.getDbView(dsName);
				tableModel = dbView.getModelByViewName(objectName);
			}
			map.put("tableModel", tableModel);
			map.put("success", "true");
		} catch (Exception ex) {
			map.put("success", "false");
		}
		return map;
	}

	/**
	 * 设置字段对话框。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("setting")
	public ModelAndView setting(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long id = RequestUtil.getLong(request, "id");
		String dsName = "";
		String objectName = "";
		int istable = 0;
		int style = 0;
		ModelAndView mv = this.getAutoView();
		if (id == 0) {
			dsName = RequestUtil.getString(request, "dsName");
			objectName = RequestUtil.getString(request, "objectName");
			istable = RequestUtil.getInt(request, "istable");
			style = RequestUtil.getInt(request, "style");
		} else {
			BpmFormDialog bpmFormDialog = bpmFormDialogService.getById(id);
			dsName = bpmFormDialog.getDsalias();
			objectName = bpmFormDialog.getObjname();
			istable = bpmFormDialog.getIstable();
			style = bpmFormDialog.getStyle();
			mv.addObject("bpmFormDialog", bpmFormDialog);
		}

		TableModel tableModel;
		// 表
		if (istable == 1) {
			BaseTableMeta meta = TableMetaFactory.getMetaData(dsName);
			tableModel = meta.getTableByName(objectName);
		}
		// 视图处理
		else {
			IDbView dbView = TableMetaFactory.getDbView(dsName);
			tableModel = dbView.getModelByViewName(objectName);
		}

		mv.addObject("tableModel", tableModel).addObject("style", style);

		return mv;
	}

	/**
	 * 取得树形数据。
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getTreeData")
	@ResponseBody
	public List getTreeData(HttpServletRequest request) throws Exception {
		List list;

		String alias = RequestUtil.getString(request, "alias");
		String pvalue = RequestUtil.getString(request, "idKey");
		String pname = RequestUtil.getString(request, "pidKeyName");
		int isRoot = RequestUtil.getInt(request, "isRoot");
		Map<String, Object> params = new HashMap<String, Object>();
		if (isRoot == 1) {
			params = RequestUtil.getQueryMap(request);
			list = bpmFormDialogService.getTreeData(alias, params, true);
		} else {
			params.put("pname", pname);
			params.put("pvalue", pvalue);
			params.put(pname, pvalue);
			list = bpmFormDialogService.getTreeData(alias, params, false);
		}

		for (Object obj : list) {
			Map<String, Object> map = (Map<String, Object>) obj;
			Object isParent = map.get("ISPARENT");
			if (isParent != null) {
				if ("true".equals(isParent.toString())) {
					map.put("isParent", true);
				} else {
					map.put("isParent", false);
				}
			}
		}

		return list;

	}

	/**
	 * 选择自定义对话框
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getAllDialogs")
	@ResponseBody
	public List<BpmFormDialog> getAllDialogs(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<BpmFormDialog> list = bpmFormDialogService.getAll();
		return list;
	}

	/**
	 * 显示对话框。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("showFrame")
	public ModelAndView showFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map paramsMap = RequestUtil.getQueryMap(request);
		String nextUrl = RequestUtil.getUrl(request);
		String urlPara = request.getQueryString();
		
		String alias = RequestUtil.getString(request, "dialog_alias_");
		BpmFormDialog bpmFormDialog =bpmFormDialogService.getByAlias(alias);
		
		//增加一个按照id预览。by liyj
		if(bpmFormDialog==null){
			Long id =  RequestUtil.getLong(request, "id");
			bpmFormDialog =bpmFormDialogService.getById(id);
		}
		
//		DbContextHolder.setDataSource(bpmFormDialog.getDsalias()); 
		
		bpmFormDialog = bpmFormDialogService.getData(bpmFormDialog, paramsMap);
		
		ModelAndView mv = this.getAutoView();
		mv.addObject("bpmFormDialog", bpmFormDialog);
		// 需要排序
		if (bpmFormDialog.getStyle() == 0){
			String sortField = RequestUtil.getString(request, "sortField");
			String orderSeq = RequestUtil.getString(request, "orderSeq", DEFAULT_ORDER_SEQ);
			String newSortField = RequestUtil.getString(request, "newSortField");
			if (StringUtil.isNotEmpty(sortField)) {
				paramsMap.put("sortField", sortField);
				paramsMap.put("orderSeq", orderSeq);
			}
			if (StringUtil.isEmpty(sortField)) {
				DialogField dialogField;
				if (BeanUtils.isNotEmpty(bpmFormDialog.getSortList())) {
					dialogField = bpmFormDialog.getSortList().get(0);
					sortField = dialogField.getFieldName();
					orderSeq = dialogField.getComment();
				} else {
					dialogField = bpmFormDialog.getDisplayList().get(0);
					sortField = dialogField.getFieldName();
				}
			}

			if (newSortField.equals(sortField)) {
				if (orderSeq.equals("ASC")) {
					orderSeq = "DESC";
				} else {
					orderSeq = "ASC";
				}
			}
			if (!StringUtil.isEmpty(newSortField)) {
				sortField = newSortField;
			}

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("sortField", StringUtil.isEmpty(newSortField) ? sortField : newSortField);
			parameters.put("newSortField", null);
			parameters.put("orderSeq", orderSeq);
			nextUrl = addParametersToUrl(nextUrl, parameters);

			mv.addObject("sortField", sortField);
			mv.addObject("orderSeq", orderSeq);
			mv.addObject("baseHref", nextUrl);
			// 需要分页
			if (bpmFormDialog.getNeedpage() == 1) {
				PageBean pageBean = bpmFormDialog.getPageBean();
				Map pageModel = new HashMap();
				pageModel.put("tableIdCode", "");
				pageModel.put("pageBean", pageBean);
				pageModel.put("showExplain", true);
				pageModel.put("showPageSize", true);
				pageModel.put("baseHref", nextUrl);
				String pageHtml = freemarkEngine.mergeTemplateIntoString("page.ftl", pageModel);
				mv.addObject("pageHtml", pageHtml);
			}
			mv.addObject("paramsMap", paramsMap);
		} else {
			// 树 对话框
			mv.addObject("urlPara", urlPara);
		}
		return mv;
	}

	/**
	 * 手机端自定义对话框
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("mobileCustom")
	public ModelAndView mobileDialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = getAutoView();
		Boolean isCombine =RequestUtil.getBoolean(request, "isCombine",false);
		String alias = RequestUtil.getString(request, "dialog_alias_");
		try {
			if(isCombine){
				BpmFormDialogCombinate combine = bpmFormDialogCombinateService.getByAlias(alias);
				mv.addObject("combineField",combine.getField());
				mv.addObject("treeDialog", combine.getTreeDialog());
				mv.addObject("listDialog", combine.getListDialog());
				mv.addObject("returnField", combine.getListDialog().getResultfield());
				mv.addObject("isSingle", combine.getListDialog().getIssingle()==1);
			}else{
				BpmFormDialog bpmFormDialog =bpmFormDialogService.getByAlias(alias);
				Boolean isTree = bpmFormDialog.getStyle()==1;
				String dialogName = isTree ? "treeDialog":"listDialog";
				mv.addObject(dialogName, bpmFormDialog);
				mv.addObject("returnField", bpmFormDialog.getResultfield());
				mv.addObject("isSingle", bpmFormDialog.getIssingle()==1);
				if(isTree){
					String disPlayName= JSONObject.parseObject(bpmFormDialog.getDisplayfield()).getString("displayName");
					mv.addObject("displayName",disPlayName);
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		mv.addObject("isCombine", isCombine);
		return mv;
	}
	
	@RequestMapping("getDialogData")
	@ResponseBody
	public JSONObject getDialogData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map paramsMap = RequestUtil.getQueryMap(request);
		String alias = RequestUtil.getString(request, "dialog_alias_");
		BpmFormDialog bpmFormDialog = bpmFormDialogService.getByAlias(alias);
		bpmFormDialogService.getData(bpmFormDialog, paramsMap);

		return getPageList(bpmFormDialog.getList(),request);
	}
	
	private JSONObject getPageList(List list, HttpServletRequest request) {
		if(list instanceof PageList){
			PageList pageList = (PageList) list;
			JSONObject pageJson = new JSONObject();
			PageBean page = pageList.getPageBean();
			pageJson.put("p", page.getCurrentPage());
			pageJson.put("count", page.getTotalCount());
			pageJson.put("pageCount", page.getTotalPage());
			pageJson.put("z", page.getPageSize());
			// 查询的字段
			String _queryName = RequestUtil.getString(request, "_queryName");
			if(StringUtil.isNotEmpty(_queryName)){
				pageJson.put("_queryName",_queryName);
				pageJson.put("_queryData", RequestUtil.getString(request,_queryName));
			}
			
			JSONObject data = new JSONObject();
			data.put("list", list);
			data.put("pageParam", pageJson);
			return data;
		}
		else throw new RuntimeException("必须是PageList!");
	}
	
	private static Map<String, Object> getQueryStringMap(String url) {
		Map<String, Object> map = new HashMap<String, Object>();
		int idx1 = url.indexOf("?");
		if (idx1 > 0) {
			String queryStr = url.substring(idx1 + 1);
			String[] queryNodeAry = queryStr.split("&");
			for (String queryNode : queryNodeAry) {
				String[] strAry = queryNode.split("=");
				if (strAry.length >= 2)
					map.put(strAry[0].trim(), strAry[1]);
			}
		}
		return map;
	}

	private static String addParametersToUrl(String url, Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		int idx1 = url.indexOf("?");
		if (idx1 > 0) {
			sb.append(url.substring(0, idx1));
		}
		sb.append("?");

		Map<String, Object> map = getQueryStringMap(url);
		map.putAll(params);

		for (Entry<String, Object> entry : map.entrySet()) {
			if (BeanUtils.isEmpty(entry.getValue()))
				continue;
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
			sb.append("&");
		}
		return sb.substring(0, sb.length() - 1);
	}
	
	/**
	 * 导出选择导出xml
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("export")
	@Action(description = "导出选择导出xml")
	public ModelAndView export(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String tableIds = RequestUtil.getString(request, "tableIds");
		ModelAndView mv = this.getAutoView();
		mv.addObject("tableIds", tableIds);
		return mv;
	}
	
	/**
	 * 导出自定义对话框xml。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("exportXml")
	@Action(description = "导出自定义对话框", detail = "导出自定义对话框:" + "<#list StringUtils.split(tableIds,\",\") as item>" + "<#assign entity=bpmFormTableService.getById(Long.valueOf(item))/>" + "【${entity.tableDesc}(${entity.tableName})】" + "</#list>")
	public void exportXml(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String strXml = null;
		String fileName = null;
		Long[] tableIds = RequestUtil.getLongAryByStr(request, "tableIds");
		List<BpmFormDialog> list = bpmFormDialogService.getAll();
		try {
			if (BeanUtils.isEmpty(tableIds)) {
				if (BeanUtils.isNotEmpty(list)) {
					strXml = bpmFormDialogService.exportXml(list);
					fileName = "全部自定义对话框记录_"
							+ DateFormatUtil.getNowByString("yyyyMMddHHmmdd")
							+ ".xml";
				}
			} else {
				strXml = bpmFormDialogService.exportXml(tableIds);
				fileName = DateFormatUtil.getNowByString("yyyyMMddHHmmdd")
						+ ".xml";
				if (tableIds.length == 1) {
					BpmFormDialog bpmFormDialog = bpmFormDialogService
							.getById(tableIds[0]);
					fileName = bpmFormDialog.getName() + "_" + fileName;
				} else if (tableIds.length == list.size()) {
					fileName = "全部自定义对话框记录_" + fileName;
				} else {

					fileName = "多个自定义对话框记录_" + fileName;
				}
			}
			FileUtil.downLoad(request, response, strXml, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 导入自定义对话框的XML
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("importXml")
	@Action(description = "导入自定义对话框")
	public void importXml(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		MultipartFile fileLoad = request.getFile("xmlFile");
		ResultMessage message = null;
		try {
			bpmFormDialogService.importXml(fileLoad.getInputStream());
			message = new ResultMessage(ResultMessage.Success, MsgUtil.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message = new ResultMessage(ResultMessage.Fail, "导入文件异常，请检查文件格式！");
		}
		writeResultMessage(response.getWriter(), message);
	}

}
