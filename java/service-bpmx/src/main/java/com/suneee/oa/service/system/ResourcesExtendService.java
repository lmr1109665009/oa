/**
 * @Title: ResourcesExtendService.java 
 * @Package com.suneee.oa.service.system 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.service.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.dao.system.ResourcesExtendDao;
import com.suneee.platform.model.system.Resources;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.ResourcesService;
import com.suneee.platform.service.system.ResourcesUrlService;
import com.suneee.platform.service.system.RoleResourcesService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.service.UcpBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName: ResourcesExtendService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-01-17 15:14:02 
 *
 */
@Service
public class ResourcesExtendService extends UcpBaseService<Resources>{
	@Resource
	private ResourcesExtendDao resourcesExtendDao;
	@Resource
	private ResourcesService resourcesService;
	@Resource
	private RoleResourcesService roleResourcesService;
	@Resource
	private ResourcesUrlService resourcesUrlService;
	
	/** (non-Javadoc)
	 * @Title: getEntityDao 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @return 
	 * @see com.suneee.core.service.GenericService#getEntityDao()
	 */
	@Override
	protected IEntityDao<Resources, Long> getEntityDao() {
		return resourcesExtendDao;
	}
	
	/** 
	 * 根据资源别名获取资源信息
	 * @param resId 资源ID
	 * @param alias 资源别名
	 * @param systemId 系统ID
	 * @return
	 */
	public Resources getByAliasForCheck(Long resId, String alias, Long systemId){
		return resourcesExtendDao.getByAliasForCheck(resId, alias, systemId);
	} 
	
	/** 
	 * 保存资源信息
	 * @param resources
	 */
	public void save(Resources resources){
		if(resources == null){
			return;
		}
		// 设置资源来源
		resources.setFromType(Resources.FROMTYPE_CLIENT);
		// 设置是否有子节点（1-是，0-否），默认有子节点
		resources.setIsFolder(Resources.IS_FOLDER_Y);
		// 设置默认图标
		resources.setIcon(Resources.ICON_DEFAULT_FOLDER);
		// 设置是否展开（1-是，0-否），默认不展开
		resources.setIsOpen(Resources.IS_OPEN_N);
		// 设置是否打开新窗口（1-是，0-否），默认不打开新窗口
		resources.setIsNewOpen(Resources.IS_NEWOPEN_N);
		// 设置子系统
		Long systemId = resources.getSystemId();
		if(systemId == null){
			resources.setSystemId(1L);
		}
		// 设置资源路径
		Long resId = resources.getResId();
		if(resId == null){
			resId = UniqueIdUtil.genId();
		}
		// 设置父资源ID，当没有父资源ID时默认为0
		if(resources.getParentId() == null){
			resources.setParentId(0L);
		}
		Resources parentResources = this.getById(resources.getParentId());
		if(parentResources == null){
			resources.setPath(resId.toString());
		} else {
			resources.setPath(parentResources.getPath() + Constants.SEPARATOR_COLON + resId.toString());
		}
		
		// 新增
		if(resources.getResId() == null){
			resources.setResId(resId);
			// 设置排序字段，默认为1
			if(resources.getSn() == null){
				resources.setSn(1);
			}
			this.add(resources);
		} 
		// 修改
		else {
			this.update(resources);
		}
	}
	
	/** (non-Javadoc)
	 * @Title: delById 
	 * @Description: 删除资源，同时删除该资源下的所有子资源
	 * @param resId 
	 * @see com.suneee.core.service.GenericService#delById(java.io.Serializable)
	 */
	@Override
	public void delById(Long resId){
		if(resId == null){
			return;
		}
		// 获取资源下的子资源
		List<Resources> resourcesList = resourcesService.getByParentId(resId, Resources.FROMTYPE_CLIENT);
		for(Resources resources : resourcesList){
			// 删除资源下的子资源
			this.delById(resources.getResId());
		}
		
		// 删除角色资源关系
		roleResourcesService.delByResId(resId);
		// 删除关联的URL
		resourcesUrlService.delByResId(resId);
		// 删除资源
		resourcesExtendDao.delById(resId);
	}

	/** 
	 * 资源排序
	 * @param resIds
	 */
	public void sort(Long[] resIds){
		if(resIds == null){
			return;
		}
		int length = resIds.length;
		for(int i = 0; i < length; i++){
			resourcesService.updSn(resIds[i], i + 1);
		}
	}
	
	/** 
	 * 更新是否显示到菜单
	 * @param resId
	 * @param isDisplayInMenu
	 */
	public void updDisplay(Long resId, Short isDisplayInMenu){
		resourcesExtendDao.updDisplay(resId, isDisplayInMenu);
	}
	
	/** 
	 * 获取用户当前企业下的资源菜单
	 * @param systemId
	 * @param sysUser
	 * @param fromType
	 * @return
	 */
	public List<Resources> getRoleResources(Long systemId, SysUser sysUser, Short fromType){
		return this.getRoleResources(systemId, sysUser, fromType, ContextSupportUtil.getCurrentEnterpriseCode());
	}
	
	/** 
	 * 获取用户指定企业下的资源菜单
	 * @param systemId
	 * @param sysUser
	 * @param fromType
	 * @param enterpriseCode
	 * @return
	 */
	public List<Resources> getRoleResources(Long systemId, SysUser sysUser, Short fromType, String enterpriseCode){
		List<Resources> resourcesList = null;
		// 如果是超级管理员，获取系统所有资源
		if(ContextUtil.isSuperAdmin(sysUser)){
			resourcesList = resourcesService.getSuperMenu(systemId, fromType);
		} else {
			resourcesList = resourcesService.getNormMenuByRole(systemId, sysUser.getUserId(), fromType, enterpriseCode);
		}
		return resourcesList;
	}
}
