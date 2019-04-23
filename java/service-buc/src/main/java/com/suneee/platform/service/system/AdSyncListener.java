package com.suneee.platform.service.system;

import com.suneee.core.ldap.LdapObjectEvent;
import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.Map;

public class AdSyncListener implements ApplicationListener<LdapObjectEvent> {

	@Override
	public void onApplicationEvent(LdapObjectEvent ev) {
		List<Map<String,String>> list=(List<Map<String, String>>) ev.getSource();
		boolean isUser=ev.isUser();
		
	}

	

}
