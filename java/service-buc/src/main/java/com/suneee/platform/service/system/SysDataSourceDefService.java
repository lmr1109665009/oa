package com.suneee.platform.service.system;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.system.SysDataSourceDefDao;
import com.suneee.platform.model.system.SysDataSourceDef;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.model.system.SysDataSourceDef;
import com.suneee.platform.dao.system.SysDataSourceDefDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.core.util.StringUtil;
import com.suneee.core.bpm.model.ProcessCmd;
import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * <pre>
 * 对象功能:SYS_DATA_SOURCE_DEF Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:Aschs
 * 创建时间:2014-08-20 11:10:07
 * </pre>
 */
@Service
public class SysDataSourceDefService extends BaseService<SysDataSourceDef> {
	@Resource
	private SysDataSourceDefDao dao;


	public SysDataSourceDefService() {
	}

	@Override
	protected IEntityDao<SysDataSourceDef, Long> getEntityDao() {
		return dao;
	}


	/**
	 * 根据json字符串获取SysDataSourceDef对象
	 * 
	 * @param json
	 * @return
	 */
	public SysDataSourceDef getSysDataSourceDef(String json) {
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher((new String[] { "yyyy-MM-dd" })));
		if (StringUtil.isEmpty(json))
			return null;
		JSONObject obj = JSONObject.fromObject(json);
		SysDataSourceDef sysDataSourceDef = (SysDataSourceDef) JSONObject.toBean(obj, SysDataSourceDef.class);
		return sysDataSourceDef;
	}

	/**
	 * TODO方法名称描述
	 * 
	 * @param classPath
	 * @return JSONArray
	 * @exception
	 * @since 1.0.0
	 */
	public JSONArray getHasSetterFieldsJsonArray(String classPath) {
		JSONArray jsonArray = new JSONArray();
		Class<?> _class;
		try {
			_class = Class.forName(classPath);
			for (Method method : _class.getMethods()) {
				if (!method.getName().startsWith("set"))
					continue;
				String fname=method.getName().replace("set", "");
				fname=fname.replaceFirst(fname.substring(0, 1),fname.substring(0, 1).toLowerCase());
				String ftype=method.getParameterTypes()[0].getName();
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("name", fname);
				jsonObject.accumulate("comment", fname);
				jsonObject.accumulate("type", ftype);
				jsonObject.accumulate("baseAttr", "0");
				jsonArray.add(jsonObject);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return jsonArray;
	}

	public static void main(String[] args) throws ClassNotFoundException {
		Class<?> _class = Class.forName("com.suneee.core.db.CustomDruidDataSource");
		//Class<?> _class = Class.forName("com.alibaba.druid.pool.DruidDataSource");
		for (Method method : _class.getMethods()) {
			if (!method.getName().startsWith("set"))
				continue;
			String fname=method.getName().replace("set", "");
			fname=fname.replaceFirst(fname.substring(0, 1),fname.substring(0, 1).toLowerCase());
			System.out.println(fname+"-"+(method.getParameterTypes().length>0?method.getParameterTypes()[0].getName():""));
		}
	}
}
