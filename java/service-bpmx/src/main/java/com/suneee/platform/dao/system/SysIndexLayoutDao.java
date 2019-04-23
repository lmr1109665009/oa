package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysIndexLayout;

/**
 * <pre>
 * 对象功能:首页布局 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:hugh
 * 创建时间:2015-03-18 17:30:22
 * </pre>
 */
@SuppressWarnings("unchecked")
@Repository
public class SysIndexLayoutDao extends BaseDao<SysIndexLayout> {
	@Override
	public Class<?> getEntityClass() {
		return SysIndexLayout.class;
	}

}