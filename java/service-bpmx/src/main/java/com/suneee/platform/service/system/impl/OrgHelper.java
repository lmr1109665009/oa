package com.suneee.platform.service.system.impl;

import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysOrg;

public class OrgHelper {

	public static SysOrg getTopOrg(){
		SysOrg org=new SysOrg();
		org.setOrgName("行政维度");
		org.setOrgId(1L);
		org.setDemId(1L);
		org.setOrgSupId(0L);
		org.setIsRoot((short) 1);
		org.setPath("1.");
		return org;
	}
	

}
