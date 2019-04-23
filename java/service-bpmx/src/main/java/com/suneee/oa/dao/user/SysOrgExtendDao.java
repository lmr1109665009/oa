package com.suneee.oa.dao.user;

import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.ucp.base.dao.UcpBaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 组织信息SysOrg扩展DAO类
 * @author xiongxianyun
 *
 */
@Repository
public class SysOrgExtendDao extends UcpBaseDao<SysOrg> {
	@Override
	  public Class<SysOrg> getEntityClass() {
	    return SysOrg.class;
	}

	/**
	 * 根据维度ID获取系统所有组织信息
	 * @param demId
	 * @return
	 */
	public List<SysOrg> getByDemId(Long demId) {
	    return getBySqlKey("getByDemId", demId);
	}
	
	/**
	 * 根据组织ID查询组织详情
	 * @param orgId
	 * @return
	 */
	public SysOrg getByOrgId(Long orgId){
		return this.getUnique("getByOrgId", orgId);
	}
	
	/** 
	 * 根据查询条件获取组织详细信息列表
	 * @param filter
	 * @return
	 */
	public List<SysOrg> getByCondition(QueryFilter filter){
		return this.getBySqlKey("getByCondition", filter);
	}
	
	/** 
	 * 根据查询条件获取组织基本信息
	 * @param queryFilter
	 * @return
	 */
	public List<SysOrg> getSimpleByCondition(QueryFilter queryFilter){
		return this.getBySqlKey("getSimpleByCondition", queryFilter);
	}
}