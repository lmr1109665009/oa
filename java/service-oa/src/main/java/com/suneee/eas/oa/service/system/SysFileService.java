package com.suneee.eas.oa.service.system;

import java.util.List;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.system.SysFile;

/**
 * 系统文件service
 * @user 子华
 * @created 2018/7/31
 */
public interface SysFileService extends BaseService<SysFile> {
	List<SysFile> getByIds(List<Long> fileIds);
}
