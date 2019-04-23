package com.suneee.eas.oa.service;

import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.Demo;

import java.util.List;

/**
 * @user 子华
 * @created 2018/7/31
 */
public interface DemoService extends BaseService<Demo> {

    public void testTx(Demo demo);

    public void saveDemo(Demo demo);

    public List<Demo> listFilter(QueryFilter filter);

}
