/**
 * @Title: SyncEnterpriseinfoJob.java 
 * @Package com.suneee.ucp.base.job 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.ucp.base.job;

import com.suneee.core.scheduler.BaseJob;
import com.suneee.core.util.AppConfigUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.oa.service.user.SysOrgExtendService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: SyncEnterpriseinfoJob 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-05-04 15:26:31 
 *
 */
public class SyncEnterpriseinfoJob extends BaseJob {
	private static final Logger LOGGER = LoggerFactory.getLogger(SyncEnterpriseinfoJob.class);

	/** (non-Javadoc)
	 * @Title: executeJob 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param context
	 * @throws Exception 
	 * @see BaseJob#executeJob(JobExecutionContext)
	 */
	@Override
	public void executeJob(JobExecutionContext context) throws Exception {
		// 获取定时任务参数
		Map<String, Object> params = context.getJobDetail().getJobDataMap();
		// 获取集团编码
		String enterpriseCode = (String)params.get("enterpriseCode");
		// 如果配置定时任务时未设置集团编码，则从配置文件中获取
		if(StringUtils.isBlank(enterpriseCode)){
			enterpriseCode = AppConfigUtil.get(Constants.AUTHORITY_PARAM_COMPCODE);
		}
		// 当集团编码为空时，不继续执行同步企业信息定时任务
		if(StringUtils.isBlank(enterpriseCode)){
			LOGGER.error("接口请求参数集团编码为空");
			throw new Exception("接口请求参数集团编码为空");
		}
		
		// 从权限中心获取企业信息
		SysOrgExtendService sysOrgExtendService = AppUtil.getBean(SysOrgExtendService.class);
		List<Enterpriseinfo> enterpriseinfoList = sysOrgExtendService.getEnterpriseinfoList(enterpriseCode);
		
		// 保存企业信息到本地数据库
		EnterpriseinfoService enterpriseinfoService = AppUtil.getBean(EnterpriseinfoService.class);
		for(Enterpriseinfo enterpriseinfo : enterpriseinfoList){
			// 判断企业信息是否存在，存在更新，不存在新增
			boolean isExist = enterpriseinfoService.isExist(enterpriseinfo.getComp_code());
			enterpriseinfo.setOpTime(new Date());
			enterpriseinfo.setCreatetime(new Date(enterpriseinfo.getCreateDate()));
			enterpriseinfo.setUpdatetime(new Date(enterpriseinfo.getUpdateDate()));
			if(isExist){
				enterpriseinfoService.update(enterpriseinfo);
			} else {
				enterpriseinfoService.add(enterpriseinfo);
			}
		}
	}
}
