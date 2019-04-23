package com.suneee.platform.service.ats;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsHolidayTypeDao;
import com.suneee.platform.model.ats.AtsConstant;
import com.suneee.platform.model.ats.AtsHolidayType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <pre>
 * 对象功能:假期类型 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-16 20:47:17
 * </pre>
 */
@Service
public class AtsHolidayTypeService extends BaseService<AtsHolidayType> {
	@Resource
	private AtsHolidayTypeDao dao;

	public AtsHolidayTypeService() {
	}

	@Override
	protected IEntityDao<AtsHolidayType, Long> getEntityDao() {
		return dao;
	}
	
	public AtsHolidayType getByCode(String code) {
		return dao.getUnique("getByCode", code);
	}
	
	public AtsHolidayType getByName(String name) {
		return dao.getUnique("getByName", name);
	}
	
	/**
	 * 保存 假期类型 信息
	 * 
	 * @param atsHolidayType
	 */
	public void save(AtsHolidayType atsHolidayType) {
		Long id = atsHolidayType.getId();
		if (id == null || id == 0) {
			id = UniqueIdUtil.genId();
			atsHolidayType.setId(id);
			atsHolidayType.setIsSys(AtsConstant.NO);
			this.add(atsHolidayType);
		} else {
			this.update(atsHolidayType);
		}
	}

}
