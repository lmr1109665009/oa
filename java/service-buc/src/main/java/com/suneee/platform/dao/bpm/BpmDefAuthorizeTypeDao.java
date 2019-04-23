/**
 * 对象功能:流程分管授权限用户中间表明细 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:xucx
 * 创建时间:2014-03-05 10:10:53
 */
package com.suneee.platform.dao.bpm;


import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.BpmDefAuthorizeType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public class BpmDefAuthorizeTypeDao extends BaseDao<BpmDefAuthorizeType> {

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return BpmDefAuthorizeType.class;
	}

	public List<BpmDefAuthorizeType> getAuthorizeTypeByMap(Map<String, Object> params)
	{
		return getBySqlKey("getAll", params);
	}

	public void delByAuthorizeId(Long authorizeId)
	{
		delBySqlKey("delByAuthorizeId", authorizeId);
	}
	
}