package com.suneee.platform.service.system;

import java.util.Date;

import javax.annotation.Resource;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.SysObjLogDao;
import com.suneee.platform.model.system.SysObjLog;
import com.suneee.platform.model.system.SysUser;
import org.springframework.stereotype.Service;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.SysObjLogDao;
import com.suneee.platform.model.system.SysObjLog;
import com.suneee.platform.model.system.SysUser;

/**
 * <pre>
 * 对象功能:SYS_OBJ_LOG Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-04-27 11:09:44
 * </pre>
 */
@Service
public class SysObjLogService extends BaseService<SysObjLog> {
	@Resource
	private SysObjLogDao dao;

	public SysObjLogService() {
	}

	@Override
	protected IEntityDao<SysObjLog, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 保存 SYS_OBJ_LOG 信息
	 * 
	 * @param sysObjLog
	 */
	public void save(SysObjLog sysObjLog) {
		Long id = sysObjLog.getId();
		if (id == null || id == 0) {
			id = UniqueIdUtil.genId();
			sysObjLog.setId(id);
			
			//其他自动生成的参数
			sysObjLog.setCreateTime(new Date());
			SysUser sysUser =(SysUser) ContextUtil.getCurrentUser();
			sysObjLog.setOperator(sysUser.getFullname());
			sysObjLog.setOperatorId(sysUser.getUserId());
			
			this.add(sysObjLog);
		} else {
			this.update(sysObjLog);
		}
	}
}
