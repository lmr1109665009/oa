package com.suneee.platform.service.system.impl.handler;

import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.IOrgHandler;
import com.suneee.platform.service.system.impl.OrgHelper;
import com.suneee.platform.model.system.SysOrg;

public class OrgHandlerAll  implements IOrgHandler{

	@Override
	public SysOrg getByType(String type) {
		return OrgHelper.getTopOrg();
	}
	
	

}
