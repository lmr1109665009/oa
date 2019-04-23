package com.suneee.eas.oa.service.system;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.system.DicItem;

import java.util.List;

/**
 * 字典项service
 * @user 子华
 * @created 2018/7/31
 */
public interface DicItemService extends BaseService<DicItem> {
    public List<DicItem> listByDicId(Long dicId);

    public void updateSort(Long[] ids);
}
