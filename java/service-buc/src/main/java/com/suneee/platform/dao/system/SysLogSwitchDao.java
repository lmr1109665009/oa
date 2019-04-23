package com.suneee.platform.dao.system;

import java.util.List;

import com.suneee.core.db.BaseDao;
import com.suneee.core.util.BeanUtils;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.model.system.SysLogSwitch;
/**
 *<pre>
 * 对象功能:日志开关 Dao类
 * 开发公司:广州宏天
 * 开发人员:Raise
 * 创建时间:2013-06-24 11:12:26
 *</pre>
 */
@Repository
public class SysLogSwitchDao extends BaseDao<SysLogSwitch>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysLogSwitch.class;
	}

	public SysLogSwitch getByModel(String model) {
		List<SysLogSwitch> switchs = this.getBySqlKey("getByModel", model);
		if(BeanUtils.isNotEmpty(switchs)){
			return switchs.get(0);
		}
		return null;
	}

}