/**
 * 
 */
package com.suneee.ucp.base.aop;

import org.aspectj.lang.JoinPoint;

import com.suneee.core.db.datasource.DataSourceUtil;
import com.suneee.core.db.datasource.DbContextHolder;
import com.suneee.core.util.AppConfigUtil;

/**
 * 使用AOP拦截的方式实现数据源的切换
 * @author Administrator
 */
public class DataSourceSwitchAspect {
	
	/**
	 * 设置默认数据源
	 * @param jp
	 */
	public void setDataSourceDefault(JoinPoint jp) {  
		DbContextHolder.setDataSource(DataSourceUtil.DEFAULT_DATASOURCE,  AppConfigUtil.get("jdbc.dbType"));  
	}  
	      
	/**
	 * 设置sqlserver数据源
	 * @param jp
	 */
	public void setDataSourceMssql(JoinPoint jp) {  
		DbContextHolder.setDataSource("dataSource_sqlserver", AppConfigUtil.get("jdbc.sqlserver.dbType"));  
	}

    /**
     * 清除上下文数据源
     */
    public void clearDataSource(){
        DbContextHolder.clearDataSource();
    }
}
