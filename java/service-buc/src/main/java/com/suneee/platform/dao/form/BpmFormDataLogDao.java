package com.suneee.platform.dao.form;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.form.BpmFormDataLog;

/**
 * <pre>
 * 对象功能:在线表单数据日志记录，记录为什么会丢失数据 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ouxb
 * 创建时间:2015-11-30 17:15:23
 * </pre>
 */
@Repository
public class BpmFormDataLogDao extends BaseDao<BpmFormDataLog> {
	@Override
	public Class<?> getEntityClass() {
		return BpmFormDataLog.class;
	}

}