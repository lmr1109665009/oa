package com.suneee.platform.service.bpm;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.bpm.MonGroupDao;
import com.suneee.platform.dao.bpm.MonGroupItemDao;
import com.suneee.platform.dao.bpm.MonOrgRoleDao;
import com.suneee.platform.model.bpm.MonGroup;
import com.suneee.platform.model.bpm.MonGroupItem;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *<pre>
 * 对象功能:监控分组 Service类
 * 开发公司:SF
 * 开发人员:xxx
 * 创建时间:2013-06-08 11:14:50
 *</pre>
 */
@Service
public class MonGroupService extends BaseService<MonGroup>
{
	@Resource
	private MonGroupDao dao;
	
	@Resource
	private MonGroupItemDao monGroupItemDao;
	
	@Resource
	private MonOrgRoleDao monOrgRoleDao;
	
	
	public MonGroupService()
	{
	}
	
	@Override
	protected IEntityDao<MonGroup, Long> getEntityDao()
	{
		return dao;
	}
	
	private void delByPk(Long id){
		monGroupItemDao.delByMainId(id);
	}
	
	public void delAll(Long[] lAryId) {
		for(Long id:lAryId){	
			delByPk(id);
			dao.delById(id);	
			monOrgRoleDao.delByGroupId(id);
		}	
	}
	
	public void addAll(MonGroup monGroup) throws Exception{
		add(monGroup);
		addSubList(monGroup);
	}
	
	public void updateAll(MonGroup monGroup) throws Exception{
		update(monGroup);
		delByPk(monGroup.getId());
		addSubList(monGroup);
	}
	
	public void addSubList(MonGroup monGroup) throws Exception{
		List<MonGroupItem> monGroupItemList=monGroup.getMonGroupItemList();
		if(BeanUtils.isNotEmpty(monGroupItemList)){
			for(MonGroupItem monGroupItem:monGroupItemList){
				monGroupItem.setGroupid(monGroup.getId());
				monGroupItem.setId(UniqueIdUtil.genId());
				monGroupItemDao.add(monGroupItem);
			}
		}
	}
	
	public List<MonGroupItem> getMonGroupItemList(Long id) {
		return monGroupItemDao.getByMainId(id);
	}
	
	
	
}
