package com.suneee.eas.common.service;

import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;

import java.util.List;

public interface BaseService<T> {
    /**
     * 根据ID查找数据
     * @param id
     * @return
     */
    public T findById(Long id);

    /**
     * 保存数据
     * @param model
     * @return
     */
    public int save(T model);

    /**
     * 更新数据
     * @param model
     * @return
     */
    public int update(T model);

    /**
     * 根据ID来删除数据
     * @param id
     * @return
     */
    public int deleteById(Long id);

    /**
     * 获取所有数据
     * @return
     */
    public List<T> listAll();

    /**
     * 获取分页数据
     * @param pageNum 当前页数
     * @param pageSize 分页大小
     * @return
     */
    public Pager<T> listPage(int pageNum, int pageSize);

    /**
     * 根据QueryFilter来创建参数查询列表
     * @param filter 查询参数
     * @return
     */
    public Pager<T> getPageBySqlKey(QueryFilter filter);
    public List<T> getListBySqlKey(QueryFilter filter);

    /**
     * 根据QueryFilter来创建参数查询对象
     * @param filter
     * @return
     */
    public T getOneBySqlKey(QueryFilter filter);

    /**
     * 根据QueryFilter来创建参数查询count
     * @param filter
     * @return
     */
    public int getCountBySqlKey(QueryFilter filter);

    /**
     * 根据QueryFilter来更新
     * @param filter
     * @return
     */
    public int updateBySqlKey(QueryFilter filter);

    /**
     * 根据QueryFilter来删除数据
     * @param filter
     * @return
     */
    public int deleteBySqlKey(QueryFilter filter);
}
