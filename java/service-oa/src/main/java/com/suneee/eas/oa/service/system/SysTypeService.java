package com.suneee.eas.oa.service.system;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.system.SysType;

/**
 * 系统分类service
 * @user 子华
 * @created 2018/7/31
 */
public interface SysTypeService extends BaseService<SysType> {

    public ResponseMessage saveType(SysType sysType);

    public ResponseMessage updateType(SysType sysType, SysType oldType);

    public void updateSort(Long[] ids);
}
