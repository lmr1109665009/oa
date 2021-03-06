package com.suneee.platform.service.system;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.util.BpmUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.engine.GroovyScriptEngine;
import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageList;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.*;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.system.SysIndexColumnDao;
import com.suneee.platform.model.bpm.BpmFormQuery;
import com.suneee.platform.model.index.IndexColumn;
import com.suneee.platform.model.index.IndexColumns;
import com.suneee.platform.model.index.IndexTabList;
import com.suneee.platform.model.index.ObjectFactory;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.OrgAuth;
import com.suneee.platform.model.system.SysIndexColumn;
import com.suneee.platform.model.system.SysObjRights;
import com.suneee.platform.service.bpm.BpmFormQueryService;
import com.suneee.platform.service.system.impl.curuser.OrgSubUserService;
import com.suneee.platform.service.util.ServiceUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.util.BpmUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.engine.GroovyScriptEngine;
import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageList;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.JSONUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.XmlBeanUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.system.SysIndexColumnDao;
import com.suneee.platform.model.bpm.BpmFormQuery;
import com.suneee.platform.model.index.IndexColumn;
import com.suneee.platform.model.index.IndexColumns;
import com.suneee.platform.model.index.IndexTabList;
import com.suneee.platform.model.index.ObjectFactory;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.OrgAuth;
import com.suneee.platform.model.system.SysIndexColumn;
import com.suneee.platform.model.system.SysObjRights;
import com.suneee.platform.service.bpm.BpmFormQueryService;
import com.suneee.platform.service.system.impl.curuser.OrgSubUserService;
import com.suneee.platform.service.util.ServiceUtil;

/**
 * <pre>
 * 对象功能:首页栏目 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:hugh
 * 创建时间:2015-03-18 11:22:46
 * </pre>
 */
@Service
public class SysIndexColumnService extends BaseService<SysIndexColumn> {
	private static Log logger = LogFactory.getLog(SysIndexColumnService.class);
	@Resource
	private SysIndexColumnDao dao;
	@Resource
	private FreemarkEngine freemarkEngine;
	@Resource
	private SysUserService sysUserService;
	@Resource
	private SysRoleService sysRoleService;
	@Resource
	private SysOrgService sysOrgService;
	@Resource
	private PositionService positionService;
	@Resource
	private GlobalTypeService globalTypeService;
	@Resource
	private BpmFormQueryService bpmFormQueryService;

	@Resource
	private CurrentUserService currentUserService;
	@Resource
	private OrgSubUserService orgSubUserService;
	@Resource
	private GroovyScriptEngine groovyScriptEngine;

	@Resource
	private OrgAuthService orgAuthService;
	@Resource
	private IndexService indexService;

	public SysIndexColumnService() {
	}

	@Override
	protected IEntityDao<SysIndexColumn, Long> getEntityDao() {
		return dao;
	}

	
	/**
	 * 获取有权限的栏目
	 * @param filter
	 * @param params
	 * @param isParse
	 * @return
	 * @throws Exception
	 */
	public List<SysIndexColumn> getHashRightColumnList(QueryFilter filter,
                                                       Map<String, Object> params, Boolean isParse) throws Exception {
		List<SysIndexColumn> list;
		if (ContextUtil.isSuperAdmin()) { // 是超级管理员 filter //
			list = dao.getAll(filter);
		}else{
			list = getByUserIdFilter();
		}
		if (isParse)
			this.parseList(list, params);
		return list;
	}

	private void parseList(List<SysIndexColumn> list, Map<String, Object> params)
			throws Exception {
		if (BeanUtils.isEmpty(list))
			return;
		for (SysIndexColumn sysIndexColumn : list) {
			String templateHtml = parseTemplateHtml(sysIndexColumn, params);
			sysIndexColumn.setTemplateHtml(templateHtml);
		}
	}

	/**
	 * 获取当前组织有权限的栏目(个人)
	 * 可以删除？
	 * @param ctxPath
	 * @return
	 * @throws Exception
	 */
	public List<SysIndexColumn> getIndexColumnData(Map<String, Object> params)
			throws Exception {
		List<SysIndexColumn> list = new ArrayList<SysIndexColumn>();
		if (ContextUtil.isSuperAdmin()) {
			list = dao.getAll();
		} else {
			// 获取当前组织有权限的栏目
			list = getByUserIdFilter();
		}
		this.parseList(list, params);
		return list;

	}

	public List<SysIndexColumn> getByUserIdFilter() {
		// 这一步很关键 用伪代码来表达map 的内容就是
		// 类型 然后 接着一堆对应的ID eg：
		// user : 1,2,3,4...
		// org : 1,2,3,4...
		// 然后 根据上面的对应ID 和 权限配置的ID进行比较，看满不满足权限条件
		
		Map<String, List<Long>> map = currentUserService
				.getUserRelation(ServiceUtil.getCurrentUser());
		
		//添加分级管理与，当栏目设置了组织。
		List<OrgAuth> orgAuthList =  orgAuthService.getByUserId(ContextUtil.getCurrentUserId());
		List<Long> authOrgIdList = new ArrayList<Long>();
		if(BeanUtils.isNotEmpty(orgAuthList)){
			for(OrgAuth orgAuth:orgAuthList){
				authOrgIdList.add(orgAuth.getOrgId());
			}
			map.put("auth_org", authOrgIdList);
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("relationMap", map);
		params.put("objType", SysObjRights.RIGHT_TYPE_INDEX_COLUMN);
		return dao.getByUserIdFilter(params);
	}
	/**
	 * 获取栏目的模版的HTML
	 * 
	 * @param sysIndexColumn
	 * @param ctxPath
	 * @return
	 * @throws Exception
	 */
	public String parseTemplateHtml(SysIndexColumn sysIndexColumn,
			Map<String, Object> params) throws Exception {
		JSONObject json = parseTemplateJSON(sysIndexColumn, params);
		return json.getString("html");
	}

	/**
	 * 获取栏目的模版的HTML
	 * 
	 * @param sysIndexColumn
	 * @param ctxPath
	 * @return
	 * @throws Exception
	 */
	public String parseTemplateHtmlJSON(SysIndexColumn sysIndexColumn,
			Map<String, Object> params) throws Exception {
		JSONObject json = parseTemplateJSON(sysIndexColumn, params);
		return json.toString();
	}

	/**
	 * 获取栏目的模版的HTML
	 * 
	 * @param sysIndexColumn
	 * @param ctxPath
	 * @return
	 * @throws Exception
	 */
	public JSONObject parseTemplateJSON(SysIndexColumn sysIndexColumn,
			Map<String, Object> params) throws Exception {
		String dataFrom = sysIndexColumn.getDataFrom();
		String html = sysIndexColumn.getTemplateHtml();
		Short colType = sysIndexColumn.getColType();
		short dataMode = sysIndexColumn.getDataMode();

		String ctxPath = params.get("__ctx").toString();
		String dataParam = sysIndexColumn.getDataParam();
		Object data = null;
		// 获取具体栏目的数据。
		try {
			Class<?>[] parameterTypes = getParameterTypes(dataParam, params);
			Object[] param = getDataParam(dataParam, params);
			if (SysIndexColumn.DATA_MODE_SERVICE == dataMode) {// service方式
				data = getModelByHandler(dataFrom, param, parameterTypes);
			} else if (SysIndexColumn.DATA_MODE_QUERY == dataMode) { // 自定义查询方式
				String alias = sysIndexColumn.getDataFrom();
				BpmFormQuery query = bpmFormQueryService.getByAlias(alias);
				data = bpmFormQueryService.getData(query, null, 1, 20);
			}
		} catch (Exception e) {
			// 出异常不影响其它数据
			e.printStackTrace();
		}
		Long height = BeanUtils.isEmpty(sysIndexColumn.getColHeight()) ? 320
				: sysIndexColumn.getColHeight();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("alias", sysIndexColumn.getAlias());
		model.put("title", sysIndexColumn.getName());
		model.put("url", sysIndexColumn.getColUrl());
		model.put("height", height);
		map.put("model", model);// 栏目
		map.put("data", data); // 获取的数据
		map.put("ctx", ctxPath);// 上下文目录
		map.put("service", indexService);// 把service注入
		PageBean pageBean = null;
		if (sysIndexColumn.getNeedPage() == 1) // 进行分页
			pageBean = handerPageBean(data);
		map.put("pageBean", pageBean); // 获取的数据

		html = "<#setting number_format=\"#\">" + html;
		html = freemarkEngine.parseByStringTemplate(map, html);
		html = parserHtml(html, sysIndexColumn, pageBean, params);
		
		JSONObject json = new JSONObject();
		// 如果是图表则返回数据
		if (SysIndexColumn.COLUMN_TYPE_CHART == colType
				|| SysIndexColumn.COLUMN_TYPE_CALENDAR == colType) // 加载图表的数据
			json.accumulate("option", data);

		// 这些数据前台解析
		json.accumulate("isRefresh", sysIndexColumn.getSupportRefesh())
				.accumulate("refreshTime", sysIndexColumn.getRefeshTime())
				.accumulate("show", sysIndexColumn.getShowEffect())
				.accumulate("type", colType).accumulate("height", height)
				.accumulate("html", html);
		return json;
	}

	/**
	 * 处理分页数据
	 * 
	 * @param data
	 * @return
	 */
	private PageBean handerPageBean(Object data) {
		PageBean pageBean = null;
		try {
			if (data instanceof PageList<?>) {
				PageList<?> pageList = (PageList<?>) data;
				pageBean = pageList.getPageBean(); // 获取分页的数据
			} else if (data instanceof IndexTabList) {
				IndexTabList indexTablist = (IndexTabList) data;
				pageBean = getIndexTabPageBean(indexTablist); // 获取分页的数据
			}
		} catch (Exception e) {

		}
		return pageBean;
	}

	private PageBean getIndexTabPageBean(IndexTabList indexTablist) {
		if (BeanUtils.isEmpty(indexTablist))
			return null;
		return indexTablist.getPageBean();
	}

	private Class<?>[] getParameterTypes(String dataParam,
			Map<String, Object> params) {
		if (JSONUtil.isEmpty(dataParam) || StringUtil.isEmpty(dataParam))
			return new Class<?>[0];
		JSONArray jary = JSONArray.fromObject(dataParam);
		Class<?>[] parameterTypes = new Class<?>[jary.size()];
		for (int i = 0; i < jary.size(); i++) {
			JSONObject json = jary.getJSONObject(i);
			String type = (String) json.get("type");

			parameterTypes[i] = getParameterTypes(type);
		}
		return parameterTypes;
	}

	private Object[] getDataParam(String dataParam, Map<String, Object> params) {
		if (JSONUtil.isEmpty(dataParam) || StringUtil.isEmpty(dataParam))
			return null;
		JSONArray jary = JSONArray.fromObject(dataParam);
		Object[] args = new Object[jary.size()];
		for (int i = 0; i < jary.size(); i++) {
			JSONObject json = jary.getJSONObject(i);
			String name = (String) json.get("name");
			String type = (String) json.get("type");
			String mode = (String) json.get("mode");
			String value = (String) json.get("value");

			Object o = value;
			if (mode.equalsIgnoreCase("1")) {// 页面传入的
				o = params.get(name);
				if (JSONUtil.isEmpty(o) && BeanUtils.isNotEmpty(value))// 如果空值则取默认值
					o = value;
			} else if (mode.equalsIgnoreCase("2")) {// 脚本传入
				o = groovyScriptEngine.executeString(value, params);
			}
			Object val = StringUtil.parserObject(o, type);

			args[i] = val;
		}
		return args;
	}

	private Class<?> getParameterTypes(String type) {
		Class<?> claz = null;
		try {
			if (type.equalsIgnoreCase("string")) {
				claz = String.class;
			} else if (type.equalsIgnoreCase("int")) {
				claz = Integer.class;
			} else if (type.equalsIgnoreCase("float")) {
				claz = Float.class;
			} else if (type.equalsIgnoreCase("double")) {
				claz = Double.class;
			} else if (type.equalsIgnoreCase("byte")) {
				claz = Byte.class;
			} else if (type.equalsIgnoreCase("short")) {
				claz = Short.class;
			} else if (type.equalsIgnoreCase("long")) {
				claz = Long.class;
			} else if (type.equalsIgnoreCase("boolean")) {
				claz = Boolean.class;
			} else if (type.equalsIgnoreCase("date")) {
				claz = Date.class;
			} else {
				claz = String.class;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return claz;
	}

	/**
	 * 给栏目添加个别名
	 * 
	 * @param html
	 * @param sysIndexColumn
	 * @param pageBean
	 * @param params
	 * @return
	 */
	private String parserHtml(String html, SysIndexColumn sysIndexColumn,
			PageBean pageBean, Map<String, Object> params) {

		if (StringUtil.isEmpty(html))
			return "";
		Document doc = Jsoup.parseBodyFragment(html);
		Elements els = doc.body().children();
		if (BeanUtils.isEmpty(els))
			return doc.body().html();
		Element el = els.get(0);
		el.attr("template-alias", sysIndexColumn.getAlias());
		JSONObject json = new JSONObject();
		// params.remove("__ctx");
		for (String key : params.keySet()) {
			json.accumulate(key, params.get(key));
		}
		if (BeanUtils.isNotEmpty(pageBean)) {
			json.element("currentPage", pageBean.getCurrentPage())
					.element("totalPage", pageBean.getTotalPage())
					.element("pageSize", pageBean.getPageSize());
		}
		el.attr("template-params", json.toString());
		html = doc.body().html();
		return html;

	}

	/**
	 * 根据handler取得数据。
	 * 
	 * <pre>
	 * handler 为 service +"." + 方法名称。
	 * </pre>
	 * 
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	private Object getModelByHandler(String handler, Object[] args,
			Class<?>[] parameterTypes) throws Exception {
		Object model = null;
		if (StringUtil.isEmpty(handler))
			return model;
		int rtn = BpmUtil.isHandlerValidNoCmd(handler, parameterTypes);
		if (rtn != 0)
			return model;
		String[] aryHandler = handler.split("[.]");
		if (aryHandler == null)
			return model;
		String beanId = aryHandler[0];
		String method = aryHandler[1];
		// 触发该Bean下的业务方法
		Object serviceBean = AppUtil.getBean(beanId);
		// 如果配置数据来源的方法带有参数的时候

		if (serviceBean == null)
			return model;
		if (args == null || args.length <= 0) {
			parameterTypes = new Class<?>[0];
		}
		Method invokeMethod = serviceBean.getClass().getDeclaredMethod(method,
				parameterTypes);

		model = invokeMethod.invoke(serviceBean, args);
		if (BeanUtils.isEmpty(model))
			model = null;
		return model;
	}

	public String getHtmlById(Long id, Map<String, Object> params)
			throws Exception {
		SysIndexColumn sysIndexColumn = this.getById(id);
		return "<div template-alias=\"" + sysIndexColumn.getAlias()
				+ "\"></div>";
	}

	/**
	 * 通过别名获取栏目
	 * 
	 * @param columnAlias
	 * @return
	 */
	public SysIndexColumn getByColumnAlias(String alias) {
		return dao.getByColumnAlias(alias);

	}

	public String getHtmlByColumnAlias(String alias, Map<String, Object> params)
			throws Exception {
		SysIndexColumn sysIndexColumn = this.getByColumnAlias(alias);
		if (BeanUtils.isEmpty(sysIndexColumn))
			return "";
		return parseTemplateHtmlJSON(sysIndexColumn, params);
	}

	/**
	 * 解析设计模版的html
	 * 
	 * @param designHtml
	 *            设计的html
	 * @param columnList
	 *            有权限栏目列表
	 * @return
	 */
	public String parserDesignHtml(String designHtml,
			List<SysIndexColumn> columnList) {
		if (StringUtil.isEmpty(designHtml))
			return null;
		Document doc = Jsoup.parseBodyFragment(designHtml);
		Elements els = doc.select("[template-alias]");
		for (Iterator<Element> it = els.iterator(); it.hasNext();) {
			Element el = it.next();
			String value = el.attr("template-alias");
			String h = getSysIndexColumn(value, columnList);
			Element parent = el.parent();
			el.remove();
			parent.append(h);
		}
		designHtml = doc.body().html();
		return designHtml;
	}

	/**
	 * 通过别名获取模版
	 * 
	 * @param alias
	 * @param columnList
	 * @return
	 */
	private String getSysIndexColumn(String alias,
			List<SysIndexColumn> columnList) {
		for (SysIndexColumn sysIndexColumn : columnList) {
			if (alias.equals(sysIndexColumn.getAlias()))
				return sysIndexColumn.getTemplateHtml();
		}
		return "";
	}

	/**
	 * 取得类型下的列表Map
	 * 
	 * @param columnList
	 * @return
	 */
	public Map<String, List<SysIndexColumn>> getColumnMap(
			List<SysIndexColumn> columnList) {
		List<GlobalType> catalogList = globalTypeService.getByCatKey(
				GlobalType.CAT_INDEX_COLUMN, false);
		Map<Long, List<SysIndexColumn>> map = new LinkedHashMap<Long, List<SysIndexColumn>>();
		Long nullCatalog = 0L;
		for (SysIndexColumn sysIndexColumn : columnList) {
			Long catalog = sysIndexColumn.getCatalog();
			if (BeanUtils.isEmpty(catalog))
				catalog = nullCatalog;
			List<SysIndexColumn> list = map.get(catalog);

			if (BeanUtils.isEmpty(list))
				list = new ArrayList<SysIndexColumn>();
			list.add(sysIndexColumn);
			map.put(catalog, list);
		}

		Map<String, List<SysIndexColumn>> map1 = new LinkedHashMap<String, List<SysIndexColumn>>();
		for (GlobalType globalType : catalogList) {
			Long typeId = globalType.getTypeId();
			String name = globalType.getTypeName();
			List<SysIndexColumn> list = map.get(typeId);
			if (BeanUtils.isNotEmpty(list))
				map1.put(name, list);
		}
		// 添加无类型的分类
		List<SysIndexColumn> list = map.get(nullCatalog);
		if (BeanUtils.isNotEmpty(list))
			map1.put("默认栏目", list);
		return map1;
	}

	/**
	 * 初始化桌面栏目，添加桌面栏目到数据库
	 */
	public static void initIndex() {
		SysIndexColumnService sysIndexColumnService = (SysIndexColumnService) AppUtil
				.getBean(SysIndexColumnService.class);
		try {
			sysIndexColumnService.init();
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}

	}

	/**
	 * 初始化首页栏目的数据</br> --如果没有数据才初始化数据
	 * 
	 * @throws Exception
	 * 
	 */
	public void init() throws Exception {
		Integer amount = dao.getCounts();
		if (amount == 0)
			initIndexColumn();
	}

	/**
	 * 初始化首页栏目的数据
	 * 
	 * @throws Exception
	 */
	public void initIndexColumn() throws Exception {
		String templatePath = ServiceUtil.getIndexTemplatePath();
		String path = templatePath + "index.xml";
		InputStream is = new BufferedInputStream(new FileInputStream(path));
		IndexColumns indexColumns = ((IndexColumns) XmlBeanUtil.unmarshall(is,
				ObjectFactory.class));
		List<IndexColumn> list = indexColumns.getIndexColumn();
		for (int i = 0; i < list.size(); i++) {
			IndexColumn indexColumn = list.get(i);
			SysIndexColumn sysIndexColumn = new SysIndexColumn();
			BeanUtils.copyNotNullProperties(sysIndexColumn, indexColumn);
			sysIndexColumn.setId((long) i + 1);
			String fileName = indexColumn.getAlias() + ".ftl";
			String templateHtml = FileUtil.readFile(templatePath + "templates"
					+ File.separator + fileName);
			sysIndexColumn.setTemplateHtml(templateHtml);
			SysIndexColumn column = dao.getByColumnAlias(sysIndexColumn
					.getAlias());
			if (BeanUtils.isNotEmpty(column))
				dao.delById(column.getId());
			dao.add(sysIndexColumn);
		}

	}

	public Boolean isExistAlias(String alias, Long id) {
		if (id == null || id == 0)
			id = null;
		return dao.isExistAlias(alias, id);
	}

	public List<SysIndexColumn> getDefaultList() {
		return dao.getBySqlKey("defaultList");
	}

}
