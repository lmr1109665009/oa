/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: AuthServiceImpl
 * Author:   lmr
 * Date:     2018/10/9 11:11
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.service.fs.impl;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.fs.AuthDao;
import com.suneee.eas.oa.model.fs.Authorization;
import com.suneee.eas.oa.service.fs.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
@Service
public class AuthServiceImpl extends BaseServiceImpl<Authorization> implements AuthService {
    private static final Logger logger = LogManager.getLogger(AuthServiceImpl.class);

    private AuthDao authDao;
    @Autowired
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
        setBaseDao(authDao);
    }
    @Override
    public int save(Authorization auth) {
        auth.setId(IdGeneratorUtil.getNextId());
        return authDao.save(auth);
    }

    @Override
    public List<Authorization> getByDocId(Long docId) {
        Map<String,Object> param = new HashMap<>();
        param.put("docId",docId);
        return authDao.getList(param);
    }

    @Override
    public List<Authorization> getByOwnerId(Long ownerId, String ownerType, Long docId) {
        Map<String,Object> param = new HashMap<>();
        param.put("ownerId",ownerId);
        param.put("ownType",ownerType);
        if(docId!=null){
            param.put("docId",docId);
        }
        return authDao.getList(param);
    }

    @Override
    public void saveList(List<Authorization> auths) {
        authDao.saveList(auths);
    }

    @Override
    public void deleteByDocId(Long docId) {
        authDao.deleteByDocId(docId);
    }

    @Override
    public List<Authorization> getByOwnerIds(List<Long> ownerId, String ownerType) {
        Map<String,Object> param = new HashMap<>();
        param.put("ownerId",ownerId);
        param.put("ownType",ownerType);
        return authDao.getList(param);
    }

}