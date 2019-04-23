package com.suneee.platform.service.ats.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import com.suneee.platform.service.ats.IAtsCalculate;
import com.suneee.platform.service.ats.IAtsCalculate;

/**
 * 
 * <pre>
 * 对象功能:考勤计算集合
 * 开发公司:广州宏天软件有限公司
 * 开发人员:hugh zhuang
 * 创建时间:2015-07-10 09:16:09
 * </pre>
 * 
 */
public class AtsCalculateContainer {

	private Map<String, IAtsCalculate> atsCalculateMap = new LinkedHashMap<String, IAtsCalculate>();

	public Map<String, IAtsCalculate> getAtsCalculateMap() {
		return atsCalculateMap;
	}

	public void setAtsCalculateMap(Map<String, IAtsCalculate> atsCalculateMap) {
		this.atsCalculateMap = atsCalculateMap;
	}

	public IAtsCalculate getHandler(String key) {
		return atsCalculateMap.get(key);
	}

}
