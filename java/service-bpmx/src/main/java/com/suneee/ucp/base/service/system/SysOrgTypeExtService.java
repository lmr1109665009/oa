/**
 * 
 */
package com.suneee.ucp.base.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.model.system.SysOrgType;
import com.suneee.platform.service.system.SysOrgTypeService;
import com.suneee.ucp.base.dao.system.SysOrgTypeExtDao;
import com.suneee.ucp.base.service.UcpBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Administrator
 *
 */
@Service
public class SysOrgTypeExtService extends UcpBaseService<SysOrgType>{
	@Resource
	private SysOrgTypeExtDao sysOrgTypeExtDao;
	
	@Resource
	private SysOrgTypeService sysOrgTypeService;
	
	@Override
	protected IEntityDao<SysOrgType, Long> getEntityDao() {
		return sysOrgTypeExtDao;
	}

	/**
	 * 根据类型名称和维度ID查询类型信息
	 * @param name
	 * @param demId
	 * @return
	 */
	public Long getOrgTypeIdByNameAndDemId(String name, Long demId){
		// 根据类型名称和维度ID查询类型信息
		SysOrgType sysOrgTypeDb = sysOrgTypeExtDao.getByNameAndDemId(name, demId);
		// 若存在则直接返回类型ID
		if(null != sysOrgTypeDb){
			return sysOrgTypeDb.getId();
		}
		
		// 类型不存在时，先创建该类型，创建成功后返回类型ID
		Long orgTypeId = UniqueIdUtil.genId();
		SysOrgType orgType = new SysOrgType();
		orgType.setId(orgTypeId);
		orgType.setDemId(demId);
		orgType.setName(name);
		Integer maxLevel = sysOrgTypeService.getMaxLevel(demId);
		maxLevel++;
		orgType.setLevels(maxLevel.longValue());
		add(orgType);
		return orgTypeId;
	}
}
