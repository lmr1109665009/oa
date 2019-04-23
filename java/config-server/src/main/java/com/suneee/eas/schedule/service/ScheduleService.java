package com.suneee.eas.schedule.service;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 定时任务服务bean
 * @user 子华
 * @created 2018/8/27
 */
public interface ScheduleService {
    /**
     * 添加任务
     * @param jobName 任务名称
     * @param cls 类名称
     * @param jsonParams 参数
     * @throws SchedulerException
     * @throws ClassNotFoundException
     */
    public void addJob(String jobName, String group, Class<? extends QuartzJobBean> cls, String jsonParams, String description) throws SchedulerException;
    public void addJob(String jobName,String group,Class<? extends QuartzJobBean> cls,String jsonParams,String description,String cron) throws SchedulerException;


    /**
     * 添加任务
     * @param jobName
     * @param cls
     * @param params
     * @param desc
     * @return
     */
    public void addJob(String jobName, String group, Class<? extends QuartzJobBean> cls, Map<String,Object> params, String desc) throws SchedulerException;
    public void addJob(String jobName, String group, Class<? extends QuartzJobBean> cls, Map<String,Object> params, String desc,String cron) throws SchedulerException;


    /**
     * 取得任务是否存在
     * @param jobName
     * @param group
     * @return
     * @throws SchedulerException
     */
    public boolean isJobExists(String jobName,String group) throws SchedulerException;
    /**
     * 根据任务名称取得任务
     * @param jobName
     * @param group
     * @return
     * @throws SchedulerException
     */
    public JobDetail getJobDetailByName(String jobName,String group) throws SchedulerException;

    /**
     * 取得任务列表
     * @param group
     * @return
     * @throws SchedulerException
     */
    public List<JobDetail> getJobList(String group) throws SchedulerException;

    /**
     * 根据任务名称获取触发器
     * @param jobName
     * @return
     * @throws SchedulerException
     */
    public List<Trigger> getTriggersByJob(String jobName,String group) throws SchedulerException;

    /**
     * 取得触发器的状态
     * @param list
     * @return
     * @throws SchedulerException
     */
    public HashMap<String, Trigger.TriggerState> getTriggerStatus(List<Trigger> list) throws SchedulerException;

    /**
     * 判断计划是否存在
     * @param triggerName 触发器名称
     * @return
     * @throws SchedulerException
     */
    public boolean isTriggerExists(String triggerName,String group) throws SchedulerException;

    /**
     * 添加计划
     * @param jobName 任务名
     * @param triggerName 计划名
     * @param planJson
     * @throws SchedulerException
     * @throws ParseException
     */
    public void addTrigger(String jobName,String group,String triggerName, String planJson) throws SchedulerException, ParseException;

    /**
     * 更新触发器
     * @param jobName
     * @param group
     * @param triggerName
     * @param planJson
     * @throws SchedulerException
     * @throws ParseException
     */
    public void updateTrigger(String jobName,String group,String triggerName, String planJson) throws SchedulerException, ParseException;

    /**
     * 添加触发器
     * @param jobName
     * @param triggerName
     * @param minute
     * @throws SchedulerException
     */
    public void addTrigger(String jobName,String group,String triggerName, int minute) throws SchedulerException;

    public void updateJobDetailTrigger(JobDetail jobDetail, Set<Trigger> triggersForJob, boolean replace)throws SchedulerException;

    /**
     * 更新定时任务
     * @param jobName
     * @param group
     * @param desc
     * @param jsonParams
     * @throws SchedulerException
     */
    public void updateJob(String jobName,String group,String desc,String jsonParams) throws SchedulerException;

    /**
     * 删除任务
     * @param jobName
     * @throws SchedulerException
     * @throws ClassNotFoundException
     */
    public void delJob(String jobName,String group) throws SchedulerException;

    public Trigger getTrigger(String triggerName,String group) throws SchedulerException;

    /**
     * 删除计划
     * @param triggerName
     * @throws SchedulerException
     * @throws ClassNotFoundException
     */
    public void delTrigger(String triggerName,String group) throws SchedulerException;

    /**
     * 停止或暂停触发器
     * @param triggerName
     * @throws SchedulerException
     */
    public void toggleTriggerRun(String triggerName,String group) throws SchedulerException;

    /**
     * 直接执行任务
     * @param jobName
     * @param group
     * @throws SchedulerException
     */
    public void executeJob(String jobName,String group) throws SchedulerException;

    /**
     * 启动
     * @throws SchedulerException
     */
    public void start() throws SchedulerException;

    /**
     * 关闭
     * @throws SchedulerException
     */
    public void shutdown() throws SchedulerException;

    /**
     * 是否启动
     * @return
     * @throws SchedulerException
     */
    public boolean isStarted() throws SchedulerException;
    /**
     * 是否挂起
     * @return
     * @throws SchedulerException
     */
    public boolean isInStandbyMode() throws SchedulerException;
}
