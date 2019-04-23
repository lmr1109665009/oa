package com.suneee.eas.schedule.controller;

import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.utils.DateUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.common.utils.StringUtil;
import com.suneee.eas.schedule.model.JobLog;
import com.suneee.eas.schedule.service.JobLogService;
import com.suneee.eas.schedule.service.ScheduleService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;

/**
 * 定时任务调度控制器
 * @user 子华
 * @created 2018/8/29
 */
@RestController
@RequestMapping(ModuleConstant.COMMON_MODULE+"/schedule/")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private JobLogService logService;

    /**
     * 添加定时任务
     * @param request
     * @return
     * @throws ClassNotFoundException
     * @throws SchedulerException
     */
    @RequestMapping("addJob")
    public ResponseMessage addJob(HttpServletRequest request) throws ClassNotFoundException, SchedulerException {
        ResponseMessage message=checkClassExist(request);
        if (message!=null){
            return message;
        }
       String cls=RequestUtil.getString(request,"cls");
       String jobName=RequestUtil.getString(request,"jobName");
       String group=RequestUtil.getString(request,"group");
       String cron=RequestUtil.getString(request,"cron");
       String desc=RequestUtil.getString(request,"desc");
       String params=RequestUtil.getString(request,"params");
       if (StringUtils.isEmpty(jobName)){
           return ResponseMessage.fail("任务名称不允许为空");
       }
       if (StringUtils.isEmpty(group)){
           return ResponseMessage.fail("任务所属分组不允许为空");
       }
       scheduleService.addJob(jobName,group, (Class<? extends QuartzJobBean>) Class.forName(cls),params,desc,cron);
       return ResponseMessage.success("定时任务添加成功");
    }

    /**
     * 更新定时任务
     * @param jobName
     * @param group
     * @param params
     * @return
     * @throws SchedulerException
     */
    @RequestMapping("updateJob")
    public ResponseMessage updateJob(@RequestParam String jobName,@RequestParam String group,String desc,String params) throws SchedulerException {
        scheduleService.updateJob(jobName,group,desc,params);
        return ResponseMessage.success("定时任务更新成功");
    }

    /**
     * 获取定时任务列表
     * @param group
     * @return
     * @throws SchedulerException
     */
    @RequestMapping("jobList")
    public ResponseMessage jobList(@RequestParam String group) throws SchedulerException {
        List<JobDetail> jobList=scheduleService.getJobList(group);
        List<Map<String,Object>> dataMapList=new ArrayList<>();
        for (JobDetail jobDetail:jobList){
            Map<String,Object> maps = new HashMap<>();
            List<Map<String,Object>> params = new ArrayList<>();
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            for (Map.Entry<String, Object> entry : jobDataMap.entrySet()) {
                Map<String,Object> data = new HashMap<>();
                String keys = entry.getKey();
                String type = "";
                if(keys.contains(",")){
                    String[] split = keys.split(",");
                    keys = split[0];
                    type=split[1];
                }else {
                    continue;
                }
                data.put("name",keys);
                data.put("value",entry.getValue());
                data.put("type",type);
                params.add(data);
            }
            maps.put("jobName",jobDetail.getKey().getName());
            maps.put("desc",jobDetail.getDescription());
            maps.put("cls",jobDetail.getJobClass().getName());
            maps.put("params",params);
            maps.put("isInStandbyMode",scheduleService.isInStandbyMode());
            dataMapList.add(maps);
        }
        return ResponseMessage.success("获取定时任务列表成功",dataMapList);
    }

    /**
     * 删除定时任务
     * @param jobName
     * @param group
     * @return
     * @throws SchedulerException
     */
    @RequestMapping("delJob")
    public ResponseMessage delJob(@RequestParam String jobName,@RequestParam String group) throws SchedulerException {
        scheduleService.delJob(jobName,group);
        return ResponseMessage.success("删除任务成功");
    }

    /**
     * 获取触发器列表
     * @param jobName
     * @param group
     * @return
     * @throws ParseException
     * @throws SchedulerException
     */
    @RequestMapping("triggerList")
    public ResponseMessage triggerList(@RequestParam String jobName,@RequestParam String group) throws SchedulerException {
        List<Trigger> triggerList=scheduleService.getTriggersByJob(jobName,group);
        HashMap<String, Trigger.TriggerState> mapState=scheduleService.getTriggerStatus(triggerList);
        List<Map<String,Object>> dataMapList=new ArrayList<>();
        for (Trigger trigger:triggerList){
            Map<String,Object> map = new HashMap<>();
            map.put("triggerName",trigger.getKey().getName());
            map.put("description",trigger.getDescription());
            for (Map.Entry<String, Trigger.TriggerState> entry:mapState.entrySet()){
                String key = entry.getKey();
                if(key.equals(trigger.getKey().getName())){
                    String triggerState = entry.getValue().toString();
                    map.put("status",triggerState);
                    break;
                }
            }
            dataMapList.add(map);
        }
        return ResponseMessage.success("获取触发器列表成功",dataMapList);
    }

    /**
     * 添加任务触发器
     * @param jobName
     * @param group
     * @param triggerName
     * @param planJson
     * @return
     * @throws ParseException
     * @throws SchedulerException
     */
    @RequestMapping("addTrigger")
    public ResponseMessage addTrigger(@RequestParam String jobName,@RequestParam String group,@RequestParam String triggerName,@RequestParam String planJson) throws ParseException, SchedulerException {
        scheduleService.addTrigger(jobName,group,triggerName,planJson);
        return ResponseMessage.success("添加成功");
    }

    /**
     * 更新触发器
     * @param jobName
     * @param group
     * @param triggerName
     * @param planJson
     * @return
     * @throws ParseException
     * @throws SchedulerException
     */
    @RequestMapping("updateTrigger")
    public ResponseMessage updateTrigger(@RequestParam String jobName,@RequestParam String group,@RequestParam String triggerName,@RequestParam String planJson) throws ParseException, SchedulerException {
        scheduleService.updateTrigger(jobName,group,triggerName,planJson);
        return ResponseMessage.success("更新成功");
    }

    /**
     * 删除触发器
     * @param triggerName
     * @param group
     * @return
     * @throws ParseException
     * @throws SchedulerException
     */
    @RequestMapping("delTrigger")
    public ResponseMessage delTrigger(@RequestParam String triggerName,@RequestParam String group) throws ParseException, SchedulerException {
        scheduleService.delTrigger(triggerName,group);
        return ResponseMessage.success("删除成功");
    }

    /**
     * 获取trigger触发器编辑数据
     * @param triggerName
     * @param group
     * @return
     * @throws ParseException
     * @throws SchedulerException
     */
    @RequestMapping("getTrigger")
    public ResponseMessage getTrigger(@RequestParam String triggerName,@RequestParam String group) throws SchedulerException {
        Trigger trigger = scheduleService.getTrigger(triggerName,group);
        if(trigger == null){
            return ResponseMessage.fail("该执行计划已失效，请刷新页面");
        }
        Map<String,Object> dataMap=new HashMap<>();
        String description = trigger.getDescription();
        int type=0;
        if(description.contains("执行一次")){
            type=1;
            String dateStr = description.substring(10);
            Date data= DateUtil.getDate(dateStr);
            dateStr= DateUtil.formatDate(data,DateUtil.FORMAT_DATETIME);
            dataMap.put("date",dateStr);
        }else if(description.contains("分钟执行")){
            type=2;
            String date = description.substring(2,description.length()-5);
            dataMap.put("date",date);
        }else if(description.contains("每天")){
            type=3;
            String date = description.substring(3,description.length()-3);
            dataMap.put("date",date);
        }else if(description.contains("每周")){
            type=4;
            String date = description.substring(3,description.length()-3);
            date = changeDate(date);
            dataMap.put("date",date);
        }else if(description.contains("每月")){
            type=5;
            String date = description.substring(3,description.length()-3);
            date = changeDate(date);
            dataMap.put("date",date);
        }else if(description.contains("CronTrigger")){
            type=6;
            String date = description.split(":")[1];
            dataMap.put("date",date);
        }
        dataMap.put("type",type);
        dataMap.put("jobName",trigger.getJobKey());
        dataMap.put("triggerName",trigger.getKey());
        return ResponseMessage.success("获取计划任务成功",dataMap);
    }

    /**
     * 修改日期
     * @param date
     * @return
     */
    private String changeDate(String date){
        String weeks = StringUtils.substringBeforeLast(date, ",");
        String lastDate = StringUtils.substringAfterLast(date, ",");
        String newDate ="";

        if(weeks.contains(",")&&weeks.contains("星期")){
            String[] days = weeks.split(",");
            for (String day:days){
                if(day.equals("星期一")){
                    newDate+="MON"+",";
                }else if(day.equals("星期二")){
                    newDate+="TUE"+",";
                }else if(day.equals("星期三")){
                    newDate+="WED"+",";
                }else if(day.equals("星期四")){
                    newDate+="THU"+",";
                }else if(day.equals("星期五")){
                    newDate+="FRI"+",";
                }else if(day.equals("星期六")){
                    newDate+="SAT"+",";
                }else if(day.equals("星期日")){
                    newDate+="SUN"+",";
                }
            }
            newDate=newDate+lastDate;
            String a = StringUtils.substringBeforeLast(newDate, ",");
            String b = StringUtils.substringAfterLast(newDate, ",");
            newDate = a+"|"+b;
        }else if(weeks.contains("最后一天")){
            newDate="L"+"|"+lastDate;
        }else if(weeks.contains("星期")||!weeks.contains(",")){
            if(weeks.equals("星期一")){
                newDate="MON"+"|"+lastDate;
            }else if(weeks.equals("星期二")){
                newDate="TUE"+"|"+lastDate;
            }else if(weeks.equals("星期三")){
                newDate="WED"+"|"+lastDate;
            }else if(weeks.equals("星期四")){
                newDate="THU"+"|"+lastDate;
            }else if(weeks.equals("星期五")){
                newDate="FRI"+"|"+lastDate;
            }else if(weeks.equals("星期六")){
                newDate="SAT"+"|"+lastDate;
            }else if(weeks.equals("星期日")){
                newDate="SUN"+"|"+lastDate;
            }else{
                newDate = weeks+"|"+lastDate;
            }
        }else{
            newDate = date;
            String a = StringUtils.substringBeforeLast(newDate, ",");
            String b = StringUtils.substringAfterLast(newDate, ",");
            newDate = a+"|"+b;
        }
        return newDate;
    }

    /**
     * 判断定时器任务class是否存在
     * @param request
     * @return
     */
    private ResponseMessage checkClassExist(HttpServletRequest request){
        String className= RequestUtil.getString(request,"cls");
        if (StringUtil.isEmpty(className)){
            return ResponseMessage.fail("参数cls不允许为空");
        }
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return ResponseMessage.fail("定时任务class不存在");
        }
        return null;
    }

    /**
     * 开启/暂停触发器
     * @param triggerName
     * @param group
     * @return
     * @throws SchedulerException
     */
    @RequestMapping("toggleTriggerRun")
    public ResponseMessage toggleTriggerRun(@RequestParam String triggerName,String group) throws SchedulerException {
        Trigger trigger = scheduleService.getTrigger(triggerName,group);
        if(trigger == null){
            return ResponseMessage.fail("该执行计划已失效，请刷新页面");
        }
        scheduleService.toggleTriggerRun(triggerName,group);
        return ResponseMessage.success("操作成功");
    }

    /**
     * 获取定时任务详情
     * @param jobName
     * @param group
     * @return
     * @throws SchedulerException
     */
    @RequestMapping("getJobDetail")
    public ResponseMessage getJobDetail(@RequestParam String jobName,@RequestParam String group) throws SchedulerException {
        if (StringUtils.isEmpty(jobName)){
            return ResponseMessage.fail("任务名称不允许为空");
        }
        Map<String,Object> dataMap = new HashMap<>();
        List<Map<String,Object>> params = new ArrayList<>();
        boolean isInStandbyMode = scheduleService.isInStandbyMode();//是否挂起
        JobDetail jobDetail = scheduleService.getJobDetailByName(jobName,group);
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        for (Map.Entry<String, Object> entry : jobDataMap.entrySet()) {
            Map<String,Object> map = new HashMap<>();
            String keys = entry.getKey();
            String type = "";
            if(keys.contains(",")){
                String[] split = keys.split(",");
                keys = split[0];
                type=split[1];
            }
            map.put("name",keys);
            map.put("value",entry.getValue());
            map.put("type",type);
            params.add(map);
        }
        dataMap.put("cls",jobDetail.getJobClass().getName());
        dataMap.put("jobName", jobName);
        dataMap.put("desc",jobDetail.getDescription());
        dataMap.put("isStandby", isInStandbyMode);
        dataMap.put("params",params);
        return ResponseMessage.success("获取任务详情成功",dataMap);
    }

    /**
     * 手动执行任务
     * @param jobName
     * @param group
     * @return
     * @throws SchedulerException
     */
    @RequestMapping("executeJob")
    public ResponseMessage executeJob(@RequestParam String jobName,@RequestParam String group) throws SchedulerException {
        scheduleService.executeJob(jobName,group);
        return ResponseMessage.success("手动执行成功");
    }

    /**
     * 验证定时任务是否存在
     * @param jobName
     * @param group
     * @return
     * @throws SchedulerException
     */
    @RequestMapping("validateJob")
    public ResponseMessage validateJob(@RequestParam String jobName,@RequestParam String group) throws SchedulerException {
        boolean isExist=scheduleService.isJobExists(jobName,group);
        String msg="";
        if (isExist){
            msg="定时任务已存在";
        }
        return ResponseMessage.success(msg,isExist);
    }

    /**
     * 获取定时任务日志分页列表
     * @param request
     * @return
     */
    @RequestMapping("logList")
    public ResponseMessage logList(HttpServletRequest request){
        QueryFilter filter=new QueryFilter("listAll",request);
        Pager<JobLog> logPager =logService.getPageBySqlKey(filter);
        return ResponseMessage.success("获取日志列表成功",logPager);
    }

    /**
     * 删除定时任务日志
     * @param request
     * @return
     */
    @RequestMapping("delLog")
    public ResponseMessage delLog(HttpServletRequest request){
        Long[] ids=RequestUtil.getLongAryByStr(request,"ids");
        QueryFilter filter=new QueryFilter("deleteByIds");
        filter.addFilter("ids",ids);
        logService.deleteBySqlKey(filter);
        return ResponseMessage.success("删除成功");
    }

}
