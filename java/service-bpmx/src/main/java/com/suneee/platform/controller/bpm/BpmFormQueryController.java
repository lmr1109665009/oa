package com.suneee.platform.controller.bpm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.db.datasource.DbContextHolder;
import com.suneee.core.table.BaseTableMeta;
import com.suneee.core.table.IDbView;
import com.suneee.core.table.TableModel;
import com.suneee.core.table.impl.TableMetaFactory;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.core.db.datasource.DbContextHolder;
import com.suneee.core.table.BaseTableMeta;
import com.suneee.core.table.IDbView;
import com.suneee.core.table.TableModel;
import com.suneee.core.table.impl.TableMetaFactory;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.bpm.BpmFormQuery;
import com.suneee.platform.model.form.BpmFormDialog;
import com.suneee.platform.model.form.QueryResult;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysDataSource;
import com.suneee.platform.service.bpm.BpmFormQueryService;
import com.suneee.platform.service.system.SysDataSourceService;
import com.suneee.platform.xml.util.MsgUtil;

/**
 * 对象功能:通用表单查询 控制器类 开发公司:广州宏天软件有限公司 开发人员:ray 创建时间:2012-11-27 10:37:13
 */
@Controller
@RequestMapping("/platform/bpm/bpmFormQuery/")
@Action(ownermodel = SysAuditModelType.FORM_MANAGEMENT)
public class BpmFormQueryController extends BaseController {
	@Resource
	private BpmFormQueryService bpmFormQueryService;

	/**
	 * 取得通用表单查询分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看通用表单查询分页列表")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<BpmFormQuery> list = bpmFormQueryService.getAll(new QueryFilter(request, "bpmFormQueryItem"));
		ModelAndView mv = this.getAutoView().addObject("bpmFormQueryList", list);

		return mv;
	}

	/**
	 * 通用表单查询对话框
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("dialog")
	@Action(description = "通用表单查询对话框")
	public ModelAndView dialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<BpmFormQuery> list = bpmFormQueryService.getAll(new QueryFilter(request, "bpmFormQueryItem"));
		ModelAndView mv = this.getAutoView().addObject("bpmFormQueryList", list);

		return mv;
	}

	/**
	 * 删除通用表单查询
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除通用表单自定义查询", execOrder = ActionExecOrder.BEFORE, detail = "删除自定义查询" + "<#list StringUtils.split(id,\",\") as item>" + " <#assign enity=bpmFormQueryService.getById(Long.valueOf(item)) />" + " 【${enity.name}】" + "</#list>")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage resultMessage = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			bpmFormQueryService.delByIds(lAryId);
			resultMessage = new ResultMessage(ResultMessage.Success, "删除通用表单查询成功!");
		} catch (Exception ex) {
			resultMessage = new ResultMessage(ResultMessage.Fail, "通用表单查询失败:" + ex.getMessage());
		}
		addMessage(resultMessage, request);
		response.sendRedirect(preUrl);
	}

	@RequestMapping("queryObj")
	@Action(description = "查看通用表单查询明细")
	@ResponseBody
	public Map<String, Object> queryObj(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String alias = RequestUtil.getString(request, "alias");
		Map<String, Object> map = new HashMap<String, Object>();
		BpmFormQuery bpmFormQuery = bpmFormQueryService.getByAlias(alias);
		if (bpmFormQuery != null) {
			map.put("bpmFormQuery", bpmFormQuery);
			map.put("success", 1);
		} else {
			map.put("success", 0);
		}
		return map;
	}

	/**
	 * 显示查询条件构建界面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String alias = RequestUtil.getString(request, "query_alias_");

		BpmFormQuery bpmFormQuery = bpmFormQueryService.getByAlias(alias);
		ModelAndView mv = this.getAutoView();
		mv.addObject("bpmFormQuery", bpmFormQuery);
		return mv;
	}

	/**
	 * 进行查询
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("doQuery")
	@Action(description = "进行查询")
	@ResponseBody
	public QueryResult doQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryResult result = new QueryResult();
		String alias = RequestUtil.getString(request, "alias");
		String queryData = RequestUtil.getString(request, "querydata");
		Integer page = RequestUtil.getInt(request, "page", 0);
		Integer pageSize = RequestUtil.getInt(request, "pagesize", 0);
		BpmFormQuery bpmFormQuery = bpmFormQueryService.getByAlias(alias);
		//TODO 为什么不能在bpmFormQueryService换？因为service有事务保护着，只用一个链接
//		DbContextHolder.setDataSource(bpmFormQuery.getDsalias());
		
		if (bpmFormQuery != null) {
			result = bpmFormQueryService.getData(bpmFormQuery, queryData, page, pageSize);
		} else
			result.setErrors("查询别名不正确，未能获取到该别名的查询对象。");
		
		//TODO 这样不太妥，应该在清空数据源之前调用的service中增加一句- -
		DbContextHolder.clearDataSource();
		DbContextHolder.setDefaultDataSource();
		return result;
	}

	/**
	 * 编辑通用表单查询
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description = "编辑通用表单查询")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		Map<String, Object> params = RequestUtil.getQueryMap(request);
		Long canReturn = RequestUtil.getLong(request, "canReturn", 0);
		String returnUrl = RequestUtil.getPrePage(request);

		BpmFormQuery bpmFormQuery = null;
		if (id != 0) {
			bpmFormQuery = bpmFormQueryService.getById(id);
		} else {
			bpmFormQuery = new BpmFormQuery();
		}
//		List<SysDataSource> dsList = sysDataSourceService.getAll();
		List<SysDataSource> dsList = AppUtil.getBean(SysDataSourceService.class).getAll();
		return getAutoView().addObject("bpmFormQuery", bpmFormQuery).addObject("returnUrl", returnUrl).addObject("dsList", dsList).addObject("canReturn", canReturn);
	}

	/**
	 * 设置字段
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
		ModelAndView mv = this.getAutoView();
		if (id == 0) {
			dsName = RequestUtil.getString(request, "dsName");
			istable = RequestUtil.getInt(request, "istable");
			objectName = RequestUtil.getString(request, "objectName");
		} else {
			BpmFormQuery bpmFormQuery = bpmFormQueryService.getById(id);
			istable = bpmFormQuery.getIsTable();
			dsName = bpmFormQuery.getDsalias();
			objectName = bpmFormQuery.getObjName();
			mv.addObject("bpmFormQuery", bpmFormQuery);
		}

		TableModel tableModel=null;
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

		mv.addObject("tableModel", tableModel);

		return mv;
	}

	/**
	 * 取得所有的自定义查询
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getAllQueries")
	@ResponseBody
	public List<BpmFormQuery> getAllDialogs(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<BpmFormQuery> list = bpmFormQueryService.getAll();
		return list;
	}
	
	/**
	 * 自定义查询的参数填值对话框
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("btnDialog")
	public ModelAndView btnDialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String alias = RequestUtil.getString(request, "alias");

		BpmFormQuery bpmFormQuery = bpmFormQueryService.getByAlias(alias);
		ModelAndView mv = this.getAutoView();
		mv.addObject("bpmFormQuery", bpmFormQuery);
		return mv;
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
	 * 导出自定义查询xml。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("exportXml")
	@Action(description = "导出自定义查询", detail = "导出自定义查询:" + "<#list StringUtils.split(tableIds,\",\") as item>" + "<#assign entity=bpmFormTableService.getById(Long.valueOf(item))/>" + "【${entity.tableDesc}(${entity.tableName})】" + "</#list>")
	public void exportXml(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String strXml = null;
		String fileName = null;
		Long[] tableIds = RequestUtil.getLongAryByStr(request, "tableIds");
		List<BpmFormQuery> list = bpmFormQueryService.getAll();
		try {
			if (BeanUtils.isEmpty(tableIds)) {
				if (BeanUtils.isNotEmpty(list)) {
					strXml = bpmFormQueryService.exportXml(list);
					fileName = "全部自定义查询记录_"
							+ DateFormatUtil.getNowByString("yyyyMMddHHmmdd")
							+ ".xml";
				}
			} else {
				strXml = bpmFormQueryService.exportXml(tableIds);
				fileName = DateFormatUtil.getNowByString("yyyyMMddHHmmdd")
						+ ".xml";
				if (tableIds.length == 1) {
					BpmFormQuery bpmFormQuery = bpmFormQueryService
							.getById(tableIds[0]);
					fileName = bpmFormQuery.getName() + "_" + fileName;
				} else if (tableIds.length == list.size()) {
					fileName = "全部自定义查询记录_" + fileName;
				} else {

					fileName = "多条自定义查询记录_" + fileName;
				}
			}
			FileUtil.downLoad(request, response, strXml, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 导入自定义查询的XML
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("importXml")
	@Action(description = "导入自定义查询")
	public void importXml(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		MultipartFile fileLoad = request.getFile("xmlFile");
		ResultMessage message = null;
		try {
			bpmFormQueryService.importXml(fileLoad.getInputStream());
			message = new ResultMessage(ResultMessage.Success,
					MsgUtil.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message = new ResultMessage(ResultMessage.Fail, "导入文件异常，请检查文件格式！");
		}
		writeResultMessage(response.getWriter(), message);
	}
	
}
