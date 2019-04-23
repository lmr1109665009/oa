package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.NodeMsgTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 *<pre>
 * 对象功能:bpm_nodemsg_template Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-05-27 18:03:14
 *</pre>
 */
@Repository
public class NodeMsgTemplateDao extends BaseDao<NodeMsgTemplate>
{
	@Override
	public Class<?> getEntityClass()
	{
		return NodeMsgTemplate.class;
	}

	
	/**
	 * 根据定义ID获取消息模版。
	 * @param defId
	 * @return
	 */
	public List<NodeMsgTemplate> getByDefId(Long defId){
		return this.getBySqlKey("getByDefId", defId);
	}
	
	
	
}