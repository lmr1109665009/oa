package com.suneee.weixin.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.web.ResultMessage;
import com.suneee.platform.annotion.Action;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.page.PageList;
import com.suneee.core.util.TimeUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.page.PageList;
import com.suneee.core.util.TimeUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.dao.system.SysReadRecodeDao;
import com.suneee.platform.model.system.SysBulletin;
import com.suneee.platform.model.system.SysBulletinColumn;
import com.suneee.platform.model.system.SysReadRecode;
import com.suneee.platform.service.system.SysBulletinColumnService;
import com.suneee.platform.service.system.SysBulletinService;
import com.suneee.weixin.model.ListModel;
import com.suneee.weixin.model.RowModel;

@Controller
@RequestMapping("/weixin/bulletin/")
public class BulletinController {
	
	@Resource
	private FreemarkEngine freemarkEngine;

	@Resource
	private SysBulletinService sysBulletinService;
	@Resource
	private SysBulletinColumnService bulletinColumnService;
	@Resource
	private SysReadRecodeDao sysReadRecodeDao;
	

	@RequestMapping("detail")
	@ResponseBody
	public SysBulletin bulletinDetail(HttpServletRequest request, HttpServletResponse response) {
		Long bulletinId= RequestUtil.getLong(request, "bulletinId", 0L);
		SysBulletin bulletin=sysBulletinService.getById(bulletinId);
		Long userId= ContextUtil.getCurrentUserId();
		
		if(!sysReadRecodeDao.hasRead(bulletinId, userId)){
			sysReadRecodeDao.add(new SysReadRecode(bulletinId,userId,"Bulletin",bulletin.getColumnid()));
		}
		
		return bulletin;
	}
	
	@RequestMapping("getListByColumn")
	@ResponseBody
	public ListModel getListByColumn(HttpServletRequest request, HttpServletResponse response) {
		QueryFilter filter=new QueryFilter(request, true);
		Long columnId=RequestUtil.getLong(request, "columnid", 0L);
		Long userId=ContextUtil.getCurrentUserId();
		
		filter.addFilter("userId", userId);
		
		SysBulletinColumn bulletinColumn=bulletinColumnService.getById(columnId);
		
		PageList<SysBulletin> pageList=sysBulletinService.getByColumnId(filter);
		
		ListModel model=new ListModel();
		model.setTitle(bulletinColumn.getName());
		
		List list=new ArrayList();
		
		for(SysBulletin bulletin:pageList){
			RowModel m=new RowModel(bulletin.getSubject(), bulletin.getId().toString(), TimeUtil.getDateTimeString(bulletin.getCreatetime())  );
			list.add(m);
		}
		
		model.setRowList(list);
		
		model.setTotal(pageList.getTotalPage());
		
		return model;
	}
	
	@RequestMapping("columns")
	@ResponseBody
	public List<SysBulletinColumn> getColumns(){
		List<SysBulletinColumn> bulletinColumns= bulletinColumnService.getColumn();
		return bulletinColumns;
	}

	/**
	 * 获得用户的通知分类和通知
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getColumnAndBulletin")
	@ResponseBody
	@Action(description = "查看公告表分页列表")
	public ResultVo getColumnAndBulletin(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try{
		Long companyId = ContextUtil.getCurrentCompanyId();
		Boolean isSuperAdmin = ContextUtil.isSuperAdmin();
		
		QueryFilter filter = new QueryFilter(request, "sysBulletinItem");
		filter.addFilter("companyId", companyId);
		filter.addFilter("isSuperAdmin", isSuperAdmin);
		List<SysBulletin> list = sysBulletinService.getAll(filter);
		// 有权限的栏目
		List<SysBulletinColumn> columnList = bulletinColumnService
				.getColumn(companyId, isSuperAdmin);
		
		List<Map> bclist=new ArrayList<Map>();
		
		//遍历所有通知，将通知分到不同的类别中
		for (SysBulletinColumn column : columnList) {
			//建立容器保存不同类型下的通知
			Map<String,Object> blistmap=new HashMap<String,Object>();
			List<SysBulletin> bl=new ArrayList<SysBulletin>();
			for (SysBulletin bulletin : list) {
				if(bulletin.getColumnid().equals(column.getId())){
					bl.add(bulletin);
				}
			}
			blistmap.put("blistbyc", bl);
			blistmap.put("column", column);
			bclist.add(blistmap);
		}
		
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取数据成功！", bclist);
		}catch(Exception e){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取数据失败！");
		}
	}


}
