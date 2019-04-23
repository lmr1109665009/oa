package com.suneee.platform.controller.system;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.engine.IScript;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.*;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.core.engine.IScript;
import com.suneee.core.log.SysAuditThreadLocalHolder;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import com.suneee.core.web.ResultMessage;
import com.suneee.platform.model.system.ConditionScript;
import com.suneee.platform.model.system.Identity;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.system.ConditionScriptService;
import com.suneee.platform.xml.util.MsgUtil;
/**
 *<pre>
 * 对象功能:系统条件脚本 控制器类
 * 开发公司:hotent
 * 开发人员:heyifan
 * 创建时间:2013-04-05 11:34:56
 *</pre>
 */
@Controller
@RequestMapping("/platform/system/conditionScript/")
@Action(ownermodel=SysAuditModelType.PROCESS_AUXILIARY)
public class ConditionScriptController extends BaseController
{
	@Resource
	private ConditionScriptService conditionScriptService;
	
	
	/**
	 * 添加或更新系统条件脚本。
	 * @param request
	 * @param conditionScript 添加或更新的实体
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新系统条件脚本",
			detail="<#if isAdd>添加<#else>更新</#if>" +
					"系统条件脚本:" +
					"【${conditionScript.methodDesc}】"
	)
	@ResponseBody
	public ResultVo save(HttpServletRequest request, ConditionScript conditionScript) throws Exception
	{
		
		//添加系统日志信息 -E
		String resultMsg=null;
		ResultVo resultVo = new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		try{
			if(conditionScript.getId()==null||conditionScript.getId()==0){
				conditionScript.setId(UniqueIdUtil.genId());
				conditionScriptService.add(conditionScript);
				resultMsg="添加系统条件脚本成功";
			}else{
			    conditionScriptService.update(conditionScript);
				resultMsg="更新系统条件脚本成功";
			}
			//添加系统日志信息 -B
			try {
				SysAuditThreadLocalHolder.putParamerter("conditionScript", conditionScript);
				SysAuditThreadLocalHolder.putParamerter("isAdd", conditionScript.getId()==null||conditionScript.getId()==0);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
			resultVo.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
			resultVo.setMessage(resultMsg);
			return  resultVo;
		}catch(Exception e){
			resultVo.setStatus(ResultVo.COMMON_STATUS_FAILED);
			resultVo.setMessage(resultMsg+","+e.getMessage());
			return  resultVo;
		}
	}
	
	/**
	 * 取得 ConditionScript 实体 
	 * @param request
	 * @return
	 * @throws Exception
	 */
    protected ConditionScript getFormObject(HttpServletRequest request) throws Exception {
    
    	JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher((new String[] { "yyyy-MM-dd" })));
    
		String json= RequestUtil.getString(request, "json");
		JSONObject obj = JSONObject.fromObject(json);
		
		ConditionScript conditionScript = (ConditionScript)JSONObject.toBean(obj, ConditionScript.class);
		
		return conditionScript;
    }
	
	/**
	 * 取得系统条件脚本分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看系统条件脚本分页列表",detail="查看系统条件脚本分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<ConditionScript> list=conditionScriptService.getAll(new QueryFilter(request,"conditionScriptItem"));
		ModelAndView mv=this.getAutoView().addObject("conditionScriptList",list);
		
		return mv;
	}
	
	/**
	 * 删除系统条件脚本
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除系统条件脚本",
			execOrder=ActionExecOrder.BEFORE,
			detail="删除系统条件脚本" +
					"<#list StringUtils.split(id,\",\") as item>" +
						"<#assign entity=conditionScriptService.getById(Long.valueOf(item))/>" +
						"【${entity.methodDesc}】"+
					"</#list>"
	)
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			conditionScriptService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除系统条件脚本成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑系统条件脚本
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="添加或编辑系统条件脚本",
			detail="<#if isAdd>添加系统条件脚本<#else>"+
					"编辑系统条件脚本：" +
					"<#assign entity=conditionScriptService.getById(Long.valueOf(id))/>" +
					"【${entity.methodDesc}】</#if>")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		//获取IScript的实现类
		List<Class> implClasses = AppUtil.getImplClass(IScript.class);
		Long id=RequestUtil.getLong(request,"id");
		String returnUrl=RequestUtil.getPrePage(request);
		ConditionScript conditionScript=conditionScriptService.getById(id);
		//添加系统日志信息 -B
		SysAuditThreadLocalHolder.putParamerter("isAdd", conditionScript==null );
		return getAutoView().addObject("conditionScript",conditionScript)
							.addObject("implClasses",implClasses)
							.addObject("returnUrl", returnUrl);
	}

	/**
	 * 取得系统条件脚本明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看系统条件脚本明细",detail="查看系统条件脚本明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		long id=RequestUtil.getLong(request,"id");
		ConditionScript conditionScript = conditionScriptService.getById(id);	
		return getAutoView().addObject("conditionScript", conditionScript);
	}
	
	
	/**
	 * 取得系统条件脚本明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getJson")
	@Action(description="取系统条件脚本",detail="取系统条件脚本")
	@ResponseBody
	public Map<String,Object> getJson(HttpServletRequest request, HttpServletResponse response)
	{	
		Map<String,Object> map = new HashMap<String, Object>();
		
		int status = 0;
		try{
		long id=RequestUtil.getLong(request,"id");
			ConditionScript conditionScript = conditionScriptService.getById(id);
			map.put("conditionScript", conditionScript);
		}catch (Exception e) {
			status=-1;
			e.printStackTrace();
		}
		map.put("status",status);
		return map;
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
			JSONArray jarray = conditionScriptService.getMethodsByName(name);
			jobject.accumulate("result", true).accumulate("methods", jarray);
		}
		catch(Exception ex){
			jobject.accumulate("result", false).accumulate("message", ex.getMessage());
		}
		return jobject.toString();
	}
	
	/**
	 * 验证脚本是否有效
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("validScriptIsEnable")
	@ResponseBody
	public String validScriptIsEnable(HttpServletRequest request, HttpServletResponse response) throws Exception{
		JSONObject jobject = new JSONObject(); 		
		try{
			String message = conditionScriptService.validScriptIsEnable();
			jobject.accumulate("result", true).accumulate("message", message);
		}
		catch(Exception ex){
			jobject.accumulate("result", false).accumulate("message", ex.getMessage());
		}
		return jobject.toString();
	}
	
	/**
	 * 获取所有 有效的条件脚本
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getConditionScript")
	@ResponseBody
	public String getConditionScript(HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<ConditionScript> list = conditionScriptService.getConditionScript();
		JSONArray jarray = JSONArray.fromObject(list);
		return jarray.toString();
	}
	
	/**
	 * 与dialog页面一起使用。 获取所有 有效的条件脚本，供选择。
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("selector")
	@Action(description="选择器",detail="选择器")
	public ModelAndView selector(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<ConditionScript> conditionScriptList=conditionScriptService.getAll(new QueryFilter(request,"conditionScriptItem"));
		ModelAndView mv=this.getAutoView().addObject("conditionScriptList",conditionScriptList);
		
		return mv;
	}
	
	/**
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("addDialog")
	@Action(description="添加对话框",detail="添加对话框")
	public ModelAndView addDialog(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		Long defId = RequestUtil.getLong(request, "defId", 0L);
		ModelAndView mv=this.getAutoView().addObject("defId",defId);
		return mv;
	}
	
	
	/**
	 * 导出条件脚本xml。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("exportXml")
	@Action(description = "导出条件脚本", detail = "导出条件脚本:" + "<#list StringUtils.split(tableIds,\",\") as item>" + "<#assign entity=bpmFormTableService.getById(Long.valueOf(item))/>" + "【${entity.tableDesc}(${entity.tableName})】" + "</#list>")
	public void exportXml(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String strXml = null;
		String fileName = null;
		Long[] tableIds = RequestUtil.getLongAryByStr(request, "tableIds");
		List<ConditionScript> list = conditionScriptService.getAll();
		try {
			if (BeanUtils.isEmpty(tableIds)) {
				strXml = conditionScriptService.exportXml(list);
				fileName = "全部条件脚本_"
						+ DateFormatUtil.getNowByString("yyyyMMddHHmmdd")
						+ ".xml";
			} else {

				strXml = conditionScriptService.exportXml(tableIds);
				fileName = DateFormatUtil.getNowByString("yyyyMMddHHmmdd")
						+ ".xml";
				if (tableIds.length == 1) {
					ConditionScript conditionScript = conditionScriptService
							.getById(tableIds[0]);
					fileName = conditionScript.getMethodName() + "_" + fileName;
				} else if (tableIds.length == list.size()) {
					fileName = "全部条件脚本_" + fileName;
				} else {
					fileName = "多条条件脚本_" + fileName;
				}
			}
			FileUtil.downLoad(request, response, strXml, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	/**
	 * 导入条件脚本的XML
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("importXml")
	@Action(description = "导入条件脚本")
	public void importXml(MultipartHttpServletRequest request,
			HttpServletResponse response) throws Exception {
		MultipartFile fileLoad = request.getFile("xmlFile");
		ResultMessage message = null;
		try {
			conditionScriptService.importXml(fileLoad.getInputStream());
			message = new ResultMessage(ResultMessage.Success,
					MsgUtil.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message = new ResultMessage(ResultMessage.Fail, "导入文件异常，请检查文件格式！");
		}
		writeResultMessage(response.getWriter(), message);
	}

}
