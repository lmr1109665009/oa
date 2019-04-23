package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.JobParam;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 对象功能:职务参数 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ouxb
 * 创建时间:2015-09-23 11:51:14
 * </pre>
 */
@Repository
public class JobParamDao extends BaseDao<JobParam> {
	@Override
	public Class<?> getEntityClass() {
		return JobParam.class;
	}

	/**
	 * 根据外键获取子表明细列表
	 * 
	 * @param jobid
	 * @return
	 */
	public List<JobParam> getByMainId(Long jobid) {
		return this.getBySqlKey("getJobParamList", jobid);
	}

	/**
	 * 根据外键删除子表记录
	 * 
	 * @param jobid
	 * @return
	 */
	public void delByMainId(Long jobid) {
		this.delBySqlKey("delByMainId", jobid);
	}
	
	/**
	 * 根据key和jobId,获取对应的value
	 * @param key
	 * @param jobId
	 * @return
	 */
	public String getValueByKeyJobId(String key, Long jobId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("key", key);
		params.put("jobId", jobId);
		return (String) this.getOne("getValueByKeyJobId",params);
	}

}