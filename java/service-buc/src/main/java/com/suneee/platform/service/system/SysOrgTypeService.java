package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.system.SysOrgTypeDao;
import com.suneee.platform.model.system.SysOrgType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 对象功能:组织结构类型 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-11-27 09:55:21
 */
@Service
public class SysOrgTypeService extends BaseService<SysOrgType>
{
	@Resource
	private SysOrgTypeDao dao;
	
	public SysOrgTypeService()
	{
	}
	
	@Override
	protected IEntityDao<SysOrgType, Long> getEntityDao()
	{
		return dao;
	}
	
	public Integer getMaxLevel(Long demId) {
		return dao.getMaxLevel(demId);
	}
	/**
	 * 根据维度Id获取该维度下所有类型。
	 * @param demId
	 * @return
	 */
	public List<SysOrgType> getByDemId(long demId){
		return dao.getByDemId(demId);
	}
}
