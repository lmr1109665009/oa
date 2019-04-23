package com.suneee.platform.service.ats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsLegalHolidayDetailDao;
import com.suneee.platform.model.ats.AtsLegalHolidayDetail;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsLegalHolidayDetailDao;
import com.suneee.platform.model.ats.AtsLegalHolidayDetail;

/**
 * <pre>
 * 对象功能:法定节假日明细 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-17 15:48:34
 * </pre>
 */
@Service
public class AtsLegalHolidayDetailService extends
        BaseService<AtsLegalHolidayDetail> {
	@Resource
	private AtsLegalHolidayDetailDao dao;

	public AtsLegalHolidayDetailService() {
	}

	@Override
	protected IEntityDao<AtsLegalHolidayDetail, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 保存 法定节假日明细 信息
	 * 
	 * @param atsLegalHolidayDetail
	 */
	public void save(AtsLegalHolidayDetail atsLegalHolidayDetail) {
		Long id = atsLegalHolidayDetail.getId();
		if (id == null || id == 0) {
			id = UniqueIdUtil.genId();
			atsLegalHolidayDetail.setId(id);
			this.add(atsLegalHolidayDetail);
		} else {
			this.update(atsLegalHolidayDetail);
		}
	}

	public List<AtsLegalHolidayDetail> getByHolidayId(Long holidayId) {
		return dao.getByHolidayId(holidayId);
	}

	public List<AtsLegalHolidayDetail> getHolidayListByAttencePolicy(
			Long attencePolicy) {
		return dao.getHolidayListByAttencePolicy(attencePolicy);
	}

	/**
	 * 处理节假日
	 * 
	 * @param attencePolicy
	 * @return
	 */
	public Map<String, String> getHolidayMap(Long attencePolicy) {
		List<AtsLegalHolidayDetail> holidayList = dao
				.getHolidayListByAttencePolicy(attencePolicy);

		Map<String, String> holidayMap = new HashMap<String, String>();
		for (AtsLegalHolidayDetail atsLegalHolidayDetail : holidayList) {
			String[] dates = DateUtil.getDaysBetweenDate(
					atsLegalHolidayDetail.getStartTime(),
					atsLegalHolidayDetail.getEndTime());
			for (String d : dates) {
				holidayMap.put(d, atsLegalHolidayDetail.getName());
			}
		}
		return holidayMap;
	}

	public Set<String> getHolidayNameByAttencePolicy(Long attencePolicy) {
		List<AtsLegalHolidayDetail> holidayList = dao
				.getHolidayListByAttencePolicy(attencePolicy);

		Set<String> name = new TreeSet<String>();
		for (AtsLegalHolidayDetail atsLegalHolidayDetail : holidayList) {
			name.add(atsLegalHolidayDetail.getName());
		}
		return name;
	}
}
