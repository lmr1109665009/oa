package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysQuerySqlDef;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
/**
 *<pre>
 * 对象功能:自定义SQL定义 Dao类
 * 开发公司:宏天软件
 * 开发人员:ray
 * 创建时间:2015-06-08 16:02:04
 *</pre>
 */
@Repository
public class SysQuerySqlDefDao extends BaseDao<SysQuerySqlDef>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysQuerySqlDef.class;
	}

	/**
	 * 根据别名获取SQL定义。
	 * @param alias
	 * @return
	 */
	public SysQuerySqlDef getByAlias(String alias){
		return this.getUnique("getByAlias", alias);
	}
	
	/**
	 * 判断别名是否存在。
	 * @param alias
	 * @param id
	 * @return
	 */
	public boolean  isAliasExists(String alias,Long id){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("alias", alias);
		params.put("id", id);
		return (Integer) this.getOne("isAliasExists", params)>0;
	}

	/**
	 * 设置分类
	 * @param categoryId
	 * @param aryTableId
	 */
	public void updCategory(Long categoryId, String[] aryId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("categoryId", categoryId);
		map.put("aryId", aryId);
		this.update("updCategory",  map);		
	}
	
	
}