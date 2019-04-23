package com.suneee.kaoqin.job;

import com.suneee.core.scheduler.BaseJob;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.DateUtil;
import com.suneee.ucp.base.service.kaoqin.CheckInoutService;
import org.quartz.JobExecutionContext;

import java.util.Calendar;
import java.util.Map;

/**
 * 同步考勤数据
 * @author mikel
 *
 */
public class SyncAttendanceJob extends BaseJob {

	// 同步到的考勤数据最大打卡时间
	// private static Date MAX_SYNC_CHECK_TIME = null;
	
	/**
     * @param context
     * @throws Exception 
     * @see BaseJob#executeJob(JobExecutionContext)
     */ 
	@Override
	public void executeJob(JobExecutionContext context) throws Exception {
		/*AttendanceRecordService recordService = (AttendanceRecordService)AppUtil.getBean(AttendanceRecordService.class);
		if (MAX_SYNC_CHECK_TIME == null) {
			MAX_SYNC_CHECK_TIME = recordService.getMaxSyncCheckTime();
		}
		CheckInoutService checkService = (CheckInoutService)AppUtil.getBean(CheckInoutService.class);
		try {
			List<CheckInout> list = checkService.getSyncDataFromTime(MAX_SYNC_CHECK_TIME);
			if (list.size() > 0) {
				MAX_SYNC_CHECK_TIME = list.get(0).getCheckTime();
			}
			recordService.saveCheckRecords(list);
		} catch (Exception e) {
			e.printStackTrace();
		}*/

		Map<String, Object> params = context.getJobDetail().getJobDataMap();
		Calendar startDay = Calendar.getInstance();
		Object startDate = params.get("startDate");
		if (startDate != null) {
			try {
				startDay.setTime(DateUtil.parseDate(startDate.toString()));
			} catch (Exception e) {
			}
		} else {
			Object backDays = params.get("backDays");
			if (backDays != null) {
				try {
					startDay.add(Calendar.DAY_OF_MONTH, -Integer.valueOf(backDays.toString()));
				} catch (Exception e) {
				}
			} else {
				// 从15天前的数据开始取
				startDay.add(Calendar.DAY_OF_MONTH, -15);
			}
		}
		CheckInoutService service = (CheckInoutService) AppUtil.getBean(CheckInoutService.class);
		service.syncAttendance(startDay.getTime());
	}

}
