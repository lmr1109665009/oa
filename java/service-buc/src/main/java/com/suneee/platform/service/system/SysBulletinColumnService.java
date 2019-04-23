package com.suneee.platform.service.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.SysBulletinColumnDao;
import com.suneee.platform.model.system.SysBulletinColumn;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class SysBulletinColumnService extends BaseService<SysBulletinColumn>
{
	@Resource
	private SysBulletinColumnDao dao;
	
	public SysBulletinColumnService()
	{
	}
	
	@Override
	protected IEntityDao<SysBulletinColumn,Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 保存 公告栏目 信息
	 * @param sysBulletinColumn
	 */

	public void save(SysBulletinColumn sysBulletinColumn){
		Long id=sysBulletinColumn.getId();
		if(id==null || id==0){
			id= UniqueIdUtil.genId();
			sysBulletinColumn.setId(id);
			this.add(sysBulletinColumn);
		}
		else{
			this.update(sysBulletinColumn);
		}
	}
	
	/**
	 * 获取栏目
	 * @return
	 */
	public List<SysBulletinColumn> getColumn(Long companyId,Boolean isSuperAdmin) {
		return dao.getColumn(companyId,isSuperAdmin);
	}
	
	/**
	 * 获取栏目，不传入公司和是否超级管理员
	 * @return
	 */
	public List<SysBulletinColumn> getColumn(){
		Long companyId = ContextUtil.getCurrentCompanyId();
		boolean isSuperAdmin = ContextUtil.isSuperAdmin();
		return dao.getColumn(companyId,isSuperAdmin);
	}
	
	/**
	 * 验证别名是否冲突。
	 * @param column
	 * @return
	 */
	public int getAmountByAlias(SysBulletinColumn column){
		int rtn=dao.getAmountByAlias(column);
		return rtn;
	}
	
	

}
