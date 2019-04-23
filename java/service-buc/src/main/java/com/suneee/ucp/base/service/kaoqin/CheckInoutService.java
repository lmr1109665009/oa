/**
 * 
 */
package com.suneee.ucp.base.service.kaoqin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.DateUtil;
import com.suneee.kaoqin.job.SaveAttendanceTask;
import com.suneee.ucp.base.dao.kaoqin.CheckInoutDao;
import com.suneee.ucp.base.model.kaoqin.CheckInout;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.base.util.DateUtils;

/**
 * 考勤机考勤信息服务类
 * @author xiongxianyun
 *
 */
@Service
public class CheckInoutService extends UcpBaseService<CheckInout>{
	@Resource
	private CheckInoutDao dao;
	@Resource
	private ThreadPoolTaskExecutor threadPool;
	
	@Override
	protected IEntityDao<CheckInout, Long> getEntityDao() {
		return dao;
	}
	
	/**
	 * 获取考勤机数据库的所有考勤信息
	 * @return
	 */
	public List<CheckInout> getAllList(){
		Map<String, String> params = new HashMap<String, String>();
		params.put("checkTime", DateFormatUtil.format(DateUtils.getMonthBefore(new Date(), -1).getTime(), DateUtils.FORMAT_DATE_NODAY));
		return dao.getAll(params);
	}

	/**
	 * 获取从指定时间开始的考勤数据
	 * @param startTime
	 * @return
	 */
	public List<CheckInout> getSyncDataFromTime(Date startTime) {
		return dao.getSyncDataFromTime(startTime);
	}

	/**
	 * 同步从某个时间开始之后的考勤数据
	 * @param startTime
	 */
	public void syncAttendance(Date startTime) {
		// 从考勤机数据库中获取考勤数据
		List<CheckInout> list = getSyncDataFromTime(startTime);
		int totalCount = list.size();
		// 线程数
		int threadNum = threadPool.getMaxPoolSize();
		// 每个线程处理的数据条数
		int handleNum = 0;
		if(totalCount < threadNum){
			threadNum = 1;
			handleNum = totalCount;
		}else{
			handleNum = totalCount/threadNum;
		}
		// 向线程池中添加线程任务
		for(int i = 1; i <= threadNum; i++){
			threadPool.execute(new SaveAttendanceTask(list, handleNum*(i-1), 
					i == threadNum ? (handleNum + totalCount%threadNum) : handleNum));
		}
	}
}
