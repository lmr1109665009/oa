package com.suneee.platform.dao.ats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.suneee.core.db.BaseDao;
import com.suneee.core.util.BeanUtils;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.model.ats.AtsShiftInfo;
/**
 *<pre>
 * 对象功能:班次设置 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-18 17:21:46
 *</pre>
 */
@Repository
public class AtsShiftInfoDao extends BaseDao<AtsShiftInfo>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AtsShiftInfo.class;
	}

	public AtsShiftInfo getByShiftName(String name) {
		return this.getUnique("getByShiftName", name);
	}

	public AtsShiftInfo getByDefault() {
		List<AtsShiftInfo> list =  this.getBySqlKey("getByDefault");
		if(BeanUtils.isNotEmpty(list))
			return list.get(0);
		return null;
	}

	public Integer isAliasExists(String name, Long id) {
		Map<String, Object> params= new HashMap<String, Object>();
		params.put("name", name);
		params.put("id", id);
		return (Integer)this.getOne("isAliasExists", params);
	}

	
	
	
	
}