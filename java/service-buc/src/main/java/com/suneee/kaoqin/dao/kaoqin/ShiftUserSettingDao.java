package com.suneee.kaoqin.dao.kaoqin;

import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.model.kaoqin.ShiftUserSetting;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:排班人员设置 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:10:16
 *</pre>
 */
@Repository
public class ShiftUserSettingDao extends BaseDao<ShiftUserSetting>
{
	@Override
	public Class<?> getEntityClass()
	{
		return ShiftUserSetting.class;
	}

	/**
	 * 根据用户id获取用户的排班
	 * @param targetId
	 * @return
	 */
	public List<ShiftUserSetting> getByTargetId(Long targetId){
		return this.getBySqlKey("getByTargetId", targetId);
	}
	
	/**
	 * 根据班次id获取数据
	 * @param settingId
	 * @return
	 */
	public List<ShiftUserSetting> getListBySettingId(QueryFilter filter){
		return this.getBySqlKey("getListBySettingId", filter);
	}
	
	/**
	 * 根据班次id和用户id获取唯一的排班设置
	 * @param params
	 * @return
	 */
	public ShiftUserSetting getByTargetAndSettingId(Map params){
		return (ShiftUserSetting)this.getOne("getByTargetAndSettingId", params);
	}
}