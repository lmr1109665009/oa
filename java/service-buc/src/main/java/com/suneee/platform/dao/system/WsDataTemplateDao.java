
package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.WsDataTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class WsDataTemplateDao extends BaseDao<WsDataTemplate>
{
	@Override
	public Class<?> getEntityClass()
	{
		return WsDataTemplate.class;
	}
	
	public List<WsDataTemplate> getByWsSetId(Long wsSetId){
		Map<String, Long> params = new HashMap<String, Long>();
		params.put("serviceId", wsSetId);
		return this.getList(getIbatisMapperNamespace() + ".getAll", params);
	}
}
