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
import com.suneee.kaoqin.dao.kaoqin.ShiftSettingDao;
import com.suneee.kaoqin.model.kaoqin.ShiftSetting;
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
import com.suneee.kaoqin.model.kaoqin.ShiftSetting;
import com.suneee.kaoqin.dao.kaoqin.ShiftSettingDao;
import com.suneee.core.service.BaseService;

/**
 *<pre>
 * 对象功能:排班设置 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:08:44
 *</pre>
 */
@Service
public class ShiftSettingService extends BaseService<ShiftSetting>
{
	@Resource
	private ShiftSettingDao dao;
	
	
	
	public ShiftSettingService()
	{
	}
	
	@Override
	protected IEntityDao<ShiftSetting, Long> getEntityDao()
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
			ShiftSetting shiftSetting=getShiftSetting(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				shiftSetting.setId(genId);
				this.add(shiftSetting);
			}else{
				shiftSetting.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(shiftSetting);
			}
			cmd.setBusinessKey(shiftSetting.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取ShiftSetting对象
	 * @param json
	 * @return
	 */
	public ShiftSetting getShiftSetting(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		ShiftSetting shiftSetting = JSONObjectUtil.toBean(json, ShiftSetting.class);
		return shiftSetting;
	}
	
	/**
	 * 保存 排班设置 信息
	 * @param shiftSetting
	 */
	public void save(ShiftSetting shiftSetting){
		Long id=shiftSetting.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			shiftSetting.setId(id);
			this.add(shiftSetting);
		}
		else{
			this.update(shiftSetting);
		}
	}
	
}
