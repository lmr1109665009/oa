package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.bpm.BpmProCopyto;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:流程抄送转发 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:heyifan
 * 创建时间:2013-03-27 15:07:58
 *</pre>
 */
@Repository
public class BpmProCopytoDao extends BaseDao<BpmProCopyto>
{
	@Override
	public Class<?> getEntityClass()
	{
		return BpmProCopyto.class;
	}
	
	public List<BpmProCopyto> getMyList(QueryFilter queryFilter){
		return this.getBySqlKey("getMyList",queryFilter);
	}

	public List<BpmProCopyto> getByRunId(Long runId) {
		return this.getBySqlKey("getByRunId",runId);
	}

	public List<BpmProCopyto> getByActInstId(Long actDeployId) {
		return this.getBySqlKey("getByActInstId",actDeployId);
	}
	
	/**
	 * 显示某个流程实例的抄送人员数据。
	 * @param queryFilter
	 * @return
	 */
	public List<BpmProCopyto> getUserInfoByRunId(QueryFilter queryFilter){
		return this.getBySqlKey("getUserInfoByRunId",queryFilter);
	}
	
	/**
	 * 通过运行ID和用户ID 获取抄送记录
	 * @param runId
	 * @param userId
	 * @return
	 */
	public List<BpmProCopyto> getByRunIdAndUserId(Long runId,Long userId){
		Map<String,Long> param = new HashMap<String, Long>();
		param.put("runId", runId);
		param.put("userId", userId);
		return this.getBySqlKey("getByRunIdAndUserId",param);
	}

	public int delByRunId(Long runId) {
		return this.delBySqlKey("delByRunId", runId);
	}
	
	/**
	 * 获取用户全部
	 * @param userId
	 * @return
	 */
	public Integer getCountByUser(Long userId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("ccUid", userId);
		return (Integer) this.getOne("getCountByUser", params);
	}	

	/**
	 * 获取用户未读
	 * @param userId
	 * @return
	 */
	public Integer getCountNotReadByUser(Long userId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("ccUid", userId);
		return (Integer) this.getOne("getCountNotReadByUser", params);
	}

	/**
	 * 根据运行Id和组织代码orgName获取用户数据。
	 * @return
	 */
	public List<BpmProCopyto> getUserInfoByRunIdAndOrgCode(Long runId,String orgCode){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("runId", runId);
		params.put("orgCode", orgCode);
		return this.getBySqlKey("getUserInfoByRunIdAndOrgCode",params);
	}
}