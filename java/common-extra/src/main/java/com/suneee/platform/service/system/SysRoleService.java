package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.model.system.SysRole;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 对象功能:系统角色表 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2011-11-18 16:24:10
 */
@Service
public class SysRoleService extends BaseService<SysRole>
{

	@Override
	protected IEntityDao<SysRole, Long> getEntityDao() {
		return null;
	}

	public List<String> getRolesByUserIdAndOrgId(Long userId, Long orgId){
		List<String> totalRoles=new ArrayList<String>();
		//根据用户获取
		return totalRoles;
	}
}
