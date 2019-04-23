package com.suneee.eas.gateway.service;

import com.suneee.ucp.base.model.system.Position;

import java.io.UnsupportedEncodingException;

/**
 * buc岗位信息service
 * @user ouyang
 * @created 2018/9/18
 */
public interface PositionService {

    /**
     * 根据用户id获取主岗位。
     * @param userId
     * @return
     */
    public Position getPrimaryPositionByUserId(Long userId) throws UnsupportedEncodingException;;

}
