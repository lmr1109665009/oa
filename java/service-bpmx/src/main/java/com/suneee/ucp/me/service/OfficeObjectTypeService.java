/**  
 * @Title: OfficeObjectTypeService.java
 * @Package com.suneee.ucp.me.service
 * @Description: TODO(用一句话描述该文件做什么)
 * @author yiwei
 * @date 2017年4月18日
 * @version V1.0  
 */
package com.suneee.ucp.me.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.me.dao.OfficeObjectTypeDao;
import com.suneee.ucp.me.model.OfficeObjectType;

/**
 * @ClassName: OfficeObjectTypeService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author yiwei
 * @date 2017年4月18日
 *
 */
@Service
public class OfficeObjectTypeService extends UcpBaseService<OfficeObjectType>{
	
	@Resource
	private OfficeObjectTypeDao dao;
	/* (非 Javadoc)
	 * 
	 * 
	 * @return
	 * @see com.suneee.core.service.GenericService#getEntityDao()
	 */
	@Override
	protected IEntityDao<OfficeObjectType, Long> getEntityDao() {
		return dao;
	}

	public List<OfficeObjectType> queryType(OfficeObjectType type){
		return dao.queryType(type);
	}
	
	public int deleteAll(Long[] ids){
		return dao.deleteAll(ids);
	}
	
	public List<OfficeObjectType> getTypeList(){
		return dao.getTypeList();
	}
	
	public List<OfficeObjectType> getNameList(String type){
		return dao.getNameList(type);
	}
	
	public List<OfficeObjectType> getSpecificationList(OfficeObjectType officeObjectType){
		return dao.getSpecificationList(officeObjectType);
	}
}
