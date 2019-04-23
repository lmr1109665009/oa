package com.suneee.platform.dao.form;

import com.suneee.core.db.BaseDao;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.model.form.SubTableSort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:bpm_subtable_sort Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-03-12 13:56:01
 *</pre>
 */
@Repository
public class SubTableSortDao extends BaseDao<SubTableSort>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SubTableSort.class;
	}

	public List<SubTableSort> getByActDefKeyAndTableName(String actDefKey,
			List<String> tableName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("actDefKey", actDefKey);
		params.put("tableNameList", tableName);
		return this.getBySqlKey("getByActDefKeyAndTableName", params);
	}

	public void delByTableNameAndActKey(List<String> tableNameList,
			String actDefKey) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("actDefKey", actDefKey);
		params.put("tableNameList", tableNameList);
		this.delBySqlKey("delByTableNameAndActKey", params);
	}

	public SubTableSort getByAKeyAndTName(String actDefKey, String tableName) {
		List<String> tableNameList = new ArrayList<String>();
		tableNameList.add(tableName);
		List<SubTableSort> list=this.getByActDefKeyAndTableName(actDefKey, tableNameList);
		if(BeanUtils.isEmpty(list)){
			return null;
		}
		
		
		return list.get(0);
	}
	
	
	public void delByActDefKey(String actDefKey) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("actDefKey", actDefKey);
		this.delBySqlKey("delByActDefKey", params);
	}

	
	
	
	
}