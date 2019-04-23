package com.suneee.eas.oa.service.system;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.system.SysBulletin;

/**
 * @Description:
 * @Author: kaize
 * @Date: 2018/8/22 11:03
 */
public interface SysBulletinService extends BaseService<SysBulletin> {

	/**
	 * 添加数据到公告组织表
	 * @param bulletinId
	 * @param orgIds
	 */
	void addToBulletinOrg(Long bulletinId,String orgIds);

	/**
	 * 根据公告id删除中间表的数据
	 * @param bulletinId
	 */
	void dellFromBulletinOrgByBulletin(Long bulletinId);

	/**
	 * 根据id删除公告
	 * @param ids
	 */
	void delByIds(Long[] ids);
}
