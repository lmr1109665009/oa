package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.dao.system.UserUnderDao;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.UserUnder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 对象功能:下属管理 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-07-05 10:08:08
 */
@Service
public class UserUnderService extends BaseService<UserUnder>
{
	@Resource
	private UserUnderDao dao;
	@Resource
	private SysUserDao sysUserDao;
	
	public UserUnderService()
	{
	}
	
	@Override
	protected IEntityDao<UserUnder, Long> getEntityDao()
	{
		return dao;
	}

	
	/**
	 * 获取某用户的下属用户
	 * @param userId
	 * @return
	 */
	public List<UserUnder> getMyUnderUser(Long userId){
		return dao.getMyUnderUser(userId);
	}
	
	/**
	 * 添加下属用户。
	 * @param userId	领导ID
	 * @param userIds	下属用户ID使用逗号分隔。
	 * @param userNames	下属用户名称使用逗号分隔。
	 * @throws Exception 
	 */
	public void addMyUnderUser(Long userId,String userIds,String userNames) throws Exception {
		String[] idArray=userIds.split(",");
		String[] nameArray=userNames.split(",");
		UserUnder userUnder=new UserUnder();
		userUnder.setUserid(userId);
		for(int i=0;i<idArray.length;i++){
			userUnder.setId(UniqueIdUtil.genId());
			userUnder.setUnderuserid(Long.parseLong(idArray[i]));
			userUnder.setUnderusername(nameArray[i]);
			if(dao.isExistUser(userUnder)>0)  continue;
			dao.add(userUnder);
		}
	}

	/**
	 * 保存上级
	 * @param userId
	 * @param fullname
	 * @param arySuperiorId
	 */
	public void saveSuperior(Long userId, String fullname,Long[] arySuperiorId){
		dao.delByUnderUserId(userId);
		//上级执行人为空
		if(arySuperiorId==null || arySuperiorId.length==0){
			return;
		}
		
//		SysUser sysUser= sysUserDao.getById(userId);
		
		UserUnder userUnder=new UserUnder();
		userUnder.setUnderuserid(userId);
		userUnder.setUnderusername(fullname);
		for(int i=0;i<arySuperiorId.length;i++){
			userUnder.setId(UniqueIdUtil.genId());			
			userUnder.setUserid(arySuperiorId[i]);
			dao.add(userUnder);
		}
	}

	/**
	 * 获取我的下属用户ID集合。
	 * @param userId
	 * @return
	 */
	public Set<String> getMyUnderUserId(Long userId){
		Set<String> list=new HashSet<String>();
		List<UserUnder> listUser= dao.getMyUnderUser(userId);
		for(UserUnder user:listUser){
			list.add(user.getUnderuserid().toString());
		}
		return list;
	} 
	
	/**
	 * 根据用户获取领导Id号。
	 * @param userId
	 * @return
	 */
	public Set<TaskExecutor> getMyLeader(Long userId){
		Set<TaskExecutor> list=new HashSet<TaskExecutor>();
		List<UserUnder> userList= dao.getMyLeader(userId);
		for(UserUnder user:userList){
			list.add(TaskExecutor.getTaskUser( user.getUserid().toString(),user.getLeaderName()));
		}
		return list;
	}
	
	/**
	 * 根据用户获取领导。
	 * @param userId
	 * @return
	 */
	public List<SysUser> getMyLeaders(Long userId){
		return sysUserDao.getUserByUnderUserId(userId);
	}

	/**
	 * 
	 * @param userId
	 * @param userId2
	 * @return 
	 */
	public boolean getByUpAndDown(Long upUserId, Long downUserId) {
		UserUnder userUnder=new UserUnder();
		userUnder.setUserid(upUserId);
		userUnder.setUnderuserid(downUserId);
		return dao.isExistUser(userUnder)>0?true:false;
	}

	/**
	 * 根据用户Id删除所有的下属关系
	 * @param userId
	 */
	public void delByUpUserId(Long userId) {
		dao.delBySqlKey("delByUpUserId", userId);
	}
	
	/** 
	 * 根据用户ID删除所有的上级关系
	 * @param userId
	 */
	public void delByUnderUserId(Long userId){
		dao.delByUnderUserId(userId);
	}

	/**
	 * 根据用户ID和下级用户Id删除上下级关系
	 * @param upUserId
	 * @param downUserId
	 */
	public void delByUpAndDown(Long upUserId, Long downUserId) {
		dao.delByUpAndDown(upUserId,downUserId);
	}
	
	/** 
	 * 根据用户中心用户ID获取用户的上级用户信息
	 * @param ucUserid
	 * @return
	 */
	public List<Map<String, Object>> getLeaderByUcUserid(Long ucUserid){
		return dao.getLeaderByUcUserid(ucUserid);
	}
	
	/** 
	 * 根据用户中心的用户ID获取用户的下属用户信息
	 * @param ucUserid
	 * @return
	 */
	public List<Map<String, Object>> getUnderUserByUcUserid(Long ucUserid){
		return dao.getUnderUserByUcUserid(ucUserid);
	}
	
	/** 
	 * 获取用户下属信息
	 * @param filter
	 * @return
	 */
	public List<Map<String, Object>> getUnderUser(QueryFilter filter){
		return dao.getUnderUser(filter);
	}
}
