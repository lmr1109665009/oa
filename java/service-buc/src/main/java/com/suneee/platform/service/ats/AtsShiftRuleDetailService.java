package com.suneee.platform.service.ats;

import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsShiftRuleDetailDao;
import com.suneee.platform.model.ats.AtsShiftRuleDetail;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsShiftRuleDetailDao;
import com.suneee.platform.model.ats.AtsShiftRuleDetail;

/**
 * <pre>
 * 对象功能:轮班规则明细 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-21 09:06:50
 * </pre>
 */
@Service
public class AtsShiftRuleDetailService extends BaseService<AtsShiftRuleDetail> {
	@Resource
	private AtsShiftRuleDetailDao dao;

	public AtsShiftRuleDetailService() {
	}

	@Override
	protected IEntityDao<AtsShiftRuleDetail, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 保存 轮班规则明细 信息
	 * 
	 * @param atsShiftRuleDetail
	 */
	public void save(AtsShiftRuleDetail atsShiftRuleDetail) {
		Long id = atsShiftRuleDetail.getId();
		if (id == null || id == 0) {
			id = UniqueIdUtil.genId();
			atsShiftRuleDetail.setId(id);
			this.add(atsShiftRuleDetail);
		} else {
			this.update(atsShiftRuleDetail);
		}
	}

	public List<AtsShiftRuleDetail> getByRuleId(Long ruleId) {
		return dao.getByRuleId(ruleId);
	}

}
