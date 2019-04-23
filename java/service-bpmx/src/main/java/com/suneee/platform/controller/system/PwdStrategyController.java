package com.suneee.platform.controller.system;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.PwdStrategy;
import com.suneee.platform.service.system.PwdStrategyService;

/**
 * <pre>
 * 对象功能:密码策略 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-06-25 14:30:17
 * </pre>
 */
@Controller
@RequestMapping("/platform/system/pwdStrategy/")
public class PwdStrategyController extends BaseController {
	@Resource
	private PwdStrategyService pwdStrategyService;

	/**
	 * 添加或更新密码策略。
	 * 
	 * @param request
	 * @param response
	 * @param pwdStrategy
	 *            添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "添加或更新密码策略")
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String json = FileUtil.inputStream2String(request.getInputStream());
		PwdStrategy pwdStrategy = JSONObjectUtil.toBean(json, PwdStrategy.class);
		String resultMsg = null;
		try {
			if (pwdStrategy.getId() == null || pwdStrategy.getId() == 0) {
				resultMsg = getText("添加", "密码策略");
			} else {
				resultMsg = getText("更新", "密码策略");
			}
			pwdStrategyService.save(pwdStrategy);
			writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
		} catch (Exception e) {
			writeResultMessage(response.getWriter(), resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}
	}

	/**
	 * 取得密码策略分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看密码策略分页列表")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<PwdStrategy> list = pwdStrategyService.getAll(new QueryFilter(request, "pwdStrategyItem"));
		ModelAndView mv = this.getAutoView().addObject("pwdStrategyList", list);
		return mv;
	}

	/**
	 * 删除密码策略
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除密码策略")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			pwdStrategyService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除密码策略成功!");
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	@RequestMapping("ngjs")
	@Action(description = "ngjs的请求，分为参数和action，action是说明这次请求的目的")
	@ResponseBody
	public Object ngjs(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String action = RequestUtil.getString(request, "action");
		if (action.equals("getById")) {
			Long id = RequestUtil.getLong(request, "id", null);
			return pwdStrategyService.getById(id);
		}
		if (action.equals("setEnable")) {
			Short enable = RequestUtil.getShort(request, "enable");
			String[] ids = RequestUtil.getStringAryByStr(request, "ids");
			JSONObject result = new JSONObject();
			if (enable == null || ids == null || ids.length == 0) {
				result.put("status", "0");
				result.put("msg", "ids,enabled参数错误");
			} else {
				try {
					pwdStrategyService.updateEnable(ids, enable);
					result.put("status", "1");
					result.put("msg", "操作成功");
				} catch (Exception e) {
					result.put("status", "0");
					result.put("msg", "更新错误:" + ExceptionUtil.getExceptionMessage(e));
				}
			}
			return result;
		}
		return null;
	}
}
