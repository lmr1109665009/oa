/**
 * 
 */
package com.suneee.ucp.base.dao.system;

import com.suneee.platform.model.system.SysOrgType;
import com.suneee.ucp.base.dao.UcpBaseDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 *
 */
@Repository
public class SysOrgTypeExtDao extends UcpBaseDao<SysOrgType>{

	/* (non-Javadoc)
	 * @see com.suneee.core.db.GenericDao#getEntityClass()
	 */
	@Override
	public Class<SysOrgType> getEntityClass() {
		// TODO Auto-generated method stub
		return SysOrgType.class;
	}

	/**
	 * 根据维度ID和组织类型名称获取组织类型信息
	 * @param name
	 * @param demId
	 * @return
	 */
	public SysOrgType getByNameAndDemId(String name, Long demId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		params.put("demId", demId);
		return this.getUnique("getAll", params);
	}
}
