package com.suneee.platform.controller.system;

import com.suneee.core.excel.util.ExcelUtil;
import com.suneee.core.page.PageList;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.form.BpmFormTemplate;
import com.suneee.platform.model.form.CommonVar;
import com.suneee.platform.model.system.*;
import com.suneee.platform.service.form.BpmFormTemplateService;
import com.suneee.platform.service.system.*;
import com.suneee.platform.service.util.ServiceUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 对象功能:视图定义 控制器类
 * 开发公司:宏天软件
 * 开发人员:ray
 * 创建时间:2015-06-08 16:02:04
 * </pre>
 */
@Controller
@RequestMapping("/platform/system/sysQueryView/")
public class SysQueryViewController extends BaseController {
	@Resource
	private SysQuerySqlDefService sysQuerySqlDefService;
	@Resource
	private SysQueryViewService sysQueryViewService;
	@Resource
	private BpmFormTemplateService bpmFormTemplateService; // 模板serivce类
	@Resource
	private SysQueryMetaFieldService sysQueryMetaFieldService; // 模板serivce类
	@Resource
	private SysQueryFieldSettingService sysQueryFieldSettingService;
	@Resource
	private SysHistoryDataService historyDataService;

	/**
	 * 添加或更新视图定义。
	 * 
	 * @param request
	 * @param response
	 * @param sysQueryView
	 *            添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "添加或更新视图定义")
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String contextPath = request.getContextPath();
		String resultMsg = "保存视图";
		String json = request.getParameter("json");
		JSONObject jsonObj = JSONObject.fromObject(json);
		JSONArray sysQueryFieldSettingArray = JSONArray.fromObject(jsonObj.getString("displayField"));
		SysQueryView sysQueryView = sysQueryViewService.getSysQueryView(json);
		try {
			boolean flag = false; // 判断是添加还是保存
			if (sysQueryView.getId() == null || sysQueryView.getId() == 0) {
				flag = true;
			}
			if (sysQueryViewService.isAliasExists(sysQueryView.getAlias(), sysQueryView.getSqlAlias(), sysQueryView.getId())) {
				resultMsg = "指定别名已经存在!";
				writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Fail);
				return;
			}
			sysQueryViewService.save(sysQueryView, sysQueryFieldSettingArray, flag, contextPath);
			resultMsg = flag ? "添加视图定义成功" : "更新视图定义成功";
			ResultMessage resultMessage = new ResultMessage(ResultMessage.Success, resultMsg, sysQueryView.getId().toString());
			writeResultMessage(response.getWriter(), resultMessage);
		} catch (Exception e) {
			e.printStackTrace();
			writeResultMessage(response.getWriter(), resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}

	}

	/**
	 * 取得视图定义分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看视图定义分页列表")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long sqlId = RequestUtil.getLong(request, "sqlId", 0);
		SysQuerySqlDef querySqlDef = sysQuerySqlDefService.getById(sqlId);
		QueryFilter queryFilter = new QueryFilter(request, "sysQueryViewItem");
		queryFilter.addFilter("sqlAlias", querySqlDef.getAlias());
		List<SysQueryView> list = sysQueryViewService.getAll(queryFilter);
		ModelAndView mv = this.getAutoView().addObject("sysQueryViewList", list).addObject("querySqlDef", querySqlDef).addObject("sqlId", sqlId);

		return mv;
	}

	@RequestMapping("sortList")
	public ModelAndView sortList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter queryFilter = new QueryFilter(request, "sysQueryViewItem");
		List<SysQueryView> list = sysQueryViewService.getAll(queryFilter);
		ModelAndView mv = this.getAutoView().addObject("sysQueryViewList", JSONArray.fromObject(list));
		return mv;
	}

	@RequestMapping("exports")
	public ModelAndView exports(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return this.getAutoView();
	}

	@RequestMapping("saveSortList")
	@ResponseBody
	public JSONObject saveSortList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject jo = new JSONObject();
		String list = request.getParameter("list");
		try {
			sysQueryViewService.saveSortList(list);
			jo.put("success", true);
		} catch (Exception e) {
			jo.put("success", false);
		}
		return jo;
	}

	/**
	 * 删除视图定义
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除视图定义")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			sysQueryViewService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除视图定义成功!");
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	/**
	 * 编辑视图定义
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description = "编辑视图定义")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		Long id = RequestUtil.getLong(request, "id", 0L);
		Long sqlId = RequestUtil.getLong(request, "sqlId", 0L);

		SysQueryView sysQueryView = null;
		List<SysQueryFieldSetting> displayFields = null;
		List<SysQueryMetaField> sysQueryMetaFields = sysQueryMetaFieldService.getListBySqlId(sqlId);

		if (id != 0) {
			sysQueryView = sysQueryViewService.getById(id);
			List<SysQueryFieldSetting> sysQueryFieldSettingList = sysQueryFieldSettingService.getBySysQueryViewId(id);

			displayFields = sysQueryFieldSettingService.getDisplayFields(sysQueryMetaFields, sysQueryFieldSettingList);

		}

		if (sysQueryView == null)
			sysQueryView = sysQueryViewService.getBySqlId(sqlId);
		List<BpmFormTemplate> templates = bpmFormTemplateService.getQueryDataTemplate();// 获取模板对象集

		return this.getAutoView().addObject("templates", templates).addObject("sysQueryView", sysQueryView).addObject("sysQueryViewJson", com.alibaba.fastjson.JSONObject.toJSON(sysQueryView)).addObject("displayFields", JSONArray.fromObject(displayFields)).addObject("commonVars", JSONArray.fromObject(CommonVar.getCurrentVars(false))).addObject("sysQueryMetaFields", JSONArray.fromObject(sysQueryMetaFields)).addObject("sqlId", sqlId);
	}

	@RequestMapping("editTemplate")
	@Action(description = "编辑视图定义")
	public ModelAndView editTemplate(HttpServletRequest request) throws Exception {
		Long id = RequestUtil.getLong(request, "id", 0L);

		List<SysHistoryData> historyDatas = historyDataService.getByObjId(id);

		SysQueryView sysQueryView = sysQueryViewService.getById(id);

		return this.getAutoView().addObject("sysQueryView", sysQueryView).addObject("historyDatas", historyDatas);
	}

	/**
	 * 取得视图定义明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description = "查看视图定义明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		SysQueryView sysQueryView = sysQueryViewService.getById(id);
		return getAutoView().addObject("sysQueryView", sysQueryView);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("data_{sqlAlias}/{view}")
	@ResponseBody
	@Action(description = "取得分页数据")
	public Map<String, Object> listJsonJQ(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "sqlAlias") String sqlAlias, @PathVariable(value = "view") String view) throws Exception {
		Map<String, Object> queryParams = RequestUtil.getQueryParamater(request);
		int currentPage = RequestUtil.getInt(request, "page", 1);
		int pageSize = RequestUtil.getInt(request, "pageSize", 20);
		String isSearch = request.getParameter("initSearch");
		String sortField = request.getParameter("sortField");
		String orderSeq = request.getParameter("orderSeq");
		//设置当前上下文变量。
		CommonVar.setCurrentVars(queryParams);

		PageList pageList = new PageList();
		if ("true".equals(isSearch)) {
			SysQueryView sysQueryView = sysQueryViewService.getQueryView(sqlAlias, view);
			pageList = sysQueryViewService.getPageList(currentPage, pageSize, queryParams, sysQueryView, sortField, orderSeq);
		}
		Map map = getMapByPageListJq(pageList);
		return map;
	}

	@RequestMapping("{sqlAlias}/{view}")
	@Action(description = "显示列表页面")
	public ModelAndView showList(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "sqlAlias") String sqlAlias, @PathVariable(value = "view") String view) throws Exception {
		Map<String, Object> params = RequestUtil.getParameterValueMap(request, false, true);
		ModelAndView mv = getShowModelAndView(sqlAlias, view, false);
		// postData 主要是扩展动态传送语句参数的过滤方式，所传入的参数，一定要带有查询标示与查询条件的所带参数一直，如：Q_USERID=1
		return mv.addObject("postData", JSONObject.fromObject(params).toString());
	}

	@RequestMapping("{sqlAlias}")
	@Action(description = "显示列表页面")
	public ModelAndView showList(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "sqlAlias") String sqlAlias) throws Exception {
		String currentView = RequestUtil.getString(request, "view");
		Map<String, Object> params = RequestUtil.getParameterValueMap(request, false, true);

		ModelAndView mv = getShowModelAndView(sqlAlias, currentView, true);
		return mv.addObject("postData", JSONObject.fromObject(params).toString());
	}

	private ModelAndView getShowModelAndView(String sqlAlias, String currentView, boolean hasTab) throws Exception {
		List<SysQueryView> viewList = new ArrayList<SysQueryView>();
		//是否存在TAB
		if (hasTab) {
			viewList = sysQueryViewService.getHasRights(sqlAlias);
			if (BeanUtils.isEmpty(viewList)) {
				return ServiceUtil.getTipInfo("您没有访问当前的权限.");

			}
			if (StringUtil.isEmpty(currentView)) {
				currentView = viewList.get(0).getAlias();
			}
			//是否支持TAB显示
			SysQuerySqlDef querySqlDef = sysQuerySqlDefService.getByAlias(sqlAlias);
			if (querySqlDef.getSupportTab() == 0) {
				hasTab = false;
				currentView = viewList.get(0).getAlias();
			}
		}
		SysQueryView queryView = sysQueryViewService.getQueryView(sqlAlias, currentView);
		ModelAndView mv = new ModelAndView();
		mv.addObject("viewList", viewList);
		mv.addObject("currentView", currentView);
		mv.addObject("queryView", queryView);
		mv.addObject("sqlAlias", sqlAlias);
		mv.addObject("hasTab", hasTab);
		mv.setViewName("/platform/system/sysQueryViewShow");
		return mv;
	}

	@RequestMapping("saveTemplate")
	public void saveTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String resultMsg = "";
		Long id = RequestUtil.getLong(request, "id");
		String template = request.getParameter("template");

		SysQueryView queryView = sysQueryViewService.getById(id);
		queryView.setTemplate(template);
		sysQueryViewService.update(queryView);

		historyDataService.add(SysHistoryData.SYS_QUERY_VIEW_TEMPLATE, queryView.getName(), template, id);
		resultMsg = "更新自定义表管理显示模板成功";
		writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
	}

	@RequestMapping("saveExports")
	public void saveExports(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, Object> queryParams = RequestUtil.getQueryParamater(request);
		int currentPage = RequestUtil.getInt(request, "page", 1);
		int pageSize = RequestUtil.getInt(request, "pageSize", 20);
		int isAll = RequestUtil.getInt(request, "isAll", (short) 0);
		Long viewId = RequestUtil.getLong(request, "viewId"); //视图ID

		String sortField = request.getParameter("sortField");
		String orderSeq = request.getParameter("orderSeq");
		//导出的字段列表 以,分隔  如：USERID,FULLNAME,ACCOUNT
		String exportNames = request.getParameter("exportNames");

		//设置当前上下文变量。
		CommonVar.setCurrentVars(queryParams);
		SysQueryView sysQueryView = sysQueryViewService.getById(viewId);

		sysQueryView.setNeedPage((short) ((isAll == 1) ? 0 : 1));

		HSSFWorkbook workbook = sysQueryViewService.genExcel(currentPage, pageSize, queryParams, sysQueryView, sortField, orderSeq, exportNames);

		ExcelUtil.downloadExcel(workbook, sysQueryView.getName(), response);
	}

	/**
	 * 初始化所有的视图模板
	 * 
	 * @param request
	 * @param response
	 * @param sysQueryView
	 *            添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("updatAllTemplate")
	@Action(description = "初始化所有的视图模板")
	public void updatAllTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String contextPath = request.getContextPath();
		try {
			ResultMessage resultMessage = new ResultMessage(ResultMessage.Success, "初始化成功！");
			sysQueryViewService.updatAllTemplate(contextPath);
			writeResultMessage(response.getWriter(), resultMessage);
		} catch (Exception e) {
			e.printStackTrace();
			writeResultMessage(response.getWriter(), "初始化失败！," + e.getMessage(), ResultMessage.Fail);
		}

	}

}
