package com.suneee.ucp.me.model;

import java.util.Date;

import com.suneee.ucp.base.model.UcpBaseModel;

/**
 * 
 * @ClassName: DocType
 * @Description: 文档类型表实体
 * @author 游刃
 * @date 2017年4月12日 下午3:55:25
 *
 */
public class DocType extends UcpBaseModel {

	public final static Integer IS_LEAF_N=1; //不是叶子节点
	public final static Integer IS_LEAF_Y=0; //是叶子节点
	public final static String IS_PARENT_N="false"; //不是父类节点
	public final static String IS_PARENT_Y="true"; //是父类节点
	
	private Long id;
	private String typeName;//类型名称
	private String remake;//备注
	private Long parentId;//父节点
	private Long promulgator;//添加者
	private String promulgatorName;//添加者名称
	private Date releaseTime;//添加时间
	private Integer isLeaf;
	private Integer isPrivate;
	private Long owner;
	// 是否父类,主要用于树的展示时用
	private String isParent;
	//是否根节点（0，非根节点,1,根节点)
	private Short isRoot=0;
	private Long departmentId;
	private String eid;//公司编码

	public DocType() {
		super();
	}
	
	
	
	public DocType(Long id, String typeName, String remake, Long parentId, Long promulgator, String promulgatorName,
			Date releaseTime,String eid) {
		super();
		this.id = id;
		this.typeName = typeName;
		this.remake = remake;
		this.parentId = parentId;
		this.promulgator = promulgator;
		this.promulgatorName = promulgatorName;
		this.releaseTime = releaseTime;
		this.eid = eid;
	}



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getRemake() {
		return remake;
	}
	public void setRemake(String remake) {
		this.remake = remake;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public Long getPromulgator() {
		return promulgator;
	}
	public void setPromulgator(Long promulgator) {
		this.promulgator = promulgator;
	}
	public String getPromulgatorName() {
		return promulgatorName;
	}
	public void setPromulgatorName(String promulgatorName) {
		this.promulgatorName = promulgatorName;
	}
	public Date getReleaseTime() {
		return releaseTime;
	}
	public void setReleaseTime(Date releaseTime) {
		this.releaseTime = releaseTime;
	}


	public String getEid() {
		return eid;
	}



	public void setEid(String eid) {
		this.eid = eid;
	}



	public Integer getIsLeaf() {
		return isLeaf;
	}



	public void setIsLeaf(Integer isLeaf) {

		this.isLeaf = isLeaf;
		if(isLeaf!=null&&isLeaf==0){
			this.isParent=IS_PARENT_N;
		}else if(isLeaf!=null&&isLeaf>0){
			this.isParent=IS_PARENT_Y;
		}else{
			this.isParent=null;
		}
	
	}



	public String getIsParent() {
		if(this.isLeaf==null)return IS_PARENT_Y;
		else
		return this.isLeaf>0?IS_PARENT_Y:IS_PARENT_N;
	}



	public void setIsParent(String isParent) {
		this.isParent = isParent;
		if(isParent!=null&&isParent.equals(IS_PARENT_N)){
			this.isLeaf=IS_LEAF_Y;
		}else if(isParent!=null&&isParent.equals(IS_PARENT_Y)){
			this.isLeaf=IS_LEAF_N;
		}else{
			this.isLeaf=null;
		}
	}



	public Short getIsRoot() {
		return isRoot;
	}



	public void setIsRoot(Short isRoot) {
		this.isRoot = isRoot;
	}



	public Integer getIsPrivate() {
		return isPrivate;
	}



	public void setIsPrivate(Integer isPrivate) {
		this.isPrivate = isPrivate;
	}



	public Long getOwner() {
		return owner;
	}



	public void setOwner(Long owner) {
		this.owner = owner;
	}



	public Long getDepartmentId() {
		return departmentId;
	}



	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}



}
