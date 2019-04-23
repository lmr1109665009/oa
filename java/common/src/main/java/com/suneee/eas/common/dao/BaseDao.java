package com.suneee.eas.common.dao;

import com.suneee.eas.common.component.QueryFilter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BaseDao<T> extends SqlSessionDaoSupport {
    private SqlSessionTemplate sqlSessionTemplate;

    public SqlSessionTemplate getSqlSessionTemplate() {
        return sqlSessionTemplate;
    }

    /**
     * 获取命名空间
     * @return
     */
    protected String getNamespace(){
        return this.getClass().getName();
    }

    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
        sqlSessionTemplate=new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 根据ID查找数据
     * @param id
     * @return
     */
    public T findById(Long id){
        return sqlSessionTemplate.selectOne(getNamespace()+".findById",id);
    }

    /**
     * 保存数据
     * @param model
     * @return
     */
    public int save(T model){
        return sqlSessionTemplate.insert(getNamespace()+".save",model);
    }

    /**
     * 更新数据
     * @param model
     * @return
     */
    public int update(T model){
        return sqlSessionTemplate.update(getNamespace()+".update",model);
    }

    /**
     * 根据ID来删除数据
     * @param id
     * @return
     */
    public int deleteById(Long id){
        return sqlSessionTemplate.delete(getNamespace()+".deleteById",id);
    }

    /**
     * 获取所有数据
     * @return
     */
    public List<T> listAll(){
        return sqlSessionTemplate.selectList(getNamespace()+".listAll");
    }

    /**
     * 根据QueryFilter来创建参数查询列表
     * @param filter
     * @return
     */
    public List<T> getListBySqlKey(QueryFilter filter){
        return sqlSessionTemplate.selectList(getNamespace()+"."+filter.getSqlKey(),filter.getFilters());
    }

    /**
     * 根据QueryFilter来创建参数查询某个对象
     * @param filter
     * @return
     */
    public T getOneBySqlKey(QueryFilter filter){
        return sqlSessionTemplate.selectOne(getNamespace()+"."+filter.getSqlKey(),filter.getFilters());
    }

    /**
     * 查询记录数
     * @param filter
     * @return
     */
    public int getCountBySqlKey(QueryFilter filter){
        return sqlSessionTemplate.selectOne(getNamespace()+"."+filter.getSqlKey(),filter.getFilters());
    }

    /**
     * 根据QueryFilter来更新
     * @param filter
     * @return
     */
    public int updateBySqlKey(QueryFilter filter){
        return sqlSessionTemplate.update(getNamespace()+"."+filter.getSqlKey(),filter.getFilters());
    }

    /**
     * 根据QueryFilter来删除数据
     * @param filter
     * @return
     */
    public int deleteBySqlKey(QueryFilter filter){
        return sqlSessionTemplate.delete(getNamespace()+"."+filter.getSqlKey(),filter.getFilters());
    }
}
