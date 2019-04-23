package com.suneee.platform.dao.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.BaseDao;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.system.Position;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:系统岗位表，实际是部门和职务的对应关系表 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2013-11-27 10:19:23
 *</pre>
 */
@Repository
public class PositionDao extends BaseDao<Position>
{
	@Override
	public Class<?> getEntityClass()
	{
		return Position.class;
	}

	/**
	 * 根据用户ID得到该用户的主岗位名称。
	 * 获取用户主岗位，一个用户只有一个主岗位
	 * @param userId
	 * @return
	 */
	public Position getPrimaryPositionByUserId(Long userId){
		return (Position)this.getUnique("getPrimaryPositionByUserId", userId);
	}
	
	/**
	 * 根据用户ID得到该用户的主岗位名称。
	 * @param userId
	 * @return
	 */
	public Position getPosByUserId(Long userId){
		return getPrimaryPositionByUserId(userId);
	}
	
	/**
	 * 根据用户id获取岗位列表。
	 * @param userId
	 * @return
	 */
	public List<Position> getByUserId(Long userId){
		return this.getBySqlKey("getByUserId", userId);
	}
	
	/**
	 * 根据岗位名称获得岗位信息
	 * @param posName
	 * @return
	 */
	public List<Position> getByPosName(String posName) {
		return this.getBySqlKey("getByPosName", posName);
	}
	
	/**
	 * 根据组织id串得到用户和组织及岗位的关系
	 * @author hjx
	 * @version 创建时间：2013-11-27  下午3:16:17
	 * @param orgIds
	 * @return
	 */
	public List<Position> getOrgPosListByOrgIds(String orgIds){
		if(StringUtil.isEmpty(orgIds))return null;
		Map map =new HashMap();
		map.put("orgIds", orgIds);
		return  this.getBySqlKey("getOrgPosListByOrgIds", map);
	}
	
	/**
	 * 根据组织id串,得到组织集合
	 * @author hjx
	 * @version 创建时间：2013-11-27  下午3:16:17
	 * @param orgIds
	 * @return
	 */
	public List<Position> getOrgListByOrgIds(String orgIds){
		if(StringUtil.isEmpty(orgIds))return null;
		Map map =new HashMap();
		map.put("orgIds", orgIds);
		return  this.getBySqlKey("getOrgListByOrgIds", map);
	}
	
	public void deleteByUpdateFlag(Long posId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("delFrom", Position.DELFROM_POS_DEL);
		params.put("updatetime", new Date());
		params.put("updateBy", ContextUtil.getCurrentUserId());
		params.put("posId", posId);
		this.update("deleteByUpdateFlag", params);
	}
	
	@Override
	public int delById(Long posId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("delFrom", Position.DELFROM_POS_DEL);
		params.put("updatetime", new Date());
		params.put("updateBy", ContextUtil.getCurrentUserId());
		params.put("posId", posId);
		return this.update("delById", params);
	}

	public Position getByPosCode(String posCode){
		return this.getUnique("getByPosCode", posCode);
	}
	
	public Position getByPosCodeForUpd(String posCode, Long posId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("posCode", posCode);
		params.put("posId", posId);
		return this.getUnique("getByPosCodeForUpd", params);
	}
	
	public List<Position> getBySupOrgId(QueryFilter queryFilter) {
		return  this.getBySqlKey("getBySupOrgId", queryFilter);
	}
	
	/**
	 * 根据组织和职务id取得岗位。
	 * @param orgId	组织ID	
	 * @param jobId	职务ID
	 * @return
	 */
	public Position getByOrgJobId(Long orgId,Long jobId){
		Map<String,Long> params=new HashMap<String, Long>();
		params.put("orgId", orgId);
		params.put("jobId", jobId);
		return (Position)this.getUnique("getByOrgJobId", params);
	}

	public void deleByJobId(Long jobId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("delFrom", Position.DELFROM_JOB_DEL);
		params.put("updatetime", new Date());
		params.put("updateBy", ContextUtil.getCurrentUserId());
		params.put("jobId", jobId);
		this.update("delByJobId", params);
	}

	/**
	 * 根据组织id删除岗位
	 */
	public void delByOrgId(Long orgId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("delFrom", Position.DELFROM_ORG_DEL);
		params.put("updatetime", new Date());
		params.put("updateBy", ContextUtil.getCurrentUserId());
		params.put("orgId", orgId);
		this.update("delByOrgId", params);
	}
	
	/**
	 * 
	 * 判断职务是否被岗位使用
	 * @param jobId
	 * @return 
	 */
	public boolean isJobUsedByPos(Long jobId){
		Integer count = (Integer)this.getOne("isJobUsedByPos",jobId);
		return count>0;
	}
	
	/**
	 * 
	 * 根据岗位id取得相应岗位（唯一）再取poscode
	 * @param posId
	 * @return 
	 */
	public String getPosCode(Long posId){
		return this.getBySqlKey("getPosCode",posId).get(0).getPosCode();
	}
	
	/**
	 * 
	 * 判断poscode是否被没有被删除的岗位所使用
	 * @param posCode
	 * @return 
	 */
	public boolean isPoscodeUsed(String posCode){
		Integer count = (Integer)this.getOne("isPoscodeUsed",posCode);
		return !(count>0);
	}

	public Position getPosNameByUserId(Long userId) {
		List<Position> list = this.getBySqlKey("getByUserId", userId);
		if(list.size()==0){
			return new Position();
		}
		return list.get(0);
	}
	
	/** 根据职务ID获取岗位信息
	 * @param jobId
	 * @return
	 */
	public List<Position> getByJobId(Long jobId){
		return this.getBySqlKey("getByJobId", jobId);
	}
	
	/** 
	 * 根据组织ID获取岗位信息
	 * @param orgId
	 * @return
	 */
	public List<Position> getByOrgId(Long orgId){
		return this.getBySqlKey("getByOrgId", orgId);
	}
}