package com.suneee.kaoqin.service.kaoqin;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.dao.kaoqin.SignCardApplyDao;
import com.suneee.kaoqin.model.kaoqin.SignCardApply;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 *<pre>
 * 对象功能:签卡申请 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-03 14:37:01
 *</pre>
 */
@Service
public class SignCardApplyService extends BaseService<SignCardApply>
{
	@Resource
	private SignCardApplyDao dao;
	
	
	
	public SignCardApplyService()
	{
	}
	
	@Override
	protected IEntityDao<SignCardApply, Long> getEntityDao()
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
			SignCardApply signCardApply=getSignCardApply(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				signCardApply.setId(genId);
				this.add(signCardApply);
			}else{
				signCardApply.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(signCardApply);
			}
			cmd.setBusinessKey(signCardApply.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取SignCardApply对象
	 * @param json
	 * @return
	 */
	public SignCardApply getSignCardApply(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		SignCardApply signCardApply = JSONObjectUtil.toBean(json, SignCardApply.class);
		return signCardApply;
	}
	
	/**
	 * 保存 签卡申请 信息
	 * @param signCardApply
	 */
	public void save(SignCardApply signCardApply){
		Long id=signCardApply.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			signCardApply.setId(id);
			this.add(signCardApply);
		}
		else{
			this.update(signCardApply);
		}
	}
	
	public List<SignCardApply> getAllApply(QueryFilter filter){
		return dao.getAllApply(filter);
	}
	
}
