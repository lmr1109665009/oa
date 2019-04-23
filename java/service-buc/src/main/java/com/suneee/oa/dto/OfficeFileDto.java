package com.suneee.oa.dto;

import java.io.Serializable;

/**
 * office文件下载dto
 */
public class OfficeFileDto implements Serializable {
    private static final long serialVersionUID = 8095167743735193391L;
    private Integer id;
    private String name;
    private String filename;
    private String path;
    private String ext;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
