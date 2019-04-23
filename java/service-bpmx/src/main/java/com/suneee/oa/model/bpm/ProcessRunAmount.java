package com.suneee.oa.model.bpm;

/**
 * 流程运行统计bean
 */
public class ProcessRunAmount {
    //流程定义ID
    private Long defId;
    private int total=0;

    public Long getDefId() {
        return defId;
    }

    public void setDefId(Long defId) {
        this.defId = defId;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
