package com.suneee.kaoqin.dao.kaoqin;

import java.util.List;

import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.model.kaoqin.BusinessTravel;
/**
 *<pre>
 * 对象功能:出差申请 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-04 14:57:21
 *</pre>
 */
@Repository
public class BusinessTravelDao extends BaseDao<BusinessTravel>
{
	@Override
	public Class<?> getEntityClass()
	{
		return BusinessTravel.class;
	}

	public List<BusinessTravel> getAllApply(QueryFilter queryFilter){
		return this.getBySqlKey("getAllApply", queryFilter);
	}
	
	
	
}