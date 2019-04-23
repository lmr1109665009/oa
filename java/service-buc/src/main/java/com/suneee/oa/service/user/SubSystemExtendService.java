/**
 * @Title: SubSystemExtendService.java 
 * @Package com.suneee.oa.service.user 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.service.user;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.platform.model.system.SubSystem;
import com.suneee.oa.dao.user.SubSystemExtendDao;
import com.suneee.ucp.base.service.UcpBaseService;

/**
 * @ClassName: SubSystemExtendService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-01-12 10:39:36 
 *
 */
@Service
public class SubSystemExtendService extends UcpBaseService<SubSystem>{
	@Resource
	private SubSystemExtendDao subSystemExtendDao;
	
	/** (non-Javadoc)
	 * @see com.suneee.core.service.GenericService#getEntityDao()
	 */
	@Override
	protected IEntityDao<SubSystem, Long> getEntityDao() {
		return subSystemExtendDao;
	}

}
