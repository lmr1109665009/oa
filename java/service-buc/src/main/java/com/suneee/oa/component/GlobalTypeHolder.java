package com.suneee.oa.component;

import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.service.system.GlobalTypeService;

/**
 * 分类holder
 */
public class GlobalTypeHolder {
    private GlobalType globalType;
    private String enterpriseCode;
    private String catKey;
    private GlobalTypeService typeService;

    public GlobalTypeHolder(String enterpriseCode, String catKey, GlobalTypeService typeService) {
        this.enterpriseCode = enterpriseCode;
        this.catKey = catKey;
        this.typeService = typeService;
    }

    /**
     * 获取默认分类
     * @return
     */
    public GlobalType getDefaultType(){
        if (globalType!=null){
            return globalType;
        }
        globalType=typeService.getDefaultType(catKey,enterpriseCode);
        if (globalType!=null){
            return globalType;
        }
        globalType=typeService.addDefaultType(catKey,enterpriseCode);
        return globalType;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getCatKey() {
        return catKey;
    }

    public void setCatKey(String catKey) {
        this.catKey = catKey;
    }

    public GlobalTypeService getTypeService() {
        return typeService;
    }

    public void setTypeService(GlobalTypeService typeService) {
        this.typeService = typeService;
    }
}
