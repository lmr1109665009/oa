package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.BpmCommonWsParams;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 *<pre>
 * 对象功能:通用webservice调用参数 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2013-10-17 10:09:20
 *</pre>
 */
@Repository
public class BpmCommonWsParamsDao extends BaseDao<BpmCommonWsParams>
{
	@Override
	public Class<?> getEntityClass()
	{
		return BpmCommonWsParams.class;
	}

	public List<BpmCommonWsParams> getByMainId(Long setid) {
		return this.getBySqlKey("getBpmCommonWsParamsList", setid);
	}
	
	public void delByMainId(Long setid) {
		this.delBySqlKey("delByMainId", setid);
		
	}
	
}