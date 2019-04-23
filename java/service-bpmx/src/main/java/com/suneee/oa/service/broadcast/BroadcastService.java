/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: BroadcastService
 * Author:   lmr
 * Date:     2018/5/2 16:23
 * Description: 轮播图管理
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.oa.service.broadcast;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.oa.dao.broadcast.BroadcastDao;
import com.suneee.oa.model.broadcast.Broadcast;
import com.suneee.ucp.mh.model.customColumn.CustomColumnVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈轮播图管理〉
 *
 * @author lmr
 * @create 2018/5/2
 * @since 1.0.0
 */
@Service
public class BroadcastService extends BaseService<Broadcast> {
    @Resource
    BroadcastDao broadcastDao;


    @Override
    protected IEntityDao<Broadcast, Long> getEntityDao() {
        return broadcastDao;
    }


    public List<Broadcast> getByStatus(String enterpriseCode) {
        return broadcastDao.getByStatus(enterpriseCode);
    }
}