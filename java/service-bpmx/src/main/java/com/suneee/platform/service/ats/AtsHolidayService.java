package com.suneee.platform.service.ats;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsHolidayDao;
import com.suneee.platform.model.ats.AtsHoliday;
import net.sf.json.util.JSONUtils;
import net.sf.ezmorph.object.DateMorpher;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.util.StringUtil;
import net.sf.json.JSONObject;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.model.ats.AtsHoliday;
import com.suneee.platform.dao.ats.AtsHolidayDao;
import com.suneee.core.service.BaseService;

/**
 *<pre>
 * 对象功能:考勤请假单 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-07-15 14:59:52
 *</pre>
 */
@Service
public class AtsHolidayService extends BaseService<AtsHoliday>
{
	@Resource
	private AtsHolidayDao dao;
	
	
	
	public AtsHolidayService()
	{
	}
	
	@Override
	protected IEntityDao<AtsHoliday, Long> getEntityDao()
	{
		return dao;
	}
	
	
	/**
	 * 保存 考勤请假单 信息
	 * @param atsHoliday
	 */
	public void save(AtsHoliday atsHoliday){
		Long id=atsHoliday.getId();
		if(id==null || id==0){
			id= UniqueIdUtil.genId();
			atsHoliday.setId(id);
			this.add(atsHoliday);
		}
		else{
			this.update(atsHoliday);
		}
	}
	
}
