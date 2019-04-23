/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: AuthDao
 * Author:   lmr
 * Date:     2018/10/9 11:13
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.dao.fs;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.fs.Authorization;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author lmr
 * @create 2018/10/9
 * @since 1.0.0
 */
@Repository
public class AuthDao extends BaseDao<Authorization> {

    public List<Authorization> getList(Map<String,Object> param) {
        return this.getSqlSessionTemplate().selectList(getNamespace()+".getList",param);
    }

    public void saveList(List<Authorization> auths) {
        this.getSqlSessionTemplate().insert(getNamespace()+".saveList",auths);
    }

    public void deleteByDocId(Long docId) {
        this.getSqlSessionTemplate().delete(getNamespace()+".deleteByDocId",docId);
    }
}