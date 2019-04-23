package com.suneee.ucp.mh.model.customColumn;

import java.util.Date;


/**
 * 首页栏目下tab的数据VO
 * @author ytw
 * */
public class TabDataVO {

    //工单号
    private String workNo;

    //主题
    private String subject;

    //创建人
    private String creator;

    //创建时间
    private Date createTime;

    // 流程定义名称
    private String processName;

    //发文号
    private String extend;


    // 执行持续时间总长（毫秒)
    protected Long duration;

    //是否已读
    private boolean hasRead;

    private String type;

    private Long id;

    private Long copyId;

    public Long getCopyId() {
        return copyId;
    }

    public void setCopyId(Long copyId) {
        this.copyId = copyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkNo() {
        return workNo;
    }

    public void setWorkNo(String workNo) {
        this.workNo = workNo;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    //流程的扩展字段
    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public boolean isHasRead() {
        return hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
