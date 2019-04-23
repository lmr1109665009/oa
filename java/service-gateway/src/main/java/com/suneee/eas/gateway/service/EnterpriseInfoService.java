package com.suneee.eas.gateway.service;

import com.suneee.platform.model.system.SysOrg;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.model.system.Position;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * buc企业信息service
 * @user 子华
 * @created 2018/9/12
 */
public interface EnterpriseInfoService {
    /**
     * 根据企业编码获取企业信息
     * @param enterpriseCode
     * @return
     */
    Enterpriseinfo getByCompCode(String enterpriseCode) throws UnsupportedEncodingException;

    /**
     * 根据用户ID获取企业列表
     * @param userId
     * @return
     */
    List<Enterpriseinfo> getByUserId(Long userId) throws UnsupportedEncodingException;

    /**
     * 根据用户ID获取组织列表
     * @param userId
     * @return
     */

    Position getOrgByUserId(Long userId) throws UnsupportedEncodingException;
}
