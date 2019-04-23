package com.suneee.platform.service.system.impl.curuser;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.model.CurrentUser;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.ICurUserService;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.core.model.CurrentUser;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.ICurUserService;
import com.suneee.platform.service.system.SysOrgService;

public class OrgSubUserService implements ICurUserService {
	
	@Resource
    SysOrgService orgService;
	

	@Override
	public List<Long> getByCurUser(CurrentUser currentUser) {
		Long orgId=currentUser.getOrgId();
		SysOrg sysOrg=orgService.getById(orgId);
		List<Long> list=new ArrayList<Long>();
		list.add(orgId);
		if(sysOrg == null) 
			return list;
		while(!sysOrg.getOrgSupId().equals(SysOrg.BEGIN_ORGID)){
			sysOrg=orgService.getById(sysOrg.getOrgSupId());
			if(sysOrg!=null){
				list.add(sysOrg.getOrgId());
			}else{
				break;
			}
		}
		return list;
	}

	@Override
	public String getKey() {
		return "orgSub";
	}

	@Override
	public String getTitle() {
		return "组织授权（包含子组织）";
	}

	

}
