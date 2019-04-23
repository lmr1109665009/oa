package com.suneee.core.service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.page.PageBean;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.query.QueryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * 服务基类。
 * 所有的业务实现类均需要从此类继承
 * @author csx
 *
 */
public abstract class GenericService <E, PK extends Serializable>{
	protected Logger logger = LoggerFactory.getLogger(GenericService.class);
	/**
	 * 需要被子类覆盖
	 * @return
	 */
	protected abstract IEntityDao<E,PK> getEntityDao();

	/**
	 * 添加对象
	 * @param entity
	 */
	public void add(E entity)
	{
		getEntityDao().add(entity);
	}

	/**
	 * 根据主键删除对象
	 * @param id
	 */
	public void delById(PK id) throws IOException {
		getEntityDao().delById(id);
	}

	/**
	 * 根据主键批量删除对象
	 * @param ids
	 */
	public void delByIds(PK[] ids) throws IOException {
		if(BeanUtils.isEmpty(ids)) return;
		for (PK p : ids){
			delById(p);
		}
	}

	/**
	 * 修改对象
	 * @param entity
	 */
	public void update(E entity)
	{
		getEntityDao().update(entity);
	}

	/**
	 * 根据主键Id获取对象
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public E getById(PK id)
	{
		return (E) getEntityDao().getById(id);
	}

	/**
	 * 取得分页。
	 * @param statatementName
	 * @param pb
	 * @return
	 */
	public List<E> getList(String statatementName,PageBean pb)
	{
		List<E>  list = getEntityDao().getList(statatementName, pb);
		return list;
	}
	/**
	 * 返回所有记录
	 * @return
	 */
	public List<E> getAll()
	{
		return getEntityDao().getAll();
	}
	
	/**
	 * 按过滤器查询记录列表
	 * @param queryFilter
	 * @return
	 */
	public List<E> getAll(QueryFilter queryFilter){
		return getEntityDao().getAll(queryFilter);
	}

	/**
	 * 获取流程定义分类统计
	 * @param filter
	 * @return
	 */
	public List<?> getAllCountByCate(QueryFilter filter){
		return getEntityDao().getAllCountByCate(filter);
	}
	
	
}
