/**
 * 
 */
package com.suneee.ucp.base.dao.kaoqin;

import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.base.model.kaoqin.CheckInout;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考勤数据操作类
 * @author xiongxianyun
 *
 */
@Repository
public class CheckInoutDao extends UcpBaseDao<CheckInout>{

	@Override
	public Class<CheckInout> getEntityClass() {
		return CheckInout.class;
	}
	
	/**
	 * 查询满足条件的所有考勤记录
	 * @param params
	 * @return
	 */
	public List<CheckInout> getAll(Map<String, String> params){
		return this.getBySqlKey("getAll", params);
	}

	/**
	 * 获取从指定时间开始的考勤数据
	 * @param startTime
	 * @return
	 */
	public List<CheckInout> getSyncDataFromTime(Date startTime) {
		Map<String, String> params = new HashMap<String, String>();
//		params.put("checkTime", DateUtil.formatDate(startTime, "yyyy-MM-dd HH:mm:ss.SSS"));
		return this.getBySqlKey("getSyncList", params);
	}

}
