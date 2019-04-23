package com.suneee.weixin.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.suneee.core.api.util.PropertyUtil;
import com.suneee.platform.model.system.SysPropertyConstants;
import com.suneee.weixin.util.TokenUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.weixin.util.TokenUtil;

/**
 * 微信使用到的常量。
 * @author ray
 *
 */
public class WeixinConsts {
	
	public final static String METHOD_GET="GET";
	
	public final static String METHOD_POST="POST";
	
	private final static String QIYE_URL="https://qyapi.weixin.qq.com/cgi-bin";

	/**
	 * 获取微信地址。
	 * @param corpId
	 * @param secret
	 * @return
	 */
	public static String getWxToken(){
		String corpId= PropertyUtil.getByAlias("wx.corpId");
		String secret=PropertyUtil.getByAlias("wx.secret");
		return QIYE_URL + "/gettoken?corpid=" + corpId + "&corpsecret=" + secret;
	}
	
	
	/**
	 * 获取微信验证地址。
	 * @param appId
	 * @param redirectUrl
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String getWxAuthorize(String redirectUrl) throws UnsupportedEncodingException{
		String corpId=PropertyUtil.getByAlias("wx.corpId");
		String redirect=URLEncoder.encode(redirectUrl, "utf-8");
		String url="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+corpId+"&redirect_uri="+redirect+"&response_type=code&scope=snsapi_base&state=hotent#wechat_redirect";
		return url;
	}
	
	//
	/**
	 * 根据code获取用户信息。
	 * @param accessToken
	 * @param code
	 * @return
	 */
	public static String getWxUserInfo(String code){
		String accessToken= TokenUtil.getWxToken();
		String url=QIYE_URL + "/user/getuserinfo?access_token="+accessToken+"&code=" + code;
		return url;
	}
	public static String getCreateUserUrl(){
		String url =QIYE_URL +"/user/create?access_token="+TokenUtil.getWxToken();
		return url;
	}
	public static String getSendMsgUrl(){
		String url =QIYE_URL +"/message/send?access_token="+TokenUtil.getWxToken();
		return url;
	}
	public static String getDeleteUserUrl(){
		String url =QIYE_URL +"/user/delete?access_token="+TokenUtil.getWxToken()+"&userid=";
		return url;
	}
	public static String getDeleteAllUserUrl(){
		String url =QIYE_URL +"/user/batchdelete?access_token="+TokenUtil.getWxToken();
		return url;
	}
	public static String getUpdateUserUrl(){
		String url =QIYE_URL +"/user/update?access_token="+TokenUtil.getWxToken();
		return url;
	}
	public static String getCreateOrgUrl(){
		String url =QIYE_URL +"/department/create?access_token="+TokenUtil.getWxToken();
		return url;
	}
	public static String getUpdateOrgUrl(){
		String url =QIYE_URL +"/department/update?access_token="+TokenUtil.getWxToken();
		return url;
	}
	public static String getDeleteOrgUrl(){
		String url =QIYE_URL +"/department/delete?access_token="+TokenUtil.getWxToken()+"&id=";
		return url;
	}
	public static String getInviteUserUrl() {
		String url =QIYE_URL +"/invite/send?access_token="+TokenUtil.getWxToken();
		return url;
	}
	
	
	


}
