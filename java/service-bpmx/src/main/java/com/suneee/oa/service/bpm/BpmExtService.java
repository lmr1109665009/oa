package com.suneee.oa.service.bpm;

import com.suneee.core.engine.IScript;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.oa.dao.bpm.TaskExtDao;
import com.suneee.oa.model.bpm.TaskAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Activiti流程拓展接口
 * @author 子华
 *
 */
@Service
public class BpmExtService implements IScript {
    private Logger logger= LoggerFactory.getLogger(BpmExtService.class);
    @Resource(name = "taskExtDao")
    private TaskExtDao taskDao;
    /**
     * 获取我的待办流程定义数量统计
     *
     * @param filter
     * @return
     */
    public List<TaskAmount> getFlowTasksCount(QueryFilter filter) {
        return (List<TaskAmount>) taskDao.getFlowTaskCount(filter);
    }
}
