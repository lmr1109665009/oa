/**
 * 对象功能:流程运行日志 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:heyifan
 * 创建时间:2012-08-06 13:56:42
 */
package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.BpmRunLog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BpmRunLogDao extends BaseDao<BpmRunLog>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return BpmRunLog.class;
	}
	
	/**
	 * 通过用户ID获取用户操作的流程日志
	 * @param userId 用户ID
	 * @return
	 */
	public List<BpmRunLog> getByUserId(Long userId){		
		List list=getBySqlKey("getByUserId",userId);
		return list;
	}
	
	/**
	 * 通过流程运行ID获取流程的操作日志
	 * @param runId 流程运行ID
	 * @return
	 */
	public List<BpmRunLog> getByRunId(Long runId){		
		List list=getBySqlKey("getByRunId",runId);
		return list;
	}
	
	/**
	 * 根据流程运行ID删除流程操作日志
	 * @param runId
	 */
	public void delByRunId(Long runId) {
		this.delBySqlKey("delByRunId", runId);
	}
}