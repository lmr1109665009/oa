package com.suneee.platform.dao.system;

import java.util.List;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.PersonScript;
/**
 *<pre>
 * 对象功能:系统条件脚本 Dao类
 * 开发公司:hotent
 * 开发人员:heyifan
 * 创建时间:2013-04-05 11:34:56
 *</pre>
 */
@Repository
public class PersonScriptDao extends BaseDao<PersonScript>
{
	@Override
	public Class<?> getEntityClass()
	{
		return PersonScript.class;
	}

	public List<PersonScript> getPersonScript(){
		return this.getBySqlKey("getPersonScript");
	}
	
	/**
	 * 根据方法名获取人员脚本
	 * @param methodName
	 * @return
	 */
	public PersonScript getByMethodName(String methodName)
	{
		return this.getUnique("getByMethodName", methodName);
	}
}