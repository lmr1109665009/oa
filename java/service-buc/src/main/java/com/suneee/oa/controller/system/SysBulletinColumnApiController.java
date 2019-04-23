package com.suneee.oa.controller.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.page.PageList;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysBulletinColumn;
import com.suneee.platform.service.system.SysBulletinColumnService;
import com.suneee.platform.service.system.SysBulletinService;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:公告栏目 控制器类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2018-1-25 19:03:00
 *</pre>
 */
@Controller
@RequestMapping("/api/system/sysBulletinColumn/")
public class SysBulletinColumnApiController extends BaseController {
	@Resource
	private SysBulletinColumnService sysBulletinColumnService;
	@Resource
	private SysBulletinService sysBulletinService;
	@Resource
	private SysOrgService sysOrgService;
	
	

	

	/**
	 * 取得公告栏目分页列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@ResponseBody
	@Action(description = "查看公告栏目分页列表")
	public ResultVo list(HttpServletRequest request,
						 HttpServletResponse response) throws Exception {
		Long companyId = ContextUtil.getCurrentCompanyId();
		QueryFilter filter = new QueryFilter(request, true);
		filter.addFilter("companyId", companyId);
		boolean isSuperAdmin=ContextUtil.isSuperAdmin();
		filter.addFilter("isSuperAdmin",isSuperAdmin);
		PageList<SysBulletinColumn> list = (PageList<SysBulletinColumn>) sysBulletinColumnService.getAll(filter);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取公告栏目分页数据成功！", PageUtil.getPageVo(list));
	}
	
	/**
	 * 删除公告栏目
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@ResponseBody
	@Action(description = "删除公告栏目")
	public ResultVo del(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ResultVo message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			delByIds(lAryId);
			message = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除公告栏目成功!");
		} catch (Exception ex) {
			message = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败"
					+ ex.getMessage());
		}
		return message;
	}

	/**
	 * 删除栏目，和选中栏目下的公告
	 * @param lAryId
	 */
	private void delByIds(Long[] lAryId) {
		if(BeanUtils.isEmpty(lAryId)) return;
		for(Long id:lAryId){
			try {
				sysBulletinColumnService.delById(id);
			} catch (IOException e) {
				e.printStackTrace();
			}
			sysBulletinService.delByColumnId(id);
		}
	}

	/**
	 * 编辑公告栏目
	 * 
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@ResponseBody
	@Action(description = "编辑公告栏目")
	public ResultVo edit(HttpServletRequest request) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		boolean isSuperAdmin=ContextUtil.isSuperAdmin();
		
		Long companyId=ContextUtil.getCurrentCompanyId();
		
		//判断是否能选择公司。
		boolean selectCompany=companyId.equals(0L) || isSuperAdmin;
		
		SysBulletinColumn sysBulletinColumn = sysBulletinColumnService.getById(id);
		
		Map<String,Object> map = new HashMap<>();
		map.put("sysBulletinColumn",sysBulletinColumn);
		
		if(selectCompany){
			List<Map<String,Object>> companyList = sysOrgService.getCompany();
			map.put("companyList",companyList);
		}
		
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取公告栏信息成功！",map);
		
	}

	/**
	 * 取得公告栏目明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@ResponseBody
	@Action(description = "查看公告栏目明细")
	public ResultVo get(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		SysBulletinColumn sysBulletinColumn = sysBulletinColumnService
				.getById(id);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取公告栏明细成功！",sysBulletinColumn);
	}
}