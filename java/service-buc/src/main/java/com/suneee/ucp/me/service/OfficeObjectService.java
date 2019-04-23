package com.suneee.ucp.me.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.me.dao.OfficeObjectDao;
import com.suneee.ucp.me.model.OfficeObject;
import com.suneee.ucp.me.model.OfficeObjectType;

@Service
public class OfficeObjectService extends UcpBaseService<OfficeObject>{

	@Resource
	private OfficeObjectDao officeObjectDao;
	
	@Override
	protected IEntityDao<OfficeObject, Long> getEntityDao() {
		return officeObjectDao;
	}
	
	public int deleteAll(Long[] ids){
		return officeObjectDao.deleteAll(ids);
	}
	
	public List<OfficeObject> query(OfficeObject object){
		return officeObjectDao.query(object);
	}
	
	public List<OfficeObject> getTypeList(){
		return officeObjectDao.getTypeList();
	}
	
	public List<OfficeObject> getNameList(String type){
		return officeObjectDao.getNameList(type);
	}
	
	public List<OfficeObject> getSpecificationList(OfficeObject officeObjectType){
		return officeObjectDao.getSpecificationList(officeObjectType);
	}
	
	public List<OfficeObject> getAreaList(){
		return officeObjectDao.getAreaList();
	}
	
}
