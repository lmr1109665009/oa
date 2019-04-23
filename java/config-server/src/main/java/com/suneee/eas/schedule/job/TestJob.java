package com.suneee.eas.schedule.job;

import com.suneee.eas.common.uploader.UploaderHandler;
import com.suneee.eas.common.utils.DateUtil;
import com.suneee.eas.common.utils.JsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

/**
 * @user 子华
 * @created 2018/8/27
 */
public class TestJob extends BaseJob {
    private static Logger log= LogManager.getLogger(TestJob.class);
    private static int i=1;
    @Autowired
    private UploaderHandler uploaderHandler;

    @Override
    protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.debug("正在执行定时任务"+i+",执行时间："+ DateUtil.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
        log.debug("看看上传组件是否被注入进来？"+uploaderHandler);
        log.debug("定时任务参数："+ JsonUtil.toJson(jobExecutionContext.getJobDetail().getJobDataMap()));
        i++;
    }
}
