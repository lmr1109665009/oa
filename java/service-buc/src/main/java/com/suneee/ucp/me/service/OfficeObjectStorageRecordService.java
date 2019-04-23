package com.suneee.ucp.me.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.me.dao.OfficeObjectStorageRecordDao;
import com.suneee.ucp.me.model.OfficeObjectStorageRecord;

@Service
public class OfficeObjectStorageRecordService extends UcpBaseService<OfficeObjectStorageRecord>{

	@Resource
	private OfficeObjectStorageRecordDao dao;
	
	@Override
	protected IEntityDao<OfficeObjectStorageRecord, Long> getEntityDao() {
		return dao;
	}
	
	public List<OfficeObjectStorageRecord> getRecordList(OfficeObjectStorageRecord record){
		return dao.getRecordList(record);
	}
}
