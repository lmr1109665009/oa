package com.suneee.platform.service.ats;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsAttenceGroupDetailDao;
import com.suneee.platform.model.ats.AtsAttenceGroupDetail;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *<pre>
 * 对象功能:考勤组明细 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-26 10:07:59
 *</pre>
 */
@Service
public class AtsAttenceGroupDetailService extends BaseService<AtsAttenceGroupDetail>
{
	@Resource
	private AtsAttenceGroupDetailDao dao;
	
	
	
	public AtsAttenceGroupDetailService()
	{
	}
	
	@Override
	protected IEntityDao<AtsAttenceGroupDetail, Long> getEntityDao()
	{
		return dao;
	}
	
	
	/**
	 * 保存 考勤组明细 信息
	 * @param atsAttenceGroupDetail
	 */
	public void save(AtsAttenceGroupDetail atsAttenceGroupDetail){
		Long id=atsAttenceGroupDetail.getId();
		if(id==null || id==0){
			id= UniqueIdUtil.genId();
			atsAttenceGroupDetail.setId(id);
			this.add(atsAttenceGroupDetail);
		}
		else{
			this.update(atsAttenceGroupDetail);
		}
	}
	
}
