package com.suneee.core.web.query.script;

import com.suneee.core.web.query.entity.JudgeScope;
import com.suneee.core.web.query.entity.JudgeScope;

/**
 * 范围值条件脚本接口
 * 
 * @author zxh
 * 
 */
public interface IScopeScript {

	/**
	 * 获取脚本的SQL
	 * @param judgeScope
	 * @return
	 */
	public String getSQL(JudgeScope judgeScope);
}
