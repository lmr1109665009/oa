package com.suneee.eas.schedule.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务日志model
 * @user 子华
 * @created 2018/8/29
 */
public class JobLog implements Serializable {
    private static final long serialVersionUID = -2117800308556176610L;
    public static final int STATE_SUCCESS=0;
    public static final int STATE_FAIL=1;
    //日志ID
    private Long logId;
    //任务名称
    private String jobName;
    //触发器名称
    private String triggerName;
    //所属分组
    private String group;
    //开始时间
    private Date startTime;
    //结束时间
    private Date endTime;
    //日志内容
    private String content;
    //状态
    private Integer state;
    //运行时间持续时间
    private long runtime;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public long getRuntime() {
        return runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }
}
