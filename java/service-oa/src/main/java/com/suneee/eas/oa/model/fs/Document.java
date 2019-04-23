/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: document
 * Author:   lmr
 * Date:     2018/9/29 16:52
 * Description: 文件
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.model.fs;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * 〈一句话功能简述〉<br> 
 * 〈文件〉
 *
 * @author lmr
 * @create 2018/9/29
 * @since 1.0.0
 */
public class Document{
    /**
     * 主键
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;
    /**
     * 文件名称
     */
    private String name;
    /**
     * 大小
     */
    private String size;
    /**
     * 文件路径
     */
    private String path;
    /**
     * 父节点id
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long parentId;
    /**
     * 关键词
     */
    private String keyWord;
    /**
     * 浏览次数（阅读次数+下载次数）
     */
    private Integer readNum;
    /**
     * 目录路径
     */
    private String dirPath;
    /**
     * 目录路径名称
     */
    private String dirPathName;
    /**
     * 文件类型
     */
    private String contentType;
    /**
     * 文件后缀名
     */
    private String ext;
    /**
     * 所属企业
     */
    private String enterpriseCode;
    /**
     * 是否为文件夹
     */
    private Byte isDir;
    /**
     * 描述
     */
    private String describes;
    /**
     * 是否删除：0=未删除，1=已删除
     */
    private Byte isDelete;
    /**
     * 文档权限
     */
    private Set<String> authNum;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人
     */
    private Long createBy;
    /**
     * 创建人姓名
     */
    private String createByName;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 更新人
     */
    private Long updateBy;
    /**
     * 更新人姓名
     */
    private String updateByName;

    /**
     * 分享时间
     */

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public Integer getReadNum() {
        return readNum;
    }

    public void setReadNum(Integer readNum) {
        this.readNum = readNum;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public Byte getIsDir() {
        return isDir;
    }

    public void setIsDir(Byte isDir) {
        this.isDir = isDir;
    }

    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
    }

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }

    public String getDirPathName() {
        return dirPathName;
    }

    public void setDirPathName(String dirPathName) {
        this.dirPathName = dirPathName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public Set<String> getAuthNum() {
        return authNum;
    }

    public void setAuthNum(Set<String> authNum) {
        this.authNum = authNum;
    }

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }

    public String getUpdateByName() {
        return updateByName;
    }

    public void setUpdateByName(String updateByName) {
        this.updateByName = updateByName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(id, document.id) &&
                Objects.equals(name, document.name) &&
                Objects.equals(size, document.size) &&
                Objects.equals(path, document.path) &&
                Objects.equals(parentId, document.parentId) &&
                Objects.equals(keyWord, document.keyWord) &&
                Objects.equals(readNum, document.readNum) &&
                Objects.equals(dirPath, document.dirPath) &&
                Objects.equals(dirPathName, document.dirPathName) &&
                Objects.equals(contentType, document.contentType) &&
                Objects.equals(ext, document.ext) &&
                Objects.equals(enterpriseCode, document.enterpriseCode) &&
                Objects.equals(isDir, document.isDir) &&
                Objects.equals(describes, document.describes) &&
                Objects.equals(isDelete, document.isDelete) &&
                Objects.equals(authNum, document.authNum) &&
                Objects.equals(createTime, document.createTime) &&
                Objects.equals(createBy, document.createBy) &&
                Objects.equals(createByName, document.createByName) &&
                Objects.equals(updateTime, document.updateTime) &&
                Objects.equals(updateBy, document.updateBy) &&
                Objects.equals(updateByName, document.updateByName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, size, path, parentId, keyWord, readNum, dirPath, dirPathName, contentType, ext, enterpriseCode, isDir, describes, isDelete, authNum, createTime, createBy, createByName, updateTime, updateBy, updateByName);
    }
}