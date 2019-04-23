package com.suneee.platform.webservice.adpter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import com.suneee.platform.model.system.SysRole;
import com.suneee.platform.model.system.SysRole;

public class SysRoleAdpter extends XmlAdapter<SysRole,SysRole>{

	@Override
	public SysRole marshal(SysRole arg0) throws Exception {
		return (SysRole)arg0;
	}

	@Override
	public SysRole unmarshal(SysRole arg0) throws Exception {
		return (SysRole)arg0;
	}	
}
