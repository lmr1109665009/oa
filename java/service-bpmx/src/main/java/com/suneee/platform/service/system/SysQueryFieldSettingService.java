package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.SysQueryFieldSettingDao;
import com.suneee.platform.model.system.SysQueryFieldSetting;
import com.suneee.platform.model.system.SysQueryMetaField;
import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * <pre>
 * 对象功能:视图自定义设定 Service类
 * 开发公司:宏天软件
 * 开发人员:ray
 * 创建时间:2015-06-08 16:02:04
 * </pre>
 */
@Service
public class SysQueryFieldSettingService extends
        BaseService<SysQueryFieldSetting> {
	@Resource
	private SysQueryFieldSettingDao dao;

	public SysQueryFieldSettingService() {
	}

	@Override
	protected IEntityDao<SysQueryFieldSetting, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 根据json字符串获取SysQueryFieldSetting对象
	 * 
	 * @param json
	 * @return
	 */
	public SysQueryFieldSetting getSysQueryFieldSetting(String json) {
		JSONUtils.getMorpherRegistry().registerMorpher(
				new DateMorpher((new String[] { "yyyy-MM-dd" })));
		if (StringUtil.isEmpty(json))
			return null;
		JSONObject obj = JSONObject.fromObject(json);
		SysQueryFieldSetting sysQueryFieldSetting = (SysQueryFieldSetting) JSONObject
				.toBean(obj, SysQueryFieldSetting.class);
		return sysQueryFieldSetting;
	}

	/**
	 * 保存 视图自定义设定 信息
	 * 
	 * @param sysQueryFieldSetting
	 */
	public void save(SysQueryFieldSetting sysQueryFieldSetting) {
		Long id = sysQueryFieldSetting.getId();
		if (id == null || id == 0) {
			id = UniqueIdUtil.genId();
			sysQueryFieldSetting.setId(id);
			this.add(sysQueryFieldSetting);
		} else {
			this.update(sysQueryFieldSetting);
		}
	}

	public void removeBySysQueryViewId(Long viewId) {
		dao.removeBySysQueryViewId(viewId);
	}

	public List<SysQueryFieldSetting> getBySysQueryViewId(Long viewId) {
		return dao.getBySysQueryViewId(viewId);
	}
	
	/**
	 * 获取最新要显示的字段
	 * @param sysQueryMetaFields
	 * @param sysQueryFieldSettingList
	 * @return
	 */
	public List<SysQueryFieldSetting> getDisplayFields(List<SysQueryMetaField> sysQueryMetaFieldList,
			List<SysQueryFieldSetting> sysQueryFieldSettingList) {
		
		Map<String,SysQueryFieldSetting> map = this.metaFieldsListToFieldsMap(sysQueryMetaFieldList);
		
		for(SysQueryMetaField metaField : sysQueryMetaFieldList){
			
			for(SysQueryFieldSetting fieldSetting : sysQueryFieldSettingList){
				if(metaField.getName().equalsIgnoreCase(fieldSetting.getFieldName())){
					map.remove(fieldSetting.getFieldName());			//	清除页面有的
					map.put(fieldSetting.getFieldName(), fieldSetting);	//	加上页面有的
					continue;
				}
			}
		}
		
		List<SysQueryFieldSetting> list = this.converToListForFieldSetting(map);
		
		return this.getSortForFieldSettingList(list);
	}
	
	/**
	 * sysQueryMetaFields to SysQueryFieldSetting
	 * @param sysQueryMetaFields
	 * @return
	 */
	private Map<String,SysQueryFieldSetting> metaFieldsListToFieldsMap(List<SysQueryMetaField> sysQueryMetaFieldList) {
		Map<String,SysQueryFieldSetting> map = new LinkedHashMap<String,SysQueryFieldSetting>();
		for(SysQueryMetaField metaField : sysQueryMetaFieldList){
			SysQueryFieldSetting fieldSetting = new SysQueryFieldSetting();
			fieldSetting.setFieldName(metaField.getFieldName());	// 继承meta的一些属性
			fieldSetting.setSn(metaField.getSn());
			fieldSetting.setWidth(metaField.getWidth());
			map.put(fieldSetting.getFieldName(), fieldSetting);
		}
		return map;
	}
	
	private List<SysQueryFieldSetting> converToListForFieldSetting(
			Map<String, SysQueryFieldSetting> map) {
		List<SysQueryFieldSetting> list = new LinkedList<SysQueryFieldSetting>();
		for(Map.Entry<String, SysQueryFieldSetting> entry:map.entrySet()){   
		    list.add(entry.getValue());
		}   
		return list;
	}
	
	/**
	 * 排序后的列表（升序排序）
	 * @param list
	 * @return
	 */
	public List<SysQueryFieldSetting> getSortForFieldSettingList(List<SysQueryFieldSetting> list){
        Collections.sort(list, new Comparator<SysQueryFieldSetting>() {  
            public int compare(SysQueryFieldSetting arg0, SysQueryFieldSetting arg1) {  
                short sn0 = arg0.getSn()==null?0:arg0.getSn();  
                short sn1 = arg1.getSn()==null?0:arg1.getSn();  
                if (sn0 > sn1) {  
                    return 1;  
                } else if (sn1 == sn0) {  
                    return 0;  
                } else {  
                    return -1;  
                }  
            }  
        });  
        return list;
	}
	
}













