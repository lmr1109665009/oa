package com.suneee.platform.service.bpm;

import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.dao.bpm.TaskUserDao;
import com.suneee.platform.dao.system.*;
import com.suneee.platform.model.bpm.TaskUser;
import com.suneee.platform.model.system.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TaskUserService extends BaseService<TaskUser> {
	@Resource
	private TaskUserDao taskUserDao;
	@Resource
	private SysUserDao sysUserDao;
	
	@Resource
	private SysOrgDao sysOrgDao;
	@Resource
	private PositionDao positionDao;
	@Resource
	private SysRoleDao sysRoleDao;
	@Resource
	private JobDao jobDao;
	
	@Override
	protected IEntityDao<TaskUser, Long> getEntityDao(){
		return taskUserDao;
	}
	
	public List<TaskUser> getByTaskId(String taskId){
		
		return taskUserDao.getByTaskId(taskId);
	}
	
	/**
	 * 取得任务的候选用户
	 * @param taskId
	 * @return
	 */
	public Set<TaskExecutor> getCandidateExecutors(String taskId){
		
		Set<TaskExecutor> taskUserSet=new HashSet<TaskExecutor>();
		List<TaskUser> taskUsers=getByTaskId(taskId);
		for(TaskUser taskUser:taskUsers){
			if(taskUser.getUserId()!=null 
					&& !BpmConst.EMPTY_USER.equals(taskUser.getUserId())){
				SysUser sysUser=sysUserDao.getById(new Long(taskUser.getUserId()));
				if(BeanUtils.isNotEmpty(sysUser))
//				taskUserSet.add(TaskExecutor.getTaskUser(taskUser.getUserId(), sysUser.getFullname()));
				taskUserSet.add(TaskExecutor.getTaskUser(taskUser.getUserId(), ContextSupportUtil.getUsername(sysUser)));
			}else if(taskUser.getGroupId()!=null){
				String tmpId=taskUser.getGroupId();
				if(TaskExecutor.USER_TYPE_ORG.equals(taskUser.getType())){//组织下的用户
					SysOrg sysOrg=sysOrgDao.getById(new Long(tmpId));
					taskUserSet.add(TaskExecutor.getTaskOrg(tmpId, sysOrg.getOrgName()));
				}else if(TaskExecutor.USER_TYPE_POS.equals(taskUser.getType())){//岗位下的用户
					Position position=positionDao.getById(new Long(tmpId));
					taskUserSet.add(TaskExecutor.getTaskPos(tmpId, position.getPosName()));
				}else if(TaskExecutor.USER_TYPE_ROLE.equals(taskUser.getType())){//角色下的用户
					SysRole sysRole=sysRoleDao.getById(new Long(tmpId));
					taskUserSet.add(TaskExecutor.getTaskRole(tmpId, sysRole.getRoleName()));
				}else if(TaskExecutor.USER_TYPE_JOB.equals(taskUser.getType())){//职务下的用户
					Job job=jobDao.getById(new Long(tmpId));
					taskUserSet.add(TaskExecutor.getTaskJob(tmpId, job.getJobname()));
				}
			}
		}
		return taskUserSet;
	}
	
	/**
	 * 根据任务Id获取任务候选人。
	 * @param taskId
	 * @return
	 */
	public Set<SysUser> getCandidateUsers(String taskId){
		Set<SysUser> taskUserSet=new HashSet<SysUser>();
		List<TaskUser> taskUsers=getByTaskId(taskId);
		for(TaskUser taskUser:taskUsers){
			if(taskUser.getUserId()!=null
					&& !BpmConst.EMPTY_USER.equals(taskUser.getUserId())){
				SysUser sysUser=sysUserDao.getById(Long.parseLong( taskUser.getUserId()));
				taskUserSet.add(sysUser);
			}else if(taskUser.getGroupId()!=null){
				Long tmpId=Long.parseLong(taskUser.getGroupId());
				if(TaskExecutor.USER_TYPE_ORG.equals(taskUser.getType())){//组织下的用户
					List<SysUser> userList= sysUserDao.getByOrgId(tmpId);
					taskUserSet.addAll(userList);
				}else if(TaskExecutor.USER_TYPE_POS.equals(taskUser.getType())){//岗位下的用户
					List<SysUser> userList= sysUserDao.getByPosId(tmpId);
					taskUserSet.addAll(userList);
				}else if(TaskExecutor.USER_TYPE_ROLE.equals(taskUser.getType())){//角色下的用户
					List<SysUser> userList=sysUserDao.getByRoleId(tmpId);
					taskUserSet.addAll(userList);
				}
			}
		}
		return taskUserSet;
	}
}
