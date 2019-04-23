/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: AuthService
 * Author:   lmr
 * Date:     2018/10/9 11:11
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.service.fs;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.fs.Authorization;

import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author lmr
 * @create 2018/10/9
 * @since 1.0.0
 */
public interface AuthService extends BaseService<Authorization> {
    int save(Authorization auth);

    List<Authorization> getByDocId(Long docId);

    List<Authorization> getByOwnerId(Long userId, String ownerType, Long docId);

    void saveList(List<Authorization> auths);

    void deleteByDocId(Long id);

    List<Authorization> getByOwnerIds(List<Long> roleIds, String ownerTypeRole);
}
