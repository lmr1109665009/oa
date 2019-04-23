package com.suneee.platform.controller.ats;

import com.suneee.core.excel.Excel;
import com.suneee.core.excel.editor.IFontEditor;
import com.suneee.core.excel.style.Color;
import com.suneee.core.excel.style.font.BoldWeight;
import com.suneee.core.excel.style.font.Font;
import com.suneee.core.excel.util.ExcelUtil;
import com.suneee.core.page.PageBean;
import com.suneee.core.util.*;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.ats.*;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.ats.*;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.UserPositionService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONStringer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 * 对象功能:考勤计算 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-31 13:51:09
 * </pre>
 */
@Controller
@RequestMapping("/platform/ats/atsAttenceCalculate/")
public class AtsAttenceCalculateController extends BaseController {
	public static String CALCULATING="_calculating_";//放计算session的常量——状态 true(正在运行)|false(结束)
	public static String CALCULATING_MSG="_calculatingMsg_";//放计算session的常量——信息
	@Resource
	private AtsAttenceCalculateService atsAttenceCalculateService;
	@Resource
	private AtsAttencePolicyService atsAttencePolicyService;

	@Resource
	private AtsAttenceCycleDetailService atsAttenceCycleDetailService;

	@Resource
	private AtsAttenceCalculateSetService atsAttenceCalculateSetService;
	@Resource
	private AtsAttendanceFileService atsAttendanceFileService;
	@Resource
	private AtsShiftInfoService atsShiftInfoService;
	@Resource
	private UserPositionService userPositionService;
	@Resource
	private SysOrgService sysOrgService;

	/**
	 * 取得考勤计算分页列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看考勤计算分页列表")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 考勤制度
		AtsAttencePolicy atsAttencePolicy = atsAttencePolicyService.getByDefault();
		// 考勤周期
		//		List<AtsAttenceCycleDetail> cycleList = null;
		//		if (BeanUtils.isNotEmpty(atsAttencePolicy))
		//			cycleList = atsAttenceCycleDetailService.getByCycleId(
		//					atsAttencePolicy.getAttenceCycle(), true);
		// 开始结束时间
		Calendar ca = Calendar.getInstance();

		ca.set(Calendar.DAY_OF_MONTH, 1);
		Date startTime = ca.getTime();
		ca.add(Calendar.MONTH, 1);
		ca.add(Calendar.DAY_OF_MONTH, -1);
		Date endTime = ca.getTime();
		return this.getAutoView().addObject("atsAttencePolicy", atsAttencePolicy).addObject("startTime", DateFormatUtil.formatDate(startTime)).addObject("endTime", DateFormatUtil.formatDate(endTime));
	}

	/**
	 * 取得考勤未计算人员分页列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getNoneCalList")
	@ResponseBody
	public JSONObject getNoneCalList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject json = new JSONObject();
		try {
			QueryFilter filter = new QueryFilter(request);
			Long orgPath = RequestUtil.getLong(request, "orgPath");
			if (BeanUtils.isNotEmpty(orgPath)) {
				SysOrg org = sysOrgService.getById(orgPath);
				if (BeanUtils.isNotEmpty(org))
					// 查找某一组织下包含其子类的所有组织
					filter.addFilter("path", org.getPath() + "%");
			}
			List<AtsAttendanceFile> list = atsAttendanceFileService.getNoneCalList(filter);
			PageBean page = filter.getPageBean();
			JSONArray jary = new JSONArray();
			for (AtsAttendanceFile atsAttendanceFile : list) {
				JSONObject j = new JSONObject();
				String orgNames = "";
				if (BeanUtils.isNotEmpty(atsAttendanceFile.getUserId()))
					orgNames = userPositionService.getOrgnamesByUserId(atsAttendanceFile.getUserId());
				j.accumulate("fileId", atsAttendanceFile.getId()).accumulate("cardNumber", atsAttendanceFile.getCardNumber()).accumulate("userName", BeanUtils.isEmpty(atsAttendanceFile.getUserName()) ? "" : atsAttendanceFile.getUserName()).accumulate("account", BeanUtils.isEmpty(atsAttendanceFile.getAccount()) ? "" : atsAttendanceFile.getAccount()).accumulate("orgName", orgNames);
				jary.add(j);
			}

			json.element("results", jary.toString()).element("records", page.getTotalCount()).element("page", page.getCurrentPage()).element("total", page.getTotalPage());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 获取表格的行列数据
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getGridColModel")
	@ResponseBody
	public JSONObject getGridColModel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Date startTime = RequestUtil.getDate(request, "startTime", StringPool.DATE_FORMAT_DATE);
		Date endTime = RequestUtil.getDate(request, "endTime", StringPool.DATE_FORMAT_DATE);
		int betweenDays = DateUtil.daysBetween(startTime, endTime);
		JSONObject json = new JSONObject();
		JSONArray colNamesAry = new JSONArray();
		JSONArray colModelAry = new JSONArray();

		this.setJsonAry(colNamesAry, colModelAry, "ID", "fileId", "fileId", 80, true, "");
		this.setJsonAry(colNamesAry, colModelAry, "组织ID", "orgName", "orgName", 80, true, "");
		this.setJsonAry(colNamesAry, colModelAry, "工号", "account", "account", 80, false, "");
		this.setJsonAry(colNamesAry, colModelAry, "姓名", "userName", "userName", 80, false, "");
		// 汇总字段
		setJsonSummary(colNamesAry, colModelAry);

		for (int i = 0; i <= betweenDays; i++) {
			Date date = DateUtil.addDay(startTime, i);
			String week = DateUtil.getWeekOfDate(date);
			String time = DateFormatUtil.format(date, "dd");
			this.setJsonAry(colNamesAry, colModelAry, time + "(" + week + ")", time, DateFormatUtil.formatDate(date), 85, false, "");
		}
		json.accumulate("colNames", colNamesAry.toString()).accumulate("colModel", colModelAry.toString());

		return json;

	}

	/**
	 * 设置汇总字段
	 * 
	 * @param colNamesAry
	 * @param colModelAry
	 */
	private void setJsonSummary(JSONArray colNamesAry, JSONArray colModelAry) {
		JSONArray jsonSet = this.getAtsAttenceCalculateSetSummary();
		for (Object obj : jsonSet) {
			JSONObject set = (JSONObject) obj;
			this.setJsonAry(colNamesAry, colModelAry, set.getString("lable"), set.getString("name"), set.getString("name"), 80, false, "sum");
		}
	}

	/**
	 * 获取汇总明细
	 * 
	 * @return
	 */
	private JSONArray getAtsAttenceCalculateSetSummary() {
		AtsAttenceCalculateSet atsAttenceCalculateSet = atsAttenceCalculateSetService.getDefault();
		JSONArray jary = new JSONArray();
		if (BeanUtils.isNotEmpty(atsAttenceCalculateSet) && BeanUtils.isNotEmpty(atsAttenceCalculateSet.getSummary()))
			jary = JSONArray.fromObject(atsAttenceCalculateSet.getSummary());
		if (JSONUtil.isEmpty(jary)) {
			jary = new JSONArray();
			JSONObject json2 = new JSONObject();
			json2.accumulate("lable", "旷工次数").accumulate("name", "S21");
			jary.add(json2);
			JSONObject json3 = new JSONObject();
			json3.accumulate("lable", "迟到次数").accumulate("name", "S31");
			jary.add(json3);
			JSONObject json4 = new JSONObject();
			json4.accumulate("lable", "早退次数").accumulate("name", "S41");
			jary.add(json4);
		}
		return jary;
	}

	/**
	 * 设置展示字段
	 * 
	 * @param colNamesAry
	 * @param colModelAry
	 * @param lable
	 * @param name
	 * @param index
	 * @param width
	 * @param hidden
	 * @param summaryType
	 */
	private void setJsonAry(JSONArray colNamesAry, JSONArray colModelAry, String lable, String name, String index, int width, boolean hidden, String summaryType) {
		JSONObject json = new JSONObject();
		json.accumulate("label", lable).accumulate("width", width).accumulate("name", name).accumulate("index", index).accumulate("hidden", hidden);
		if (BeanUtils.isNotEmpty(summaryType))
			json.accumulate("summaryType", summaryType);
		colNamesAry.add(lable);
		colModelAry.add(json);
	}

	@RequestMapping("reportGrid")
	@ResponseBody
	public JSONObject reportGrid(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Date startTime = RequestUtil.getDate(request, "Q_beginattenceTime_DL", StringPool.DATE_FORMAT_DATE);
		Date endTime = RequestUtil.getDate(request, "Q_endattenceTime_DE", StringPool.DATE_FORMAT_DATE);
		QueryFilter filter = new QueryFilter(request, true);
		Long orgPath = RequestUtil.getLong(request, "orgPath");
		if (BeanUtils.isNotEmpty(orgPath)) {
			SysOrg org = sysOrgService.getById(orgPath);
			if (BeanUtils.isNotEmpty(org))
				// 查找某一组织下包含其子类的所有组织
				filter.addFilter("path", org.getPath() + "%");
		}
		List<AtsAttenceCalculate> list = atsAttenceCalculateService.getList(filter);
		int betweenDays = DateUtil.daysBetween(startTime, endTime);
		return getPageList(list, filter.getPageBean(), betweenDays, startTime);
	}

	/**
	 * 分页数据
	 * 
	 * @param list
	 * @param pageBean
	 * @param betweenDays
	 * @param startTime
	 * @return
	 */
	private JSONObject getPageList(List<AtsAttenceCalculate> list, PageBean pageBean, int betweenDays, Date startTime) {
		JSONArray jary = new JSONArray();
		AtsAttenceCalculateSet atsAttenceCalculateSet = atsAttenceCalculateSetService.getDefault();
		for (AtsAttenceCalculate calculate : list) {
			Long fileId = calculate.getFileId();
			JSONObject json = new JSONObject();
			json.accumulate("fileId", fileId).accumulate("account", BeanUtils.isEmpty(calculate.getAccount()) ? "" : calculate.getAccount()).accumulate("userName", BeanUtils.isEmpty(calculate.getUserName()) ? "" : calculate.getUserName()).accumulate("orgName", BeanUtils.isEmpty(calculate.getOrgName()) ? "" : calculate.getOrgName());
			Double shouldAttenceHours = 0d;
			Double actualAttenceHours = 0d;
			Double absentNumber = 0d;
			Double lateNumber = 0d;
			Double leaveNumber = 0d;
			Double absentTime = 0d;
			Double lateTime = 0d;
			Double leaveTime = 0d;
			for (int i = 0; i <= betweenDays; i++) {

				Date date = DateUtil.addDay(startTime, i);
				String time = DateFormatUtil.format(date, "dd");
				AtsAttenceCalculate cal = atsAttenceCalculateService.getByFileIdAttenceTime(fileId, date);
				if (BeanUtils.isEmpty(cal)) {
					json.accumulate(time, "无排班记录");
					continue;
				}
				String tilte = this.getSetDetail(cal, atsAttenceCalculateSet);
				json.accumulate(time, tilte);

				if (BeanUtils.isNotEmpty(cal.getShouldAttenceHours()))
					shouldAttenceHours += cal.getShouldAttenceHours();
				if (BeanUtils.isNotEmpty(cal.getActualAttenceHours()))
					actualAttenceHours += cal.getActualAttenceHours();
				// 计算旷工、迟到、早退数
				if (BeanUtils.isNotEmpty(cal.getAbsentNumber()))
					absentNumber += cal.getAbsentNumber();
				if (BeanUtils.isNotEmpty(cal.getLateNumber()))
					lateNumber += cal.getLateNumber();
				if (BeanUtils.isNotEmpty(cal.getLeaveNumber()))
					leaveNumber += cal.getLeaveNumber();
				if (BeanUtils.isNotEmpty(cal.getAbsentTime()))
					absentTime += cal.getAbsentTime();
				if (BeanUtils.isNotEmpty(cal.getLateTime()))
					lateTime += cal.getLateTime();
				if (BeanUtils.isNotEmpty(cal.getLeaveTime()))
					leaveTime += cal.getLeaveTime();
			}
			JSONArray jsonSet = this.getAtsAttenceCalculateSetSummary();
			for (Object obj : jsonSet) {
				JSONObject set = (JSONObject) obj;
				String key1 = set.getString("name");
				double val = 0d;
				if (key1.contains("S11"))
					val = shouldAttenceHours;
				else if (key1.contains("S12"))
					val = actualAttenceHours;
				else if (key1.contains("S21"))
					val = absentNumber;
				else if (key1.contains("S22"))
					val = absentTime;
				else if (key1.contains("S31"))
					val = lateNumber;
				else if (key1.contains("S32"))
					val = lateTime;
				else if (key1.contains("S41"))
					val = leaveNumber;
				else if (key1.contains("S42"))
					val = leaveTime;

				json.accumulate(key1, val);
			}
			jary.add(json);

		}

		JSONObject json = new JSONObject();
		json.element("results", jary.toString()).element("records", pageBean.getTotalCount()).element("page", pageBean.getCurrentPage()).element("total", pageBean.getTotalPage());
		return json;
	}

	private String getSetDetail(AtsAttenceCalculate cal, AtsAttenceCalculateSet atsAttenceCalculateSet) {
		if (BeanUtils.isEmpty(cal))
			return "";
		String tilte = "";
		if (cal.getIsScheduleShift() == AtsConstant.NO)
			return AtsConstant.NO_SHIFT;
		Short dateType = cal.getDateType();

		//先处理考勤流程里面的数据
		if (cal.getOtNumber() != null && cal.getOtNumber() > 0) {
			return cal.getOtRecord();
		}
		if (cal.getHolidayNumber() != null && cal.getHolidayNumber() > 0) {
			return cal.getHolidayRecord();
		}
		if(cal.getTripNumber()!=null&&cal.getTripNumber()>0){
			return cal.getTripRecord();
		}
		
		if (dateType == AtsConstant.DATE_TYPE_DAYOFF) {
			tilte = AtsConstant.DATE_TYPE_DAYOFF_STRING;
		} else if (dateType == AtsConstant.DATE_TYPE_HOLIDAY) {
			tilte = BeanUtils.isEmpty(cal.getHolidayName()) ? AtsConstant.DATE_TYPE_HOLIDAY_STRING : cal.getHolidayName();
		} else {
			if (cal.getIsCardRecord() == AtsConstant.NO) {
				tilte = "无打卡记录";
			}
		}
		if (BeanUtils.isNotEmpty(tilte))
			return tilte;
		if (BeanUtils.isEmpty(atsAttenceCalculateSet) || BeanUtils.isEmpty(atsAttenceCalculateSet.getDetail()))
			return "实出勤时数:" + (BeanUtils.isEmpty(cal.getActualAttenceHours()) ? 0 : cal.getActualAttenceHours());
		JSONArray jary = JSONArray.fromObject(atsAttenceCalculateSet.getDetail());
		for (Object o : jary) {
			JSONObject json = (JSONObject) o;
			String name = json.getString("name");
			String lable = json.getString("lable");
			Double val = null;
			if (name.contains("S11"))
				val = cal.getShouldAttenceHours();
			else if (name.contains("S12"))
				val = cal.getActualAttenceHours();
			else if (name.contains("S21"))
				val = cal.getAbsentNumber();
			else if (name.contains("S22"))
				val = cal.getAbsentTime();
			else if (name.contains("S31"))
				val = cal.getLateNumber();
			else if (name.contains("S32"))
				val = cal.getLateTime();
			else if (name.contains("S41"))
				val = cal.getLeaveNumber();
			else if (name.contains("S42"))
				val = cal.getLeaveTime();
			if (BeanUtils.isNotEmpty(val) && val > 0)
				return lable + ":" + val;
		}

		return "实出勤时数:" + (BeanUtils.isEmpty(cal.getActualAttenceHours()) ? 0 : cal.getActualAttenceHours());
	}

	/**
	 * 全部计算
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("calculate")
	@ResponseBody
	public JSONObject calculate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Date startTime = RequestUtil.getDate(request, "startTime", StringPool.DATE_FORMAT_DATE);
		Date endTime = RequestUtil.getDate(request, "endTime", StringPool.DATE_FORMAT_DATE);
		Long attencePolicyId = RequestUtil.getLong(request, "attencePolicyId");
		Long[] fileIds = RequestUtil.getLongAryByStr(request, "fileIds");
		
		JSONObject json = new JSONObject();
		json.element("success", true);
		
		request.getSession().setAttribute(CALCULATING, true);//标记正在计算
		try {
			if (BeanUtils.isEmpty(fileIds))
				atsAttenceCalculateService.allCalculate(startTime, endTime, attencePolicyId);
			else
				// 选中记录
				atsAttenceCalculateService.calculate(startTime, endTime, attencePolicyId, fileIds);
		} catch (Exception e) {
			e.printStackTrace();
			json.element("success", false);
			json.element("results", "出错了" + e.getMessage());
		}
		request.getSession().setAttribute(CALCULATING, false);//标记
		
		return json;
	}

	/**
	 * 日历展示
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("calendar")
	@Action(description = "日历")
	public ModelAndView calendar(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Date startTime = RequestUtil.getDate(request, "startTime", StringPool.DATE_FORMAT_DATE);
		Date endTime = RequestUtil.getDate(request, "endTime", StringPool.DATE_FORMAT_DATE);
		Long fileId = RequestUtil.getLong(request, "fileId");

		List<AtsAttenceCalculate> list = atsAttenceCalculateService.getByFileIdAttenceTime(fileId, startTime, endTime);
		AtsAttenceCalculateSet atsAttenceCalculateSet = atsAttenceCalculateSetService.getById(1L);
		JSONArray jary = new JSONArray();
		for (AtsAttenceCalculate cal : list) {
			String backgroundColor = AtsConstant.BACKGROUND_COLOR_WORKDAY;
			if (cal.getDateType() == AtsConstant.DATE_TYPE_DAYOFF)
				backgroundColor = AtsConstant.BACKGROUND_COLOR_DAYOFF;
			else if (cal.getDateType() == AtsConstant.DATE_TYPE_HOLIDAY)
				backgroundColor = AtsConstant.BACKGROUND_COLOR_HOLIDAY;

			JSONObject json = new JSONObject();
			json.accumulate("title", getSetDetail(cal, atsAttenceCalculateSet)).accumulate("start", DateFormatUtil.formatDate(cal.getAttenceTime())).accumulate("backgroundColor", backgroundColor);
			jary.add(json);
		}
		return getAutoView().addObject("events", jary.toString()).addObject("startTime", startTime);
	}

	/**
	 * 汇总展示
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("summary")
	@Action(description = "汇总")
	public ModelAndView summary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Date startTime = RequestUtil.getDate(request, "startTime", StringPool.DATE_FORMAT_DATE);
		Date endTime = RequestUtil.getDate(request, "endTime", StringPool.DATE_FORMAT_DATE);
		Long fileId = RequestUtil.getLong(request, "fileId");
		String colName = RequestUtil.getString(request, "colName");

		List<AtsAttenceCalculate> list = atsAttenceCalculateService.getByFileIdAttenceTime(fileId, startTime, endTime);
		List<AtsAttenceCalculate> calculateList = new ArrayList<AtsAttenceCalculate>();
		for (AtsAttenceCalculate cal : list) {
			Double val = null;
			String unit = "次";
			if (colName.equals("S11")) {
				val = cal.getShouldAttenceHours();
				unit = "小时";
			} else if (colName.contains("S12")) {
				val = cal.getActualAttenceHours();
				unit = "小时";
			} else if (colName.contains("S21")) {
				val = cal.getAbsentNumber();
				unit = "次";
			} else if (colName.contains("S22")) {
				val = cal.getAbsentTime();
				unit = "小时";
			} else if (colName.contains("S31")) {
				val = cal.getLateNumber();
				unit = "次";
			} else if (colName.contains("S32")) {
				val = cal.getLateTime();
				unit = "分钟";
			} else if (colName.contains("S41")) {
				val = cal.getLeaveNumber();
			} else if (colName.contains("S42")) {
				val = cal.getLeaveTime();
				unit = "分钟";
			}

			if (BeanUtils.isNotEmpty(val) && val > 0) {
				AtsAttenceCalculate calculate = new AtsAttenceCalculate();
				calculate.setAttenceTime(cal.getAttenceTime());
				calculate.setShouldAttenceHours(val);
				calculate.setUnit(unit);
				calculateList.add(calculate);
			}
		}

		return getAutoView().addObject("calculateList", calculateList);
	}

	/**
	 * 考勤情况展示
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("bill")
	@Action(description = "考勤情况")
	public ModelAndView bill(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Date attenceTime = RequestUtil.getDate(request, "colName", StringPool.DATE_FORMAT_DATE);
		Long fileId = RequestUtil.getLong(request, "fileId");

		AtsAttenceCalculate calculate = atsAttenceCalculateService.getByFileIdAttenceTime(fileId, attenceTime);

		if (BeanUtils.isEmpty(calculate)) {
			return getAutoView().addObject("calculate", calculate);
		}

		this.setShiftTime(calculate);

		this.setAbsentRecord(calculate);

		return getAutoView().addObject("calculate", calculate);
	}

	private void setShiftTime(AtsAttenceCalculate calculate) {
		if (BeanUtils.isEmpty(calculate.getShiftTime()))
			return;
		JSONArray jary = JSONArray.fromObject(calculate.getShiftTime());
		for (Object obj : jary) {
			JSONObject json = (JSONObject) obj;
			Integer segment = (Integer) json.get("segment");
			String shiftTime = json.getString("onTime") + "--" + json.getString("offTime");
			if (segment.intValue() == AtsConstant.SEGMENT_1) {
				calculate.setShiftTime11(shiftTime);
			} else if (segment.intValue() == AtsConstant.SEGMENT_2) {
				calculate.setShiftTime21(shiftTime);
			} else if (segment.intValue() == AtsConstant.SEGMENT_3) {
				calculate.setShiftTime31(shiftTime);
			}
		}
	}

	private void setAbsentRecord(AtsAttenceCalculate calculate) {
		if (BeanUtils.isEmpty(calculate.getAbsentRecord()))
			return;
		JSONArray jary = JSONArray.fromObject(calculate.getAbsentRecord());
		for (int i = 0; i < jary.size(); i++) {
			JSONObject json = (JSONObject) jary.get(i);
			Integer segment = (Integer) json.get("segment");
			String absentRecord = "无";
			Object onCardTime = null;
			Object offCardTime = null;
			if (BeanUtils.isNotEmpty(segment)) {
				onCardTime = json.get("onCardTime");
				offCardTime = json.get("offCardTime");
			} else {
				segment = i + 1;
			}

			if (BeanUtils.isNotEmpty(onCardTime) && BeanUtils.isNotEmpty(offCardTime)) {
				absentRecord = onCardTime + "--" + offCardTime.toString();
			} else if (BeanUtils.isNotEmpty(onCardTime))
				absentRecord = onCardTime + "--无";
			else if (BeanUtils.isNotEmpty(offCardTime))
				absentRecord = "无--" + offCardTime;

			if (segment.intValue() == AtsConstant.SEGMENT_1) {
				calculate.setAbsentRecord11(absentRecord);
			} else if (segment.intValue() == AtsConstant.SEGMENT_2) {
				calculate.setAbsentRecord21(absentRecord);
			} else if (segment.intValue() == AtsConstant.SEGMENT_3) {
				calculate.setAbsentRecord31(absentRecord);
			}
		}
	}

	/**
	 * 考勤结果导出明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("exportReportDetail")
	public void exportReportDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, false);
		Long orgPath = RequestUtil.getLong(request, "orgPath");
		Integer action = RequestUtil.getInt(request, "action");
		if (BeanUtils.isNotEmpty(orgPath)) {
			SysOrg org = sysOrgService.getById(orgPath);
			if (BeanUtils.isNotEmpty(org))
				// 查找某一组织下包含其子类的所有组织
				filter.addFilter("path", org.getPath() + "%");
		}
		if (action == 3) {
			String[] type = RequestUtil.getStringAryByStr(request, "type");
			if (BeanUtils.isNotEmpty(type)) {
				List<String> attenceType = new ArrayList<String>();
				for (String t : type) {
					attenceType.add("%" + t + "%");
				}
				filter.addFilter("attenceType", attenceType);
			}
		}

		List<AtsAttenceCalculate> list = atsAttenceCalculateService.getListData(filter);
		try {
			Excel excel = new Excel();
			excel.sheet().sheetName("考勤结果明细");// 重命名当前处于工作状态的表的名称
			if (action == 1 || action == 2) {
				String[] titleAry = new String[] { "姓名", "员工编号", "组织", "考勤日期", "应出勤时数", "实际出勤时数", "迟到次数", "迟到分钟", "早退次数", "早退分钟", "旷工次数", "旷工小时数" };
				excel.row(0, 0).value(titleAry).font(new IFontEditor() {
					// 设置字体
					@Override
					public void updateFont(Font font) {
						font.boldweight(BoldWeight.BOLD);// 粗体
					}
				}).bgColor(Color.GREY_25_PERCENT).width(4000);

				for (int i = 0; i < list.size(); i++) {
					AtsAttenceCalculate cal = list.get(i);
					List<Object> rowData = new ArrayList<Object>();
					rowData.add(cal.getUserName());
					rowData.add(cal.getAccount());
					rowData.add(cal.getOrgName());
					rowData.add(DateFormatUtil.formatDate(cal.getAttenceTime()));
					rowData.add(cal.getShouldAttenceHours());
					rowData.add(cal.getActualAttenceHours());
					rowData.add(cal.getLateNumber());
					rowData.add(cal.getLateTime());
					rowData.add(cal.getLeaveNumber());
					rowData.add(cal.getLeaveTime());
					rowData.add(cal.getAbsentNumber());
					rowData.add(cal.getAbsentTime());
					excel.row(i + 1).value(rowData.toArray());
				}
			} else {// 考勤结果
				String[] titleAry = new String[] { "姓名", "工号", "组织", "考勤日期", "星期", "班次名称", "异常类型", "第一段上班", "第一段下班", "第二段上班", "第二段下班", "第三段上班", "第三段下班" };
				excel.row(0, 0).value(titleAry).font(new IFontEditor() {
					// 设置字体
					@Override
					public void updateFont(Font font) {
						font.boldweight(BoldWeight.BOLD);// 粗体
					}
				}).bgColor(Color.GREY_25_PERCENT).width(4000);

				for (int i = 0; i < list.size(); i++) {
					AtsAttenceCalculate calculate = list.get(i);
					String orgNames = userPositionService.getOrgnamesByUserId(calculate.getUserId());

					String attenceType = AtsUtils.getAttenceType(calculate, "、", true);
					getAbsentRecord(calculate);
					List<Object> rowData = new ArrayList<Object>();
					rowData.add(calculate.getUserName());
					rowData.add(calculate.getAccount());
					rowData.add(orgNames);
					rowData.add(DateFormatUtil.formatDate(calculate.getAttenceTime()));
					rowData.add(DateUtil.getWeekOfDate(calculate.getAttenceTime()));
					rowData.add(BeanUtils.isNotEmpty(calculate.getShiftName()) ? calculate.getShiftName() : "");
					rowData.add(attenceType);
					rowData.add(calculate.getAbsentRecord11());
					rowData.add(calculate.getAbsentRecord12());
					rowData.add(calculate.getAbsentRecord21());
					rowData.add(calculate.getAbsentRecord22());
					rowData.add(calculate.getAbsentRecord31());
					rowData.add(calculate.getAbsentRecord32());
					excel.row(i + 1).value(rowData.toArray());
				}
			}

			// 取得表的数据
			String fileName = "考勤结果明细";
			ExcelUtil.downloadExcel(excel.getWorkBook(), fileName, response);

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.getSession().setAttribute("isDownload", true);
	}

	@RequestMapping("isDownload")
	public void isDownload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean isDownload = (Boolean) request.getSession().getAttribute("isDownload");
		request.getSession().setAttribute("isDownload", false);
		JSONStringer stringer = new JSONStringer();
		stringer.object().key("success").value(isDownload).endObject();
		response.getWriter().print(stringer);
	}

	/**
	 * 考勤结果明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("detailList")
	@ResponseBody
	public JSONObject detailList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, true);
		String[] type = RequestUtil.getStringAryByStr(request, "type");
		Long orgPath = RequestUtil.getLong(request, "orgPath");
		if (BeanUtils.isNotEmpty(orgPath)) {
			SysOrg org = sysOrgService.getById(orgPath);
			if (BeanUtils.isNotEmpty(org))
				// 查找某一组织下包含其子类的所有组织
				filter.addFilter("path", org.getPath() + "%");
		}
		if (BeanUtils.isNotEmpty(type)) {
			List<String> attenceType = new ArrayList<String>();
			for (String t : type) {
				attenceType.add("%" + t + "%");
			}
			filter.addFilter("attenceType", attenceType);

		}

		List<AtsAttenceCalculate> list = atsAttenceCalculateService.getListData(filter);

		return getPageDetailList(list, filter.getPageBean());
	}
	
	/**
	 * 获取计算时的状态
	 * 因为计算通常需要耗时很久，需要更新点状态来告诉操作者数据在变化
	 * 使其不至于认为页面挂掉
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 * List<AtsAttendanceFile>
	 * @exception 
	 * @since  1.0.0
	 */
	@RequestMapping("getCalculatingState")
	@ResponseBody
	public JSONObject getCalculatingState(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status",request.getSession().getAttribute(CALCULATING));
		jsonObject.put("msg",request.getSession().getAttribute(CALCULATING_MSG));
		return jsonObject;
	}
	
	/**
	 * 分页数据
	 * 
	 * @param list
	 * @param pageBean
	 * @param betweenDays
	 * @param startTime
	 * @return
	 */
	private JSONObject getPageDetailList(List<AtsAttenceCalculate> list, PageBean pageBean) {
		JSONArray jary = new JSONArray();
		for (AtsAttenceCalculate calculate : list) {
			/*if(DateFormatUtil.formatDate(calculate.getAttenceTime()).equals("2015-07-21")){
				System.out.println();
			}*/
			JSONObject json = new JSONObject();
			String orgNames = userPositionService.getOrgnamesByUserId(calculate.getUserId());
			String attenceType = AtsUtils.getAttenceType(calculate, "、", true);
			getAbsentRecord(calculate);//设置一下没有效打卡却有打卡的明细显示时间，默认放在第一段上班和下班时间
			String abnormity =calculate.getAbnormity()==AtsAttenceCalculate.AbnormityType.normal?"否":"是";
			
			json.accumulate("id", calculate.getId()).accumulate("account", BeanUtils.isEmpty(calculate.getAccount()) ? "" : calculate.getAccount()).accumulate("userName", BeanUtils.isEmpty(calculate.getUserName()) ? "" : calculate.getUserName()).accumulate("orgName", BeanUtils.isEmpty(orgNames) ? "" : orgNames).accumulate("attenceTime", DateFormatUtil.formatDate(calculate.getAttenceTime())).accumulate("week", DateUtil.getWeekOfDate(calculate.getAttenceTime())).accumulate("shiftName", BeanUtils.isNotEmpty(calculate.getShiftName()) ? calculate.getShiftName() : "").accumulate("attenceType", attenceType).accumulate("shiftTime11", BeanUtils.isEmpty(calculate.getAbsentRecord11()) ? "" : calculate.getAbsentRecord11())
					.accumulate("shiftTime12", BeanUtils.isEmpty(calculate.getAbsentRecord12()) ? "" : calculate.getAbsentRecord12()).accumulate("shiftTime21", BeanUtils.isEmpty(calculate.getAbsentRecord21()) ? "" : calculate.getAbsentRecord21()).accumulate("shiftTime22", BeanUtils.isEmpty(calculate.getAbsentRecord22()) ? "" : calculate.getAbsentRecord22()).accumulate("shiftTime31", BeanUtils.isEmpty(calculate.getAbsentRecord31()) ? "" : calculate.getAbsentRecord31()).accumulate("shiftTime32", BeanUtils.isEmpty(calculate.getAbsentRecord32()) ? "" : calculate.getAbsentRecord32()).accumulate("abnormity", abnormity);//TODO
			jary.add(json);
		}
		JSONObject json = new JSONObject();
		json.element("results", jary.toString()).element("records", pageBean.getTotalCount()).element("page", pageBean.getCurrentPage()).element("total", pageBean.getTotalPage());
		return json;
	}

	private void getAbsentRecord(AtsAttenceCalculate calculate) {
		//不包含旷工才设置
		if (BeanUtils.isEmpty(calculate.getAbsentRecord())&&!calculate.getAttenceType().contains("00"+AtsConstant.TIME_TYPE_ABSENT)){
			if(StringUtil.isNotEmpty(calculate.getCardRecord())){
				String[] strs = calculate.getCardRecord().split("\\|");
				calculate.setAbsentRecord11(strs[0]);
				calculate.setAbsentRecord12(strs[strs.length-1]);
				//System.out.println(strs);
			}
			return;
		}
			
		JSONArray jary = JSONArray.fromObject(calculate.getAbsentRecord());
		for (int i = 0; i < jary.size(); i++) {
			if(jary.get(i) == null || "null".equals(jary.get(i).toString()))  
				continue;
			JSONObject json = (JSONObject) jary.get(i);
			Integer segment = (Integer) json.get("segment");
			Object onCardTime = null;
			Object offCardTime = null;
			if (BeanUtils.isNotEmpty(segment)) {
				onCardTime = json.get("onCardTime");
				offCardTime = json.get("offCardTime");
			} else {
				segment = i + 1;
			}
			String onTime = "";
			String offTime = "";
			if (BeanUtils.isNotEmpty(onCardTime))
				onTime = onCardTime.toString();
			if (BeanUtils.isNotEmpty(offCardTime))
				offTime = offCardTime.toString();

			if (segment.intValue() == AtsConstant.SEGMENT_1) {
				calculate.setAbsentRecord11(onTime);
				calculate.setAbsentRecord12(offTime);
			} else if (segment.intValue() == AtsConstant.SEGMENT_2) {
				calculate.setAbsentRecord21(onTime);
				calculate.setAbsentRecord22(offTime);
			} else if (segment.intValue() == AtsConstant.SEGMENT_3) {
				calculate.setAbsentRecord31(onTime);
				calculate.setAbsentRecord32(offTime);
			}
		}

	}
}
