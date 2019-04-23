package com.suneee.oa.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskExtDao extends BaseDao<TaskEntity> {
    /**
     * 获取我的流程任务数量
     * @return
     */
    public List<?> getFlowTaskCount(QueryFilter filter)
    {
        return getBySqlKey("getFlowTaskCount", filter);
    }

    @Override
    public Class<TaskEntity> getEntityClass()
    {
        return TaskEntity.class;
    }

    @Override
    public String getIbatisMapperNamespace()
    {
        return "com.suneee.core.bpm.model.ProcessTask";
    }
}
