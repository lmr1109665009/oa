package com.suneee.ucp.base.util;

import java.util.Comparator;

/**
 * 比较字符串顺序
 * @ClassName: MapKeyComparator 
 * @Description: TODO
 * @author: 刘贤华
 * @date: 2016年12月27日 上午10:44:33
 */
public class MapKeyComparator implements Comparator<String>
{
    @Override
    public int compare(String str1, String str2) {
        
        return str1.compareTo(str2);
    }
}
