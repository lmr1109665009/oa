package com.suneee.ucp.mh.service.attendance;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.mh.dao.attendance.AttendanceVacationDao;
import com.suneee.ucp.mh.model.attendance.AttendanceVacation;

/**
 *<pre>
 * 对象功能:假期类型 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 10:43:17
 *</pre>
 */
@Service("ucpAttendanceVacationService")
public class AttendanceVacationService extends UcpBaseService<AttendanceVacation>
{
	@Resource(name="ucpAttendanceVacationDao")
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
