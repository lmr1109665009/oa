package com.suneee.kaoqin.dao.kaoqin;

import java.util.List;

import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.model.kaoqin.GoOutApply;
/**
 *<pre>
 * 对象功能:外出申请 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 16:30:42
 *</pre>
 */
@Repository
public class GoOutApplyDao extends BaseDao<GoOutApply>
{
	@Override
	public Class<?> getEntityClass()
	{
		return GoOutApply.class;
	}

	/**
	 * 获取运行中以及结束的流程数据
	 * @param filter
	 * @return
	 */
	public List<GoOutApply> getAllApply(QueryFilter filter){
		return this.getBySqlKey("getAllApply", filter);
	}
	
}