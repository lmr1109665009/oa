package com.suneee.kaoqin.dao.kaoqin;

import java.util.List;

import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.model.kaoqin.LeaveApply;
/**
 *<pre>
 * 对象功能:请假申请流程 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-11 09:35:17
 *</pre>
 */
@Repository
public class LeaveApplyDao extends BaseDao<LeaveApply>
{
	@Override
	public Class<?> getEntityClass()
	{
		return LeaveApply.class;
	}

	/**
	 * 获取请假流程数据
	 * @param filter
	 * @return
	 */
	public List<LeaveApply> getAllApply(QueryFilter filter){
		return this.getBySqlKey("getAllApply", filter);
	}
	
}