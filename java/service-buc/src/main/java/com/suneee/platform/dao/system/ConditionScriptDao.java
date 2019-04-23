package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.ConditionScript;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 *<pre>
 * 对象功能:系统条件脚本 Dao类
 * 开发公司:hotent
 * 开发人员:heyifan
 * 创建时间:2013-04-05 11:34:56
 *</pre>
 */
@Repository
public class ConditionScriptDao extends BaseDao<ConditionScript>
{
	@Override
	public Class<?> getEntityClass()
	{
		return ConditionScript.class;
	}

	public List<ConditionScript> getConditionScript(){
		return this.getBySqlKey("getConditionScript");
	}
	/**
	 * 根据方法名获取条件脚本
	 * @param methodName
	 * @return
	 */
	public ConditionScript getByMethodName(String methodName)
	{
		return this.getUnique("getByMethodName", methodName);
	}
}