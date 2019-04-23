/**
 * 
 */
package com.suneee.ucp.base.dao.system;

import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.ucp.base.dao.UcpBaseDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 *
 */
@Repository
public class SysOrgExtDao extends UcpBaseDao<SysOrg>{

	@Override
	public Class<SysOrg> getEntityClass() {
		return SysOrg.class;
	}

	/**
	 * 根据组织名称路径获取组织信息
	 * @param orgPathName
	 * @param demId
	 * @param orgId
	 * @return
	 */
	public SysOrg getByOrgPathName(String orgPathName, Long demId, Long orgId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("demId", demId);
		params.put("orgPathName", orgPathName);
		params.put("orgId", orgId);
		SysOrg getByOrgPathName = this.getUnique("getByOrgPathName", params);
		return getByOrgPathName;
	}
	
	public SysOrg getByOrgPathNameForUpd(String orgPathName, Long demId, Long orgId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("demId", demId);
		params.put("orgPathName", orgPathName);
		params.put("orgId", orgId);
		return this.getUnique("getByOrgPathName", params);
	}
	
	/**
	 * 根据企业编码查询企业信息
	 * @param orgCodes
	 * @return
	 */
	public List<SysOrg> getByOrgCodes(List<String> orgCodes){
		return this.getBySqlKey("getByOrgCodes", orgCodes);
	}
	
	/**
	 * 获取组织详细信息列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getOrgDetailsList(QueryFilter queryFilter){
		return this.getListBySqlKey("getOrgDetailsList", queryFilter);
	}
	/**
	 * 获取组织详细信息列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysOrg> getToUIdByOrg(Long userId){
		return this.getBySqlKey("getToUIdByOrg",userId);
	}

}
