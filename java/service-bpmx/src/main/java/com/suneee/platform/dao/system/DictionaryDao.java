/**
 * 对象功能:数据字典 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ljf
 * 创建时间:2011-11-23 11:07:27
 */
package com.suneee.platform.dao.system;


import com.suneee.core.db.BaseDao;
import com.suneee.core.table.SqlTypeConst;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.system.Dictionary;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DictionaryDao extends BaseDao<Dictionary>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return Dictionary.class;
	}
	
	/**
	 * 根据类型获取数据字典。
	 * @param typeId
	 * @return
	 */
	public List<Dictionary> getByTypeId(long typeId){	
		return this.getBySqlKey("getByTypeId", typeId);
	}
	public List<Dictionary> getByTypeIdAndEid(long typeId,String eid){
		Map<String, Object> params=new HashMap<>();
		params.put("typeId", typeId);
		params.put("eid", eid);
		return this.getBySqlKey("getByTypeIdAndEid", params);
	}


	public List<Dictionary> getByTypeIdAndItemName(long typeId,String itemName){
		Map<String, Object> params=new HashMap<>();
		params.put("typeId", typeId);
		params.put("itemName", itemName);
		return this.getBySqlKey("getByTypeIdAndItemName", params);
	}




	/**
	 * 根据节点路径查询其下的字典数据。
	 * @param nodePath		节点路径
	 * @return
	 */
	public List<Dictionary> getByNodePath(String nodePath){
		Map<String,String> params=new HashMap<String, String>();
		params.put("nodePath", nodePath+"%");
		return this.getBySqlKey("getByNodePath", params);
	}
	
	/**
	 * 根据分类id删除数据字典。
	 * @param typeId
	 */
	public void delByTypeId(Long typeId){
		  this.delBySqlKey("delByTypeId", typeId);
	}
	
	/**
	 * 根据分类ID和字典关键字判断是否关键字已经存在。
	 * @param typeId
	 * @param itemKey
	 */
	public boolean isItemKeyExists(long typeId,String itemKey,String itemValue){
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("typeId", typeId);
		params.put("itemKey", itemKey);
		params.put("itemValue", itemValue);
		int count=(Integer) this.getOne("isItemKeyExists", params);
		return count>0;
	}
	
	/**
	 * 更新是判断字典关键字是否已经存在。
	 * @param dicId		字典ID
	 * @param typeId	分类ID
	 * @param itemKey	字典key值。
	 * @return
	 */
	public boolean isItemKeyExistsForUpdate(long dicId, long typeId,String itemKey){
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("dicId", dicId);
		params.put("typeId", typeId);
		params.put("itemKey", itemKey);
		int count=(Integer) this.getOne("isItemKeyExistsForUpdate", params);
		return count>0;
	}

	/**
	 * 更新是判断字典项值是否已经存在。
	 * @param typeId	分类ID
	 * @param itemValue	字典项值。
	 * @return
	 */
	public boolean isItemValueExistsForUpdate(long typeId,long dicId,String itemValue){
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("typeId", typeId);
		params.put("dicId", dicId);
		params.put("itemValue", itemValue);
		int count=(Integer) this.getOne("isItemValueExistsForUpdate", params);
		return count>0;
	}
	
	/**
	 * 更新字典排序
	 * @param dicId
	 * @param sn
	 */
	public void updSn(Long dicId,Integer sn){
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("dicId", dicId);
		params.put("sn", sn);
		this.update("updSn", params);
	}
	
	/**
	 * 根据父节点id 找到字典项列表
	 * @param id
	 * @return
	 */
	public List<Dictionary> getByParentId(long id){
		return this.getBySqlKey("getByParentId",id);
	}




	public List<Dictionary> getByItemValue(String itemValue) {		
		return this.getBySqlKey("getByItemValue", itemValue);
	}
	
	public List<Dictionary> getByTypeAndItemName(Long typeId, String itemName){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("typeId", typeId);
		params.put("itemName", itemName);
		return this.getBySqlKey("getByTypeAndItemName", params);
	}

	public List<Dictionary> getByDictTypeAndKey(Map<String, String> map) {
		return getBySqlKey("getByDictTypeAndKey", map);
	}

	public Dictionary getByTypeIdAndItemValue(Long typeId, String itemValue) {
		Dictionary dictionary = new Dictionary();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("typeId", typeId);
		params.put("itemValue", itemValue);
		List<Dictionary> list = this.getBySqlKey("getByTypeIdAndItemValue",params);
		if(list.size()>0){
			return list.get(0);
		}else{
			return dictionary;
		}
	}

	/**
	 * 对象功能：根据查询条件字典列表
	 */
	public List<Dictionary> getDictionarysByQueryFilter(QueryFilter queryFilter) {
		if (this.getDbType().equals(SqlTypeConst.ORACLE)
				|| this.getDbType().equals(SqlTypeConst.SQLSERVER)
				|| this.getDbType().equals(SqlTypeConst.MYSQL)) {
			return this.getBySqlKey("getDictionarysByQueryFilter",
					queryFilter);
		} else {
			return this.getBySqlKey("getDictionarysByQueryFilter", queryFilter);
		}
	}

}