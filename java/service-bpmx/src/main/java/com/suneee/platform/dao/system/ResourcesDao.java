/**
 * 对象功能:子系统资源 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2011-12-05 17:00:54
 */
package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.model.system.Resources;
import com.suneee.platform.model.system.ResourcesUrlExt;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ResourcesDao extends BaseDao<Resources>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return Resources.class;
	}
	
	/**
	 * 根据父id获取资源数据。
	 * @param parentId
	 * @return
	 */
	public List<Resources> getByParentId(Long parentId, Short fromType){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("parentId", parentId);
		params.put("fromType", fromType);
		return this.getBySqlKey("getByParentId", params);
	}
	
	
	/**
	 * 根据系统id获取所有的资源列表。
	 * @param systemId
	 * @return
	 */
	public List<Resources> getBySystemId(long systemId, Short fromType){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("systemId", systemId);
		params.put("fromType", fromType);
		return this.getBySqlKey("getBySystemId", params);
	}
	
	
	/**
	 * 根据系统id和当前用户id获取资源菜单。
	 * @param systemId
	 * @param userId
	 * @return
	 */
	public List<Resources> getNormMenu(Long systemId,Long userId){
		Map<String, Long> p=new HashMap<String, Long>();
		p.put("systemId", systemId);
		p.put("userId", userId);
		return this.getBySqlKey("getNormMenu", p);
	}
	
	/**
	 * 根据系统id和当前用户角色获取当前企业下的资源菜单
	 * @param systemId
	 * @param roles
	 * @return
	 */
	public List<Resources> getNormMenuByRole(long systemId,Long userId,Short fromType ){
		return this.getNormMenuByRole(systemId, userId, fromType, ContextSupportUtil.getCurrentEnterpriseCode());
	}
	
	/**
	 * 获取用户指定企业下的资源菜单 
	 * @param systemId
	 * @param userId
	 * @param fromType
	 * @param enterpriseCode
	 * @return
	 */
	public List<Resources> getNormMenuByRole(Long systemId, Long userId, Short fromType, String enterpriseCode){
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("systemId", systemId);
		p.put("userId", userId);
		p.put("fromType", fromType);
		p.put("enterpriseCode", enterpriseCode);
		return this.getBySqlKey("getNormMenuByRole", p);
	}
	
	/**
	 * 根据系统id和当前用户角色(自己和所属于部门的所有角色)
	 * @param systemId
	 * @param roles
	 * @return
	 */
	public List<Resources> getNormMenuByAllRole(long systemId,String rolealias, Short fromType ){
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("systemId", systemId);
		p.put("rolealias", rolealias);
		p.put("fromType", fromType);
		p.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		return this.getBySqlKey("getNormMenuByAllRole", p);
	}
	
	/**
	 * 根据系统id获取全部的菜单。
	 * @param systemId
	 * @param fromType
	 * @return
	 */
	public List<Resources> getSuperMenu(Long systemId, Short fromType){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("systemId", systemId);
		params.put("fromType", fromType);
		return this.getBySqlKey("getSuperMenu", params);
	}
	
	/**
	 * 根据系统id获取资源的默认URL和角色映射对象列表。
	 * @param systemId	系统id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ResourcesUrlExt> getDefaultUrlAndRoleBySystemId(long systemId){
		String stament=this.getIbatisMapperNamespace() + ".getDefaultUrlAndRoleBySystemId";
		return this.getSqlSessionTemplate().selectList(stament, systemId);
	}
	
	/**
	 * 根据系统id获取功能和角色的映射。
	 * @param systemId		系统ID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ResourcesUrlExt> getFunctionAndRoleBySystemId(long systemId){
		String stament=this.getIbatisMapperNamespace() + ".getFunctionAndRoleBySystemId";
		return this.getSqlSessionTemplate().selectList(stament, systemId);
	}
	

	
	
	
	/**
	 * 判断别名在该系统中是否存在。
	 * @param systemId	系统id
	 * @param alias		系统别名
	 * @return
	 */
	public Integer isAliasExists(Long systemId,String alias){
		Map<String, Object> params= new HashMap<String, Object>();
		params.put("alias", alias);
		params.put("systemId", systemId);
		return (Integer)this.getOne("isAliasExists", params);
	}
	
	
	/**
	 * 判断别名是否存在。
	 * @param systemId
	 * @param resId
	 * @param alias
	 * @return
	 */
	public Integer isAliasExistsForUpd(Long systemId,Long resId, String alias){
		Map<String, Object> params= new HashMap<String, Object>();
		params.put("alias", alias);
		params.put("systemId", systemId);
		params.put("resId", resId);
		return (Integer)this.getOne("isAliasExistsForUpd", params);
	}
	
	/**
	 * 根据路径取得资源实体列表
	 * @param url
	 * @return
	 */
	public List<Resources> getByUrl(String url) {
		return this.getBySqlKey("getByUrl", url);
	}

	public List<Resources> getBySystemIdAndParentId(long systemId, long parentId) {
		Map<String, Object> params= new HashMap<String, Object>();
		params.put("parentId", parentId);
		params.put("systemId", systemId);
		return this.getBySqlKey("getBySystemIdAndParentId", params);
	}
	

	public void updSn(Long resId, long sn) {
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("resId", resId);
		map.put("sn", sn);
		this.update("updSn", map);
	}
	
	/**
	 * 根据系统别名和url获取url和角色的映射。
	 * @param systemAlias
	 * @param url
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ResourcesUrlExt> getDefaultUrlAndRoleByUrlSystemAlias(String systemAlias,String url){
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("sysAlias", systemAlias);
		params.put("url", url);
		String stament=this.getIbatisMapperNamespace() + ".getDefaultUrlAndRoleByUrlSystemAlias";
		return this.getSqlSessionTemplate().selectList(stament, params);
	}
	/**
	 * 根据系统id获取功能和角色的映射。
	 * @param systemId		系统ID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ResourcesUrlExt> getFunctionAndRoleBySystemAlias(String sysAlias,String func){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("sysAlias", sysAlias);
		map.put("func", func);
		String stament=this.getIbatisMapperNamespace() + ".getFunctionAndRoleBySystemAlias";
		return this.getSqlSessionTemplate().selectList(stament, map);
	}
	
	
	/**
	 * 根据系统Id和别名获取资源。
	 * @param systemId
	 * @param alias
	 * @return
	 */
	public Resources getByAlias(Long systemId,String alias){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("systemId",systemId);
		map.put("alias",alias);
		
		
		return this.getUnique("getByAlias", map);
	}
	
	
	/**
	 * 根据父id和用户id获取下级tab资源。
	 * @param resId
	 * @param userId
	 * @return
	 */
	@Deprecated
	public List<Resources> getByParentUserId(Long resId,Long userId){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("resId",resId);
		map.put("userId",userId);
		
		return this.getBySqlKey("getByParentUserId", map);
	}
	/**
	 * 通过角色获取下级资源
	 * @param resId
	 * @param rolealias
	 * @return
	 */
	public List<Resources> getNormMenuByAllRoleParentId(Long resId,String rolealias){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("resId",resId);
		map.put("rolealias",rolealias);
		
		return this.getBySqlKey("getNormMenuByAllRoleParentId", map);
	}
	
	
	
}