package com.suneee.kaoqin.dao.kaoqin;

import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.model.kaoqin.SignCardApply;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 *<pre>
 * 对象功能:签卡申请 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-03 14:37:01
 *</pre>
 */
@Repository
public class SignCardApplyDao extends BaseDao<SignCardApply>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SignCardApply.class;
	}
	
	public List<SignCardApply> getAllApply(QueryFilter queryFilter){
		return this.getBySqlKey("getAllApply", queryFilter);
	}

}