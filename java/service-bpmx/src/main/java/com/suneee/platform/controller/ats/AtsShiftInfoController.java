package com.suneee.platform.controller.ats;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.ats.AtsCardRule;
import com.suneee.platform.model.ats.AtsConstant;
import com.suneee.platform.model.ats.AtsShiftInfo;
import com.suneee.platform.model.ats.AtsShiftType;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.ats.AtsCardRuleService;
import com.suneee.platform.service.ats.AtsLegalHolidayDetailService;
import com.suneee.platform.service.ats.AtsShiftInfoService;
import com.suneee.platform.service.ats.AtsShiftTimeService;
import com.suneee.platform.service.ats.AtsShiftTypeService;
import com.suneee.platform.service.system.SysOrgService;

/**
 * <pre>
 * 对象功能:班次设置 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-18 17:21:46
 * </pre>
 */
@Controller
@RequestMapping("/platform/ats/atsShiftInfo/")
public class AtsShiftInfoController extends BaseController {
	@Resource
	private AtsShiftInfoService atsShiftInfoService;
	@Resource
	private AtsShiftTimeService atsShiftTimeService;
	@Resource
	private AtsShiftTypeService atsShiftTypeService;
	@Resource
	private AtsCardRuleService atsCardRuleService;
	@Resource
	private SysOrgService sysOrgService;
	@Resource
	private AtsLegalHolidayDetailService atsLegalHolidayDetailService;
	/**
	 * 添加或更新班次设置。
	 * 
	 * @param request
	 * @param response
	 * @param atsShiftInfo
	 *            添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "添加或更新班次设置")
	public void save(HttpServletRequest request, HttpServletResponse response,
			AtsShiftInfo atsShiftInfo) throws Exception {
		String resultMsg = null;
		try {
			
			Integer rtn = atsShiftInfoService.isAliasExists(atsShiftInfo.getName(),atsShiftInfo.getId());
			if ((rtn > 0 && BeanUtils.isEmpty(atsShiftInfo.getId())) ||  (rtn >1 &&  BeanUtils.isNotEmpty(atsShiftInfo.getId())) ) {
				writeResultMessage(response.getWriter(), "该班次名称,已经存在请检查！", ResultMessage.Fail);
				return;
			}
			if (atsShiftInfo.getId() == null || atsShiftInfo.getId() == 0) {
		
				resultMsg = getText("添加", "班次设置");
			} else {
				resultMsg = getText("更新", "班次设置");
			}
			atsShiftInfoService.save(atsShiftInfo);
			writeResultMessage(response.getWriter(), resultMsg,
					ResultMessage.Success);
		} catch (Exception e) {
			writeResultMessage(response.getWriter(),
					resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}
	}

	/**
	 * 取得班次设置分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看班次设置分页列表")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<AtsShiftInfo> list = atsShiftInfoService.getAll(new QueryFilter(
				request, "atsShiftInfoItem"));
		for (AtsShiftInfo atsShiftInfo : list) {
			setAtsShiftInfo(atsShiftInfo);
		}
		return this.getAutoView().addObject("atsShiftInfoList", list);
	}

	/**
	 * 删除班次设置
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除班次设置")
	public void del(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			atsShiftInfoService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除班次设置成功!");
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败"
					+ ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	/**
	 * 编辑班次设置
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description = "编辑班次设置")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		Long id = RequestUtil.getLong(request, "id", 0L);
		String returnUrl = RequestUtil.getPrePage(request);
		AtsShiftInfo atsShiftInfo = atsShiftInfoService.getById(id);
		if (BeanUtils.isNotEmpty(atsShiftInfo))
			atsShiftInfo.setDetailList(atsShiftInfoService.getDetailList(id));
		setAtsShiftInfo(atsShiftInfo);
		List<AtsShiftType> shiftTypeList = atsShiftTypeService
				.getListByStatus(AtsConstant.YES);
		return getAutoView().addObject("atsShiftInfo", atsShiftInfo)
				.addObject("shiftTypeList", shiftTypeList)
				.addObject("returnUrl", returnUrl);
	}

	private void setAtsShiftInfo(AtsShiftInfo atsShiftInfo) {
		if (BeanUtils.isEmpty(atsShiftInfo))
			return;
		if (BeanUtils.isNotEmpty(atsShiftInfo.getShiftType())) {
			AtsShiftType atsShiftType = atsShiftTypeService
					.getById(atsShiftInfo.getShiftType());
			if (BeanUtils.isNotEmpty(atsShiftType))
				atsShiftInfo.setShiftTypeName(atsShiftType.getName());
		}
		if (BeanUtils.isNotEmpty(atsShiftInfo.getCardRule())) {
			AtsCardRule atsCardRule = atsCardRuleService.getById(atsShiftInfo
					.getCardRule());
			if (BeanUtils.isNotEmpty(atsCardRule))
				atsShiftInfo.setCardRuleName(atsCardRule.getName());
		}

		if (BeanUtils.isNotIncZeroEmpty(atsShiftInfo.getOrgId())) {
			SysOrg sysOrg = sysOrgService.getById(atsShiftInfo.getOrgId());
			if (BeanUtils.isNotEmpty(sysOrg))
				atsShiftInfo.setOrgName(sysOrg.getOrgName());
		}
	}

	/**
	 * 取得班次设置明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description = "查看班次设置明细")
	public ModelAndView get(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		AtsShiftInfo atsShiftInfo = atsShiftInfoService.getById(id);
		return getAutoView().addObject("atsShiftInfo", atsShiftInfo);
	}
	
	/**
	 *取得班次设置分页列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("replace")
	public ModelAndView replace(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String isSingle = RequestUtil.getString(request, "isSingle", "false");
		Long attencePolicy = RequestUtil.getLong(request, "attencePolicy");
		Set<String>  holidayNameSet=  atsLegalHolidayDetailService.getHolidayNameByAttencePolicy(attencePolicy);
		return this.getAutoView().addObject("isSingle", isSingle).addObject("holidayNameSet",holidayNameSet);
	}
	/**
	 *取得班次设置分页列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("dialog")
	public ModelAndView dialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String isSingle = RequestUtil.getString(request, "isSingle", "false");
		return this.getAutoView().addObject("isSingle", isSingle);
	}

	/**
	 * 取得班次设置分页列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("selector")
	@Action(description = "查看班次设置分页列表")
	public ModelAndView selector(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String isSingle = RequestUtil.getString(request, "isSingle", "false");
		List<AtsShiftInfo> list = atsShiftInfoService.getAll(new QueryFilter(
				request, "atsShiftInfoItem"));
		for (AtsShiftInfo atsShiftInfo : list) {
			atsShiftInfo.setShiftTime(atsShiftTimeService.getShiftTime(atsShiftInfo.getId()));
			setAtsShiftInfo(atsShiftInfo);
		}
		return this.getAutoView().addObject("atsShiftInfoList", list).addObject("isSingle", isSingle);
	}
}
