package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.SysOrgRoleManageDao;
import com.suneee.platform.model.system.SysOrgRoleManage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 对象功能:组织可以授权的角色范围(用于分级授权) Service类
 * 开发公司:宏天
 * 开发人员:ray
 * 创建时间:2012-11-02 15:03:27
 */
@Service
public class SysOrgRoleManageService extends BaseService<SysOrgRoleManage>
{
	@Resource
	private SysOrgRoleManageDao dao;
	
	public SysOrgRoleManageService()
	{
	}
	
	@Override
	protected IEntityDao<SysOrgRoleManage, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 添加组织可以管理的角色。
	 * 如果已经添加则跳过。
	 * @param orgId
	 * @param roleIds
	 */
	public boolean addOrgRole(Long orgId,String roleIds,Integer grade){
		
		boolean rtn=false;
		String[] aryRoles=roleIds.split(",");
		for(String sRoleId:aryRoles){
			long roleId=Long.parseLong(sRoleId);
			if(dao.isOrgRoleExists(orgId, roleId)){
				continue;
			}
			long id= UniqueIdUtil.genId();
			SysOrgRoleManage orgRole=new SysOrgRoleManage();
			orgRole.setId(id);
			orgRole.setOrgid(orgId);
			orgRole.setRoleid(roleId);
			orgRole.setCanDel(grade);
			dao.add(orgRole);
			rtn=true;
		}
		return rtn;
	}
	
	/**
	 * 
	 * 根据组织id删除可分配权限
	 * @param orgId 
	 * void
	 */
	public void delByOrgId(Long orgId){
		dao.delByOrgId(orgId);
	}
	
}
