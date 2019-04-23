package com.suneee.platform.service.system;

import com.suneee.core.model.CurrentUser;
import com.suneee.core.util.AppUtil;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CurrentUserService {

	/**
	 * 根据当前用户对象获取当前用户身份。
	 * 
	 * @param currentUser
	 * @return
	 */
	public Map<String, List<Long>> getUserRelation(CurrentUser currentUser) {
		Map<String, List<Long>> map = new HashMap<String, List<Long>>();
		try {
			List<ICurUserService> objectList = getUserRelation();
			for (ICurUserService curObj : objectList) {
				List<Long> list = curObj.getByCurUser(currentUser);
				map.put(curObj.getKey(), list);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * 根据当前用户对象获取当前用户身份。
	 * 
	 * @return
	 */
	public List<String> getUserListKey() {
		List<String> list = new ArrayList<String>();
		try {
			List<ICurUserService> objectList = getUserRelation();
			for (ICurUserService curObj : objectList) {
				String key = curObj.getKey();
				list.add(key);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取权限的实现方法，这里返回算法的对应实现。
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 */
	public List<ICurUserService> getUserRelation()
			throws ClassNotFoundException {
		Map<String, Object> instMap = AppUtil
				.getImplInstance(ICurUserService.class);
		Collection<Object> instCollect = instMap.values();
		List<ICurUserService> list = new ArrayList<ICurUserService>();
		for (Object obj : instCollect) {
			list.add((ICurUserService) obj);
		}
		
		return list;
	}
	
	/**
	 * 根据relationKey获取ICurUserService，relationKey 在表app-beans.xml中进行定义。
	 * @param relationKey
	 * @return
	 */
	public List<ICurUserService> getUserRelationByKey(String relationKey){
		List<ICurUserService> list =(List<ICurUserService>) AppUtil.getBean(relationKey);
		return list;
	}
	
	/**
	 * 获取指定人的关系对象。
	 * @param currentUser
	 * @param relationKey
	 * @return
	 */
	public Map<String, List<Long>> getUserRelation(CurrentUser currentUser,String relationKey) {
		Map<String, List<Long>> map = new HashMap<String, List<Long>>();
		List<ICurUserService> objectList = getUserRelationByKey(relationKey);
		for (ICurUserService curObj : objectList) {
			List<Long> list = curObj.getByCurUser(currentUser);
			map.put(curObj.getKey(), list);
		}
		return map;
	}

}
