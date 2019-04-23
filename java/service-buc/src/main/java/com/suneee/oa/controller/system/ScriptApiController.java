package com.suneee.oa.controller.system;

import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.page.PageList;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.Script;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.system.ScriptService;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 对象功能:脚本管理 控制器类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2018-01-11 16:09:00
 */
@Controller
@RequestMapping("/api/system/script/")
@Action(ownermodel=SysAuditModelType.PROCESS_AUXILIARY)
public class ScriptApiController extends BaseController
{
	@Resource
	private ScriptService scriptService;
	
	
	/**
	 * 取得流程脚本（旧版本：脚本管理）分页列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@ResponseBody
	@Action(description="查看脚本管理分页列表",detail="查看脚本管理分页列表")
	public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception
	{	
		List<String> categoryList=scriptService.getDistinctCategory();
		PageList<Script> list= (PageList<Script>) scriptService.getAll(new QueryFilter(request,true));
		Map<String,Object> map = new HashMap<>();
		map.put("scriptList", PageUtil.getPageVo(list));
		map.put("categoryList",categoryList);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流程脚本列表数据成功！",map);
	}
	
	/**
	 * 删除脚本管理
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("del")
	@ResponseBody
	@Action(description="删除脚本管理",
			execOrder=ActionExecOrder.BEFORE,
			detail="删除脚本管理:" +
					"<#list StringUtils.split(id,\",\") as item>" +
					"<#assign entity=scriptService.getById(Long.valueOf(item))/>" +
					"【${entity.name}】" +
				"</#list>")
	public ResultVo del(HttpServletRequest request) throws Exception
	{
		ResultVo message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			scriptService.delByIds(lAryId);
			message=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除脚本成功!");
		}
		catch(Exception ex){
			message=new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败:" + ex.getMessage());
		}
		return message;
	}

	@RequestMapping("edit")
	@ResponseBody
	@Action(description="添加或编辑脚本管理",
			detail="<#if isAdd>添加新脚本<#else>"+
					"编辑脚本:" +
					"<#assign entity=scriptService.getById(Long.valueOf(id))/>" +
					"【${entity.name}】</#if>"
			)
	public ResultVo edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		List<String> categoryList=scriptService.getDistinctCategory();
		Script script=null;
		boolean isadd=true;
		if(id!=0){
			 script= scriptService.getById(id);
			 isadd=false;
		}else{
			script=new Script();
		}
		SysAuditThreadLocalHolder.putParamerter("isAdd", isadd);

		Map<String,Object> map = new HashMap<>();
		map.put("script",script);
		map.put("categoryList",categoryList);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取脚本信息成功！",map);
	}

	/**
	 * 取得流程脚本明细
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@ResponseBody
	@Action(description="查看脚本管理明细",detail="查看脚本管理明细")
	public ResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		long id=RequestUtil.getLong(request,"id");
		Script script = scriptService.getById(id);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流程脚本信息成功！",script);
	}

	@RequestMapping("getScriptsGroupByCategory")
	@ResponseBody
	public List<Map<String,Object>> getScriptsGroupByCategory() throws Exception {
		List<Script> list=scriptService.getAll();
		//获取所有脚本分类集合
		Set<String> categorySet = new HashSet<>();
		for(Script script:list){
			categorySet.add(script.getCategory());
		}
		//对所有脚本进行分类
		List<Map<String,Object>> resultList = new ArrayList<>();
		for(String str:categorySet){
			List<Script> scriptList = new ArrayList<>();
			for(Script script:list){
				if(str.equals(script.getCategory())){
					scriptList.add(script);
				}
			}
			Map<String,Object> categoryMap = new HashMap<>();
			categoryMap.put("category",str);
			categoryMap.put("resultList",scriptList);
			resultList.add(categoryMap);
		}
		return resultList;
	}
}
