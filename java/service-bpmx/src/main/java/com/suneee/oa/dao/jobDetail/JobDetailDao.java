package com.suneee.oa.dao.jobDetail;

import com.suneee.oa.model.jobDetail.JobDetail;
import com.suneee.ucp.base.dao.UcpBaseDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JobDetailDao extends UcpBaseDao<JobDetail> {
    @Override
    public Class<JobDetail> getEntityClass() {
        return JobDetail.class;
    }

    public JobDetail getByJobNameAndCode(String jobName, String code) {
        Map<String ,String> map = new HashMap<>();
        map.put("jobName",jobName);
        map.put("code",code);
        List<JobDetail> list = this.getBySqlKey("getByJobNameAndCode", map);
        if(null!=list){
            return list.get(0);
        }else {
            return null;
        }
    }

    public List<JobDetail> getBycode(String enterpriseCode) {
        return this.getBySqlKey("getBycode",enterpriseCode);
    }

    public void delByNameAndCode(String name, String code) {
        Map<String,String> params = new HashMap<>();
        params.put("jobName",name);
        params.put("enterpriseCode",code);
        this.delBySqlKey("delByNameAndCode",params);
    }
}
