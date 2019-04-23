package com.suneee.platform.service.system;

import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysOrg;

public interface IOrgService {
	
	/**
	 * 根据scope取得组织。
	 * @param type	system 或script
	 * @param value	
	 * @return
	 */
	SysOrg getSysOrgByScope(String type, String value);

}
