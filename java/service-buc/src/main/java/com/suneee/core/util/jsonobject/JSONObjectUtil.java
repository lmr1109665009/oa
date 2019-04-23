/**
 * 描述：TODO
 * 包名：com.suneee.base.core.util
 * 文件名：JSONObjectUtil.java
 * 作者：User-mailto:chensx@jee-soft.cn
 * 日期2014-7-29-下午3:57:22
 *  2014广州宏天软件有限公司版权所有
 * 
 */
package com.suneee.core.util.jsonobject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.jsonobject.support.BooleanSerializer;
import com.suneee.core.util.jsonobject.support.DateSerializer;
import com.suneee.core.util.jsonobject.support.JsonObjectSerializer;
import com.suneee.core.util.jsonobject.support.SuperclassExclusionStrategy;
import com.suneee.core.web.util.RequestUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * <pre>
 * 描述：TODO
 * 构建组：x5-base-core
 * 作者：csx
 * 邮箱:chensx@jee-soft.cn
 * 日期:2014-7-29-下午3:57:22
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
public class JSONObjectUtil {
	/**
	 * @param jsonObject
	 * @param cls
	 * @return C
	 * @exception
	 * @since 1.0.0
	 */
	public static <C> C toBean(JSONObject jsonObject, Class<C> cls) {
		return toBean(jsonObject.toString(), cls);
	}

	/**
	 * 把jsonStr生成一个cls对象
	 * 
	 * @param jsonObject
	 * @param cls
	 * @return C
	 * @exception
	 * @since 1.0.0
	 */
	public static <C> C toBean(String jsonStr, Class<C> cls) {
		try {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(String.class, new JsonObjectSerializer()).registerTypeAdapter(Date.class, new DateSerializer()).registerTypeAdapter(Boolean.class, new BooleanSerializer()).addDeserializationExclusionStrategy(new SuperclassExclusionStrategy()).addSerializationExclusionStrategy(new SuperclassExclusionStrategy());
			Gson gson = gsonBuilder.create();
			C o = (C) gson.fromJson(jsonStr, cls);
			return o;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * <pre>
	 * 重写了JSONObject自带的方法 解释某些参数同时为子对象的
	 * !!!!这里可以兼容了子对象中存在json格式而导致解释报错，
	 * 但是如果子对象又有子对象这种情况不支持了
	 * 实现思路是人为将其子表json拆分出来
	 * 然后利用反射机制实现子表数据的处理
	 * </pre>
	 * 
	 * @param jsonObject
	 * @param cls
	 * @param map
	 * @return C
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @exception
	 * @since 1.0.0
	 */
	public static <C> C toBean(JSONObject jsonObject, Class<C> cls, Map<String, Class<?>> map) {

		Map<String, JSONArray> jaMap = new HashMap<String, JSONArray>();
		for (String key : map.keySet()) {
			JSONArray ja = jsonObject.getJSONArray(key);
			jaMap.put(key, ja);
			jsonObject.remove(key);
		}

		C main = toBean(jsonObject, cls);// 生成主表对象
		try {
			for (String key : map.keySet()) {
				Class<?> c = map.get(key);
				JSONArray ja = jaMap.get(key);
				List subList = toBeanList(ja, c);
				Method method = cls.getMethod("set" + StringUtil.makeFirstLetterUpperCase(key), List.class);//获取
				method.invoke(main, subList);
			}
		} catch (Exception e) {
			return null;
		}
		return main;
	}

	/**
	 * 重写了JSONObject自带的方法 解释某些参数同时为子对象的 这里可以处理掉子对象中存在json格式而导致解释报错
	 * 
	 * @param jsonObject
	 * @param cls
	 * @param map
	 * @return C
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @exception
	 * @since 1.0.0
	 */
	public static <C> C toBean(String json, Class<C> cls, Map<String, Class<?>> map) {
		return toBean(JSONObject.fromObject(json), cls, map);
	}

	/**
	 * 根据jsonArrayStr 转成一个List
	 * 
	 * @param arrayStr
	 * @param cls
	 * @return List<C>
	 * @exception
	 * @since 1.0.0
	 */
	public static <C> List<C> toBeanList(String arrayStr, Class<C> cls) {
		return toBeanList(JSONArray.fromObject(arrayStr), cls);
	}

	/**
	 * 根据jsonArray 转成一个List
	 * 
	 * @param arrayStr
	 * @param cls
	 * @return List<C>
	 * @exception
	 * @since 1.0.0
	 */
	public static <C> List<C> toBeanList(JSONArray jsonArray, Class<C> cls) {
		List<C> list = new ArrayList<C>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			C o = toBean(jsonObject, cls);
			list.add(o);
		}
		return list;
	}

	/**
	 * 模仿requestUitl.java的同名方法 将参数放到map中。<br>
	 * 
	 * <pre>
	 * 1.如果是需要分页的情况，参数需要做如下配置：
	 * Q_参数名称_参数类型
	 * 
	 * 可以使用的类型如下：
	 * S：字符串
	 * L：长整型
	 * N：整形
	 * DB:double
	 * BD：decimal
	 * FT:浮点型数据
	 * SN：short数据
	 * DL：开始时间
	 * DG:结束时间
	 * SL:字符串 模糊查询
	 * SLL:字符串 左模糊查询
	 * SLR:字符串 右模糊查询
	 * 2.如果参数不需要分页的情况可以不用按照上面的方式传递参数。
	 * 
	 * </pre>
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> getQueryMap(JSONObject jsonObject) {
		Map map = new HashMap();

		for (Object obj : jsonObject.keySet()) {
			String key = obj.toString();
			String[] values = new String[1];
			values[0] = jsonObject.getString(key);

			if (key.equals("sortField") || key.equals("orderSeq") || key.equals("sortColumns")) {
				String val = values[0].trim();
				if (StringUtil.isNotEmpty(val)) {
					map.put(key, values[0].trim());
				}
			}
			if (values.length > 0 && StringUtils.isNotEmpty(values[0])) {
				if (key.startsWith("Q_")) {
					RequestUtil.addParameter(key, values, map);
				} else {
					if (values.length == 1) {
						String val = values[0].trim();
						if (StringUtil.isNotEmpty(val)) {
							map.put(key, values[0].trim());
						}
					} else {
						map.put(key, values);
					}
				}
			}
		}

		return map;
	}

	public static void main(String[] args) {
	}
}
