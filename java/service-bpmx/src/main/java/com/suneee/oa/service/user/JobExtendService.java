package com.suneee.oa.service.user;

import com.suneee.core.db.IEntityDao;
import com.suneee.oa.dao.user.JobExtendDao;
import com.suneee.platform.model.system.Job;
import com.suneee.ucp.base.service.UcpBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 职务信息Job扩展Service类
 * @author xiongxianyun
 *
 */
@Service
public class JobExtendService extends UcpBaseService<Job> {

	@Resource
	private JobExtendDao jobExtendDao;

	@Override
	protected IEntityDao<Job, Long> getEntityDao() {
		return this.jobExtendDao;
	}

	public void delByIds(Long[] jobIds) {
		
	}

	/**
	 * 判断职务是否被用户使用
	 * @param jobId
	 * @return
	 */
	public boolean isJobUsedByUser(Long jobId){
		return jobExtendDao.isJobUsedByUser(jobId);
	}
}