/**
 * 对象功能:流程流转状态 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:helh
 * 创建时间:2013-09-18 09:25:55
 */
package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.bpm.BpmProTransTo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BpmProTransToDao extends BaseDao<BpmProTransTo>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return BpmProTransTo.class;
	}
	
	public BpmProTransTo getByTaskId(Long taskId){
		List<BpmProTransTo> list=this.getBySqlKey("getByTaskId", taskId);
		if(list.size()==0) return null;
		return list.get(0);
	}
	
	public void delByActInstId(Long actInstId){
		delBySqlKey("delByActInstId", actInstId);
	}
	
	public List<BpmProTransTo> mattersList(QueryFilter filter){
		return this.getBySqlKey("mattersList", filter);
	}

	/**
	 * 根据流程定义ID获取流程分类
	 * @param filter
	 * @return
	 */
	public List<?> getTransMattersCount(QueryFilter filter){
		return this.getBySqlKey("getTransMattersCount", filter);
	}

	public void delByTaskId(Long taskId){
		delBySqlKey("delByTaskId", taskId);
	}
	
}