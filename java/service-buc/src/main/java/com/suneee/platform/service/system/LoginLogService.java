package com.suneee.platform.service.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.LoginLogDao;
import com.suneee.platform.model.system.LoginLog;
import net.sf.json.util.JSONUtils;
import net.sf.ezmorph.object.DateMorpher;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.util.StringUtil;
import net.sf.json.JSONObject;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.model.system.LoginLog;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.dao.system.LoginLogDao;
import com.suneee.core.service.BaseService;

/**
 * <pre>
 * 对象功能:sys_login_log Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-07-01 14:10:56
 * </pre>
 */
@Service
public class LoginLogService extends BaseService<LoginLog> {
	@Resource
	private LoginLogDao dao;

	public LoginLogService() {
	}

	@Override
	protected IEntityDao<LoginLog, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 保存 sys_login_log 信息
	 * 
	 * @param loginLog
	 */
	public void save(LoginLog loginLog) {
		Long id = loginLog.getId();
		if (id == null || id == 0) {
			id = UniqueIdUtil.genId();
			loginLog.setId(id);
			this.add(loginLog);
		} else {
			this.update(loginLog);
		}
	}

	/**
	 * 获取今天的失败登录次数，当成功登录后，会清空失败次数，重新计算
	 * 
	 * @param user
	 * @return int
	 * @exception
	 * @since 1.0.0
	 */
	public int getTodayFailCount(String account) {
		int failCount = 0;

		Map<String, Object> map = new HashMap<String, Object>();
		Date today = TimeUtil.getDateByDateString(new Date().toLocaleString());
		Date tomorrow = DateUtil.addDay(today, 1);
		map.put("account", account);
		map.put("beginloginTime", today);
		map.put("endloginTime", tomorrow);
		map.put("orderField", "LOGINTIME");
		map.put("orderSeq", "ASC");
		List<LoginLog> loginLogs = dao.getBySqlKey("getAll", map);

		for (LoginLog loginLog : loginLogs) {
			if (loginLog.getStatus() == LoginLog.Status.SUCCESS) {
				failCount = 0;
			} else if (loginLog.getStatus() < 0) {
				failCount++;
			}
		}

		return failCount;
	}

}
