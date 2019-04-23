package com.suneee.kaoqin.service.kaoqin;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.kaoqin.dao.kaoqin.ExemmptSettingDao;
import com.suneee.kaoqin.model.kaoqin.ExemmptSetting;
import com.suneee.platform.model.system.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *<pre>
 * 对象功能:免签设置 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:06:05
 *</pre>
 */
@Service
public class ExemmptSettingService extends BaseService<ExemmptSetting>
{
	@Resource
	private ExemmptSettingDao dao;
	
	
	
	public ExemmptSettingService()
	{
	}
	
	@Override
	protected IEntityDao<ExemmptSetting, Long> getEntityDao()
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
			ExemmptSetting exemmptSetting=getExemmptSetting(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				exemmptSetting.setId(genId);
				this.add(exemmptSetting);
			}else{
				exemmptSetting.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(exemmptSetting);
			}
			cmd.setBusinessKey(exemmptSetting.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取ExemmptSetting对象
	 * @param json
	 * @return
	 */
	public ExemmptSetting getExemmptSetting(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		ExemmptSetting exemmptSetting = JSONObjectUtil.toBean(json, ExemmptSetting.class);
		return exemmptSetting;
	}
	
	/**
	 * 保存 免签设置 信息
	 * @param exemmptSetting
	 */
	public void save(ExemmptSetting exemmptSetting){
		Long id=exemmptSetting.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			exemmptSetting.setId(id);
			this.add(exemmptSetting);
		}
		else{
			this.update(exemmptSetting);
		}
	}
	
	public ExemmptSetting getByTargetId(Long targetId){
		return dao.getByTargetId(targetId);
	}

	/**
	 * 获取所有免签人员
	 * @return
	 */
	public List<SysUser> getExcludeUsers() {
		Map<String, Object> params = new HashMap<String, Object>();
		return dao.getBySqlKeyGenericity("getExcludeUsers", params);
	}
}
