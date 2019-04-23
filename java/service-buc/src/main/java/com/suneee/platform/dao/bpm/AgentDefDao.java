package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.AgentDef;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 *<pre>
 * 对象功能:代理的流程列表 Dao类
 * 开发公司:SF
 * 开发人员:xxx
 * 创建时间:2013-04-29 11:15:10
 *</pre>
 */
@Repository
public class AgentDefDao extends BaseDao<AgentDef>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AgentDef.class;
	}
	public List<AgentDef> getByMainId(Long settingid) {
		return this.getBySqlKey("getAgentDefList", settingid);
	}
	
	public void delByMainId(Long settingid) {
		this.delBySqlKey("delByMainId", settingid);
	}
}