package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysHistoryData;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 *<pre>
 * 对象功能:历史数据 Dao类
 * 开发公司:宏天软件
 * 开发人员:ray
 * 创建时间:2015-06-26 22:47:29
 *</pre>
 */
@Repository
public class SysHistoryDataDao extends BaseDao<SysHistoryData>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysHistoryData.class;
	}

	/**
	 * 根据管理ID获取数据
	 * @param relateId
	 * @return
	 */
	public List<SysHistoryData> getByObjId(Long relateId){
		return this.getBySqlKey("getByObjId", relateId);
	}
	
	
	
}