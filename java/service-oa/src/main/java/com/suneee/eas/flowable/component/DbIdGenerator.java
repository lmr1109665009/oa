package com.suneee.eas.flowable.component;

import com.suneee.eas.common.utils.IdGeneratorUtil;
import org.flowable.common.engine.impl.cfg.IdGenerator;
import org.springframework.stereotype.Component;

/**
 * activiti ID生成器
 * @user 子华
 * @created 2018/9/6
 */
@Component
public class DbIdGenerator implements IdGenerator {

    @Override
    public String getNextId() {
        return IdGeneratorUtil.getNextId().toString();
    }
}