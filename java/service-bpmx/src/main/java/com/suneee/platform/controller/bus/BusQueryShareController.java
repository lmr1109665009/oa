package com.suneee.platform.controller.bus;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bus.BusQueryShare;
import com.suneee.platform.service.bus.BusQueryShareService;

/**
 * <pre>
 * 对象功能:查询过滤共享 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2013-12-20 10:10:44
 * </pre>
 */
@Controller
@RequestMapping("/platform/bus/busQueryShare/")
public class BusQueryShareController extends BaseController {
	@Resource
	private BusQueryShareService busQueryShareService;

	/**
	 * 添加或更新查询过滤共享。
	 * 
	 * @param request
	 * @param response
	 * @param busQueryShare
	 *            添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "添加或更新查询过滤共享")
	public void save(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String resultMsg = null;
		BusQueryShare busQueryShare = getFormObject(request);
		try {
			if (busQueryShare.getId() == null || busQueryShare.getId() == 0) {
				busQueryShare.setId(UniqueIdUtil.genId());
				busQueryShare.setSharerId(ContextUtil.getCurrentUserId());
				busQueryShareService.save(busQueryShare,true);
			} else {
				busQueryShareService.save(busQueryShare, false);
			}
			resultMsg = getText("保存成功");
			writeResultMessage(response.getWriter(), resultMsg,
					ResultMessage.Success);
		} catch (Exception e) {
			writeResultMessage(response.getWriter(),
					resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}
	}

	/**
	 * 取得 BpmDataTemplate 实体
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	protected BusQueryShare getFormObject(HttpServletRequest request)
			throws Exception {

		JSONUtils.getMorpherRegistry().registerMorpher(
				new DateMorpher((new String[] { "yyyy-MM-dd HH:mm:ss" })));

		String json = RequestUtil.getString(request, "json", false);
		if (StringUtil.isEmpty(json))
			return null;
		JSONObject obj = JSONObject.fromObject(json);
		String shareRight = obj.getString("shareRight");
		obj.remove("shareRight");

		BusQueryShare busQueryShare = (BusQueryShare) JSONObject.toBean(obj,
				BusQueryShare.class);
		busQueryShare.setShareRight(shareRight);

		return busQueryShare;
	}

	/**
	 * 取得查询过滤共享分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看查询过滤共享分页列表")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<BusQueryShare> list = busQueryShareService.getAll(new QueryFilter(
				request, "busQueryShareItem"));
		ModelAndView mv = this.getAutoView().addObject("busQueryShareList",
				list);

		return mv;
	}

	/**
	 * 删除查询过滤共享
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除查询过滤共享")
	public void del(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			busQueryShareService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除查询过滤共享成功!");
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败"
					+ ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	/**
	 * 编辑查询过滤共享
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description = "编辑查询过滤共享")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		Long filterId = RequestUtil.getLong(request, "filterId", 0L);
		BusQueryShare busQueryShare = busQueryShareService
				.getByFilterId(filterId);
		return getAutoView().addObject("busQueryShare", busQueryShare)
				.addObject("filterId", filterId);
	}

	/**
	 * 取得查询过滤共享明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description = "查看查询过滤共享明细")
	public ModelAndView get(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		BusQueryShare busQueryShare = busQueryShareService.getById(id);
		return getAutoView().addObject("busQueryShare", busQueryShare);
	}

}
