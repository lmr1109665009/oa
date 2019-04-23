package com.suneee.platform.dao.file;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.file.FileManageLog;
import org.springframework.stereotype.Repository;

/**
 * <pre>
 * 对象功能:文件管理 DAO类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ouxb
 * 创建时间:2014年6月17日21:25:43
 * </pre>
 */
@Repository
public class FileManageLogDao extends BaseDao<FileManageLog> {

	@Override
	public Class getEntityClass() {
		return FileManageLog.class;
	}

}
