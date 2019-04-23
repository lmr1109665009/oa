package com.suneee.platform.controller.system;

import java.io.PrintWriter;
import java.util.ArrayList;

import java.util.List;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.db.datasource.DbContextHolder;
import com.suneee.core.db.datasource.JdbcTemplateUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.suneee.platform.annotion.Action;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.db.datasource.DbContextHolder;
import com.suneee.core.db.datasource.JdbcTemplateUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysDataSource;
import com.suneee.platform.model.system.SysQueryMetaField;
import com.suneee.platform.model.system.SysQuerySqlDef;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysDataSourceService;
import com.suneee.platform.service.system.SysQueryMetaFieldService;
import com.suneee.platform.service.system.SysQuerySqlDefService;
import com.suneee.platform.xml.util.MsgUtil;
/**
 *<pre>
 * 对象功能:自定义SQL定义 控制器类
 * 开发公司:宏天软件
 * 开发人员:ray
 * 创建时间:2015-06-08 16:02:04
 *</pre>
 */
@Controller
@RequestMapping("/platform/system/sysQuerySqlDef/")
public class SysQuerySqlDefController extends BaseController
{
	@Resource
	private SysQuerySqlDefService sysQuerySqlDefService;
	
	@Resource
	private GlobalTypeService globalTypeService;
	
	@Resource
	private SysQueryMetaFieldService sysQueryMetaFieldService;
	@Resource
	private SysDataSourceService sysDataSourceService;
	
	/**
	 * 添加或更新自定义SQL定义。
	 * @param request
	 * @param response
	 * @param sysQuerySqlDef 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新自定义SQL定义")
	public void save(HttpServletRequest request, HttpServletResponse response ) throws Exception
	{
		String resultMsg=null;		
		try{
			String jsonStr = RequestUtil.getString(request, "jsonStr", "", false);
			SysQuerySqlDef sysQuerySqlDef = sysQuerySqlDefService.getSysQuerySqlDef(jsonStr);

			String fieldJson = RequestUtil.getString(request, "fieldJson",  "", false);
			
			String alias=sysQuerySqlDef.getAlias();
			
			if(sysQuerySqlDef.getId()==null||sysQuerySqlDef.getId()==0){
				boolean isExist=sysQuerySqlDefService.isAliasExists(alias,0L);
				if(isExist){
					resultMsg="指定别名已经存在!";
					writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Fail);
					return;
				}
				//切换数据源
				DbContextHolder.setDataSource(sysQuerySqlDef.getDsname());
				
				List<SysQueryMetaField> fieldList=sysQuerySqlDefService.obtainFieldListBySql(sysQuerySqlDef.getSql());
				
				DbContextHolder.setDefaultDataSource();
				//切换数据源
				sysQuerySqlDef.setMetaFields(fieldList);
					
				sysQuerySqlDefService.save(sysQuerySqlDef);
				resultMsg="添加自定义SQL定义成功!";
			}else{
				boolean isExist=sysQuerySqlDefService.isAliasExists(alias,sysQuerySqlDef.getId());
				if(isExist){
					resultMsg="指定别名已经存在!";
					writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Fail);
					return;
				}
				
				List<SysQueryMetaField> pageFieldList = sysQuerySqlDefService.getSysQueryMetaFieldArr(fieldJson);	//	页面上的
				
				//切换数据源
				DbContextHolder.setDataSource(sysQuerySqlDef.getDsname());
				List<SysQueryMetaField> metaFieldList = sysQuerySqlDefService.obtainFieldListBySql(sysQuerySqlDef.getSql());	//	meta上的
				DbContextHolder.setDefaultDataSource();
				
				List<SysQueryMetaField> fieldList = sysQuerySqlDefService.getMetaFieldList(pageFieldList,metaFieldList);
				
				sysQuerySqlDef.setMetaFields(fieldList);
				sysQuerySqlDefService.save(sysQuerySqlDef);
			    
				resultMsg="更新自定义SQL定义成功!";
			}
			writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Success);
		}catch(Exception e){
			e.printStackTrace();
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得自定义SQL定义分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看自定义SQL定义分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		Long categoryId = RequestUtil.getLong(request, "categoryId",0);
		QueryFilter filter = new QueryFilter(request,"sysQuerySqlDefItem");
		if(categoryId!=0)filter.addFilter("categoryid", categoryId);
		
		List<SysQuerySqlDef> list=sysQuerySqlDefService.getAll(filter);
		ModelAndView mv=this.getAutoView().addObject("sysQuerySqlDefList",list);
		return mv.addObject("categoryId",categoryId);
	}
	
	/**
	 * 删除自定义SQL定义
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除自定义SQL定义")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			sysQuerySqlDefService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除自定义SQL定义成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑自定义SQL定义
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑自定义SQL定义")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		String categoryId = RequestUtil.getString(request, "categoryId");
		Long id=RequestUtil.getLong(request,"id",0L);
		
		SysQuerySqlDef sysQuerySqlDef=sysQuerySqlDefService.getById(id);
		if(sysQuerySqlDef==null){
			sysQuerySqlDef=new SysQuerySqlDef();
			sysQuerySqlDef.setCategoryid(categoryId);
		}
		
		List<SysDataSource> sysDataSources = sysDataSourceService.getAllAndDefault();
		List<SysQueryMetaField> sysQueryFields = new ArrayList<SysQueryMetaField>();
		List<GlobalType> globalTypesDICList = globalTypeService.getByCatKey(GlobalType.CAT_DIC, true);
		
		if (id != 0L) {
			sysQueryFields = sysQueryMetaFieldService.getListBySqlId(id);
		}
		if(BeanUtils.isEmpty(sysDataSources)||sysDataSources.size()==0){
			sysDataSources = new ArrayList<SysDataSource>();
		}
		
		com.alibaba.fastjson.JSONObject jsonObj= (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.toJSON(sysQuerySqlDef);
		
		return getAutoView().addObject("sysQuerySql",jsonObj)
				.addObject("sysQueryFields", JSONArray.fromObject(sysQueryFields))
				.addObject("dsList", JSONArray.fromObject(sysDataSources))
				.addObject("globalTypesDICList", JSONArray.fromObject(globalTypesDICList));
	}

	/**
	 * 取得自定义SQL定义明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看自定义SQL定义明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		SysQuerySqlDef sysQuerySqlDef = sysQuerySqlDefService.getById(id);	
		return getAutoView().addObject("sysQuerySqlDef", sysQuerySqlDef);
	}
	
	/**
	 * 验证创建视图查询语句 是否正确
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("validSql")
	@ResponseBody
	public boolean validSql(HttpServletRequest request, HttpServletResponse response) throws Exception {
 
		String sql = request.getParameter("sql");
		String dsalias = RequestUtil.getString(request, "dsalias", "");
		String mapStr = RequestUtil.getString(request, "map", "{}");// 参数map
		JSONObject map = JSONObject.fromObject(mapStr);
		Boolean rollback = RequestUtil.getBoolean(request, "rollback", true);// 是否回滚
		try {
			DbContextHolder.setDataSource(dsalias);
			sql=explainSql(sql, map);
			Boolean b = JdbcTemplateUtil.executeSql(sql, rollback);
			return b;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 解释Sql，用jsonObject的map来解释sql中的特殊字符
	 * @param sql
	 * @param jsonObject
	 * @return  String
	 */
	private  String explainSql(String sql, JSONObject jsonObject) {
		// 拼装sql
		for (Object obj : jsonObject.keySet()) {
			String key = obj.toString();
			String val = jsonObject.getString(key);
			sql = sql.replace(key, val);
		}
		return sql;
	}
	
	/**
	 * 设置 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("setCategory")
	public void setCategory(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter writer = response.getWriter();
		Long categoryId = RequestUtil.getLong(request, "categoryId", 0);
		String ids = RequestUtil.getString(request, "ids");
		String[] aryId = ids.split(",");
		try {
			sysQuerySqlDefService.updCategory(categoryId, aryId);
			writeResultMessage(writer, new ResultMessage(ResultMessage.Success,"设置成功！"));
		} catch (Exception ex) {
			String msg = ExceptionUtil.getExceptionMessage(ex);
			writeResultMessage(writer, new ResultMessage(ResultMessage.Fail,
					msg));
		}
		
	}
	
	/**
	 * 导出表定义xml。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("exportXml")
	@Action(description = "导出自定义Sql查询表", detail = "导出自定义Sql查询表:" + "<#list StringUtils.split(tableIds,\",\") as item>" + "<#assign entity=bpmFormTableService.getById(Long.valueOf(item))/>" + "【${entity.tableDesc}(${entity.tableName})】" + "</#list>")
	public void exportXml(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String strXml = null;
		String fileName = null;
		Long[] tableIds = RequestUtil.getLongAryByStr(request, "tableIds");
		

		try {
			// 没有选择任何项 导出全部
			if (BeanUtils.isEmpty(tableIds)) {
				List<SysQuerySqlDef> list = sysQuerySqlDefService.getAll();
				if (BeanUtils.isNotEmpty(list)) {
					strXml = sysQuerySqlDefService.exportXml(list);
					fileName = "全部自定义Sql查询记录_"
							+ DateFormatUtil.getNowByString("yyyyMMddHHmmdd")+".xml";
				}
			} else {
				strXml = sysQuerySqlDefService.exportXml(tableIds);
				fileName = DateFormatUtil.getNowByString("yyyyMMddHHmmdd")+".xml";
				if (tableIds.length == 1) {
					SysQuerySqlDef sysQuerySqlDef = sysQuerySqlDefService
							.getById(tableIds[0]);
					fileName = sysQuerySqlDef.getName() + "_" + fileName;
				}  else {
					fileName = "自定义Sql查询记录_" + fileName;
				}
			}
			FileUtil.downLoad(request, response, strXml, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

		/**
		 * 导入自定义Sql查询的XML
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("importXml")
		@Action(description = "导入自定义Sql查询")
		public void importXml(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
			MultipartFile fileLoad = request.getFile("xmlFile");
			ResultMessage message = null;
			try {
				sysQuerySqlDefService.importXml(fileLoad.getInputStream());
				message = new ResultMessage(ResultMessage.Success, MsgUtil.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				message = new ResultMessage(ResultMessage.Fail, "导入文件异常,请检查文件格式!");
			}
			writeResultMessage(response.getWriter(), message);
		}
	
	
}

