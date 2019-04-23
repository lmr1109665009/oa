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
import com.suneee.kaoqin.dao.kaoqin.WorkingCalendarDao;
import com.suneee.kaoqin.model.kaoqin.WorkingCalendar;
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
import com.suneee.kaoqin.model.kaoqin.WorkingCalendar;
import com.suneee.kaoqin.dao.kaoqin.WorkingCalendarDao;
import com.suneee.core.service.BaseService;

/**
 *<pre>
 * 对象功能:工作日历 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:12:02
 *</pre>
 */
@Service
public class WorkingCalendarService extends BaseService<WorkingCalendar>
{
	@Resource
	private WorkingCalendarDao dao;
	
	
	
	public WorkingCalendarService()
	{
	}
	
	@Override
	protected IEntityDao<WorkingCalendar, Long> getEntityDao()
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
			WorkingCalendar workingCalendar=getWorkingCalendar(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				workingCalendar.setId(genId);
				this.add(workingCalendar);
			}else{
				workingCalendar.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(workingCalendar);
			}
			cmd.setBusinessKey(workingCalendar.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取WorkingCalendar对象
	 * @param json
	 * @return
	 */
	public WorkingCalendar getWorkingCalendar(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		WorkingCalendar workingCalendar = JSONObjectUtil.toBean(json, WorkingCalendar.class);
		return workingCalendar;
	}
	
	/**
	 * 保存 工作日历 信息
	 * @param workingCalendar
	 */
	public void save(WorkingCalendar workingCalendar){
		Long id=workingCalendar.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			workingCalendar.setId(id);
			this.add(workingCalendar);
		}
		else{
			this.update(workingCalendar);
		}
	}
	
}
