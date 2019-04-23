package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysObjRights;
import com.suneee.platform.model.system.SysPopupRemind;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 对象功能:sys_popup_remind Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-03-18 11:36:25
 * </pre>
 */
@Repository
public class SysPopupRemindDao extends BaseDao<SysPopupRemind> {
	@Override
	public Class<?> getEntityClass() {
		return SysPopupRemind.class;
	}
	
	public List<SysPopupRemind> getByUser(Map<String, List<Long>> map){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("relationMap", map);//基本固定
		params.put("objType", SysObjRights.RIGHT_TYPE_POPUP_MSG);
		List<SysPopupRemind> sprs = this.getBySqlKey("getByUser",params);
		return sprs;
	}
	
	/**
	 * 获取所有的提醒列表(启用的)
	 */
	public SysPopupRemind getByIds(Long id){
		SysPopupRemind spr = this.getUnique("getByIds",id);
		return spr;
	}
}