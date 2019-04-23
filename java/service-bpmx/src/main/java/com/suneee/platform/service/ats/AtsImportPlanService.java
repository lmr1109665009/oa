package com.suneee.platform.service.ats;
import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsImportPlanDao;
import com.suneee.platform.model.ats.AtsImportPlan;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsImportPlanDao;
import com.suneee.platform.model.ats.AtsImportPlan;

/**
 *<pre>
 * 对象功能:打卡导入方案 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-26 13:50:13
 *</pre>
 */
@Service
public class AtsImportPlanService extends BaseService<AtsImportPlan>
{
	@Resource
	private AtsImportPlanDao dao;
	
	
	
	public AtsImportPlanService()
	{
	}
	
	@Override
	protected IEntityDao<AtsImportPlan, Long> getEntityDao()
	{
		return dao;
	}
	
	
	/**
	 * 保存 打卡导入方案 信息
	 * @param atsImportPlan
	 */
	public void save(AtsImportPlan atsImportPlan){
		Long id=atsImportPlan.getId();
		if(id==null || id==0){
			id= UniqueIdUtil.genId();
			atsImportPlan.setId(id);
			this.add(atsImportPlan);
		}
		else{
			this.update(atsImportPlan);
		}
	}
	
}
