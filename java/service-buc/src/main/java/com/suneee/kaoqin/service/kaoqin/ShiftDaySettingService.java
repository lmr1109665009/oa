package com.suneee.kaoqin.service.kaoqin;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.dao.kaoqin.ShiftDaySettingDao;
import com.suneee.kaoqin.model.kaoqin.ShiftDaySetting;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *<pre>
 * 对象功能:单日排班设置 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:08:00
 *</pre>
 */
@Service
public class ShiftDaySettingService extends BaseService<ShiftDaySetting>
{
	@Resource
	private ShiftDaySettingDao dao;
	
	
	
	public ShiftDaySettingService()
	{
	}
	
	@Override
	protected IEntityDao<ShiftDaySetting, Long> getEntityDao()
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
			ShiftDaySetting shiftDaySetting=getShiftDaySetting(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				shiftDaySetting.setId(genId);
				this.add(shiftDaySetting);
			}else{
				shiftDaySetting.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(shiftDaySetting);
			}
			cmd.setBusinessKey(shiftDaySetting.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取ShiftDaySetting对象
	 * @param json
	 * @return
	 */
	public ShiftDaySetting getShiftDaySetting(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		ShiftDaySetting shiftDaySetting = JSONObjectUtil.toBean(json, ShiftDaySetting.class);
		return shiftDaySetting;
	}
	
	/**
	 * 保存 单日排班设置 信息
	 * @param shiftDaySetting
	 */
	public void save(ShiftDaySetting shiftDaySetting){
		Long id=shiftDaySetting.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			shiftDaySetting.setId(id);
			this.add(shiftDaySetting);
		}
		else{
			this.update(shiftDaySetting);
		}
	}

	/**
	 * 根据日期获取某个班次的单日排班设置
	 * @param date
	 */
	public ShiftDaySetting getShiftSettingByDay(Long shiftId, Date date) {
		QueryFilter filter = new QueryFilter(new JSONObject());
		filter.addFilter("settingId", shiftId);
		filter.addFilter("scheduleDate", date);
		filter.addFilter("status", ShiftDaySetting.STATUS_NORMAL);
		List<ShiftDaySetting> list = dao.getAll(filter);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取指定日期之间的某个班次的单日排班
	 * @param shiftId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<ShiftDaySetting> getShiftDaySettingBetween(Long shiftId, Date startDate, Date endDate) {
		QueryFilter filter = new QueryFilter(new JSONObject());
		filter.addFilter("settingId", shiftId);
		filter.addFilter("beginscheduleDate", startDate);
		filter.addFilter("endscheduleDate", endDate);
		filter.addFilter("status", ShiftDaySetting.STATUS_NORMAL);
		return dao.getAll(filter);
	}
	
}
