package com.suneee.ucp.mh.service.gatewayManage;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.ucp.mh.dao.rowTemp.SysRowTempDao;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.mh.model.rowTemp.SysRowTemp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 模板管理service
 * @author ytw
 * */
@Service
public class SysRowTempService extends BaseService<SysRowTemp> {

    private Logger logger = LoggerFactory.getLogger(SysRowTempService.class);


    @Autowired
    private SysRowTempDao sysRowTempDao;

    @Override
    protected IEntityDao getEntityDao() {
        return sysRowTempDao;
    }

    public ResultVo save(SysRowTemp sysColumnTemp){
        String width = sysColumnTemp.getWidth();
        String[] widthArr = width.split(",");
        //列数
        sysColumnTemp.setColNum(widthArr.length);
        if(sysColumnTemp.getId()==null||sysColumnTemp.getId()==0){
            sysColumnTemp.setCreateBy(ContextUtil.getCurrentUserId());
            sysRowTempDao.add(sysColumnTemp);
        }else{
            sysColumnTemp.setUpdateBy(ContextUtil.getCurrentUserId());
            sysColumnTemp.setUpdatetime(new Date());
            sysRowTempDao.update(sysColumnTemp);
        }
        return ResultVo.getSuccessInstance();
    }

}
