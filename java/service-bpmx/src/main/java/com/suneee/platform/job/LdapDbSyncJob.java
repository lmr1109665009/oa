package com.suneee.platform.job;

import com.suneee.core.scheduler.BaseJob;
import com.suneee.core.util.AppUtil;
import com.suneee.platform.service.ldap.LdapDbSync;
import com.suneee.platform.service.ldap.SysOrgSyncService;
import com.suneee.platform.service.ldap.SysUserSyncService;
import org.quartz.JobExecutionContext;

public class LdapDbSyncJob extends BaseJob {

	@Override
	public void executeJob(JobExecutionContext context) throws Exception {
		LdapDbSync orgDataSyncService = (LdapDbSync) AppUtil.getBean(SysOrgSyncService.class);
		LdapDbSync userDataSyncService = (LdapDbSync) AppUtil.getBean(SysUserSyncService.class);
	
		orgDataSyncService.syncLodapToDb();
		userDataSyncService.syncLodapToDb();
		
	}

}
