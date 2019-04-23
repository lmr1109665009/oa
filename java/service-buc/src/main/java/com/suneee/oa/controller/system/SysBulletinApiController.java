package com.suneee.oa.controller.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.page.PageList;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysBulletin;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysReadRecode;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.*;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 *<pre>
 * 对象功能:公告表 控制器类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2018-1-25 19:03:00
 *</pre>
 */
@Controller
@RequestMapping("/api/system/sysBulletin/")
public class SysBulletinApiController extends UcpBaseController {
	@Resource
	private SysBulletinService sysBulletinService;
	@Resource
	private SysBulletinColumnService sysBulletinColumnService;
	@Resource
	private SysBulletinTemplateService sysBulletinTemplateService;
	@Resource
	private SysReadRecodeService sysReadRecodeService;
	@Resource
	private SysOrgService sysOrgService;
	@Resource
	private UserPositionService userPositionService;

	@RequestMapping("save")
	@ResponseBody
	@Action(description = "添加或更新公告表")
	public ResultVo save(HttpServletRequest request, HttpServletResponse response,
					 SysBulletin sysBulletin) throws Exception {
		String resultMsg = null;
		try {
			if(sysBulletin.getStatus() == 1){
				sysBulletin.setPublishTime(new Date());
			}
			SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
			//获取当前用户的企业编码
			String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
			sysBulletin.setEnterpriseCode(enterpriseCode);
			if (sysBulletin.getId() == null) {
				sysBulletin.setCreatorid(sysUser.getUserId());
				sysBulletin.setCreator(sysUser.getFullname());
				sysBulletin.setId(UniqueIdUtil.genId());
				sysBulletinService.add(sysBulletin);
				if(StringUtil.isNotEmpty(sysBulletin.getPublishRangeID_org())){
					//添加数据到公告组织中间表
					sysBulletinService.addToBulletinOrg(sysBulletin.getId(),sysBulletin.getPublishRangeID_org());
				}
				resultMsg = getText("添加成功", "公告表");
			} else {
				sysBulletinService.update(sysBulletin);
				if(StringUtil.isNotEmpty(sysBulletin.getPublishRangeID_org())){
					//先删除之前公告对应数据，再添加更新后的数据
					sysBulletinService.dellFromBulletinOrgByBulletin(sysBulletin.getId());
					sysBulletinService.addToBulletinOrg(sysBulletin.getId(),sysBulletin.getPublishRangeID_org());
				}
				resultMsg = getText("更新成功", "公告表");
			}

			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,resultMsg);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,resultMsg + "," + e.getMessage());
		}
	}

	/**
	 * 列表数据(有关我的公告)
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("listAboutMe")
	@ResponseBody
	@Action(description = "查看公告表分页列表")
	public ResultVo listAboutMe(HttpServletRequest request,
						 HttpServletResponse response) throws Exception {
		Boolean isSuperAdmin = ContextUtil.isSuperAdmin();
		Long userId = ContextUtil.getCurrentUserId();
		//获取当前用户的企业编码
		String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
		QueryFilter filter = new QueryFilter(request, true);
		filter.addFilter("enterpriseCode", enterpriseCode);
		if(!isSuperAdmin){
			List<SysOrg> orgs = sysOrgService.getOrgsByUserId(userId);
			String orgPaths = "";
			for(SysOrg org:orgs){
				if(enterpriseCode.equals(org.getOrgCode())){
					orgPaths += org.getPath();
				}
			}
			filter.addFilter("orgPaths",orgPaths);
			filter.addFilter("userId",userId);
		}else{
			filter.addFilter("isSuperAdmin",true);
		}
		PageList<SysBulletin> list = (PageList<SysBulletin>) sysBulletinService.getAllBulletin(filter);

		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取公告表分页列表数据成功！",PageUtil.getPageVo(list));
	}

	/**
	 * 列表数据(我创建的的公告)
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("listCreateByMe")
	@ResponseBody
	@Action(description = "查看公告表分页列表")
	public ResultVo listCreateByMe(HttpServletRequest request,
						 HttpServletResponse response) throws Exception {
		Boolean isSuperAdmin = ContextUtil.isSuperAdmin();
		Long userId = ContextUtil.getCurrentUserId();
		//获取当前用户的企业编码
		String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
		QueryFilter filter = new QueryFilter(request, true);
		if(!isSuperAdmin){
			filter.addFilter("creatorId", userId);
			filter.addFilter("enterpriseCode", enterpriseCode);
		}
		PageList<SysBulletin> list = (PageList<SysBulletin>) sysBulletinService.listCreateByMe(filter);

		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取我创建的公告表分页列表数据成功！",PageUtil.getPageVo(list));
	}

	/**
	 * 删除公告表
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@ResponseBody
	@Action(description = "删除公告表")
	public ResultVo del(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ResultVo message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			sysBulletinService.delByIds(lAryId);
			//删除中间表对应数据
			for(Long id:lAryId){
				sysBulletinService.dellFromBulletinOrgByBulletin(id);
			}
			message = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除公告表成功!");
		} catch (Exception ex) {
			message = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败"
					+ ex.getMessage());
		}
		return message;
	}

	/**
	 * 编辑公告
	 * 
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@ResponseBody
	@Action(description = "编辑公告")
	public ResultVo edit(HttpServletRequest request) throws Exception {
	
		Long id = RequestUtil.getLong(request, "id");
		SysBulletin sysBulletin = sysBulletinService.getByBulletinId(id);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取公告信息成功！",sysBulletin);
	}

	/**
	 * 取得公告表明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@ResponseBody
	@Action(description = "查看公告表明细")
	public ResultVo get(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		SysBulletin sysBulletin = sysBulletinService.getByBulletinId(id);
		
		if(sysBulletin != null){
			long userId = ContextUtil.getCurrentUserId();
			if(!sysReadRecodeService.hasRead(id,userId)){
				sysReadRecodeService.add(new SysReadRecode(id,userId,"Bulletin",sysBulletin.getColumnid()));
			}
		}
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取公告明细成功！",sysBulletin);
	}
}