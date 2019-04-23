package com.suneee.kaoqin.service.kaoqin;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.dao.kaoqin.AttendanceRecordDao;
import com.suneee.kaoqin.dao.kaoqin.AttendanceRelationDao;
import com.suneee.kaoqin.model.kaoqin.AttendanceRecord;
import com.suneee.platform.dao.system.SysOrgDao;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.system.SysUser;
import com.suneee.ucp.base.model.kaoqin.CheckInout;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 *<pre>
 * 对象功能:考勤记录表 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:mikel
 * 创建时间:2017-05-03 11:58:37
 *</pre>
 */
@Service
public class AttendanceRecordService extends BaseService<AttendanceRecord>
{
	@Resource
	private AttendanceRecordDao dao;
	@Resource
	private AttendanceRelationDao relationDao;
	@Resource
	private SysOrgDao orgDao;
	@Resource
	private SysUserDao userDao;
	
	public AttendanceRecordService()
	{
	}
	
	@Override
	protected IEntityDao<AttendanceRecord, Long> getEntityDao()
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
			AttendanceRecord attendanceRecord=getAttendanceRecord(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				attendanceRecord.setAttendanceId(genId);
				this.add(attendanceRecord);
			}else{
				attendanceRecord.setAttendanceId(Long.parseLong(cmd.getBusinessKey()));
				this.update(attendanceRecord);
			}
			cmd.setBusinessKey(attendanceRecord.getAttendanceId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取AttendanceRecord对象
	 * @param json
	 * @return
	 */
	public AttendanceRecord getAttendanceRecord(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		AttendanceRecord attendanceRecord = JSONObjectUtil.toBean(json, AttendanceRecord.class);
		return attendanceRecord;
	}
	
	/**
	 * 保存 考勤记录表 信息
	 * @param attendanceRecord
	 */
	public void save(AttendanceRecord attendanceRecord){
		Long id=attendanceRecord.getAttendanceId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			attendanceRecord.setAttendanceId(id);
			this.add(attendanceRecord);
		}
		else{
			this.update(attendanceRecord);
		}
	}
	
	/**
	 * 获取当前数据库中同步的最大考勤打卡记录时间
	 * @return
	 */
	public Date getMaxSyncCheckTime() {
		Date date = (Date) dao.getOne("getMaxSyncCheckTime", null);
		if (date == null) {
			// 如果数据库还没有记录，则从上个月1号开始
			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_MONTH, 1);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			c.add(Calendar.MONTH, -1);
			date = c.getTime();
		}
		return date;
	}

	/**
	 * 保存同步的考勤数据
	 * @param list
	 */
	public void saveCheckRecords(List<CheckInout> list) {
		List<AttendanceRecord> records = new ArrayList<AttendanceRecord>();
		for (CheckInout checkInout : list) {
			AttendanceRecord record = genRecord(checkInout);
			records.add(record);
			// 每一千条保存一次
			if (records.size() == 1000) {
				dao.batchAdd(records);
				records = new ArrayList<AttendanceRecord>();
			}
		}
		if (records.size() > 0) {
			dao.batchAdd(records);
		}
	}
	
	/**
	 * 通过同步的考勤数据生成考勤记录对象
	 * @param checkInout
	 * @return
	 */
	public AttendanceRecord genRecord(CheckInout checkInout) {
		AttendanceRecord record = new AttendanceRecord();
		BeanUtils.copyProperties(record, checkInout);
		record.setAttendanceId(UniqueIdUtil.genId());
		record.setCheckFrom(AttendanceRecord.FROM_SYNC);
		record.setCreatetime(new Date());
		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtils.isNotEmpty(checkInout.getBadgenumber())) {
			params.put("attendNo", checkInout.getBadgenumber());
		}
		if (StringUtils.isNotEmpty(checkInout.getStaffNo())) {
			params.put("staffNo", checkInout.getStaffNo());
		}
		if (StringUtils.isNotEmpty(checkInout.getSsn())) {
			params.put("ssn", checkInout.getSsn());
		}
		SysUser user = userDao.getUnique("getAttendanceRationUser", params);
		if (user != null) {
			// AttendanceRelation relation = relationDao.getByBadgenumber();
			Long userId = user.getUserId();
			//SysOrg org = orgDao.getPrimaryOrgByUserId(userId);
			//record.setDepartment(org.getOrgName());
			record.setUserId(userId);
		}
		return record;
	}

	/**
	 * 判断考勤信息是否存在
	 * @param list
	 */
	public boolean isAttendanceExist(CheckInout inout){
		// 构造查询条件
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("badgenumber", inout.getBadgenumber());
		params.put("checkTime", inout.getCheckTime());
		// 是否存在
		Integer count = Integer.valueOf(dao.getOne("isAttendanceExist", params).toString());
		return count > 0;
	}
	
	/**
	 * 获取某个用户某天的考勤记录
	 * @param userId
	 * @param date
	 * @return
	 */
	public List<AttendanceRecord> getRecordList(Long userId, Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return getRecordList(userId, DateUtil.setStartDay(c).getTime(), DateUtil.setEndDay(c).getTime());
	}
	
	/**
	 * 获取某个用户在指定时间段的考勤记录
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<AttendanceRecord> getRecordList(Long userId, Date startTime, Date endTime) {
		QueryFilter queryFilter = new QueryFilter(new JSONObject());
		queryFilter.addFilter("userId", userId);
		queryFilter.addFilter("startTime", startTime);
		queryFilter.addFilter("endTime", endTime);
		return dao.getBySqlKey("getRecordList", queryFilter);
	}
}
