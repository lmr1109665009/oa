package com.suneee.eas.oa.service.user;

import com.suneee.platform.model.system.UserPosition;

import java.util.List;

/**
 * @user 子华
 * @created 2018/8/22
 */
public interface UserPositionService {
    public List<UserPosition> getByAccount(String account);
}
