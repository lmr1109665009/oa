package com.suneee.ucp.mh.service.attendance;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.mh.dao.attendance.VacationLogDao;
import com.suneee.ucp.mh.model.attendance.VacationLog;

/**
 *<pre>
 * 对象功能:结余调整日志 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 11:37:20
 *</pre>
 */
@Service("ucpVacationLogService")
public class VacationLogService extends UcpBaseService<VacationLog>
{
	@Resource(name="ucpVacationLogDao")
	private VacationLogDao dao;
	
	public VacationLogService()
	{
	}
	
	@Override
	protected IEntityDao<VacationLog, Long> getEntityDao() 
	{
		return dao;
	}
	
	/**
	 * 根据json字符串获取VacationLog对象
	 * @param json
	 * @return
	 */
	public VacationLog getVacationLog(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		VacationLog vacationLog = JSONObjectUtil.toBean(json, VacationLog.class);
		return vacationLog;
	}
	
	/**
	 * 保存 结余调整日志 信息
	 * @param vacationLog
	 */
	public void save(VacationLog vacationLog){
		Long id=vacationLog.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			vacationLog.setId(id);
			this.add(vacationLog);
		}
		else{
			this.update(vacationLog);
		}
	}
	
}
