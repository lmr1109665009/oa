/**
 * @Title: LogoutController.java
 * @Package com.suneee.oa.controller.system
 * @Description: TODO(用一句话描述该文件做什么)
 */
package com.suneee.oa.controller.system;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;
/**
 * @ClassName: LogoutController
 * @Description: 用于处理退出系统逻辑
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-01-29 11:32:00
 *
 */
@Controller
public class LogoutController extends UcpBaseController{

	@Autowired
	private StringRedisTemplate redisTemplate;

	@RequestMapping("/logout")
	@ResponseBody
	public ResultVo logout(HttpServletRequest request, HttpServletResponse response){

		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "退出成功");
	}


}