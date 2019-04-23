package com.suneee.platform.service.form;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.bpm.TaskReadDao;
import com.suneee.platform.model.bpm.TaskRead;
import com.suneee.platform.model.system.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 *<pre>
 * 对象功能:任务是否已读 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:hotent
 * 创建时间:2013-04-16 17:30:53
 *</pre>
 */
@Service
public class TaskReadService extends BaseService<TaskRead>
{
	@Resource
	private TaskReadDao dao;
	
	
	
	public TaskReadService()
	{
	}
	
	@Override
	protected IEntityDao<TaskRead, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 添加查看记录
	 * @param actInstId		流程实例ID
	 * @param taskId		任务ID
	 */
	public void saveReadRecord(Long actInstId,Long taskId){
		SysUser sysUser=(SysUser) ContextUtil.getCurrentUser();
		Long userId=sysUser.getUserId();
		if(dao.isTaskRead(taskId, userId)) return;
		
		TaskRead taskRead=new TaskRead();
		taskRead.setId(UniqueIdUtil.genId());
		taskRead.setActinstid(actInstId);
		taskRead.setTaskid(taskId);
		taskRead.setUserid(userId);
		taskRead.setUsername(sysUser.getFullname());
		taskRead.setCreatetime(new Date());
		dao.add(taskRead);
	}
	
	/**
	 * 判断任务是否已读
	 * @param taskId 任务ID
	 * @param userId 用户ID
	 * @return
	 */
	public boolean isTaskRead(Long taskId,Long userId){
		return dao.isTaskRead(taskId, userId);
	}
	
	public List<TaskRead> getTaskRead(Long actInstId,Long taskId,String assignee){
		return dao.getTaskRead(actInstId,taskId,assignee);
	}

	public void delByActInstId(Long actInstId) {
		dao.delByActInstId(actInstId);
	}
}
