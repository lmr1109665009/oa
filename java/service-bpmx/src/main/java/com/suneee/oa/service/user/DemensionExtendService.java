package com.suneee.oa.service.user;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.system.Demension;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.oa.dao.user.DemensionExtendDao;
import com.suneee.ucp.base.service.UcpBaseService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 维度信息Demension扩展Service类
 * @author xiongxianyun
 *
 */
@Service
public class DemensionExtendService extends UcpBaseService<Demension> {

	@Resource
	private DemensionExtendDao demensionExtendDao;

	@Resource
	private SysOrgTypeExtendService sysOrgTypeExtendService;

	@Resource
	private SysOrgExtendService sysOrgExtendService;

	@Override
	protected IEntityDao<Demension, Long> getEntityDao()  {
		return this.demensionExtendDao;
	}

	/**
	 * 保存维度信息
	 * @param demension
	 * @param orgTypes
	 */
	public void save(Demension demension, String orgTypes) {
		// 保存维度信息
		if (demension.getDemId() == null) {
			demension.setDemId(Long.valueOf(UniqueIdUtil.genId()));
			add(demension);
		} else {
			update(demension);
		}
		// 保存组织类型信息
		this.sysOrgTypeExtendService.saveOrgType(demension.getDemId(), orgTypes);
	}

	
	/**
	 * 根据查询条件获取维度信息列表
	 * @param filter
	 * @return
	 */
	public List<Demension> getAllDemension(QueryFilter filter) {
		return this.demensionExtendDao.getAllDemension(filter);
	}

	/**
	 * 删除维度信息
	 * @param demId
	 * @param isForce
	 * @return
	 */
	public boolean del(Long demId, boolean isForce) {
		// 如果强制删除，则删除维度信息、组织类型信息、维度关联的组织信息
		if (isForce) {
			// 删除维度信息
			this.demensionExtendDao.delById(demId);
			// 删除组织类型信息
			this.sysOrgTypeExtendService.delByDemId(demId);
			// 删除关联的组织信息
			this.sysOrgExtendService.delByDemId(demId);
			return true;
		}
		
		// 非强制删除，先查询维度下的组织信息
		List<SysOrg> orgList = this.sysOrgExtendService.getByDemId(demId);
		// 维度没有关联组织信息，则删除维度信息及组织类型
		if (orgList.isEmpty()) {
			// 删除维度信息
			this.demensionExtendDao.delById(demId);
			// 删除组织类型
			this.sysOrgTypeExtendService.delByDemId(demId);
			return true;
		}

		return false;
	}
}