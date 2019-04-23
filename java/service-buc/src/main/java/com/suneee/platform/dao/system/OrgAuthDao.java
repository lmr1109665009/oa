package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.OrgAuth;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:SYS_ORG_AUTH Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:miao
 * 创建时间:2014-08-08 10:19:21
 *</pre>
 */
@Repository
public class OrgAuthDao extends BaseDao<OrgAuth>
{
	@Override
	public Class<?> getEntityClass()
	{
		return OrgAuth.class;
	}

	public List<OrgAuth> getByUserId(long userId) {
		return this.getBySqlKey("getByUserId", userId);
	}

	public OrgAuth getUserIdDimId(Long dimId, Long userId) {
		HashMap<String, Object> map = new HashMap<String,Object>();
		map.put("dimId", dimId);
		map.put("userId", userId);
		return this.getUnique("getUserIdDimId", map);
	}

	public boolean checkOrgAuthIsExist(Long userId, Long orgId){
		Map map = new HashMap<String, Long>();
		map.put("orgId", orgId);
		map.put("userId", userId);
		int count = (Integer) this.getOne("checkOrgAuthIsExist", map);
		if(count > 0) return true;
		return false;
	}


	public OrgAuth getByUserIdAndOrgId(long userId, long orgId) {
		Map map = new HashMap<String, Long>();
		map.put("orgId", orgId);
		map.put("userId", userId);
		return (OrgAuth) this.getOne("getByUserIdAndOrgId", map);
		
	}
	
	
	
	public void delByUserId(Long userId){
		delBySqlKey("delByUserId", userId);
	}
	
	public void delByOrgId(Long orgId){
		delBySqlKey("delByOrgId", orgId);
	}


}