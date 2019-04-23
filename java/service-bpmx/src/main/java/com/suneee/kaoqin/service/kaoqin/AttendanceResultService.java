package com.suneee.kaoqin.service.kaoqin;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.dao.kaoqin.AttendanceRecordDao;
import com.suneee.kaoqin.dao.kaoqin.AttendanceResultDao;
import com.suneee.kaoqin.dao.kaoqin.ExemmptSettingDao;
import com.suneee.kaoqin.dao.kaoqin.ShiftTimeDao;
import com.suneee.kaoqin.model.kaoqin.AttendanceResult;
import com.suneee.platform.model.system.SysUser;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 *<pre>
 * 对象功能:考勤结果表 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:mikel
 * 创建时间:2017-05-04 10:48:13
 *</pre>
 */
@Service
public class AttendanceResultService extends BaseService<AttendanceResult>
{
	@Resource
	private AttendanceResultDao dao;
	@Resource
	private AttendanceRecordDao recordDao;
	@Resource
	private ShiftTimeDao timeDao;
	@Resource
	private ExemmptSettingDao exemmptSettingDao;
	
	
	
	public AttendanceResultService()
	{
	}
	
	@Override
	protected IEntityDao<AttendanceResult, Long> getEntityDao()
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
			AttendanceResult attendanceResult=getAttendanceResult(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				attendanceResult.setResultId(genId);
				this.add(attendanceResult);
			}else{
				attendanceResult.setResultId(Long.parseLong(cmd.getBusinessKey()));
				this.update(attendanceResult);
			}
			cmd.setBusinessKey(attendanceResult.getResultId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取AttendanceResult对象
	 * @param json
	 * @return
	 */
	public AttendanceResult getAttendanceResult(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		AttendanceResult attendanceResult = JSONObjectUtil.toBean(json, AttendanceResult.class);
		return attendanceResult;
	}
	
	/**
	 * 保存 考勤结果表 信息
	 * @param attendanceResult
	 */
	public void save(AttendanceResult attendanceResult){
		Long id=attendanceResult.getResultId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			attendanceResult.setResultId(id);
			this.add(attendanceResult);
		}
		else{
			this.update(attendanceResult);
		}
	}

	/**
	 * 获取按天汇总的考勤结果
	 * @param queryFilter
	 * @return
	 */
	public List<AttendanceResult> getDayList(QueryFilter queryFilter) {
		return dao.getBySqlKey("getDayList", queryFilter);
	}
	
	/**
	 * 获取某个用户在某个班次某天的考勤结果记录
	 * @param userId
	 * @param date
	 * @return
	 */
	public List<AttendanceResult> getResultList(Long userId, Long shiftId, Date date) {
		QueryFilter queryFilter = new QueryFilter(new JSONObject());
		queryFilter.addFilter("userId", userId);
		queryFilter.addFilter("shiftId", shiftId);
		queryFilter.addFilter("attendanceDate", date);
		queryFilter.getFilters().put("orderField", "seq_key");
		queryFilter.getFilters().put("orderSeq", "asc");
		return dao.getAll(queryFilter);
	}
	
	/**
	 * 获取某个用户在某个班次指定时间段的考勤结果记录
	 * @param userId
	 * @param date
	 * @return
	 */
	public List<AttendanceResult> getResultList(Long userId, Long shiftId, Date startTime, Date endTime) {
		QueryFilter queryFilter = new QueryFilter(new JSONObject());
		queryFilter.addFilter("userId", userId);
		queryFilter.addFilter("shiftId", shiftId);
		queryFilter.addFilter("beginstandardTime", startTime);
		queryFilter.addFilter("endstandardTime", endTime);
		queryFilter.getFilters().put("orderField", "seq_key");
		queryFilter.getFilters().put("orderSeq", "asc");
		return dao.getAll(queryFilter);
	}

	public List<AttendanceResult> getProcessResultList(Date startTime, Date endTime) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		return dao.getBySqlKey("getProcessResultList", params);
	}

	/**
	 * 获取用户汇总的考勤数据
	 * @param queryFilter
	 * @return
	 */
	public List<AttendanceResult> getSummaryList(QueryFilter queryFilter) {
		return dao.getBySqlKey("getSummaryList", queryFilter);
	}

	/**
	 * 获取用户汇总的明细考勤数据
	 * @param queryFilter
	 * @return
	 */
	public List<AttendanceResult> getDetailList(QueryFilter queryFilter) {
		return dao.getBySqlKey("getDetailList", queryFilter);
	}

	/**
	 * 根据汇总查询用户的明细考勤数据
	 * @param queryFilter
	 * @return
	 */
	public List<AttendanceResult> getUserDetailList(QueryFilter queryFilter) {
		return dao.getBySqlKey("getUserDetailList", queryFilter);
	}

	/**
	 * 根据日期获取加班人员
	 * @param time
	 * @return
	 */
	public List<SysUser> getOvertimeUsersOfDay(Date date) {
		return dao.getBySqlKeyGenericity("getOvertimeUsersOfDay", date);
	}

	public List<AttendanceResult> getMonthAttendance(QueryFilter filter) {
		return dao.getBySqlKey("getMonthAttendance", filter);
	}

	public void clearAllComputedResults(Calendar day) {
		// 删除所有工作日考勤计算结果
		dao.delBySqlKey("removeAllWorkingComputedResult", day.getTime());
		// 还原流程相关的结果状态
		dao.update("resetProcessResult", day.getTime());
	}

	public List<AttendanceResult> getOvertimeList(QueryFilter filter) {
		return dao.getBySqlKey("getOvertimeList", filter);
	}
}
