/**
 * 对象功能:表单验证规则 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:phl
 * 创建时间:2012-01-11 10:53:15
 */
package com.suneee.platform.dao.form;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.form.BpmFormRule;
import org.springframework.stereotype.Repository;

@Repository
public class BpmFormRuleDao extends BaseDao<BpmFormRule>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return BpmFormRule.class;
	}
}