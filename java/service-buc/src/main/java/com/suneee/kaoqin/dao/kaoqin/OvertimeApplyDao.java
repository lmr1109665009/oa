package com.suneee.kaoqin.dao.kaoqin;

import java.util.List;

import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.model.kaoqin.OvertimeApply;
/**
 *<pre>
 * 对象功能:加班申请流程 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-05 10:20:27
 *</pre>
 */
@Repository
public class OvertimeApplyDao extends BaseDao<OvertimeApply>
{
	@Override
	public Class<?> getEntityClass()
	{
		return OvertimeApply.class;
	}

	public List<OvertimeApply> getAllApply(QueryFilter queryFilter){
		return this.getBySqlKey("getAllApply", queryFilter);
	}
	
}