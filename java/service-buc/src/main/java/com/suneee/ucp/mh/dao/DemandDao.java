package com.suneee.ucp.mh.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.suneee.core.web.query.QueryFilter;


import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.mh.model.Demand;

@Repository
public class DemandDao extends UcpBaseDao<Demand> {
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass() {
		return Demand.class;
	}
	
	/**
	 * 获取所有档案信息
	 * @param queryFilter
	 * @return
	 */
	public List<Demand> getArchiveList(QueryFilter queryFilter){
		return this.getBySqlKey("getDemandList", queryFilter);
	}

}
