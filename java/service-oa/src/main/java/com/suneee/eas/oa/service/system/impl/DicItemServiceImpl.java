package com.suneee.eas.oa.service.system.impl;

import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.system.DicItemDao;
import com.suneee.eas.oa.dao.system.DicTypeDao;
import com.suneee.eas.oa.model.system.DicItem;
import com.suneee.eas.oa.service.system.DicItemService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典项service
 * @user 子华
 * @created 2018/7/31
 */
@Service
public class DicItemServiceImpl extends BaseServiceImpl<DicItem> implements DicItemService {
    private static final Logger log= LogManager.getLogger(DicItemServiceImpl.class);

    private DicItemDao dicItemDao;

    @Autowired
    public void setDicItemDao(DicItemDao dicItemDao) {
        this.dicItemDao = dicItemDao;
        setBaseDao(dicItemDao);
    }

    @Override
    public int save(DicItem model) {
        model.setId(IdGeneratorUtil.getNextId());
        return super.save(model);
    }

    @Override
    public List<DicItem> listByDicId(Long dicId) {
        QueryFilter filter=new QueryFilter("listAll");
        filter.addFilter("dicId",dicId);
        List<DicItem> itemList=dicItemDao.getListBySqlKey(filter);
        return itemList;
    }

    @Override
    public void updateSort(Long[] ids) {
        Integer sn=60;
        for (Long id:ids){
            DicItem dicItem=new DicItem();
            dicItem.setId(id);
            dicItem.setSn(sn);
            dicItemDao.update(dicItem);
            sn++;
        }
    }
}
