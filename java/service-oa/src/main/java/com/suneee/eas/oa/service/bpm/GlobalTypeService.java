/**
 * @Title: GlobalTypeService.java 
 * @Package com.suneee.eas.oa.service.bpm 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.bpm;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.suneee.platform.model.system.GlobalType;

/**
 * @ClassName: GlobalTypeService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-21 14:39:52 
 *
 */
public interface GlobalTypeService {
	public GlobalType getSceneTypeById(Long typeId) throws UnsupportedEncodingException;
	
	public List<GlobalType> getSceneTypeList() throws UnsupportedEncodingException ;
}
