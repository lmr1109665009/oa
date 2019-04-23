package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.model.bpm.BpmCommonWsSet;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 *<pre>
 * 对象功能:通用webservice调用设置 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2013-10-17 10:09:20
 *</pre>
 */
@Repository
public class BpmCommonWsSetDao extends BaseDao<BpmCommonWsSet>
{
	@Override
	public Class<?> getEntityClass()
	{
		return BpmCommonWsSet.class;
	}
	
	public BpmCommonWsSet getByAlias(String alias){
		List<BpmCommonWsSet> list = this.getBySqlKey("getByAlias",alias);
		if(BeanUtils.isNotEmpty(list)){
			return list.get(0);
		}
		return null;
	}

}