package com.suneee.ucp.mh.service;
import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.suneee.core.web.query.QueryFilter;

import com.suneee.core.db.IEntityDao;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.mh.dao.DemandDao;
import com.suneee.ucp.mh.model.Archive;
import com.suneee.ucp.mh.model.Demand;

@Service
public class DemandService extends UcpBaseService<Demand>{

	@Resource
	private DemandDao demandDao;
	@Override
	protected IEntityDao<Demand, Long> getEntityDao() {
		return demandDao;
	}

	/**
	 * 获取档案信息列表
	 * @param queryFilter
	 * @return
	 */
	public List<Demand> getArchiveList(QueryFilter queryFilter){
		return demandDao.getArchiveList(queryFilter);
	}
}
