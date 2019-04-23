package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.model.bpm.AgentSetting;
import com.suneee.core.db.BaseDao;
import com.suneee.core.util.BeanUtils;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:代理设定 Dao类
 * 开发公司:hotent
 * 开发人员:xxx
 * 创建时间:2013-04-29 11:15:10
 *</pre>
 */
@Repository
public class AgentSettingDao extends BaseDao<AgentSetting>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AgentSetting.class;
	}
	public List<AgentSetting> getGeneralAgentByFilter(Map<String,Object> params){
		return this.getBySqlKey("getAgentByFilter", params);
	}
	public List<AgentSetting> getByFlowKey(String flowKey,Map<String,Object> params) {
		return this.getBySqlKey("getByFlowKey", params);
	}
	public AgentSetting getValidByFlowAndAuthidAndDate(String flowKey,Long authid, Date date) {
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("flowkey", flowKey);
		params.put("authid", authid);
		params.put("date", date);
		List<AgentSetting> list = this.getBySqlKey("getValidByFlowAndAuthidAndDate", params);
		if(BeanUtils.isNotEmpty(list)){
			return list.get(0);
		}else{
			return null;
		}
	}


	/**
	 * 更新代理设置状态  是启用还是禁用
	 * @param id
	 * @param enabled
	 */
	public Boolean updateEnabled(long id, Short enabled) {
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("id",id);
		params.put("enabled",enabled);
		int i = this.update("updateEnabled", params);
		if(i > 0){
			return true;
		}
		return false;
	}
}