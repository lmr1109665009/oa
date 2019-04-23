package com.suneee.platform.service.system.impl.curuser;

import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.engine.GroovyScriptEngine;
import com.suneee.core.model.CurrentUser;
import com.suneee.platform.service.system.ICurUserService;
import com.suneee.core.engine.GroovyScriptEngine;
import com.suneee.core.model.CurrentUser;
import com.suneee.platform.service.system.ICurUserService;

public class ScriptUserService implements ICurUserService {
	
	@Resource
	private GroovyScriptEngine groovyScriptEngine;

	@Override
	public List<Long> getByCurUser(CurrentUser currentUser) {
		return null;
	}

	@Override
	public String getKey() {
		return "user";
	}

	@Override
	public String getTitle() {
		return "人员脚本";
	}

}
