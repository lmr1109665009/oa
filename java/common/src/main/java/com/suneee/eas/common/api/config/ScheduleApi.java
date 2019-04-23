package com.suneee.eas.common.api.config;

import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.constant.ServiceConstant;

/**
 * 定时任务api地址
 * @user 子华
 * @created 2018/7/30
 */
public class ScheduleApi {
    /**
     * 获取任务列表
     */
    public static final String jobList= ModuleConstant.COMMON_MODULE+"/schedule/jobList";
    /**
     * 添加任务
     */
    public static final String addJob= ModuleConstant.COMMON_MODULE+"/schedule/addJob";
    /**
     * 更新任务
     */
    public static final String updateJob= ModuleConstant.COMMON_MODULE+"/schedule/updateJob";
    /**
     * 删除任务
     */
    public static final String delJob= ModuleConstant.COMMON_MODULE+"/schedule/delJob";
    /**
     * 新增触发器
     */
    public static final String addTrigger= ModuleConstant.COMMON_MODULE+"/schedule/addTrigger";
    /**
     * 更新触发器
     */
    public static final String updateTrigger= ModuleConstant.COMMON_MODULE+"/schedule/updateTrigger";
    /**
     * 触发器列表
     */
    public static final String triggerList= ModuleConstant.COMMON_MODULE+"/schedule/triggerList";
    /**
     * 删除触发器
     */
    public static final String delTrigger= ModuleConstant.COMMON_MODULE+"/schedule/delTrigger";
    /**
     * 获取触发器编辑数据
     */
    public static final String getTrigger= ModuleConstant.COMMON_MODULE+"/schedule/getTrigger";
    /**
     * 暂停/开启触发器
     */
    public static final String toggleTriggerRun= ModuleConstant.COMMON_MODULE+"/schedule/toggleTriggerRun";
    /**
     * 获取任务详情
     */
    public static final String getJobDetail= ModuleConstant.COMMON_MODULE+"/schedule/getJobDetail";
    /**
     * 手动执行任务
     */
    public static final String executeJob= ModuleConstant.COMMON_MODULE+"/schedule/executeJob";
    /**
     * 验证定时任务
     */
    public static final String validateJob= ModuleConstant.COMMON_MODULE+"/schedule/validateJob";
    /**
     * 定时任务日志列表
     */
    public static final String logList= ModuleConstant.COMMON_MODULE+"/schedule/logList";
    /**
     * 删除定时任务日志
     */
    public static final String delLog= ModuleConstant.COMMON_MODULE+"/schedule/delLog";
}
