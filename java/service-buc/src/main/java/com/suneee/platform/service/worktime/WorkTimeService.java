package com.suneee.platform.service.worktime;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.worktime.WorkTimeDao;
import com.suneee.platform.model.worktime.WorkTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 对象功能:班次时间 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:cjj
 * 创建时间:2012-02-22 16:58:15
 */
@Service
public class WorkTimeService extends BaseService<WorkTime>
{
	@Resource
	private WorkTimeDao dao;
	
	public WorkTimeService()
	{
	}
	
	@Override
	protected IEntityDao<WorkTime, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 根据settingId取worktime
	 * @param settingId
	 * @return
	 */
	public List<WorkTime> getBySettingId(Long settingId){
		return dao.getBySettingId(String.valueOf(settingId));
	}
	
	public void workTimeAdd(Long settingId, String[] startTime, String[] endTime, String[] memo){
		
		if(startTime!=null && endTime!=null){
			
			dao.delBySqlKey("delBySettingId", settingId);
			
			for(int idx=0;idx<startTime.length;idx++){
				WorkTime worktime = new WorkTime();
				try {
					worktime.setId(UniqueIdUtil.genId());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				worktime.setSettingId(settingId);
				worktime.setStartTime(startTime[idx]);
				worktime.setEndTime(endTime[idx]);
				worktime.setMemo(memo[idx]);
				dao.add(worktime);
			}
		}
	}
}
