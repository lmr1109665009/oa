/**
 * @Title: SubSystemExtendDao.java 
 * @Package com.suneee.oa.dao.user 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.dao.user;

import com.suneee.platform.model.system.SubSystem;
import com.suneee.ucp.base.dao.UcpBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @ClassName: SubSystemExtendDao 
 * @Description: 子系统SubSystem的扩展DAO类
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-01-12 10:37:57 
 *
 */
@Repository
public class SubSystemExtendDao extends UcpBaseDao<SubSystem>{

	@Override
	public Class<SubSystem> getEntityClass() {
		// TODO Auto-generated method stub
		return SubSystem.class;
	}

}
