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
import com.suneee.platform.dao.ats.AtsTripDao;
import com.suneee.platform.model.ats.AtsTrip;
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
import com.suneee.platform.model.ats.AtsTrip;
import com.suneee.platform.dao.ats.AtsTripDao;
import com.suneee.core.service.BaseService;

/**
 *<pre>
 * 对象功能:考勤出差单 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-07-15 14:59:30
 *</pre>
 */
@Service
public class AtsTripService extends BaseService<AtsTrip>
{
	@Resource
	private AtsTripDao dao;
	
	
	
	public AtsTripService()
	{
	}
	
	@Override
	protected IEntityDao<AtsTrip, Long> getEntityDao()
	{
		return dao;
	}
	
	
	/**
	 * 保存 考勤出差单 信息
	 * @param atsTrip
	 */
	public void save(AtsTrip atsTrip){
		Long id=atsTrip.getId();
		if(id==null || id==0){
			id= UniqueIdUtil.genId();
			atsTrip.setId(id);
			this.add(atsTrip);
		}
		else{
			this.update(atsTrip);
		}
	}
	
}
