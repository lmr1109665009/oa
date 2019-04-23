package com.suneee.platform.service.ats;
import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsCardRuleDao;
import com.suneee.platform.model.ats.AtsCardRule;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsCardRuleDao;
import com.suneee.platform.model.ats.AtsCardRule;

/**
 *<pre>
 * 对象功能:取卡规则 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-18 16:16:16
 *</pre>
 */
@Service
public class AtsCardRuleService extends BaseService<AtsCardRule>
{
	@Resource
	private AtsCardRuleDao dao;
	
	
	
	public AtsCardRuleService()
	{
	}
	
	@Override
	protected IEntityDao<AtsCardRule, Long> getEntityDao()
	{
		return dao;
	}
	
	
	/**
	 * 保存 取卡规则 信息
	 * @param atsCardRule
	 */
	public void save(AtsCardRule atsCardRule){
		Long id=atsCardRule.getId();
		if(id==null || id==0){
			id= UniqueIdUtil.genId();
			atsCardRule.setId(id);
			this.add(atsCardRule);
		}
		else{
			this.update(atsCardRule);
		}
	}
	
}
