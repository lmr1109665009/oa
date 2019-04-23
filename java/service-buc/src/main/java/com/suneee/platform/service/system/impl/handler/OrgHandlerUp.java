package com.suneee.platform.service.system.impl.handler;

import javax.annotation.Resource;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.IOrgHandler;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.SysOrgService;

public class OrgHandlerUp implements IOrgHandler{

	@Resource
    SysOrgService orgService;
	
	@Override
	public SysOrg getByType(String type) {
		SysOrg sysOrg=(SysOrg) ContextUtil.getCurrentOrg();
		if (sysOrg.getOrgSupId()!=1) {
			sysOrg = orgService.getById(sysOrg.getOrgSupId());
		}
		return sysOrg;
	}
}
