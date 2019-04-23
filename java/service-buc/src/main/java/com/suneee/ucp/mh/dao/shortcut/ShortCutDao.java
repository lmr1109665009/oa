package com.suneee.ucp.mh.dao.shortcut;

import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.mh.model.shortcut.ShortCut;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ShortCutDao extends UcpBaseDao<ShortCut> {

    @Override
    public Class<?> getEntityClass() {
        // TODO Auto-generated method stub
        return ShortCut.class;
    }

    public List<ShortCut> getCustomShortCutByUserId(Long userId,String enterpriseCode) {
        Map<String,Object> param = new HashMap<>();
        param.put("userId",userId);
        param.put("enterpriseCode",enterpriseCode);
        return this.getBySqlKey("getCustomShortCutByUserId", param);
    }


    /**
     * 保存用户自定义快捷方式
     */
    public int saveCustomShortCut(Map<String, Object> params) {
        return this.update("saveCustomShortCut", params);
    }

    /**
     * 删除用户自定义快捷方式
     */
    public int deleteCustomShortCut(Map<String,Object> map) {
        return this.update("deleteCustomShortCut", map);
    }

    public int deleteCustomShortCutByUserIds(List<Long> userIdList) {
        return this.delBySqlKey("deleteCustomShortCutByUserIds", userIdList);
    }
}
