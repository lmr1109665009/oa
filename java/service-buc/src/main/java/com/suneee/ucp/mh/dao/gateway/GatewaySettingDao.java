package com.suneee.ucp.mh.dao.gateway;


import com.suneee.core.db.BaseDao;
import com.suneee.ucp.mh.model.gatewayManage.GatewaySetting;
import org.springframework.stereotype.Repository;

/**
 * 门户设置dao
 * @author ytw
 * */
@Repository
public class GatewaySettingDao extends BaseDao<GatewaySetting> {

    @Override
    public Class getEntityClass() {
        return GatewaySetting.class;
    }


}
