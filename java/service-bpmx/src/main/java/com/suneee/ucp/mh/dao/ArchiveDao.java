package com.suneee.ucp.mh.dao;

import com.suneee.core.web.query.QueryFilter;
import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.mh.model.Archive;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ArchiveDao extends UcpBaseDao<Archive> {
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass() {
		return Archive.class;
	}
	
	/**
	 * 获取所有档案信息
	 * @param queryFilter
	 * @return
	 */
	public List<Archive> getArchiveList(QueryFilter queryFilter){
		return this.getBySqlKey("getArchiveList", queryFilter);
	}

}
