package com.suneee.eas.oa.dao;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.Demo;
import org.springframework.stereotype.Repository;

/**
 * @user 子华
 * @created 2018/7/31
 */
@Repository
public class DemoDao extends BaseDao<Demo> {
    public void saveDemo(Demo demo){
        getSqlSessionTemplate().insert(getNamespace()+".saveDemo",demo);
    }

}
