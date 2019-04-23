/**
 * 对象功能:流程分管授权限用户中间表明细 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:xucx
 * 创建时间:2014-03-05 10:10:53
 */
package com.suneee.platform.dao.bpm;


import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.bpm.BpmDefAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BpmDefAuthorizeDao extends BaseDao<BpmDefAuthorize>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return BpmDefAuthorize.class;
	}

	/**
	 * 根据授权名称查询是否存在
	 * @param filter
	 * @return
	 */
	public List<BpmDefAuthorize> getByName(QueryFilter filter) {
		return getBySqlKey("getByName",filter);
	}
}