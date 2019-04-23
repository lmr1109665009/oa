package com.suneee.oa.controller.system;

import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysHistoryData;
import com.suneee.platform.service.system.SysHistoryDataService;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *<pre>
 * 对象功能:历史数据 控制器类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2017-12-27 19:51:00
 *</pre>
 */
@Controller
@RequestMapping("/api/system/sysHistoryData/")
public class SysHistoryDataApiController extends BaseController
{
	@Resource
	private SysHistoryDataService sysHistoryDataService;

	/**
	 * 取得历史数据分页列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@ResponseBody
	@Action(description="查看历史数据分页列表")
	public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception
	{	
		Long objId=RequestUtil.getLong(request, "objId");
		QueryFilter filter = new QueryFilter(request,true);
		filter.addFilter("objId", objId);
		List<SysHistoryData> list =sysHistoryDataService.getAll(filter);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取历史数据分页列表成功！",list);
	}

	/**
	 * 恢复（获得返回内容）
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getContent")
	@ResponseBody
	@Action(description="获得返回内容")
	public ResultVo getContent(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		SysHistoryData sysHistoryData = sysHistoryDataService.getById(id);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获得返回内容成功！",sysHistoryData.getContent());
	}

	/**
	 * 取得历史数据明细
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@ResponseBody
	@Action(description="查看历史数据明细")
	public ResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		SysHistoryData sysHistoryData = sysHistoryDataService.getById(id);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取历史数据明细成功！",sysHistoryData);
	}

	/**
	 * 删除历史数据
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@ResponseBody
	@Action(description="删除历史数据")
	public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
		sysHistoryDataService.delByIds(lAryId);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"删除历史数据成功！");
	}
}

