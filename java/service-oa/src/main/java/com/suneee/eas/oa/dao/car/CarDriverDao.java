package com.suneee.eas.oa.dao.car;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.car.CarDriver;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @user zousiyu
 * @date 2018/8/16 13:33
 */
@Repository
public class CarDriverDao extends BaseDao<CarDriver> {
    public int countByDriverId(Map<String, Object> params){
        return getSqlSessionTemplate().selectOne(getNamespace() + ".countByDriverId", params);
    }
}
