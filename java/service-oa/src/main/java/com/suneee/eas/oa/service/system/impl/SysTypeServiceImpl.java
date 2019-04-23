package com.suneee.eas.oa.service.system.impl;

import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.system.SysTypeDao;
import com.suneee.eas.oa.model.system.SysType;
import com.suneee.eas.oa.service.system.SysTypeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 系统分类service
 * @user 子华
 * @created 2018/7/31
 */
@Service
public class SysTypeServiceImpl extends BaseServiceImpl<SysType> implements SysTypeService {
    private static final Logger log= LogManager.getLogger(SysTypeServiceImpl.class);
    private SysTypeDao sysTypeDao;

    @Autowired
    public void setSysTypeDao(SysTypeDao sysTypeDao) {
        this.sysTypeDao = sysTypeDao;
        setBaseDao(sysTypeDao);
    }

    @Override
    public List<SysType> getListBySqlKey(QueryFilter filter) {
        filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
        return super.getListBySqlKey(filter);
    }

    @Override
    public ResponseMessage saveType(SysType sysType) {
        Date now=new Date();
        sysType.setCreateTime(now);
        sysType.setCreateBy(ContextSupportUtil.getCurrentUserId());
        if (sysType.getParentId()==null){
            if (StringUtils.isEmpty(sysType.getCatKey())){
                return ResponseMessage.fail("请选择类别");
            }
            sysType.setParentId(0L);
            sysType.setDepth(0);
            sysType.setNodePath("");
        }else {
            SysType parent=sysTypeDao.findById(sysType.getParentId());
            if (parent==null){
                return ResponseMessage.fail("父分类节点不存在");
            }
            sysType.setDepth(parent.getDepth()+1);
            sysType.setNodePath(parent.getNodePath()+parent.getTypeId()+".");
            sysType.setEnterpriseCode(parent.getEnterpriseCode());
            sysType.setCatKey(parent.getCatKey());
        }
        sysType.setTypeId(IdGeneratorUtil.getNextId());
        sysType.setEnterpriseCode(ContextSupportUtil.getCurrentEnterpriseCode());

        //判断是否重复
        QueryFilter countFilter=new QueryFilter("countAll");
        countFilter.addFilter("enterpriseCode",sysType.getEnterpriseCode());
        countFilter.addFilter("catKey",sysType.getCatKey());
        countFilter.addFilter("name",sysType.getName());
        countFilter.addFilter("parentId",sysType.getParentId());
        Integer count=sysTypeDao.getCountBySqlKey(countFilter);
        if (count>0){
            return ResponseMessage.fail("分类已存在，无法添加");
        }

        sysTypeDao.save(sysType);
        return null;
    }

    @Override
    public ResponseMessage updateType(SysType sysType, SysType oldType) {
        sysType.setUpdateBy(ContextSupportUtil.getCurrentUserId());
        sysType.setUpdateTime(new Date());

        boolean isNeedUpdatePath=false;
        if (sysType.getParentId()!=null&&!sysType.getParentId().equals(oldType.getParentId())){
            if (oldType.getNodePath().contains(sysType.getParentId().toString())){
                return ResponseMessage.fail("父节无法挂在子节点上");
            }
            SysType parent=sysTypeDao.findById(sysType.getParentId());
            if (parent==null){
                return ResponseMessage.fail("父分类节点不存在");
            }
            sysType.setDepth(parent.getDepth()+1);
            sysType.setNodePath(parent.getNodePath()+parent.getTypeId()+".");
            isNeedUpdatePath=true;
        }

        //判断是否重复
        QueryFilter countFilter=new QueryFilter("countAll");
        countFilter.addFilter("enterpriseCode",oldType.getEnterpriseCode());
        countFilter.addFilter("catKey",oldType.getCatKey());
        countFilter.addFilter("name",sysType.getName());
        countFilter.addFilter("parentId",sysType.getParentId()==null?oldType.getParentId():sysType.getParentId());
        countFilter.addFilter("notSelf",sysType.getTypeId());
        Integer count=sysTypeDao.getCountBySqlKey(countFilter);
        if (count>0){
            return ResponseMessage.fail("分类已存在，无法修改");
        }

        sysTypeDao.update(sysType);
        if (isNeedUpdatePath){
            //更新子分类nodePath
            QueryFilter filter=new QueryFilter("listAll");
            filter.addFilter("nodePath",oldType.getNodePath()+oldType.getTypeId()+"."+"%");
            filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
            List<SysType> typeList=sysTypeDao.getListBySqlKey(filter);
            updateNodePath(typeList,oldType.getNodePath()+oldType.getTypeId()+".",sysType.getNodePath()+sysType.getTypeId()+".");
        }
        return null;
    }

    /**
     * 更新排序
     * @param ids
     */
    @Override
    public void updateSort(Long[] ids) {
        Integer sn=1;
        for (Long id:ids){
            SysType sysType=new SysType();
            sysType.setTypeId(id);
            sysType.setSn(sn);
            sysTypeDao.update(sysType);
            sn++;
        }
    }

    /**
     * 更新nodePath
     * @param typeList
     * @param srcStr
     * @param destStr
     */
    private void updateNodePath(List<SysType> typeList,String srcStr,String destStr){
        if (typeList==null||typeList.size()==0){
            return;
        }
        for (SysType type:typeList){
            SysType saver=new SysType();
            saver.setTypeId(type.getTypeId());
            saver.setNodePath(type.getNodePath().replace(srcStr,destStr));
            String[] strArr=saver.getNodePath().split("\\.");
            if (strArr.length==0){
                saver.setDepth(0);
            }else {
                saver.setDepth(strArr.length);
            }
            sysTypeDao.update(saver);
        }
    }
}
