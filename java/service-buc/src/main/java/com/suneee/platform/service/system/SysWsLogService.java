package com.suneee.platform.service.system;

import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.system.SysWsLogDao;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.model.system.SysWsLog;
import com.suneee.platform.dao.system.SysWsLogDao;

/**
 *<pre>
 * 对象功能:SYS_WS_LOG Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:cjj
 * 创建时间:2013-05-31 10:41:48
 *</pre>
 */
@Service
public class SysWsLogService extends BaseService<SysWsLog>
{
	@Resource
	private SysWsLogDao dao;
	
	
	
	public SysWsLogService()
	{
	}
	
	@Override
	protected IEntityDao<SysWsLog, Long> getEntityDao()
	{
		return dao;
	}
	
	
}
