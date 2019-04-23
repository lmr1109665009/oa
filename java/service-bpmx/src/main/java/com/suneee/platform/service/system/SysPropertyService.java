package com.suneee.platform.service.system;

import com.suneee.core.api.system.IPropertyService;
import com.suneee.core.cache.ICache;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.eas.common.utils.ContextUtil;
import com.suneee.platform.dao.system.SysPropertyDao;
import com.suneee.platform.model.system.SysProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 对象功能:系统配置参数表 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ouxb
 * 创建时间:2015-04-16 11:20:41
 * </pre>
 */
@Service
public class SysPropertyService extends BaseService<SysProperty> implements IPropertyService {
	@Resource
	private SysPropertyDao dao;


	public SysPropertyService() {
	}

	@Override
	protected IEntityDao<SysProperty, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 保存 系统配置参数表 信息
	 * <pre>
	 * 保存时清空缓存。
	 * </pre>
	 * @param sysProperty
	 */
	public void save(SysProperty sysProperty) {
		this.update(sysProperty);
		
		reloadProperty();
	}
	
	/**
	 * 重新加载系统参数。
	 * @return
	 */
	private  Map<String,String>  reloadProperty(){
		ICache cache= AppUtil.getBean(ICache.class);
		SysPropertyService propertyService=AppUtil.getBean(SysPropertyService.class);
		Map<String,String> map=new HashMap<String, String>();
		List<SysProperty> list=propertyService.getAll();
		for(SysProperty property:list){
			map.put(property.getAlias().toLowerCase(), property.getValue());
		}
		String runtimeEnv= ContextUtil.getRuntimeEnv();
		cache.add(SysProperty.PropertyCache+"_"+runtimeEnv, map);
		return map;
	}
	
	/**
	 * 根据别名获取参数值。
	 * @param alias
	 */
	public  String getByAlias(String alias){
		String runtimeEnv= ContextUtil.getRuntimeEnv();
		ICache cache=AppUtil.getBean(ICache.class);
		Map<String,String> map=(Map<String,String>) cache.getByKey(SysProperty.PropertyCache+"_"+runtimeEnv);
		if(BeanUtils.isEmpty(map)){
			map=reloadProperty();
		}
		return map.get(alias.toLowerCase());
	}
	
	public  String getByAlias(String alias,String defaultValue){
		String val=getByAlias(alias);
		if(StringUtil.isEmpty(val)) return defaultValue;
		return val;
	}
	
	/**
	 * 根据别名获取整形参数值。
	 * @param alias
	 * @return
	 */
	public  Integer getIntByAlias(String alias){
		String val= getByAlias(alias);
		if(StringUtil.isEmpty(val)) return 0;
		Integer rtn=Integer.parseInt(val);
		return rtn;
	}
	
	/**
	 * 根据别名获取整形的参数值。
	 * @param alias
	 * @param defaulValue
	 * @return
	 */
	public  Integer getIntByAlias(String alias,Integer defaulValue){
		String val= getByAlias(alias);
		if(StringUtil.isEmpty(val)) return defaulValue;
		Integer rtn=Integer.parseInt(val);
		return rtn;
	}
	/**
	 * 根据别名获取长整型参数。
	 * @param alias
	 * @return
	 */
	public  Long getLongByAlias(String alias){
		String val= getByAlias(alias);
		Long rtn=Long.parseLong(val);
		return rtn;
	}
	
	/**
	 * 根据别名获取布尔型值。
	 * @param alias
	 * @return
	 */
	public  boolean getBooleanByAlias(String alias){
		String val= getByAlias(alias);
		return Boolean.parseBoolean(val);
	}
	/**
	 * 根据别名获取布尔型值。 1 为true,0为false
	 * @param alias
	 * @return
	 */
	public  boolean getBooleanByAlias(String alias,boolean defaulValue){
		String val= getByAlias(alias);
		if(StringUtil.isEmpty(val)) return defaulValue;
		
		if("1".equals(val)) return true;
		
		return Boolean.parseBoolean(val);
	}

}
