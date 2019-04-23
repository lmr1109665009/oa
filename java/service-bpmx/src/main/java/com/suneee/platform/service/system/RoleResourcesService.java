package com.suneee.platform.service.system;


import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.RoleResourcesDao;
import com.suneee.platform.model.system.RoleResources;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.RoleResourcesDao;
import com.suneee.platform.model.system.RoleResources;

/**
 * 对象功能:角色资源映射 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2011-12-08 11:23:15
 */
@Service
public class RoleResourcesService extends BaseService<RoleResources>
{
	@Resource
	private RoleResourcesDao roleResourcesDao;
	@Resource
	private SysRoleService sysRoleService;
	@Resource
	private SubSystemService subSystemService;
	
	
	public RoleResourcesService()
	{
	}
	
	@Override
	protected IEntityDao<RoleResources, Long> getEntityDao()
	{
		return roleResourcesDao;
	}
	
	/**
	 * 添加系统和角色和资源的映射关系。
	 * <pre>
	 * 	1.根据系统id和角色id删除资源映射关系。
	 * 	2.根据角色和系统删除角色和系统的映射关系。
	 * 	3.添加角色和系统的映射关系。
	 * 	4.添加角色和资源的映射关系。
	 * </pre>
	 * @param systemId	系统ID
	 * @param roleId	角色id
	 * @param resIds	资源id数组。
	 * @throws Exception
	 */
	public void update( Long systemId, Long roleId,Long[] resIds, Short fromType) throws Exception{
		//删除
		roleResourcesDao.delByRoleAndSys(systemId, roleId, fromType);
		//添加角色和资源的映射。
		if(systemId>0&&roleId>0&&resIds!=null&&resIds.length>0){
			for(long resId:resIds){
				RoleResources rores=new RoleResources();
				rores.setRoleResId(UniqueIdUtil.genId());
				rores.setResId(resId);
				rores.setSystemId(systemId);
				rores.setRoleId(roleId);
				rores.setFromType(fromType);
				add(rores);
			}
		}
	
	}

	public void saveBatch(Long systemId, Long[] roleIds, Long[] resIds, Short fromType) {
		//删除
		roleResourcesDao.delByRoleAndSysAndRes(systemId, roleIds,resIds);
		//添加角色和资源的映射。
		if(systemId>0 && roleIds!=null && roleIds.length>0 && resIds!=null && resIds.length>0){
			
			for(long resId:resIds){
				for (long roleId:roleIds) {
					RoleResources rores=new RoleResources();
					rores.setRoleResId(UniqueIdUtil.genId());
					rores.setResId(resId);
					rores.setSystemId(systemId);
					rores.setRoleId(roleId);
					rores.setFromType(fromType);
					add(rores);
				}
			}
		}
	}
	
	public void delByRoleIdAndSystemId(Long roleId, Long systemId){
		roleResourcesDao.delByRoleAndSys(systemId, roleId, null);
	}
	
	public void delByResId(Long resId){
		roleResourcesDao.delByResId(resId);
	}
}
