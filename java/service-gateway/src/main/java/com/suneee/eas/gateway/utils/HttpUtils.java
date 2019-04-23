/**
 * 
 */
package com.suneee.eas.gateway.utils;

import com.suneee.eas.common.utils.JsonUtil;
import com.suneee.eas.common.utils.StringUtil;
import com.suneee.eas.gateway.component.Result;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 *
 */
@Component
public class HttpUtils{
	private static final Logger log = LogManager.getLogger("httplog");
	private static Environment env;

	@Autowired
	public void setEnv(Environment env) {
		HttpUtils.env = env;
		UC_API_URL=env.getProperty(Constants.UC_API_URL);
		MESSAGE_API_URL=env.getProperty(Constants.MESSAGE_API_URL);
		AC_API_URL=env.getProperty(Constants.AUTHORITY_CENTER_API_URL);
		String temp=env.getProperty(Constants.HTTP_CONNECTION_TIMEOUT);
		if (StringUtil.isEmpty(temp)){
			CONNECTTIMEOUT=5000;
		}else {
			CONNECTTIMEOUT=Integer.parseInt(temp);
		}
		temp=env.getProperty(Constants.HTTP_READ_TIMEOUT);
		if (StringUtil.isEmpty(temp)){
			READTIMEOUT=5000;
		}else {
			READTIMEOUT=Integer.parseInt(temp);
		}
		UC_API_URL=env.getProperty(Constants.UC_API_URL);
	}

	private static String UC_API_URL;
	private static String MESSAGE_API_URL;
	private static String AC_API_URL;
	private static int CONNECTTIMEOUT;
	private static int READTIMEOUT;
	/**
	 * 发送请求到用户中心
	 * @param apiMethod 请求方法
	 * @param params 请求参数
	 * @param needSign 是否需要签名校验
	 * @return
	 * @throws Exception 
	 */
	public static Result sendToUserCenter(String apiMethod, Map<String, Object> params, boolean needSign) throws IOException{
		if(needSign){
			// 将请求参数按照字母顺序排序
			Map<String, Object> sortParams = TreeMapUtils.sortMapByKey(params);
			// 获取签名校验字符串
			String sign = null;
			try {
				sign = AesUtils.Encrypt(JsonUtil.toJson(sortParams), env.getProperty(Constants.UC_ENCRYPTCODE));
			} catch (Exception e) {
				log.error("生成签名校验字符串失败", e);
			}
			params.put("sign", sign);
		}
		String url = UC_API_URL;
		if(UC_API_URL.endsWith(Constants.SEPARATOR_BACK_SLANT)){
			url = url + apiMethod;
		} else {
			url = url + Constants.SEPARATOR_BACK_SLANT + apiMethod;
		}
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=" + Constants.CHARSET_UTF8);
		String response;
		try {
			response = sendData(url, JsonUtil.toJson(params), headers, Constants.CHARSET_UTF8);
		} catch (Exception e) {
			log.info("接口请求异常，再次发送请求。异常信息：" + e.getMessage());
			response = sendData(url, JsonUtil.toJson(params), headers, Constants.CHARSET_UTF8);
		}
		return JsonUtil.toObject(response, Result.class);
	}
	
	/**
	 * 发送消息到消息中心
	 * @param apiMethod 接口名称
	 * @param jsonData 请求参数
	 * @return
	 * @throws IOException
	 */
	public static JSONObject sendToMessageQuene(String apiMethod, String jsonData) throws IOException{
		String url = MESSAGE_API_URL;
		return HttpUtils.sendToMessageQuene(url, apiMethod, jsonData);
	}
	
	/** 
	 * 发送消息到消息中心
	 * @param url 接口地址
	 * @param apiMethod 接口方法
	 * @param jsonData 接口参数
	 * @return
	 * @throws IOException
	 */
	public static JSONObject sendToMessageQuene(String url, String apiMethod, String jsonData) throws IOException{
		if(url.endsWith(Constants.SEPARATOR_BACK_SLANT)){
			url = url + apiMethod;
		} else {
			url = url + Constants.SEPARATOR_BACK_SLANT + apiMethod;
		}
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=" + Constants.CHARSET_UTF8);
		
		String response = sendData(url, jsonData, headers, Constants.CHARSET_UTF8);
		return JSONObject.fromObject(response);
	}
	
	/**
	 * 发送请求到权限中心
	 * @param apiMethod
	 * @param jsonData
	 * @return
	 * @throws IOException
	 */
	public static JSONObject sendToAuthorityCenter(String apiMethod, String jsonData) throws IOException{
		String url = AC_API_URL;
		if(AC_API_URL.endsWith(Constants.SEPARATOR_BACK_SLANT)){
			url = url  + apiMethod;
		} else {
			url = url + Constants.SEPARATOR_BACK_SLANT + apiMethod;
		}
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=" + Constants.CHARSET_UTF8);
		String response = sendData(url, jsonData, headers, Constants.CHARSET_UTF8);
		return JSONObject.fromObject(response);
	}
	
	/**
	 * 发送请求
	 * @param url
	 * @param jsonData
	 * @param headers
	 * @return
	 * @throws IOException 
	 */
	public static String sendData(String url, String jsonData, Map<String, String> headers, String charset) throws IOException{
		log.info("Request URL：" + url + " Request Parameters:" + jsonData);
		HttpURLConnection httpConnection = null;
		BufferedReader responseBuffer = null;
		String  response = null;
		try {
			URL targetUrl = new URL(url);

			httpConnection = (HttpURLConnection) targetUrl.openConnection();
			// 设置是否向httpUrlConnection输出,默认情况下是false
			httpConnection.setDoOutput(true);
			// 设置请求方式
			httpConnection.setRequestMethod("POST");
			// 设置连接超时时间
			httpConnection.setConnectTimeout(CONNECTTIMEOUT);
			// 设置读取超时时间
			httpConnection.setReadTimeout(READTIMEOUT);
			// 设置是否使用缓存
			httpConnection.setUseCaches(false);
			// 设置请求头信息
			if(headers != null){
				for(Map.Entry<String, String> header : headers.entrySet()){
					httpConnection.setRequestProperty(header.getKey(), header.getValue());
				}
			}
			OutputStream outputStream = httpConnection.getOutputStream();
			outputStream.write(jsonData.getBytes(charset));
			outputStream.flush();
			outputStream.close();
			
			if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Request Failed : HTTP error code : " + httpConnection.getResponseCode());
			}

			responseBuffer = new BufferedReader(
					new InputStreamReader(httpConnection.getInputStream(), charset));

			StringBuilder result = new StringBuilder();
			String output;
			while ((output = responseBuffer.readLine()) != null) {
				result.append(output);
			}
			responseBuffer.close();
			httpConnection.disconnect();
			response = result.toString();
			log.info("Response：" + response);
			return response;
		} catch (MalformedURLException e) {
			log.error("Request Failed:" + e.getMessage(), e);
			throw new RuntimeException("Request Failed:"+e.getMessage(), e);
		} catch (ProtocolException e) {
			log.error("Request Failed:" + e.getMessage(), e);
			throw new RuntimeException("Request Failed:"+e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			log.error("Request Failed:" + e.getMessage(), e);
			throw new RuntimeException("Request Failed:"+e.getMessage(), e);
		} catch (IOException e) {
			log.error("Request Failed:" + e.getMessage(), e);
			throw new RuntimeException("Request Failed:"+e.getMessage(), e);
		}finally{
			if(responseBuffer != null){
				responseBuffer.close();
			}
			if(httpConnection != null){
				httpConnection.disconnect();
			}
		}
	}
}
