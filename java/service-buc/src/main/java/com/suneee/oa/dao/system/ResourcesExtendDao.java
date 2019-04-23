/**
 * @Title: ResourcesExtendDao.java 
 * @Package com.suneee.oa.dao.system 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.dao.system;

import com.suneee.platform.model.system.Resources;
import com.suneee.ucp.base.dao.UcpBaseDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ResourcesExtendDao 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-01-17 15:17:40 
 *
 */
@Repository
public class ResourcesExtendDao extends UcpBaseDao<Resources>{

	/** (non-Javadoc)
	 * @Title: getEntityClass 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @return 
	 * @see com.suneee.core.db.GenericDao#getEntityClass()
	 */
	@Override
	public Class<Resources> getEntityClass() {
		// TODO Auto-generated method stub
		return Resources.class;
	}
	
	/** 
	 * 根据资源别名获取资源信息
	 * @param resId 资源ID
	 * @param alias 资源别名
	 * @param systemId 系统ID
	 * @return
	 */
	public Resources getByAliasForCheck(Long resId, String alias, Long systemId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("systemId", systemId);
		params.put("alias", alias);
		params.put("resId", resId);
		return (Resources)this.getOne("getByAliasForCheck", params);
	}
	
	/** 
	 * 更新资源是否显示到菜单
	 * @param resId
	 * @param isDisplayInMenu
	 * @return
	 */
	public int updDisplay(Long resId, Short isDisplayInMenu){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("resId", resId);
		params.put("isDisplayInMenu", isDisplayInMenu);
		return this.update("updDisplay", params);
	}

}
