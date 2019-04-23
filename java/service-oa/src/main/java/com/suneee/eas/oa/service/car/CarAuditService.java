package com.suneee.eas.oa.service.car;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.car.CarAudit;

import javax.servlet.http.HttpServletRequest;

/**
 * 车辆审批service
 * @user 子华
 * @created 2018/9/4
 */
public interface CarAuditService extends BaseService<CarAudit> {
    public void doAudit(HttpServletRequest request);

    public void deleteByTargetId(Long targetId);

    public void deleteByInstId(String processInstanceId);

    public void doCarAdminAudit(String taskId,String driverId);

    public void doCarDriverOutAudit(String taskId);

    public void doCarDriverBackAudit(String taskId);

    CarAudit findByApplyId(Long applyId);

    public void auditDisAgree(String taskId, int status);

}
