package com.suneee.ucp.me.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.me.model.OfficeObjectStorageRecord;

@Repository
public class OfficeObjectStorageRecordDao extends UcpBaseDao<OfficeObjectStorageRecord>{

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass() {
		return OfficeObjectStorageRecord.class;
	}

	public List<OfficeObjectStorageRecord> getRecordList(OfficeObjectStorageRecord record){
		return this.getBySqlKey("getWithStorageId", record);
	}
}
