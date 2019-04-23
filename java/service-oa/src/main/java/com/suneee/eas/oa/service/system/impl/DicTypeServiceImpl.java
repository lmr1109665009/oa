package com.suneee.eas.oa.service.system.impl;

import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.system.DicItemDao;
import com.suneee.eas.oa.dao.system.DicTypeDao;
import com.suneee.eas.oa.model.system.DicItem;
import com.suneee.eas.oa.model.system.DicType;
import com.suneee.eas.oa.service.system.DicTypeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据字典service
 * @user 子华
 * @created 2018/7/31
 */
@Service
public class DicTypeServiceImpl extends BaseServiceImpl<DicType> implements DicTypeService {
    private static final Logger log= LogManager.getLogger(DicTypeServiceImpl.class);
    private DicTypeDao dicTypeDao;
    @Autowired
    private DicItemDao dicItemDao;

    @Autowired
    public void setDicTypeDao(DicTypeDao dicTypeDao) {
        this.dicTypeDao = dicTypeDao;
        setBaseDao(dicTypeDao);
    }

    @Override
    public int save(DicType model) {
        model.setEnterpriseCode(ContextSupportUtil.getCurrentEnterpriseCode());
        model.setDicId(IdGeneratorUtil.getNextId());
        return super.save(model);
    }

    @Override
    public List<DicType> getListBySqlKey(QueryFilter filter) {
        filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
        return super.getListBySqlKey(filter);
    }

    @Override
    public DicType getByCode(String code) {
        QueryFilter filter=new QueryFilter("listAll");
        filter.addFilter("code",code);
        filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
        return dicTypeDao.getOneBySqlKey(filter);
    }

    @Override
    public void saveObj(DicType dicType) {
        this.save(dicType);
        Integer sn=60;
        for (DicItem item:dicType.getList()){
            item.setDicId(dicType.getDicId());
            item.setId(IdGeneratorUtil.getNextId());
            item.setSn(sn);
            dicItemDao.save(item);
            sn++;
        }
    }

    @Override
    public void updateObj(DicType dicType) {
        this.update(dicType);
        QueryFilter filter=new QueryFilter("listAll");
        filter.addFilter("dicId",dicType.getDicId());
        List<DicItem> itemList=dicItemDao.getListBySqlKey(filter);
        for (DicItem item:itemList){
            boolean isFind=false;
            for (DicItem temp:dicType.getList()){
                if (item.getId().equals(temp.getId())){
                    isFind=true;
                    break;
                }
            }
            if (!isFind){
                dicItemDao.deleteById(item.getId());
            }
        }

        for (DicItem item:dicType.getList()){
            if (item.getId()==null){
                item.setId(IdGeneratorUtil.getNextId());
                item.setDicId(dicType.getDicId());
                dicItemDao.save(item);
            }else {
                dicItemDao.update(item);
            }
        }
    }
}
