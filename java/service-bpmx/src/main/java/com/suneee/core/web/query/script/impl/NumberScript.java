package com.suneee.core.web.query.script.impl;

import com.suneee.core.web.query.entity.JudgeSingle;
import com.suneee.core.web.query.script.ISingleScript;
import com.suneee.core.web.query.entity.JudgeSingle;

/**
 * 数值类型的脚本
 * 
 * @author zxh
 * 
 */
public class NumberScript implements ISingleScript {

	@Override
	public String getSQL(JudgeSingle judgeSingle) {
		StringBuilder sb = new StringBuilder();
		if ("1".equals(judgeSingle.getCompare())) {
			sb.append(judgeSingle.getFixFieldName()).append("=")
					.append(judgeSingle.getValue());
		} else if ("2".equals(judgeSingle.getCompare())) {
			sb.append(judgeSingle.getFixFieldName()).append("!=")
					.append(judgeSingle.getValue());
		} else if ("3".equals(judgeSingle.getCompare())) {
			sb.append(judgeSingle.getFixFieldName()).append(">")
					.append(judgeSingle.getValue());
		} else if ("4".equals(judgeSingle.getCompare())) {
			sb.append(judgeSingle.getFixFieldName()).append(">=")
					.append(judgeSingle.getValue());
		} else if ("5".equals(judgeSingle.getCompare())) {
			sb.append(judgeSingle.getFixFieldName()).append("<")
					.append(judgeSingle.getValue());
		} else if ("6".equals(judgeSingle.getCompare())) {
			sb.append(judgeSingle.getFixFieldName()).append("<=")
					.append(judgeSingle.getValue());
		}
		return sb.toString();
	}

}
