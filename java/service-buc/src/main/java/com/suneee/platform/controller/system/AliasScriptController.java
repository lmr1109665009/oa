package com.suneee.platform.controller.system;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.db.IRollBack;
import com.suneee.core.db.RollbackJdbcTemplate;
import com.suneee.core.engine.GroovyScriptEngine;
import com.suneee.core.engine.IScript;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.*;
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

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IRollBack;
import com.suneee.core.db.RollbackJdbcTemplate;
import com.suneee.core.engine.GroovyScriptEngine;
import com.suneee.core.engine.IScript;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.AliasScript;
import com.suneee.platform.model.system.PersonScript;
import com.suneee.platform.service.system.AliasScriptService;
import com.suneee.platform.xml.util.MsgUtil;
/**
 *<pre>
 * 对象功能:自定义别名脚本表 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2013-12-19 11:26:03
 *</pre>
 */
@Controller
@RequestMapping("/platform/system/aliasScript/")
public class AliasScriptController extends BaseController
{
	@Resource
	private RollbackJdbcTemplate rollbackJdbcTemplate;
	
	@Resource
	private AliasScriptService aliasScriptService;
	
	@Resource
	private GroovyScriptEngine groovyScriptEngine;
	
	/**
	 * 添加或更新自定义别名脚本表。
	 * @param request
	 * @param response
	 * @param aliasScript 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新自定义别名脚本表")
	public void save(HttpServletRequest request, HttpServletResponse response,AliasScript aliasScript) throws Exception
	{
		String resultMsg=null;		
		try{
			//如果是自定义类型的，以下属性都为空！
			if("custom".equals(aliasScript.getScriptType())){
				aliasScript.setClassName("");
				aliasScript.setClassInsName("");
				aliasScript.setMethodName("");
				aliasScript.setMethodDesc("");
				aliasScript.setReturnType("");
				aliasScript.setArgument("");
			}else{
				aliasScript.setScriptComten("");
			}
			
			if(aliasScript.getId()==null||aliasScript.getId()==0){
				aliasScript.setId(UniqueIdUtil.genId());
				aliasScriptService.add(aliasScript);
				resultMsg="新增别名脚本成功！";
			}else{
			    aliasScriptService.update(aliasScript);
				resultMsg="修改别名脚本成功！";
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得自定义别名脚本表分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看自定义别名脚本表分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<AliasScript> list=aliasScriptService.getAll(new QueryFilter(request,"aliasScriptItem"));
		ModelAndView mv=this.getAutoView().addObject("aliasScriptList",list);
		return mv;
	}
	
	/**
	 * 删除自定义别名脚本表
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除自定义别名脚本表")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			aliasScriptService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除别名脚本成功！");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除别名脚本失败:" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑自定义别名脚本表
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑自定义别名脚本表")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		//获取IScript的实现类
		List<Class> implClasses = AppUtil.getImplClass(IScript.class);
		Long id=RequestUtil.getLong(request,"id",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		AliasScript aliasScript=aliasScriptService.getById(id);
		//添加系统日志信息 -B
		SysAuditThreadLocalHolder.putParamerter("isAdd", aliasScript==null );
		return getAutoView().addObject("aliasScript",aliasScript)
							.addObject("implClasses",implClasses)
							.addObject("returnUrl", returnUrl);		
	}

	/**
	 * 取得自定义别名脚本表明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看自定义别名脚本表明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		AliasScript aliasScript = aliasScriptService.getById(id);	
		return getAutoView().addObject("aliasScript", aliasScript);
	}
	
	/**
	 * 通过类名获取类的所有方法
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getMethodsByName")
	@ResponseBody	
	public String getMethodsByName(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String name = RequestUtil.getString(request, "name");
		JSONObject jobject = new JSONObject(); 		
		try{
			JSONArray jarray = aliasScriptService.getMethodsByName(name);
			jobject.accumulate("result", true).accumulate("methods", jarray);
		}
		catch(Exception ex){
			jobject.accumulate("result", false).accumulate("message", ex.getMessage());
		}
		return jobject.toString();
	}
	
	
	/**
	 * 测试自定义别名脚本内容
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("preview")
	@Action(description="测试自定义别名脚本内容")
	public void preview(HttpServletRequest request, HttpServletResponse response) throws Exception{
		PrintWriter writer = response.getWriter();
		final String scriptStr=RequestUtil.getString(request, "scriptStr", "");
		String alias=RequestUtil.getString(request, "alias", "");
		Map returnMap=new HashMap();
		if("".equals(scriptStr)){
			returnMap.put("isSuccess", 1);
			returnMap.put("msg", "删除");
			returnMap.put("result", "自定义别名脚本内容不能为空！");
		}else{
			//[{"paraName":"argo","paraType":"java.lang.String","paraValue":"te"},{"paraName":"arg1","paraType":"java.lang.String","paraValue":"gg"}]
			Map map=new HashMap();
			boolean mark = false;
			if(!"".equals(alias)){
				JSONArray array = JSONArray.fromObject(alias);
				for (int i = 0; i < array.size(); i++){
					JSONObject obj = array.getJSONObject(i);
					String paraValue = obj.getString("paraValue");
					String paraType = obj.getString("paraType");
					if(obj.containsKey("isRequired")&&"1".equals(obj.getString("isRequired"))){						
						if(StringUtil.isEmpty(paraValue)){
							mark = true;	
							returnMap.put("isSuccess", 2);
							returnMap.put("msg", obj.getString("paraName")+"别名脚本参数是必填的！");
							returnMap.put("result", "");
							break;
						}
					}
					
					Object javaObj = paraValue;
					//类型转换
					if(obj.containsKey("paraType")&&StringUtil.isNotEmpty(paraType)&&StringUtil.isNotEmpty(paraValue)){
						javaObj = this.getValueObject(paraValue,paraType);
					}
					map.put(obj.getString("paraName"),javaObj);
				}
			}
			if(!mark){
				Object result = rollbackJdbcTemplate.executeRollBack(new IRollBack(){
					@Override
					public Object execute(String script, Map<String, Object> map){
						return groovyScriptEngine.executeObject(scriptStr, map);
					}
				}, scriptStr, map);
				returnMap.put("isSuccess", 0);
				returnMap.put("msg","自定义别名脚本内容测试成功");
				returnMap.put("result", result);
			}
			
		}
		JSONObject obj = JSONObject.fromObject(returnMap);
		writer.print(obj.toString());
	}
	
	
	/**
	 * 测试自定义类型的别名脚本
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("customPreview")
	@Action(description="测试自定义别名脚本内容")
	public void customPreview(HttpServletRequest request, HttpServletResponse response) throws Exception{
		PrintWriter writer = response.getWriter();
		
		final String scriptComten =RequestUtil.getString(request, "scriptComten", "", false);
		Map returnMap=new HashMap();
		Map map = RequestUtil.getParameterValueMap(request, false, false);
		if("".equals(scriptComten)){
			returnMap.put("isSuccess", 1);
			returnMap.put("msg", "自定义别名脚本内容不能为空！");
			returnMap.put("result", "");
		}else{
			//回滚事务的调用方式
			Object result = rollbackJdbcTemplate.executeRollBack(new IRollBack(){
				@Override
				public Object execute(String script, Map<String, Object> map){
					return groovyScriptEngine.executeObject(scriptComten, map);
				}
			}, scriptComten, map);
		//	Object result = groovyScriptEngine.executeObject(scriptComten, map);	
			returnMap.put("isSuccess", 0);
			returnMap.put("msg", "自定义别名脚本内容测试成功");
			returnMap.put("result", result);			
		}
		JSONObject obj = JSONObject.fromObject(returnMap);
		writer.print(obj.toString());
	}
	
	
	/**
	 * 取得自定义别名脚本表明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("toCustomPreview")
	@Action(description="查看自定义别名脚本表明细")
	public ModelAndView toCustomPreview(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String scriptComten =RequestUtil.getString(request, "scriptComten", "", false);
		/*if(!"".equals(scriptComten)){
			scriptComten = new String(scriptComten.getBytes("ISO-8859-1"),"UTF-8"); 
		}*/		 	
		return getAutoView().addObject("scriptComten", scriptComten);
	}
	
	
	/**
	 * 取得自定义别名脚本表明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("toPreview")
	@Action(description="查看自定义别名脚本表明细")
	public ModelAndView toPreview(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String scriptStr=RequestUtil.getString(request, "scriptStr", "");
		String alias=RequestUtil.getString(request, "alias", "");	
		return getAutoView().addObject("scriptStr", scriptStr).addObject("alias", alias);
	}
	
	
	/**
	 * 检查自定义别名脚本的别名有没有重复
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("checkAliasName")
	@Action(description="测试自定义别名脚本的别名是否重复")
	public void checkAliasName(HttpServletRequest request, HttpServletResponse response) throws Exception{
		PrintWriter writer = response.getWriter();
		String aliasName=RequestUtil.getString(request, "aliasName", "");
		Map returnMap=new HashMap();
		if("".equals(aliasName)){
			returnMap.put("isSuccess", 2);
			returnMap.put("msg", "别名脚本的别名为空！");
		}else{
			Map map=new HashMap();		
			map.put("aliasName",aliasName);
			AliasScript as= aliasScriptService.getAliasScriptByName(aliasName);
			if(BeanUtils.isNotEmpty(as)&&BeanUtils.isNotEmpty(as.getAliasName())){
				returnMap.put("isSuccess", 1);
				returnMap.put("msg", "别名脚本的别名:"+aliasName+"已存在！");
			}else{
				returnMap.put("isSuccess", 0);
				returnMap.put("msg", "别名脚本的别名:"+aliasName+"可以使用！");
			}
		}
		JSONObject obj = JSONObject.fromObject(returnMap);
		writer.print(obj.toString());
	}
	
	
	/**
	 * 通过别名获取脚本并执行别名脚本返回数据  	
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("executeAliasScript")
	@Action(description="测试自定义别名脚本内容")
	public void executeAliasScript(HttpServletRequest request, HttpServletResponse response) throws Exception{
		PrintWriter writer = response.getWriter();
		String aliasName=RequestUtil.getString(request, "aliasName", "");	// 兼容旧版本
		Map returnMap=new HashMap();
		try {
			if("".equals(aliasName)){
				throw new RuntimeException("别名为空，执行失败！");
			}else{
				Map map=new HashMap();	
				map.put("aliasName",aliasName);
				AliasScript as= aliasScriptService.getAliasScriptByName(aliasName);
				if(as!= null && StringUtil.isNotEmpty(as.getAliasName())){
					if("1".equals(as.getEnable().toString())){       //别名脚本禁用了！（ 0为可用，1为禁用）
						throw new RuntimeException("别名脚本已被禁用!");
					}else{
						//自定义脚本类型custom
						if("custom".equals(as.getScriptType())){
							//自动获取Request中的参数
							map = RequestUtil.getParameterValueMap(request, false, false);
							Object result = groovyScriptEngine.executeObject(as.getScriptComten(), map);	
							returnMap.put("isSuccess", 0);
							returnMap.put("msg", "别名脚本执行成功！");
							returnMap.put("result", result);
						}else{
							String scriptStr ="";
							scriptStr += as.getClassInsName()+"."+as.getMethodName();
							String jsonStr = as.getArgument();
							if(StringUtil.isNotEmpty(jsonStr)&&!"[]".equals(jsonStr)){
								JSONArray arry = JSONArray.fromObject(jsonStr);
								String str = "";
								for (int i = 0; i < arry.size(); i++){
									JSONObject obj = arry.getJSONObject(i);
									String paraName = obj.getString("paraName");
									String paraValue = RequestUtil.getString(request, paraName, "");
									String paraType = obj.getString("paraType");
									if(obj.containsKey("isRequired")&&"1".equals(obj.getString("isRequired"))){						
										if(StringUtil.isEmpty(paraValue)){
											throw new RuntimeException("别名脚本:"+aliasName+"的"+paraName+"参数不能为空！");
										}
									}
									
									Object javaObj = paraValue;
									//类型转换
									if(obj.containsKey("paraType")&&StringUtil.isNotEmpty(paraType)&&StringUtil.isNotEmpty(paraValue)){
										javaObj = this.getValueObject(paraValue,paraType);
									}
									map.put(paraName,javaObj);
									str += paraName+",";
								}
								str = str.substring(0, str.length()-1);
								scriptStr += "("+str+")";
							}else{
								scriptStr +="()";
							}
							
							Object result = groovyScriptEngine.executeObject(scriptStr, map);	
							returnMap.put("isSuccess", 0);
							returnMap.put("msg", "paraName");
							returnMap.put("result", result);
						}					
					}
				}else{
					throw new RuntimeException("别名脚本异常，别名为空！");
				}
			}
		} catch (Exception e) {
			returnMap.put("isSuccess", 1);
			returnMap.put("msg", e.getMessage());
			returnMap.put("result", "");
		}
		JSONObject obj = JSONObject.fromObject(returnMap);
		writer.print(obj.toString());
	}
	
	
	/**
	 * 别名脚本开发帮助
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("goToHelp")
	@Action(description="查看自定义别名脚本表明细")
	public ModelAndView goToHelp(HttpServletRequest request, HttpServletResponse response) throws Exception
	{	
		return getAutoView();
	}
	
	/**
	 * 脚本参数的格式转换
	 * @param paraValue
	 * @param paraType
	 * @return 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	public Object getValueObject(String paraValue,String paraType){
		Object javaObj = paraValue;
		paraType = paraType.toLowerCase();
		if(paraType.contains("string")){
			javaObj = paraValue.toString();
		}else if(paraType.contains("date")){
			javaObj = TimeUtil.convertString(paraValue, "yyyy-MM-dd");
		}else if(paraType.contains("long")){
			javaObj = Long.parseLong(paraValue);
		}else if(paraType.contains("byte")){
			javaObj = paraValue.getBytes();
		}else if(paraType.contains("short")){
			javaObj = Short.parseShort(paraValue);
		}else if(paraType.contains("float")){
			javaObj = Float.parseFloat(paraValue);
		}else if(paraType.contains("char")){
			char[] a = paraValue.toCharArray();
			javaObj = a[0];
		}else if(paraType.contains("double")){
			javaObj = Double.parseDouble(paraValue);
		}else if(paraType.contains("boolean")){
			javaObj = Boolean.parseBoolean(paraValue);
		}else if(paraType.contains("int")){
			javaObj =  Integer.parseInt(paraValue);
		}else if(paraType.contains("map")){	
			//暂时不处理
		}else if(paraType.contains("list")){
			//暂时不处理
		}
		return javaObj;
	}
	
	/**
	 * 获取自定义别名脚本列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getAllAliasScripts")
	@ResponseBody
	@Action(description="获取自定义别名脚本列表，不分页")
	public List<AliasScript> getAllAliasScripts(HttpServletRequest request,HttpServletResponse response) throws Exception{
		return aliasScriptService.getAll(new QueryFilter(request, false));
	}
	
	
	/**
	 * 导出别名脚本xml。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("exportXml")
	@Action(description = "导出别名脚本", detail = "导出别名脚本:" + "<#list StringUtils.split(tableIds,\",\") as item>" + "<#assign entity=bpmFormTableService.getById(Long.valueOf(item))/>" + "【${entity.tableDesc}(${entity.tableName})】" + "</#list>")
	public void exportXml(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String strXml = null;
		String fileName = null;
		Long[] tableIds = RequestUtil.getLongAryByStr(request, "tableIds");
		List<AliasScript> list = aliasScriptService.getAll();
		try {
			if (BeanUtils.isEmpty(tableIds)) {
				if (BeanUtils.isNotEmpty(list)) {
					strXml = aliasScriptService.exportXml(list);
					fileName = "全部别名脚本_"
							+ DateFormatUtil.getNowByString("yyyyMMddHHmmdd")
							+ ".xml";
				}

			} else {

				strXml = aliasScriptService.exportXml(tableIds);
				fileName = DateFormatUtil.getNowByString("yyyyMMddHHmmdd")
						+ ".xml";
				if (tableIds.length == 1) {
					AliasScript aliasScript = aliasScriptService
							.getById(tableIds[0]);
					fileName = aliasScript.getAliasName() + "_" + fileName;
				} else if (tableIds.length == list.size()) {
					fileName = "全部别名脚本_" + fileName;
				} else {
					fileName = "多条别名脚本_" + fileName;
				}
			}
			FileUtil.downLoad(request, response, strXml, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 导入别名脚本的XML
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("importXml")
	@Action(description = "导入别名脚本")
	public void importXml(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		MultipartFile fileLoad = request.getFile("xmlFile");
		ResultMessage message = null;
		try {
			aliasScriptService.importXml(fileLoad.getInputStream());
			message = new ResultMessage(ResultMessage.Success, MsgUtil.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message = new ResultMessage(ResultMessage.Fail, "导入文件异常，请检查文件格式！");
		}
		writeResultMessage(response.getWriter(), message);
	}
	
	
	
}
