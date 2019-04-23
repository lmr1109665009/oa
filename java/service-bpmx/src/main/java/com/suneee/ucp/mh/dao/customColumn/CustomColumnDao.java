package com.suneee.ucp.mh.dao.customColumn;

import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.mh.model.customColumn.CustomColumn;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 首页栏目自定义tab的dao
 * @author ytw
 * */
@Repository
public class CustomColumnDao extends UcpBaseDao<CustomColumn>{


    @Override
    public Class getEntityClass() {
        return CustomColumn.class;
    }


    public int deleteCustomShortCutByUserIds(List<Long> userIdList){
        return this.delBySqlKey("deleteCustomShortCutByUserIds", userIdList);
    }


    public void delByColumnId(Long columnId) {
        this.delBySqlKey("deleteCustomTab",columnId);
    }
}
