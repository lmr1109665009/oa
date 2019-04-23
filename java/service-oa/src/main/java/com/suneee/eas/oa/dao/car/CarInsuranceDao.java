/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CarInsuranceDao
 * Author:   lmr
 * Date:     2018/8/16 14:23
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.dao.car;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.car.CarInsurance;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author lmr
 * @create 2018/8/16
 * @since 1.0.0
 */
@Repository
public class CarInsuranceDao extends BaseDao<CarInsurance> {
    public int isInsurNumRepeatForAdd(Map<String,Object> params) {
        return this.getSqlSessionTemplate().selectOne(getNamespace() + ".isInsurNumRepeatForAdd",params);
    }

    public int isInsurNumRepeatForUpdate(Map<String,Object> params) {
        return this.getSqlSessionTemplate().selectOne(getNamespace()+".isInsurNumRepeatForUpdate",params);
    }

    public int deleteById(Map<String,Object> params) {
        return getSqlSessionTemplate().update(getNamespace() + ".deleteById",params);
    }
}