/**
 * 对象功能:子系统资源 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2011-12-05 17:00:54
 */
package com.suneee.eas.oa.dao.system;

import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.oa.model.system.Resources;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ResourcesDao extends BaseDao<Resources> {

    /**
     * 获取全部的菜单。
     * @return
     */
    public List<Resources> getSuperMenu(){
        return getSqlSessionTemplate().selectList(getNamespace()+".getSuperMenu");
    }

    /**
     * 根据系统id和当前用户角色(自己和所属于部门的所有角色)
     * @return
     */
    public List<Resources> getNormMenuByAllRole(String rolealias){
        Map<String, Object> p=new HashMap<String, Object>();
        p.put("rolealias", rolealias);
        p.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
        return getSqlSessionTemplate().selectList(getNamespace() + ".getNormMenuByAllRole", p);
    }

    /**
     * 根据系统id和当前用户角色获取当前企业下的资源菜单
     * @return
     */
    public List<Resources> getNormMenuByRole(Long userId){
        return this.getNormMenuByRole(userId, ContextSupportUtil.getCurrentEnterpriseCode());
    }

    /**
     * 获取用户指定企业下的资源菜单
     * @param userId
     * @param enterpriseCode
     * @return
     */
    public List<Resources> getNormMenuByRole(Long userId, String enterpriseCode){
        Map<String, Object> p=new HashMap<String, Object>();
        p.put("userId", userId);
        p.put("enterpriseCode", enterpriseCode);
        return getSqlSessionTemplate().selectList(getNamespace() + ".getNormMenuByRole", p);
    }

    /**
     * 获取资源管理列表
     * @param filter
     * @return
     */
    public List<Resources> getAll(QueryFilter filter){
       return getSqlSessionTemplate().selectList(getNamespace() + "." +filter.getSqlKey(), filter.getFilters());
    }

    /**
     * 根据资源别名获取资源信息
     * @param resId 资源ID
     * @param alias 资源别名
     * @return
     */
    public Resources getByAliasForCheck(Long resId, String alias){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("alias", alias);
        params.put("resId", resId);
        return getSqlSessionTemplate().selectOne(getNamespace() + ".getByAliasForCheck", params);
    }

    /**
     * 更新资源是否显示到菜单
     * @param resId
     * @param isDisplayInMenu
     * @return
     */
    public int updDisplay(Long resId, Short isDisplayInMenu){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("resId", resId);
        params.put("isDisplayInMenu", isDisplayInMenu);
        return getSqlSessionTemplate().update(getNamespace() + ".updDisplay", params);
    }

    /**
     * 根据父id获取资源数据。
     * @param parentId
     * @return
     */
    public List<Resources> getByParentId(Long parentId){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("parentId", parentId);
        return getSqlSessionTemplate().selectList(getNamespace() + ".getByParentId", params);
    }

    /**
     * 更新排序
     * @param resId
     * @param sn
     */
    public void updSn(Long resId, long sn) {
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("resId", resId);
        map.put("sn", sn);
        getSqlSessionTemplate().update(getNamespace() + ".updSn", map);
    }

    /**
     * 获取公开的资源
     * @return
     */
    public List<Resources> getPublicList(){
        return getSqlSessionTemplate().selectList(getNamespace() + ".getPublicList");
    }
}