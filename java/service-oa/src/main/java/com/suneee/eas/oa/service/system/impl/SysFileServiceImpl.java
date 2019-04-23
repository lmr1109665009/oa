package com.suneee.eas.oa.service.system.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.system.SysFileDao;
import com.suneee.eas.oa.model.system.SysFile;
import com.suneee.eas.oa.service.system.SysFileService;

/**
 * 系统文件service
 * @user 子华
 * @created 2018/7/31
 */
@Service
public class SysFileServiceImpl extends BaseServiceImpl<SysFile> implements SysFileService {
    private static final Logger log= LogManager.getLogger(SysFileServiceImpl.class);
    private SysFileDao sysFileDao;

    @Autowired
    public void setSysTypeDao(SysFileDao sysFileDao) {
        this.sysFileDao = sysFileDao;
        setBaseDao(sysFileDao);
    }

    @Override
    public int save(SysFile model) {
        model.setFileId(IdGeneratorUtil.getNextId());
        model.setCreateBy(ContextSupportUtil.getCurrentUserId());
        model.setCreator(ContextSupportUtil.getCurrentUsername());
        model.setCreateTime(new Date());
        model.setEnterpriseCode(ContextSupportUtil.getCurrentEnterpriseCode());
        return super.save(model);
    }

    @Override
    public int update(SysFile model) {
        model.setUpdateBy(ContextSupportUtil.getCurrentUserId());
        model.setUpdator(ContextSupportUtil.getCurrentUsername());
        model.setUpdateTime(new Date());
        return super.update(model);
    }
    
    @Override
    public List<SysFile> getByIds(List<Long> fileIds){
    	return sysFileDao.getByIds(fileIds);
    }
}
