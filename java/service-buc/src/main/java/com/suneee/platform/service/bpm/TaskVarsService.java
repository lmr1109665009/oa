package com.suneee.platform.service.bpm;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.bpm.TaskVarsDao;
import com.suneee.platform.model.bpm.TaskVars;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 对象功能:流程变量定义 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:phl
 * 创建时间:2011-12-01 16:50:07
 */
@Service
public class TaskVarsService extends BaseService<TaskVars>
{

	@Resource
	private TaskVarsDao dao;
	public TaskVarsService()
	{
	}
	
	@Override
	protected IEntityDao<TaskVars, Long> getEntityDao()
	{
		return dao;
	}
    /**
     * 获取本任务下的所有流程变量
     * @param queryFilter
     * @return
     */
	public List<TaskVars> getVars(QueryFilter queryFilter){
	    return dao.getTaskVars(queryFilter);
	}
	
	/**
	 * 根据act实例ID删除参数表
	 * @param actInstId 
	 * void
	 * @exception 
	 * @since  1.0.0
	 */
	public void delVarsByActInstId(String actInstId){
		dao.delVarsByActInstId(actInstId);
	}
	
	/**
	 * 根据act实例ID删除参数历史表
	 * @param actInstId 
	 * void
	 * @exception 
	 * @since  1.0.0
	 */
	public void delHiVarsByActInstId(String actInstId){
		dao.delBySqlKey("delHiVarsByActInstId", actInstId);
	}
}
