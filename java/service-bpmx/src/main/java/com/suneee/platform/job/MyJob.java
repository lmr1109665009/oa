package com.suneee.platform.job;

import com.suneee.core.scheduler.BaseJob;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suneee.core.scheduler.BaseJob;

public class MyJob extends BaseJob {
	protected Logger logger = LoggerFactory.getLogger(MyJob.class);
	@Override
	public void executeJob(JobExecutionContext context) throws Exception {
		//com.suneee.platform.job.MyJob
		logger.info("com.suneee.platform.job.MyJob");
		//context.getJobDetail().getJobDataMap()
		
	}


}
