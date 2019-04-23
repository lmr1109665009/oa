package com.suneee.platform.service.system;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.system.SysLogSwitchDao;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysLogSwitch;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.model.system.SysAudit;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysLogSwitch;
import com.suneee.platform.dao.system.SysLogSwitchDao;
import com.sun.corba.se.spi.logging.LogWrapperBase;

/**
 *<pre>
 * 对象功能:日志开关 Service类
 * 开发公司:广州宏天
 * 开发人员:Raise
 * 创建时间:2013-06-24 11:12:27
 *</pre>
 */
@Service
public class SysLogSwitchService extends BaseService<SysLogSwitch>
{
	@Resource
	private SysLogSwitchDao dao;
	
	
	
	public SysLogSwitchService()
	{
	}
	
	@Override
	protected IEntityDao<SysLogSwitch, Long> getEntityDao()
	{
		return dao;
	}
	
	public List<SysLogSwitch> getAll(){
		
		List<SysLogSwitch> switchs = new ArrayList<SysLogSwitch>();
		SysAuditModelType[] types =  SysAuditModelType.values();
		for(SysAuditModelType type:types){
			SysLogSwitch logSwitch = dao.getByModel(type.toString());
			if(logSwitch ==null){
				logSwitch = getInitSysLogSwitch(type);
			}
			switchs.add(logSwitch);
		}
		return switchs;
	}
	
	private SysLogSwitch getInitSysLogSwitch(SysAuditModelType sysAuditModelType){
		SysLogSwitch sysLogSwitch = new SysLogSwitch();
		sysLogSwitch.setModel(sysAuditModelType.toString());
		sysLogSwitch.setStatus(SysLogSwitch.STATUS_CLOSE);
		return sysLogSwitch;
	}

	public SysLogSwitch getByModel(String ownermodel) {
		return dao.getByModel(ownermodel);
	}
}
