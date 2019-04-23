/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: BroadcastDao
 * Author:   lmr
 * Date:     2018/5/2 16:33
 * Description: 轮播图dao
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.oa.dao.broadcast;

import com.suneee.core.db.BaseDao;
import com.suneee.oa.model.broadcast.Broadcast;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈轮播图dao〉
 *
 * @author lmr
 * @create 2018/5/2
 * @since 1.0.0
 */
@Repository
public class BroadcastDao extends BaseDao<Broadcast> {

    @Override
    public Class getEntityClass() {
         return Broadcast.class;
    }

    public List<Broadcast> getByStatus(String enterpriseCode) {
        return this.getBySqlKey("getByStatus",enterpriseCode);
    }
}