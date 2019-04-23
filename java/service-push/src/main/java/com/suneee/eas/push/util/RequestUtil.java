package com.suneee.eas.push.util;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;

/**
 * @program: ems
 * @description: 解析请求工具类
 * @author: liuhai
 * @create: 2018-06-25 15:44
 **/
public class RequestUtil {

    public static String getMessage(HttpServletRequest request) {
        String message = "";
        try {
            String encoding = request.getCharacterEncoding();
            if (encoding == null) {
                encoding = "UTF-8";
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), encoding));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(URLDecoder.decode(line, "utf-8"));
            }
            message = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }
}
