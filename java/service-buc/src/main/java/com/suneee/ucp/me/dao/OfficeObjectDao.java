package com.suneee.ucp.me.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.me.model.OfficeObject;
import com.suneee.ucp.me.model.OfficeObjectType;

@Repository
public class OfficeObjectDao extends UcpBaseDao<OfficeObject>{

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass() {
		return OfficeObject.class;
	}

	/**
	 * 获取办公用品列表
	 * @return
	 */
	public List<OfficeObject> getOfficeObjectList(){
		return this.getAll();
	}
	
	public List<OfficeObject> query(OfficeObject officeobject){
		return (List<OfficeObject>) this.getBySqlKey("query", officeobject);
	}
	
	public int deleteAll(Long[] ids) {
		return this.delBySqlKey("deleteAll", ids);
	}
	
	public List<OfficeObject> getTypeList(){
		return this.getBySqlKey("typeList");
	}
	
	public List<OfficeObject> getNameList(String type){
		return this.getBySqlKey("nameList", type);
	}
	
	public List<OfficeObject> getSpecificationList(OfficeObject officeObject){
		return this.getBySqlKey("specificationList", officeObject);
	}
	
	public List<OfficeObject> getAreaList(){
		return this.getBySqlKey("areaList");
	}
}
