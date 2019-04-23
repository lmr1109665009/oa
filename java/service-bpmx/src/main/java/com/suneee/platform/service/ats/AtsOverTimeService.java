package com.suneee.platform.service.ats;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsOverTimeDao;
import com.suneee.platform.model.ats.AtsOverTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *<pre>
 * 对象功能:考勤加班单 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-07-15 15:00:33
 *</pre>
 */
@Service
public class AtsOverTimeService extends BaseService<AtsOverTime>
{
	@Resource
	private AtsOverTimeDao dao;
	
	
	
	public AtsOverTimeService()
	{
	}
	
	@Override
	protected IEntityDao<AtsOverTime, Long> getEntityDao()
	{
		return dao;
	}
	
	
	/**
	 * 保存 考勤加班单 信息
	 * @param atsOverTime
	 */
	public void save(AtsOverTime atsOverTime){
		Long id=atsOverTime.getId();
		if(id==null || id==0){
			id= UniqueIdUtil.genId();
			atsOverTime.setId(id);
			this.add(atsOverTime);
		}
		else{
			this.update(atsOverTime);
		}
	}
	
}
