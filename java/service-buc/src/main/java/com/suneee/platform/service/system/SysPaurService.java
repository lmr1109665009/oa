package com.suneee.platform.service.system;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.system.SysPaurDao;
import com.suneee.platform.model.system.SysPaur;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.system.SysPaurDao;
import com.suneee.platform.model.system.SysPaur;

/**
 *<pre>
 * 对象功能:SYS_PAUR Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-12-24 14:41:59
 *</pre>
 */
@Service
public class SysPaurService extends BaseService<SysPaur>
{
	@Resource
	private SysPaurDao dao;
	
	public SysPaurService()
	{
	}
	
	@Override
	protected IEntityDao<SysPaur, Long> getEntityDao()
	{
		return dao;
	}
	/**
	 * 根据用户id和资源别名获取个性资源设置记录
	 * @param userId
	 * @param aliasName
	 * @return
	 */
	public SysPaur getByUserAndAlias(Long userId, String aliasName){
		if(userId==null || aliasName==null)  return null;
		return dao.getByUserAndAlias(userId, aliasName);
	}
	
	/**
	 * 取当前用户皮肤设置
	 * @return
	 */
	public  String getCurrentUserSkin(Long userId){
		String skinStyle="default";		
		SysPaur skinSysPaur=dao.getByUserAndAlias(userId, "skin");
		if(skinSysPaur==null){
			skinSysPaur=dao.getByUserAndAlias(0L, "skin");//个性设置空的话取系统默认设置
		}
		if(skinSysPaur!=null && skinSysPaur.getPaurvalue()!=null){
			skinStyle=skinSysPaur.getPaurvalue();
		}
		return skinStyle;
	}
}
