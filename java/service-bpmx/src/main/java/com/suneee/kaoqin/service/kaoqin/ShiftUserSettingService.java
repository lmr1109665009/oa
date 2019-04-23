package com.suneee.kaoqin.service.kaoqin;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.dao.kaoqin.ShiftUserSettingDao;
import com.suneee.kaoqin.model.kaoqin.ShiftUserSetting;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 *<pre>
 * 对象功能:排班人员设置 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:10:16
 *</pre>
 */
@Service
public class ShiftUserSettingService extends BaseService<ShiftUserSetting>
{
	@Resource
	private ShiftUserSettingDao dao;
	
	
	
	public ShiftUserSettingService()
	{
	}
	
	@Override
	protected IEntityDao<ShiftUserSetting, Long> getEntityDao()
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
			ShiftUserSetting shiftUserSetting=getShiftUserSetting(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				shiftUserSetting.setId(genId);
				this.add(shiftUserSetting);
			}else{
				shiftUserSetting.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(shiftUserSetting);
			}
			cmd.setBusinessKey(shiftUserSetting.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取ShiftUserSetting对象
	 * @param json
	 * @return
	 */
	public ShiftUserSetting getShiftUserSetting(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		ShiftUserSetting shiftUserSetting = JSONObjectUtil.toBean(json, ShiftUserSetting.class);
		return shiftUserSetting;
	}
	
	/**
	 * 保存 排班人员设置 信息
	 * @param shiftUserSetting
	 */
	public void save(ShiftUserSetting shiftUserSetting){
		Long id=shiftUserSetting.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			shiftUserSetting.setId(id);
			this.add(shiftUserSetting);
		}
		else{
			this.update(shiftUserSetting);
		}
	}
	
	/**
	 * 根据用户id获取用户的排班
	 * @param targetId
	 * @return
	 */
	public List<ShiftUserSetting> getByTargetId(Long targetId){
		return dao.getByTargetId(targetId);
	}
	
	/**
	 * 根据班次id获取数据
	 * @param settingId
	 * @return
	 */
	public List<ShiftUserSetting> getListBySettingId(QueryFilter filter){
		return dao.getListBySettingId(filter);
	}
	
	/**
	 * 根据班次id和用户id获取唯一的排班设置
	 * @param params
	 * @return
	 */
	public ShiftUserSetting getByTargetAndSettingId(Map params){
		return dao.getByTargetAndSettingId(params);
	}
	
}
