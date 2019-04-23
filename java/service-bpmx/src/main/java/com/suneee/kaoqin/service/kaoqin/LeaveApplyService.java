package com.suneee.kaoqin.service.kaoqin;
import java.util.List;
import java.util.Map;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;

import javax.annotation.Resource;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.dao.kaoqin.LeaveApplyDao;
import com.suneee.kaoqin.model.kaoqin.LeaveApply;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.kaoqin.model.kaoqin.LeaveApply;
import com.suneee.kaoqin.dao.kaoqin.LeaveApplyDao;
import com.suneee.core.service.BaseService;

/**
 *<pre>
 * 对象功能:请假申请流程 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-11 09:35:17
 *</pre>
 */
@Service
public class LeaveApplyService extends BaseService<LeaveApply>
{
	@Resource
	private LeaveApplyDao dao;
	
	
	
	public LeaveApplyService()
	{
	}
	
	@Override
	protected IEntityDao<LeaveApply, Long> getEntityDao()
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
			LeaveApply leaveApply=getLeaveApply(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				leaveApply.setId(genId);
				this.add(leaveApply);
			}else{
				leaveApply.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(leaveApply);
			}
			cmd.setBusinessKey(leaveApply.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取LeaveApply对象
	 * @param json
	 * @return
	 */
	public LeaveApply getLeaveApply(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		LeaveApply leaveApply = JSONObjectUtil.toBean(json, LeaveApply.class);
		return leaveApply;
	}
	
	/**
	 * 保存 请假申请 信息
	 * @param leaveApply
	 */
	public void save(LeaveApply leaveApply){
		Long id=leaveApply.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			leaveApply.setId(id);
			this.add(leaveApply);
		}
		else{
			this.update(leaveApply);
		}
	}
	
	/**
	 * 获取请假流程数据
	 * @param filter
	 * @return
	 */
	public List<LeaveApply> getAllApply(QueryFilter filter){
		return dao.getAllApply(filter);
	}
	
}
