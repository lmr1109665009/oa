package com.suneee.core.web.query.script.impl;

import com.suneee.core.web.query.entity.JudgeScope;
import com.suneee.core.web.query.script.IScopeScript;
import com.suneee.core.web.query.script.ISingleScript;
import com.suneee.core.web.query.entity.JudgeScope;

/**
 * 范围值
 * 
 * @author zxh
 * 
 */
public class ScopeScript implements IScopeScript {
	@Override
	public String getSQL(JudgeScope judgeScope) {
		StringBuilder sb = new StringBuilder();
		// 获得单值脚本构造器
		ISingleScript queryScript = SingleScriptFactory
				.getQueryScript(judgeScope.getOptType());
		// 获得开始值脚本
		String scriptBegin = queryScript.getSQL(judgeScope.getBeginJudge());
		// 获得结束值脚本
		String scriptEnd = queryScript.getSQL(judgeScope.getEndJudge());
		// 拼接
		sb.append(" (").append(scriptBegin).append(" ")
				.append(judgeScope.getRelation()).append(" ").append(scriptEnd)
				.append(") ");
		return sb.toString();
	}
}
