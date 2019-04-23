package com.suneee.eas.oa.dao.car;


import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.car.OilCard;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @user zousiyu
 * @date 2018/8/20 10:02
 */
@Repository
public class OilCardDao extends BaseDao<OilCard> {

    
    //查询油卡编号是否存在
    public int checkCardNumIsExist(Map<String, Object> params){
        return   getSqlSessionTemplate().selectOne(getNamespace()+".checkCardNumIsExist",params);
    }
    
}

