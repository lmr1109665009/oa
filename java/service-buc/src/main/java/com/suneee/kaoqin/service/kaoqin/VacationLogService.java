package com.suneee.kaoqin.service.kaoqin;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.kaoqin.dao.kaoqin.VacationLogDao;
import com.suneee.kaoqin.model.kaoqin.VacationLog;
import net.sf.json.util.JSONUtils;
import net.sf.ezmorph.object.DateMorpher;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import net.sf.json.JSONObject;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.kaoqin.model.kaoqin.VacationLog;
import com.suneee.kaoqin.dao.kaoqin.VacationLogDao;
import com.suneee.core.service.BaseService;

/**
 *<pre>
 * 对象功能:结余调整日志 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 11:37:20
 *</pre>
 */
@Service
public class VacationLogService extends BaseService<VacationLog>
{
	@Resource
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
	 * 流程处理器方法 用于处理业务数据
	 * @param cmd
	 * @throws Exception
	 */
	public void processHandler(ProcessCmd cmd)throws Exception{
		Map data=cmd.getFormDataMap();
		if(BeanUtils.isNotEmpty(data)){
			String json=data.get("json").toString();
			VacationLog vacationLog=getVacationLog(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				vacationLog.setId(genId);
				this.add(vacationLog);
			}else{
				vacationLog.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(vacationLog);
			}
			cmd.setBusinessKey(vacationLog.getId().toString());
		}
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
