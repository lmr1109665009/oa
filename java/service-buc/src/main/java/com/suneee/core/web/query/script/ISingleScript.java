package com.suneee.core.web.query.script;

import com.suneee.core.web.query.entity.JudgeSingle;

/**
 * 单值条件脚本
 * 
 * @author zxh
 * 
 */
public interface ISingleScript {
	/**
	 * 获取脚本的SQL
	 * 
	 * @param judgeSingle
	 *            单值条件
	 * @return
	 */
	public String getSQL(JudgeSingle judgeSingle);
}
