/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: authorization
 * Author:   lmr
 * Date:     2018/9/29 17:24
 * Description: 分类权限设置
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.model.fs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;

/**
 * 〈一句话功能简述〉<br> 
 * 〈分类权限设置〉
 *
 * @author lmr
 * @create 2018/9/29
 * @since 1.0.0
 */
public class Authorization {

    /**
     * 主键
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;
    /**
     * 文件id
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long docId;
    /**
     * 权限类型 create,del,edit,download,access
     */
    private String type;
    /**
     * 授权对象ID
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long ownerId;
    /**
     * 授权对象类型：user,org,role
     */
    private String ownerType;
    /**
     * 授权对象名称
     */
    private String ownerName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}