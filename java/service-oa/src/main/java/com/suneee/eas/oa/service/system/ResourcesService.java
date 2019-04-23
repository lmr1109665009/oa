package com.suneee.eas.oa.service.system;

import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.system.Resources;
import com.suneee.platform.model.system.SysUser;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * 对象功能:子系统资源 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2011-12-05 17:00:54
 */
public interface ResourcesService extends BaseService<Resources> {

    List<Resources> getSysMenu(SysUser user) throws UnsupportedEncodingException;

    List<Resources> getAll(QueryFilter filter);

    Resources getByAliasForCheck(Long resId, String alias);

    void saveResource(Resources resources);

    void updDisplay(Long resId, Short isDisplayInMenu);

    List<Resources> getByParentId(Long id);

    void sort(Long[] resIds);

    String exportXml(Long[] resId,Map<String,Boolean> map) throws Exception;

    String importXml(InputStream inputStream, Resources parentRes) throws Exception;

    void delByIds(Long[] ids);
}
