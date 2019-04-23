package com.suneee.platform.service.system.impl.curuser;

import com.suneee.core.model.CurrentUser;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.ICurUserService;
import com.suneee.platform.service.system.SysOrgService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class OrgUserService implements ICurUserService {
	
	@Resource
	private SysOrgService sysOrgService;

	@Override
	public List<Long> getByCurUser(CurrentUser currentUser) {
		List<SysOrg> orgs= sysOrgService.getOrgsByUserId(currentUser.getUserId());
		List<Long> list=new ArrayList<Long>();
		for(SysOrg org:orgs){
			list.add(org.getOrgId());
		}
		return list;
	}

	@Override
	public String getKey() {
		return "org";
	}

	@Override
	public String getTitle() {
		return "组织授权（本层级）";
		
	}

	

}
