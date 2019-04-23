package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.system.SysKnowledgePer;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:SYS_ KNOWLEDGE _PERMISSION Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-07-28 10:23:07
 *</pre>
 */
@Repository
public class SysKnowledgePerDao extends BaseDao<SysKnowledgePer>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysKnowledgePer.class;
	}

	public List<SysKnowledgePer> getAllList(QueryFilter queryFilter) {
		return this.getBySqlKey("getAllList", queryFilter);
	}

	public List<SysKnowledgePer> getByUserIdFilter(Map<String, List<Long>> map) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("relationMap", map);//基本固定
		List<SysKnowledgePer> globalTypeList = this.getBySqlKey("getByUserIdFilter",params);
		return globalTypeList;
	}

	public void delByRefId(Long perRefId) {
		this.delBySqlKey("delByRefId", perRefId);
	}

}