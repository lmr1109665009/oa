package com.suneee.oa.service.jobDetail;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.oa.dao.jobDetail.JobDetailDao;
import com.suneee.oa.model.jobDetail.JobDetail;
import com.suneee.ucp.base.service.UcpBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class JobDetailService extends UcpBaseService<JobDetail>{
    @Resource
    private JobDetailDao jobDetailDao;

    @Override
    protected IEntityDao<JobDetail, Long> getEntityDao() {
        return this.jobDetailDao;
    }

    public JobDetail getByJobNameAndCode(String JobName){
        String code = CookieUitl.getCurrentEnterpriseCode();
        return jobDetailDao.getByJobNameAndCode(JobName,code);
    }

    public List<JobDetail> getBycode(){
        String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
        return jobDetailDao.getBycode(enterpriseCode);
    }

    public void delByNameAndCode(String name,String code){jobDetailDao.delByNameAndCode(name,code);}
}
