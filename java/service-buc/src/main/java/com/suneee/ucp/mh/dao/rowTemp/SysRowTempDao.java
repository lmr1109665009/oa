package com.suneee.ucp.mh.dao.rowTemp;

import com.suneee.core.db.BaseDao;
import com.suneee.ucp.mh.model.rowTemp.SysRowTemp;
import org.springframework.stereotype.Repository;

/**
 * 模板管理dao
*  @author ytw
*/
@Repository
public class SysRowTempDao extends BaseDao<SysRowTemp> {


    @Override
    public Class getEntityClass() {
        return SysRowTemp.class;
    }



}