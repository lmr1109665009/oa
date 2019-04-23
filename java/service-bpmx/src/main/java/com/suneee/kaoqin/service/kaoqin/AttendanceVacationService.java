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
import com.suneee.kaoqin.dao.kaoqin.AttendanceVacationDao;
import com.suneee.kaoqin.model.kaoqin.AttendanceVacation;
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
import com.suneee.kaoqin.model.kaoqin.AttendanceVacation;
import com.suneee.kaoqin.dao.kaoqin.AttendanceVacationDao;
import com.suneee.core.service.BaseService;

/**
 *<pre>
 * 对象功能:假期类型 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 10:43:17
 *</pre>
 */
@Service
public class AttendanceVacationService extends BaseService<AttendanceVacation>
{
	@Resource
	private AttendanceVacationDao dao;
	
	
	
	public AttendanceVacationService()
	{
	}
	
	@Override
	protected IEntityDao<AttendanceVacation, Long> getEntityDao()
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
			AttendanceVacation attendanceVacation=getAttendanceVacation(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				attendanceVacation.setId(genId);
				this.add(attendanceVacation);
			}else{
				attendanceVacation.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(attendanceVacation);
			}
			cmd.setBusinessKey(attendanceVacation.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取AttendanceVacation对象
	 * @param json
	 * @return
	 */
	public AttendanceVacation getAttendanceVacation(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		AttendanceVacation attendanceVacation = JSONObjectUtil.toBean(json, AttendanceVacation.class);
		return attendanceVacation;
	}
	
	/**
	 * 保存 假期类型 信息
	 * @param attendanceVacation
	 */
	public void save(AttendanceVacation attendanceVacation){
		Long id=attendanceVacation.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			attendanceVacation.setId(id);
			this.add(attendanceVacation);
		}
		else{
			this.update(attendanceVacation);
		}
	}
	/**
	 * 检查是否重复
	 * @param id 
	 * @param subject
	 * @return
	 */
	public Boolean checkRepeat(Long id, String subject) {
		List<AttendanceVacation> list = dao.checkRepeat(id, subject);
		return list.size() > 0;
	}

	public List<AttendanceVacation> getAllByRemain() {
		return dao.getBySqlKey("getAllByRemain");
	}

	/**
	 * 根据假期类型编码查询假期
	 * @param code
	 * @return
	 */
	public AttendanceVacation getVacationByCode(String code) {
		return dao.getUnique("getVacationByCode", code);
	}
	
}
