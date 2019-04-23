package com.suneee.platform.service.ats;
import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsShiftTypeDao;
import com.suneee.platform.model.ats.AtsConstant;
import com.suneee.platform.model.ats.AtsShiftType;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsShiftTypeDao;
import com.suneee.platform.model.ats.AtsConstant;
import com.suneee.platform.model.ats.AtsShiftType;

/**
 *<pre>
 * 对象功能:班次类型 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-16 21:44:00
 *</pre>
 */
@Service
public class AtsShiftTypeService extends BaseService<AtsShiftType>
{
	@Resource
	private AtsShiftTypeDao dao;
	
	
	
	public AtsShiftTypeService()
	{
	}
	
	@Override
	protected IEntityDao<AtsShiftType, Long> getEntityDao()
	{
		return dao;
	}
	
	
	/**
	 * 保存 班次类型 信息
	 * @param atsShiftType
	 */
	public void save(AtsShiftType atsShiftType){
		Long id=atsShiftType.getId();
		if(id==null || id==0){
			id= UniqueIdUtil.genId();
			atsShiftType.setId(id);
			atsShiftType.setIsSys(AtsConstant.NO);
			this.add(atsShiftType);
		}
		else{
			this.update(atsShiftType);
		}
	}

	public List<AtsShiftType> getListByStatus(Short status) {
		return dao.getListByStatus(status);
	}
	
}
