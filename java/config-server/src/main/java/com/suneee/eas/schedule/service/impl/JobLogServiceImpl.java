package com.suneee.eas.schedule.service.impl;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.schedule.dao.JobLogDao;
import com.suneee.eas.schedule.model.JobLog;
import com.suneee.eas.schedule.service.JobLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 任务日志service
 * @user 子华
 * @created 2018/8/30
 */
@Service
public class JobLogServiceImpl extends BaseServiceImpl<JobLog> implements JobLogService {
    private JobLogDao logDao;

    @Autowired
    public void setLogDao(JobLogDao logDao) {
        this.logDao = logDao;
        setBaseDao(logDao);
    }

    @Override
    public int save(JobLog model) {
        model.setLogId(IdGeneratorUtil.getNextId());
        return super.save(model);
    }
}
