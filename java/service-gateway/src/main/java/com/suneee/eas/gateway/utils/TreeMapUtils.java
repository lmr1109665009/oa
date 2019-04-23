package com.suneee.eas.gateway.utils;

import java.util.Map;
import java.util.TreeMap;

/**
 * TreeMap 根据字母顺序排序
 * 
 * @ClassName: TreeMapUtils
 * @Description: TODO
 * @author: 刘贤华
 * @date: 2016年12月27日 上午10:45:06
 */
public class TreeMapUtils
{
    /**
     * TreeMap 根据字母顺序排序
     * 
     * @Title: sortMapByKey
     * @Description: TODO
     * @param map
     * @return: Map<String,Object>
     */
    public static Map<String, Object> sortMapByKey(Map<String, Object> map)
    {
        if (map == null || map.isEmpty())
        {
            return null;
        }

        Map<String, Object> sortMap = new TreeMap<String, Object>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }

}
