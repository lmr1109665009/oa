package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysReadRecode;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
/**
 *<pre>
 * 对象功能:已读记录 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:miaojf
 * 创建时间:2015-07-13 18:44:29
 *</pre>
 */
@Repository
public class SysReadRecodeDao extends BaseDao<SysReadRecode>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysReadRecode.class;
	}

	public boolean hasRead(Long id, Long userId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("objectId", id);
		map.put("userId", userId);
		int count = (Integer) this.getOne("hasRead", map);
		
		if(count>0) return true;
		return false;
	}
	/**
	 * 删除通过参数
	 * @param groupId 分组id
	 * @param objectId 对象id
	 * @param userId userId
	 */
	public void deleteByParam(Long groupId, Long objectId, Long userId){
		if((groupId==null || groupId <1) && (objectId == null || objectId<1)
				&& (userId == null ||userId<1)){
			throw new RuntimeException("删除已读记录必须提供参数！");
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("groupid", groupId);
		params.put("objectid", objectId);
		params.put("userid", userId);
		this.delBySqlKey("deleteByParam", params);
	}
	
}