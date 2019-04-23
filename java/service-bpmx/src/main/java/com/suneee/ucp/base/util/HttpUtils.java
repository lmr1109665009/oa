/**
 * 
 */
package com.suneee.ucp.base.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suneee.core.util.AppConfigUtil;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.extentity.Result;

import net.sf.json.JSONObject;

/**
 * @author Administrator
 *
 */
public class HttpUtils{
	
	private static final String UC_API_URL = AppConfigUtil.get(Constants.UC_API_URL);
	private static final Logger LOGGER = LoggerFactory.getLogger("httplog");
	private static final String MESSAGE_API_URL = AppConfigUtil.get(Constants.MESSAGE_API_URL);
	private static final String AC_API_URL = AppConfigUtil.get(Constants.AUTHORITY_CENTER_API_URL);
	private static final int CONNECTTIMEOUT = AppConfigUtil.getInt(Constants.HTTP_CONNECTION_TIMEOUT, 5000);
	private static final int READTIMEOUT = AppConfigUtil.getInt(Constants.HTTP_READ_TIMEOUT, 5000);
	/**
	 * 发送请求到用户中心
	 * @param apiMethod 请求方法
	 * @param object 请求参数
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
				sign = AesUtils.Encrypt(JsonUtils.toJson(sortParams), AppConfigUtil.get(Constants.UC_ENCRYPTCODE));
			} catch (Exception e) {
				LOGGER.error("生成签名校验字符串失败", e);
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
			response = sendData(url, JsonUtils.toJson(params), headers, Constants.CHARSET_UTF8);
		} catch (Exception e) {
			LOGGER.info("接口请求异常，再次发送请求。异常信息：" + e.getMessage());
			response = sendData(url, JsonUtils.toJson(params), headers, Constants.CHARSET_UTF8);
		}
		return JsonUtils.fromJson(response, Result.class);
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
		LOGGER.info("Request URL：" + url + " Request Parameters:" + jsonData);
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
			LOGGER.info("Response：" + response);
			return response;
		} catch (MalformedURLException e) {
			LOGGER.error("Request Failed:" + e.getMessage(), e);
			throw new RuntimeException("Request Failed:"+e.getMessage(), e);
		} catch (ProtocolException e) {
			LOGGER.error("Request Failed:" + e.getMessage(), e);
			throw new RuntimeException("Request Failed:"+e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Request Failed:" + e.getMessage(), e);
			throw new RuntimeException("Request Failed:"+e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error("Request Failed:" + e.getMessage(), e);
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
