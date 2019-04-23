package com.suneee.platform.controller.mobile;

import com.suneee.core.util.LocaleUtil;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 切换语言
 * @author zxh
 *
 */
@Controller
@RequestMapping("/platform/mobile/lang/")
public class MobileLangController extends MobileBaseController {
	@Resource
	private SessionLocaleResolver sessionLocaleResolver;
	/**
	 * 切换语言
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("changLang")
	@Action(description = "切换语言")
	@ResponseBody
	public Object changLang(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String lang = RequestUtil.getString(request, "lang", "zh_CN");
		Locale curLocale = LocaleUtil.getLocale(lang);
		//设置多语言支持
		sessionLocaleResolver.setLocale(request, response, curLocale);

		return map;

	}
}
