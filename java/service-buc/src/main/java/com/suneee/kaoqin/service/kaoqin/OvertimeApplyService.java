package com.suneee.kaoqin.service.kaoqin;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.dao.kaoqin.OvertimeApplyDao;
import com.suneee.kaoqin.model.kaoqin.OvertimeApply;
import org.springframework.stereotype.Service;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.dao.kaoqin.OvertimeApplyDao;
import com.suneee.kaoqin.model.kaoqin.OvertimeApply;
import com.suneee.platform.model.system.SysUser;

import net.sf.json.JSONObject;

/**
 *<pre>
 * 对象功能:加班申请 流程Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-05 10:20:27
 *</pre>
 */
@Service
public class OvertimeApplyService extends BaseService<OvertimeApply>
{
	@Resource
	private OvertimeApplyDao dao;
	
	
	
	public OvertimeApplyService()
	{
	}
	
	@Override
	protected IEntityDao<OvertimeApply, Long> getEntityDao()
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
			OvertimeApply overtimeApply=getOvertimeApply(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				overtimeApply.setId(genId);
				this.add(overtimeApply);
			}else{
				overtimeApply.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(overtimeApply);
			}
			cmd.setBusinessKey(overtimeApply.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取OvertimeApply对象
	 * @param json
	 * @return
	 */
	public OvertimeApply getOvertimeApply(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		OvertimeApply overtimeApply = JSONObjectUtil.toBean(json, OvertimeApply.class);
		return overtimeApply;
	}
	
	/**
	 * 保存 加班申请 信息
	 * @param overtimeApply
	 */
	public void save(OvertimeApply overtimeApply){
		Long id=overtimeApply.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			overtimeApply.setId(id);
			this.add(overtimeApply);
		}
		else{
			this.update(overtimeApply);
		}
	}
	
	public List<OvertimeApply> getAllApply(QueryFilter queryFilter){
		return dao.getAllApply(queryFilter);
	}
	
	/**
	 * 获取申请通过的在指定日期加班的人员列表
	 * @param date
	 * @return
	 */
	public List<SysUser> getPassedApplyUsersOfWorkDay(Date date) {
		QueryFilter filter = new QueryFilter(new JSONObject());
		String dateStr = DateFormatUtil.format(date);
		filter.addFilter("endStartTime", dateStr);
		filter.addFilter("beginEndTime", dateStr);
		return dao.getBySqlKeyGenericity("getPassedApplyUsersOfWorkDay", filter);
	}
}
