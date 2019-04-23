package com.suneee.oa.model.jobDetail;

import com.suneee.ucp.base.model.UcpBaseModel;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 定时任务管理Model
 * @author pengfeng
 * 2018.5.14
 */
public class JobDetail extends UcpBaseModel{

    private String jobName;


    private String enterpriseCode;


    public String getJobName(){return jobName;}
    public void setJobName(String jobName){this.jobName=jobName;}

    public String getEnterpriseCode(){return enterpriseCode;}
    public void setEnterpriseCode(String enterpriseCode){this.enterpriseCode=enterpriseCode;}

    /**
     * @see Object#hashCode()
     */
    public int hashCode()
    {
        return new HashCodeBuilder(-82280557, -700257973)

                .append(this.jobName)
                .append(this.enterpriseCode)
                .toHashCode();
    }
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return new ToStringBuilder(this)

                .append("jobName", this.jobName)
                .append("enterpriseCode",this.enterpriseCode)
                .toString();
    }
}
