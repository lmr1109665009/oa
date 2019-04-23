/**
 * @Title: BroadcastService.java 
 * @Package com.suneee.eas.oa.service.broadcast 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.broadcast;

import java.util.List;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.broadcast.Broadcast;

/**
 * @ClassName: BroadcastService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-20 15:26:15 
 *
 */
public interface BroadcastService extends BaseService<Broadcast>{
	public List<Broadcast> getByStatus(String enterpriseCode);
	
	/** 批量删除轮播图
	 * @param ids
	 */
	public void delByIds(Long[] ids);
}
