package com.suneee.kaoqin.service.kaoqin;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.dao.kaoqin.HolidaysSettingDao;
import com.suneee.kaoqin.model.kaoqin.HolidaysSetting;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *<pre>
 * 对象功能:节假日设置 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:07:14
 *</pre>
 */
@Service
public class HolidaysSettingService extends BaseService<HolidaysSetting>
{
	@Resource
	private HolidaysSettingDao dao;
	
	
	
	public HolidaysSettingService()
	{
	}
	
	@Override
	protected IEntityDao<HolidaysSetting, Long> getEntityDao()
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
			HolidaysSetting holidaysSetting=getHolidaysSetting(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				holidaysSetting.setId(genId);
				this.add(holidaysSetting);
			}else{
				holidaysSetting.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(holidaysSetting);
			}
			cmd.setBusinessKey(holidaysSetting.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取HolidaysSetting对象
	 * @param json
	 * @return
	 */
	public HolidaysSetting getHolidaysSetting(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		HolidaysSetting holidaysSetting = JSONObjectUtil.toBean(json, HolidaysSetting.class);
		return holidaysSetting;
	}
	
	/**
	 * 保存 节假日设置 信息
	 * @param holidaysSetting
	 */
	public void save(HolidaysSetting holidaysSetting){
		Long id=holidaysSetting.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			holidaysSetting.setId(id);
			this.add(holidaysSetting);
		}
		else{
			this.update(holidaysSetting);
		}
	}

	/**
	 * 根据日期查询假期设置
	 * @param time
	 * @return
	 */
	public HolidaysSetting getHolidaysSettingByDay(Date date) {
		QueryFilter filter = new QueryFilter(new JSONObject());
		filter.addFilter("endstartDate", date);
		filter.addFilter("beginendDate", date);
		filter.addFilter("status", HolidaysSetting.STATUS_NORMAL);
		List<HolidaysSetting> list = dao.getAll(filter);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 根据日期时间段查询假期设置
	 * @param time
	 * @return
	 */
	public List<HolidaysSetting> getHolidaysSettingBetween(Date startDate, Date endDate) {
		QueryFilter filter = new QueryFilter(new JSONObject());
		filter.addFilter("startDate", startDate);
		filter.addFilter("endDate", endDate);
		filter.addFilter("status", HolidaysSetting.STATUS_NORMAL);
		return dao.getBySqlKey("getHolidaysSettingBetween", filter);
	}
	
}
