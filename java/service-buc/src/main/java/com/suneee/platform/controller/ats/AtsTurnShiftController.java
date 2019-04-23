package com.suneee.platform.controller.ats;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.page.PageBean;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.StringPool;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.page.PageBean;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.StringPool;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.ats.AtsAttencePolicy;
import com.suneee.platform.model.ats.AtsAttendanceFile;
import com.suneee.platform.model.ats.AtsCardRule;
import com.suneee.platform.model.ats.AtsConstant;
import com.suneee.platform.model.ats.AtsScheduleShift;
import com.suneee.platform.model.ats.AtsShiftInfo;
import com.suneee.platform.model.ats.AtsShiftRuleDetail;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.ats.AtsAttencePolicyService;
import com.suneee.platform.service.ats.AtsAttendanceFileService;
import com.suneee.platform.service.ats.AtsCardRuleService;
import com.suneee.platform.service.ats.AtsLegalHolidayDetailService;
import com.suneee.platform.service.ats.AtsLegalHolidayService;
import com.suneee.platform.service.ats.AtsScheduleShiftService;
import com.suneee.platform.service.ats.AtsShiftInfoService;
import com.suneee.platform.service.ats.AtsShiftRuleDetailService;
import com.suneee.platform.service.ats.AtsWorkCalendarService;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.platform.service.system.UserPositionService;

/**
 * <pre>
 * 对象功能:轮班向导 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-18 17:21:46
 * </pre>
 */
@Controller
@RequestMapping("/platform/ats/atsTurnShift/")
public class AtsTurnShiftController extends BaseController {
	@Resource
	private AtsAttendanceFileService atsAttendanceFileService;
	@Resource
	private SysUserService sysUserService;
	@Resource
	private AtsAttencePolicyService atsAttencePolicyService;
	@Resource
	private AtsCardRuleService atsCardRuleService;
	@Resource
	private AtsShiftRuleDetailService atsShiftRuleDetailService;
	@Resource
	private AtsShiftInfoService atsShiftInfoService;
	@Resource
	private AtsWorkCalendarService atsWorkCalendarService;
	@Resource
	private AtsLegalHolidayService atsLegalHolidayService;
	@Resource
	private AtsLegalHolidayDetailService atsLegalHolidayDetailService;
	@Resource
	private AtsScheduleShiftService atsScheduleShiftService;
	@Resource
	private UserPositionService userPositionService;
	@Resource
	private SysOrgService sysOrgService;

	/**
	 * 取得轮班规则分页列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("wizard")
	@Action(description = "查看轮班规则分页列表")
	public ModelAndView wizard(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 默认数据
		// 开始结束时间
		Calendar ca = Calendar.getInstance();

		ca.set(Calendar.DAY_OF_MONTH, 1);
		Date startTime = ca.getTime();
		ca.add(Calendar.MONTH, 1);
		ca.add(Calendar.DAY_OF_MONTH, -1);
		Date endTime = ca.getTime();
		// 考勤制度
		AtsAttencePolicy atsAttencePolicy = atsAttencePolicyService
				.getByDefault();
		return this.getAutoView()
				.addObject("startTime", DateFormatUtil.formatDate(startTime))
				.addObject("endTime", DateFormatUtil.formatDate(endTime))
				.addObject("atsAttencePolicy", atsAttencePolicy);

	}

	/**
	 * 查询人员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("userList")
	@Action(description = "查询用户人员")
	@ResponseBody
	public JSONObject userList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, true);
		Long orgPath = RequestUtil.getLong(request, "orgPath");
		if(BeanUtils.isNotEmpty(orgPath)){
			SysOrg org =sysOrgService.getById(orgPath);
			if(BeanUtils.isNotEmpty(org))
				// 查找某一组织下包含其子类的所有组织
				filter.addFilter("path",org.getPath() + "%");
		}
		
		List<AtsAttendanceFile> list = atsAttendanceFileService.getList(filter);
		return getPageList(list, filter.getPageBean());
	}

	private JSONObject getPageList(List<AtsAttendanceFile> list,
			PageBean pageBean) {
		JSONArray jary = new JSONArray();
		for (AtsAttendanceFile atsAttendanceFile : list) {
			JSONObject json = new JSONObject();
			String orgNames = userPositionService
					.getOrgnamesByUserId(atsAttendanceFile.getUserId());
			json.accumulate("id", atsAttendanceFile.getId())
					.accumulate("account", atsAttendanceFile.getAccount())
					.accumulate("fullname", atsAttendanceFile.getUserName())
					.accumulate("cardNumber", atsAttendanceFile.getCardNumber())
					.accumulate("orgName", orgNames).accumulate("posName", "");
			jary.add(json);
		}

		JSONObject json = new JSONObject();
		json.element("results", jary.toString())
				.element("records", pageBean.getTotalCount())
				.element("page", pageBean.getCurrentPage())
				.element("total", pageBean.getTotalPage());
		return json;
	}

	/**
	 * 选择排班规则计算出排班数据
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("calculate")
	@ResponseBody
	public String calculate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long ruleId = RequestUtil.getLong(request, "ruleId");
		Integer startNum = RequestUtil.getInt(request, "startNum");
		// 编排的区间开始时间
		Date startDate = RequestUtil.getDate(request, "startDate",
				StringPool.DATE_FORMAT_DATE);
		// 实际选择的开始时间
		Date startTime = RequestUtil.getDate(request, "startTime",
				StringPool.DATE_FORMAT_DATE);
		// 实际选择的结束时间
		Date endTime = RequestUtil.getDate(request, "endTime",
				StringPool.DATE_FORMAT_DATE);
		Short holidayHandle = RequestUtil.getShort(request, "holidayHandle");
		Long attencePolicy = RequestUtil.getLong(request, "attencePolicy");

		Map<String, String> holidayMap = atsLegalHolidayDetailService
				.getHolidayMap(attencePolicy);
		List<AtsShiftRuleDetail> shiftRuleDetailList = atsShiftRuleDetailService
				.getByRuleId(ruleId);
		Map<Integer, AtsShiftRuleDetail> map = getShiftRuleDetailMap(shiftRuleDetailList);
		// 排班规则长度
		int shiftRuleSize = shiftRuleDetailList.size();
		// 2个时间的天数
		int betweenDays = DateUtil.daysBetween(startTime, endTime);
		int addHoliday = 0;
		JSONArray jay = new JSONArray();
		for (int i = 0; i <= betweenDays; i++) {
			Date date = DateUtil.addDay(startTime, i);
			String time = DateFormatUtil.formatDate(date);
			String shiftName = "";
			String holidayName = "";
			Long shiftId = null;
			short dateType = 0;

			JSONObject json = new JSONObject();
			/**
			 * <pre>
			 * 1:替换	     表示按照原有规则排班 只将法定节日那几天替换成法定节日;
			 * 2:不替换  表示按照原有规则排班,法定节假日按原有的排班规则;
			 * 3:顺延      表示 指法定节假日结束的第一天排班将从法定节假日开始的第一天班次的下个班次的开始。
			 * </pre>
			 */
			if (holidayHandle.shortValue() == AtsConstant.HOLIDAY_HANDLE_REPLACE
					|| holidayHandle == AtsConstant.HOLIDAY_HANDLE_NOREPLACE) {
				// 序号
				int sn = (i + startNum) % shiftRuleSize;
				if (sn == 0)
					sn = shiftRuleSize;
				AtsShiftRuleDetail atsShiftRuleDetail = map.get(sn);
				shiftName = atsShiftRuleDetail.getShiftName();
				shiftId = atsShiftRuleDetail.getShiftId();
				// 节假日处理
				dateType = atsShiftRuleDetail.getDateType();
				if (holidayHandle.shortValue() == AtsConstant.HOLIDAY_HANDLE_REPLACE) {
					holidayName = holidayMap.get(time);
					if (BeanUtils.isNotEmpty(holidayName)) {
						shiftId = null;
						shiftName = null;
						dateType = AtsConstant.DATE_TYPE_HOLIDAY;
						
					}
				}
			} else if (holidayHandle.shortValue() == AtsConstant.HOLIDAY_HANDLE_POSTPONE) {
				// 判断是否节假日
				holidayName = holidayMap.get(time);
				if (BeanUtils.isNotEmpty(holidayName)) {
					addHoliday = addHoliday + 1;
					dateType = AtsConstant.DATE_TYPE_HOLIDAY;
				} else {
					int sn = (i + startNum - addHoliday) % shiftRuleSize;
					if (sn == 0)
						sn = shiftRuleSize;
					AtsShiftRuleDetail atsShiftRuleDetail = map.get(sn);
					shiftName = atsShiftRuleDetail.getShiftName();
					shiftId = atsShiftRuleDetail.getShiftId();
					dateType = atsShiftRuleDetail.getDateType();
				}
			}
		
			json.accumulate("title", BeanUtils.isEmpty(shiftName)?"":shiftName)
					.accumulate("start", time)
					.accumulate("dateType", dateType)
					.accumulate("shiftId", shiftId)
					.accumulate("holidayName", BeanUtils.isEmpty(holidayName)?"":holidayName);
			jay.add(json);
		}

		JSONObject json = new JSONObject();
		json.accumulate("data", jay.toString()).accumulate("beginCol",
				DateUtil.daysBetween(startDate, startTime) + 1);
		return json.toString();
	}

	/**
	 * 获取规则的排序map
	 * 
	 * @param list
	 * @return
	 */
	private Map<Integer, AtsShiftRuleDetail> getShiftRuleDetailMap(
			List<AtsShiftRuleDetail> list) {
		Map<Integer, AtsShiftRuleDetail> map = new HashMap<Integer, AtsShiftRuleDetail>();
		for (AtsShiftRuleDetail atsShiftRuleDetail : list) {
			if (BeanUtils.isNotIncZeroEmpty(atsShiftRuleDetail.getShiftId())) {
				AtsShiftInfo atsShiftInfo = atsShiftInfoService
						.getById(atsShiftRuleDetail.getShiftId());
				atsShiftRuleDetail.setShiftName(atsShiftInfo.getName());
			}
			map.put(atsShiftRuleDetail.getSn(), atsShiftRuleDetail);
		}
		return map;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("scheduleList")
	@ResponseBody
	public JSONObject scheduleList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Date startTime = RequestUtil.getDate(request, "startTime",
				StringPool.DATE_FORMAT_DATE);
		Date endTime = RequestUtil.getDate(request, "endTime",
				StringPool.DATE_FORMAT_DATE);
		int betweenDays = DateUtil.daysBetween(startTime, endTime);
		JSONObject json = new JSONObject();
		JSONArray colNamesAry = new JSONArray();
		JSONArray colModelAry = new JSONArray();

		setJsonAry(colNamesAry, colModelAry, "ID", "fileId", "fileId", 80, true);
		setJsonAry(colNamesAry, colModelAry, "工号", "account", "account", 80,
				false);
		setJsonAry(colNamesAry, colModelAry, "姓名", "userName", "userName", 80,
				false);

		for (int i = 0; i <= betweenDays; i++) {
			Date date = DateUtil.addDay(startTime, i);
			String week = DateUtil.getWeekOfDate(date);
			String time = DateFormatUtil.formatDate(date);
			setJsonAry(colNamesAry, colModelAry, time + "</br>" + week, time,
					time, 90, false);
		}
		json.accumulate("colNames", colNamesAry.toString()).accumulate(
				"colModel", colModelAry.toString());

		return json;

	}

	private void setJsonAry(JSONArray colNamesAry, JSONArray colModelAry,
			String lable, String name, String index, int width, boolean hidden) {
		JSONObject json = new JSONObject();
		json.accumulate("label", lable).accumulate("width", width)
				.accumulate("name", name).accumulate("index", index)
				.accumulate("hidden", hidden);
		colNamesAry.add(lable);
		colModelAry.add(json);
	}

	/**
	 * 日历排班处理
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("calendarShiftHandler")
	@ResponseBody
	public JSONObject calendarShiftHandler(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Date startTime = RequestUtil.getDate(request, "startTime",
				StringPool.DATE_FORMAT_DATE);
		Date endTime = RequestUtil.getDate(request, "endTime",
				StringPool.DATE_FORMAT_DATE);
		int betweenDays = DateUtil.daysBetween(startTime, endTime);
		JSONArray jay = new JSONArray();
		for (int i = 0; i <= betweenDays; i++) {
			Date date = DateUtil.addDay(startTime, i);
			String time = DateFormatUtil.formatDate(date);
			JSONObject json = new JSONObject();
			json.accumulate("title", "").accumulate("start", time);
			jay.add(json);
		}
		JSONObject json = new JSONObject();
		json.element("success", true).element("results", jay.toString());

		return json;
	}

	/**
	 * 列表排班处理
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("listShiftHandler")
	@ResponseBody
	public JSONObject listShiftHandler(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Date startTime = RequestUtil.getDate(request, "startTime",
				StringPool.DATE_FORMAT_DATE);
		Date endTime = RequestUtil.getDate(request, "endTime",
				StringPool.DATE_FORMAT_DATE);
		String users = RequestUtil.getString(request, "users");
		int betweenDays = DateUtil.daysBetween(startTime, endTime);
		JSONArray userAry = JSONArray.fromObject(users);
		JSONArray jsonAry = new JSONArray();
		for (Object user : userAry) {
			JSONObject json = new JSONObject();
			JSONObject userJson = (JSONObject) user;
			Object fileId = userJson.get("id");
			Long userId = Long.valueOf(fileId.toString());
			json.accumulate("fileId", userId);
			json.accumulate("account", BeanUtils.isEmpty(userJson
					.get("account")) ? "" : userJson.get("account"));
			json.accumulate("userName", userJson.get("userName"));
			for (int i = 0; i <= betweenDays; i++) {
				Date date = DateUtil.addDay(startTime, i);
				String attenceTime = DateFormatUtil.formatDate(date);
				AtsScheduleShift ass = atsScheduleShiftService
						.getByFileIdAttenceTime(userId, date);
				JSONObject shiftJson = new JSONObject();
				String title = "";
				String dateType = "";
				String shiftId = "";
				String holidayName = "";
				if (BeanUtils.isNotEmpty(ass)) {
					if(BeanUtils.isNotIncZeroEmpty(ass.getShiftId())){
						AtsShiftInfo atsShiftInfo = atsShiftInfoService
								.getById(ass.getShiftId());
						title = atsShiftInfo.getName();
						shiftId = ass.getShiftId().toString();
					}
					dateType = ass.getDateType().toString();
				}
				shiftJson.accumulate("title", title)
						.accumulate("start", attenceTime)
						.accumulate("dateType", dateType)
						.accumulate("shiftId", shiftId)
						.accumulate("holidayName", holidayName);
				
				json.accumulate(attenceTime, shiftJson.toString());
			}
			jsonAry.add(json);
		}

		JSONObject json = new JSONObject();
		json.accumulate("results", jsonAry.toString());
		return json;
	}

	/**
	 * 完成
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("finished")
	@ResponseBody
	public String finished(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String userAry = RequestUtil.getString(request, "userAry");
		String listRowDatas = RequestUtil.getString(request, "listRowDatas");
		String attencePolicyName = RequestUtil.getString(request,
				"attencePolicyName");
		Short shiftType = RequestUtil.getShort(request, "shiftType");
		JSONArray userJsonAry = JSONArray.fromObject(userAry);
		JSONArray listRowDatasJson = JSONArray.fromObject(listRowDatas);
		List<AtsScheduleShift> list = null;
		JSONObject json = new JSONObject();

		json.element("success", true);
		try {
			list = atsScheduleShiftService.save(userJsonAry, listRowDatasJson,
					attencePolicyName, shiftType);
			String results = getScheduleShiftPageList(list);
			json.element("results", results);
		} catch (Exception e) {
			e.printStackTrace();
			json.element("success", false);
			json.element("results", "出错了" + e.getMessage());
		}

		return json.toString();
	}

	private String getScheduleShiftPageList(List<AtsScheduleShift> list) {
		JSONArray jary = new JSONArray();
		for (AtsScheduleShift atsScheduleShift : list) {
			JSONObject json = new JSONObject();
			String shiftName = "";
			String cardRuleName = "";
			String dateType = AtsConstant.DATE_TYPE_WORKDAY_STRING;
			if (BeanUtils.isNotIncZeroEmpty(atsScheduleShift.getShiftId())) {
				AtsShiftInfo atsShiftInfo = atsShiftInfoService
						.getById(atsScheduleShift.getShiftId());
				shiftName = atsShiftInfo.getName();
				AtsCardRule atsCardRule = atsCardRuleService
						.getById(atsShiftInfo.getCardRule());
				cardRuleName = atsCardRule.getName();
			}
			if (atsScheduleShift.getDateType() == AtsConstant.DATE_TYPE_HOLIDAY) {
				dateType = BeanUtils.isEmpty(atsScheduleShift.getTitle())?AtsConstant.DATE_TYPE_HOLIDAY_STRING:atsScheduleShift.getTitle();
			} else if (atsScheduleShift.getDateType() == AtsConstant.DATE_TYPE_DAYOFF) {
				dateType = AtsConstant.DATE_TYPE_DAYOFF_STRING;
			}
			json.accumulate("userName", atsScheduleShift.getUserName())
					.accumulate("orgName", atsScheduleShift.getOrgName())
					.accumulate(
							"attenceTime",
							DateFormatUtil.formatDate(atsScheduleShift
									.getAttenceTime()))
					.accumulate("dateType", dateType)
					.accumulate("shiftName", shiftName)
					.accumulate("cardNumber", atsScheduleShift.getCardNumber())
					.accumulate("policyName",
							atsScheduleShift.getAttencePolicyName())
					.accumulate("cardRuleName", cardRuleName);

			jary.add(json);
		}

		return jary.toString();
	}
}
