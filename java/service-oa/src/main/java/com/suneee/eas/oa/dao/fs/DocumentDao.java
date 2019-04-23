/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: DocumentDao
 * Author:   lmr
 * Date:     2018/9/30 17:05
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.dao.fs;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.fs.Document;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author lmr
 * @create 2018/9/30
 * @since 1.0.0
 */
@Repository
public class DocumentDao extends BaseDao<Document> {

    public List findByIds(Map<String, Object> param) {
        return this.getSqlSessionTemplate().selectList(getNamespace()+".findByIds",param);
    }

    public void saveList(List<Document> documentList) {
        this.getSqlSessionTemplate().insert(getNamespace()+".saveList",documentList);
    }

    public void delByPath(Map<String, Object> params) {
        this.getSqlSessionTemplate().update(getNamespace()+".delByPath",params);
    }

    public List<Document> getByDirPath(Map<String,Object> params) {
        return this.getSqlSessionTemplate().selectList(getNamespace()+".getByDirPath",params);
    }

    public List<Document> getByParentId(Map<String, Object> params) {
        return this.getSqlSessionTemplate().selectList(getNamespace()+".getByParentId",params);
    }

    public int countNameRepetition(Document document) {
        return (int) this.getSqlSessionTemplate().selectOne(getNamespace()+".countNameRepetition",document);
    }

    public void delById(Map<String,Object> params) {
        this.getSqlSessionTemplate().update(getNamespace()+".delById",params);
    }
}