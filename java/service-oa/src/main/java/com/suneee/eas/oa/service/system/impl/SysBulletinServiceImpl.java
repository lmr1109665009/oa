package com.suneee.eas.oa.service.system.impl;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.oa.dao.system.SysBulletinDao;
import com.suneee.eas.oa.model.system.SysBulletin;
import com.suneee.eas.oa.service.system.SysBulletinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysBulletinServiceImpl extends BaseServiceImpl<SysBulletin> implements SysBulletinService {


	private SysBulletinDao dao;

	@Autowired
	public void setDao(SysBulletinDao dao) {
		this.dao = dao;
		setBaseDao(dao);
	}

	@Override
	public void delByIds(Long[] ids) {
		for(Long id:ids){
			this.deleteById(id);
		}
	}

	/**
	 * 添加数据到公告组织表
	 * @param bulletinId
	 * @param orgIds
	 */
	@Override
	public void addToBulletinOrg(Long bulletinId,String orgIds){
		String[] orgIdList = orgIds.split(",");
		for(String orgId:orgIdList){
			dao.addToBulletinOrg(bulletinId,orgId);
		}
	}

	/**
	 * 根据公告id删除中间表的数据
	 * @param bulletinId
	 */
	public void dellFromBulletinOrgByBulletin(Long bulletinId){
		dao.dellFromBulletinOrgByBulletin(bulletinId);
	}

}
