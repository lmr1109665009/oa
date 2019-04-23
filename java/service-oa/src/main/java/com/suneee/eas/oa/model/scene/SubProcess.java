package com.suneee.eas.oa.model.scene;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 手机场景管理Model
 * @author pengfeng
 * 2018.5.14
 */
public class SubProcess{

    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long Id;

    //场景名称
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long sceneId;

    //主流程定义Id
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
    private Long defId;

    //主流程名称
    private String defName;

    //主流程定义key
    private String defKey;

    //子流程Id
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
    private Long subDefId;

    //
    private String subDefKey;

    //子流程名称
    private String subDefName;

    private String subActDefId;

    //流程定义key
    private String sceneName;

    //所属分类Id
    private String jsonmaping;

    //所属分类名称
    private String triggerJson;

    //描述
    private String description;

    //排序
    private int sn;

    /**
     * 子流程图标 
     */ 
    private String subImgPath;

    public Long getId(){return Id;}
    public void setId(Long Id){this.Id=Id;}

    public String getSceneName() {return sceneName;}
    public void setSceneName(String sceneName) {this.sceneName=sceneName;}

    public Long getDefId(){return defId;}
    public void setDefId(Long defId){this.defId=defId;}

    public String getDefName() {return defName;}
    public void setDefName(String defName){this.defName=defName;}

    public String getDefKey(){return defKey;}
    public void setDefKey(String defKey){this.defKey=defKey;}

    public Long getSubDefId(){return subDefId;}
    public void setSubDefId(Long subDefId){ this.subDefId=subDefId;}

    public String getSubDefKey() {return subDefKey;}
    public void setSubDefKey(String subDefKey){this.subDefKey=subDefKey;}

    public String getSubDefName() {return subDefName;}
    public void setSubDefName(String subDefName){this.subDefName=subDefName;}

    public String getSubActDefId() {
        return subActDefId;
    }

    public void setSubActDefId(String subActDefId) {
        this.subActDefId = subActDefId;
    }

    public Long getSceneId() {return sceneId;}
    public void setSceneId(Long sceneId){this.sceneId=sceneId;}

    public String getJsonmaping() {return jsonmaping;}
    public void setJsonmaping(String jsonmaping){this.jsonmaping=jsonmaping;}

    public String getTriggerJson() {return triggerJson;}
    public void setTriggerJson(String triggerJson){this.triggerJson=triggerJson;}

    public String getDescription() {return description;}
    public void setDescription(String description){this.description=description;}

    public int getSn() {return sn;}
    public void setSn(int sn){this.sn=sn;}

    /**
	 * @return the subImgPath
	 */
	public String getSubImgPath() {
		return subImgPath;
	}
	/**
	 * @param subImgPath the subImgPath to set
	 */
	public void setSubImgPath(String subImgPath) {
		this.subImgPath = subImgPath;
	}
	/**
     * @see Object#hashCode()
     */
    public int hashCode()
    {
        return new HashCodeBuilder(-82280557, -700257973)
                .append(this.Id)
                .append(this.sceneId)
                .append(this.sceneName)
                .append(this.defId)
                .append(this.defName)
                .append(this.defKey)
                .append(this.subDefId)
                .append(this.subDefName)
                .append(this.subDefKey)
                .append(this.subActDefId)
                .append(this.description)
                .append(this.jsonmaping)
                .append(this.triggerJson)
                .append(this.sn)
                .append(this.subImgPath)
                .toHashCode();
    }
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("Id", this.Id)
                .append("sceneId", this.sceneId)
                .append("sceneName", this.sceneName)
                .append("defId", this.defId)
                .append("defName", this.defName)
                .append("defKey",this.defKey)
                .append("subDefId",this.subDefId)
                .append("subDefKey",this.subDefKey)
                .append("subDefName",this.subDefName)
                .append("subActDefId",this.subActDefId)
                .append("description", this.description)
                .append("jsonmaping",this.jsonmaping)
                .append("triggerJson",this.triggerJson)
                .append("sn",this.sn)
                .append("subImgPath",this.subImgPath)
                .toString();
    }

}
