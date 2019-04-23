package com.suneee.platform.service.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageList;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.system.SysBulletinDao;
import com.suneee.platform.model.system.SysBulletin;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageList;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.system.SysBulletinDao;
import com.suneee.platform.model.system.SysBulletin;

@Service
public class SysBulletinService extends BaseService<SysBulletin> {
	@Resource
	private SysBulletinDao dao;
	@Resource
	private SysOrgTacticService orgTacticService;

	public SysBulletinService() {
	}

	@Override
	protected IEntityDao<SysBulletin, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 保存 公告表 信息
	 * 
	 * @param sysBulletin
	 */
	public void save(SysBulletin sysBulletin) {
		Long id = sysBulletin.getId();
		if (id == null || id == 0) {
			id = UniqueIdUtil.genId();
			sysBulletin.setId(id);
			this.add(sysBulletin);
		} else {
			this.update(sysBulletin);
		}
	}
	
	public void saveByFlow(ProcessCmd cmd){
		Map map= cmd.getFormDataMap();
		String json =  JSONObject.toJSON(map).toString();
		SysBulletin bulletin = JSONObject.parseObject(json,SysBulletin.class);
		if(StringUtil.isEmpty( bulletin.getSubject())) return;
		
		bulletin.setStatus(0);
		bulletin.setCreator(ContextUtil.getCurrentUser().getFullname());
		bulletin.setCreatorid(ContextUtil.getCurrentUserId());
		
		cmd.addVariable("type", bulletin.getColumnid()+"");
		if(StringUtil.isNotEmpty(cmd.getBusinessKey())){
			bulletin.setId(Long.parseLong(cmd.getBusinessKey()) );
			this.update(bulletin);
			return;
		}
		
		Long id=UniqueIdUtil.genId();
		bulletin.setId(id);
		//写回主键，这个数据将保存到流程中。
		cmd.setBusinessKey(id.toString()); 
		this.add(bulletin);
	}
	
	public void updateStatus(Long id,int status){
		dao.updateStatus( id, status);
	}

	
	/**
	 * 根据栏目id取得list
	 * 
	 * @param columnId
	 */
	public void delByColumnId(Long columnId) {
		dao.delByColumnId(columnId);
	}
	
	/**
	 * 根据别名取list
	 * 
	 * @param tenantId
	 * @param alias
	 * @param pb
	 * @return
	 */
	public List<SysBulletin> getAllByAlias(String alias) {
		PageBean pb = new PageBean();
		pb.setCurrentPage(1);
		pb.setPagesize(10);
		pb.setShowTotal(false);
		List<SysBulletin> list = dao.getAllByAlias(alias, pb);
		return list;
	}
	
	/**
	 * 
	 * @param queryFilter
	 * @return
	 */
	public List<SysBulletin> getAllByAlias(QueryFilter queryFilter) {
		return dao.getAllByAlias(queryFilter);
	}
	
	/**
	 * 取当前用户的可以看到的公告
	 */
	public List<SysBulletin> getTopBulletin(int pageSize){
		List<SysBulletin> list = new ArrayList<SysBulletin>();
		Map<String, Object> map =new HashMap<String, Object>();
		PageBean pb = new PageBean();
		pb.setCurrentPage(1);
		pb.setPagesize(pageSize);
		if(!ContextUtil.isSuperAdmin()){
			Long companyId = ContextUtil.getCurrentCompanyId();
			map.put("companyId", companyId);
		}
		list = dao.getBySqlKey("getTopBulletin", map, pb);
		return list;
	}
	
	
	public PageList<SysBulletin> getByColumnId(QueryFilter queryFilter){
		PageList<SysBulletin> list = dao.getByColumnId(queryFilter);
		return list;
	}

	public List<SysBulletin> getTopNews(int top) {
		return dao.getTopNews(top);
	}

	/**
	 * 根据查询条件获取所有公告
	 * @param filter
	 * @return
	 */
	public List<SysBulletin> getAllBulletin(QueryFilter filter){
		return dao.getAllBulletin(filter);
	}

	/**
	 * 获取当前用户创建的公告
	 * @param filter
	 * @return
	 */
	public List<SysBulletin> listCreateByMe(QueryFilter filter){
		return dao.listCreateByMe(filter);
	}

	/**
	 * 添加数据到公告组织表
	 * @param bulletinId
	 * @param orgCodes
	 */
	public void addToBulletinOrg(Long bulletinId,String orgIds){
		String[] orgIdList = orgIds.split(",");
		for(String orgId:orgIdList){
			dao.addToBulletinOrg(bulletinId,orgId);
		}
	}

	/**
	 * 根据公告id删除中间表的数据
	 * @param bulletinId
	 */
	public void dellFromBulletinOrgByBulletin(Long bulletinId){
		dao.dellFromBulletinOrgByBulletin(bulletinId);
	}

	/**
	 * 根据id获取
	 * @param id
	 * @return
	 */
	public SysBulletin getByBulletinId(Long id){
		return dao.getByBulletinId(id);
	}

}
