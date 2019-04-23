package com.suneee.ucp.me.dao;

import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.me.model.OfficeObjectApply;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OfficeObjectApplyDao extends UcpBaseDao<OfficeObjectApply>{

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass() {
		return OfficeObjectApply.class;
	}
	
	/**
	 * 根据userId获取列表
	 * @param queryFilter
	 * @return
	 */
//	@SuppressWarnings("unchecked")
//	public List<OfficeObjectApply> getApplyListWithApplicant(QueryFilter queryFilter){
//		
//		return getListBySqlKey("", queryFilter);
//	}
	
	/**
	 * 修改申请状态
	 * @param params
	 * @return
	 */
//	public int updateState(Map params){
//		return update("update", params);
//	}
	
//	public List<OfficeObjectApply> getApplyList(QueryFilter queryFilter){
//		return getListBySqlKey("getApplyList", queryFilter);
//	}
//	
//	public List<OfficeObjectApply> getApprovalList(QueryFilter queryFilter){
//		return getListBySqlKey("getApprovalList", queryFilter);
//	}
	
	public List<OfficeObjectApply> getTypeList(){
		return this.getBySqlKey("typeList");
	}
	
	public List<OfficeObjectApply> getCreatorList(){
		return this.getBySqlKey("creatorList");
	}
	
}
