package com.suneee.ucp.base.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    /**
     * 去除特殊字符
     * @param content
     * @return
     */
    public static String specialCharFilter(String content){
        String regEx="[`~!@#$%^&*()+=|{}':;',//[//]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(content);
        return  m.replaceAll("").trim();
    }

    /**
     * 计算任务高光图key
     * @param highLightMap
     * @return
     */
    public static String computeHighLightKey(Map<String,String> highLightMap){
        if (highLightMap==null){
            return "";
        }
        StringBuilder builder=new StringBuilder();
        Set<String> keySet=highLightMap.keySet();
        for (String key:keySet){
            builder.append(key+":"+highLightMap.get(key)+"|");
        }
        return DigestUtils.md5Hex(builder.toString());
    }

    /**
     * 更改查询字段(如：某某某->%某%某%某%)
     * @param param
     * @return
     */
    public static String changceFilter(String param){
        String paramNew = "";
        String[] arr = new String[param.length()];
        for(int i = 0; i < param.length(); i++) {
            arr[i] = param.substring(i, i + 1);
            if (i < param.length()) {
                paramNew += "%" + arr[i];
            }
        }
        return paramNew+"%";
    }
}
