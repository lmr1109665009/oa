package com.suneee.platform.service.file;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.file.FileManageLogDao;
import com.suneee.platform.model.file.FileManageLog;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <pre>
 * 对象功能:文件管理 service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ouxb
 * 创建时间:2014年6月17日21:25:43
 * </pre>
 */
@Service
public class FileManageLogService  extends BaseService<FileManageLog> {

	@Resource
    FileManageLogDao dao;

	@Override
	protected IEntityDao<FileManageLog, Long> getEntityDao() {
		return dao;
	}
	
	/**
	 * 增加操作记录
	 * @param fileId
	 * @param operateType
	 */
	public void add(Long fileId,String operateType) {
		FileManageLog fileManageLog = new FileManageLog();
		fileManageLog.setId(UniqueIdUtil.genId());
		fileManageLog.setFileId(fileId);
		fileManageLog.setOperationId(ContextUtil.getCurrentUserId());
		fileManageLog.setOperationDeptId(ContextUtil.getCurrentOrgId());
		fileManageLog.setOperateTime(new Date());
		fileManageLog.setOperateType(operateType);
		super.add(fileManageLog);
	}
	
	
}
