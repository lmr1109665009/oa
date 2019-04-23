package com.suneee.platform.service.ats;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.dao.ats.AtsAttenceCalculateSetDao;
import com.suneee.platform.model.ats.AtsAttenceCalculateSet;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.dao.ats.AtsAttenceCalculateSetDao;
import com.suneee.platform.model.ats.AtsAttenceCalculateSet;

/**
 * <pre>
 * 对象功能:考勤计算设置 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-06-03 14:46:19
 * </pre>
 */
@Service
public class AtsAttenceCalculateSetService extends
        BaseService<AtsAttenceCalculateSet> {
	@Resource
	private AtsAttenceCalculateSetDao dao;

	public AtsAttenceCalculateSetService() {
	}

	@Override
	protected IEntityDao<AtsAttenceCalculateSet, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 保存 考勤计算设置 信息
	 * 
	 * @param atsAttenceCalculateSet
	 */
	public void save(AtsAttenceCalculateSet atsAttenceCalculateSet) {
		Short type = atsAttenceCalculateSet.getType();
		Long id = 1L;
		AtsAttenceCalculateSet set = getDefault();
		if (BeanUtils.isEmpty(set)) {
			set = new AtsAttenceCalculateSet();
			set.setId(id);
			if (type == 1) {
				set.setSummary(atsAttenceCalculateSet.getDetail());
			} else {
				set.setDetail(atsAttenceCalculateSet.getDetail());
			}
			this.add(set);
		} else {
			if (type == 1) {
				set.setSummary(atsAttenceCalculateSet.getDetail());
			} else {
				set.setDetail(atsAttenceCalculateSet.getDetail());
			}
			this.update(set);
		}
	}

	public AtsAttenceCalculateSet getDefault() {
		return dao.getById(1L);
	}

}
