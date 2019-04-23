/**
 * 对象功能:下属管理 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-07-05 10:08:08
 */
package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.system.UserUnder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserUnderDao extends BaseDao<UserUnder>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return UserUnder.class;
	}

	
	/**
	 * 取得我的下属
	 * @param userId
	 * @return
	 */
	public List<UserUnder> getMyUnderUser(Long userId){
		return this.getBySqlKey("getMyUnderUser", userId);
	}


	/**
	 * 根据用户id，下属Id判断是否已经存在
	 * @param userUnder
	 * @return
	 */
	public Integer isExistUser(UserUnder userUnder){
		return (Integer)this.getOne("isExistUser", userUnder);
	}

	
	/**
	 * 根据用户ID获取领导。
	 * @param userId
	 * @return
	 */
	public List<UserUnder> getMyLeader(Long userId){
		return this.getBySqlKey("getMyLeader", userId);
	}


	public void delByUpUserId(Long userId) {
		this.delBySqlKey("delByUpUserId", userId);
	}
	
	
	public void delByUpAndDown(Long upUserId, Long downUserId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("upUserId", upUserId);
		params.put("downUserId", downUserId);
		this.delBySqlKey("delByUpAndDown", params);
		
	}
	
	/**
	 * 根据用户Id删除上级Id。
	 * @param underUserId
	 */
	public void delByUnderUserId(Long underUserId) {
		this.delBySqlKey("delByUnderUserId", underUserId);
		
	}
	
	
	/** 
	 * 根据用户中心用户ID获取用户的上级用户信息
	 * @param ucUserid
	 * @return
	 */
	public List<Map<String, Object>> getLeaderByUcUserid(Long ucUserid){
		String statement = this.getIbatisMapperNamespace() + ".getLeaderByUcUserid";
		return this.getSqlSessionTemplate().selectList(statement, ucUserid);
	}
	
	/** 
	 * 根据用户中心的用户ID获取用户的下属用户信息
	 * @param ucUserid
	 * @return
	 */
	public List<Map<String, Object>> getUnderUserByUcUserid(Long ucUserid){
		String statement = this.getIbatisMapperNamespace() + ".getUnderUserByUcUserid";
		return this.getSqlSessionTemplate().selectList(statement, ucUserid);
	}
	
	/** 
	 * 获取用户下属
	 * @param filter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getUnderUser(QueryFilter filter){
		return this.getListBySqlKey("getUnderUser", filter);
	}
}