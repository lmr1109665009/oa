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
package com.suneee.eas.oa.service.broadcast.impl;

import java.util.List;

import com.suneee.eas.oa.service.broadcast.BroadcastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.oa.dao.broadcast.BroadcastDao;
import com.suneee.eas.oa.model.broadcast.Broadcast;

/**
 * 〈一句话功能简述〉<br> 
 * 〈轮播图管理〉
 *
 * @author lmr
 * @create 2018/5/2
 * @since 1.0.0
 */
@Service
public class BroadcastServiceImpl extends BaseServiceImpl<Broadcast> implements BroadcastService {
    BroadcastDao broadcastDao;
    @Autowired
    protected void setBroadcastDao(BroadcastDao broadcastDao) {
    	this.broadcastDao = broadcastDao;
    	setBaseDao(broadcastDao);
    }

    public List<Broadcast> getByStatus(String enterpriseCode) {
        return broadcastDao.getByStatus(enterpriseCode);
    }
    
    /** 批量删除轮播图
     * @param ids
     */
    public void delByIds(Long[] ids){
    	if(ids == null){
    		return;
    	}
    	for(Long id : ids){
    		this.deleteById(id);
    	}
    }
}