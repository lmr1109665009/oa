package com.suneee.core.web.query.script.impl;

import com.suneee.core.web.query.entity.JudgeScript;
import com.suneee.core.web.query.script.ISqlScript;
import com.suneee.core.web.query.entity.JudgeScript;
import com.suneee.core.web.query.script.ISqlScript;

/**
 * 脚本类型
 * @author zxh
 *
 */
public class SqlScript implements ISqlScript {

	@Override
	public String getSQL(JudgeScript judgeScript) {
		if(judgeScript == null)
			return "";
		
		return judgeScript.getValue();
	}


}
