package com.suneee.platform.service.worktime;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.worktime.OverTimeDao;
import com.suneee.platform.model.worktime.OverTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 对象功能:加班情况 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-02-20 09:25:52
 */
@Service
public class OverTimeService extends BaseService<OverTime>
{
	@Resource
	private OverTimeDao dao;
	
	public OverTimeService()
	{
	}
	
	@Override
	protected IEntityDao<OverTime, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 *  取得工作日类型
	 * @return
	 */
	public List getWorkType()
	{
		List typelist = new ArrayList();
		Map map = new HashMap();
		map.put("typeId", "1");
		map.put("typeName", "加班");
		typelist.add(map);
		map = new HashMap();
		map.put("typeId", "2");
		map.put("typeName", "请假");
		typelist.add(map);
		return typelist;
	}
	
	/**
	 * 取得一段时间内的加班/请假情况列表
	 * @param userId 用户ID
	 * @param type   工作变更类型 1，加班 2，请假
	 * @param startTime  任务开始时间
	 * @param endTime    结束时间
	 * @return
	 */
	public List<OverTime> getListByUserId(long userId, int type, Date startTime, Date endTime){
		return dao.getListByUserId(userId, type, startTime, endTime);
	}
	
	
	/**
	 * 取得一开始时间（有关的\之后）用户的加班/请假情况列表
	 * @param startTime  开始时间
	 * @param userId  用户ID
	 * @param type	工作变更类型 1，加班 2，请假
	 * @return
	 */
	public List<OverTime> getListByStart(Date startTime, long userId, int type) {
		return dao.getListByStart(startTime,userId,type);
	}
	
}
