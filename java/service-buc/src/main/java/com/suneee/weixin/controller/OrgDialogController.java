package com.suneee.weixin.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageList;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageList;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.system.Demension;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.DemensionService;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.SysUserService;

@Controller
@RequestMapping("/weixin/orgDialog/")
public class OrgDialogController {
	
//	@Resource
//	private ISysUserService userService;
	@Resource
	private SysUserService userService;
	@Resource
	private DemensionService dimensionService;
	@Resource
	private SysOrgService sysOrgService;

	
	@RequestMapping("userList")
	@ResponseBody
	public JSONObject userList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		QueryFilter filter =new QueryFilter(request);
		Long orgId = RequestUtil.getLong(request, "orgId",0L);
		if(orgId>0){
			SysOrg org = sysOrgService.getById(orgId);
			if(org!= null) filter.addFilter("orgPath",org.getPath()+"%");
		}
		List<SysUser> userList=  userService.getUsersByQuery(filter);
		return this.getPageList(userList,request);
	}
	
	
	private JSONObject getPageList(List list, HttpServletRequest request) {
		if(list instanceof PageList){
			PageList pageList = (PageList) list;
			JSONObject pageJson = new JSONObject();
			PageBean page = pageList.getPageBean();
			pageJson.put("page", page.getCurrentPage());
			pageJson.put("count", page.getTotalCount());
			pageJson.put("pageCount", page.getTotalPage());
			pageJson.put("pageSize", page.getPageSize());
			// 查询的字段
			String _queryName = RequestUtil.getString(request, "_queryName");
			if(StringUtil.isNotEmpty(_queryName)){
				pageJson.put("_queryName",_queryName);
				pageJson.put("_queryData", RequestUtil.getString(request,_queryName));
			}
			
			JSONObject data = new JSONObject();
			data.put("list", list);
			data.put("pageParam", pageJson);
			return data;
		}
		else throw new RuntimeException("必须是PageList!");
	}
	
	@RequestMapping("getDimList")
	@ResponseBody
	public List<Demension> getDimList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<Demension> dimensionList = dimensionService.getAll();
		return dimensionList;
	}
	
	@RequestMapping("getOrgListByDim")
	@ResponseBody
	public List<SysOrg> getOrgListByDim(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long dimId = RequestUtil.getLong(request, "dimId", 1L);
		List<SysOrg> org = sysOrgService.getOrgsByDemIdOrAll(dimId);
		return org;
	}
	
	@RequestMapping("getOrgByParentId")
	@ResponseBody
	public List<SysOrg> getOrgByParentId(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long dimId = RequestUtil.getLong(request, "dimId", 1L);
		Long parentid =RequestUtil.getLong(request, "parentId", 1L);
		List<SysOrg> org = null;///sysOrgService.get
		return org; 
	}
	
	@RequestMapping("orgList")
	@ResponseBody
	public JSONObject orgList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		boolean needPage = RequestUtil.getBoolean(request, "needPage", true);		
		List<SysOrg> orgList =  sysOrgService.getAll(new QueryFilter(request, needPage));
	
		if(needPage){
			return this.getPageList(orgList,request);
		}else{
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("list", orgList);
			return jsonObject;
		}
		
	}
	@RequestMapping("orgListToFile")
	@ResponseBody
	public JSONObject orgListToFile(HttpServletRequest request, HttpServletResponse response) throws Exception{
		boolean needPage = RequestUtil.getBoolean(request, "needPage", true);
		Long userId = ContextUtil.getCurrentUserId();
		SysOrg org = sysOrgService.getByUserId(userId);
		List<SysOrg> orgList =new ArrayList<SysOrg>();
		QueryFilter filter =new QueryFilter(request,needPage);
		if(org.getOrgId()==null){
			 orgList =  sysOrgService.getAll(filter);	
		}else{
			List<SysOrg> list = sysOrgService.getOrgByOrgSupId(org.getOrgId());
			if(list.size()==0){				
				orgList.add(org);
			}else{
				orgList.add(org);
				for (SysOrg Orgs : list) {
					orgList.add(Orgs);
					List<SysOrg> list2 = sysOrgService.getOrgByOrgSupId(Orgs.getOrgId());
					if(list2.size()==0){
						continue;
					}else{
						for (SysOrg sysOrg2 : list2) {
							orgList.add(sysOrg2);
							List<SysOrg> list3 = sysOrgService.getOrgByOrgSupId(sysOrg2.getOrgId());
							if(list3.size()==0){
								continue;
							}else{
								for (SysOrg sysOrg3 : list3) {
									orgList.add(sysOrg3);
								}
							}
						}
					}
				}
			}
		}
		if(needPage){
			return this.getPageList(orgList,request);
		}else{
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("list", orgList);
			return jsonObject;
		}
		
	}
}
