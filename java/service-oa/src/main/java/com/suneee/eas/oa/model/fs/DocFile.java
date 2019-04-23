package com.suneee.eas.oa.model.fs;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;
import com.suneee.ucp.base.model.UcpBaseModel;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Alias("docFile")
public class DocFile extends UcpBaseModel {
	public final static Integer IS_LEAF_N=1; //不是叶子节点
	public final static Integer IS_LEAF_Y=0; //是叶子节点
	public final static String IS_PARENT_N="false"; //不是父类节点
	public final static String IS_PARENT_Y="true"; //是父类节点

	public final static Integer CLASSIFY_TYPE=40; //表示流程归档文件
	public final static String ARCHIVE_PATH="/OA/archive/"; //表示流程归档文件路径

	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long id;
	private String name;// 文件名
	private Long parentId;// 所属文档类型
	private String parentName;// 所属文档类型名称
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
	private Integer isPrivate;
	private String firstName;
	private String lastName;
	private Long owner;
	private String fileSaveId;//前端归档传过来的id
	private String downloadUrl;//下载路径
    private String eid;//企业编码
    private Long departmentId;
	private Integer isDocType;
	private Integer isLeaf;
	private String isParent;
	private Short isRoot=0;
	private Integer classify;
	private String bucket;      //桶
public Integer getClassify() {
		return classify;
	}

	public void setClassify(Integer classify) {
		this.classify = classify;
	}

	//  `ID` BIGINT(20) NOT NULL,
//  `NAME` VARCHAR(255) NOT NULL COMMENT '文件名',
//  `TYPES` VARCHAR(255) DEFAULT NULL COMMENT '文档类型链',
//  `SIZE` VARCHAR(50) DEFAULT NULL COMMENT '大小',
//  `PATH` VARCHAR(255) DEFAULT NULL COMMENT '文件路径',
//  `PARENT_ID` BIGINT(20) DEFAULT NULL COMMENT '父节点',
//  `UPTIME` DATETIME DEFAULT NULL,
//  `UPER` BIGINT(20) DEFAULT NULL,
//  `UPERNAME` VARCHAR(100) DEFAULT NULL,
//  `UPERID` BIGINT(20) DEFAULT NULL COMMENT '发布人id',
//  `ADESCRIBE` VARCHAR(255) DEFAULT NULL COMMENT '文件描述',
//  `RANK` VARCHAR(20) DEFAULT NULL COMMENT '文件等级',
//  `DOWNNUMBER` BIGINT(20) DEFAULT '0' COMMENT '下载次数',
//  `IS_PRIVATE` INT(1) NOT NULL DEFAULT '0' COMMENT '是否私有0 不是 1是',
//  `OWNER` BIGINT(20) DEFAULT NULL COMMENT '所属人id',
//   `department_id` BIGINT(20) DEFAULT NULL,
//  `file_save_id` VARCHAR(50) DEFAULT NULL,
//  `eid` VARCHAR(200) DEFAULT NULL,
//  `IS_DOCTYPE` INT(1) NOT NULL DEFAULT '0' COMMENT '是否是目录0 不是 1是',
	public DocFile() {
		super();
	}
	
	public DocFile(Long id, String name, Long parentId, String parentName, String types, String size, String path,
                   Date upTime, Long uper, String uperName, String describe, String rank, Long downNumber, Date bDate,
                   Date eDate, Integer isPrivate, String firstName, String lastName, Long owner, String fileSaveId,
                   String downloadUrl, String eid, Long departmentId, Integer isDocType, Integer isLeaf, String isParent,
                   Short isRoot, Integer clasify) {
		super();
		this.id = id;
		this.name = name;
		this.parentId = parentId;
		this.parentName = parentName;
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
		this.isPrivate = isPrivate;
		this.firstName = firstName;
		this.lastName = lastName;
		this.owner = owner;
		this.fileSaveId = fileSaveId;
		this.downloadUrl = downloadUrl;
		this.eid = eid;
		this.departmentId = departmentId;
		this.isDocType = isDocType;
		this.isLeaf = isLeaf;
		this.isParent = isParent;
		this.isRoot = isRoot;
		this.classify=classify;
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
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
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
	public Integer getIsPrivate() {
		return isPrivate;
	}
	public void setIsPrivate(Integer isPrivate) {
		this.isPrivate = isPrivate;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Long getOwner() {
		return owner;
	}
	public void setOwner(Long owner) {
		this.owner = owner;
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
	public Long getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}
	
	public Integer getIsDocType() {
		return isDocType;
	}
	public void setIsDocType(Integer isDocType) {
		this.isDocType = isDocType;
	}
	public Integer getIsLeaf() {
		return isLeaf;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
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
	
//    `ID` BIGINT(20) NOT NULL,
//    `NAME` VARCHAR(255) NOT NULL COMMENT '文件名',
//    `TYPES` VARCHAR(255) DEFAULT NULL COMMENT '文档类型链',
//    `SIZE` VARCHAR(50) DEFAULT NULL COMMENT '大小',
//    `PATH` VARCHAR(255) DEFAULT NULL COMMENT '文件路径',
//    `PARENT_ID` BIGINT(20) DEFAULT NULL COMMENT '父节点',
//    `UPTIME` DATETIME DEFAULT NULL,
//    `UPER` BIGINT(20) DEFAULT NULL,
//    `UPERNAME` VARCHAR(100) DEFAULT NULL,
//    `UPERID` BIGINT(20) DEFAULT NULL COMMENT '发布人id',
//    `ADESCRIBE` VARCHAR(255) DEFAULT NULL COMMENT '文件描述',
//    `RANK` VARCHAR(20) DEFAULT NULL COMMENT '文件等级',
//    `DOWNNUMBER` BIGINT(20) DEFAULT '0' COMMENT '下载次数',
//    `IS_PRIVATE` INT(1) NOT NULL DEFAULT '0' COMMENT '是否私有0 不是 1是',
//    `OWNER` BIGINT(20) DEFAULT NULL COMMENT '所属人id',
//     `department_id` BIGINT(20) DEFAULT NULL,
//    `file_save_id` VARCHAR(50) DEFAULT NULL,
//    `eid` VARCHAR(200) DEFAULT NULL,
//    `IS_DOCTYPE` INT(1) NOT NULL DEFAULT '0' COMMENT '是否是目录0 不是 1是',
}

