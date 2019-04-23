package com.suneee.ucp.me.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.me.model.SysNews;
/**
 * 
* @ClassName: SysNewsDao 
* @Description: 新闻动态dao
* @author 游刃 
* @date 2017年4月25日 下午2:08:55 
*
 */

@Repository
public class SysNewsDao  extends UcpBaseDao<SysNews>{

	@Override
	public Class getEntityClass() {
		return SysNews.class;
	}

	public List<SysNews> getTopNews(int top){
		return this.getBySqlKey("getTopNews", top);
	}
	public SysNews getNewsById(Long id){
		return this.getUnique("getNewsById", id);
	}
}
