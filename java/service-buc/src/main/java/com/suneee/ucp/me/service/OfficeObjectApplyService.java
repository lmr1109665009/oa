package com.suneee.ucp.me.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.me.dao.OfficeObjectApplyDao;
import com.suneee.ucp.me.model.OfficeObject;
import com.suneee.ucp.me.model.OfficeObjectApply;

@Service
public class OfficeObjectApplyService extends UcpBaseService<OfficeObjectApply>{
	@Resource
	private OfficeObjectApplyDao dao;

	@Override
	protected IEntityDao<OfficeObjectApply, Long> getEntityDao() {
		return dao;
	}
	
	/**
	 * 审核操作
	 * @param params
	 * @return
	 */
//	public int updateState(Map params){
//		return dao.updateState(params);
//	}
//	
//	public List<OfficeObjectApply> getApplyList(QueryFilter queryFilter){
//		return dao.getApplyList(queryFilter);
//	}
//	
//	public List<OfficeObjectApply> getApprovalList(QueryFilter queryFilter){
//		return dao.getApprovalList(queryFilter);
//	}
	
	public List<OfficeObjectApply> getTypeList(){
		return dao.getTypeList();
	}
	
	public List<OfficeObjectApply> getCreatorList(){
		return dao.getCreatorList();
	}
	
}
