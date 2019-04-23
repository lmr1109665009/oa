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
import com.suneee.kaoqin.dao.kaoqin.BusinessTravelDao;
import com.suneee.kaoqin.model.kaoqin.BusinessTravel;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.kaoqin.model.kaoqin.BusinessTravel;
import com.suneee.kaoqin.dao.kaoqin.BusinessTravelDao;
import com.suneee.core.service.BaseService;

/**
 *<pre>
 * 对象功能:出差申请 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-04 14:57:21
 *</pre>
 */
@Service
public class BusinessTravelService extends BaseService<BusinessTravel>
{
	@Resource
	private BusinessTravelDao dao;
	
	
	
	public BusinessTravelService()
	{
	}
	
	@Override
	protected IEntityDao<BusinessTravel, Long> getEntityDao()
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
			BusinessTravel businessTravel=getBusinessTravel(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				businessTravel.setId(genId);
				this.add(businessTravel);
			}else{
				businessTravel.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(businessTravel);
			}
			cmd.setBusinessKey(businessTravel.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取BusinessTravel对象
	 * @param json
	 * @return
	 */
	public BusinessTravel getBusinessTravel(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		BusinessTravel businessTravel = JSONObjectUtil.toBean(json, BusinessTravel.class);
		return businessTravel;
	}
	
	/**
	 * 保存 出差申请 信息
	 * @param businessTravel
	 */
	public void save(BusinessTravel businessTravel){
		Long id=businessTravel.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			businessTravel.setId(id);
			this.add(businessTravel);
		}
		else{
			this.update(businessTravel);
		}
	}
	
	public List<BusinessTravel> getAllApply(QueryFilter queryFilter){
		return dao.getBySqlKey("getAllApply", queryFilter);
	}
	
}
