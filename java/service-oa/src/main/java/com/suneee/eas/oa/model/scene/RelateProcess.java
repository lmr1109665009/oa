/**
 * @Title: RelateProcess.java 
 * @Package com.suneee.eas.oa.model.scene 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.model.scene;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;

/**
 * @ClassName: RelateProcess 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-09-04 13:48:41 
 *
 */
public class RelateProcess {
	/**
	 * 关联ID
	 */ 
	@JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
	private Long relId;
	/**
	 * 场景ID
	 */ 
	@JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
	private Long sceneId;
	/**
	 * 流程定义ID 
	 */ 
	@JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
	private Long defId;
	/**
	 * 流程定义名称
	 */ 
	private String defName;
	/**
	 * 流程定义key 
	 */ 
	private String defKey;
	/**
	 * activiti流程定义ID 
	 */ 
	private String actDefId;
	/**
	 * 关联流程图标
	 */ 
	private String relImgPath;
	/**
	 * 排序 
	 */ 
	private Integer sn;
	/**
	 * @return the relId
	 */
	public Long getRelId() {
		return relId;
	}
	/**
	 * @param relId the relId to set
	 */
	public void setRelId(Long relId) {
		this.relId = relId;
	}
	/**
	 * @return the sceneId
	 */
	public Long getSceneId() {
		return sceneId;
	}
	/**
	 * @param sceneId the sceneId to set
	 */
	public void setSceneId(Long sceneId) {
		this.sceneId = sceneId;
	}
	/**
	 * @return the defId
	 */
	public Long getDefId() {
		return defId;
	}
	/**
	 * @param defId the defId to set
	 */
	public void setDefId(Long defId) {
		this.defId = defId;
	}
	/**
	 * @return the defName
	 */
	public String getDefName() {
		return defName;
	}
	/**
	 * @param defName the defName to set
	 */
	public void setDefName(String defName) {
		this.defName = defName;
	}
	/**
	 * @return the defKey
	 */
	public String getDefKey() {
		return defKey;
	}
	/**
	 * @param defKey the defKey to set
	 */
	public void setDefKey(String defKey) {
		this.defKey = defKey;
	}
	/**
	 * @return the actDefId
	 */
	public String getActDefId() {
		return actDefId;
	}
	/**
	 * @param actDefId the actDefId to set
	 */
	public void setActDefId(String actDefId) {
		this.actDefId = actDefId;
	}
	/**
	 * @return the relImgPath
	 */
	public String getRelImgPath() {
		return relImgPath;
	}
	/**
	 * @param relImgPath the relImgPath to set
	 */
	public void setRelImgPath(String relImgPath) {
		this.relImgPath = relImgPath;
	}
	/**
	 * @return the sn
	 */
	public Integer getSn() {
		return sn;
	}
	/**
	 * @param sn the sn to set
	 */
	public void setSn(Integer sn) {
		this.sn = sn;
	}
	/** (non-Javadoc)
	 * @Title: toString 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @return 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [relId=").append(relId);
		builder.append(", sceneId=").append(sceneId);
		builder.append(", defId=").append(defId);
		builder.append(", defName=").append(defName);
		builder.append(", defKey=").append(defKey);
		builder.append(", actDefId=").append(actDefId);
		builder.append(", relImgPath=").append(relImgPath);
		builder.append(", sn=").append(sn);
		builder.append("]");
		return builder.toString();
	}
}
