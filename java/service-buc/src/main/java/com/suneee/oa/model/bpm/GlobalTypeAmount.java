package com.suneee.oa.model.bpm;

public class GlobalTypeAmount {
    //分类ID
    private Long typeId=0L;
    private int total=0;

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
