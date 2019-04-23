package com.suneee.eas.oa.dao.car;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.car.CarApply;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * 车辆申请Dao
 * @user 子华
 * @created 2018/8/29
 */
@Repository
public class CarApplyDao extends BaseDao<CarApply> {

    /**
     * 更新状态
     * @param applyId
     * @param status
     */
    public void updateStatus(Long applyId, int status){
        Map<String, Object> params = new HashMap<>();
        params.put("applyId", applyId);
        params.put("status", status);
        getSqlSessionTemplate().update(getNamespace() + ".updateStatus", params);
    }

    /**
     * 根据流程实例ID来获取车辆申请信息
     * @param procInstId
     * @return
     */
    public CarApply findByProcInstId(String procInstId) {
        return getSqlSessionTemplate().selectOne(getNamespace()+".findByProcInstId");
    }

    /**
     * 根据流程实例ID来更新审批状态
     * @param procInstId
     * @param status
     */
    public int updateStatusByInstId(String procInstId, int status) {
        Map<String,Object> params=new HashMap<>();
        params.put("procInstId",procInstId);
        params.put("status",status);
        return getSqlSessionTemplate().update(getNamespace()+".updateStatusByInstId",params);
    }
}
