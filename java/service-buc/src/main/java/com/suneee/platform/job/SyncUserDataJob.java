/**  
 * @Title: SyncUserDataJob.java 
 * @Package com.jinfuzi.erp.job 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author zyl  
 * @date 2015年5月11日 上午11:30:34 
 * @version V1.0  
 */ 
package com.suneee.platform.job;

import java.util.List;

import com.suneee.core.db.datasource.JdbcTemplateUtil;
import com.suneee.core.page.PageBean;
import com.suneee.core.scheduler.BaseJob;
import org.quartz.JobExecutionContext;

import com.suneee.core.db.datasource.JdbcTemplateUtil;
import com.suneee.core.page.PageBean;
import com.suneee.core.scheduler.BaseJob;
import com.suneee.platform.model.system.SysUser;

/** 
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
    	String sql = "select * from sys_user t where t.update > ";
    	List page = JdbcTemplateUtil.getPage("x5", sql, null, new PageBean(1, 20));
    	System.out.println(page);
    	for (Object object : page) {
    		SysUser user = new SysUser();
    		
		}
    }
    
}
