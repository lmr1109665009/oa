package com.suneee.ucp.mh.dao;

import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.ucp.base.dao.UcpBaseDao;

public class SuneeeWorkDao extends UcpBaseDao<ProcessRun> {

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return ProcessRun.class;
	}

}
