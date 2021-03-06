package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.BpmNodeSql;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:bpm_node_sql Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-03-05 09:59:29
 *</pre>
 */
@Repository
public class BpmNodeSqlDao extends BaseDao<BpmNodeSql>
{
	@Override
	public Class<?> getEntityClass()
	{
		return BpmNodeSql.class;
	}

	
	public List getByDef(String actdefId,String nodeId,String action)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("actdefId", actdefId);
		map.put("nodeId", nodeId);
		map.put("action", action);
		
		return this.getBySqlKey("getByDef", map);
		
		
	}
	
	
	
	
}