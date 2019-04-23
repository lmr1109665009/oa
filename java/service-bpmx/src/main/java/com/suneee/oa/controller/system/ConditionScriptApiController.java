package com.suneee.oa.controller.system;

import com.suneee.core.engine.IScript;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.page.PageList;
import com.suneee.core.util.AppUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.ConditionScript;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.system.ConditionScriptService;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *<pre>
 * 对象功能:系统条件脚本 控制器类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2018-1-12 10:31:00
 *</pre>
 */
@Controller
@RequestMapping("/api/system/conditionScript/")
@Action(ownermodel=SysAuditModelType.PROCESS_AUXILIARY)
public class ConditionScriptApiController extends BaseController
{
	@Resource
	private ConditionScriptService conditionScriptService;
	

	
	/**
	 * 取得系统条件脚本分页列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@ResponseBody
	@Action(description="查看系统条件脚本分页列表",detail="查看系统条件脚本分页列表")
	public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception
	{	
		PageList<ConditionScript> list= (PageList<ConditionScript>) conditionScriptService.getAll(new QueryFilter(request,true));
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取条件脚本分页列表数据成功！", PageUtil.getPageVo(list));
	}
	
	/**
	 * 删除系统条件脚本
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@ResponseBody
	@Action(description="删除系统条件脚本",
			execOrder=ActionExecOrder.BEFORE,
			detail="删除系统条件脚本" +
					"<#list StringUtils.split(id,\",\") as item>" +
						"<#assign entity=conditionScriptService.getById(Long.valueOf(item))/>" +
						"【${entity.methodDesc}】"+
					"</#list>"
	)
	public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		ResultVo message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			conditionScriptService.delByIds(lAryId);
			message=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除系统条件脚本成功!");
		}catch(Exception ex){
			message=new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败" + ex.getMessage());
		}
		return message;
	}
	
	/**
	 * 	编辑系统条件脚本
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@ResponseBody
	@Action(description="添加或编辑系统条件脚本",
			detail="<#if isAdd>添加系统条件脚本<#else>"+
					"编辑系统条件脚本：" +
					"<#assign entity=conditionScriptService.getById(Long.valueOf(id))/>" +
					"【${entity.methodDesc}】</#if>")
	public ResultVo edit(HttpServletRequest request) throws Exception
	{
		//获取IScript的实现类
		List<Class> implClasses = AppUtil.getImplClass(IScript.class);
		Long id=RequestUtil.getLong(request,"id");
		ConditionScript conditionScript=conditionScriptService.getById(id);
		//添加系统日志信息 -B
		SysAuditThreadLocalHolder.putParamerter("isAdd", conditionScript==null );

		Map<String,Object> map = new HashMap<>();
		map.put("conditionScript",conditionScript);
		map.put("implClasses",implClasses);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取条件脚本数据成功！",map);
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
	public ResultVo getMethodsByName(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String name = RequestUtil.getString(request, "name");
		JSONArray jarray = conditionScriptService.getMethodsByName(name);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取所有方法成功！",jarray);
	}

	/**
	 * 取得系统条件脚本明细
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@ResponseBody
	@Action(description="查看系统条件脚本明细",detail="查看系统条件脚本明细")
	public ResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		long id=RequestUtil.getLong(request,"id");
		ConditionScript conditionScript = conditionScriptService.getById(id);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取条件脚本明细成功！",conditionScript);
	}
}
