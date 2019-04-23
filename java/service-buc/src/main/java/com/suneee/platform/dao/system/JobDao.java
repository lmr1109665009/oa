package com.suneee.platform.dao.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.Job;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:职务表 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2013-11-28 16:17:48
 *</pre>
 */
@Repository
public class JobDao extends BaseDao<Job>
{
	@Override
	public Class<?> getEntityClass()
	{
		return Job.class;
	}

	public void deleteByUpdateFlag(Long id){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("jobid", id);
		params.put("updatetime", new Date());
		params.put("updateBy", ContextUtil.getCurrentUserId());
		this.update("deleteByUpdateFlag", params);
	}
	
	/**
	 * 根据用户id获取职务列表。
	 * @param userId
	 * @return
	 */
	public List<Job> getByUserId(Long userId){
		return this.getBySqlKey("getByUserId", userId);
	}
	
	public Job getByJobCode(String jobCode){
		return this.getUnique("getByJobCode", jobCode);
	}
	
	public Job getByJobCodeForUpd(String jobCode, Long jobid){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("jobcode", jobCode);
		params.put("jobid", jobid);
		return this.getUnique("getByJobCodeForUpd", params);
	}
	
	/**
	 * 判断职务名称是否存在
	 * @param jobname
	 * @return
	 */
	public boolean isExistJobCode(String jobCode) {
		Integer count=(Integer)this.getOne("isExistJobCode", jobCode);
		return count>0;
	}
	
	
	
	/**
	 * 判断职务是否存在，更新时使用。
	 * @param jobCode
	 * @param jobId
	 * @return
	 */
	public boolean isExistJobCodeForUpd(String jobCode,Long jobId) {
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("jobCode", jobCode);
		params.put("jobid", jobId);
		Integer count=(Integer)this.getOne("isExistJobCodeForUpd", params);
		return count>0;
	}
	
	/** 
	 * 获取用户的最高职务分类级别
	 * @param userId
	 * @param enterpriseCode
	 * @return
	 */
	public Long getMaxCategoryByUidAndEcode(Long userId, String enterpriseCode){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("enterpriseCode", enterpriseCode);
		return (Long)this.getOne("getMaxCategoryByUidAndEcode", params);
	}
}