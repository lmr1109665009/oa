package com.suneee.oa.service.user;
 
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.PinyinUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.system.SubSystem;
import com.suneee.platform.model.system.SysRole;
import com.suneee.platform.model.system.UserRole;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.RoleResourcesService;
import com.suneee.platform.service.system.SysRoleService;
import com.suneee.platform.service.system.UserRoleService;
import com.suneee.oa.dao.user.SysRoleExtendDao;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.service.UcpBaseService;
 
 /**
  * 角色信息SysRole扩展Service类
 * @author xiongxianyun
 *
 */
@Service
public class SysRoleExtendService extends UcpBaseService<SysRole>  {
	@Resource
	private SysRoleExtendDao sysRoleExtendDao;
	@Resource
	private SysRoleService sysRoleService;
	@Resource
	private SubSystemExtendService subSystemExtendService;
	@Resource
	private UserRoleService userRoleService;
	@Resource
	private RoleResourcesService roleResourcesService;
	
	@Override
	protected IEntityDao<SysRole, Long> getEntityDao() {
		return sysRoleExtendDao;
	}
	
	/** 
	 * 获取所有角色分类名称
	 * @return
	 */
	public List<String> getAllCategory(QueryFilter filter){
		return sysRoleService.getDistinctCategory(filter);
	}
	
	
	/** 
	 * 根据角色名称查询角色信息
	 *   
	 * @param roleName
	 * @param roleId
	 * @param systemId
	 * @param enterpriseCode
	 * @return
	 */
	public SysRole getByRoleName(String roleName, Long roleId, Long systemId, String enterpriseCode){
		return sysRoleExtendDao.getByRoleName(roleName, roleId, systemId, enterpriseCode);
	}
	
	/** 
	 * 保存角色信息
	 * @param sysRole
	 */
	public void save(SysRole sysRole){
		if(sysRole == null){
			return;
		}
		// 设置角色别名
		StringBuffer alias =new StringBuffer( PinyinUtil.getPinYinHeadCharFilter(sysRole.getRoleName()));
		if(StringUtils.isNotBlank(sysRole.getEnterpriseCode())){
			alias.insert(0, Constants.SEPARATOR_UNDERLINE);
			alias.insert(0, sysRole.getEnterpriseCode().toLowerCase());
		}
		SubSystem system = subSystemExtendService.getById(sysRole.getSystemId());
		if(system != null && StringUtils.isNotBlank(system.getAlias())){
			alias.insert(0, Constants.SEPARATOR_UNDERLINE);
			alias.insert(0, system.getAlias());
		}
		sysRole.setAlias(this.getUniqueAlias(alias.toString(), sysRole.getRoleId(), 0));
		// 设置是否允许删除，默认为1
		sysRole.setAllowDel((short)1);
		// 设置是否允许编辑，默认为1
		sysRole.setAllowEdit((short)1);
		
		if(sysRole.getRoleId() == null){
			sysRole.setRoleId(UniqueIdUtil.genId());
			this.add(sysRole);
		} else {
			this.update(sysRole);
		}
	}

	public void delByIds(Long[] roleIds){
		if(roleIds == null || roleIds.length == 0){
			return;
		}
		StringBuffer failedRole = new StringBuffer();
		List<UserRole> userRoleList = null;
		SysRole sysRole = null;
		for(Long roleId : roleIds){
			// 查询角色下关联的用户
			userRoleList = userRoleService.getUserRoleByRoleId(roleId);
			// 当角色下关联了用户时
			if(!userRoleList.isEmpty()){
				sysRole = this.getById(roleId);
				if(sysRole != null){
					failedRole.append(sysRole.getRoleName()).append("、");
				}
				continue;
			}
			// 当角色下没有关联用户时，删除角色
			this.delById(roleId);
		}
		
		String content = failedRole.toString();
		if(content.length() != 0){
			content = content.substring(0, content.lastIndexOf("、"));
		}
		MessageUtil.addMsg(content);
	}
	public void delById(Long roleId){
		if(roleId == null){
			return;
		}
		SysRole sysRole = this.getById(roleId);
		if(sysRole == null){
			return;
		}
		// 删除角色信息
		sysRoleExtendDao.delById(roleId);
		// 删除用户角色关系
		userRoleService.delByRoleId(roleId);
		// 删除角色资源关系
		roleResourcesService.delByRoleIdAndSystemId(roleId, sysRole.getSystemId());
	}
	
	/** 
	 * 角色批量禁用/启用
	 * @param roleIds
	 * @param enabled
	 */
	public void updEnabled(Long[] roleIds, Short enabled){
		if(roleIds == null || enabled == null){
			return;
		}
		for(Long roleId : roleIds){
			this.updEnabled(roleId, enabled);
		}
	}
	
	/** 
	 * 角色禁用/启用
	 * @param roleId
	 * @param enabled
	 */
	public void updEnabled(Long roleId, Short enabled){
		sysRoleExtendDao.updEnabled(roleId, enabled);
	}
	
	/** 
	 * 获取角色信息列表（包含禁用）
	 * @param queryFilter
	 * @return
	 */
	public List<SysRole> getRoleList(QueryFilter queryFilter){
		return sysRoleService.getRoleList(queryFilter);
	}
	
	/** 
	 * 获取唯一角色别名
	 * @param alias
	 * @param roleId
	 * @param index
	 * @return
	 */
	private String getUniqueAlias(String alias, Long roleId, int index){
		String tmpAlias = alias;
		if(index > 0){
			tmpAlias = alias + index;
		}
		
		SysRole sysRole = sysRoleExtendDao.getByAlias(tmpAlias, roleId);
		if(sysRole != null ){
			tmpAlias = getUniqueAlias(alias, roleId, index++);
		}
		return tmpAlias;
	}
}