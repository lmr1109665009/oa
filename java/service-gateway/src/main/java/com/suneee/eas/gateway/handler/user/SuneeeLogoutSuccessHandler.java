package com.suneee.eas.gateway.handler.user;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.HttpUtil;
import com.suneee.eas.gateway.utils.CookieUitl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 退出成功handler
 * @user 子华
 * @created 2018/9/12
 */
public class SuneeeLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler{

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException{
        Cookie[] cookies=request.getCookies();
        for (Cookie cookieItem:cookies){
            if (cookieItem.getName().endsWith("ue_")){
                cookieItem.setMaxAge(-1);
                continue;
            }
            if (cookieItem.getName().equals("isLogin")){
                cookieItem.setMaxAge(-1);
            }
        }
        HttpUtil.writeResponseJson(response, ResponseMessage.success("退出成功"));
    }
}
