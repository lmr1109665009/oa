package com.suneee.eas.oa.model.car;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @user zousiyu
 * @date 2018/8/23 14:28
 */
public class CarRemindSetting {
    
    @JsonDeserialize(using = LongJsonDeserializer.class)
    @JsonSerialize(using = LongJsonSerializer.class)
    /**
     * 提醒设置id(主键)
     */
    private Long id;
    
    /**
     * 提醒名称
     */
    private String  name;

    /**
     * 别名（唯一性）
     */
    private String alias;

    /**
     * 提前多少天
     */
    private String params;

    /**
     * 标识
     */
    private  String flag;

    /**
     * 所属企业
     */
    private String enterpriseCode;

    /**
     * 通知方式
     */
    private  String informType;

    /**
     * 提前多少公里提醒
     */
    private String mile;


    public String getMile() {
        return mile;
    }

    public void setMile(String mile) {
        this.mile = mile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getInformType() {
        return informType;
    }

    public void setInformType(String informType) {
        this.informType = informType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("alias", alias)
                .append("params", params)
                .append("flag", flag)
                .append("enterpriseCode", enterpriseCode)
                .append("informType", informType)
                .append("mile", mile)
                .toString();
    }
}
