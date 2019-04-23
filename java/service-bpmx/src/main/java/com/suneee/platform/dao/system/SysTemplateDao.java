/**
 * 对象功能:模版管理 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:pkq
 * 创建时间:2011-12-28 14:04:30
 */
package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SysTemplateDao extends BaseDao<SysTemplate>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return SysTemplate.class;
	}
	
	/**
	 * 设置默认模板
	 * @param id
	 */
	public int setDefault(long id){
		return this.update("updateDefault", id);
	}
	
	/**
	 * 设置用途类型的所有模板为非默认状态
	 * @param tempType
	 */
	public int setNotDefaultByUseType(Integer useType){
		return this.update("updateNotDefaultByUseType", useType);
	}
	
	/**
	 * 通过用途类型获取模板
	 * @param useType
	 * @return
	 */
	public SysTemplate getDefaultByUseType(Integer useType){
		long useTypel = (int)useType;
		return this.getUnique("getDefaultByUseType", useTypel);
	}

	public void delByUseType(Integer useType) {
		this.delBySqlKey("delByUseType", useType);
	}
}