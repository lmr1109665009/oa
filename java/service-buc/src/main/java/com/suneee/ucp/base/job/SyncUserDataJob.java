/**  
 * @Title: SyncUserDataJob.java 
 * @Package com.jinfuzi.erp.job 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author zyl  
 * @date 2015年5月11日 上午11:30:34 
 * @version V1.0  
 */ 
package com.suneee.ucp.base.job;

import org.quartz.JobExecutionContext;

import com.suneee.core.scheduler.BaseJob;
import com.suneee.core.util.AppUtil;
import com.suneee.ucp.base.service.system.SysUserExtService;


/** 
 * 定时任务：同步用户信息到用户中心
 * @ClassName: SyncUserDataJob 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zyl 
 * @date 2015年5月11日 上午11:30:34  
 */

public class SyncUserDataJob extends BaseJob 
{
    /**
     * @param context
     * @throws Exception 
     * @see BaseJob#executeJob(JobExecutionContext)
     */ 
    @Override
    public void executeJob(JobExecutionContext context)
        throws Exception
    {
    	SysUserExtService sysUserExtService = (SysUserExtService)AppUtil.getBean(SysUserExtService.class);
    	sysUserExtService.syncUserToUserCenter();
    }
}


