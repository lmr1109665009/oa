package com.suneee.platform.service.system;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.system.SysIndexLayoutDao;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.system.SysIndexLayoutDao;
import com.suneee.platform.model.system.SysIndexLayout;

/**
 * <pre>
 * 对象功能:首页布局 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:hugh
 * 创建时间:2015-03-18 17:30:22
 * </pre>
 */
@Service
public class SysIndexLayoutService extends BaseService<SysIndexLayout> {
	@Resource
	private SysIndexLayoutDao dao;

	public SysIndexLayoutService() {
	}

	@Override
	protected IEntityDao<SysIndexLayout, Long> getEntityDao() {
		return dao;
	}

}
