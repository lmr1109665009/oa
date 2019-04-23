/**
 * 对象功能:SYS_ACCEPT_IP Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2012-02-20 17:35:46
 */
package com.suneee.platform.dao.system;


import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysAcceptIp;
import org.springframework.stereotype.Repository;

@Repository
public class SysAcceptIpDao extends BaseDao<SysAcceptIp>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return SysAcceptIp.class;
	}
}