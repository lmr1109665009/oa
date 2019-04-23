package com.suneee.eas.schedule.service.impl;

import com.suneee.eas.common.component.schedule.ParameterObj;
import com.suneee.eas.common.component.schedule.PlanObject;
import com.suneee.eas.common.utils.ContextUtil;
import com.suneee.eas.common.utils.DateUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.common.utils.StringUtil;
import com.suneee.eas.schedule.service.ScheduleService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;

/**
 * 定时任务service
 * @user 子华
 * @created 2018/8/27
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {
    private static Logger log= LogManager.getLogger(ScheduleServiceImpl.class);
    @Autowired
    private Scheduler scheduler;

    private static HashMap<String, String> mapWeek;

    static{
        mapWeek=new HashMap<String, String>();
        mapWeek.put("MON", "星期一");
        mapWeek.put("TUE", "星期二");
        mapWeek.put("WED", "星期三");
        mapWeek.put("THU", "星期四");
        mapWeek.put("FRI", "星期五");
        mapWeek.put("SAT", "星期六");
        mapWeek.put("SUN", "星期日");
    }

    /**
     * 添加任务
     * @param jobName 任务名称
     * @param cls 类名称
     * @param jsonParams 参数
     * @throws SchedulerException
     * @throws ClassNotFoundException
     */
    public void addJob(String jobName,String group,Class<? extends QuartzJobBean> cls,String jsonParams,String description) throws SchedulerException{
        addJob(jobName,group,cls,jsonParams,description,null);
    }
    public void addJob(String jobName,String group,Class<? extends QuartzJobBean> cls,String jsonParams,String description,String cron) throws SchedulerException{
        JobBuilder jb =JobBuilder.newJob(cls);
        jb.withIdentity(jobName, group);
        setRuntimeEnv(jb);
        if(StringUtils.isNotEmpty(jsonParams)){
            setJobMap(jsonParams,jb);
        }
        jb.storeDurably();
        if (StringUtils.isNotEmpty(description)){
            jb.withDescription(description);
        }
        JobDetail jobDetail= jb.build();
        scheduler.addJob(jobDetail, true);

        if (StringUtils.isEmpty(cron)){
            return;
        }
        JobKey jobKey=JobKey.jobKey(jobName, group);
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, group);
        // 表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        // 根据cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(scheduleBuilder).forJob(jobKey).build();
        scheduler.scheduleJob(trigger);
    }

    /**
     * 设置当前运行环境
     * @param jobBuilder
     */
    private void setRuntimeEnv(JobBuilder jobBuilder){
        String runtimeEnv= ContextUtil.getRuntimeEnv();
        if (StringUtil.isEmpty(runtimeEnv)){
            return;
        }
        jobBuilder.usingJobData("runtimeEnv",runtimeEnv);
    }

    /**
     * 添加定时任务
     * @param jobName
     * @param cls
     * @param params
     * @param desc
     * @return
     */
    public void addJob(String jobName, String group, Class<? extends QuartzJobBean> cls, Map<String,Object> params, String desc) throws SchedulerException {
        addJob(jobName,group,cls,params,desc,null);
    }
    public void addJob(String jobName, String group, Class<? extends QuartzJobBean> cls, Map<String,Object> params, String desc,String cron) throws SchedulerException {
        boolean isJobExist=isJobExists(jobName,group);
        if(isJobExist){
            throw new SchedulerException("定时任务已存在");
        }
        JobBuilder jb =JobBuilder.newJob(cls);
        jb.withIdentity(jobName, group);
        if(params!=null){
            JobDataMap dataMap=new JobDataMap();
            dataMap.putAll(params);
            jb.usingJobData(dataMap);
        }
        jb.storeDurably();
        if (StringUtils.isNotEmpty(desc)){
            jb.withDescription(desc);
        }
        JobDetail jobDetail= jb.build();
        scheduler.addJob(jobDetail, true);

        if (StringUtils.isEmpty(cron)){
            return;
        }
        JobKey jobKey=JobKey.jobKey(jobName, group);
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, group);
        // 表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        // 根据cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(scheduleBuilder).forJob(jobKey).build();
        scheduler.scheduleJob(trigger);
    }


    /**
     * 取得任务是否存在
     * @param jobName
     * @param group
     * @return
     * @throws SchedulerException
     */
    public boolean isJobExists(String jobName,String group) throws SchedulerException{
        return scheduler.checkExists(JobKey.jobKey(jobName,group));
    }
    /**
     * 根据任务名称取得任务
     * @param jobName
     * @param group
     * @return
     * @throws SchedulerException
     */
    public JobDetail getJobDetailByName(String jobName,String group) throws SchedulerException{
        if(scheduler==null ){
            return null;
        }
        JobKey key=JobKey.jobKey(jobName, group);
        JobDetail detail= scheduler.getJobDetail(key);
        return detail;
    }

    /**
     * 取得任务列表
     * @param group
     * @return
     * @throws SchedulerException
     */
    public List<JobDetail> getJobList(String group) throws SchedulerException{
        List<JobDetail> list=new ArrayList<JobDetail>();
        GroupMatcher<JobKey> matcher=GroupMatcher.groupEquals(group);
        Set<JobKey> set= scheduler.getJobKeys(matcher);
        for(JobKey jobKey : set) {
            JobDetail detail= scheduler.getJobDetail(jobKey);
            list.add(detail);
        }
        return list;
    }

    /**
     * 根据任务名称获取触发器
     * @param jobName
     * @return
     * @throws SchedulerException
     */
    public List<Trigger> getTriggersByJob(String jobName,String group) throws SchedulerException  {
        if(scheduler==null ){
            return new ArrayList<Trigger>();
        }
        JobKey key=JobKey.jobKey(jobName, group);
        return (List<Trigger>) scheduler.getTriggersOfJob(key);
    }

    /**
     * 取得触发器的状态
     * @param list
     * @return
     * @throws SchedulerException
     */
    public HashMap<String, Trigger.TriggerState> getTriggerStatus( List<Trigger> list) throws SchedulerException{
        HashMap<String, Trigger.TriggerState> map=new HashMap<String, Trigger.TriggerState>();
        for(Iterator<Trigger> it=list.iterator();it.hasNext();){
            Trigger trigger=it.next();
            TriggerKey key=trigger.getKey();
            Trigger.TriggerState state= scheduler.getTriggerState(key);
            map.put(key.getName(), state);
        }
        return map;
    }

    /**
     * 判断计划是否存在
     * @param triggerName 触发器名称
     * @return
     * @throws SchedulerException
     */
    public boolean isTriggerExists(String triggerName,String group) throws SchedulerException{
        TriggerKey triggerKey=TriggerKey.triggerKey(triggerName, group);
        return scheduler.checkExists(triggerKey);
    }

    /**
     * 添加计划
     * @param jobName 任务名
     * @param triggerName 计划名
     * @param planJson
     * @throws SchedulerException
     * @throws ParseException
     */
    public void addTrigger(String jobName,String group,String triggerName, String planJson) throws SchedulerException, ParseException{
        JobKey jobKey=JobKey.jobKey(jobName, group);
        TriggerBuilder<Trigger> tb=  TriggerBuilder.newTrigger();
        tb.withIdentity(triggerName,group);
        setTriggerBuilder(planJson,tb);
        tb.forJob(jobKey);
        Trigger trig=tb.build();
        scheduler.scheduleJob(trig);
    }

    /**
     * 更新触发器
     * @param jobName
     * @param group
     * @param triggerName
     * @param planJson
     * @throws SchedulerException
     * @throws ParseException
     */
    public void updateTrigger(String jobName,String group,String triggerName, String planJson) throws SchedulerException, ParseException{
        JobKey jobKey=JobKey.jobKey(jobName, group);
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName,group);
        TriggerBuilder<Trigger> tb=  TriggerBuilder.newTrigger();
        tb.withIdentity(triggerName,group);
        setTriggerBuilder(planJson,tb);
        tb.forJob(jobKey);
        Trigger trig=tb.build();
        scheduler.rescheduleJob(triggerKey,trig);
    }

    /**
     * 添加触发器
     * @param jobName
     * @param triggerName
     * @param minute
     * @throws SchedulerException
     */
    public void addTrigger(String jobName,String group,String triggerName, int minute) throws SchedulerException{
        JobKey jobKey=JobKey.jobKey(jobName, group);
        TriggerBuilder<Trigger> tb=  TriggerBuilder.newTrigger();
        tb.withIdentity(triggerName,group);
        ScheduleBuilder sb= CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withIntervalInMinutes(minute);
        tb.startNow();
        tb.withSchedule(sb);
        tb.withDescription("每:" + minute +"分钟执行!");

        tb.forJob(jobKey);
        Trigger trig=tb.build();
        scheduler.scheduleJob(trig);
    }

    public void updateJobDetailTrigger(JobDetail jobDetail, Set<Trigger> triggersForJob, boolean replace)throws SchedulerException{
        if(jobDetail==null||triggersForJob.size()==0){
            return;
        }
        scheduler.scheduleJob(jobDetail,triggersForJob,true);
    }

    /**
     * 更新定时任务
     * @param jobName
     * @param group
     * @param desc
     * @param jsonParams
     * @throws SchedulerException
     */
    @Override
    public void updateJob(String jobName, String group, String desc, String jsonParams) throws SchedulerException {
        JobDetail jobDetail=scheduler.getJobDetail(JobKey.jobKey(jobName,group));
        List<? extends Trigger> triggerList=scheduler.getTriggersOfJob(JobKey.jobKey(jobName,group));
        if (StringUtils.isNotEmpty(desc)){
            jobDetail=jobDetail.getJobBuilder().withDescription(desc).build();
        }
        setJobDetailMap(jsonParams,jobDetail);
        Set<Trigger> triggerSet=new TreeSet<>();
        triggerSet.addAll(triggerList);
        scheduler.scheduleJob(jobDetail,triggerSet,true);
    }

    /**
     * 触发器builder
     * @param planJson
     * @param tb
     * @throws ParseException
     */
    private void setTriggerBuilder(String planJson,TriggerBuilder<Trigger> tb) throws ParseException{
        JSONObject jsonObject= JSONObject.fromObject(planJson);

        PlanObject planObject=(PlanObject)JSONObject.toBean(jsonObject, PlanObject.class) ;
        int type=planObject.getType();
        String value=planObject.getTimeInterval();
        switch(type){
            //启动一次
            case 1:
                Date date= DateUtil.getDate(value,DateUtil.FORMAT_DATETIME);
                tb.startAt(date);
                tb.withDescription("执行一次,执行时间:" + DateUtil.formatDate(date,DateUtil.FORMAT_DATETIME));
                break;
            //每分钟执行
            case 2:
                int minute=Integer.parseInt(value);
                ScheduleBuilder sb= CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withIntervalInMinutes(minute);

                tb.startNow();
                tb.withSchedule(sb);
                tb.withDescription("每:" + minute +"分钟执行!");
                break;
            //每天时间点执行
            case 3:
                String[] aryTime=value.split(":");
                int hour=Integer.parseInt(aryTime[0]);
                int m=Integer.parseInt(aryTime[1]);
                ScheduleBuilder sb1=CronScheduleBuilder.dailyAtHourAndMinute(hour, m);
                tb.startNow();
                tb.withSchedule(sb1);
                tb.withDescription("每天：" + hour +":" + m +"执行!");
                break;
            //每周时间点执行
            case 4:
                //0 15 10 ? * MON-FRI Fire at 10:15am every Monday, Tuesday, Wednesday, Thursday and Friday
                String[] aryExpression=value.split("[|]");
                String week=aryExpression[0];
                String[] aryTime1=aryExpression[1].split(":");
                String h1= aryTime1[0];
                String m1= aryTime1[1];
                String cronExperssion="0 "+ m1 +" " + h1 +" ? * " + week;
                ScheduleBuilder sb4=CronScheduleBuilder.cronSchedule(cronExperssion);
                tb.startNow();
                tb.withSchedule(sb4);
                String weekName=getWeek(week);
                tb.withDescription("每周：" + weekName +"," +h1 +":"+ m1 +"执行!");
                break;
            //每月执行
            case 5:
                //0 15 10 15 * ?
                String[] aryExpression5=value.split("[|]");
                String day=aryExpression5[0];
                String[] aryTime2=aryExpression5[1].split(":");
                String h2= aryTime2[0];
                String m2= aryTime2[1];
                String cronExperssion1="0 "+ m2 +" " + h2 +" "+day+" * ?" ;
                ScheduleBuilder sb5=CronScheduleBuilder.cronSchedule(cronExperssion1);
                tb.startNow();
                tb.withSchedule(sb5);
                String dayName=getDay(day);
                tb.withDescription("每月:" + dayName +","   +h2 +":"+ m2 +"执行!");
                break;
            //表达式
            case 6:
                value = jsonObject.get("txtCronExpression").toString();
                ScheduleBuilder sb6=CronScheduleBuilder.cronSchedule(value);
                tb.startNow();
                tb.withSchedule(sb6);
                tb.withDescription("CronTrigger表达式:" + value);
                break;
        }
    }

    private String getDay(String day){
        String[] aryDay=day.split(",");
        int len=aryDay.length;
        String str="";
        for(int i=0;i<len;i++){
            String tmp=aryDay[i];
            tmp=tmp.equals("L")?"最后一天":tmp;
            if(i<len-1){
                str+=tmp +",";
            }
            else{
                str+=tmp;
            }
        }
        return str;
    }

    /**
     * 取得星期名称
     * @param week
     * @return
     */
    private String getWeek(String week){
        String[] aryWeek=week.split(",");
        int len=aryWeek.length;
        String str="";
        for(int i=0;i<len;i++){
            if(i<len-1)
                str+=mapWeek.get(aryWeek[i]) +",";
            else
                str+=mapWeek.get(aryWeek[i]);
        }
        return str;
    }



    /**
     * 设置任务参数
     * @param json
     * @param jb
     */
    private void setJobMap(String json,JobBuilder jb){
        JSONArray aryJson= JSONArray.fromObject(json);
        ParameterObj[] list=(ParameterObj[])aryJson.toArray(aryJson, ParameterObj.class);
        for(int i=0;i<list.length;i++){
            ParameterObj obj=(ParameterObj)list[i];
            String type=obj.getType();
            String name=obj.getName();
            String value=obj.getValue();
            if(type.equals("int")){
                if(StringUtils.isEmpty(value)){
                    jb.usingJobData(name+",int", 0);
                }else{
                    jb.usingJobData(name+",int", Integer.parseInt(value));
                }
            }
            else if(type.equals("long")){
                if(StringUtils.isEmpty(value)){
                    jb.usingJobData(name+",long", 0);
                }else{
                    jb.usingJobData(name+",long", Long.parseLong(value));
                }
            }
            else if(type.equals("float")){
                if(StringUtils.isEmpty(value)){
                    jb.usingJobData(name+",float", 0.0);
                }else{
                    jb.usingJobData(name+",float", Float.parseFloat(value));
                }
            }
            else if(type.equals("boolean")){
                if(StringUtils.isEmpty(value)){
                    jb.usingJobData(name+",boolean", false);
                }else{
                    jb.usingJobData(name+",boolean", Boolean.parseBoolean(value));
                }
            }
            else{
                jb.usingJobData(name+",String",value);
            }
        }
    }

    /**
     * 设置jobDetail参数
     * @param json
     * @param jobDetail
     */
    private void setJobDetailMap(String json,JobDetail jobDetail){
        JobDataMap dataMap=jobDetail.getJobDataMap();
        dataMap.clear();
        if (StringUtils.isEmpty(json)){
            return;
        }
        JSONArray aryJson= JSONArray.fromObject(json);
        ParameterObj[] list=(ParameterObj[])aryJson.toArray(aryJson, ParameterObj.class);
        for(int i=0;i<list.length;i++){
            ParameterObj obj=(ParameterObj)list[i];
            String type=obj.getType();
            String name=obj.getName();
            String value=obj.getValue();
            if(type.equals("int")){
                if(StringUtils.isEmpty(value)){
                    dataMap.put(name+",int", 0);
                }else{
                    dataMap.put(name+",int", Integer.parseInt(value));
                }
            }
            else if(type.equals("long")){
                if(StringUtils.isEmpty(value)){
                    dataMap.put(name+",long", 0);
                }else{
                    dataMap.put(name+",long", Long.parseLong(value));
                }
            }
            else if(type.equals("float")){
                if(StringUtils.isEmpty(value)){
                    dataMap.put(name+",float", 0.0);
                }else{
                    dataMap.put(name+",float", Float.parseFloat(value));
                }
            }
            else if(type.equals("boolean")){
                if(StringUtils.isEmpty(value)){
                    dataMap.put(name+",boolean", false);
                }else{
                    dataMap.put(name+",boolean", Boolean.parseBoolean(value));
                }
            }
            else{
                dataMap.put(name+",String", value);
            }
        }
    }

    /**
     * 删除任务
     * @param jobName
     * @throws SchedulerException
     * @throws ClassNotFoundException
     */
    public void delJob(String jobName,String group) throws SchedulerException {
        JobKey key=JobKey.jobKey(jobName,group);
        scheduler.deleteJob(key);
    }

    public Trigger getTrigger(String triggerName,String group) throws SchedulerException{
        TriggerKey key=TriggerKey.triggerKey(triggerName, group);
        Trigger trigger= scheduler.getTrigger(key);
        return trigger;
    }

    /**
     * 删除计划
     * @param triggerName
     * @throws SchedulerException
     * @throws ClassNotFoundException
     */
    public void delTrigger(String triggerName,String group) throws SchedulerException {
        TriggerKey key=TriggerKey.triggerKey(triggerName, group);
        scheduler.unscheduleJob(key);
    }

    /**
     * 停止或暂停触发器
     * @param triggerName
     * @throws SchedulerException
     */
    public void toggleTriggerRun(String triggerName,String group) throws SchedulerException {
        TriggerKey key=TriggerKey.triggerKey(triggerName, group);
        Trigger.TriggerState state= scheduler.getTriggerState(key);
        if(state== Trigger.TriggerState.PAUSED){
            scheduler.resumeTrigger(key);
        }
        else if(state== Trigger.TriggerState.NORMAL){
            scheduler.pauseTrigger(key);
        }
    }

    /**
     * 直接执行任务
     * @param jobName
     * @param group
     * @throws SchedulerException
     */
    public void executeJob(String jobName,String group) throws SchedulerException{
        JobKey key=JobKey.jobKey(jobName, group);
        scheduler.triggerJob(key);
    }

    /**
     * 启动
     * @throws SchedulerException
     */
    public void start() throws SchedulerException{
        scheduler.start();
    }

    /**
     * 关闭
     * @throws SchedulerException
     */
    public void shutdown() throws SchedulerException{
        scheduler.standby();
    }

    /**
     * 是否启动
     * @return
     * @throws SchedulerException
     */
    public boolean isStarted() throws SchedulerException{
        return scheduler.isStarted();
    }
    /**
     * 是否挂起
     * @return
     * @throws SchedulerException
     */
    public boolean isInStandbyMode() throws SchedulerException{
        return scheduler.isInStandbyMode();
    }
}
