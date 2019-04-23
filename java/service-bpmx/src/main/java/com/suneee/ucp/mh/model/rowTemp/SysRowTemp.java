package com.suneee.ucp.mh.model.rowTemp;

import java.io.Serializable;
import java.util.Date;

/**
*
*  @author SysColumnTemp
*/
public class SysRowTemp implements Serializable {

    private static final long serialVersionUID = 1523329238252L;


    /**
    * 主键
    * 
    * isNullAble:0
    */
    private Long id;

    /**
    * 模板名称
    * isNullAble:0
    */
    private String tempName;

    /**
    * 列宽，逗号分隔多列
    * isNullAble:0
    */
    private String width;

    /**
    * 模板描述
    * isNullAble:1
    */
    private String description;

    /**
    * 
    * isNullAble:0,defaultVal:CURRENT_TIMESTAMP
    */
    private Date createtime;

    /**
    * 
    * isNullAble:1
    */
    private Date updatetime;

    /**
    * 
    * isNullAble:0
    */
    private Long createBy;

    /**
    * 
    * isNullAble:1
    */
    private Long updateBy;

    private Integer colNum;

    public Integer getColNum() {
        return colNum;
    }

    public void setColNum(Integer colNum) {
        this.colNum = colNum;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getId(){
        return this.id;
    }

    public void setTempName(String tempName){
        this.tempName = tempName;
    }

    public String getTempName(){
        return this.tempName;
    }

    public void setWidth(String width){
        this.width = width;
    }

    public String getWidth(){
        return this.width;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }

    public void setCreateBy(Long createBy){
        this.createBy = createBy;
    }

    public Long getCreateBy(){
        return this.createBy;
    }

    public void setUpdateBy(Long updateBy){
        this.updateBy = updateBy;
    }

    public Long getUpdateBy(){
        return this.updateBy;
    }
    @Override
    public String toString() {
        return "SysColumnTemp{" +
                "id='" + id + '\'' +
                "tempName='" + tempName + '\'' +
                "width='" + width + '\'' +
                "description='" + description + '\'' +
                "createtime='" + createtime + '\'' +
                "updatetime='" + updatetime + '\'' +
                "createBy='" + createBy + '\'' +
                "updateBy='" + updateBy + '\'' +
            '}';
    }



}
