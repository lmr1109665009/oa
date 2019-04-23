package com.suneee.ucp.me.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.me.dao.SysNewsDao;
import com.suneee.ucp.me.model.SysNews;

/**
 * 
 * @ClassName: SysNewsServcie
 * @Description: 新闻动态service
 * @author 游刃
 * @date 2017年4月25日 下午2:10:11
 *
 */
@Service
public class SysNewsServcie extends UcpBaseService<SysNews> {

	@Resource
	private SysNewsDao sysNewsDao;

	@Override
	protected IEntityDao<SysNews, Long> getEntityDao() {
		return sysNewsDao;
	}
	
	public List<SysNews> getTopNews(int top){
		return sysNewsDao.getTopNews(top);
	}
	public SysNews getNewsById(Long id){
		return sysNewsDao.getNewsById(id);
	}

}
