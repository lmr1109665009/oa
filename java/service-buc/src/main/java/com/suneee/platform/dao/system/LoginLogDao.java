package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.LoginLog;

/**
 * <pre>
 * 对象功能:sys_login_log Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-07-01 14:10:56
 * </pre>
 */
@Repository
public class LoginLogDao extends BaseDao<LoginLog> {
	@Override
	public Class<?> getEntityClass() {
		return LoginLog.class;
	}
}