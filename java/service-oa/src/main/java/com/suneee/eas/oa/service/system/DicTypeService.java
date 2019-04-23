package com.suneee.eas.oa.service.system;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.system.DicType;

import javax.validation.Valid;

/**
 * 数据字典service
 * @user 子华
 * @created 2018/7/31
 */
public interface DicTypeService extends BaseService<DicType> {

    public DicType getByCode(String code);

    public void saveObj(DicType dicType);

    public void updateObj(DicType dicType);
}
