package com.suneee.oa.controller.system;

import com.suneee.core.page.PageList;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.system.Job;
import com.suneee.platform.model.system.Position;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.PositionService;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.impl.OrgServiceImpl;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 *<pre>
 * 对象功能:系统岗位表，实际是部门和职务的对应关系表 控制器类
 * 开发公司:深圳象翌
 * 开发人员:KaiZe
 * 创建时间:2018-04-23 10:31:00
 *</pre>
 */
@Controller
@RequestMapping("/api/system/position/")
public class PositionApiController extends BaseController
{
	@Resource
	private PositionService positionService;
	@Resource
	private SysOrgService sysOrgService;
	@Resource
	private OrgServiceImpl orgServiceImpl;


	
	/**
	 * 组织对话框的展示
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("selector")
	@ResponseBody
	public ResultVo selector(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		QueryFilter filter = new QueryFilter(request, true);
		String type = RequestUtil.getString(request, "type");
		String typeVal = RequestUtil.getString(request, "typeVal");
		Long orgId = RequestUtil.getLong(request, "orgId");
		SysOrg sysOrgTemp = sysOrgService.getById(orgId);
		if (BeanUtils.isEmpty(sysOrgTemp)) {
			SysOrg sysOrg =  orgServiceImpl.getSysOrgByScope(type, typeVal);
			Map<String, Object> filters = filter.getFilters();
			filters.remove("orgId");
			filter.setFilters(filters);
			filter.addFilter("orgPath", StringUtil.isNotEmpty(sysOrg.getPath()) ? (sysOrg.getPath() + "%") : ("%."+sysOrg.getOrgId()+".%"));
		}
		filter.addFilter("nodekey", Job.NODE_KEY);
		filter.addFilter("orgCode", CookieUitl.getCurrentEnterpriseCode());
		PageList<Position> positionList= (PageList<Position>) positionService.getBySupOrgId(filter);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取岗位列表数据成功！", PageUtil.getPageVo(positionList));
	}
}
