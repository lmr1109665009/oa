package com.suneee.eas.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;

public class JsonUtil {
    private static ObjectMapper mapper = null;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static{
        //创建json对象
        mapper = new ObjectMapper();
        //json处理时间格式
        mapper.setDateFormat(dateFormat);
    }

    /**
     * 获取objectMapper对象
     * @return
     */
    public static ObjectMapper getObjectMapper() {
        return mapper;
    }

    /**
     * 将对象转换为json字符串
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        String json = null;
        try {
            json = mapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 将json字符串转换为对象
     * @param json
     * @param ref
     * @param <T>
     * @return
     */
    public static <T> T toObject(String json,TypeReference<T> ref) {
        T t = null;
        try {
            t = mapper.readValue(json, ref);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }
    public static <T> T toObject(String json,Class<T> cls) {
        T t=null;
        try {
            t = mapper.readValue(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }
}