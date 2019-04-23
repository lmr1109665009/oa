package com.suneee.platform.service.system;

import java.util.Date;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.ReportTemplateDao;
import com.suneee.platform.model.system.ReportTemplate;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.ReportTemplateDao;
import com.suneee.platform.model.system.ReportTemplate;

/**
 * 对象功能:报表模板Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:cjj
 * 创建时间:2012-04-12 09:59:47
 */
@Service
public class ReportTemplateService extends BaseService<ReportTemplate>
{
	@Resource
	private ReportTemplateDao dao;
	
	public ReportTemplateService()
	{
	}
	
	@Override
	protected IEntityDao<ReportTemplate, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 *  插入报表模板
	 * @param reportTemplate
	 * @param localPath
	 * @param createTime
	 * @throws Exception
	 */
	public void saveReportTemplate(ReportTemplate reportTemplate, String localPath, Date createTime) throws Exception {
		
		if(createTime==null){
			createTime= new Date();
		}
		
		if(reportTemplate.getReportId()==null){
			reportTemplate.setReportId(UniqueIdUtil.genId());
			reportTemplate.setReportLocation(localPath);
			reportTemplate.setCreateTime(createTime);
			reportTemplate.setUpdateTime(createTime);
			add(reportTemplate);
		}else{
			reportTemplate.setReportLocation(localPath);
			reportTemplate.setCreateTime(createTime);
			reportTemplate.setUpdateTime(new Date());
			update(reportTemplate);
		}
	}
	
}
