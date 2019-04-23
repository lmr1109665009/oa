package com.suneee.core.db;

import com.suneee.core.page.PageList;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.page.PageList;
import com.suneee.core.web.query.QueryFilter;

/**
 * 集成BaseDao基类。
 * @author ray
 * @param <E>
 */
public abstract class WfBaseDao<E> extends BaseDao<E> {
	
	/**
	 * 获取代办
	 * @param userId
	 * @param queryFilter
	 * @return
	 */
	public PageList getMyTodoTask(Long userId, QueryFilter queryFilter){
		queryFilter.addFilter("userId", userId);
		return (PageList) this.getBySqlKey("getMyTodoTask", queryFilter);
	}
	
	/**
	 * 获取我的草稿
	 * @param userId
	 * @param queryFilter
	 * @return
	 */
	public PageList getDraftByUser(Long userId,QueryFilter queryFilter){
		queryFilter.addFilter("userId", userId);
		return (PageList) this.getBySqlKey("getDraftByUser", queryFilter);
	}
	
	/**
	 * 获取已经结束的流程。
	 * @param userId
	 * @param queryFilter
	 * @return
	 */
	public PageList getEndByUser(Long userId,QueryFilter queryFilter){
		queryFilter.addFilter("userId", userId);
		return (PageList) this.getBySqlKey("getEndByUser", queryFilter);
	}

}
