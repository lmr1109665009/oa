package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.MonGroupItem;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 *<pre>
 * 对象功能:监控流程项目 Dao类
 * 开发公司:广州宏天软件
 * 开发人员:zyp
 * 创建时间:2013-06-08 11:14:50
 *</pre>
 */
@Repository
public class MonGroupItemDao extends BaseDao<MonGroupItem>
{
	@Override
	public Class<?> getEntityClass()
	{
		return MonGroupItem.class;
	}

	public List<MonGroupItem> getByMainId(Long groupid) {
		return this.getBySqlKey("getMonGroupItemList", groupid);
	}
	
	public void delByMainId(Long groupid) {
		this.delBySqlKey("delByMainId", groupid);
	}
}