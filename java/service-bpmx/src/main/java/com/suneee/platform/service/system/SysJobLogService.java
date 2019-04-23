package com.suneee.platform.service.system;

import java.util.Date;

import javax.annotation.Resource;

import com.suneee.core.api.log.ISysJobLog;
import com.suneee.core.api.log.ISysJobLogService;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.system.SysJobLogDao;
import com.suneee.platform.model.system.SysJobLog;
import org.springframework.stereotype.Service;

import com.suneee.core.api.log.ISysJobLog;
import com.suneee.core.api.log.ISysJobLogService;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.system.SysJobLogDao;
import com.suneee.platform.model.system.SysJobLog;

/**
 * 对象功能:SYS_JOBLOG Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:phl
 * 创建时间:2011-12-28 17:01:51
 */
@Service
public class SysJobLogService extends BaseService<SysJobLog> implements ISysJobLogService
{
	@Resource
	private SysJobLogDao dao;
	
	public SysJobLogService()
	{
	}
	
	@Override
	protected IEntityDao<SysJobLog, Long> getEntityDao()
	{
		return dao;
	}
	


	@Override
	public void addLog(ISysJobLog jobLog) {
		dao.add((SysJobLog) jobLog);
		
	}

	@Override
	public ISysJobLog getJobLog(String jobName, String trigName,
			Date strStartTime, Date strEndTime, long runTime, String content,
			int state) {
		SysJobLog jobLog=new SysJobLog();
		jobLog.setJobName(jobName);
		jobLog.setTrigName(trigName);
		jobLog.setStartTime(strStartTime);
		jobLog.setEndTime(strEndTime);
		jobLog.setRunTime(runTime);
		jobLog.setContent(content);
		jobLog.setState(state);
		
		return jobLog;
	}
}
