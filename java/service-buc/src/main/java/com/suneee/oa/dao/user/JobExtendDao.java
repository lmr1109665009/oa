package com.suneee.oa.dao.user;

import com.suneee.platform.model.system.Job;
import com.suneee.ucp.base.dao.UcpBaseDao;
import org.springframework.stereotype.Repository;

/**
 * 职务信息Job扩展DAO类
 * @author xiongxianyun
 *
 */
@Repository
public class JobExtendDao extends UcpBaseDao<Job> {
	@Override
	public Class<Job> getEntityClass() {
		return Job.class;
	}


	/**
	 * 判断职务是否被用户使用
	 * @param jobId
	 * @return
	 */
	public boolean isJobUsedByUser(Long jobId){
		Integer count = (Integer)this.getOne("isJobUsedByUser",jobId);
		return count>0;
	}
}