package com.suneee.platform.dao.ats;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.ats.AtsShiftRule;

/**
 * <pre>
 * 对象功能:轮班规则 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-21 09:06:10
 * </pre>
 */
@Repository
public class AtsShiftRuleDao extends BaseDao<AtsShiftRule> {
	@Override
	public Class<?> getEntityClass() {
		return AtsShiftRule.class;
	}

}