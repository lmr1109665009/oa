package com.suneee.eas.push.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

/**
 * 内部模拟http请求工具类
 * @author liuhai
 * @Date 2018/06/21
 */
public class HttpClientUtil {

    public static String sendPostRequest(String url, String data){
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        String responseStr = null;
        try{
            httpclient = HttpClients.createDefault();
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).build();//设置请求和传输超时时间
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(data, ContentType.create("application/json", "UTF-8"));
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Content-Type", "application/json");
            response = httpclient.execute(httpPost);
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                responseStr = EntityUtils.toString(entity, "UTF-8");
                responseStr.replaceAll("\r", "");//去掉返回结果中的"\r"字符
            }
            response.close();
            httpclient.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return responseStr;
    }

    public static String sendPostRequest(String url, String data, String contentType){
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        String responseStr = null;
        if(!StringUtils.hasText(contentType)){
            contentType = "application/json";
        }
        try{
            httpclient = HttpClients.createDefault();
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).build();//设置请求和传输超时时间
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(data, ContentType.create(contentType, "UTF-8"));
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Content-Type", contentType);
            response = httpclient.execute(httpPost);
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                responseStr = EntityUtils.toString(entity, "UTF-8");
                responseStr.replaceAll("\r", "");//去掉返回结果中的"\r"字符
            }
            response.close();
            httpclient.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return responseStr;
    }
}
