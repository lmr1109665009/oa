/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ColumnDao
 * Author:   lmr
 * Date:     2018/5/4 15:57
 * Description: 栏目dao
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.ucp.mh.dao.customColumn;

import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.mh.model.customColumn.CustomColumnVO;
import org.springframework.stereotype.Repository;

/**
 * 〈一句话功能简述〉<br> 
 * 〈栏目dao〉
 *
 * @author lmr
 * @create 2018/5/4
 * @since 1.0.0
 */
@Repository
public class CustomColumnVODao extends UcpBaseDao<CustomColumnVO> {

    @Override
    public Class getEntityClass() {
        return CustomColumnVO.class;
    }

    public boolean isAliasExisted(String columnAlias) {
        return (Integer) this.getOne("isAliasExisted", columnAlias)>0;
    }


}