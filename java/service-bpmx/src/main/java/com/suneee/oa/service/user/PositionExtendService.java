package com.suneee.oa.service.user;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.PinyinUtil;
import com.suneee.oa.dao.user.PositionExtendDao;
import com.suneee.platform.model.system.Job;
import com.suneee.platform.model.system.Position;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.PositionService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.service.UcpBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 岗位信息Position扩展Service类
 * @author xiongxianyun
 *
 */
@Service
public class PositionExtendService extends UcpBaseService<Position> {

	@Resource
	private PositionExtendDao positionExtendDao;

	@Resource
	private SysOrgExtendService sysOrgExtendService;

	@Resource
	private JobExtendService jobExtendService;

	@Resource
	private PositionService positionService;

	@Override
	protected IEntityDao<Position, Long> getEntityDao() {
		return this.positionExtendDao;
	}

	/**
	 * 根据组织ID和职务ID查询岗位信息
	 * @param orgId
	 * @param jobId
	 * @return
	 */
	public Position getByOrgIdAndJobId(Long orgId, Long jobId) {
		return this.positionExtendDao.getByOrgIdAndJobId(orgId, jobId);
	}

	/**
	 * 新增职务信息
	 * @param orgId
	 * @param jobId
	 * @return
	 */
	public Position addPosition(Long orgId, Long jobId)  {
		// 获取组织信息和职务信息
		SysOrg sysOrg = this.sysOrgExtendService.getById(orgId);
		Job job = this.jobExtendService.getById(jobId);
		
		// 生成岗位名称，格式：组织名称_职务名称
		String positionName = sysOrg.getOrgName() + Constants.SEPARATOR_UNDERLINE + job.getJobname();
		// 生成岗位编码，格式：组织编码_岗位名称拼音首字母
		String positionCode = sysOrg.getOrgCode().toLowerCase() + Constants.SEPARATOR_UNDERLINE + PinyinUtil.getPinYinHeadCharFilter(positionName);
		return this.positionService.generatePosition(positionCode, positionName, sysOrg.getOrgId(), job.getJobid(), 0);
	}
}