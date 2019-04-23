package com.suneee.platform.service.ats;
import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsAttencePolicyDao;
import com.suneee.platform.model.ats.AtsAttencePolicy;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsAttencePolicyDao;
import com.suneee.platform.model.ats.AtsAttencePolicy;

/**
 *<pre>
 * 对象功能:考勤制度 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-17 20:54:19
 *</pre>
 */
@Service
public class AtsAttencePolicyService extends BaseService<AtsAttencePolicy>
{
	@Resource
	private AtsAttencePolicyDao dao;
	
	
	
	public AtsAttencePolicyService()
	{
	}
	
	@Override
	protected IEntityDao<AtsAttencePolicy, Long> getEntityDao()
	{
		return dao;
	}
	
	
	/**
	 * 保存 考勤制度 信息
	 * @param atsAttencePolicy
	 */
	public void save(AtsAttencePolicy atsAttencePolicy){
		Long id=atsAttencePolicy.getId();
		if(id==null || id==0){
			id= UniqueIdUtil.genId();
			atsAttencePolicy.setId(id);
			this.add(atsAttencePolicy);
		}
		else{
			this.update(atsAttencePolicy);
		}
	}

	public AtsAttencePolicy getByDefault() {
		return dao.getByDefault();
	}

	public AtsAttencePolicy getByName(String name) {
		return dao.getByName(name);
	}
	
}
