package com.suneee.weixin.util;

import com.suneee.core.util.HttpUtil;
import com.suneee.weixin.model.TokenModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suneee.core.util.HttpUtil;
import com.suneee.weixin.api.WeixinConsts;
import com.suneee.weixin.model.TokenModel;

import net.sf.json.JSONObject;


/**
 * 获取token工具类。
 * @author ray
 *
 */
public class TokenUtil {
	
	protected static Logger log = LoggerFactory.getLogger(TokenUtil.class);
	
	private static TokenModel model=new TokenModel();
	
	/**
	 * 获取企业token。
	 * <pre>
	 * 1.如果没有初始化则直接从数据库中获取。
	 * 2.如果已经初始化。
	 * 	1.如果已经过期则重新获取。
	 * 	2.从缓存中获取。
	 * </pre>
	 * @return
	 */
	public static synchronized String getWxToken() {
		//没有初始化直接获取。
		if(!model.isInit()){
			String token=getToken();
			return token;
		}
		else{
			//如果token要过期则重新获取。
			if(model.isExpire()){
				String token=getToken();
				return token;
			}
			else{
				//从缓存中获取。
				return model.getToken();
			}
		}
	}
	
	
	
	/**
	 * 获取token。
	 * @return
	 */
	private static String getToken() {
		String url=WeixinConsts.getWxToken();
		String rtn= HttpUtil.sendHttpsRequest(url, "", WeixinConsts.METHOD_GET);
		System.out.println(rtn);
		//取到了
		if(rtn.indexOf("errcode")==-1){
			JSONObject jsonObj=JSONObject.fromObject(rtn);
			String token=jsonObj.getString("access_token");
			int expireIn=jsonObj.getInt("expires_in");
			model.setToken(token, expireIn);
			return token;
		}
		//获取失败
		else{
			JSONObject jsonObj=JSONObject.fromObject(rtn);
			String errMsg=jsonObj.getString("errmsg");
			log.error(errMsg);
			return "-1";
		}
	}
	
	
}
