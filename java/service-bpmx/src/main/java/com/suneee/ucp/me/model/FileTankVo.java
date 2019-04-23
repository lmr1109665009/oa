package com.suneee.ucp.me.model;


import java.util.Date;

import com.suneee.core.util.TimeUtil;

public class FileTankVo {
	//文件目录属性
	private String documentName;
	private String docuemntSize;
	private Date uploadTime;
	private String creator;
	private String creatorName;
	private String parentId;
	//文件下载属性
	private String documentIPath;
	private String id;
	private String eid;
	private Long departmentId;

	
	public FileTankVo() {
		super();
	}


	
	



















	public FileTankVo(String documentName, String docuemntSize, Date uploadTime, String creator, String creatorName,
			String parentId, String documentIPath, String id,String eid) {
		super();
		this.documentName = documentName;
		this.docuemntSize = docuemntSize;
		this.uploadTime = uploadTime;
		this.creator = creator;
		this.creatorName = creatorName;
		this.parentId = parentId;
		this.documentIPath = documentIPath;
		this.id = id;
		this.eid = eid;
	}























	public String getCreatorName() {
		return creatorName;
	}










	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}










	public String getParentId() {
		return parentId;
	}






	public void setParentId(String parentId) {
		this.parentId = parentId;
	}






	public String getDocumentName() {
		return documentName;
	}






	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}






	public String getDocuemntSize() {
		return docuemntSize;
	}






	public void setDocuemntSize(String docuemntSize) {
		this.docuemntSize = docuemntSize;
	}






	public String getUploadTime() {
		return TimeUtil.getDateString(this.uploadTime);
	}






	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}






	public String getCreator() {
		return creator;
	}






	public void setCreator(String creator) {
		this.creator = creator;
	}






	public String getDocumentIPath() {
		return documentIPath;
	}






	public void setDocumentIPath(String documentIPath) {
		this.documentIPath = documentIPath;
	}






	public String getId() {
		return id;
	}






	public void setId(String id) {
		this.id = id;
	}
    






















	public String getEid() {
		return eid;
	}























	public void setEid(String eid) {
		this.eid = eid;
	}























	public Long getDepartmentId() {
		return departmentId;
	}























	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	
}
