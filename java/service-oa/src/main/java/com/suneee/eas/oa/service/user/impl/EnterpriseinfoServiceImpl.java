package com.suneee.eas.oa.service.user.impl;

import com.suneee.eas.oa.service.user.EnterpriseinfoService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 企业信息service
 * @user 子华
 * @created 2018/8/22
 */
@Service
public class EnterpriseinfoServiceImpl implements EnterpriseinfoService {
    /**
     * 查询用户所属企业的企业编码与集团编码键值对：Map<企业编码，集团编码>
     * @param curUserId 用户ID
     * @return
     */
    @Override
    public Map<String,String> getCodeMapByUserId(Long curUserId) {
        return null;
    }
}
