package com.suneee.platform.service.mobile;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.mobile.MobileUserInfoDao;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.mobile.MobileUserInfoDao;
import com.suneee.platform.model.mobile.MobileUserInfo;

/**
 *<pre>
 * 对象功能:MOBILE_USER_INFO Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2013-01-06 11:55:09
 *</pre>
 */
@Service
public class MobileUserInfoService extends BaseService<MobileUserInfo>
{
	@Resource
	private MobileUserInfoDao dao;
	
	public MobileUserInfoService()
	{
	}
	
	@Override
	protected IEntityDao<MobileUserInfo, Long> getEntityDao()
	{
		return dao;
	}
	
}
