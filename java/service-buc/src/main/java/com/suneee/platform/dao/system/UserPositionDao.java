package com.suneee.platform.dao.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.BaseDao;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.model.system.Job;
import com.suneee.platform.model.system.UserPosition;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:SYS_USER_POS Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2013-11-27 10:19:23
 *</pre>
 */
@Repository
public class UserPositionDao extends BaseDao<UserPosition>
{
	@Override
	public Class<?> getEntityClass()
	{
		return UserPosition.class;
	}
	
	@Override
	public int delById(Long userPosId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userPosId", userPosId);
		params.put("updatetime", new Date());
		params.put("updateBy", ContextUtil.getCurrentUserId());
		params.put("delFrom", UserPosition.DELFROM_USER_POS_DEL);
		return this.update("delById", params);
	}
	/**
	 * 取得某个职位下的所有用户ID
	 * @param posId
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Long> getUserIdsByPosId(Long posId)
	{
		List list=getBySqlKey("getUserIdsByPosId", posId);
		return list;
	}
	
	
	
	/**
	 * 根据用户id删除用户和岗文的关系。
	 * @param userId 用户ID
	 * @param delFrom 删除源头
	 */
	public void delByUserId(Long userId, String delFrom){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("updatetime", new Date());
		params.put("updateBy",  ContextUtil.getCurrentUserId());
		params.put("delFrom", delFrom);
		this.update("delByUserId", params);
	}
	
	/**
	 * 获取用户在当前企业下的用户岗位信息
	 * @param param
	 * @return
	 */
	public List<UserPosition> getByUserId(Long userId)
	{ 
		return this.getByUserIdAndEnterpriseCode(userId, CookieUitl.getCurrentEnterpriseCode());
	}	
	
	/** 
	 * 获取用户在指定企业下的用户岗位关系
	 * @param userId
	 * @param enterpriseCode
	 * @return
	 */
	public List<UserPosition> getByUserIdAndEnterpriseCode(Long userId, String enterpriseCode){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("enterpriseCode", enterpriseCode);
		return getBySqlKey("getByUserId", params);
	}
	
	/** 
	 * 根据用户ID获取用户所有企业下的岗位信息
	 * @param userId
	 * @return
	 */
	public List<UserPosition> getAllByUserId(Long userId)
	{ 
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		return getBySqlKey("getByUserId", params);
	}
	
	/**
	 * 根据posId得到岗位
	 * @param param
	 * @return
	 */
	public List<UserPosition> getByPosId(Long userId)
	{ 
		return getBySqlKey("getByPosId", userId);
	}
	
	/**
	 * 根据userId得到组织，组织不能重复
	 * @param param
	 * @return
	 */
	public List<UserPosition> getOrgListByUserId(Long userId)
	{ 
		return getBySqlKey("getOrgListByUserId", userId);
	}	
	
	/**
	 * 根据用户ID得到用户的主岗位
	 * @param userId
	 * @param enterpriseCode
	 * @return
	 */
	public UserPosition getPrimaryUserPositionByUserId(Long userId){
		return this.getPrimaryUserPositionByUserId(userId, CookieUitl.getCurrentEnterpriseCode());
	}


	/**
	 * 根据用户ID得到用户指定企业下的主岗位
	 * @param userId
	 * @param enterpriseCode
	 * @return
	 */
	public UserPosition getPrimaryUserPositionByUserId(Long userId, String enterpriseCode){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("enterpriseCode", enterpriseCode);
		return this.getUnique("getPrimaryUserPositionByUserId", params);
	}
	
	/**
	 * 根据组织id串得到用户和组织及岗位的关系
	 * @author hjx
	 * @version 创建时间：2013-11-27  下午3:16:17
	 * @param orgIds
	 * @return
	 */
	public List<UserPosition> getUserByOrgIds(String orgIds){
		Map<String, Object> param =new HashMap<String, Object>();
		param.put("orgIds", orgIds);
		return  this.getBySqlKey("getUserByOrgIds", param);
	}
	
	/**
	 * 根据组织id获取组织负责人。
	 * @param orgId
	 * @return
	 */
	public List<UserPosition> getChargeByOrgId(Long orgId)	{
		return this.getBySqlKey("getChargeByOrgId", orgId);
	}	
	
	/**
	 * 根据用户ID获取可以负责的组织列表。
	 * @param userId
	 * @return
	 */
	public List<UserPosition> getChargeOrgByUserId(Long userId){
		return this.getBySqlKey("getChargeOrgByUserId", userId);
	}
	
	
	
	/**
	 * 根据组织ID删除组织用户关系。
	 * @param orgId
	 */
	public void delByOrgId(Long orgId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orgId", orgId);
		params.put("updatetime", new Date());
		params.put("updateBy",  ContextUtil.getCurrentUserId());
		params.put("delFrom", UserPosition.DELFROM_ORG_DEL);
		this.update("delByOrgId",params);
	}
	
	/**
	 * 根据组织路径，逻辑删除该组织及其子组织与岗位的关系
	 * @param path 
	 * void
	 * @exception 
	 * @since  1.0.0
	 */
	public void delByOrgPath(String path){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("path", path);
		params.put("updatetime", new Date());
		params.put("updateBy",  ContextUtil.getCurrentUserId());
		params.put("delFrom", UserPosition.DELFROM_ORG_DEL);
		this.update("delByOrgPath", params);
	}
	
	/**
	 * 根据组织路径，查询该组织及其子组织与岗位的关系
	 * @param path
	 * @return
	 */
	public List<UserPosition> getByOrgPath(String path){
		return this.getBySqlKey("getByOrgPath", path);
	}
	
	/**
	 * 根据岗位ID删除用户岗位关系
	 * @param param
	 * @return
	 */
	public void delByPosId(Long posId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("posId", posId);
		params.put("updatetime", new Date());
		params.put("updateBy",  ContextUtil.getCurrentUserId());
		params.put("delFrom", UserPosition.DELFROM_POS_DEL);
		this.update("delByPosId", params);
	}
	/**
	 * 根据组织ID获取对应关系
	 * @author hjx
	 * @version 创建时间：2013-11-28  上午9:59:35
	 * @param orgId
	 * @return
	 */
	public List<UserPosition> getByOrgId(Long orgId){
		return this.getBySqlKey("getByOrgId", orgId);
	}	
	
	/**
	 * 对象功能：查找该条件的用户组织的实体
	 * 开发公司:广州宏天软件有限公司
	 * 开发人员:pkq
     * 创建时间:2011-11-08 12:04:22 
	 */
	public UserPosition getUserPosModel(Long userId,Long posId)	
	{
		Map<String, Object> param =new HashMap<String, Object>();
		param.put("userId", userId);
		param.put("posId", posId);
		UserPosition userPosition=(UserPosition)getUnique("getUserPosModel", param);
		return userPosition;
	}	
	
	/**
	 * 更新用户组织为非主组织。
	 * @param userId
	 */
	public void updNotPrimaryByUserId(Long userId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("updatetime", new Date());
		params.put("updateBy", ContextUtil.getCurrentUserId());
		this.update("updNotPrimaryByUserId", params);
	}
	
	/**
	 * 根据UserId获取未删除的用户岗位信息
	 * @param userId
	 * @return
	 */
	public List<UserPosition> getPosIdByUserId(Long userId) {
		List<UserPosition> userPosition = this.getBySqlKey("getPosByUserId", userId);
		return userPosition;
	}

	/**
	 * 根据职务id更新用户岗位关系表
	 * @param jobId
	 */
	public void delByJobId(Long jobId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("jobId", jobId);
		params.put("updatetime", new Date());
		params.put("updateBy",  ContextUtil.getCurrentUserId());
		params.put("delFrom", UserPosition.DELFROM_JOB_DEL);
		this.update("delByJobId", params);
	}
	
	/**
	 * @param jobId
	 */
	public List<UserPosition> getByJobId(Long jobId){
		return getBySqlKey("getByJobId", jobId);
	}
	
	/**
	 * 
	 * 根据orgId判断组织下是否存在人员
	 * @param orgId
	 * @return 
	 */
	public boolean isUserExistFromOrg(Long orgId){
		int count= (Integer)this.getOne("isUserExistFromOrg", orgId);
		return count>0;
	}
	
	/**
	 * 获取用户的主岗位
	 * @param userId
	 * @return
	 */
	public UserPosition getChargeByUserId(Long userId) {
		List<UserPosition> list = this.getBySqlKey("getChargeByUserId", userId);
		if(list.size()==0){
			return new UserPosition();
		}
		return list.get(0);
	}

	/**
	 * 获取用户职务级别最高的岗位
	 * @param userId
	 * @return
	 */
	public UserPosition getMaxLevelByUserId(Long userId){
		List<UserPosition> list = this.getBySqlKey("getMaxLevelByUserId", userId);
		if(list.size()==0){
			return new UserPosition();
		}
		return list.get(0);
	}
	/**
	 * 查找用户是否有某个级别的职务
	 * @param userId
	 * @param aGrade
	 * @return
	 */
	public List<UserPosition> getByGradeAndUserId(Long userId,Long aGrade){
		Map<String, Object> a  = new HashMap<String, Object>();
		a.put("userId", userId);
		a.put("aGrade", aGrade);
		List<UserPosition> list = this.getBySqlKey("getByGradeAndUserId", a);
		return list;
	}
	
	public  List<UserPosition> getBydepartAndGrade(long orgId, int grade){
		Map<String, Object> a  = new HashMap<String, Object>();
		a.put("orgId", orgId);
		a.put("grade", grade);
		List<UserPosition> list = this.getBySqlKey("getBydepartAndGrade", a);
		return list;
	}

	/**
	 * 根据用户ID和组织路径获取岗位信息
	 * @param userId
	 * @param path
	 * @param orgCodes
	 * @return
	 */
	public List<UserPosition> getByUserIdAndOrgPath(Long userId, String path, List<String> orgCodes){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("path", path);
		params.put("orgCodes", orgCodes);
		return this.getBySqlKey("getByUserIdAndOrgPath", params);
	}

	/**
	 * 根据职位id查询用户
	 * */
	public List<Long> getUserIdByJobIds(List<Job> list){
		return this.getSqlSessionTemplate().selectList("getUserIdByJobIds", list);
	}
	
	/**
	 * 根据userId判断该用户是否有多个岗位
	 * @param userId
	 */
	
	public boolean getCountByUserId(Long userId){
		int userCount= (Integer)this.getOne("getCountByUserId", userId);
		return userCount>1;
	}
	
	/** 查询组织及其子组织下的用户ID
	 * @param path
	 * @return
	 */
	public List<Long> getUserIdByOrgPath(String path){
		Map<String, String> params = new HashMap<>();
		params.put("path", "%" + path + "%");
		params.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		return this.getSqlSessionTemplate().selectList("getUserIdByOrgPath", params);
	}


	public List getPrimaryOrgByUserId(Long userId, String enterpriseCode){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("enterpriseCode", enterpriseCode);
		return this.getListBySqlKey("getPrimaryOrgByUserId", params);
	}
}