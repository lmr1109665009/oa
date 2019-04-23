package com.suneee.core.bpm.setting;

import com.suneee.core.api.org.model.ISysUser;
import org.activiti.engine.task.Task;

import com.suneee.core.api.org.model.ISysUser;

/**
 * 流程跳过接口。
 * @author ray
 *
 */
public interface ISkipCondition {
	/**
	 * 判断流程任务是否可以跳过。
	 * @param task
	 * @return
	 */
	boolean canSkip(Task task);
	
	/**
	 * 用于记录任务执行人。
	 * @return
	 */
	ISysUser getExecutor();
	
	/**
	 * 策略标题
	 * @return
	 */
	String getTitle();
	
	
	
	

}
