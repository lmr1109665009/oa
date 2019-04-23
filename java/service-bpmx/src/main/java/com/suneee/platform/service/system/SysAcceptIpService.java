package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.system.SysAcceptIpDao;
import com.suneee.platform.model.system.SysAcceptIp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 对象功能:SYS_ACCEPT_IP Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2012-02-20 17:35:46
 */
@Service
public class SysAcceptIpService extends BaseService<SysAcceptIp>
{
	@Resource
	private SysAcceptIpDao sysAcceptIpDao;
	
	public SysAcceptIpService()
	{
	}
	
	@Override
	protected IEntityDao<SysAcceptIp, Long> getEntityDao()
	{
		return sysAcceptIpDao;
	}	
}
