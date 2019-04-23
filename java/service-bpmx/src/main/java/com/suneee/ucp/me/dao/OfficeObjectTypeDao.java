/**  
 * @Title: OfficeObjectTypeDao.java
 * @Package com.suneee.ucp.me.dao
 * @Description: TODO(用一句话描述该文件做什么)
 * @author yiwei
 * @date 2017年4月18日
 * @version V1.0  
 */
package com.suneee.ucp.me.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.me.model.OfficeObjectType;

/**
 * @ClassName: OfficeObjectTypeDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author yiwei
 * @date 2017年4月18日
 *
 */
@Repository
public class OfficeObjectTypeDao extends UcpBaseDao<OfficeObjectType>{

	/* (非 Javadoc)
	 * 
	 * 
	 * @return
	 * @see com.suneee.core.db.GenericDao#getEntityClass()
	 */
	@Override
	public Class getEntityClass() {
		return OfficeObjectType.class;
	}
	
	public List<OfficeObjectType> queryType(OfficeObjectType type){
		return (List<OfficeObjectType>) this.getBySqlKey("queryType", type);
	}

	public int deleteAll(Long[] ids) {
		return this.delBySqlKey("deleteAll", ids);
	}
	
	public List<OfficeObjectType> getTypeList(){
		return this.getBySqlKey("typeList");
	}
	
	public List<OfficeObjectType> getNameList(String type){
		return this.getBySqlKey("nameList", type);
	}
	
	public List<OfficeObjectType> getSpecificationList(OfficeObjectType officeObjectType){
		return this.getBySqlKey("specificationList", officeObjectType);
	}
}
