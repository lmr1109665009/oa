package com.suneee.platform.controller.ats;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.excel.Excel;
import com.suneee.core.excel.editor.IFontEditor;
import com.suneee.core.excel.style.BorderStyle;
import com.suneee.core.excel.style.Color;
import com.suneee.core.excel.style.DataFormat;
import com.suneee.core.excel.style.font.BoldWeight;
import com.suneee.core.excel.style.font.Font;
import com.suneee.core.excel.util.ExcelUtil;
import com.suneee.core.page.PageBean;
import com.suneee.core.util.*;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.excel.Excel;
import com.suneee.core.excel.editor.IFontEditor;
import com.suneee.core.excel.style.BorderStyle;
import com.suneee.core.excel.style.Color;
import com.suneee.core.excel.style.DataFormat;
import com.suneee.core.excel.style.font.BoldWeight;
import com.suneee.core.excel.style.font.Font;
import com.suneee.core.excel.util.ExcelUtil;
import com.suneee.core.page.PageBean;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringPool;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.ats.AtsConstant;
import com.suneee.platform.model.ats.AtsScheduleShift;
import com.suneee.platform.model.ats.AtsShiftInfo;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.ats.AtsAttendanceFileService;
import com.suneee.platform.service.ats.AtsScheduleShiftService;
import com.suneee.platform.service.ats.AtsShiftInfoService;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.platform.xml.util.MsgUtil;

/**
 * <pre>
 * 对象功能:排班列表 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-25 17:05:06
 * </pre>
 */
@Controller
@RequestMapping("/platform/ats/atsScheduleShift/")
public class AtsScheduleShiftController extends BaseController {
	@Resource
	private AtsScheduleShiftService atsScheduleShiftService;

	@Resource
	private AtsAttendanceFileService atsAttendanceFileService;
	@Resource
	private UserPositionService userPositionService;

	@Resource
	private AtsShiftInfoService atsShiftInfoService;
	@Resource
	private SysOrgService sysOrgService;
	/**
	 * 添加或更新排班列表。
	 * 
	 * @param request
	 * @param response
	 * @param atsScheduleShift
	 *            添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "添加或更新排班列表")
	public void save(HttpServletRequest request, HttpServletResponse response,
			AtsScheduleShift atsScheduleShift) throws Exception {
		String resultMsg = null;
		try {
			if (atsScheduleShift.getId() == null
					|| atsScheduleShift.getId() == 0) {
				resultMsg = getText("添加", "排班列表");
			} else {
				resultMsg = getText("更新", "排班列表");
			}
			atsScheduleShiftService.save(atsScheduleShift);
			writeResultMessage(response.getWriter(), resultMsg,
					ResultMessage.Success);
		} catch (Exception e) {
			writeResultMessage(response.getWriter(),
					resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}
	}

	/**
	 * 取得排班列表分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看排班列表分页列表")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
//		List<AtsScheduleShift> list = atsScheduleShiftService
//				.getList(new QueryFilter(request, "atsScheduleShiftItem"));
//		for (AtsScheduleShift atsScheduleShift : list) {
//			String orgNames = userPositionService
//					.getOrgnamesByUserId(atsScheduleShift.getUserId());
//			atsScheduleShift.setOrgName(orgNames);
//		}
		// 开始结束时间
		Calendar ca = Calendar.getInstance();

		ca.set(Calendar.DAY_OF_MONTH, 1);
		Date startTime = ca.getTime();
		ca.add(Calendar.MONTH, 1);
		ca.add(Calendar.DAY_OF_MONTH, -1);
		Date endTime = ca.getTime();
		return this.getAutoView()
				.addObject("startTime", DateFormatUtil.formatDate(startTime))
				.addObject("endTime", DateFormatUtil.formatDate(endTime));
	}
	/**
	 * 删除排班列表
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除排班列表")
	public void del(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			atsScheduleShiftService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除排班列表成功!");
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败"
					+ ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 删除排班列表
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("delData")
	@Action(description = "删除排班列表")
	@ResponseBody
	public JSONObject delData(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		JSONObject json = new JSONObject();
		try {
			Integer showType = RequestUtil.getInt(request, "showType",0);
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			if(showType == 0)
				atsScheduleShiftService.delByIds(lAryId);
			else{
				Date startTime = RequestUtil.getDate(request, "startTime",
						StringPool.DATE_FORMAT_DATE);
				Date endTime = RequestUtil.getDate(request, "endTime",
						StringPool.DATE_FORMAT_DATE);
				atsScheduleShiftService.delByFileIdAttenceTime(lAryId,startTime,endTime);
			}
			json.accumulate("success", true).accumulate("msg", "删除排班列表成功!");

		} catch (Exception ex) {
			ex.printStackTrace();
			 json.accumulate("success", false).accumulate("msg","删除排班列表失败!");	
		}
	
		return json;
	}
	
	
	

	/**
	 * 编辑排班列表
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description = "编辑排班列表")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		Long id = RequestUtil.getLong(request, "id", 0L);
		String returnUrl = RequestUtil.getPrePage(request);
		AtsScheduleShift atsScheduleShift = atsScheduleShiftService.getById(id);

		return getAutoView().addObject("atsScheduleShift", atsScheduleShift)
				.addObject("returnUrl", returnUrl);
	}

	/**
	 * 取得排班列表明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description = "查看排班列表明细")
	public ModelAndView get(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		AtsScheduleShift atsScheduleShift = atsScheduleShiftService.getById(id);
		return getAutoView().addObject("atsScheduleShift", atsScheduleShift);
	}

	/**
	 * 排班列表的标题
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("scheduleShiftGrid")
	@Action(description = "排班列表的标题")
	@ResponseBody
	public JSONObject scheduleShiftGrid(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Integer showType = RequestUtil.getInt(request, "showType",0);
		JSONObject json = new JSONObject();
		JSONArray colNamesAry = new JSONArray();
		JSONArray colModelAry = new JSONArray();

		this.setJsonAry(colNamesAry, colModelAry, "主键", "id", "id", 80, true,true);
		this.setJsonAry(colNamesAry, colModelAry, "姓名", "userName", "userName", 80,
				false,true);
		this.setJsonAry(colNamesAry, colModelAry, "所属组织", "orgName", "orgName", 80,false,true);
		
		if(showType == 0){//列表模式
			this.setJsonAry(colNamesAry, colModelAry, "考勤日期", "attenceTime", "attenceTime", 80,false,false);
			this.setJsonAry(colNamesAry, colModelAry, "日期类型", "dateType", "dateType", 80,false,false);
			this.setJsonAry(colNamesAry, colModelAry, "班次名称", "shiftName", "shiftName", 80,false,false);
			this.setJsonAry(colNamesAry, colModelAry, "考勤卡号", "cardNumber", "cardNumber", 80,false,false);
			this.setJsonAry(colNamesAry, colModelAry, "考勤制度", "attencePolicyName", "attencePolicyName", 80,false,false);
			this.setJsonAry(colNamesAry, colModelAry, "取卡规则", "cardRuleName", "cardRuleName", 80,false,false);
		}else{
			Date startTime = RequestUtil.getDate(request, "startTime",
					StringPool.DATE_FORMAT_DATE);
			Date endTime = RequestUtil.getDate(request, "endTime",
					StringPool.DATE_FORMAT_DATE);
			int betweenDays = DateUtil.daysBetween(startTime, endTime);
			for (int i = 0; i <= betweenDays; i++) {
				Date date = DateUtil.addDay(startTime, i);
				String week = DateUtil.getWeekOfDate(date);
				String time = DateFormatUtil.formatDate(date);
				this.setJsonAry(colNamesAry, colModelAry, time + "</br>" + week, time,
						time, 90, false,false);
			}
			
		}
		 json.accumulate("success", true)
			.accumulate("colNames", colNamesAry.toString())
			.accumulate("colModel", colModelAry.toString());
		
		return json;

	}

	private void setJsonAry(JSONArray colNamesAry, JSONArray colModelAry,
			String lable, String name, String index, int width, boolean hidden,boolean frozen) {
		JSONObject json = new JSONObject();
		json.accumulate("label", lable).accumulate("width", width)
				.accumulate("name", name).accumulate("index", index)
				.accumulate("hidden", hidden);
		if(frozen){
			json.accumulate("frozen", true);
		}
		colNamesAry.add(lable);
		colModelAry.add(json);
	}

	/**
	 * 排班列表的数据
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("scheduleShiftData")
	@Action(description = "排班列表的数据")
	@ResponseBody
	public JSONObject scheduleShiftData(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Integer showType = RequestUtil.getInt(request, "showType",0);
		Long orgPath = RequestUtil.getLong(request, "orgPath");
		if(showType == 0){
			QueryFilter queryFilter = new QueryFilter(request, true);
			if(BeanUtils.isNotEmpty(orgPath)){
				SysOrg org =sysOrgService.getById(orgPath);
				if(BeanUtils.isNotEmpty(org))
					// 查找某一组织下包含其子类的所有组织
					queryFilter.addFilter("path",org.getPath() + "%");
			}
			
			List<AtsScheduleShift> list = atsScheduleShiftService
						.getList(queryFilter);
			JSONObject json = getPageList(list, queryFilter.getPageBean());
			 return json;
		}else{
			QueryFilter queryFilter = new QueryFilter(request, true);
			if(BeanUtils.isNotEmpty(orgPath)){
				SysOrg org =sysOrgService.getById(orgPath);
				if(BeanUtils.isNotEmpty(org))
					// 查找某一组织下包含其子类的所有组织
					queryFilter.addFilter("path",org.getPath() + "%");
			}
			List<AtsScheduleShift> list = atsScheduleShiftService
						.getCalendar(queryFilter);	
			JSONArray jsonAry = new JSONArray();
			for (AtsScheduleShift atsScheduleShift : list) {
				JSONObject json = new JSONObject();
	
				String orgNames = userPositionService
						.getOrgnamesByUserId(atsScheduleShift.getUserId());
				Long fileId = atsScheduleShift.getFileId();
				json.accumulate("id", atsScheduleShift.getFileId())
					.accumulate("userName", atsScheduleShift.getUserName())
					.accumulate("orgName", orgNames);
				Date startTime = RequestUtil.getDate(request, "Q_beginattenceTime_DL",
						StringPool.DATE_FORMAT_DATE);
				Date endTime = RequestUtil.getDate(request, "Q_endattenceTime_DE",
						StringPool.DATE_FORMAT_DATE);
				int betweenDays = DateUtil.daysBetween(startTime, endTime);
				for (int i = 0; i <= betweenDays; i++) {
					Date date = DateUtil.addDay(startTime, i);
					String attenceTime = DateFormatUtil.formatDate(date);
					AtsScheduleShift ass = atsScheduleShiftService.getByFileIdAttenceTime(fileId, date);
//					JSONObject shiftJson = new JSONObject();
					String title = "";
					if (BeanUtils.isNotEmpty(ass)) {
						title = ass.getDateType()== AtsConstant.DATE_TYPE_WORKDAY?"【工作日】":( ass.getDateType()== AtsConstant.DATE_TYPE_DAYOFF?"【休息日】":"【节假日】");
						
						if(BeanUtils.isNotIncZeroEmpty(ass.getShiftId())){
							AtsShiftInfo atsShiftInfo = atsShiftInfoService
									.getById(ass.getShiftId());
							title += atsShiftInfo.getName();
						}
					}
					json.accumulate(attenceTime,title);
				}
				jsonAry.add(json);
			}
			PageBean pageBean = queryFilter.getPageBean();
			JSONObject json = new JSONObject();
				json.accumulate("results", jsonAry.toString())
				.element("records", pageBean.getTotalCount())
				.element("page", pageBean.getCurrentPage())
				.element("total", pageBean.getTotalPage());
			return json;
		}
	
	}
	
	private JSONObject getPageList(List<AtsScheduleShift> list,
			PageBean pageBean) {
		JSONArray jary = new JSONArray();
		
		for (AtsScheduleShift atsScheduleShift : list) {
			String orgNames = userPositionService
					.getOrgnamesByUserId(atsScheduleShift.getUserId());
			atsScheduleShift.setOrgName(orgNames);
			JSONObject json = new JSONObject();
			json.accumulate("id", atsScheduleShift.getId())
				.accumulate("userName", atsScheduleShift.getUserName())
				.accumulate("orgName", BeanUtils.isNotEmpty(orgNames)?orgNames:"")
				.accumulate("attenceTime", DateFormatUtil.formatDate(atsScheduleShift.getAttenceTime()))
				.accumulate("dateType", atsScheduleShift.getDateType()== AtsConstant.DATE_TYPE_WORKDAY?"工作日":( atsScheduleShift.getDateType()== AtsConstant.DATE_TYPE_DAYOFF?"休息日":"节假日"))
				.accumulate("shiftName", BeanUtils.isNotEmpty(atsScheduleShift.getShiftName())?atsScheduleShift.getShiftName():"")
				.accumulate("cardNumber", atsScheduleShift.getCardNumber())
				.accumulate("attencePolicyName", BeanUtils.isNotEmpty(atsScheduleShift.getAttencePolicyName())?atsScheduleShift.getAttencePolicyName():"")
				.accumulate("cardRuleName", BeanUtils.isNotEmpty(atsScheduleShift.getCardRuleName())?atsScheduleShift.getCardRuleName():"");
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
	 * 导出排班记录
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("exportData")
	@Action(description = "导出排班记录")
	public void exportData(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] accounts = RequestUtil.getStringAryByStr(request, "accounts");
		String[] userNames = RequestUtil
				.getStringAryByStr(request, "userNames");
		String[] orgNames = RequestUtil.getStringAryByStr(request, "orgNames");
		String[] shiftNames = RequestUtil.getStringAryByStr(request,
				"shiftNames");

		int template = RequestUtil.getInt(request, "template", 1);

		Date startTime = RequestUtil.getDate(request, "startTime");
		Date endTime = RequestUtil.getDate(request, "endTime");

		try {
			// 取得表的数据
			String fileName = "员工排班列表";
			Excel excel = new Excel();
			excel.sheet().sheetName(fileName);// 重命名当前处于工作状态的表的名称

			if (template == 1)
				getExcelTemplate1(excel, accounts, userNames, orgNames,
						shiftNames, startTime, endTime);
			else if (template == 2)
				getExcelTemplate2(excel, accounts, userNames, orgNames,
						shiftNames, startTime, endTime);
			ExcelUtil.downloadExcel(excel.getWorkBook(), fileName, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出模版一
	 * 
	 * @param excel
	 * @param accounts
	 * @param userNames
	 * @param orgNames
	 * @param shiftNames
	 * @param startTime
	 * @param endTime
	 */
	private void getExcelTemplate1(Excel excel, String[] accounts,
			String[] userNames, String[] orgNames, String[] shiftNames,
			Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		excel.row(0, 0)
				.value(new String[] { "排班时间范围",
						DateFormatUtil.formatDate(startTime),
						DateFormatUtil.formatDate(endTime) }).width(4000);
		excel.region(0, 0, 0, 0).bgColor(Color.GREY_25_PERCENT)
				.font(new IFontEditor() {
					// 设置字体
					@Override
					public void updateFont(Font font) {
						font.boldweight(BoldWeight.BOLD);// 粗体
					}
				});

		List<String> titleList2 = new ArrayList<String>();

		List<String> titleList = new ArrayList<String>();
		titleList.add("员工编号");
		titleList.add("姓名");
		titleList.add("组织名称");
		int betweenDays = DateUtil.daysBetween(startTime, endTime);
		for (int j = 0; j <= betweenDays; j++) {
			Date attenceTime = DateUtil.addDay(startTime, j);
			String week = DateUtil.getWeekOfDate(attenceTime);
			String day = DateFormatUtil.format(attenceTime, "d");
			titleList2.add(week);
			titleList.add(day);
		}
		excel.row(1, 0).value(titleList.toArray()).font(new IFontEditor() {
			// 设置字体
			@Override
			public void updateFont(Font font) {
				font.boldweight(BoldWeight.BOLD);// 粗体
			}
		}).bgColor(Color.SKY_BLUE).width(4000).dataFormat("@")
				.borderFull(BorderStyle.THIN, Color.AUTOMATIC);

		excel.row(2, 3).value(titleList2.toArray()).font(new IFontEditor() {
			// 设置字体
			@Override
			public void updateFont(Font font) {
				font.boldweight(BoldWeight.BOLD);// 粗体
			}
		}).borderFull(BorderStyle.THIN, Color.AUTOMATIC);
		excel.region(1, 0, 2, 0).merge()
				.borderFull(BorderStyle.THIN, Color.AUTOMATIC);
		excel.region(1, 1, 2, 1).merge()
				.borderFull(BorderStyle.THIN, Color.AUTOMATIC);
		List<String> accountList = new ArrayList<String>();
		List<String> userNameList = new ArrayList<String>();
		List<String> orgNameList = new ArrayList<String>();
		if (BeanUtils.isNotEmpty(accounts)) {
			for (int i = 0; i < accounts.length; i++) {
				String account = accounts[i];
				String userName = "";
				if (BeanUtils.isNotEmpty(userNames))
					userName = userNames[i];
				String orgName = "";
				try {
					if (BeanUtils.isNotEmpty(orgNames))
						orgName = orgNames[i];
				} catch (Exception e) {
					// TODO: handle exception
				}

				accountList.add(account);
				userNameList.add(userName);
				orgNameList.add(orgName);
			}
		}
		excel.column(0, 3).value(accountList.toArray());
		excel.column(1, 3).value(userNameList.toArray());
		excel.column(2, 3).value(orgNameList.toArray());
		List<String> list = new ArrayList<String>();
		for (String shiftName : shiftNames) {
			list.add(shiftName);
		}
		list.add("休息日");
		list.add("节假日");
		String[] s = (String[]) list.toArray(new String[list.size()]);
		for (int i = 0; i < betweenDays; i++) {
			excel.column(3 + i, 3).addValidationData(s).width(4000);
		}
	}

	private void getExcelTemplate2(Excel excel, String[] accounts,
			String[] userNames, String[] orgNames, String[] shiftNames,
			Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		List<String> accountList = new ArrayList<String>();
		List<String> userNameList = new ArrayList<String>();
		List<String> orgNameList = new ArrayList<String>();
		List<String> attenceTimeList = new ArrayList<String>();

		int betweenDays = DateUtil.daysBetween(startTime, endTime);
		if (BeanUtils.isNotEmpty(accounts)) {
			for (int i = 0; i < accounts.length; i++) {
				String account = accounts[i];
				String userName = "";
				if (BeanUtils.isNotEmpty(userNames))
					userName = userNames[i];
				String orgName = "";
				try {
					if (BeanUtils.isNotEmpty(orgNames))
						orgName = orgNames[i];
				} catch (Exception e) {
					// TODO: handle exception
				}
				for (int j = 0; j <= betweenDays; j++) {
					Date attenceTime = DateUtil.addDay(startTime, j);
					attenceTimeList.add(DateFormatUtil.formatDate(attenceTime));
					accountList.add(account);
					userNameList.add(userName);
					orgNameList.add(orgName);
				}
			}
		} else {
			for (int i = 0; i <= betweenDays; i++) {
				Date attenceTime = DateUtil.addDay(startTime, i);
				attenceTimeList.add(DateFormatUtil.formatDate(attenceTime));
			}
		}

		String[] titleAry = new String[] { "员工编号", "姓名", "组织名称", "考勤日期",
				"班次名称", "日期类型" };
		excel.row(0, 0).value(titleAry).font(new IFontEditor() {
			// 设置字体
			@Override
			public void updateFont(Font font) {
				font.boldweight(BoldWeight.BOLD);// 粗体
			}
		}).bgColor(Color.GREY_25_PERCENT).width(3500);

		excel.column(0, 1).value(accountList.toArray()).width(3000)
				.dataFormat("@");
		excel.column(1, 1).value(userNameList.toArray()).width(3000);
		excel.column(2, 1).value(orgNameList.toArray()).width(3000);

		excel.sheet().setDefaultColumnStyle(3, DataFormat.DATE);
		excel.column(3, 1).value(attenceTimeList.toArray()).width(3000);
		excel.column(4, 1).addValidationData(shiftNames).width(4000);
		excel.column(5, 1)
				.addValidationData(
						new String[] { AtsConstant.DATE_TYPE_WORKDAY_STRING,
								AtsConstant.DATE_TYPE_DAYOFF_STRING,
								AtsConstant.DATE_TYPE_HOLIDAY_STRING })
				.width(3000);
	}

	/**
	 * 导入数据保存
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("importData")
	@Action(description = "导入数据保存", detail = "导入业务数据模板数据")
	public void importData(MultipartHttpServletRequest request,
			HttpServletResponse response) throws Exception {
		MultipartFile file = request.getFile("file");
		Short holidayHandle = RequestUtil.getShort(request, "holidayHandle");
		int template = RequestUtil.getInt(request, "template", 1);
		ResultMessage message = null;
		String fileExt = file.getOriginalFilename().substring(
				file.getOriginalFilename().lastIndexOf("."));
		try {
			atsScheduleShiftService.importExcel(file.getInputStream(),
					holidayHandle, fileExt, template);
			message = new ResultMessage(ResultMessage.Success,
					MsgUtil.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			MsgUtil.clean();
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				message = new ResultMessage(ResultMessage.Fail, "导入失败:" + str);
			} else {
				String msg = ExceptionUtil.getExceptionMessage(e);
				message = new ResultMessage(ResultMessage.Fail, msg);
			}
		}
		writeResultMessage(response.getWriter(), message);

	}

}
