package com.suneee.platform.controller.system;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysBulletinColumn;
import com.suneee.platform.service.system.SysBulletinColumnService;
import com.suneee.platform.service.system.SysBulletinService;
import com.suneee.platform.service.system.SysOrgService;

/**
 * 对象功能:公告栏目 控制器类
 */
@Controller
@RequestMapping("/platform/system/sysBulletinColumn/")
public class SysBulletinColumnController extends BaseController {
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
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看公告栏目分页列表")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long companyId = ContextUtil.getCurrentCompanyId();
		QueryFilter filter = new QueryFilter(request, "sysBulletinColumnList");
		filter.addFilter("companyId", companyId);
		boolean isSuperAdmin=ContextUtil.isSuperAdmin();
		filter.addFilter("isSuperAdmin",isSuperAdmin);
		
		
		List<SysBulletinColumn> list = sysBulletinColumnService.getAll(filter);
		return this.getAutoView()
					.addObject("sysBulletinColumnList",list)
					.addObject("isSuperAdmin", isSuperAdmin)
					;
	}
	
	/**
	 * 删除公告栏目
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除公告栏目")
	public void del(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除公告栏目成功!");
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败"
					+ ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	/**
	 * 删除栏目，和选中栏目下的公告
	 * @param lAryId
	 */
	private void delByIds(Long[] lAryId) throws IOException {
		if(BeanUtils.isEmpty(lAryId)) return;
		for(Long id:lAryId){
			sysBulletinColumnService.delById(id);
			sysBulletinService.delByColumnId(id);
		}
	}

	/**
	 * 编辑公告栏目
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description = "编辑公告栏目")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		String returnUrl = RequestUtil.getPrePage(request);
		boolean isSuperAdmin=ContextUtil.isSuperAdmin();
		
		Long companyId=ContextUtil.getCurrentCompanyId();
		
		//判断是否能选择公司。
		boolean selectCompany=companyId.equals(0L) || isSuperAdmin;
		
		SysBulletinColumn sysBulletinColumn = sysBulletinColumnService.getById(id);
		
		
		ModelAndView mv=getAutoView().addObject("sysBulletinColumn", sysBulletinColumn)
				.addObject("selectCompany", selectCompany)
				.addObject("returnUrl", returnUrl);
		
		if(selectCompany){
			List<Map<String,Object>> companyList = sysOrgService.getCompany();
			mv.addObject("companyList", companyList);
		}
		
		return mv;
		
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
	@Action(description = "查看公告栏目明细")
	public ModelAndView get(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		SysBulletinColumn sysBulletinColumn = sysBulletinColumnService
				.getById(id);
		return getAutoView().addObject("sysBulletinColumn", sysBulletinColumn);
	}
}