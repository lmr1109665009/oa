package com.suneee.weixin;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.HttpUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.system.SysPropertyConstants;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.weixin.api.WeixinConsts;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 实现微信OATH2.0协议，微信跳转到应用的页面。
 * 
 * <pre>
 * 
 * 1.判定用户是否登录，如果已经登录，那么直接跳转到指定的页面。
 * 
 * 2.没有登录的情况。
 * 	首先从上下文获取code。
 *  1.如果没有获取到则跳转到微信服务器获取会话code。
 * 	2.获取code后，微信服务器再次跳转回此代理页面。
 * 	3.代理页面根据code和token提交数据到微信服务器获取，当前用户帐号。
 * 	4.获取帐号后，系统让此账户自动登录。
 * 	5.在跳转回指定页面，页面验证成功。
 * 
 * 在微信菜单URL做如下配置：
 * 
 * http://平台域名/bpmx3/proxy?from=wx&redirect=需要跳转到的页面地址。
 * http://hotent.eicp.net/bpmx3/proxy?redirect=http://hotent.eicp.net/bpmx3/weixin/index.html
 * </pre>
 * 
 * @author ray
 *
 */
public class ProxyServlet  extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8382507359838493519L;
	
	protected Logger log = LoggerFactory.getLogger(ProxyServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String redirect=req.getParameter("redirect");
		SysUser sysUser=(SysUser) ContextUtil.getCurrentUser();
		
		//没有登录
		if(sysUser==null){
			//
			String code=req.getParameter("code");
			if(StringUtil.isEmpty(code)){
				String rdUrl= PropertyUtil.getByAlias(SysPropertyConstants.PLATFORM_URL) +"/proxy?redirect="+redirect;
				String redirectUrl=WeixinConsts.getWxAuthorize( rdUrl);
				resp.sendRedirect(redirectUrl);
			}
			else if(StringUtil.isNotEmpty(code)){
				String userUrl=WeixinConsts.getWxUserInfo( code);
				try{
					String json= HttpUtil.sendHttpsRequest(userUrl, "", WeixinConsts.METHOD_GET);
					JSONObject jsonObj=JSONObject.fromObject(json);
					//{"UserId":"USERID", "DeviceId":"DEVICEID"}
					String userId=jsonObj.getString("UserId");
					//让系统登录
					//SecurityUtil.login(req, userId, "", true);
					//第一次使用流程平台改变为已使用微信登陆
					SysUser user = (SysUser) ContextUtil.getCurrentUser();
					if(user.getHasSyncToWx() ==1){
						user.setHasSyncToWx(2);
						AppUtil.getBean(SysUserService.class).update(user);
					}
					resp.sendRedirect(redirect);
				}
				catch(Exception ex){
					log.error(ex.getMessage());
				}
			}
		}
		else{
			resp.sendRedirect(redirect);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}

}
