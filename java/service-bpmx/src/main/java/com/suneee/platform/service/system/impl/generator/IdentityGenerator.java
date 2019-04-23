package com.suneee.platform.service.system.impl.generator;

import com.suneee.core.datakey.IKeyGenerator;
import com.suneee.core.util.AppUtil;
import com.suneee.platform.service.system.IdentityService;
import com.suneee.core.datakey.IKeyGenerator;
import com.suneee.core.util.AppUtil;
import com.suneee.platform.service.system.IdentityService;

/**
 * 根据流水号别名获取KEY。
 * @author ray
 *
 */
public class IdentityGenerator implements IKeyGenerator {
	
	private String alias="";

	@Override
	public Object nextId() throws Exception {
		IdentityService identityService=(IdentityService) AppUtil.getBean(IdentityService.class);
		return identityService.nextId(alias);
		 
	}

	/**
	 * 设置别名。
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}
	 

}
