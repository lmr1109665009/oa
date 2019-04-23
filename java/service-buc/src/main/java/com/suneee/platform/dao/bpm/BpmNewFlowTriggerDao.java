package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.BpmNewFlowTrigger;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:触发新流程配置 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:miao
 * 创建时间:2015-05-28 11:20:59
 *</pre>
 */
@Repository
public class BpmNewFlowTriggerDao extends BaseDao<BpmNewFlowTrigger>
{
	@Override
	public Class<?> getEntityClass()
	{
		return BpmNewFlowTrigger.class;
	}
	
	
	public BpmNewFlowTrigger getByFlowkeyNodeId(String nodeId, String flowKey) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("nodeId", nodeId);
		params.put("flowkey", flowKey);
		return this.getUnique("getByFlowkeyNodeId",params);
	}
	
	/**
	 * 获取流程相关定义的触发定义。
	 * @param flowKey
	 * @return
	 */
	public List< BpmNewFlowTrigger> getByFlowkey( String flowKey) {
		return this.getBySqlKey("getByFlowkey",flowKey);
	}

	
}