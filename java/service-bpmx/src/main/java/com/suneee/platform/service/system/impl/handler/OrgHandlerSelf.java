package com.suneee.platform.service.system.impl.handler;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.IOrgHandler;

public class OrgHandlerSelf  implements IOrgHandler {

	@Override
	public SysOrg getByType(String type) {
		SysOrg sysOrg=(SysOrg) ContextUtil.getCurrentOrg();
		return sysOrg;
	}

}
