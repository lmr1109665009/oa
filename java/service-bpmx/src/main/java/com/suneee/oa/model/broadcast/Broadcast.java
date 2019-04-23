/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: Broadcast
 * Author:   lmr
 * Date:     2018/5/2 15:55
 * Description: 轮播图片
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.oa.model.broadcast;

import com.suneee.core.model.BaseModel;

/**
 * 〈一句话功能简述〉<br> 
 * 〈轮播图片〉
 *
 * @author lmr
 * @create 2018/5/2
 * @since 1.0.0
 */
public class Broadcast extends BaseModel {
    //轮播id
    private Long id;
    //主题
    private String topic;
    //图片地址
    private String url;
    //图片id和名字
    private String attachment;
    //图片文案
    private String doc;
    //时间间隔
    private Long time;
    //备注
    private String remarks;
    //1启用 0禁用状态
    private int status ;
    //序号
    private Long sn;
    /**
     * 企业编码
     */
    private String enterpriseCode;
    /**
     * 企业名称
     */
    private String enterpriseName;

    public String getEnterpriseCode() {
        return enterpriseCode;
    }



    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getSn() {
        return sn;
    }

    public void setSn(Long sn) {
        this.sn = sn;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}