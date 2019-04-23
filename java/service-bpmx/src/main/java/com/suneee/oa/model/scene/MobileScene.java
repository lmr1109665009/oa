package com.suneee.oa.model.scene;

import com.suneee.ucp.base.model.UcpBaseModel;
import net.sf.json.JSONObject;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 手机场景管理Model
 * @author pengfeng
 * 2018.5.14
 */
public class MobileScene extends UcpBaseModel {
    private Long Id;

    //场景名称
    private String sceneName;

    //流程定义Id
    private Long defId;

    //流程名称
    private String defName;

    //流程定义key
    private String defKey;

    //流程唯一
    private String actDefId;

    //所属分类Id
    private Long typeId;

    //所属分类名称
    private String typeName;

    //关联流程Id，多个用","号隔开
    private String relDefIds;

    //关联流程名字，多个用","号隔开
    private String relDefNames;

    //描述
    private String description;

    //企业编码
    private String enterpriseCode;

    //图标路径
    private String imgPath;

    //排序
    private int sn;

    private String mobileSceneJsonData;

    public Long getId(){return Id;}
    public void setId(Long Id){this.Id=Id;}

    public String getSceneName() {return sceneName;}
    public void setSceneName(String sceneName) {this.sceneName=sceneName;}

    public Long getDefId(){return defId;}
    public void setDefId(Long defId){this.defId=defId;}

    public String getDefName() {return defName;}
    public void setDefName(String defName){this.defName=defName;}

    public String getDefKey() {return defKey;}
    public void setDefKey(String defKey){this.defKey=defKey;}

    public String getActDefId(){return actDefId;}
    public void setActDefId(String actDefId){ this.actDefId=actDefId;}

    public Long getTypeId() {return typeId;}
    public void setTypeId(Long typeId){this.typeId=typeId;}

    public String getTypeName() {return typeName;}
    public void setTypeName(String typeName){this.typeName=typeName;}

    public String getRelDefIds() {return relDefIds;}
    public void setRelDefIds(String relDefIds){this.relDefIds=relDefIds;}

    public String getRelDefNames() {return relDefNames;}
    public void setRelDefNames(String relDefNames){this.relDefNames=relDefNames;}

    public String getDescription() {return description;}
    public void setDescription(String description){this.description=description;}

    public String getEnterpriseCode() {return enterpriseCode;}
    public void setEnterpriseCode(String enterpriseCode){this.enterpriseCode=enterpriseCode;}

    public String getImgPath() {return imgPath;}
    public void setImgPath(String imgPath){this.imgPath=imgPath;}

    public int getSn() {return sn;}
    public void setSn(int sn){this.sn=sn;}

    public String getMobileSceneJsonData(){return mobileSceneJsonData;}
    public void setMobileSceneJsonData(String mobileSceneJsonData){this.mobileSceneJsonData=mobileSceneJsonData;}

    /**
     * @see Object#hashCode()
     */
    public int hashCode()
    {
        return new HashCodeBuilder(-82280557, -700257973)
                .append(this.Id)
                .append(this.sceneName)
                .append(this.defId)
                .append(this.defName)
                .append(this.description)
                .append(this.defName)
                .append(this.actDefId)
                .append(this.typeId)
                .append(this.typeName)
                .append(this.relDefIds)
                .append(this.relDefNames)
                .append(this.imgPath)
                .append(this.enterpriseCode)
                .append(this.sn)
                .append(this.mobileSceneJsonData)
                .toHashCode();
    }
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("Id", this.Id)
                .append("sceneName", this.sceneName)
                .append("defId", this.defId)
                .append("defName", this.defName)
                .append("description", this.description)
                .append("defKey",this.defKey)
                .append("actDefId",this.actDefId)
                .append("typeId",this.typeId)
                .append("typeName",this.typeName)
                .append("relDefIds",this.relDefIds)
                .append("relDefNames",this.relDefNames)
                .append("imgPath",this.imgPath)
                .append("enterpriseCode",this.enterpriseCode)
                .append("sn",this.sn)
                .append("mobileSceneJsonData",mobileSceneJsonData)
                .toString();
    }
}
