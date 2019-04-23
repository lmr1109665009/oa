package com.suneee.platform.service.ats;
import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsAttenceCycleDetailDao;
import com.suneee.platform.model.ats.AtsAttenceCycleDetail;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsAttenceCycleDetailDao;
import com.suneee.platform.model.ats.AtsAttenceCycleDetail;

/**
 *<pre>
 * 对象功能:考勤周期明细 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-17 15:47:49
 *</pre>
 */
@Service
public class AtsAttenceCycleDetailService extends BaseService<AtsAttenceCycleDetail>
{
	@Resource
	private AtsAttenceCycleDetailDao dao;
	
	
	
	public AtsAttenceCycleDetailService()
	{
	}
	
	@Override
	protected IEntityDao<AtsAttenceCycleDetail, Long> getEntityDao()
	{
		return dao;
	}
	
	
	/**
	 * 保存 考勤周期明细 信息
	 * @param atsAttenceCycleDetail
	 */
	public void save(AtsAttenceCycleDetail atsAttenceCycleDetail){
		Long id=atsAttenceCycleDetail.getId();
		if(id==null || id==0){
			id= UniqueIdUtil.genId();
			atsAttenceCycleDetail.setId(id);
			this.add(atsAttenceCycleDetail);
		}
		else{
			this.update(atsAttenceCycleDetail);
		}
	}

	public List<AtsAttenceCycleDetail> getByCycleId(Long cycleId) {
		return dao.getByCycleId(cycleId);
	}
	
	public List<AtsAttenceCycleDetail> getByCycleId(Long cycleId,boolean isCal) {
		
		List<AtsAttenceCycleDetail> list =  dao.getByCycleId(cycleId);
		if(!isCal)
			return list;
		for (AtsAttenceCycleDetail acd : list) {
			acd.setMemo(acd.getName()+"(" + DateFormatUtil.formatDate(acd.getStartTime())+"--" +DateFormatUtil.formatDate(acd.getEndTime())+")");
		}
		return list;
	}
	
}
