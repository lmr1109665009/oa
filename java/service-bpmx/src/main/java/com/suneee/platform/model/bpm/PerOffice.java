package com.suneee.platform.model.bpm;

import com.suneee.core.model.BaseModel;

public class PerOffice extends BaseModel {
    protected String id;
    // 流程企业编码
    protected String enterpriseCode;
    // 流程区分字段
    protected String comeFrom;
    protected Long runId;
    // 流程定义ID
    protected Long defId;
    // actDefId流程定义ID
    protected String actDefId;
    // 流程分类名称
    protected String typeName;

    public PerOffice() {
        super();
    }



    public PerOffice(String id, String enterpriseCode, String comeFrom, Long runId, Long defId, String actDefId,
                     String typeName) {
        super();
        this.id = id;
        this.enterpriseCode = enterpriseCode;
        this.comeFrom = comeFrom;
        this.runId = runId;
        this.defId = defId;
        this.actDefId = actDefId;
        this.typeName = typeName;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getRunId() {
        return runId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }
    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }
    public String getComFrom() {
        return comeFrom;
    }



    public void setComFrom(String comeFrom) {
        this.comeFrom = comeFrom;
    }



    public Long getDefId() {
        return defId;
    }
    public void setDefId(Long defId) {
        this.defId = defId;
    }
    public String getActDefId() {
        return actDefId;
    }
    public void setActDefId(String actDefId) {
        this.actDefId = actDefId;
    }
    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }



}
