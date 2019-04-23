package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.OrgAuthDao;
import com.suneee.platform.model.system.AuthRole;
import com.suneee.platform.model.system.OrgAuth;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *<pre>
 * 对象功能:SYS_ORG_AUTH Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:miao
 * 创建时间:2014-08-08 10:19:21
 *</pre>
 */
@Service
public class OrgAuthService extends BaseService<OrgAuth>
{
	@Resource
	private OrgAuthDao dao;
	@Resource
	private AuthRoleService authRoleService;
	
	
	public OrgAuthService()
	{
	}
	
	@Override
	protected IEntityDao<OrgAuth, Long> getEntityDao()
	{
		return dao;
	}

	public void add(OrgAuth orgAuth, Long[] roleIds) {
		checkOrgAuthIsExist(orgAuth.getUserId(), orgAuth.getOrgId());
		this.add(orgAuth);
		saveAuthRoles(roleIds,orgAuth.getId());
		
	}
	private void checkOrgAuthIsExist(Long userId, Long orgId) {
		if (dao.checkOrgAuthIsExist(userId,orgId)) {
			throw new RuntimeException("当前组织的分级管理员已经存在，请勿重复添加！");
		}
	}
	/**
	 * 先删除后新增
	 * @param roleIds
	 * @param authId
	 */
	private void saveAuthRoles(Long[] roleIds, Long authId) {
		authRoleService.delByAuthId(authId);
		if(BeanUtils.isEmpty(roleIds)) return;
		
		for(long roleId : roleIds){
			authRoleService.add(new AuthRole(UniqueIdUtil.genId(),authId,roleId));
		}
	}

	public void update(OrgAuth orgAuth, Long[] roleIds) {
		this.update(orgAuth);
		saveAuthRoles(roleIds,orgAuth.getId());
	}
	
	@Override
	public void delById(Long id) throws IOException {
		authRoleService.delByAuthId(id);
		super.delById(id);
	}
	/**
	 * 通过用户获取所有授权的组
	 */
	public List<OrgAuth> getByUserId(long userId) {
		List<OrgAuth> groupAuthList = dao.getByUserId(userId);
		List<OrgAuth> authList = new ArrayList<OrgAuth>();		
		for(OrgAuth auth : groupAuthList){
			boolean isChild = false;
			for(OrgAuth groupAuth : groupAuthList){
				if((auth.getId()!= groupAuth.getId()) && auth.getDimId().equals(groupAuth.getDimId())){				
						if(StringUtil.isNotEmpty(groupAuth.getOrgPath())&&StringUtil.isNotEmpty(auth.getOrgPath())){
							if(auth.getOrgPath().startsWith(groupAuth.getOrgPath())){
								isChild = true;
							}
						}					
				}					
			}
			if(StringUtil.isNotEmpty(auth.getOrgPath())){
			   if(!isChild)authList.add(auth); 
			}
		}		
		return authList;
	}
	/**
	 * 通过用户，和组织id 获取他的权限
	 * @param orgId
	 * @param userId
	 * @return
	 */
	public OrgAuth getUserIdDimId(Long dimId, Long userId) {
		return dao.getUserIdDimId(dimId,userId);
	}

	public boolean checkIsExist(Long userId, Long orgId) {
		return dao.checkOrgAuthIsExist(userId, orgId);
	}


	public OrgAuth getByUserIdAndOrgId(long userId, long orgId) {
		return dao.getByUserIdAndOrgId(userId,orgId);
	}
	
	
	public void delByUserId(Long userId){
		dao.delByUserId(userId);
	}
	
	public void delByOrgId(Long orgId){
		dao.delByOrgId(orgId);
	}

	
	
}
