package com.suneee.platform.service.ats;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.*;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.eas.common.utils.ContextUtil;
import com.suneee.platform.controller.ats.AtsAttenceCalculateController;
import com.suneee.platform.dao.ats.AtsAttenceCalculateDao;
import com.suneee.platform.model.ats.*;
import com.suneee.platform.service.ats.impl.AtsCalculateContainer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

/**
 * <pre>
 * 对象功能:考勤计算 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-31 13:51:08
 * </pre>
 */
@Service
public class AtsAttenceCalculateService extends BaseService<AtsAttenceCalculate> {
	@Resource
	private AtsAttenceCalculateDao dao;
	@Resource
	private AtsAttencePolicyService atsAttencePolicyService;
	@Resource
	private AtsAttendanceFileService atsAttendanceFileService;
	@Resource
	private AtsScheduleShiftService atsScheduleShiftService;
	@Resource
	private AtsCardRecordService atsCardRecordService;
	@Resource
	private AtsCardRuleService atsCardRuleService;
	@Autowired(required = false)
	private AtsCalculateContainer atsCalculateContainer;

	public AtsAttenceCalculateService() {
	}

	@Override
	protected IEntityDao<AtsAttenceCalculate, Long> getEntityDao() {
		return dao;
	}

	public List<AtsAttenceCalculate> getByFileIdAttenceTime(Long fileId, Date beginattenceTime, Date endattenceTime) {
		return dao.getByFileIdAttenceTime(fileId, beginattenceTime, endattenceTime);
	}

	public AtsAttenceCalculate getByFileIdAttenceTime(Long fileId, Date attenceTime) {
		return dao.getByFileIdAttenceTime(fileId, attenceTime);
	}

	/**
	 * 保存 考勤计算 信息
	 * 
	 * @param atsAttenceCalculate
	 */
	public void save(AtsAttenceCalculate atsAttenceCalculate) {
		Long fileId = atsAttenceCalculate.getFileId();
		Date attenceTime = atsAttenceCalculate.getAttenceTime();
		AtsAttenceCalculate calculate = dao.getByFileIdAttenceTime(fileId, attenceTime);
		if (BeanUtils.isEmpty(calculate)) {
			atsAttenceCalculate.setId(UniqueIdUtil.genId());
			this.add(atsAttenceCalculate);
		} else {
			Long id = calculate.getId();
			atsAttenceCalculate.setId(id);
			this.update(atsAttenceCalculate);
		}
	}

	public void delCalculate(AtsAttenceCalculate calculate) {
		Long fileId = calculate.getFileId();
		Date attenceTime = calculate.getAttenceTime();
		AtsAttenceCalculate cal = dao.getByFileIdAttenceTime(fileId, attenceTime);
		if (BeanUtils.isNotEmpty(cal))
			dao.delById(cal.getId());
	}

	/**
	 * 计算选择的
	 * 
	 * @param startTime
	 * @param endTime
	 * @param attendPolicyId
	 * @param fileIds
	 * @throws Exception
	 */
	public void calculate(Date startTime, Date endTime, Long attencePolicyId, Long[] fileIds) throws Exception {
		AtsAttencePolicy atsAttencePolicy = atsAttencePolicyService.getById(attencePolicyId);
		for (int i=0;i<fileIds.length;i++) {
			Long fileId = fileIds[i];
			AtsAttendanceFile atsAttendanceFile = atsAttendanceFileService.getById(fileId);
			HttpServletRequest request = ContextUtil.getRequest();
			request.getSession().setAttribute(AtsAttenceCalculateController.CALCULATING_MSG, "正在计算用户："+atsAttendanceFile.getUserName()+"..后面还有 "+(fileIds.length-i)+" 个用户");
			this.calculate(startTime, endTime, atsAttencePolicy, atsAttendanceFile);
		}
	}

	/**
	 * 计算所有
	 * 
	 * @param startTime
	 * @param endTime
	 * @param attendPolicyId
	 * @throws Exception
	 */
	public void allCalculate(Date startTime, Date endTime, Long attencePolicyId) throws Exception {
		// 把考勤档案的所有人查出来 TODO 这个数据要好久,这后面考虑多线程
		AtsAttencePolicy atsAttencePolicy = atsAttencePolicyService.getById(attencePolicyId);
		List<AtsAttendanceFile> list = atsAttendanceFileService.getByAttendPolicy(attencePolicyId);
		// 取得这个时间段的排班
		for (int i=0;i<list.size();i++) {
			AtsAttendanceFile atsAttendanceFile = list.get(i);
			HttpServletRequest request = ContextUtil.getRequest();
			request.getSession().setAttribute(AtsAttenceCalculateController.CALCULATING_MSG, "正在计算用户："+atsAttendanceFile.getUserName()+"..后面还有 "+(list.size()-i)+" 个用户");
			this.calculate(startTime, endTime, atsAttencePolicy, atsAttendanceFile);
		}
	}

	/**
	 * 考勤计算
	 * 
	 * <pre>
	 *   传入参数：考勤的开始和结束时间 【startTime、endTime】
	 *   		考勤制度  【atsAttencePolicy】
	 *   		要计算的考勤档案的人 【atsAttendanceFile】
	 *   准备数据：
	 *   	1、取出这段时间内考勤人的排班的数据(包含排班班次、取卡规则);
	 *   	2、取出这段时间内考勤人的打卡记录;（开始时间-1天  ~~ 结束时间+1天）
	 *   计算逻辑：
	 * 		1、循环这个段时间的每一天;
	 * 		2、判断是否有排班，如果否，则删除之前的计算结果;
	 * 		3、判断排班类型：休息日和假日处理 ;
	 * 		4、判断该人是否打卡考勤；
	 * 		5、处理请假、出差等情况。
	 * 		6、取出当前人这个时间段的有效卡
	 * 		7、如果不存在卡，进行异常处理。
	 * 		8、存在卡，进行分段次处理；（详见  分段取卡处理逻辑）
	 * 	
	 * 	分段取卡处理逻辑：
	 * 		1、第一段上班时间点取班次取卡范围内的最早打卡。
	 * 		2、 最后一段下班时间点取班次取卡范围内的最晚打卡。
	 * 		3、段内取卡段间上班的临近规则可以细分为以下情况：
	 * 			3.1、段内>=2条打卡记录。最前卡分配给上一段下班的、最后卡分配给下一段上班。
	 * 			3.2、段内<2条打卡记录。
	 * 				3.2.1、段内只有1条打卡记录。
	 * 					根据段内取卡规则，分为最近取卡点和手工分配，进行段内卡的分配；
	 * 					没分到卡的然后根据没打卡规则进行下班前取卡或上班后取卡规则进行取卡。
	 * 				3.2.2、段内没有打卡记录。
	 * 					根据没打卡规则进行下班前取卡和上班后取卡。
	 * 考勤处理异常逻辑：缺卡、正常、迟到、早退、旷工
	 * 	根据取出的卡考勤制度进行处理，是否缺卡、正常、迟到、早退、旷工
	 * 
	 * </pre>
	 * 
	 * @param startTime
	 * @param endTime
	 * @param betweenDays
	 * @param atsAttencePolicy
	 * @param atsAttendanceFile
	 * @throws Exception
	 */
	public void calculate(Date startTime, Date endTime, AtsAttencePolicy atsAttencePolicy, AtsAttendanceFile atsAttendanceFile) throws Exception {
		int betweenDays = DateUtil.daysBetween(startTime, endTime);
		Long fileId = atsAttendanceFile.getId();
		//排版数据，key是日期 eg:2015-07-11：object
		Map<String, AtsScheduleShift> scheduleShiftMap = atsScheduleShiftService.getByFileIdStartEndTimeMap(fileId, startTime, endTime);
		//打卡数据
		Set<Date> cardRecordList = atsCardRecordService.getByCardNumberSet(atsAttendanceFile.getCardNumber(), startTime, endTime);

		for (int i = 0; i <= betweenDays; i++) {
			Date attenceTime = DateUtil.addDay(startTime, i);
			String dateStr = DateFormatUtil.formatDate(attenceTime);
			AtsAttenceCalculate calculate = new AtsAttenceCalculate();
			calculate.setFileId(fileId);
			calculate.setAttenceTime(attenceTime);
			//超过今天的日期是否计算 ,以后可以配置
			if (attenceTime.after(new Date())) {
				this.delCalculate(calculate);
				break;
			}
			// 排班
			AtsScheduleShift atsScheduleShift = scheduleShiftMap.get(dateStr);
			if (BeanUtils.isEmpty(atsScheduleShift)) { // 没有排班的数据
				this.delCalculate(calculate);
				continue;
			}

			setTodayCardRecord(calculate, attenceTime, atsAttendanceFile.getCardNumber());

			calculate.setIsScheduleShift(AtsConstant.YES);
			// 时间类型
			Short dateType = atsScheduleShift.getDateType();
			// 排班设置
			calculate.setDateType(dateType);

			//检查今天排版的是不是节假日
			if (dateType.shortValue() == AtsConstant.DATE_TYPE_DAYOFF || dateType.shortValue() == AtsConstant.DATE_TYPE_HOLIDAY) {
				// 处理休息日，节假日
				this.handerDayoffHoliday(calculate, atsScheduleShift, dateType);
				continue;
			}

			//班次
			AtsShiftInfo shiftInfo = atsScheduleShift.getShiftInfo();
			if (BeanUtils.isEmpty(shiftInfo)) {// TODO
				continue;
			}

			calculate.setShiftId(shiftInfo.getId());
			if (atsAttendanceFile.getIsAttendance().shortValue() == AtsConstant.NO) {// 处理是否参与打卡考勤
				this.handerNoneAttendance(calculate);
				continue;
			}

			List<AtsShiftTime> shiftTimeList = shiftInfo.getShiftTimeList();
			String shiftTime = getShiftTime(shiftTimeList);
			// 排班时间
			calculate.setShiftTime(shiftTime);
			// 排班时间<段次、排班时间>
			Map<Short, AtsShiftTime> shiftTimeMap = getShiftTimeMap(shiftTimeList);
			// 取卡规则
			AtsCardRule atsCardRule = shiftInfo.getAtsCardRule();
			// 段次
			Short segmentNum = atsCardRule.getSegmentNum();
			// 标准工时
			double shouldAttenceHours = shiftInfo.getStandardHour();
			// 写死
			Long shiftTypeId = shiftInfo.getShiftType();
			if (shiftTypeId.longValue() == 3 || shiftTypeId.longValue() == 4) { // 休息日和节假日
				dateType = shiftTypeId.longValue() == 3 ? AtsConstant.DATE_TYPE_DAYOFF : AtsConstant.DATE_TYPE_HOLIDAY;
				// 处理异常休息日，节假日
				this.handerAbnormaDayoffHoliday(calculate, atsScheduleShift, dateType, shouldAttenceHours, segmentNum);
				continue;
			}
			// 获取有效的打卡记录
			Set<Date> cardRecordSet = this.getValidCardRecord(attenceTime, shiftTimeMap, atsCardRule, cardRecordList);
			// 是否有打卡考勤
			if (BeanUtils.isEmpty(cardRecordSet)) {// 没有打卡记录计旷工
				calculate = this.handerAbnormalAttendance(calculate, shiftTimeMap, segmentNum, shouldAttenceHours);
				this.save(calculate);
				continue;
			} else {
				calculate.setIsCardRecord(AtsConstant.YES);
				// 设置打卡记录
				calculate.setCardRecord(cardRecordSet);
			}

			if (segmentNum == AtsConstant.SEGMENT_1) { // 一段处理
				this.handleSegment1(calculate, atsAttencePolicy, shiftTimeMap, atsCardRule, cardRecordSet, segmentNum);
			} else if (segmentNum == AtsConstant.SEGMENT_2) {// 二段处理
				this.handleSegment2(calculate, atsAttencePolicy, shiftTimeMap, atsCardRule, cardRecordSet, segmentNum);
			} else if (segmentNum == AtsConstant.SEGMENT_3) {// 三段处理
				this.handleSegment3(calculate, atsAttencePolicy, shiftTimeMap, atsCardRule, cardRecordSet, segmentNum);
			}
			double actualAttenceHours = this.getActualAttenceHours(calculate, shouldAttenceHours);

			calculate.setShouldAttenceHours(shouldAttenceHours);
			calculate.setActualAttenceHours(actualAttenceHours);// 实际
			this.save(calculate);
		}

		// 处理请假、出差、加班、补休 TODO
		this.handerSpecial(atsAttendanceFile, scheduleShiftMap, cardRecordList, startTime, endTime);

	}

	/**
	 * TODO 处理请假、出差、加班、补卡、
	 * 
	 * @param scheduleShiftMap
	 * @param cardRecordList
	 * 
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @throws Exception
	 */
	private void handerSpecial(AtsAttendanceFile atsAttendanceFile, Map<String, AtsScheduleShift> scheduleShiftMap, Set<Date> cardRecordList, Date startTime, Date endTime) throws Exception {
		if (atsCalculateContainer == null)
			return;

		AtsModel atsModel = new AtsModel();
		atsModel.setUserId(atsAttendanceFile.getUserId());
		atsModel.setFileId(atsAttendanceFile.getId());
		atsModel.setStartTime(startTime);
		atsModel.setEndTime(endTime);

		Map<String, IAtsCalculate> atsCalculateMap = atsCalculateContainer.getAtsCalculateMap();
		for (Iterator<Entry<String, IAtsCalculate>> it = atsCalculateMap.entrySet().iterator(); it.hasNext();) {
			IAtsCalculate calculate = it.next().getValue();
			List<AtsAttenceCalculate> list = calculate.calculate(atsModel, scheduleShiftMap, cardRecordList);
			if (BeanUtils.isEmpty(list))
				continue;
			for (AtsAttenceCalculate atsAttenceCalculate : list) {
				this.update(atsAttenceCalculate);
			}
		}
	}

	/**
	 * 处理不参与考勤
	 */
	private void handerNoneAttendance(AtsAttenceCalculate calculate) {
		calculate.setShouldAttenceHours(0d);
		calculate.setActualAttenceHours(0d);
		calculate.setIsCardRecord(AtsConstant.CARD_NORMAL_ABNORMA);
		// 设置考勤类型
		this.setAttenceType(calculate, (short) 0, AtsConstant.TIME_TYPE_NORMAL);
		this.save(calculate);
	}

	/**
	 * // 处理异常休息日，节假日
	 * 
	 * @param calculate
	 * @param atsScheduleShift
	 * @param dateType
	 * @param shouldAttenceHours
	 * @param segmentNum
	 */
	private void handerAbnormaDayoffHoliday(AtsAttenceCalculate calculate, AtsScheduleShift atsScheduleShift, Short dateType, double shouldAttenceHours, Short segmentNum) {

		calculate.setShouldAttenceHours(0d);
		calculate.setActualAttenceHours(shouldAttenceHours);
		calculate.setIsCardRecord(AtsConstant.CARD_NORMAL_ABNORMA);
		// 设置考勤类型
		this.setAttenceType(calculate, segmentNum, AtsConstant.TIME_TYPE_NORMAL);
		this.save(calculate);
	}

	/**
	 * 处理休息日，节假日
	 * 
	 * @param dateType
	 * @param calculate
	 * @param atsScheduleShift
	 */
	private void handerDayoffHoliday(AtsAttenceCalculate calculate, AtsScheduleShift atsScheduleShift, Short dateType) {
		// 这个类型不计算迟到、早退等情况，可以计算加班
		if (dateType.shortValue() == AtsConstant.DATE_TYPE_HOLIDAY)
			calculate.setHolidayName(atsScheduleShift.getTitle());
		calculate.setShouldAttenceHours(0d);
		// 设置考勤类型
		this.setAttenceType(calculate, (short) (dateType + 6), AtsConstant.TIME_TYPE_NORMAL);
		this.save(calculate);
	}

	/**
	 * 处理异常考勤
	 * 
	 * @param calculate
	 * @param shiftTimeMap
	 * @param segmentNum
	 * @param shouldAttenceHours
	 * @return
	 */
	private AtsAttenceCalculate handerAbnormalAttendance(AtsAttenceCalculate calculate, Map<Short, AtsShiftTime> shiftTimeMap, Short segmentNum, double shouldAttenceHours) {
		AtsShiftTime atsShiftTime = shiftTimeMap.get(segmentNum);
		if (BeanUtils.isEmpty(atsShiftTime))
			return calculate;
		if (atsShiftTime.getAttendanceType() == AtsConstant.ATTENDANCE_TYPE_NORMAL_ABNORMAL) {// 正常出勤不计异常
			calculate.setShouldAttenceHours(shouldAttenceHours);
			calculate.setIsCardRecord(AtsConstant.CARD_NORMAL_ABNORMA);
			// 设置考勤类型
			for (short i = 1; i <= segmentNum; i++) {
				this.setAttenceType(calculate, i, AtsConstant.TIME_TYPE_NORMAL);
			}
			// 设置考勤类型
			this.setAttenceType(calculate, segmentNum, AtsConstant.TIME_TYPE_NORMAL);
		} else {
			calculate.setShouldAttenceHours(shouldAttenceHours);
			calculate.setIsCardRecord(AtsConstant.NO);
			calculate.setAbsentNumber((double) segmentNum);
			calculate.setAbsentTime(shouldAttenceHours);
			// 设置考勤类型
			for (short i = 1; i <= segmentNum; i++) {
				this.setAttenceType(calculate, i, AtsConstant.TIME_TYPE_ABSENT, AtsConstant.TIME_TYPE_ABSENT_CARD);
			}
		}
		return calculate;
	}

	/**
	 * 设置考勤类型
	 * 
	 * @param calculate
	 * @param segmentNum
	 * @param args
	 */
	private void setAttenceType(AtsAttenceCalculate calculate, short segmentNum, Short... args) {
		Set<Integer> types = new TreeSet<Integer>();
		String separator = ",";
		String attenceType = calculate.getAttenceType();
		if (BeanUtils.isNotEmpty(attenceType)) {
			String[] attendanceTypeAry = attenceType.split(separator);
			for (String type : attendanceTypeAry) {
				types.add(Integer.parseInt(type));
			}
		}
		for (Short type : args) {
			types.add(segmentNum * 1000 + type);
			//设置异常
			if (type == AtsConstant.TIME_TYPE_ABSENT || type == AtsConstant.TIME_TYPE_ABSENT_CARD || type == AtsConstant.TIME_TYPE_LATE || type == AtsConstant.TIME_TYPE_LEAVE) {
				calculate.setAbnormity(AtsAttenceCalculate.AbnormityType.abnormity);
			}
		}
		calculate.setAttenceType(StringUtils.join(types.toArray(), separator));
	}

	/**
	 * 获取有效打卡记录
	 * 
	 * @param attenceTime
	 * @param shiftTimeMap
	 * @param atsCardRule
	 * @param cardRecordList
	 * @return
	 */
	private Set<Date> getValidCardRecord(Date attenceTime, Map<Short, AtsShiftTime> shiftTimeMap, AtsCardRule atsCardRule, Set<Date> cardRecordList) {
		Short segmentNum = atsCardRule.getSegmentNum();
		// 获取第一段班次
		AtsShiftTime shiftTimeFirst = shiftTimeMap.get(AtsConstant.SEGMENT_1);
		// 最后一段班次
		AtsShiftTime shiftTimeLast = shiftTimeMap.get(segmentNum);
		// 上班时间
		Date onTime = this.getTime(attenceTime, shiftTimeFirst.getOnTime(), shiftTimeFirst.getOnType());
		// 下班时间
		Date offTime = this.getTime(attenceTime, shiftTimeLast.getOffTime(), shiftTimeLast.getOffType());

		// 上班提前几个小时
		Double startNum = atsCardRule.getStartNum();
		// 下班延后几个小时
		Double endNum = atsCardRule.getEndNum();

		// 上班提前开始算打卡有效时间
		Date onAdjustStartTime = DateUtil.addMinute(DateUtil.addHour(onTime, -startNum), -1);
		// 下班班延后之前算打卡有效时间
		Date offAdjustEndTime = DateUtil.addMinute(DateUtil.addHour(offTime, endNum), 1);

		// 设置有效卡
		Set<Date> set = new TreeSet<Date>();

		for (Date onCardTime : cardRecordList) {
			if (DateUtil.isBetween(onAdjustStartTime, offAdjustEndTime, onCardTime))// 判断是否在有效卡内的时间
				set.add(onCardTime);
		}
		return set;
	}

	/**
	 * 处理一段取卡
	 * 
	 * @param calculate
	 * @param atsAttencePolicy
	 * @param shiftTimeMap
	 * @param atsCardRule
	 * @param cardRecordSet
	 * @param segmentNum
	 */
	private void handleSegment1(AtsAttenceCalculate calculate, AtsAttencePolicy atsAttencePolicy, Map<Short, AtsShiftTime> shiftTimeMap, AtsCardRule atsCardRule, Set<Date> cardRecordSet, Short segmentNum) {
		// 考勤时间
		Date attenceTime = calculate.getAttenceTime();
		AtsShiftTime atsShiftTime = shiftTimeMap.get(segmentNum);
		atsShiftTime.getAttendanceType();
		// 1． 第一段上班时间点：取班次取卡范围内的最早打卡。
		Date onCardTime = handleFirstSegmentOn(attenceTime, shiftTimeMap, atsCardRule, cardRecordSet);
		// 2． 最后一段下班时间点：取班次取卡范围内的最晚打卡。
		Date offCardTime = handleLastSegmentOff(attenceTime, shiftTimeMap, atsCardRule, cardRecordSet, segmentNum);
		this.handleCalculate(onCardTime, offCardTime, calculate, atsAttencePolicy, shiftTimeMap, atsCardRule, segmentNum);

		// 设置有效卡
		Set<Date> cardTimeSet = new TreeSet<Date>();
		this.addCardTimeSet(cardTimeSet, onCardTime);
		this.addCardTimeSet(cardTimeSet, offCardTime);
		this.setValidCardRecord(cardRecordSet, cardTimeSet, calculate);

	}

	/**
	 * 处理二段打卡
	 * 
	 * @param calculate
	 * @param atsAttencePolicy
	 * @param shiftTimeMap
	 * @param atsCardRule
	 * @param cardRecordSet
	 * @param segmentNum
	 */
	private void handleSegment2(AtsAttenceCalculate calculate, AtsAttencePolicy atsAttencePolicy, Map<Short, AtsShiftTime> shiftTimeMap, AtsCardRule atsCardRule, Set<Date> cardRecordSet, Short segmentNum) {
		// 考勤时间
		Date attenceTime = calculate.getAttenceTime();
		// 1． 第一段上班时间点：取班次取卡范围内的最早打卡。
		Date onCardTime1 = handleFirstSegmentOn(attenceTime, shiftTimeMap, atsCardRule, cardRecordSet);

		// 2． 最后一段下班时间点：取班次取卡范围内的最晚打卡。
		Date offCardTime2 = handleLastSegmentOff(attenceTime, shiftTimeMap, atsCardRule, cardRecordSet, segmentNum);

		// 段次内卡
		Map<Short, Date> segmentInsideCardMap12 = getSegmentInsideCardMap(attenceTime, shiftTimeMap, atsCardRule, cardRecordSet, AtsConstant.SEGMENT_1);
		Date offCardTime1 = segmentInsideCardMap12.get(AtsConstant.ATTENCE_TYPE_OFF);
		Date onCardTime2 = segmentInsideCardMap12.get(AtsConstant.ATTENCE_TYPE_ON);

		this.handleCalculate(onCardTime1, offCardTime1, calculate, atsAttencePolicy, shiftTimeMap, atsCardRule, AtsConstant.SEGMENT_1);
		this.handleCalculate(onCardTime2, offCardTime2, calculate, atsAttencePolicy, shiftTimeMap, atsCardRule, AtsConstant.SEGMENT_2);

		// 设置有效卡
		Set<Date> cardTimeSet = new TreeSet<Date>();
		this.addCardTimeSet(cardTimeSet, onCardTime1);
		this.addCardTimeSet(cardTimeSet, offCardTime1);
		this.addCardTimeSet(cardTimeSet, onCardTime2);
		this.addCardTimeSet(cardTimeSet, offCardTime2);
		this.setValidCardRecord(cardRecordSet, cardTimeSet, calculate);
	}

	/**
	 * 处理3段打卡
	 * 
	 * @param calculate
	 * @param atsAttencePolicy
	 * @param shiftTimeMap
	 * @param atsCardRule
	 * @param cardRecordSet
	 */
	private void handleSegment3(AtsAttenceCalculate calculate, AtsAttencePolicy atsAttencePolicy, Map<Short, AtsShiftTime> shiftTimeMap, AtsCardRule atsCardRule, Set<Date> cardRecordSet, Short segmentNum) {
		// 考勤时间
		Date attenceTime = calculate.getAttenceTime();
		// 1． 第一段上班时间点：取班次取卡范围内的最早打卡。
		Date onCardTime1 = handleFirstSegmentOn(attenceTime, shiftTimeMap, atsCardRule, cardRecordSet);

		// 2． 最后一段下班时间点：取班次取卡范围内的最晚打卡。
		Date offCardTime3 = handleLastSegmentOff(attenceTime, shiftTimeMap, atsCardRule, cardRecordSet, segmentNum);

		// 段次内卡 1-2段
		Map<Short, Date> segmentInsideCardMap12 = getSegmentInsideCardMap(attenceTime, shiftTimeMap, atsCardRule, cardRecordSet, AtsConstant.SEGMENT_1);

		// 段次内卡 2-3段
		Map<Short, Date> segmentInsideCardMap23 = getSegmentInsideCardMap(attenceTime, shiftTimeMap, atsCardRule, cardRecordSet, AtsConstant.SEGMENT_2);

		Date offCardTime1 = segmentInsideCardMap12.get(AtsConstant.ATTENCE_TYPE_OFF);
		Date onCardTime2 = segmentInsideCardMap12.get(AtsConstant.ATTENCE_TYPE_ON);
		Date offCardTime2 = segmentInsideCardMap23.get(AtsConstant.ATTENCE_TYPE_OFF);
		Date onCardTime3 = segmentInsideCardMap23.get(AtsConstant.ATTENCE_TYPE_ON);

		this.handleCalculate(onCardTime1, offCardTime1, calculate, atsAttencePolicy, shiftTimeMap, atsCardRule, AtsConstant.SEGMENT_1);

		this.handleCalculate(onCardTime2, offCardTime2, calculate, atsAttencePolicy, shiftTimeMap, atsCardRule, AtsConstant.SEGMENT_2);

		this.handleCalculate(onCardTime3, offCardTime3, calculate, atsAttencePolicy, shiftTimeMap, atsCardRule, AtsConstant.SEGMENT_3);

		// 设置有效卡
		Set<Date> cardTimeSet = new TreeSet<Date>();
		this.addCardTimeSet(cardTimeSet, onCardTime1);
		this.addCardTimeSet(cardTimeSet, offCardTime1);
		this.addCardTimeSet(cardTimeSet, onCardTime2);
		this.addCardTimeSet(cardTimeSet, offCardTime2);
		this.addCardTimeSet(cardTimeSet, onCardTime3);
		this.addCardTimeSet(cardTimeSet, offCardTime3);
		this.setValidCardRecord(cardRecordSet, cardTimeSet, calculate);
	}

	/**
	 * 增加有效打卡记录
	 * 
	 * @param cardTimeSet
	 * @param cardTime
	 */
	private void addCardTimeSet(Set<Date> cardTimeSet, Date cardTime) {
		if (BeanUtils.isEmpty(cardTime))
			return;
		cardTimeSet.add(cardTime);
	}

	/**
	 * 设置有效卡
	 * 
	 * @param cardRecordSet
	 * @param cardTime
	 * @param calculate
	 */
	private void setValidCardRecord(Set<Date> cardRecordSet, Set<Date> cardTime, AtsAttenceCalculate calculate) {
		// 取出有效卡
		List<Date> validCardRecord = new ArrayList<Date>();
		for (Date date : cardRecordSet) {
			for (Date date1 : cardTime) {
				if (BeanUtils.isEmpty(date1))
					continue;
				if (date.equals(date1))
					validCardRecord.add(date);
			}
		}
		String s = "";
		for (Date date : validCardRecord) {
			s += DateFormatUtil.format(date, StringPool.DATE_FORMAT_TIME) + "|";
		}
		// 设置打卡记录
		calculate.setValidCardRecord(s);
	}

	/**
	 * 处理上下班相同卡问题，距离那个卡近给那个卡
	 * 
	 * @param onCardTime
	 * @param offCardTime
	 * @param attenceTime
	 * @param shiftTimeMap
	 * @param segmentNum
	 */

	private Short handleSameCard(Date onCardTime, Date offCardTime, Date onTime, Date offTime, Short segmentNum) {
		if (BeanUtils.isEmpty(onCardTime) || BeanUtils.isEmpty(offCardTime))
			return null;
		if (onCardTime.compareTo(offCardTime) != 0)// 不同时间
			return null;
		// 距离那个卡近
		if ((offTime.getTime() - onCardTime.getTime()) >= (onCardTime.getTime() - onTime.getTime()))
			return AtsConstant.ASSIGN_SEGMENT_PRE;
		else
			return AtsConstant.ASSIGN_SEGMENT_NEXT;
	}

	/**
	 * 处理考勤计算
	 * 
	 * @param onCardTime
	 * @param offCardTime
	 * @param calculate
	 * @param atsAttencePolicy
	 * @param shiftTimeMap
	 * @param atsCardRule
	 * @param segmentNum
	 */
	private void handleCalculate(Date onCardTime, Date offCardTime, AtsAttenceCalculate calculate, AtsAttencePolicy atsAttencePolicy, Map<Short, AtsShiftTime> shiftTimeMap, AtsCardRule atsCardRule, Short segmentNum) {
		// =====处理打卡
		AtsShiftTime atsShiftTime = shiftTimeMap.get(segmentNum);
		if (atsShiftTime.getAttendanceType().shortValue() == AtsConstant.ATTENDANCE_TYPE_NORMAL_ABNORMAL) {// 如果有不计异常
			this.setAttenceType(calculate, segmentNum, AtsConstant.TIME_TYPE_NORMAL);
			return;
		}

		// 考勤时间
		Date attenceTime = calculate.getAttenceTime();

		// 上班提前几个小时
		Double startNum = atsCardRule.getStartNum();
		// 下班延后几个小时
		Double endNum = atsCardRule.getEndNum();
		// 上班时间
		Date onTime = this.getTime(attenceTime, atsShiftTime.getOnTime(), atsShiftTime.getOnType());
		// 下班时间
		Date offTime = this.getTime(attenceTime, atsShiftTime.getOffTime(), atsShiftTime.getOffType());

		// ///////=======================上班处理======================
		// 上班提前开始算打卡有效时间
		Date onAdjustStartTime = DateUtil.addMinute(DateUtil.addHour(onTime, -startNum), -1);

		// 到上班允许迟到时间
		Date onAdjustEndTime = DateUtil.addMinute(onTime, getFloatAdjust(atsAttencePolicy.getLateAllow(), atsShiftTime.getOnFloatAdjust()) + 1);
		// 上班旷工开始时间
		Date onAbsentStartTime = DateUtil.addMinute(onTime, getFloatAdjust(atsAttencePolicy.getAbsentAllow(), atsShiftTime.getOnFloatAdjust()) + 1);

		// ///////=======================下班处理======================
		// 下班旷工开始时间
		Date offAbsentStartTime = DateUtil.addMinute(offTime, -getFloatAdjust(atsAttencePolicy.getAbsentAllow(), atsShiftTime.getOnFloatAdjust()) - 1);
		// 早退允许值
		Date offAdjustStartTime = DateUtil.addMinute(offTime, -getFloatAdjust(atsAttencePolicy.getLeaveAllow(), atsShiftTime.getOffFloatAdjust()) - 1);

		// 下班延迟多久算打卡有效时间
		Date offAdjustEndTime = DateUtil.addMinute(DateUtil.addHour(offTime, endNum), 1);

		// //处理相同卡问题
		Short sameCard = this.handleSameCard(onCardTime, offCardTime, onTime, offTime, segmentNum);
		if (BeanUtils.isNotEmpty(sameCard)) {
			if (sameCard.shortValue() == AtsConstant.ASSIGN_SEGMENT_PRE)
				onCardTime = null;
			else
				offCardTime = null;
		}

		// 上班时间处理结果
		/**
		 * 这里的计算逻辑是，拿出打卡规则算出来的上班时间卡， 然后跟班次设置的上班时间来对比 得出是：旷工，缺卡，迟到，早退这几种类型
		 */
		Short onTimeType = this.judgeOnTimeType(onCardTime, onAdjustStartTime, onAdjustEndTime, onAbsentStartTime);

		Short offTimeType = this.judgeOffTimeType(offCardTime, offAdjustStartTime, offAdjustEndTime, offAbsentStartTime);

		// 处理考勤时间
		this.handSchuqin(onCardTime, offCardTime, onTime, offTime, calculate, segmentNum);
		// 上下班都正常上班-----计打卡正常
		if (onTimeType == AtsConstant.TIME_TYPE_NORMAL && offTimeType == AtsConstant.TIME_TYPE_NORMAL) {
			// 设置考勤类型
			this.setAttenceType(calculate, segmentNum, AtsConstant.TIME_TYPE_NORMAL);
			return;
		}

		double segmentRest = atsShiftTime.getSegmentRest();

		// 上班正常 下班旷工----计旷工 或者 下班正常 上班旷工----计旷工 （那就是上班或下班旷工，计旷工）
		if (onTimeType == AtsConstant.TIME_TYPE_ABSENT || offTimeType == AtsConstant.TIME_TYPE_ABSENT || onTimeType == AtsConstant.TIME_TYPE_ABSENT_CARD || offTimeType == AtsConstant.TIME_TYPE_ABSENT_CARD) {
			this.setAbsentCalculate(calculate, segmentNum, onTime, offTime, onCardTime, offCardTime, segmentRest);

			// 设置考勤类型
			if (onTimeType == AtsConstant.TIME_TYPE_ABSENT_CARD || onTimeType == AtsConstant.TIME_TYPE_NORMAL || offTimeType == AtsConstant.TIME_TYPE_ABSENT_CARD || offTimeType == AtsConstant.TIME_TYPE_NORMAL) { // 缺卡也算旷工
				this.setAttenceType(calculate, segmentNum, AtsConstant.TIME_TYPE_ABSENT, AtsConstant.TIME_TYPE_ABSENT_CARD);
			} else {
				this.setAttenceType(calculate, segmentNum, AtsConstant.TIME_TYPE_ABSENT);
			}
		} else {
			// 上班迟到--------计迟到
			if (onTimeType == AtsConstant.TIME_TYPE_LATE) {
				this.setLateCalculate(calculate, segmentNum, onTime, onCardTime);
				// 设置考勤类型
				this.setAttenceType(calculate, segmentNum, AtsConstant.TIME_TYPE_LATE);
			}
			// 下班早退-------计早退
			if (offTimeType == AtsConstant.TIME_TYPE_LEAVE) {// 早退
				this.setLeaveCalculate(calculate, segmentNum, offTime, offCardTime);
				// 设置考勤类型
				this.setAttenceType(calculate, segmentNum, AtsConstant.TIME_TYPE_LEAVE);
			}
		}

	}

	/**
	 * 处理实际考勤
	 * 
	 * @param onFirDate
	 * @param offFirDate
	 * @param onTime
	 * @param offTime
	 * @param calculate
	 * @param segmentNum
	 */
	private void handSchuqin(Date onDate, Date offDate, Date onTime, Date offTime, AtsAttenceCalculate calculate, Short segmentNum) {

		String onCardTime = "";
		String offCardTime = "";
		if (BeanUtils.isNotEmpty(onDate))
			onCardTime = DateFormatUtil.format(onDate, StringPool.DATE_FORMAT_TIME_NOSECOND);
		if (BeanUtils.isNotEmpty(offDate))
			offCardTime = DateFormatUtil.format(offDate, StringPool.DATE_FORMAT_TIME_NOSECOND);
		JSONObject json = new JSONObject();
		json.accumulate("segment", segmentNum).accumulate("onCardTime", onCardTime).accumulate("offCardTime", offCardTime);

		JSONArray jary = new JSONArray();
		if (segmentNum.shortValue() != AtsConstant.SEGMENT_1) {
			JSONArray ary = JSONArray.fromObject(calculate.getAbsentRecord());
			for (Object o : ary) {
				jary.add(o);
			}
		}
		jary.add(json);
		calculate.setAbsentRecord(jary.toString());
	}

	/**
	 * 处理段内下、上班卡
	 * 
	 * @param attenceTime
	 * @param shiftTimeMap
	 * @param atsCardRule
	 * @param cardRecordSet
	 * @param segmentNum
	 * @return
	 */
	private Map<Short, Date> getSegmentInsideCardMap(Date attenceTime, Map<Short, AtsShiftTime> shiftTimeMap, AtsCardRule atsCardRule, Set<Date> cardRecordSet, Short segmentNum) {
		Date offCardTime = null;
		Date onCardTime = null;
		// 先判断这个段内是否有卡
		List<Date> segmentInsideCardDate = handSegmentInsideCardDate(attenceTime, shiftTimeMap, cardRecordSet, segmentNum);
		if (segmentInsideCardDate.size() >= 2) {// 下班时间点与下一段上班时间点之间打卡数>=2，
												// 则优先取下班时间点所有后卡中最临近卡。
			offCardTime = handleSegmentInside(attenceTime, shiftTimeMap, segmentInsideCardDate, segmentNum, AtsConstant.ATTENCE_TYPE_OFF);
			onCardTime = handleSegmentInside(attenceTime, shiftTimeMap, segmentInsideCardDate, (short) (segmentNum + 1), AtsConstant.ATTENCE_TYPE_ON);
		} else {
			if (segmentInsideCardDate.size() == 1) {// 只有一个打卡，则按打卡规则设置
				Date insideCardDate = segmentInsideCardDate.get(0);
				short assignSegment = AtsConstant.ASSIGN_SEGMENT_PRE;
				if (atsCardRule.getSegFirAssignType() == AtsConstant.ASSIGN_TYPE_HAND) // 手工分配
					assignSegment = atsCardRule.getSegFirAssignSegment();
				else
					// 最近打卡点
					assignSegment = this.handleAssignNear(shiftTimeMap, segmentNum, attenceTime, insideCardDate);
				//
				if (assignSegment == AtsConstant.ASSIGN_SEGMENT_PRE)
					offCardTime = insideCardDate;
				else
					onCardTime = insideCardDate;
			}
			// 处理下班打卡前的卡
			if (BeanUtils.isEmpty(offCardTime))
				offCardTime = handleSegmentInsideOff(attenceTime, shiftTimeMap, atsCardRule, cardRecordSet, segmentNum);
			// 处理上班打卡后的卡
			if (BeanUtils.isEmpty(onCardTime))
				onCardTime = handleSegmentInsideOn(attenceTime, shiftTimeMap, atsCardRule, cardRecordSet, (short) (segmentNum + 1));
		}
		Map<Short, Date> map = new HashMap<Short, Date>();
		map.put(AtsConstant.ATTENCE_TYPE_OFF, offCardTime);
		map.put(AtsConstant.ATTENCE_TYPE_ON, onCardTime);
		return map;
	}

	/**
	 * 处理段内上班打卡前的卡
	 * 
	 * @param attenceTime
	 * @param shiftTimeMap
	 * @param atsCardRule
	 * @param cardRecordSet
	 * @param segmentNum
	 * @return
	 */
	private Date handleSegmentInsideOff(Date attenceTime, Map<Short, AtsShiftTime> shiftTimeMap, AtsCardRule atsCardRule, Set<Date> cardRecordSet, Short segmentNum) {
		AtsShiftTime shiftTime = shiftTimeMap.get(segmentNum);
		// 下班时间
		Date offTime = this.getTime(attenceTime, shiftTime.getOffTime(), shiftTime.getOffType());
		if (shiftTime.getOffPunchCard() == AtsConstant.NO)//
			return offTime;
		// TODO 要进行多次取卡
		Date onCardTime = this.handrTakeCard1(cardRecordSet, offTime, atsCardRule.getSegAftFirStartNum(), 0d, AtsConstant.CARD_LATEST, 1);
		return onCardTime;
	}

	/**
	 * 处理段内上班的卡后卡
	 * 
	 * @param attenceTime
	 * @param shiftTimeMap
	 * @param atsCardRule
	 * @param cardRecordSet
	 * @param segmentNum
	 * @return
	 */
	private Date handleSegmentInsideOn(Date attenceTime, Map<Short, AtsShiftTime> shiftTimeMap, AtsCardRule atsCardRule, Set<Date> cardRecordSet, Short segmentNum) {
		AtsShiftTime shiftTime = shiftTimeMap.get(segmentNum);
		// 下班时间
		Date onTime = this.getTime(attenceTime, shiftTime.getOnTime(), shiftTime.getOnType());
		if (shiftTime.getOnPunchCard().shortValue() == AtsConstant.NO)//
			return onTime;
		// TODO 要进行2次取卡
		Date onCardTime = this.handrTakeCard1(cardRecordSet, onTime, 0d, atsCardRule.getSegBefFirEndNum(), AtsConstant.CARD_FIRST, 2);
		return onCardTime;
	}

	/**
	 * 处理最近取卡点
	 * 
	 * @param shiftTimeMap
	 * @param segmentNum
	 * @param attenceTime
	 * @param insideCardDate
	 * @return
	 */
	private Short handleAssignNear(Map<Short, AtsShiftTime> shiftTimeMap, Short segmentNum, Date attenceTime, Date insideCardDate) {
		AtsShiftTime atsShiftTimeOff = shiftTimeMap.get(segmentNum);
		AtsShiftTime atsShiftTimeOn = shiftTimeMap.get((short) (segmentNum + 1));
		// 下班时间
		Date offTime = this.getTime(attenceTime, atsShiftTimeOff.getOffTime(), atsShiftTimeOff.getOffType());
		// 上班时间
		Date onTime = this.getTime(attenceTime, atsShiftTimeOn.getOnTime(), atsShiftTimeOn.getOffType());

		Long off = offTime.getTime() - insideCardDate.getTime();
		Long on = insideCardDate.getTime() - onTime.getTime();
		if (off >= on)
			return AtsConstant.ASSIGN_SEGMENT_PRE;
		else
			return AtsConstant.ASSIGN_SEGMENT_NEXT;
	}

	/**
	 * 处理段内有效卡
	 * 
	 * @param attenceTime
	 * @param shiftTimeMap
	 * @param cardRecordSet
	 * @param segmentNum
	 * @return
	 */
	private List<Date> handSegmentInsideCardDate(Date attenceTime, Map<Short, AtsShiftTime> shiftTimeMap, Set<Date> cardRecordSet, Short segmentNum) {
		AtsShiftTime atsShiftTimeOff = shiftTimeMap.get(segmentNum);
		AtsShiftTime atsShiftTimeOn = shiftTimeMap.get((short) (segmentNum + 1));
		// 下班时间
		Date offTime = this.getTime(attenceTime, atsShiftTimeOff.getOffTime(), atsShiftTimeOff.getOffType());
		// 上班时间
		Date onTime = DateUtil.addMinute(this.getTime(attenceTime, atsShiftTimeOn.getOnTime(), atsShiftTimeOn.getOnType()), 1);
		List<Date> list = new ArrayList<Date>();
		// 这个段内有效时间
		for (Date cardTime : cardRecordSet) {
			if (DateUtil.isBetween(offTime, onTime, cardTime))// 取出有效卡
				list.add(cardTime);
		}
		return list;
	}

	/**
	 * 处理段内大于2张卡的时间，上班取最早卡，下班取最晚卡
	 * 
	 * @param attenceTime
	 * @param shiftTimeMap
	 * @param cardRecordList
	 * @param segmentNum
	 * @param attenceType
	 * @return
	 */
	private Date handleSegmentInside(Date attenceTime, Map<Short, AtsShiftTime> shiftTimeMap, List<Date> cardRecordList, Short segmentNum, Short attenceType) {
		AtsShiftTime atsShiftTime = shiftTimeMap.get(segmentNum);
		if (attenceType.shortValue() == AtsConstant.ATTENCE_TYPE_ON) {// 上班
			Date onTime = this.getTime(attenceTime, atsShiftTime.getOnTime(), atsShiftTime.getOnType());
			// 如果不打卡考勤则取当前上班时间
			if (atsShiftTime.getOnPunchCard() == AtsConstant.NO)//
				return onTime;
			return cardRecordList.get(0);// 上班取最早卡

		} else {// 下班
			Date offTime = this.getTime(attenceTime, atsShiftTime.getOffTime(), atsShiftTime.getOffType());
			// 如果不打卡考勤则取当前上班时间
			if (atsShiftTime.getOffPunchCard() == AtsConstant.NO)//
				return offTime;
			return cardRecordList.get(cardRecordList.size() - 1);// 下班取最晚卡
		}
	}

	/**
	 * 第一段上班时间点取班次取卡范围内的最早打卡。
	 * 
	 * @param attenceTime
	 *            考勤时间
	 * @param shiftTimeMap
	 *            班次Map
	 * @param atsCardRule
	 *            取卡规则
	 * @param cardRecordSet
	 *            打卡记录
	 * @return
	 */
	private Date handleFirstSegmentOn(Date attenceTime, Map<Short, AtsShiftTime> shiftTimeMap, AtsCardRule atsCardRule, Set<Date> cardRecordSet) {
		AtsShiftTime atsShiftTime = shiftTimeMap.get(AtsConstant.SEGMENT_1);

		// 上班时间
		Date onTime = this.getTime(attenceTime, atsShiftTime.getOnTime(), atsShiftTime.getOnType());
		// 上班有效取卡时间
		Date onCardTime = null;

		if (atsShiftTime.getOnPunchCard() == AtsConstant.NO) // 如果不打卡考勤则取当前上班时间
			return onTime;
		// 上班第一次取卡 取出上班的有效卡。这里是根据取卡规则来获取上班打卡时间
		onCardTime = this.onFirTakeCard(cardRecordSet, atsCardRule, onTime);
		if (BeanUtils.isNotEmpty(onCardTime))
			return onCardTime;
		// 如果第一未取出卡，进行第二次取卡
		onCardTime = this.onSecTakeCard(cardRecordSet, atsCardRule, onTime);
		if (BeanUtils.isNotEmpty(onCardTime))
			return onCardTime;
		// 如果第二次取卡还取不出来，则计算时间不大于下班时间
		Double segBefSecEndNum = atsCardRule.getSegBefSecEndNum();
		if (BeanUtils.isEmpty(segBefSecEndNum))
			segBefSecEndNum = 0d;
		// 如果该浮动调整值不为0，且大于迟到、早退允许值，那么该点的迟到、早退允许值以浮动调整值为准；否则仍然以班次中设置的迟到、早退允许值为准；
		// 上班提前几个小时
		Double startNum = atsCardRule.getStartNum();
		onCardTime = this.handrTakeCard(cardRecordSet, onTime, startNum, atsCardRule.getSegBefFirEndNum() + segBefSecEndNum,// 这个
				AtsConstant.CARD_FIRST);
		return onCardTime;
	}

	/**
	 * 获取有效时间（有今天、明天、后天）
	 * 
	 * @param attenceTime
	 * @param time
	 * @param type
	 * @return
	 */
	private Date getTime(Date attenceTime, Date time, Short type) {
		if (BeanUtils.isEmpty(type))
			type = AtsConstant.TIME_TODAY;
		if (type.shortValue() == AtsConstant.TIME_YESTERDAY)
			attenceTime = DateUtil.addDay(attenceTime, -1);
		else if (type.shortValue() == AtsConstant.TIME_TOMORROW)
			attenceTime = DateUtil.addDay(attenceTime, 1);
		return DateUtil.getTime(attenceTime, time);
	}

	/**
	 * 最后一段下班时间点取班次取卡范围内的最晚打卡。
	 * 
	 * @param attenceTime
	 *            考勤时间
	 * @param shiftTimeMap
	 *            班次Map
	 * @param atsCardRule
	 *            取卡规则
	 * @param cardRecordSet
	 *            打卡记录
	 * @param segmentNum
	 *            段次
	 * @return
	 */
	private Date handleLastSegmentOff(Date attenceTime, Map<Short, AtsShiftTime> shiftTimeMap, AtsCardRule atsCardRule, Set<Date> cardRecordSet, Short segmentNum) {
		AtsShiftTime atsShiftTime = shiftTimeMap.get(segmentNum);
		// 下班时间
		Date offTime = this.getTime(attenceTime, atsShiftTime.getOffTime(), atsShiftTime.getOffType());
		// 下班有效取卡时间
		Date offCardTime = null;
		if (atsShiftTime.getOffPunchCard() == AtsConstant.NO)// 如果不打卡考勤则取当前下班班时间
			return offTime;
		// 下班第一次取卡 取出下的有效卡
		offCardTime = this.offFirTakeCard(cardRecordSet, atsCardRule, offTime);
		if (BeanUtils.isNotEmpty(offCardTime))
			return offCardTime;
		// 如果第一未取出卡，进行第二次取卡
		offCardTime = this.offSecTakeCard(cardRecordSet, atsCardRule, offTime);
		if (BeanUtils.isNotEmpty(offCardTime))
			return offCardTime;
		// 如果第二次取卡还取不出来，则计算时间不大于下班时间
		Double segAftSecEndNum = atsCardRule.getSegAftSecEndNum();
		if (BeanUtils.isEmpty(segAftSecEndNum))
			segAftSecEndNum = 0d;
		offCardTime = this.handrTakeCard(cardRecordSet, offTime, atsCardRule.getEndNum(), atsCardRule.getSegAftFirEndNum() + segAftSecEndNum,// 这个
				AtsConstant.CARD_LATEST);
		return offCardTime;
	}

	/**
	 * 实际上班时间
	 * 
	 * @param calculate
	 * @param shouldAttenceHours
	 * @return
	 */
	private double getActualAttenceHours(AtsAttenceCalculate calculate, double shouldAttenceHours) {
		double actualAttenceHours = shouldAttenceHours; // 减去旷工，迟到，早退的；
		if (BeanUtils.isNotEmpty(calculate.getAbsentTime()))// 减去旷工小时
			actualAttenceHours = actualAttenceHours - calculate.getAbsentTime();
		if (BeanUtils.isNotEmpty(calculate.getLateTime()))// 减迟到分钟
			actualAttenceHours = actualAttenceHours - (calculate.getLateTime() / 60);
		if (BeanUtils.isNotEmpty(calculate.getLeaveTime()))// 减早退分钟
			actualAttenceHours = actualAttenceHours - (calculate.getLeaveTime() / 60);// 减分钟
		return actualAttenceHours;
	}

	/**
	 * 上班第一次取卡 有效卡
	 * 
	 * @param cardRecordSet
	 * @param atsCardRule
	 * @param onTime
	 * @param onAdjustStartTime
	 */
	private Date onFirTakeCard(Set<Date> cardRecordSet, AtsCardRule atsCardRule, Date onTime) {
		Double befStartNum = atsCardRule.getSegBefFirStartNum();
		if (befStartNum == null)
			return null;
		if (befStartNum > atsCardRule.getStartNum())
			befStartNum = atsCardRule.getStartNum();
		// 是否考虑 结束时间不能大过下班时间
		return this.handrTakeCard(cardRecordSet, onTime, befStartNum, atsCardRule.getSegBefFirEndNum(), atsCardRule.getSegBefFirTakeCardType());
	}

	/**
	 * 上班第二次取卡
	 * 
	 * @param cardRecordSet
	 * @param atsCardRule
	 * @param onTime
	 * @param onAdjustStartTime
	 * @return
	 */
	private Date onSecTakeCard(Set<Date> cardRecordSet, AtsCardRule atsCardRule, Date onTime) {
		Double befStartNum = atsCardRule.getSegBefSecStartNum();
		if (befStartNum == null || atsCardRule.getSegBefSecEndNum() == null)
			return null;
		befStartNum = atsCardRule.getSegBefFirStartNum() + befStartNum;
		if (befStartNum > atsCardRule.getStartNum())
			befStartNum = atsCardRule.getStartNum();
		// 是否考虑 结束时间不能大过下班时间
		return this.handrTakeCard(cardRecordSet, onTime, befStartNum, atsCardRule.getSegBefSecEndNum(), atsCardRule.getSegBefFirTakeCardType());
	}

	/**
	 * 下班第一次取卡 有效卡
	 * 
	 * @param cardRecordSet
	 * @param atsCardRule
	 * @param onTime
	 */
	private Date offFirTakeCard(Set<Date> cardRecordSet, AtsCardRule atsCardRule, Date offTime) {
		Double aftEndNum = atsCardRule.getSegAftFirEndNum();
		if (aftEndNum == null)
			return null;
		if (aftEndNum > atsCardRule.getEndNum())
			aftEndNum = atsCardRule.getEndNum();
		// 是否考虑 开始时间不能小于上班班时间
		return handrTakeCard(cardRecordSet, offTime, atsCardRule.getSegAftFirStartNum(), aftEndNum, atsCardRule.getSegAftFirTakeCardType());
	}

	/**
	 * 下班第二次取卡 有效卡
	 * 
	 * @param cardRecordSet
	 * @param atsCardRule
	 * @param onTime
	 */
	private Date offSecTakeCard(Set<Date> cardRecordSet, AtsCardRule atsCardRule, Date offTime) {
		Double aftEndNum = atsCardRule.getSegAftSecEndNum();
		if (aftEndNum == null || atsCardRule.getSegAftSecEndNum() == null)
			return null;
		aftEndNum = atsCardRule.getSegAftFirStartNum() + aftEndNum;
		if (aftEndNum > atsCardRule.getEndNum())
			aftEndNum = atsCardRule.getStartNum();
		// 是否考虑 开始时间不能小于上班班时间
		return handrTakeCard(cardRecordSet, offTime, atsCardRule.getSegAftSecStartNum(), aftEndNum, atsCardRule.getSegAftSecTakeCardType());
	}

	/**
	 * 取卡处理（是否增加或减少1分钟）
	 * 
	 * @param cardRecordSet
	 * @param time
	 *            上下班时间
	 * @param startNum
	 *            开始时间
	 * @param endNum
	 *            结束时间
	 * @param cardType
	 *            取卡规则
	 * @param i
	 *            上班班减一 1 下班加一 2
	 * @return
	 */
	private Date handrTakeCard1(Set<Date> cardRecordSet, Date time, Double startNum, Double endNum, Short cardType, int i) {
		Date date = null;
		if (BeanUtils.isEmpty(cardRecordSet))
			return date;
		Date adjustStartTime = DateUtil.addHour(time, -startNum);
		if (i == 1)
			adjustStartTime = DateUtil.addMinute(adjustStartTime, -1);
		Date adjustEndTime = DateUtil.addHour(time, endNum);
		if (i == 2)
			adjustStartTime = DateUtil.addMinute(adjustStartTime, 1);
		List<Date> list = new ArrayList<Date>();
		for (Date cardTime : cardRecordSet) {
			if (DateUtil.isBetween(adjustStartTime, adjustEndTime, cardTime))// 取出有效卡
				list.add(cardTime);
		}

		if (BeanUtils.isEmpty(list))
			return date;

		if (cardType == AtsConstant.CARD_FIRST)// 取最早卡
			date = list.get(0);
		else
			date = list.get(list.size() - 1);// 取最晚卡
		return date;
	}

	/**
	 * 取卡处理
	 * 
	 * @param cardRecordSet
	 * @param time
	 *            上下班时间
	 * @param startNum
	 *            开始时间
	 * @param endNum
	 *            结束时间
	 * @param cardType
	 *            取卡规则
	 * @return
	 */
	private Date handrTakeCard(Set<Date> cardRecordSet, Date time, Double startNum, Double endNum, Short cardType) {
		Date date = null;
		if (BeanUtils.isEmpty(cardRecordSet))
			return date;
		Date adjustStartTime = DateUtil.addMinute(DateUtil.addHour(time, -startNum), -1);
		Date adjustEndTime = DateUtil.addMinute(DateUtil.addHour(time, endNum), 1);
		List<Date> list = new ArrayList<Date>();
		for (Date cardTime : cardRecordSet) {
			if (DateUtil.isBetween(adjustStartTime, adjustEndTime, cardTime))// 取出有效卡
				list.add(cardTime);
		}

		if (BeanUtils.isEmpty(list))
			return date;

		if (cardType == AtsConstant.CARD_FIRST)// 取最早卡
			date = list.get(0);
		else
			date = list.get(list.size() - 1);// 取最晚卡
		return date;
	}

	/**
	 * 浮动值计算 如果该浮动调整值不为0，且大于迟到、早退允许值，那么该点的迟到、早退允许值以浮动调整值为准；否则仍然以班次中设置的迟到、早退允许值为准；
	 * 
	 * @param num
	 * @param floatAdjust
	 * @return
	 */
	private Integer getFloatAdjust(Integer num, Double floatAdjust) {
		if (BeanUtils.isEmpty(floatAdjust))
			return num;
		if (floatAdjust > 0 && floatAdjust > num)
			return floatAdjust.intValue();
		return num;
	}

	/**
	 * 设置早退的时间计算
	 * 
	 * @param calculate
	 * @param segment
	 * @param offTime
	 * @param offFirDate
	 */
	private void setLeaveCalculate(AtsAttenceCalculate calculate, short segment, Date offTime, Date offFirDate) {
		double leaveNumber = 1d;
		double leaveTime1 = DateUtil.betweenMinute(offFirDate, offTime);
		JSONObject json = new JSONObject();
		json.accumulate("segment", segment).accumulate("date", DateFormatUtil.format(offFirDate, StringPool.DATE_FORMAT_TIME_NOSECOND)).accumulate("time", leaveTime1);
		JSONArray jary = new JSONArray();
		if (segment != AtsConstant.SEGMENT_1) {
			if (BeanUtils.isNotEmpty(calculate.getLeaveNumber()))
				leaveNumber += calculate.getLeaveNumber();
			if (BeanUtils.isNotEmpty(calculate.getLeaveTime()))
				leaveTime1 += calculate.getLeaveTime();
			if (BeanUtils.isNotEmpty(calculate.getLeaveRecord())) {// 取出之前的记录
				JSONArray ary = JSONArray.fromObject(calculate.getLeaveRecord());
				for (Object o : ary) {
					jary.add(o);
				}
			}
		}

		jary.add(json);
		calculate.setLeaveNumber(leaveNumber);
		calculate.setLeaveTime(leaveTime1);
		calculate.setLeaveRecord(jary.toString());

	}

	/**
	 * 设置迟到的时间计算
	 * 
	 * @param calculate
	 * @param segment
	 * @param onTime
	 * @param onFirDate
	 */
	private void setLateCalculate(AtsAttenceCalculate calculate, short segment, Date onTime, Date onFirDate) {
		double lateNumber = 1d;
		double lateTime = DateUtil.betweenMinute(onTime, onFirDate);
		JSONObject json = new JSONObject();
		json.accumulate("segment", segment).accumulate("date", DateFormatUtil.format(onFirDate, StringPool.DATE_FORMAT_TIME_NOSECOND)).accumulate("time", lateTime);
		JSONArray jary = new JSONArray();
		if (segment != AtsConstant.SEGMENT_1) {
			if (BeanUtils.isNotEmpty(calculate.getLateNumber()))
				lateNumber += calculate.getLateNumber();
			if (BeanUtils.isNotEmpty(calculate.getLateTime()))
				lateTime += calculate.getLateTime();
			if (BeanUtils.isNotEmpty(calculate.getLateRecord())) {// 取出之前的记录
				JSONArray ary = JSONArray.fromObject(calculate.getLateRecord());
				for (Object o : ary) {
					jary.add(o);
				}
			}
		}
		jary.add(json);

		calculate.setLateNumber(lateNumber);
		calculate.setLateTime(lateTime);
		calculate.setLateRecord(jary.toString());
	}

	/**
	 * 设置旷工计算
	 * 
	 * @param calculate
	 * @param segment
	 * @param onTime
	 * @param offTime
	 * @param onFirDate
	 * @param offFirDate
	 * @param double1
	 */
	private void setAbsentCalculate(AtsAttenceCalculate calculate, short segment, Date onTime, Date offTime, Date onFirDate, Date offFirDate, Double segmentRest) {
		double absentNumber = 1d;
		double absentTime1 = DateUtil.betweenHour(onTime, offTime, segmentRest);
		if (BeanUtils.isNotEmpty(onFirDate) && BeanUtils.isNotEmpty(offFirDate)) {// 这里再考虑
			Date startTime = onFirDate;
			Date endTime = offFirDate;
			if (onFirDate.before(onTime))
				startTime = onTime;
			if (offFirDate.after(offTime))
				endTime = offTime;
			absentTime1 = absentTime1 - DateUtil.betweenHour(startTime, endTime, segmentRest);
			DecimalFormat df = new DecimalFormat("0.00");
			absentTime1 = Double.parseDouble(df.format(absentTime1));
		}
		if (segment != AtsConstant.SEGMENT_1) {
			if (BeanUtils.isNotEmpty(calculate.getAbsentNumber()))
				absentNumber += calculate.getAbsentNumber();
			if (BeanUtils.isNotEmpty(calculate.getAbsentTime()))
				absentTime1 += calculate.getAbsentTime();
		}
		calculate.setAbsentNumber(absentNumber);
		calculate.setAbsentTime(absentTime1);
	}

	/**
	 * 判断上班时间（正常上班、迟到、旷工）
	 * 
	 * @param onCardTime
	 * @param onAdjustStartTime
	 * @param onAdjustEndTime
	 * @param absentTime
	 * @return
	 */
	private Short judgeOnTimeType(Date onCardTime, Date onAdjustStartTime, Date onAdjustEndTime, Date onAbsentStartTime) {
		if (BeanUtils.isEmpty(onCardTime))
			return AtsConstant.TIME_TYPE_ABSENT_CARD;
		if (DateUtil.isBetween(onAdjustStartTime, onAdjustEndTime, onCardTime)) {// 计算上班开始时间--上班结束时间这段计为上班有效时间
			return AtsConstant.TIME_TYPE_NORMAL;
		} else if (DateUtil.isBetween(onAdjustEndTime, onAbsentStartTime, onCardTime)) {// 计算上班结束时间--旷工开始时间这段计为迟到
			return AtsConstant.TIME_TYPE_LATE;
		}
		return AtsConstant.TIME_TYPE_ABSENT;
	}

	/**
	 * 判断下班时间 （正常下班、早退、旷工）
	 * 
	 * @param onCardTime
	 * @param offAdjustStartTime
	 * @param offAdjustEndTime
	 * @param offAbsentStartTime
	 * @return
	 */
	private Short judgeOffTimeType(Date onCardTime, Date offAdjustStartTime, Date offAdjustEndTime, Date offAbsentStartTime) {
		if (BeanUtils.isEmpty(onCardTime))
			return AtsConstant.TIME_TYPE_ABSENT_CARD;
		if (DateUtil.isBetween(offAdjustStartTime, offAdjustEndTime, onCardTime)) {// 计算早退开始时间---下班有效时间；计为下班有效时间
			return AtsConstant.TIME_TYPE_NORMAL;
		} else if (DateUtil.isBetween(offAbsentStartTime, offAdjustStartTime, onCardTime)) {// 计算旷工开始时间---早退开始时间；这段计为早退
			return AtsConstant.TIME_TYPE_LEAVE;
		}
		return AtsConstant.TIME_TYPE_ABSENT;
	}

	private String getShiftTime(List<AtsShiftTime> shiftTimeList) {
		JSONArray jary = new JSONArray();
		try {
			for (AtsShiftTime atsShiftTime : shiftTimeList) {
				JSONObject json = new JSONObject();
				Date onTime1 = atsShiftTime.getOnTime();
				Date offTime1 = atsShiftTime.getOffTime();
				String onTime = DateFormatUtil.format(onTime1, StringPool.DATE_FORMAT_TIME_NOSECOND);
				String offTime = DateFormatUtil.format(offTime1, StringPool.DATE_FORMAT_TIME_NOSECOND);
				json.accumulate("segment", atsShiftTime.getSegment()).accumulate("onTime", onTime).accumulate("offTime", offTime);
				jary.add(json);
			}
		} catch (Exception e) {
		}
		return jary.toString();
	}

	private Map<Short, AtsShiftTime> getShiftTimeMap(List<AtsShiftTime> shiftTimeList) {
		Map<Short, AtsShiftTime> map = new HashMap<Short, AtsShiftTime>();
		for (AtsShiftTime atsShiftTime : shiftTimeList) {
			map.put(atsShiftTime.getSegment(), atsShiftTime);
		}
		return map;
	}

	/**
	 * 获取当天的打卡记录，后面可能会更改，但主要是给那些假期处理直接continue时也设置打卡数据， 为什么不把那些假期处理放在设置完的后面？看逻辑就知道他的取卡规则不会限于取当天的打卡，所以在这里
	 * 
	 * @param calculate
	 * @param attenceTime
	 * @param cardNumber
	 *            void
	 * @throws ParseException
	 * @exception
	 * @since 1.0.0
	 */
	private void setTodayCardRecord(AtsAttenceCalculate calculate, Date attenceTime, String cardNumber) throws ParseException {
		Date today = DateFormatUtil.parse(DateFormatUtil.format(attenceTime, "yyyy-MM-dd"));
		Date secondDay = DateUtil.addDay(attenceTime, 1);
		Set<Date> set = atsCardRecordService.getByCardNumberSet(cardNumber, secondDay, today);//因为startTime-1,endTime +1 ...
		calculate.setCardRecord(set);
		if (!set.isEmpty()) {
			calculate.setIsCardRecord(AtsConstant.YES);
		}
	}

	public List<AtsAttenceCalculate> getList(QueryFilter filter) {
		return dao.getList(filter);
	}

	public List<AtsAttenceCalculate> getListData(QueryFilter filter) {
		return dao.getListData(filter);
	}

	public static void main(String[] args) throws ParseException {
		Date now = new Date();
		System.out.println(DateFormatUtil.parse(DateFormatUtil.format(now, "yyyy-MM-dd")));
	}
}
