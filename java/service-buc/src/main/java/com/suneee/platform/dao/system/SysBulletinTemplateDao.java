
package com.suneee.platform.dao.system;

import java.util.List;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;

import com.suneee.core.db.BaseDao;

import com.suneee.platform.model.system.SysBulletinTemplate;

@Repository
public class SysBulletinTemplateDao extends BaseDao<SysBulletinTemplate>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysBulletinTemplate.class;
	}

}