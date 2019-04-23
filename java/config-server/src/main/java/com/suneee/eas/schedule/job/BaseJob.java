package com.suneee.eas.schedule.job;

import com.suneee.eas.schedule.model.JobLog;
import com.suneee.eas.schedule.service.JobLogService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 定时任务基层类
 * @user 子华
 * @created 2018/8/30
 */
public abstract class BaseJob extends QuartzJobBean {
    private static Logger log= LogManager.getLogger(BaseJob.class);
    @Autowired
    private JobLogService jobLogService;
    /**
     * 需要执行任务操作方法体
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    protected abstract void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String jobName=context.getJobDetail().getKey().getName();
        String group=context.getJobDetail().getKey().getGroup();
        String triggerName="directExec";
        Trigger trigger=context.getTrigger();
        if(trigger!=null)
            triggerName=trigger.getKey().getName();
        if(triggerName.contains("MT")){
            triggerName="手动执行";
        }
        Date startTime=new Date();
        try
        {
            executeJob(context);
            Date endTime=new Date();
            //记录日志
            long runtime=(endTime.getTime()-startTime.getTime()) /1000;
            addLog(jobName,triggerName,group,startTime, endTime, runtime, "任务执行成功!", JobLog.STATE_SUCCESS);
        }
        catch(Exception e) {
            Date endTime=new Date();
            long runtime=(endTime.getTime()-startTime.getTime()) /1000;
            addLog(jobName, triggerName,group, startTime, endTime, runtime,e.toString(),JobLog.STATE_FAIL);
            e.printStackTrace();
            log.error("执行任务出错:" + e.getMessage());
        }
    }

    /**
     * 记录日志
     * @param jobName
     * @param triggerName
     * @param group
     * @param startTime
     * @param endTime
     * @param runtime
     * @param content
     * @param state
     */
    private void addLog(String jobName, String triggerName,String group, Date startTime, Date endTime, long runtime, String content, int state) {
        JobLog jobLog=new JobLog();
        jobLog.setJobName(jobName);
        jobLog.setTriggerName(triggerName);
        jobLog.setGroup(group);
        jobLog.setStartTime(startTime);
        jobLog.setEndTime(endTime);
        jobLog.setRuntime(runtime);
        jobLog.setContent(content);
        jobLog.setState(state);
        jobLogService.save(jobLog);
    }


    /**
     * 获取定时任务参数
     * @param context
     * @return
     */
    protected Map<String,Object> getJobParams(JobExecutionContext context){
        Map<String,Object> params=new HashMap<>();
        JobDataMap jobDataMap =context.getJobDetail().getJobDataMap();
        for (String key:jobDataMap.keySet()){
            parseParam(params,key,jobDataMap.get(key));
        }
        return params;
    }

    /**
     * 解析参数
     * @param params
     * @param key
     * @param val
     */
    private void parseParam(Map<String,Object> params,String key,Object val){
        if (!key.contains(",")){
            params.put(key,val);
            return;
        }
        String[] keyObj=key.split(",");
        if ("int".equalsIgnoreCase(keyObj[1])){
            if (val==null){
                params.put(keyObj[0],0);
            }else {
                params.put(keyObj[0],Integer.parseInt(val.toString()));
            }
        }else if ("long".equalsIgnoreCase(keyObj[1])){
            if (val==null){
                params.put(keyObj[0],0L);
            }else {
                params.put(keyObj[0],Long.parseLong(val.toString()));
            }
        }else if ("float".equalsIgnoreCase(keyObj[1])){
            if (val==null){
                params.put(keyObj[0],0.0f);
            }else {
                params.put(keyObj[0],Float.parseFloat(val.toString()));
            }
        }else if ("boolean".equalsIgnoreCase(keyObj[1])){
            if (val==null){
                params.put(keyObj[0],false);
            }else {
                params.put(keyObj[0],Boolean.parseBoolean(val.toString()));
            }
        }else {
            if (val==null){
                params.put(keyObj[0],"");
            }else {
                params.put(keyObj[0],val.toString());
            }
        }
    }

}
