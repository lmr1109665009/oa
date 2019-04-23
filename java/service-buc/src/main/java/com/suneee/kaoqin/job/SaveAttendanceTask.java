package com.suneee.kaoqin.job;

import java.util.ArrayList;
import java.util.List;

import com.suneee.core.util.AppUtil;
import com.suneee.kaoqin.service.kaoqin.AttendanceRecordService;
import com.suneee.core.util.AppUtil;
import com.suneee.ucp.base.model.kaoqin.CheckInout;

/**
 * @author Administrator
 *
 */
public class SaveAttendanceTask implements Runnable{
	
	private List<CheckInout> checkList;
	
	private int start;
	
	private int handleNum;
	
	/**
	 * @param list 需要处理的考勤记录列表
	 * @param start 开始处理的记录索引
	 * @param handleNum 要处理的记录条数
	 */
	public SaveAttendanceTask(List<CheckInout> list, int start, int handleNum){
		this.checkList = list;
		this.start = start;
		this.handleNum = handleNum;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		AttendanceRecordService attendanceService = AppUtil.getBean(AttendanceRecordService.class);
		// 保存考勤数据到本系统数据库
		List<CheckInout> addingList = new ArrayList<CheckInout>();
		CheckInout check = null;
		for(int i = 0; i < handleNum; i++){
			check = checkList.get(i + start);
			// 判断考勤信息是否已经能够存在
			boolean isExist = attendanceService.isAttendanceExist(check);
			// 不存在则保存考勤记录
			if(!isExist){
				addingList.add(check);
			}
			// 考勤记录每1000条插入数据库
			if(addingList.size() == 1000){
				attendanceService.saveCheckRecords(addingList);
				addingList = new ArrayList<CheckInout>();
			}
		}
		// 不足一千条的记录入库
		if (addingList.size() > 0) {
			attendanceService.saveCheckRecords(addingList);
		}
	}

}
