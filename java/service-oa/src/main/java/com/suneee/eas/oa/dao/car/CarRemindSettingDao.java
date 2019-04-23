package com.suneee.eas.oa.dao.car;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.car.CarInfo;
import com.suneee.eas.oa.model.car.CarRemindSetting;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @user zousiyu
 * @date 2018/8/23 14:37
 */
@Repository
public class CarRemindSettingDao extends BaseDao<CarRemindSetting> {
    /**
     * 提醒名字不能重复
     * @param params
     * @return
     */
   public int countByName(Map<String, Object> params){
        return getSqlSessionTemplate().selectOne(getNamespace() + ".countByName", params);
    }

    public List<CarRemindSetting> getByName(Map<String, Object> params){
        return getSqlSessionTemplate().selectList(getNamespace() + ".getByName", params);
    }
   
    public List<CarInfo> mainAndInfoList(Map<String, Object> params){
        return getSqlSessionTemplate().selectList(getNamespace() + ".mainAndInfoList", params);
    }
}
