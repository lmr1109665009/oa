package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.db.datasource.DataSourceUtil;
import com.suneee.core.db.datasource.JdbcTemplateUtil;
import com.suneee.core.db.entity.SQLClause;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.engine.GroovyScriptEngine;
import com.suneee.core.excel.util.ExcelUtil;
import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageList;
import com.suneee.core.service.BaseService;
import com.suneee.core.table.ColumnModel;
import com.suneee.core.util.AppConfigUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.SysQueryViewDao;
import com.suneee.platform.model.form.BpmFormTemplate;
import com.suneee.platform.model.form.CommonVar;
import com.suneee.platform.model.system.*;
import com.suneee.platform.service.form.BpmFormTemplateService;
import com.suneee.platform.service.util.ServiceUtil;
import freemarker.template.TemplateException;
import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * <pre>
 * 对象功能:视图定义 Service类
 * 开发公司:宏天软件
 * 开发人员:ray
 * 创建时间:2015-06-08 16:02:04
 * </pre>
 */
@Service
public class SysQueryViewService extends BaseService<SysQueryView> {
	@Resource
	private SysQueryViewDao dao;

	@Resource
	private SysQuerySqlDefService sysQuerySqlDefService;

	@Resource
	private SysQueryFieldSettingService sysQueryFieldSettingService;

	@Resource
	private SysQueryMetaFieldService sysQueryMetaFieldService; // 模板serivce类

	@Resource
	private CurrentUserService currentUserService;

	@Resource
	private FreemarkEngine freemarkEngine;

	@Resource
	private GroovyScriptEngine groovyScriptEngine;

	@Resource
	private BpmFormTemplateService bpmFormTemplateService;
	@Resource
	private SysDataSourceService sysDataSourceService;

	public SysQueryViewService() {
	}

	@Override
	protected IEntityDao<SysQueryView, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 根据json字符串获取SysQueryView对象
	 * 
	 * @param json
	 * @return
	 */
	public SysQueryView getSysQueryView(String json) {
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher((new String[] { "yyyy-MM-dd" })));
		if (StringUtil.isEmpty(json))
			return null;
		JSONObject obj = JSONObject.fromObject(json);
		SysQueryView sysQueryView = (SysQueryView) JSONObject.toBean(obj, SysQueryView.class);
		return sysQueryView;
	}

	public SysQueryView getBySqlId(Long sqlId) throws Exception {
		SysQueryView sysQueryView = new SysQueryView();
		sysQueryView.setNeedPage((short) 1);

		SysQuerySqlDef sysQuerySqlDef = sysQuerySqlDefService.getById(sqlId);
		sysQueryView.setButtons(sysQuerySqlDef.getButtonDef());
		sysQueryView.setSqlAlias(sysQuerySqlDef.getAlias());
		return sysQueryView;
	}

	private Map<String, SysQueryMetaField> convertToMap(List<SysQueryMetaField> metaFields) {
		Map<String, SysQueryMetaField> map = new HashMap<String, SysQueryMetaField>();
		for (SysQueryMetaField field : metaFields) {
			map.put(field.getName(), field);
		}
		return map;
	}

	/**
	 * 保存字段设置。
	 * 
	 * @param sysQueryView
	 * @param sysQueryFieldSettingArray
	 * @param flag
	 */
	public void save(SysQueryView sysQueryView, JSONArray sysQueryFieldSettingArray, boolean flag, String contextPath) {
		String sqlAlias = sysQueryView.getSqlAlias();
		List<SysQueryMetaField> metaFields = sysQueryMetaFieldService.getListBySqlAlias(sqlAlias);
		Map<String, SysQueryMetaField> fieldMap = convertToMap(metaFields);
		//构建字段
		List<SysQueryFieldSetting> fieldSettings = new ArrayList<SysQueryFieldSetting>();
		Long viewId = flag ? UniqueIdUtil.genId() : sysQueryView.getId();
		for (Object obj : sysQueryFieldSettingArray) {
			JSONObject jsonObj = (JSONObject) obj;
			SysQueryFieldSetting fieldSetting = (SysQueryFieldSetting) JSONObject.toBean(jsonObj, SysQueryFieldSetting.class);
			String fieldName = jsonObj.getString("name");
			fieldSetting.setFieldName(fieldName);
			fieldSetting.setId(null);
			fieldSetting.setViewId(viewId);
			//设置字段
			fieldSetting.setMetaField(fieldMap.get(fieldName));
			fieldSettings.add(fieldSetting);
		}
		sysQueryView.setMetaFieldMap(metaFields);
		fieldSettings = sysQueryFieldSettingService.getSortForFieldSettingList(fieldSettings);
		sysQueryView.setFieldSettings(fieldSettings);
		//生成HTML模版
		String templateHtml = this.generateTemplate(sysQueryView, contextPath);
		sysQueryView.setTemplate(templateHtml);
		if (flag) {
			sysQueryView.setId(viewId);
			this.add(sysQueryView);
		} else {
			sysQueryFieldSettingService.removeBySysQueryViewId(viewId);
			this.update(sysQueryView);
		}
		//添加字段设置
		for (SysQueryFieldSetting obj : fieldSettings) {
			sysQueryFieldSettingService.save(obj);
		}
	}

	/**
	 * 根据sqlAlias别名获取有权限的视图页面。
	 * 
	 * @param sqlAlias
	 * @return
	 */
	public List<SysQueryView> getHasRights(String sqlAlias) {
		Map<String, List<Long>> relationMap = currentUserService.getUserRelation(ServiceUtil.getCurrentUser());
		return dao.getHasRights(relationMap, sqlAlias);
	}

	/**
	 * 根据sql别名和视图别名获取视图对象。
	 * 
	 * @param sqlAlias
	 * @param viewAlias
	 * @return
	 */
	public SysQueryView getQueryView(String sqlAlias, String viewAlias) {
		SysQueryView queryView = dao.getBySqlView(sqlAlias, viewAlias);
		return queryView;
	}

	/**
	 * 根据别名获取视图列表
	 * 
	 * @param sqlAlias
	 * @return
	 */
	public List<SysQueryView> getListBySqlAlias(String sqlAlias) {
		List<SysQueryView> sysQueryViewList = dao.getListBySqlAlias(sqlAlias);
		return sysQueryViewList;
	}

	/**
	 * 根据视图生成模版。
	 * 
	 * @param sysQueryView
	 * @return
	 */
	private String generateTemplate(SysQueryView sysQueryView, String contextPath) {
		String templateAlias = sysQueryView.getTemplateAlias();
		BpmFormTemplate bpmFormTemplate = bpmFormTemplateService.getByTemplateAlias(templateAlias);
		String template = bpmFormTemplate.getHtml();

		List<SQLClause> conditions = SqlParseUtil.getQueryParameters(sysQueryView, true);

		List<JSONObject> navButtons = getButtons(sysQueryView.getButtons(), 0);
		List<JSONObject> rowButtons = getButtons(sysQueryView.getButtons(), 1);

		String sortField = "";
		String sortSeq = "";

		SysQueryFieldSetting fieldSetting = getDefaultSortField(sysQueryView);
		if (fieldSetting != null) {
			sortField = fieldSetting.getMetaField().getFieldName();
			sortSeq = fieldSetting.getSortSeq();
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sysQueryView", sysQueryView);
		params.put("conditions", conditions);
		params.put("ctx", contextPath);
		params.put("navButtons", navButtons);
		params.put("rowButtons", rowButtons);

		params.put("sortField", sortField);
		params.put("sortSeq", sortSeq);

		String html = "";
		try {
			html = freemarkEngine.parseByStringTemplate(params, template);
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return html;
	}

	/**
	 * 获取默认的排序列。
	 * 
	 * @param sysQueryView
	 * @return
	 */
	private SysQueryFieldSetting getDefaultSortField(SysQueryView sysQueryView) {
		List<SysQueryFieldSetting> list = sysQueryView.getFieldSettings();
		for (SysQueryFieldSetting setting : list) {
			if (setting.getDefaultSort() != null && setting.getDefaultSort().intValue() == 1) {
				return setting;
			}
		}
		return null;
	}

	/**
	 * 获取视图配置的按钮。
	 * 
	 * @param buttons
	 * @param inRow
	 * @return
	 */
	private List<JSONObject> getButtons(String buttons, int inRow) {
		List<JSONObject> list = new ArrayList<JSONObject>();
		if (StringUtil.isEmpty(buttons))
			return list;
		JSONArray array = JSONArray.fromObject(buttons);
		for (int i = 0; i < array.size(); i++) {
			JSONObject jsonObject = (JSONObject) array.get(i);
			if (jsonObject.getInt("inRow") == inRow) {
				list.add(jsonObject);
			}
		}
		return list;
	}

	/**
	 * 取得分页数据。
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param params
	 * @param queryView
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public PageList getPageList(int currentPage, int pageSize, Map<String, Object> params, SysQueryView sysQueryView, String sortField, String sortSeq) throws Exception {
		Long viewId = sysQueryView.getId();
		String sqlAlias = sysQueryView.getSqlAlias();
		SysQuerySqlDef sqlDef = sysQuerySqlDefService.getByAlias(sqlAlias);
		//获取虚拟列列表。
		List<SysQueryMetaField> fieldList = sysQueryMetaFieldService.getListByView(viewId, -1);

		Map<String, SysQueryMetaField> fieldMap = convertMetaField(fieldList);

		String dbType = getDbType(sqlDef.getAlias());
		String dsName = sqlDef.getDsname();

		String sql = getSql(params, sysQueryView, sqlDef, dbType, sortField, sortSeq);

		PageList list = new PageList();
		//获取SQL.
		if (sysQueryView.getNeedPage() == 1) {
			list = JdbcTemplateUtil.getPage(sqlDef.getDsname(), currentPage, pageSize, sql, params);
		}
		//不分页
		else {
			NamedParameterJdbcTemplate jdbcTemplate = JdbcTemplateUtil.getNamedParameterJdbcTemplate(dsName);
			List tmpList = jdbcTemplate.queryForList(sql, params);
			PageBean pageBean = new PageBean(1, tmpList.size());
			pageBean.setTotalCount(tmpList.size());
			list.addAll(tmpList);
			list.setPageBean(pageBean);
		}

		handData(list, fieldMap, dsName);
		return list;
	}

	@SuppressWarnings("rawtypes")
	private void handData(PageList list, Map<String, SysQueryMetaField> fieldMap, String dsName) throws Exception {
		JdbcTemplate jdbcTemplate = JdbcTemplateUtil.getNewJdbcTemplate(dsName);
		Map<String, Map<String, String>> valMap = new HashMap<String, Map<String, String>>();
		for (Object o : list) {
			Map row = (Map) o;
			handRow(row, fieldMap, valMap, jdbcTemplate);
		}
	}

	/**
	 * 处理行数据。
	 * 
	 * @param row
	 * @param fieldMap
	 */
	@SuppressWarnings("unchecked")
	private void handRow(Map row, Map<String, SysQueryMetaField> fieldMap, Map<String, Map<String, String>> valMap, JdbcTemplate jdbcTemplate) {
		Map<String, SysQueryMetaField> vituralMap = new HashMap<String, SysQueryMetaField>();
		Collection<SysQueryMetaField> fieldList = fieldMap.values();
		for (SysQueryMetaField field : fieldList) {
			if (field.getIsVirtual() == 0)
				continue;
			vituralMap.put(field.getVirtualFrom().toUpperCase(), field);
		}
		Map newMap = new HashMap();
		for (Object obj : row.keySet()) {
			String key = obj.toString();
			String keyUpper = key.toUpperCase();
			SysQueryMetaField field = fieldMap.get(keyUpper);
			//日期处理
			if (field != null && field.getDataType() != null && ColumnModel.COLUMNTYPE_DATE.equalsIgnoreCase(field.getDataType())) {
				String dataFormat = field.getDateFormat();
				Object tmp = row.get(key);
				if (tmp instanceof Date) {
					if (StringUtil.isEmpty(dataFormat))
						dataFormat = "yyyy-MM-dd";
					String tmpDate = TimeUtil.getDateTimeString((Date) tmp, dataFormat);
					newMap.put(keyUpper, tmpDate);
					continue;
				}
			}
			if (field != null) {
				newMap.put(keyUpper, row.get(key));
			}

			//虚拟列
			if (vituralMap.containsKey(keyUpper)) {
				SysQueryMetaField virtualField = vituralMap.get(keyUpper);
				int type = Integer.parseInt(virtualField.getResultFromType());
				switch (type) {
				//数据字典
				case 1:

					break;
				//sql语句
				case 2:
					handSql(newMap, row, virtualField, valMap, jdbcTemplate);
					break;
				//下拉框
				case 3:
					handSelectBox(newMap, row, virtualField, valMap);
					break;
				}
			}
		}
		row.clear();
		row.putAll(newMap);
	}

	/**
	 * 处理下拉消息框。
	 * 
	 * @param row
	 * @param field
	 * @param fieldMap
	 */
	private void handSelectBox(Map newMap, Map row, SysQueryMetaField virtualField, Map<String, Map<String, String>> valMap) {
		String name = virtualField.getVirtualFrom().toUpperCase();
		String fieldName = virtualField.getName().toUpperCase();

		String val = row.get(name).toString();
		//是否存在缓存
		boolean rtn = isExistCache(fieldName, valMap, val);
		if (rtn) {
			//从缓存获取
			String tmp = getCache(fieldName, valMap, val);
			newMap.put(fieldName, tmp);
			return;
		}

		String content = virtualField.getResultFrom();
		//[{"$$hashKey":"005","optionKey":"男","optionValue":"男性"},{"$$hashKey":"009","optionKey":"女","optionValue":"女性"}]
		boolean isFind = false;
		JSONArray jsonAry = JSONArray.fromObject(content);
		for (Object obj : jsonAry) {
			JSONObject jsonObject = (JSONObject) obj;
			String key = jsonObject.getString("optionKey");
			if (val.equals(key)) {
				String realVal = jsonObject.getString("optionValue");
				newMap.put(fieldName, realVal);
				putToCache(fieldName, valMap, val, realVal);
				isFind = true;
				break;
			}
		}
		//没有找到匹配的值时实际的列值。
		if (!isFind) {
			newMap.put(fieldName, val);
			putToCache(fieldName, valMap, val, val);
		}
	}

	/**
	 * 获取缓存
	 * 
	 * @param cacheKey
	 * @param valMap
	 * @param fromVal
	 * @return
	 */
	private String getCache(String cacheKey, Map<String, Map<String, String>> valMap, String fromVal) {
		Map<String, String> map = valMap.get(cacheKey);
		return map.get(fromVal);
	}

	/**
	 * 缓存是否存在
	 * 
	 * @param cacheKey
	 * @param valMap
	 * @param fromVal
	 * @return
	 */
	private boolean isExistCache(String cacheKey, Map<String, Map<String, String>> valMap, String fromVal) {
		if (!valMap.containsKey(cacheKey))
			return false;
		Map<String, String> map = valMap.get(cacheKey);
		if (map.containsKey(fromVal)) {
			return true;
		}
		return false;
	}

	/**
	 * 放置缓存。
	 * 
	 * @param cacheKey
	 * @param valMap
	 * @param fromVal
	 * @param val
	 */
	private void putToCache(String cacheKey, Map<String, Map<String, String>> valMap, String fromVal, String val) {
		Map<String, String> map = null;
		if (valMap.containsKey(cacheKey)) {
			map = valMap.get(cacheKey);
			map.put(fromVal, val);
		} else {
			map = new HashMap<String, String>();
			map.put(fromVal, val);
			valMap.put(cacheKey, map);
		}
	}

	@SuppressWarnings("unchecked")
	private void handSql(Map newMap, Map row, SysQueryMetaField virtualField, Map<String, Map<String, String>> valMap, JdbcTemplate jdbcTemplate) {

		String fieldName = virtualField.getName().toUpperCase();
		//select userName from sys_user where userid=#con#
		String fromVal = row.get(virtualField.getVirtualFrom()).toString();
		//判断是否存在
		boolean rtn = isExistCache(fieldName, valMap, fromVal);
		if (rtn) {
			String realVal = getCache(fieldName, valMap, fromVal);
			newMap.put(fieldName, realVal);
		}
		//取值
		String sql = virtualField.getResultFrom().replace("#CON#", fromVal);
		String realVal = fromVal;
		//如果返回结果出错，那么返回原始值。
		try {
			realVal = jdbcTemplate.queryForObject(sql, String.class);
		} catch (EmptyResultDataAccessException ex) {
		} catch (IncorrectResultSizeDataAccessException ex) {
		}
		newMap.put(fieldName, realVal);
		//添加缓存
		putToCache(fieldName, valMap, fromVal, realVal);
	}

	/**
	 * 将元数据列表转成MAP结构。
	 * 
	 * @param fieldList
	 * @return
	 */
	private Map<String, SysQueryMetaField> convertMetaField(List<SysQueryMetaField> fieldList) {
		Map<String, SysQueryMetaField> map = new HashMap<String, SysQueryMetaField>();
		for (SysQueryMetaField field : fieldList) {
			map.put(field.getName().toUpperCase(), field);
		}
		return map;
	}

	/**
	 * 根据数据源别名获取数据库类型。
	 * 
	 * @param dsAlias
	 * @return
	 */
	private String getDbType(String dsAlias) {
		String dbType = AppConfigUtil.get("jdbc.dbType");
		if (StringUtil.isNotEmpty(dsAlias) && DataSourceUtil.DEFAULT_DATASOURCE.equalsIgnoreCase(dsAlias)) {
			SysDataSource source = sysDataSourceService.getByAlias(dsAlias);
			dbType = source.getDbType();
		}
		return dbType;
	}

	/**
	 * 返回SQL语句。
	 * 
	 * @param params
	 * @param sysQueryView
	 * @return
	 * @throws TemplateException
	 * @throws IOException
	 */
	private String getSql(Map<String, Object> params, SysQueryView sysQueryView, SysQuerySqlDef sqlDef, String dbType, String sortField, String sortSeq) throws TemplateException, IOException {
		short filterType = sysQueryView.getFilterType();
		String sql = "";
		//字段组合
		if (filterType == 1) {
			String filterJson = sysQueryView.getFilter();
			sql = sqlDef.getSql();
			sql += " where 1=1  ";
			String sqlFilter = SqlParseUtil.getFilterSQL(filterJson, dbType);
			boolean isEmpty = StringUtil.isEmpty(sqlFilter);
			String sqlQuery = SqlParseUtil.getQuerySQL(sysQueryView, isEmpty, params, dbType);
			sql = sql + (isEmpty ? "" : " and ") + sqlFilter + sqlQuery + SqlParseUtil.getSortSql(sortField, sortSeq);
			logger.debug(sql);
		}
		//sql语句
		else if (filterType == 2) {
			sql = getScriptSql(params, sysQueryView);
			logger.debug(sql);
		}
		//追加SQL
		else if (filterType == 3) {
			//sql=sqlDef.getSql() +" " + getScriptSql(params, sysQueryView);
			sql = sqlDef.getSql();
			sql += " where 1=1  ";
			String sqlQuery = SqlParseUtil.getQuerySQL(sysQueryView, true, params, dbType);
			sql = sql + getScriptSql(sysQueryView) + sqlQuery + SqlParseUtil.getSortSql(sortField, sortSeq);
			logger.debug(sql);
		}else {
			sql=sqlDef.getSql();
		}
		return sql;
	}

	/**
	 * 获取从脚本构建sql语句。
	 * 
	 * @param params
	 * @param sysQueryView
	 * @return
	 */
	private String getScriptSql(Map<String, Object> params, SysQueryView sysQueryView) {
		String script = sysQueryView.getFilter();
		//先替换脚本中的变量例如 [CUR_USER].
		List<CommonVar> list = CommonVar.getCurrentVars(true);
		for (CommonVar var : list) {
			script = script.replace(var.getAlias(), var.getValue().toString());
		}
		Map map = new HashMap();
		//一般用户判断。
		map.put("params", params);
		map.putAll(params);
		String sql = groovyScriptEngine.executeString(script, map);
		logger.debug(sql);
		return sql;
	}

	/**
	 * 获取从脚本构建sql语句。
	 * 
	 * @param params
	 * @param sysQueryView
	 * @return
	 */
	private String getScriptSql(SysQueryView sysQueryView) {
		String script = sysQueryView.getFilter();
		//先替换脚本中的变量例如 [CUR_USER].
		List<CommonVar> list = CommonVar.getCurrentVars(true);
		for (CommonVar var : list) {
			script = script.replace(var.getAlias(), var.getValue().toString());
		}
		return script;
	}

	public void removeBySQLAlias(String sqlAlias) {
		List<SysQueryView> viewList = dao.getListBySqlAlias(sqlAlias);
		for (SysQueryView sv : viewList) {
			sysQueryFieldSettingService.removeBySysQueryViewId(sv.getId());
		}
		dao.removeBySQLAlias(sqlAlias);
	}

	public void delByIds(Long[] lAryId) {
		for (Long id : lAryId) {
			sysQueryFieldSettingService.removeBySysQueryViewId(id);
			dao.delById(id);
		}
	}

	public boolean isAliasExists(String alias, String sqlAlias, Long id) {
		return dao.isAliasExists(alias, sqlAlias, id);
	}

	public void saveSortList(String list) throws Exception {
		JSONArray ja = JSONArray.fromObject(list);
		Iterator iterator = ja.iterator();
		while (iterator.hasNext()) {
			JSONObject next = (JSONObject) iterator.next();
			long id = next.getLong("id");
			SysQueryView sv = dao.getById(id);
			short sn = (short) next.getInt("sn");
			sv.setSn(sn);
			dao.update(sv);
		}
	}

	/**
	 * 生成EXCEL.
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param queryParams
	 * @param sysQueryView
	 * @param sortField
	 * @param orderSeq
	 * @param exportFields
	 * @return
	 * @throws Exception
	 */
	public HSSFWorkbook genExcel(int currentPage, int pageSize, Map queryParams, SysQueryView sysQueryView, String sortField, String orderSeq, String exportFields) throws Exception {

		//获取虚拟列列表。
		List<SysQueryMetaField> fieldList = sysQueryMetaFieldService.getListByView(sysQueryView.getId(), -1);
		Map<String, SysQueryMetaField> fieldMap = convertMetaField(fieldList);

		String[] aryFields = exportFields.split(",");
		Map<String, String> exportMaps = new LinkedHashMap<String, String>();
		for (String field : aryFields) {
			SysQueryMetaField metaField = fieldMap.get(field);
			exportMaps.put(field, metaField.getFieldDesc());
		}

		PageList pageList = getPageList(currentPage, pageSize, queryParams, sysQueryView, sortField, orderSeq);

		HSSFWorkbook book = ExcelUtil.exportExcel(sysQueryView.getName(), 24, exportMaps, pageList);

		return book;

	}

	/**
	 * 初始化所有的模板
	 * 
	 * @param contextPath
	 */
	public void updatAllTemplate(String contextPath) {
		List<SysQueryView> list = dao.getAllList();
		for (SysQueryView sysQueryView : list) {
			String sqlAlias = sysQueryView.getSqlAlias();
			List<SysQueryMetaField> metaFields = sysQueryMetaFieldService.getListBySqlAlias(sqlAlias);
			Map<String, SysQueryMetaField> fieldMap = convertToMap(metaFields);

			List<SysQueryFieldSetting> fieldSettings1 = new ArrayList<SysQueryFieldSetting>();
			List<SysQueryFieldSetting> fieldSettings2 = sysQueryFieldSettingService.getBySysQueryViewId(sysQueryView.getId());

			for (SysQueryFieldSetting fieldSetting : fieldSettings2) {
				fieldSetting.setId(null);
				fieldSetting.setViewId(sysQueryView.getId());
				//设置字段
				fieldSetting.setMetaField(fieldMap.get(fieldSetting.getFieldName()));
				fieldSettings1.add(fieldSetting);
			}
			sysQueryView.setMetaFieldMap(metaFields);
			sysQueryView.setFieldSettings(fieldSettings1);
			//生成HTML模版
			String templateHtml = this.generateTemplate(sysQueryView, contextPath);
			sysQueryView.setTemplate(templateHtml);
			this.update(sysQueryView);
		}
	}

}
