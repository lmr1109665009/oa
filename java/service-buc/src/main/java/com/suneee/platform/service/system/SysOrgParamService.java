package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.dao.system.SysOrgParamDao;
import com.suneee.platform.dao.system.SysParamDao;
import com.suneee.platform.model.system.SysOrgParam;
import com.suneee.platform.model.system.SysParam;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 对象功能:组织参数属性 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2012-02-24 10:04:50
 */
@Service
public class SysOrgParamService extends BaseService<SysOrgParam>
{
	@Resource
	private SysOrgParamDao sysOrgParamDao;
	@Resource
	private SysParamDao sysParamDao;
	
	public SysOrgParamService()
	{
	}
	
	@Override
	protected IEntityDao<SysOrgParam, Long> getEntityDao()
	{
		return sysOrgParamDao;
	}
	
	/**
	 * 添加组织参数。
	 * @param orgId			组织id
	 * @param valueList		组织属性列表。
	 */
	public void add(long orgId,List<SysOrgParam> valueList){

		sysOrgParamDao.delByOrgId(orgId);
		if(valueList==null||valueList.size()==0)return;
		for(SysOrgParam p:valueList){
			sysOrgParamDao.add(p);
		}
	
	}

	public List<SysOrgParam> getByOrgId(Long orgId){
		List<SysOrgParam>list= sysOrgParamDao.getByOrgId(orgId);
		return list;
	}
	public List<SysOrgParam> getAll(Map map){
		return sysOrgParamDao.getAll(map);
	}
	public List<SysOrgParam> getListByOrgId(Long orgId) {
		
		List<SysOrgParam>list= sysOrgParamDao.getByOrgId(orgId);
		if(list.size()>0){
			for(SysOrgParam param:list){
				long paramId=param.getParamId();
				SysParam sysParam=sysParamDao.getById(paramId);
				if(BeanUtils.isNotEmpty(sysParam)){
					param.setParamName(sysParam.getParamName());
				}
			}
		}
		return list;
	}
	
	/**
	 * 根据部门ID或属性Key获取对象
	 * @param paramKey
	 * @param orgId
	 * @return
	 */
	public SysOrgParam getByParamKeyAndOrgId(String paramKey,Long orgId){
		return sysOrgParamDao.getByParamKeyAndOrgId(paramKey,orgId);
	}
}
