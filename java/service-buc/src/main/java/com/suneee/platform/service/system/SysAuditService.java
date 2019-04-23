package com.suneee.platform.service.system;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.system.SysAuditDao;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.model.system.SysAudit;
import com.suneee.platform.dao.system.SysAuditDao;

/**
 * 对象功能:系统日志 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2011-11-26 11:35:04
 */
@Service
public class SysAuditService extends BaseService<SysAudit>
{
	@Resource
	private SysAuditDao dao;
	
	public SysAuditService()
	{
	}
	
	@Override
	protected IEntityDao<SysAudit, Long> getEntityDao() {
		return dao;
	}
}
