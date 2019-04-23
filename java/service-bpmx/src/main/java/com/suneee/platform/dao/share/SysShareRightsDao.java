
package com.suneee.platform.dao.share;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.share.SysShareRights;
import org.springframework.stereotype.Repository;

@Repository
public class SysShareRightsDao extends BaseDao<SysShareRights>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysShareRights.class;
	}

}