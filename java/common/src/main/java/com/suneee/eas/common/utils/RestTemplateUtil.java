package com.suneee.eas.common.utils;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * RestTemplate工具类
 * @user 子华
 * @created 2018/8/16
 */
public class RestTemplateUtil {
    private static RestTemplate template;

    public static RestTemplate getTemplate() {
        return template;
    }

    public static void setTemplate(RestTemplate template) {
        RestTemplateUtil.template = template;
    }

    /**
     * post请求
     * @param url
     * @param responseType
     * @param params 请求参数
     * @param <T>
     * @return
     */
    public static  <T> T post(String url, Class<T> responseType,MultiValueMap<String, Object> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
        return template.postForObject(url,request,responseType);
    }
    public static  ResponseEntity<?> post(String url, ParameterizedTypeReference typeReference,MultiValueMap<String, Object> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
        return template.exchange(url,HttpMethod.POST,request,typeReference);
    }

    /**
     * post请求
     * @param url
     * @param urlParams url地址参数：http://www.suneee.com/api/getUser?userId=${userId},则urlParams需要提供一个参数userId
     * @param responseType
     * @param params 请求参数
     * @param <T>
     * @return
     */
    public static  <T> T post(String url,Map<String,Object> urlParams, Class<T> responseType,MultiValueMap<String, Object> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
        return template.postForObject(url,request,responseType,urlParams);
    }

    public static  ResponseEntity<?> post(String url,Map<String,Object> urlParams,ParameterizedTypeReference typeReference,MultiValueMap<String, Object> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
        return template.exchange(url,HttpMethod.POST,request,typeReference,urlParams);
    }

    public static  <T> T post(String url, Class<T> responseType,String json){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        return template.postForObject(url,request,responseType);
    }
    public static  ResponseEntity<?> post(String url, ParameterizedTypeReference typeReference, String json){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        return template.exchange(url, HttpMethod.POST, request, typeReference);
    }

    /**
     * post 请求
     * @param url
     * @param urlParams urlParams url地址参数：http://www.suneee.com/api/getUser?userId=${userId},则urlParams需要提供一个参数userId
     * @param responseType
     * @param params 请求参数
     * @param <T>
     * @return
     * @throws UnsupportedEncodingException
     */
    public static <T> T get(String url,Map<String,Object> urlParams, Class<T> responseType,MultiValueMap<String, Object> params) throws UnsupportedEncodingException {
        if (url.contains("?")){
            url+=buildQueryParams(params);
        }else {
            url+="?"+buildQueryParams(params);
        }
        return template.getForObject(url,responseType,urlParams);
    }
    public static <T> T get(String url, Class<T> responseType,MultiValueMap<String, Object> params) throws UnsupportedEncodingException {
        if (url.contains("?")){
            url+=buildQueryParams(params);
        }else {
            url+="?"+buildQueryParams(params);
        }
        return template.getForObject(url,responseType);
    }
    public static <T> T get(String url, Class<T> responseType) throws UnsupportedEncodingException {
        return get(url,responseType,null);
    }

    public static ResponseEntity<?> get(String url,Map<String,Object> urlParams, ParameterizedTypeReference typeReference,MultiValueMap<String, Object> params) throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(null, headers);
        if (url.contains("?")){
            url+=buildQueryParams(params);
        }else {
            url+="?"+buildQueryParams(params);
        }
        return template.exchange(url,HttpMethod.GET,request,typeReference,urlParams);
    }
    public static ResponseEntity<?> get(String url, ParameterizedTypeReference typeReference,MultiValueMap<String, Object> params) throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(null, headers);
        if (url.contains("?")){
            url+=buildQueryParams(params);
        }else {
            url+="?"+buildQueryParams(params);
        }
        return template.exchange(url,HttpMethod.GET,request,typeReference);
    }
    public static ResponseEntity<?> get(String url, ParameterizedTypeReference typeReference) throws UnsupportedEncodingException {
        return get(url,typeReference,null);
    }

    /**
     * 创建参数拼接
     * @param params
     * @return
     */
    private static String buildQueryParams(MultiValueMap<String, Object> params) throws UnsupportedEncodingException {
        if (params==null||params.size()==0){
            return "";
        }
        StringBuilder builder=new StringBuilder();
        for (String key:params.keySet()){
            List<Object> objList=params.get(key);
            for (Object obj:objList){
                if (obj instanceof String){
                    builder.append("&"+key+"="+URLEncoder.encode((String) obj,"utf-8"));
                }else {
                    builder.append("&"+key+"="+obj);
                }
            }
        }
        if (builder.length()>0){
            builder.replace(0,1,"");
        }
        return builder.toString();
    }
}
