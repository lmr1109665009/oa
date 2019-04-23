/**
 * 对象功能:总分类表
 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ljf
 * 创建时间:2011-11-23 11:07:27
 */
package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.platform.model.system.GlobalType;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class GlobalTypeDao extends BaseDao<GlobalType> {
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass() {
		return GlobalType.class;
	}



	/**
	 * 根据路径取子结点
	 * 
	 * @param parentId
	 * @return
	 */
	public List<GlobalType> getByNodePath(String nodePath) {
		Map<String, String> params=new HashMap<String, String>();
		params.put("nodePath", nodePath+"%");
		return this.getBySqlKey("getByNodePath", params);
	}



	
	/**
	 * 根据路径取子结点
	 * @param parentId
	 * @param catKay
	 * @return
	 */
	public List<GlobalType> getByParentId(long parentId, String catKey) {
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("catkey", catKey);
		params.put("parentId", parentId);
		return this.getBySqlKey("getByParentId", params);
	
	}

	/**
	 * 根据查询条件获取子结点
	 * @param filter 查询条件
	 * @return
	 */
	public List<GlobalType> getByQueryfilter(QueryFilter filter) {
		return this.getBySqlKey("getByQueryfilter",filter);
	}

	/**
	 * 判断nodekey在某分类中是否存在。
	 * @param catKey
	 * @param nodeKey
	 * @return
	 */
	public boolean isNodeKeyExists(String catKey,String nodeKey,String enterpriseCode)
	{
		Map<String,String> params=new HashMap<String, String>();
		params.put("catkey", catKey);
		params.put("nodeKey", nodeKey);
		params.put("enterprise_code", enterpriseCode);
		int rtn= (Integer)this.getOne("isNodeKeyExists", params);
		return rtn>0;
	}	
	
	/**
	 * 判断nodekey 在该大类下是否存在。
	 * @param typeId
	 * @param catKey
	 * @param nodeKey
	 * @return
	 */
	public boolean isNodeKeyExistsForUpdate(Long typeId,String catKey,String nodeKey,String enterpriseCode){
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("typeId", typeId);
		params.put("catkey", catKey);
		params.put("nodeKey", nodeKey);
		params.put("enterpriseCode", enterpriseCode);
		int rtn= (Integer)this.getOne("isNodeKeyExistsForUpdate", params);
		return rtn>0;
	}

	/**
	 * 根据企业编码查询分类是否重复
	 * @param typeId
	 * @param catKey
	 * @param nodeKey
	 * @return
	 */
	public boolean isNodeKeyExistsForEnterprise(Long typeId,String catKey,String nodeKey,String ecodes){
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("typeId", typeId);
		params.put("catkey", catKey);
		params.put("nodeKey", nodeKey);
		params.put("enterprise_code", ecodes);
		int rtn= (Integer)this.getOne("isNodeKeyExistsForEnterprise", params);
		return rtn>0;
	}
	public boolean isNodeKeyExistsForEnterprise(String catKey,String nodeKey,String ecodes){
		return isNodeKeyExistsForEnterprise(null,catKey,nodeKey,ecodes);
	}

	/**
	 * 更新在分类中的排序数据。
	 * @param typeId
	 * @param sn
	 */
	public void updSn(Long typeId,Long sn){
		GlobalType globalType=new GlobalType();
		globalType.setTypeId(typeId);
		globalType.setSn(sn);
		this.update("updSn", globalType);
	}
	
	/**
	 * 根据catkey获取数据。
	 * @param catKey
	 * @param typeIdList
	 * @return
	 */
	public List<GlobalType> getByCatKey(String catKey, List<Long> typeIdList){
		return getByCatKey(catKey,typeIdList,null);
	}
	public List<GlobalType> getByCatKey(String catKey, List<Long> typeIdList,String nodePath){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("catkey", catKey);
		params.put("typeIdList", typeIdList);
		if (StringUtil.isNotEmpty(nodePath)){
			List<GlobalType> list=this.getByNodePath(nodePath);
			if (list.size()>0){
				List<Long> ids=new ArrayList<Long>();
				for (GlobalType type:list){
					ids.add(type.getTypeId());
				}
				params.put("notIds",ids);
			}
		}
		return this.getBySqlKey("getByCatKey", params);
	}

	/**
	 * 根据企业编码来查询分类
	 * @param catKey
	 * @param ecodes
	 * @param nodePath
	 * @return
	 */
	public List<GlobalType> getByCatKey(String catKey, Set<String> ecodes,String nodePath){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("catkey", catKey);
		params.put("ecodes", ecodes);
		if (StringUtil.isNotEmpty(nodePath)){
			List<GlobalType> list=this.getByNodePath(nodePath);
			if (list.size()>0){
				List<Long> ids=new ArrayList<Long>();
				for (GlobalType type:list){
					ids.add(type.getTypeId());
				}
				params.put("notIds",ids);
			}
		}
		return this.getBySqlKey("getByCatKey", params);
	}
	public List<GlobalType> getByCatKey(String catKey, Set<String> ecodes){
		return getByCatKey(catKey,ecodes,null);
	}

	public List<GlobalType> getByCatKey(String catKey, String ecode,String nodePath){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("catkey", catKey);
		params.put("ecode", ecode);
		if (StringUtil.isNotEmpty(nodePath)){
			List<GlobalType> list=this.getByNodePath(nodePath);
			if (list.size()>0){
				List<Long> ids=new ArrayList<Long>();
				for (GlobalType type:list){
					ids.add(type.getTypeId());
				}
				params.put("notIds",ids);
			}
		}
		return this.getBySqlKey("getByCatKey", params);
	}
	public List<GlobalType> getByCatKey(String catKey, String ecode){
		return getByCatKey(catKey,ecode,null);
	}

	/**
	 * 根据nodekey获取字典的分类类型。
	 * @param nodeKey 分类表的nodekey。
	 * @return
	 */
	public GlobalType getByDictNodeKey(String nodeKey){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nodeKey", nodeKey);
		params.put("enterprise_code", CookieUitl.getCurrentEnterpriseCode());
		GlobalType globalType=   this.getUnique("getByDictNodeKey", params);
		return globalType;
	}
	/**
	 * 根据nodekey,ecodes获取字典的分类类型。
	 * @param nodeKey 分类表的nodekey。
	 * @return
	 */
	public GlobalType getByDictNodeKeyAndEid(String nodeKey, String eid) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nodeKey", nodeKey+"%");
		params.put("enterprise_code", eid);
		List<GlobalType> list = this.getBySqlKey("getByDictNodeKeyAndEid", params);
		GlobalType globalType=new GlobalType();
		if(list.size()>0){
		 globalType = list.get(0);
		}
		return globalType;
	}
	/**
	 * 根据nodekey获取字典的分类类型。
	 * @param nodeKey 分类表的nodekey。
	 * @return
	 */
	public GlobalType getByCateKeyAndNodeKey(String catKey,String nodeKey){
		Map params=new HashMap();
		params.put("catKey", catKey);
		params.put("nodeKey", nodeKey);
		GlobalType globalType=  this.getUnique("getByCateKeyAndNodeKey", params);
		return globalType;
	}
	
	/** 
	 * 根据catKey和nodeKey获取指定企业下的分类信息
	 * @param catKey
	 * @param nodeKey
	 * @param enterpriseCode
	 * @return
	 */
	public GlobalType getByCateKeyAndNodeKey(String catKey,String nodeKey, String enterpriseCode){
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("catKey", catKey);
		params.put("nodeKey", nodeKey);
		params.put("enterpriseCode", enterpriseCode);
		GlobalType globalType=  this.getUnique("getByCateKeyAndNodeKey", params);
		return globalType;
	}
	
	/**
	 * 取得个人的分类类型。
	 * @param catKey	分类ID
	 * @param userId	用户ID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<GlobalType> getPersonType(String catKey,Long userId){
		@SuppressWarnings("rawtypes")
		Map params=new Hashtable();
		params.put("catkey", catKey);
		params.put("userId", userId);
		List<GlobalType> list=this.getBySqlKey("getPersonType", params);
		return list;
	}
	

	/**
	 * 按用户取得表单分类列表
	 * @param catKey 分类key
	 * @param userId 用户ID
	 * @param roleIds 角色IDS 格式如 1,2
	 * @param orgIds 组织IDs 格式如 1,2
	 * @return
	 */
	public List<GlobalType> getByFormRights(String catKey,Long userId,String roleIds,String orgIds){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("ownerId", userId);
		params.put("catKey", catKey);
		if(StringUtils.isNotEmpty(roleIds)){
			params.put("roleIds", roleIds);
		}
		if(StringUtils.isNotEmpty(orgIds)){
		params.put("orgIds",orgIds);
		}
		return getBySqlKey("getByFormRights",params);
	}



	public GlobalType getByNodeKey(String nodeKey) {
		List<GlobalType> list =	getBySqlKey("getByNodeKey", nodeKey);
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}	
	}


	/**
	 * 取得个人的分类类型。
	 * @param catKey	分类ID
	 * @return
	 */

	public List<GlobalType> selectByCatekey(String catKey){
		List<GlobalType> list=this.getBySqlKey("selectByCatekey", catKey);
		return list;
	}

	/**
	 * 更新路径及企业信息
	 * @param type
	 */
	public void updateTypeInfo(GlobalType type){
		this.update("updateTypeInfo",type);
	}

	/**
	 * 根据企业编码查询总分类ID列表
	 * @param ecodes 企业编码
	 * @return 返回总分类ID列表
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getTypeIdsByEcodes(Set<String> ecodes){
		if(ecodes == null || ecodes.isEmpty()){
			return new ArrayList<Long>();
		}
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("ecodes", ecodes);
		return this.getBySqlKeyGenericity("getTypeIdsByEcodes",params);
	}
	public List<Long> getTypeIdsByEcode(String ecode){
		if(StringUtil.isEmpty(ecode)){
			return new ArrayList<Long>();
		}
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("ecode", ecode);
		return this.getBySqlKeyGenericity("getTypeIdsByEcode",params);
	}

}