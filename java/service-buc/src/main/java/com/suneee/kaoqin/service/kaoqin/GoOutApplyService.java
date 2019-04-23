package com.suneee.kaoqin.service.kaoqin;
import java.util.List;
import java.util.Map;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.query.QueryFilter;

import javax.annotation.Resource;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.dao.kaoqin.GoOutApplyDao;
import com.suneee.kaoqin.model.kaoqin.GoOutApply;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.kaoqin.model.kaoqin.GoOutApply;
import com.suneee.kaoqin.dao.kaoqin.GoOutApplyDao;
import com.suneee.core.service.BaseService;

/**
 *<pre>
 * 对象功能:外出申请 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 16:30:42
 *</pre>
 */
@Service
public class GoOutApplyService extends BaseService<GoOutApply>
{
	@Resource
	private GoOutApplyDao dao;
	
	
	
	public GoOutApplyService()
	{
	}
	
	@Override
	protected IEntityDao<GoOutApply, Long> getEntityDao()
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
			GoOutApply goOutApply=getGoOutApply(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				goOutApply.setId(genId);
				this.add(goOutApply);
			}else{
				goOutApply.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(goOutApply);
			}
			cmd.setBusinessKey(goOutApply.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取GoOutApply对象
	 * @param json
	 * @return
	 */
	public GoOutApply getGoOutApply(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		GoOutApply goOutApply = JSONObjectUtil.toBean(json, GoOutApply.class);
		return goOutApply;
	}
	
	/**
	 * 保存 外出申请 信息
	 * @param goOutApply
	 */
	public void save(GoOutApply goOutApply){
		Long id=goOutApply.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			goOutApply.setId(id);
			this.add(goOutApply);
		}
		else{
			this.update(goOutApply);
		}
	}
	
	public List<GoOutApply> getAllApply(QueryFilter filter){
		return dao.getAllApply(filter);
	}
}
