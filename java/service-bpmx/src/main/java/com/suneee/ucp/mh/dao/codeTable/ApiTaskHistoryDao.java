package com.suneee.ucp.mh.dao.codeTable;

import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.mh.model.codeTable.ApiTaskHistory;
import org.springframework.stereotype.Repository;

@Repository
public class ApiTaskHistoryDao extends UcpBaseDao<ApiTaskHistory>{

	@Override
	public Class<?> getEntityClass() {
		// TODO Auto-generated method stub
		return ApiTaskHistory.class;
	}

	@Override
	public ApiTaskHistory getById(Long primaryKey) {
		return super.getById(primaryKey);
	}

	public ApiTaskHistory getByRunId(Long runId){
		return this.getUnique("getByRunId",runId);
	}

}
