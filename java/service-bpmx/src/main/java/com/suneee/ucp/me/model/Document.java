package com.suneee.ucp.me.model;

import java.util.Date;

import com.suneee.ucp.base.model.UcpBaseModel;

/**
 * 
 * @ClassName: Document
 * @Description:文档表实体
 * @author 游刃
 * @date 2017年4月12日 下午3:55:41
 *
 */
public class Document extends UcpBaseModel {
	private Long id;
	private String name;// 文件名
	private Long docTypeId;// 所属文档类型
	private String docTypeName;// 所属文档类型名称
	private String types;// 文档类型链，模糊查询用
	private String size;// 文件大小
	private String path;// 文件路径
	private Date upTime;// 上传时间
	private Long uper;// 上传人id
	private String uperName;// 上传人用户名
	private String describe;// 文件描述
	private String rank;// 文件等级
	private Long downNumber;// 下载次数
	private Date bDate;// 开始时间
	private Date eDate;// 结束时间

	private String firstName;
	private String lastName;
	
	private String fileSaveId;//前端归档传过来的id
	private String downloadUrl;//下载路径
    private String eid;//企业编码
	public Document() {
		super();
	}

	public Document(Long id, String name, Long docTypeId, String docTypeName, String types, String size, String path,
			Date upTime, Long uper, String uperName, String describe, String rank, Long downNumber, Date bDate,
			Date eDate, String firstName, String lastName,String eid) {
		super();
		this.id = id;
		this.name = name;
		this.docTypeId = docTypeId;
		this.docTypeName = docTypeName;
		this.types = types;
		this.size = size;
		this.path = path;
		this.upTime = upTime;
		this.uper = uper;
		this.uperName = uperName;
		this.describe = describe;
		this.rank = rank;
		this.downNumber = downNumber;
		this.bDate = bDate;
		this.eDate = eDate;
		this.firstName = firstName;
		this.lastName = lastName;
		this.eid = eid;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
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
		try {
			int a = name.indexOf(".");
			this.firstName = name.substring(0, a);
			this.lastName = name.substring(a+1);
		} catch (Exception e) {
		}
	}

	public Long getDocTypeId() {
		return docTypeId;
	}

	public void setDocTypeId(Long docTypeId) {
		this.docTypeId = docTypeId;
	}

	public String getDocTypeName() {
		return docTypeName;
	}

	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getUpTime() {
		return upTime;
	}

	public void setUpTime(Date upTime) {
		this.upTime = upTime;
	}

	public Long getUper() {
		return uper;
	}

	public void setUper(Long uper) {
		this.uper = uper;
	}

	public String getUperName() {
		return uperName;
	}

	public void setUperName(String uperName) {
		this.uperName = uperName;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public Long getDownNumber() {
		return downNumber;
	}

	public void setDownNumber(Long downNumber) {
		this.downNumber = downNumber;
	}

	public Date getbDate() {
		return bDate;
	}

	public void setbDate(Date bDate) {
		this.bDate = bDate;
	}

	public Date geteDate() {
		return eDate;
	}

	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}

	public String getFileSaveId() {
		return fileSaveId;
	}

	public void setFileSaveId(String fileSaveId) {
		this.fileSaveId = fileSaveId;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

}
