/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ColumnService
 * Author:   lmr
 * Date:     2018/5/4 15:55
 * Description: 栏目service
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.ucp.mh.service.customColumn;

import com.suneee.core.db.IEntityDao;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.mh.dao.customColumn.CustomColumnDao;
import com.suneee.ucp.mh.dao.customColumn.CustomColumnVODao;
import com.suneee.ucp.mh.model.customColumn.CustomColumnVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈栏目service〉
 *
 * @author lmr
 * @create 2018/5/4
 * @since 1.0.0
 */
@Service
public class CustomColumnVOService extends UcpBaseService<CustomColumnVO> {
    @Resource
    private CustomColumnVODao customColumnVODao;

    @Resource
    private CustomColumnDao customColumnDao;

    @Override
    protected IEntityDao<CustomColumnVO, Long> getEntityDao() {
        return customColumnVODao;
    }

    public CustomColumnVO getByAlias(String alias,String type) {
        Map<String,String> param = new HashMap<>();
        param.put("columnAlias",alias);
        param.put("columnType",type);
        return customColumnVODao.getUnique("getByAlias",param);
    }

    public boolean isAliasExisted(String name) {
        return customColumnVODao.isAliasExisted(name);
    }

    public CustomColumnVO getDefaultByType(String alias,String type) {
        Map<String,String> param = new HashMap<>();
        param.put("columnAlias",alias);
        param.put("columnType",type);
        return customColumnVODao.getUnique("getDefaultByType",param);
    }

    public void delByOwner(Long owner,String columnAlias,String enterpriseCode) {
        List<Long> columnIds = this.getByALiasAndOwner(owner,columnAlias,enterpriseCode);
        for (Long columnId: columnIds) {
            customColumnDao.delByColumnId(columnId);
        }
        Map<String,Object> param = new HashMap<>();
        param.put("columnAlias",columnAlias);
        param.put("owner",owner);
        param.put("enterpriseCode",enterpriseCode);
        customColumnVODao.delBySqlKey("delByOwner",param);
    }

    public List getByALiasAndOwner(Long owner, String columnAlias,String enterpriseCode ) {
        Map<String,Object> param = new HashMap<>();
        param.put("columnAlias",columnAlias);
        param.put("owner",owner);
        param.put("enterpriseCode",enterpriseCode);
        return customColumnVODao.getBySqlKey("getByALiasAndOwner",param);
    }
}