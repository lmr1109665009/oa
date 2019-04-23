package com.suneee.platform.dao.bpm;

import com.suneee.core.db.GenericDao;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Repository;
@Repository
public class ExecutionExtDao extends GenericDao<ExecutionEntity,String>
{
	@Override
	public Class getEntityClass()
	{
		return ExecutionEntity.class;
	}
}
