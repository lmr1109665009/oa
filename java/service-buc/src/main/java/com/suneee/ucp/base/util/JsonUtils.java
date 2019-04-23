/**
 * 
 */
package com.suneee.ucp.base.util;

import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

/**
 * json工具类：json字符串与java对象互转
 * @author Administrator
 *
 */
public class JsonUtils {
	/**
	 * java对象转为json字符串
	 * @param object
	 * @return
	 */
	public static String toJson(Object object){
		Gson gson = new Gson();
		return gson.toJson(object);
	}
	
	/**
	 * json字符串转为java对象
	 * @param jsonStr
	 * @param clazz 
	 * @return
	 */
	public static <T> T fromJson(String jsonStr, Class<T> clazz){
		Gson gson = new Gson();
		return gson.fromJson(jsonStr, clazz);
	}
	
	/**
	 * json字符串转换为java对象集合
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> jsonToList(String json, Class<T[]> clazz)
    {
        Gson gson = new Gson();
        T[] array = gson.fromJson(json, clazz);
        return Arrays.asList(array);
    }
}
