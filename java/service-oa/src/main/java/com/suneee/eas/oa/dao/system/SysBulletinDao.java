
package com.suneee.eas.oa.dao.system;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.system.SysBulletin;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class SysBulletinDao extends BaseDao<SysBulletin>
{

	/**
	 * 添加数据到公告组织表
	 */
	public void addToBulletinOrg(Long bulletinId,String orgId){
		Map<String,Object> params = new HashMap<>();
		params.put("bulletinId",bulletinId);
		params.put("orgId",orgId);
		this.getSqlSessionTemplate().insert("addToBulletinOrg",params);
	}

	/**
	 * 通过公告id删除公告组织中间表的数据
	 */
	public void dellFromBulletinOrgByBulletin(Long bulletinId){
		Map<String,Long> params = new HashMap<>();
		params.put("bulletinId",bulletinId);
		this.getSqlSessionTemplate().delete("dellFromBulletinOrgByBulletin",params);
	}
}