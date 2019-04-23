package com.suneee.platform.service.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.system.SysPopupRemindDao;
import com.suneee.platform.model.system.SysPopupRemind;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.util.ServiceUtil;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 对象功能:sys_popup_remind Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-03-18 11:36:25
 * </pre>
 */
@Service
public class SysPopupRemindService extends BaseService<SysPopupRemind> {
	@Resource
	private SysPopupRemindDao dao;
	@Resource
	private CurrentUserService currentUserService;

	public SysPopupRemindService() {
	}

	@Override
	protected IEntityDao<SysPopupRemind, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 添加
	 */
	@Override
	public void add(SysPopupRemind sysPopupRemind) {
		// 设置创建者
		SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
		JSONObject json = new JSONObject();
		json.put("id", sysUser.getUserId());
		json.put("name", sysUser.getUsername());
		sysPopupRemind.setCreator(json.toString());
		super.add(sysPopupRemind);
	}
	
	/**
	 * 更新状态
	 * @param ids	：以,分割的ids
	 * @param enabled 
	 * void
	 * @exception 
	 * @since  1.0.0
	 */
	public void updateEnabled(String[] ids,Short enabled){
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("ids", ids);
		map.put("enabled", enabled);
		dao.update("updateEnabled", map);
	}
	
	/**
	 * 获取对一个用户生效提醒列表 已过滤了不启动的remind列表
	 * 
	 * @param user
	 * @return List<SysPopupRemind>
	 * @exception
	 * @since 1.0.0
	 */
	public List<SysPopupRemind> getByUser(SysUser user) {
		//这一步很关键 用伪代码来表达map 的内容就是
		//类型 然后 接着一堆对应的ID eg：
		//user : 1,2,3,4... 
		//org : 1,2,3,4...
		//然后 根据上面的对应ID 和 权限配置的ID进行比较，看满不满足权限条件
		Map<String, List<Long>> map = currentUserService.getUserRelation(ServiceUtil.getCurrentUser());
		//开始坚持map中返回的ID符不符合授权设置
		List<SysPopupRemind> sprs = dao.getByUser(map);
		return sprs;
	}
	
	/**
	 * 获取所用启动中的提醒列表
	 */
	public List<SysPopupRemind> getByIds(String[] ids) {
		List<SysPopupRemind> sprs = new ArrayList<SysPopupRemind>();
		for(String s :ids){
			Long id = Long.parseLong(s);
			SysPopupRemind spr = dao.getByIds(id);
			if(spr==null) continue;
			sprs.add(spr);
		}
		return sprs;
	}
}
