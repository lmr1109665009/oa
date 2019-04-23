package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.SysErrorLogDao;
import com.suneee.platform.model.system.SysErrorLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 *<pre>
 * 对象功能:系统错误日志 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2013-07-12 16:42:11
 *</pre>
 */
@Service
public class SysErrorLogService extends BaseService<SysErrorLog>
{
	@Resource
	private SysErrorLogDao dao;
	
	
	
	public SysErrorLogService()
	{
	}
	
	@Override
	protected IEntityDao<SysErrorLog, Long> getEntityDao()
	{
		return dao;
	}
	
	
	/**
	 * 添加错误日志。
	 * @param account	账号
	 * @param ip		IP
	 * @param error		错误日志
	 * @param errorUrl	错误的URL
	 */
	public Long addError(String account,String ip,String error,String errorUrl){
		Long id = UniqueIdUtil.genId();
		SysErrorLog sysErrorLog =  new SysErrorLog();
		sysErrorLog.setId(id);
		sysErrorLog.setHashcode(error.hashCode()+"");
		sysErrorLog.setAccount(account);
		sysErrorLog.setIp(ip);
		sysErrorLog.setError(error);
		sysErrorLog.setErrorurl(StringUtils.substring(errorUrl, 0,1999));
		sysErrorLog.setErrordate(new Date());
		dao.add(sysErrorLog);
		return id;
	}
	
}
