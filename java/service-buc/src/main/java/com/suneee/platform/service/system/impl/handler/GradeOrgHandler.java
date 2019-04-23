package com.suneee.platform.service.system.impl.handler;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.IOrgHandler;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.impl.OrgHelper;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.SysOrgService;

public  class GradeOrgHandler implements IOrgHandler{
	
	protected Map<String,String> aliasMap=new HashMap<String, String>();
	
	public void setAliasMap(Map<String,String> aliasMap) {
		this.aliasMap=aliasMap;
	}
	
	
	
	@Resource
    SysOrgService orgService;
	
	@Override
	public SysOrg getByType(String type) {
		
		SysOrg sysOrg=(SysOrg) ContextUtil.getCurrentOrg();
		if(BeanUtils.isNotEmpty(sysOrg)){
			Long gradeValue=getGradeByValue(type);
			while(!gradeValue.equals(sysOrg.getOrgType())){
				sysOrg=orgService.getById(sysOrg.getOrgSupId());
				if(BeanUtils.isEmpty(sysOrg)){
					sysOrg=OrgHelper.getTopOrg();
					break;
				}
			}
		}else{
			sysOrg=OrgHelper.getTopOrg();
		}
		return sysOrg;
	}
	
	public  Long  getGradeByValue(String value){
		return Long.parseLong(aliasMap.get(value));
	}
}
