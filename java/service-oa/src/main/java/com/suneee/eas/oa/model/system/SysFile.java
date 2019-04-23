package com.suneee.eas.oa.model.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * 系统文件model
 * @user 子华
 * @created 2018/8/8
 */
public class SysFile implements Serializable {

    private static final long serialVersionUID = 5128742169958579210L;
    //附件
    public static final String TYPE_ATTACHMENT="attachment";

    //文件标识
    public static final Integer ID_FILE=0;
    //文件夹标识
    public static final Integer ID_FOLDER=1;

    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    //文件ID
    private Long fileId;
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    //父ID
    private Long parentId;
    //是否为文件夹
    private Integer isDir;
    //文件名称
    private String name;
    //文件存储路径
    private String path;
    //文件大小
    private Long size;
    //文件类型
    private String contentType;
    //所属分类：附件、归档等
    @JsonIgnore
    private String type;
    //文件拓展名
    private String ext;
    //文件夹路径
    @JsonIgnore
    private String dirPath="";
    //文件路径名称
    private String dirPathName="";
    //创建人ID
    private Long createBy;
    //创建人
    private String creator;
    //创建时间
    private Date createTime;
    //更新人ID
    private Long updateBy;
    //更新人
    private String updator;
    //更新时间
    private Date updateTime;
    //排序
    @JsonIgnore
    private Integer sn;
    //企业编码
    @JsonIgnore
    private String enterpriseCode;
    //下载地址
    private String download;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getIsDir() {
        return isDir;
    }

    public void setIsDir(Integer isDir) {
        this.isDir = isDir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getDirPathName() {
        return dirPathName;
    }

    public void setDirPathName(String dirPathName) {
        this.dirPathName = dirPathName;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }
}
