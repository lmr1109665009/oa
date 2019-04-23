package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.system.SysUrlPermissionDao;
import com.suneee.platform.model.system.SysUrlPermission;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *<pre>
 * 对象功能:URL地址拦截管理 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:wdr
 * 创建时间:2014-03-27 16:32:01
 *</pre>
 */
@Service
public class SysUrlPermissionService extends BaseService<SysUrlPermission>
{
	@Resource
	private SysUrlPermissionDao dao;
	
	
	public SysUrlPermissionService()
	{
	}
	
	@Override
	protected IEntityDao<SysUrlPermission, Long> getEntityDao()
	{
		return dao;
	}


	
	
}
