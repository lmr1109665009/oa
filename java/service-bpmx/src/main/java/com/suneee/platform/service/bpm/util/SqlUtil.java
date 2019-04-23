package com.suneee.platform.service.bpm.util;

public class SqlUtil {
	
	/**
	 * 已结束流程数据
	 * @param tableName 自定义表名
	 * @return
	 */
	public static String getEnd(String tableName){
		String	sql="SELECT dj.*, his.*, '' taskId,'' assingner FROM bpm_pro_run_his his, "+tableName+"  dj WHERE dj.id = his.BUSINESSKEY AND his.ENDTIME IS NOT NULL";
		return sql;
	}
	
	/**
	 * 运行中流程数据
	 * @param tableName 自定义表名
	 * @return
	 */
	public static String getTodo(String tableName){
		String sql="SELECT dj.*, run.*, task.id_ taskId,u.FULLNAME assingner FROM bpm_pro_run run,sys_user u, act_ru_task task, "+tableName+"  dj WHERE dj.id = run.BUSINESSKEY AND task.PROC_INST_ID_ = run.actInstId AND task.DESCRIPTION_ != 39 AND u.USERID = task.ASSIGNEE_ GROUP BY run.BUSINESSKEY";	
		return sql;
	}
	
	/**
	 * 获取运行中和已结束的流程数据
	 * @param tableName 自定义表名
	 * @return
	 */
	public static String getAll(String tableName){

		String sql="";
		String ua=" UNION ALL ";

		sql=getTodo( tableName)+ua+getEnd(tableName);
		return sql;
	}

}
