package com.suneee.eas.oa.dao.car;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.car.CarAudit;
import org.springframework.stereotype.Repository;

/**
 * 车辆审批Dao
 * @user 子华
 * @created 2018/8/29
 */
@Repository
public class CarAuditDao extends BaseDao<CarAudit> {
    public int deleteByTargetId(Long targetId) {
        return getSqlSessionTemplate().delete(getNamespace()+".deleteByTargetId",targetId);
    }

    public int deleteByInstId(String procInstId) {
        return getSqlSessionTemplate().delete(getNamespace()+".deleteByInstId",procInstId);
    }

    public CarAudit findByApplyId(Long applyId){
        return getSqlSessionTemplate().selectOne(getNamespace() + ".findByApplyId", applyId);
    }
}
