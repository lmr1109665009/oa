package com.suneee.oa.model.bpm;

/**
 * 任务统计类
 */
public class TaskAmount {
    //分类ID
    private Long typeId=0L;
    //流程定义ID
    private Long defId;
    private int read=0;
    private int total=0;
    private int notRead=0;

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Long getDefId() {
        return defId;
    }

    public void setDefId(Long defId) {
        this.defId = defId;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getNotRead() {
        return notRead;
    }

    public void setNotRead(int notRead) {
        this.notRead = notRead;
    }
}
