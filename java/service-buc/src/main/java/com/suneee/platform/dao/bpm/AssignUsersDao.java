package com.suneee.platform.dao.bpm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.AssignUsers;
/**
 *<pre>
 * 对象功能:bpm_assign_users Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-02-11 11:22:47
 *</pre>
 */
@Repository
public class AssignUsersDao extends BaseDao<AssignUsers>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AssignUsers.class;
	}

	public List<AssignUsers> getByRunIdAndNodeId(Long runId, String nodeId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("runId", runId);
		params.put("nodeId", nodeId);
		return this.getBySqlKey("getByRunIdAndNodeId", params);
	}

	
	
	
	
}