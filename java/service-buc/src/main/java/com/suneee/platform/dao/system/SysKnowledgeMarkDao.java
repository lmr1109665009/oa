package com.suneee.platform.dao.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysKnowledgeMark;
/**
 *<pre>
 * 对象功能:SYS_ KNOWLEDGE_MARK Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-07-28 10:23:42
 *</pre>
 */
@Repository
public class SysKnowledgeMarkDao extends BaseDao<SysKnowledgeMark>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysKnowledgeMark.class;
	}

	public List<SysKnowledgeMark> getByMarkAlias(String bookMarkAlias) {
		return this.getBySqlKey("getByMarkAlias", bookMarkAlias);
	}

	public void deleteByKnow(Long knowId) {
		Map<String,Long> map = new HashMap<String,Long>();
		map.put("knowledgeId", knowId);
		this.delBySqlKey("deleteByKnow", map);
	}

	public List<SysKnowledgeMark> isExist(String bookMark) {
		return this.getBySqlKey("getByMarkName", bookMark);
	}
}