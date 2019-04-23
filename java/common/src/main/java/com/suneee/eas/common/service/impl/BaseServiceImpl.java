package com.suneee.eas.common.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.common.service.BaseService;

import java.util.List;

public class BaseServiceImpl<T> implements BaseService<T> {
    private BaseDao<T> baseDao;

    public void setBaseDao(BaseDao<T> baseDao) {
        this.baseDao = baseDao;
    }

    public T findById(Long id) {
        if (id==null){
            return null;
        }
        return baseDao.findById(id);
    }

    public int save(T model) {
        if (model==null){
            return 0;
        }
        return baseDao.save(model);
    }

    public int update(T model) {
        if (model==null){
            return 0;
        }
        return baseDao.update(model);
    }

    public int deleteById(Long id) {
        if (id==null){
            return 0;
        }
        return baseDao.deleteById(id);
    }

    public List<T> listAll() {
        return baseDao.listAll();
    }

    @Override
    public Pager<T> listPage(int pageNum, int pageSize) {
        Page<T> pageObj= PageHelper.startPage(pageNum,pageSize);
        listAll();
        return toPaper(pageObj);
    }

    @Override
    public Pager<T> getPageBySqlKey(QueryFilter filter) {
        Integer pageNum= filter.getFilters().get("pageNum")==null?1:Integer.valueOf((String) filter.getFilters().get("pageNum"));
        Integer pageSize= filter.getFilters().get("pageSize")==null?20:Integer.valueOf((String) filter.getFilters().get("pageSize"));
        Page<T> pageObj= PageHelper.startPage(pageNum,pageSize);
        getListBySqlKey(filter);
        return toPaper(pageObj);
    }

    @Override
    public List<T> getListBySqlKey(QueryFilter filter) {
        return baseDao.getListBySqlKey(filter);
    }

    @Override
    public T getOneBySqlKey(QueryFilter filter) {
        return baseDao.getOneBySqlKey(filter);
    }

    @Override
    public int getCountBySqlKey(QueryFilter filter) {
        return baseDao.getCountBySqlKey(filter);
    }

    @Override
    public int updateBySqlKey(QueryFilter filter) {
        return baseDao.updateBySqlKey(filter);
    }

    @Override
    public int deleteBySqlKey(QueryFilter filter) {
        return baseDao.deleteBySqlKey(filter);
    }

    /**
     * 转换成分页数据对象
     * @param pageObj
     * @return
     */
    protected Pager<T> toPaper(Page<T> pageObj){
        Pager<T> pager=new Pager<T>();
        pager.setTotal(pageObj.getTotal());
        pager.setPages(pageObj.getPages());
        pager.setPageNum(pageObj.getPageNum());
        pager.setPageSize(pageObj.getPageSize());
        pager.setStartRow(pageObj.getStartRow());
        pager.setEndRow(pageObj.getEndRow());
        pager.setList(pageObj.getResult());
        return pager;
    }
}
