package com.suneee.ucp.mh.service;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.mh.dao.SuneeeWorkDao;

public class SuneeeWorkService extends UcpBaseService<ProcessRun> {

	@Resource
	private SuneeeWorkDao dao;
	
	@Override
	protected IEntityDao<ProcessRun, Long> getEntityDao() {
		return dao;
	}

}
