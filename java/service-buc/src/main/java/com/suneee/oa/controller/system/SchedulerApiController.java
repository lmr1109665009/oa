package com.suneee.oa.controller.system;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.page.PageList;
import com.suneee.core.scheduler.ParameterObj;
import com.suneee.core.scheduler.SchedulerService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysJobLog;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.platform.service.system.SysJobLogService;
import com.suneee.weixin.model.ListModel;
import com.suneee.weixin.util.CommonUtil;
import com.suneee.oa.service.jobDetail.JobDetailService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.vo.ResultVo;
import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Controller
@RequestMapping("/platform/system/schedulerApi/")
@Action(ownermodel= SysAuditModelType.SYSTEM_SETTING)
public class SchedulerApiController extends BaseController {

    @Resource
    SchedulerService schedulerService;
    @Resource
    SysJobLogService sysJobLogService;
    @Resource
    DictionaryService dictionaryService;
    @Resource
    JobDetailService jobDetailService;

    Scheduler scheduler;

    /**
     * 获得任务列表
     * @param response
     * @param request
     * @return
     * @throws SchedulerException
     */
    @RequestMapping("getJobListJson")
    @ResponseBody
    public ResultVo getJobList(HttpServletResponse response, HttpServletRequest request) throws SchedulerException,Exception
    {
        boolean isInStandbyMode = schedulerService.isInStandbyMode();//是否挂起
        List<JobDetail> list=new ArrayList<>();
        String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
        if(StringUtil.isNotEmpty(enterpriseCode)){
            QueryFilter filter = new QueryFilter();
            filter.addFilter("enterpriseCode",enterpriseCode);
            List<com.suneee.oa.model.jobDetail.JobDetail> jobDetailList = jobDetailService.getAll(filter);
            for(com.suneee.oa.model.jobDetail.JobDetail jobDetail:jobDetailList){
                //将任务的名称加上企业编码作为新的唯一标识
                String jobKey = jobDetail.getJobName()+"-"+jobDetail.getEnterpriseCode();
                JobDetail detail = schedulerService.getJobDetailByName(jobKey);
                list.add(detail);
            }
        }else{
            list = schedulerService.getJobList();
        }
        Set<Object> set = new HashSet<>();
        for (JobDetail jobDetail:list){
            Map<String,Object> maps = new HashMap<>();
            Set<Object> dataSet = new HashSet<>();
            String name = jobDetail.getJobClass().getName();
            List<Dictionary> byItemValue = dictionaryService.getByItemValue(name);
            String typeName ="";
            Map<String,String> typeNames = new HashMap<>();
            if(byItemValue.size()>0){
                typeName=byItemValue.get(0).getItemName();
            }
            typeNames.put("typeName",typeName);
            typeNames.put("className",name);
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            for (Map.Entry<String, Object> entry : jobDataMap.entrySet()) {
                Map<String,String> data = new HashMap<>();
                String keys = entry.getKey().toString();
                String type = "";
                if(keys.contains(",")){
                    String[] split = keys.split(",");
                    keys = split[0];
                    type=split[1];
                }
                String value = entry.getValue().toString();
                data.put("name",keys);
                data.put("value",value.toString());
                data.put("type",type);
                dataSet.add(data);
            }
            maps.put("name",jobDetail.getKey().getName().contains("-")?jobDetail.getKey().getName().split("-")[0]:jobDetail.getKey().getName());
            maps.put("description",jobDetail.getDescription());
            maps.put("typeName",typeNames);
            maps.put("jobDataMap",dataSet);
            maps.put("isInStandbyMode",isInStandbyMode);
            set.add(maps);
        }
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取数据成功！",set);
    }

    /**
     * 判断新增任务名是否已经存在
     * @param request
     * @param response
     * @return
     * @throws SchedulerException
     */
    @RequestMapping("isExist")
    @ResponseBody
    public boolean isExist(HttpServletRequest request,HttpServletResponse response) throws SchedulerException{
        String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
        String name = RequestUtil.getString(request,"name");
        boolean isExist=schedulerService.isJobExists(name+"-"+enterpriseCode);
        if(isExist){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 添加任务，当viewName=1【STEP1】时，为跳转到添加的jsp页面，当viewName=2【STEP2】时，为添加的业务方法，然后返回结果。
     * 当前后端分离开发时，只需要改写 viewName=2【STEP2】的情况
     * @param response
     * @param request
     * @param viewName
     * @return
     * @throws Exception
     */
    @RequestMapping("/addJob{viewName}")
    @ResponseBody
    @Action(description="添加定时计划作业",
            execOrder= ActionExecOrder.AFTER,
            detail="<#if STEP1>进入 添加定时计划作业  编辑页面<#else>添加定时计划【${name}】</#if>"
    )
    public ResultVo addJob(HttpServletResponse response, HttpServletRequest request, @PathVariable("viewName") String viewName) throws Exception
    {
        String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
        try {
            SysAuditThreadLocalHolder.putParamerter("STEP1",STEP1.equals(viewName));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        if(STEP1.equals(viewName))
        {
            if(StringUtil.isEmpty(enterpriseCode)){
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"您没有设置组织，不能进行此操作");
            }
            List<Dictionary> dicList = dictionaryService.getByNodeKeyAndEid(Constants.DIC_NODEKEY_DSRW,enterpriseCode);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"新增接口调用成功",dicList);
        }//上面的代码主要是判断进行页面的跳转，此处可以注释掉
        else if(STEP2.equals(viewName))
        {
            try
            {
                if(StringUtil.isEmpty(enterpriseCode)){
                    return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"对不起你没有设置组织，不能添加");
                }
                String className= RequestUtil.getString(request, "jobClass");
                String jobName=RequestUtil.getString(request, "name");
                jobName = jobName+"-"+enterpriseCode;
                String parameterJson=RequestUtil.getString(request, "jobDataMap");
                Boolean isSuitAbile = validJson(parameterJson);

                if(!isSuitAbile){
                    return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"有参数为空值");
                }
                String description=RequestUtil.getString(request,"description");
                boolean isExist=schedulerService.isJobExists(jobName);
                if(isExist)
                {
                    //获取原先定时器任务的计划列表
                    List<Trigger> triggers = schedulerService.getTriggersByJob(jobName);
                    //如果该任务存在则先删除在保存
                    schedulerService.delJob(jobName);
                    schedulerService.addJob(jobName, className, parameterJson,description);
                    //将原来的计划列表加入到新编辑的定时器任务
                    if(triggers.size()>0){
                        JobDetail jobDetail = schedulerService.getJobDetailByName(jobName);
                        Set<Trigger> set = new HashSet<Trigger>();
                        for (Trigger trigger:triggers){
                            set.add(trigger);
                        }
                        schedulerService.updateJobDetailTri(jobDetail,set,true);
                    }
                    return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"更新任务成功");
                }
                else
                {
                    com.suneee.oa.model.jobDetail.JobDetail detail = new com.suneee.oa.model.jobDetail.JobDetail();
                    detail.setJobName(jobName.split("-")[0]);
                    detail.setEnterpriseCode(enterpriseCode);
                    jobDetailService.add(detail);
                    schedulerService.addJob(jobName, className, parameterJson,description);

                    return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"添加任务成功!");
                }
            }
            catch(ClassNotFoundException ex)
            {
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"添加失败!");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"添加失败!");
            }
        }
        return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"参数不正确!");
    }

    private Boolean validJson(String parameterJson) {
        net.sf.json.JSONArray aryJson= net.sf.json.JSONArray.fromObject(parameterJson);
        ParameterObj[] list=(ParameterObj[])aryJson.toArray(aryJson, ParameterObj.class);
        for(int i=0;i<list.length;i++){
            ParameterObj obj=(ParameterObj)list[i];
            String type=obj.getType();
            String name=obj.getName();
            String value=obj.getValue();
            if(StringUtil.isEmpty(type)||StringUtil.isEmpty(name)||StringUtil.isEmpty(value)){
                return false;
            }
        }
        return true;
    }


    /**
     * 删除任务
     * @param response
     * @param request
     * @throws IOException
     * @throws SchedulerException
     * @throws ClassNotFoundException
     */
    @RequestMapping("/delJob")
    @ResponseBody
    @Action(description="删除定时计划作业",
            detail= "删除定时计划作业" +
                    "<#list StringUtils.split(jobName,\",\") as item>" +
                    "【${item}】" +
                    "</#list>"
    )
    public ResultVo delJob(HttpServletResponse response, HttpServletRequest request) throws IOException, SchedulerException, ClassNotFoundException
    {
        try {
            String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
            String[] names=RequestUtil.getStringAryByStr(request, "jobName");
            if(BeanUtils.isNotEmpty(names)) {
                for (String name:names){
                    jobDetailService.delByNameAndCode(name,enterpriseCode);
                    name=name+"-"+enterpriseCode;
                    schedulerService.delJob(name);
                }
            }else{
               return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"还没选择要删除的记录");
            }
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"删除任务成功");
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"删除任务失败");
        }
    }
    /**
     * 添加计划，当viewName=2【STEP2】时为添加计划的保存
     * @param response
     * @param request
     * @param viewName
     * @return
     * @throws IOException
     * @throws SchedulerException
     * @throws ParseException
     */
    @RequestMapping("/addTrigger{viewName}")
    @ResponseBody
    @Action(
            description="添加定时计划",
            execOrder=ActionExecOrder.AFTER,
            detail="<#if STEP1>进入 添加定时计划  编辑页面<#else>添加定时计划作业【${jobName}】计划【${name}】</#if>"
    )
    public ResultVo addTrigger(HttpServletResponse response, HttpServletRequest request, @PathVariable("viewName") String viewName) throws IOException, SchedulerException, ParseException
    {
        ResultVo resultVo = new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
        //添加系统日志信息 -B
        try {
            SysAuditThreadLocalHolder.putParamerter("STEP1",STEP1.equals(viewName));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        //ModelAndView mv=new ModelAndView();
        if(STEP1.equals(viewName))
        {
           /* String jobName=RequestUtil.getString(request,"jobName");
            mv.setViewName("/platform/scheduler/triggerAdd");
            mv.addObject("jobName", jobName);*/
            return null;
        }
        else if(STEP2.equals(viewName))
        {
            String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
            if(StringUtil.isEmpty(enterpriseCode)){
                resultVo.setMessage("您没有设置组织，不能执行此操作");
                return resultVo;
            }
            String trigName=RequestUtil.getString(request, "name");
            String jobName=RequestUtil.getString(request,"jobName");
            jobName=jobName+"-"+enterpriseCode;
            trigName = trigName+"-"+enterpriseCode;
            String planJson=RequestUtil.getString(request,"planJson");
            Boolean isCron = validCron(planJson);
            if(!isCron){
                resultVo.setMessage("该表达式样式错误！请修改");
                return resultVo;
            }
            if(planJson.contains(",L")){
                resultVo.setMessage("最后一天只能单独选择");
                return resultVo;
            }
            //判断触发器是否存在
            boolean rtn=schedulerService.isTriggerExists(trigName);
            if(rtn)
            {
                resultVo.setMessage("指定的计划名称已经存在!");
                return resultVo;
            }
            try {
                schedulerService.addTrigger(jobName, trigName, planJson);
                resultVo.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
                resultVo.setMessage("添加计划成功!");
                return resultVo;
            } catch (SchedulerException e) {
                String str = MessageUtil.getMessage();
                if (StringUtil.isNotEmpty(str)) {
                    resultVo.setMessage(str);
                    return resultVo;
                } else {
                    resultVo.setMessage("添加计划失败!");
                    return resultVo;
                }
            }
        }
        return resultVo;
    }

    public Boolean validCron(String cron) throws ParseException{
       net.sf.json.JSONObject json= net.sf.json.JSONObject.fromObject(cron);
        int type = (int) json.get("type");
        if(type==6){
            Object cronStr = json.get("txtCronExpression");
       if(null!=cronStr){
           try {
               ScheduleBuilder sb6=CronScheduleBuilder.cronSchedule(cronStr.toString());
               return true;
           }catch (Exception e){
               return false;
           }
       }else{
           return false;
       }
        }else{
            return true;
        }
    }
    /**
     * 计划列表
     * @param response
     * @param request
     * @return
     * @throws SchedulerException
     */
    @RequestMapping("/getTriggersByJob")
    @ResponseBody
    public ResultVo getTriggersByJob(HttpServletResponse response, HttpServletRequest request) throws SchedulerException
    {

        Set set = new HashSet();

        String jobName=RequestUtil.getString(request,"jobName");
        String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
        if(StringUtil.isEmpty(enterpriseCode)){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"您没有设置组织，不能执行此操作");
        }
        List<Trigger> list= schedulerService.getTriggersByJob(jobName+"-"+enterpriseCode);
        HashMap<String, Trigger.TriggerState> mapState=schedulerService.getTriggerStatus(list);
        Iterator<Map.Entry<String, Trigger.TriggerState>> iterator = mapState.entrySet().iterator();
        for (Trigger trigger:list){
            Map<String,Object> map = new HashMap<>();
            map.put("name",trigger.getKey().getName().contains("-")?trigger.getKey().getName().split("-")[0]:trigger.getKey().getName());
            map.put("description",trigger.getDescription());
            if(iterator.hasNext()){
                String triggerState = iterator.next().getValue().toString();
                map.put("status",triggerState);
                set.add(map);
                continue;
            }

        }
        JSONObject json=new JSONObject();
        json.put("list",set);
        json.put("jobName",jobName);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取数据成功！",json);
    }
    /**
     * 执行任务
     * @param request
     * @param response
     * @throws IOException
     * @throws SchedulerException
     */
    @RequestMapping("executeJob")
    @ResponseBody
    public ResultVo executeJob(HttpServletRequest request, HttpServletResponse response) throws IOException, SchedulerException{

        try {
            String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
            if(StringUtil.isEmpty(enterpriseCode)){
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"您没有设置组织，不能执行此操作");
            }
            String[] jobNames=RequestUtil.getStringAryByStr(request,"jobName");
        if(BeanUtils.isNotEmpty(jobNames)){
            for (String jobName:jobNames){
                schedulerService.executeJob(jobName+"-"+enterpriseCode);
            }
        }
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"执行成功");
        }catch (Exception e) {
           return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"执行失败",e.getMessage());
        }
    }
    /**
     *验证类
     * @param request
     * @throws Exception
     */
    @RequestMapping("validClass")
    @ResponseBody
    public ResultVo validClass(HttpServletRequest request) throws Exception {
        String className = RequestUtil.getString(request, "className", "");
        boolean rtn = BeanUtils.validClass(className);
        ResultVo obj =null;
        if (rtn) {
            obj = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "验证类成功!");
        } else {
            obj = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "验证类失败!");
        }
        return obj;
    }
    /**
     * 删除触发器
     * @param response
     * @param request
     * @throws IOException
     * @throws SchedulerException
     * @throws ClassNotFoundException
     */
    @RequestMapping("/delTrigger")
    @ResponseBody
    @Action(
            description="删除定时计划作业计划" ,
            execOrder=ActionExecOrder.BEFORE ,
            detail="删除定时计划作业计划：" +
                    "<#list StringUtils.split(name,\",\") as item>" +
                    "<#assign entity=SysAuditLinkService.getTrigger(item)/>" +
                    "【作业：${entity.jobKey.name}，计划：${item}】" +
                    "</#list>"
    )
    public ResultVo delTrigger(HttpServletResponse response, HttpServletRequest request) throws IOException, SchedulerException, ClassNotFoundException
    {
        try {
            String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
            String[] names = RequestUtil.getStringAryByStr(request,"name");
            if(BeanUtils.isNotEmpty(names)) {
                for (String name: names) {
                    schedulerService.delTrigger(name+"-"+enterpriseCode);
                }
                return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"操作成功");
            }else{
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"还没选择记录");
            }
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"操作失败");
        }

    }
    /**
     * 启用或禁用
     * @param response
     * @param request
     * @throws IOException
     * @throws SchedulerException
     */
    @RequestMapping("/toggleTriggerRun")
    @ResponseBody
    @Action(
            description="启用或禁用定时计划作业计划" ,
            execOrder=ActionExecOrder.AFTER ,
            detail="设置定时计划作业【${jobName}】的计划状态：" +
                    "<#list StringUtils.split(triggerName,\",\") as item>" +
                    "【${item}：" +
                    "	<#if TriggerState.NORMAL==striggerStatus[item]>启用" +
                    " 	<#elseif TriggerState.PAUSED==striggerStatus[item]>禁用 "+
                    " 	<#elseif TriggerState.ERROR==striggerStatus[item]>执行出错" +
                    " 	<#elseif TriggerState.COMPLETE==striggerStatus[item]>完成" +
                    " 	<#elseif TriggerState.BLOCKED==striggerStatus[item]>正在执行" +
                    " 	<#elseif TriggerState.NONE==striggerStatus[item]>未启动" +
                    " 	<#elseif TriggerState.PAUSED==striggerStatus[item]>禁用" +
                    "	<#else>未知</#if>" +
                    "】" +
                    "</#list>"
    )
    public ResultVo toggleTriggerRun(HttpServletResponse response, HttpServletRequest request) throws IOException, SchedulerException
    {
        try {
        String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
        String[] names=RequestUtil.getStringAryByStr(request, "name");
        if(BeanUtils.isNotEmpty(names)){
            for(String name:names){
                schedulerService.toggleTriggerRun(name+"-"+enterpriseCode);
            }
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"操作成功!");
        }else{
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"还没选择要操作的记录");
        }
        }catch (Exception e) {
            return  new ResultVo(ResultVo.COMMON_STATUS_FAILED,"操作失败");
        }

    }
    /**
     * 任务执行日志列表
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getLogList")
    @ResponseBody
    public ResultVo selector(HttpServletRequest request,HttpServletResponse response) throws Exception
    {
        try {
        JSONObject json = new JSONObject();
        String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
        if(StringUtil.isEmpty(enterpriseCode)){
             return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"您没有设置组织，不能执行此操作");
        }
        String jobName=RequestUtil.getString(request, "jobName");
        String trigName=RequestUtil.getString(request, "trigName");
        QueryFilter filter =new QueryFilter(request);
        if(StringUtil.isNotEmpty(jobName)) filter.addFilter("jobName",jobName+"-"+enterpriseCode);
        if(StringUtil.isNotEmpty(trigName)) filter.addFilter("trigName",trigName+"-"+enterpriseCode);
        List<SysJobLog> list=sysJobLogService.getAll(filter);
        for (SysJobLog log:list){
            log.setJobName(log.getJobName().contains("-")?log.getJobName().split("-")[0]:log.getJobName());
        }
        ListModel model = CommonUtil.getListModel((PageList)list);
        json.put("jobName",jobName);
        json.put("trigName",trigName);
        json.put("list",model);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"日志列表获取成功",json);
        }catch (Exception e){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"日志列表获取失败",e.getMessage());
        }
    }
    /**
     * 删除任务日志
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("delJobLog")
    @ResponseBody
    @Action(
            description="删除定时计划作业执行日志",
            execOrder=ActionExecOrder.BEFORE,
            detail= "删除定时计划作业执行日志" +
                    "<#list StringUtils.split(logId,\",\") as item>" +
                    "<#assign entity=sysJobLogService.getById(Long.valueOf(item))/>" +
                    "【作业：${entity.jobName}，计划：${entity.trigName}，内容：${entity.content}】" +
                    "</#list>"
    )
    public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        try {
            Long[] lAryId =RequestUtil.getLongAryByStr(request, "logId");
            sysJobLogService.delByIds(lAryId);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"删除任务日志成功");
        } catch (Exception e) {
           return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"删除任务日志失败");
        }
    }

    /**
     * 修改定时器的状态
     * @param request
     * @throws Exception
     */
    @RequestMapping("changeStart")
    @ResponseBody
    public ResultVo changeStart(HttpServletRequest request) throws Exception
    {
        ResultVo message=null;
        String resultMsg ="";
        boolean isStandby = RequestUtil.getBoolean(request, "isStandby");
        try {
            //如果是挂起状态就启动，否则就挂起
            if (isStandby) {
                schedulerService.start();
                resultMsg="启动定时器成功!";
            }else {
                schedulerService.shutdown();
                resultMsg="停止定时器成功!";
            }
            message=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,resultMsg);
        } catch (Exception e) {
            e.printStackTrace();
            if (isStandby) {
                resultMsg="启动定时器失败:";
            }else {
                resultMsg="停止定时器失败:";
            }
            message=new ResultVo(ResultVo.COMMON_STATUS_FAILED,resultMsg+e.getMessage());
        }
        return message;
    }

    @RequestMapping("/getJobDetailByName")
    @ResponseBody
    public ResultVo getJobDetailByName(HttpServletRequest request,HttpServletResponse response)  throws SchedulerException {
        String name = RequestUtil.getString(request, "name");
        if(StringUtil.isEmpty(name)){
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"请选择任务");
            }
        String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
        if(StringUtil.isEmpty(enterpriseCode)){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"您没有设置组织，不能执行此操作");
        }
        String newName =name+"-"+enterpriseCode;
            JSONObject json = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            List<Dictionary> dicList = dictionaryService.getByNodeKeyAndEid(Constants.DIC_NODEKEY_DSRW,enterpriseCode);
            boolean isInStandbyMode = schedulerService.isInStandbyMode();//是否挂起
            JobDetail jobDetail = schedulerService.getJobDetailByName(newName);
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            for (Map.Entry<String, Object> entry : jobDataMap.entrySet()) {
                Map<String,String> map = new HashMap<>();
                String keys = entry.getKey().toString();
                String type = "";
                if(keys.contains(",")){
                    String[] split = keys.split(",");
                    keys = split[0];
                    type=split[1];
                }
                String value = entry.getValue().toString();
                map.put("name",keys);
                map.put("value",value.toString());
                map.put("type",type);
                jsonArray.add(map);
            }
            String className = jobDetail.getJobClass().getName();
            Map<String,String> typeNames = new HashMap<>();
            List<Dictionary> byItemValue = dictionaryService.getByItemValue(className);
            String typeName ="";
            if(byItemValue.size()>0){
                typeName=byItemValue.get(0).getItemName();
            }
            typeNames.put("typeName",typeName);
            typeNames.put("className",className);
            json.put("name", name);
            json.put("typeName",typeNames);
            json.put("description",jobDetail.getDescription());
            json.put("isStandby", isInStandbyMode);
            json.put("dicList",dicList);
            json.put("dataJson",jsonArray);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取成功", json);
    }

    /**
     *根据计划名称获取信息
     * @param request
     * @param response
     * @return
     * @throws SchedulerException
     */
    @RequestMapping("/getTriggerByKey")
    @ResponseBody
    public ResultVo getTriggerByKey(HttpServletRequest request,HttpServletResponse response) throws Exception{
        JSONObject json = new JSONObject();
        String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
        String key = RequestUtil.getString(request,"name");
        if(StringUtil.isNotEmpty(key)){
            Trigger trigger = schedulerService.getTrigger(key+"-"+enterpriseCode);
            if(trigger == null){
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"该执行计划已失效，请刷新页面");
            }
            String description = trigger.getDescription();
            int type=0;
            if(description.contains("执行一次")){
                type=1;
                String date = description.substring(10);
                json.put("date",date);
            }else if(description.contains("分钟执行")){
                type=2;
                String date = description.substring(2,description.length()-5);
                json.put("date",date);
            }else if(description.contains("每天")){
                type=3;
               String date = description.substring(3,description.length()-3);
               json.put("date",date);
            }else if(description.contains("每周")){
                type=4;
                String date = description.substring(3,description.length()-3);
                date = changeDate(date);
                json.put("date",date);
            }else if(description.contains("每月")){
                type=5;
                String date = description.substring(3,description.length()-3);
                date = changeDate(date);
                json.put("date",date);
            }else if(description.contains("CronTrigger")){
                type=6;
                String date = description.split(":")[1];
                json.put("date",date);
            }
            json.put("type",type);
            json.put("jobName",trigger.getJobKey());
            json.put("name",trigger.getKey().getName().contains("-")?trigger.getKey().getName().split("-")[0]:trigger.getKey());
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取计划成功",json);
        }else{
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"没有获取到参数");
        }
    }

    /**
     * 更新计划信息
     * @param request
     * @param response
     * @return
     * @throws SchedulerException
     * @throws ParseException
     */
    @RequestMapping("/updateTrigger")
    @ResponseBody
    public ResultVo updateTrigger(HttpServletRequest request, HttpServletResponse response) throws SchedulerException,ParseException{
        String trigName=RequestUtil.getString(request, "name");
        String jobName=RequestUtil.getString(request,"jobName");
        String planJson=RequestUtil.getString(request,"planJson");
        String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
        if(StringUtil.isEmpty(enterpriseCode)){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"对不起您没有设置组织，不能执行此操作");
        }
        if(!validCron(planJson)){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"该表达式样式错误！请修改");
        }
        if(planJson.contains(",L")){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"最后一天只能单独选择");
        }
        try {
            schedulerService.updateTrigger(jobName+"-"+enterpriseCode,trigName+"-"+enterpriseCode,planJson);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"更新计划信息成功");
        }catch (Exception e){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"更新计划信息失败",e.getMessage());
        }

    }

    public String changeDate(String date) throws Exception{
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

}
