/**
 * 
 */
package com.suneee.oa.dao.user;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.suneee.core.web.query.QueryFilter;
import com.suneee.oa.model.user.UserSynclog;
import com.suneee.ucp.base.dao.UcpBaseDao;

/**
 * @ClassName: UserSynclogDao 
 * @Description: 用户同步日志Dao类
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-01-04 10:56:46 
 *
 */
@Repository
public class UserSynclogDao extends UcpBaseDao<UserSynclog>{

	@Override
	public Class<UserSynclog> getEntityClass() {
		// TODO Auto-generated method stub
		return UserSynclog.class;
	}
	
	/**
	 * 根据用户ID删除用户同步日志
	 * @param userId
	 * @return
	 */
	public int deleteByUserId(Long userId){
		return this.delBySqlKey("delByUserId", userId);
	}
	
	/**
	 * 根据用户ID查询用户同步日志
	 * @param userId
	 * @return
	 */
	public UserSynclog getByUserId(Long userId){
		return this.getUnique("getByUserId", userId);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getList(QueryFilter filter){
		return this.getListBySqlKey("getList", filter);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getByLogId(Long logId){
		return (Map<String, Object>)this.getOne("getByLogId", logId);
	}

}
