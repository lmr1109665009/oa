package com.suneee.platform.service.system;

import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.system.SysOfficeTemplateDao;
import com.suneee.platform.model.system.SysOfficeTemplate;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysRole;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.system.SysOfficeTemplateDao;
import com.suneee.platform.model.system.SysOfficeTemplate;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysRole;

/**
 * 对象功能:office模版 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2012-05-25 10:16:16
 */
@Service
public class SysOfficeTemplateService extends BaseService<SysOfficeTemplate>
{
	@Resource
	private SysOfficeTemplateDao dao;
	
	@Resource
	private SysRoleService sysRoleService;

	@Resource
	private SysOrgService sysOrgService;
	
	public SysOfficeTemplateService()
	{
	}
	
	@Override
	protected IEntityDao<SysOfficeTemplate, Long> getEntityDao()
	{
		return dao;
	}

	public List<SysOfficeTemplate> getOfficeTemplateByUserId(Long userId, QueryFilter filter) {
		//直接分配到自己ID
		filter.addFilter("userId", userId);
		
		
		//用户角色查询
		List<SysRole> roles = sysRoleService.getByUserId(userId);
		if(BeanUtils.isNotEmpty(roles) ){
			String roleIds = "";
			for (SysRole sysRole : roles)
			{
				roleIds += sysRole.getRoleId()+",";
			}
			roleIds =roleIds.substring(0, roleIds.length()-1);
			filter.addFilter("roleIds", roleIds);
		}
		
		//用户组织查询
		List<SysOrg> orgs = sysOrgService.getOrgsByUserId(userId);
		if(BeanUtils.isNotEmpty(orgs)){
			String orgIds = "";
			for (SysOrg sysOrg : orgs)
			{
				orgIds += sysOrg.getOrgId()+",";
			}
			orgIds =orgIds.substring(0, orgIds.length()-1);
			filter.addFilter("orgIds", orgIds);
		}
		return dao.getOfficeTemplateByUserId(filter);
	}
}
