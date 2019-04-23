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
import com.suneee.kaoqin.dao.kaoqin.AnnualVacationSettingDao;
import com.suneee.kaoqin.model.kaoqin.AnnualVacationSetting;
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
import com.suneee.kaoqin.model.kaoqin.AnnualVacationSetting;
import com.suneee.kaoqin.dao.kaoqin.AnnualVacationSettingDao;
import com.suneee.core.service.BaseService;

/**
 *<pre>
 * 对象功能:年假设置 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 11:30:12
 *</pre>
 */
@Service
public class AnnualVacationSettingService extends BaseService<AnnualVacationSetting>
{
	@Resource
	private AnnualVacationSettingDao dao;
	
	
	
	public AnnualVacationSettingService()
	{
	}
	
	@Override
	protected IEntityDao<AnnualVacationSetting, Long> getEntityDao()
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
			AnnualVacationSetting annualVacationSetting=getAnnualVacationSetting(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				annualVacationSetting.setId(genId);
				this.add(annualVacationSetting);
			}else{
				annualVacationSetting.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(annualVacationSetting);
			}
			cmd.setBusinessKey(annualVacationSetting.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取AnnualVacationSetting对象
	 * @param json
	 * @return
	 */
	public AnnualVacationSetting getAnnualVacationSetting(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		AnnualVacationSetting annualVacationSetting = JSONObjectUtil.toBean(json, AnnualVacationSetting.class);
		return annualVacationSetting;
	}
	
	/**
	 * 保存 年假设置 信息
	 * @param annualVacationSetting
	 */
	public void save(AnnualVacationSetting annualVacationSetting){
		Long id=annualVacationSetting.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			annualVacationSetting.setId(id);
			this.add(annualVacationSetting);
		}
		else{
			this.update(annualVacationSetting);
		}
	}
	
	/**
	 * 清空年假表
	 */
	public void emptyTable() {
		dao.delBySqlKey("emptyTable", null);
	}
	
}
