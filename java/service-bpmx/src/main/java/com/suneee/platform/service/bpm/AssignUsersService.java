package com.suneee.platform.service.bpm;
import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.bpm.AssignUsersDao;
import com.suneee.platform.model.bpm.AssignUsers;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.bpm.AssignUsersDao;
import com.suneee.platform.model.bpm.AssignUsers;

/**
 *<pre>
 * 对象功能:bpm_assign_users Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-02-11 11:22:47
 *</pre>
 */
@Service
public class AssignUsersService extends BaseService<AssignUsers>
{
	@Resource
	private AssignUsersDao dao;
	
	
	
	public AssignUsersService()
	{
	}
	
	@Override
	protected IEntityDao<AssignUsers, Long> getEntityDao()
	{
		return dao;
	}


	public Long addAssignUser(List<AssignUsers> assignUserslList) {
		if (BeanUtils.isEmpty(assignUserslList)) return 0L;
		Long runId = UniqueIdUtil.genId();
		for (AssignUsers assignUsers : assignUserslList) {
			assignUsers.setId(UniqueIdUtil.genId());
			assignUsers.setRunId(runId);
			dao.add(assignUsers);
		}
		return runId;
	}

	public List<AssignUsers> getByRunIdAndNodeId(Long runId, String nodeId) {
		
		return dao.getByRunIdAndNodeId(runId,nodeId);
	}

	
	
	
}
