package com.suneee.platform.service.ats;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.*;
import com.suneee.platform.dao.ats.AtsShiftInfoDao;
import com.suneee.platform.dao.ats.AtsShiftTimeDao;
import com.suneee.platform.model.ats.AtsCardRule;
import com.suneee.platform.model.ats.AtsConstant;
import com.suneee.platform.model.ats.AtsShiftInfo;
import com.suneee.platform.model.ats.AtsShiftTime;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.StringPool;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsShiftInfoDao;
import com.suneee.platform.dao.ats.AtsShiftTimeDao;
import com.suneee.platform.model.ats.AtsCardRule;
import com.suneee.platform.model.ats.AtsConstant;
import com.suneee.platform.model.ats.AtsShiftInfo;
import com.suneee.platform.model.ats.AtsShiftTime;

/**
 * <pre>
 * 对象功能:班次设置 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-18 17:21:46
 * </pre>
 */
@Service
public class AtsShiftInfoService extends BaseService<AtsShiftInfo> {
	@Resource
	private AtsShiftInfoDao dao;
	@Resource
	private AtsShiftTimeDao atsShiftTimeDao;

	@Resource
	private AtsCardRuleService atsCardRuleService;

	public AtsShiftInfoService() {
	}

	@Override
	protected IEntityDao<AtsShiftInfo, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 保存 班次设置 信息
	 * 
	 * @param atsShiftInfo
	 * @throws Exception
	 */
	public void save(AtsShiftInfo atsShiftInfo) throws Exception {
		Long id = atsShiftInfo.getId();
		Boolean flag = true;
		if (id == null || id == 0) {
			id = UniqueIdUtil.genId();
			atsShiftInfo.setId(id);

		} else {
			flag = false;
			atsShiftTimeDao.delByShiftId(id);
		}

		String detailList = atsShiftInfo.getDetailList();
		if (BeanUtils.isEmpty(detailList))
			return;
		JSONArray jary = JSONArray.fromObject(detailList);
		double standardHour = 0;
		for (Object obj : jary) {
			JSONObject json = (JSONObject) obj;
			String segment = (String) json.get("segment");
			String attendanceType = (String) json.get("attendanceType");
			String onType = (String) json.get("onType");
			String onTime = (String) json.get("onTime");
			String onPunchCard = (String) json.get("onPunchCard");
			String onFloatAdjust = (String) json.get("onFloatAdjust");
			String segmentRest = (String) json.get("segmentRest");
			String offType = (String) json.get("offType");
			String offTime = (String) json.get("offTime");
			String offPunchCard = (String) json.get("offPunchCard");
			String offFloatAdjust = (String) json.get("offFloatAdjust");

			Date onDate = DateFormatUtil.parse(onTime,
					StringPool.DATE_FORMAT_TIME_NOSECOND);

			Date offDate = DateFormatUtil.parse(offTime,
					StringPool.DATE_FORMAT_TIME_NOSECOND);

			Short onType1 = BeanUtils.isEmpty(onType) ? AtsConstant.TIME_TODAY
					: Short.valueOf(onType);
			Short offType1 = BeanUtils.isEmpty(offType) ? AtsConstant.TIME_TODAY
					: Short.valueOf(offType);

			Double rest = StringUtils.isEmpty(segmentRest) ? 0 : Double
					.valueOf(segmentRest);

			standardHour += getStandardHour(onType1, onDate, offType1, offDate,
					rest);

			AtsShiftTime ast = new AtsShiftTime();
			ast.setSegment(Short.valueOf(segment));
			ast.setAttendanceType(Short.valueOf(attendanceType));
			ast.setOnType(onType1);
			ast.setOnTime(onDate);
			ast.setOnPunchCard(Short.valueOf(onPunchCard));
			ast.setOnFloatAdjust(StringUtils.isEmpty(onFloatAdjust) ? 0
					: Double.valueOf(onFloatAdjust));
			ast.setSegmentRest(rest);
			ast.setOffType(offType1);
			ast.setOffTime(offDate);
			ast.setOffPunchCard(Short.valueOf(offPunchCard));
			ast.setOffFloatAdjust(StringUtils.isEmpty(offFloatAdjust) ? 0
					: Double.valueOf(offFloatAdjust));

			ast.setShiftId(id);
			ast.setId(UniqueIdUtil.genId());
			atsShiftTimeDao.add(ast);
		}

		if (flag) {
			atsShiftInfo.setStandardHour(standardHour);
			this.add(atsShiftInfo);
		} else {
			this.update(atsShiftInfo);
		}
	}

	private double getStandardHour(Short onType, Date onDate, Short offType,
			Date offDate, Double rest) {
		if (onType.shortValue() == AtsConstant.TIME_YESTERDAY)
			onDate = DateUtil.addDay(onDate, -1);
		if (offType.shortValue() == AtsConstant.TIME_TOMORROW)
			offDate = DateUtil.addDay(offDate, 1);

		return DateUtil.betweenHour(onDate, offDate, rest);
	}

	public String getDetailList(Long id) {
		List<AtsShiftTime> list = atsShiftTimeDao.getByShiftId(id);
		JSONArray jary = new JSONArray();
		for (AtsShiftTime ast : list) {
			JSONObject json = new JSONObject();
			json.accumulate("segment", String.valueOf(ast.getSegment()));
			json.accumulate("attendanceType",
					String.valueOf(ast.getAttendanceType()));

			json.accumulate("onType", String.valueOf(ast.getOnType()));
			json.accumulate("onTime",
					DateFormatUtil.format(ast.getOnTime(), "HH:mm"));
			json.accumulate("onPunchCard", String.valueOf(ast.getOnPunchCard()));
			json.accumulate("onFloatAdjust",
					String.valueOf(ast.getOnFloatAdjust()));
			json.accumulate("segmentRest", String.valueOf(ast.getSegmentRest()));
			json.accumulate("offType", String.valueOf(ast.getOffType()));
			json.accumulate("offTime",
					DateFormatUtil.format(ast.getOffTime(), "HH:mm"));
			json.accumulate("offPunchCard",
					String.valueOf(ast.getOffPunchCard()));
			json.accumulate("offFloatAdjust",
					String.valueOf(ast.getOffFloatAdjust()));
			jary.add(json);
		}
		return jary.toString();
	}

	public AtsShiftInfo getShiftInfoById(Long id) {
		AtsShiftInfo atsShiftInfo = this.getById(id);
		if (BeanUtils.isEmpty(atsShiftInfo))
			return atsShiftInfo;
		List<AtsShiftTime> shiftTimeList = atsShiftTimeDao.getByShiftId(id);
		AtsCardRule atsCardRule = atsCardRuleService.getById(atsShiftInfo
				.getCardRule());
		atsShiftInfo.setAtsCardRule(atsCardRule);
		atsShiftInfo.setShiftTimeList(shiftTimeList);
		return atsShiftInfo;

	}

	public AtsShiftInfo getByShiftName(String shiftName) {
		return dao.getByShiftName(shiftName);
	}

	public AtsShiftInfo getByDefault() {
		return dao.getByDefault();
	}

	public Integer isAliasExists(String name, Long id) {
		return dao.isAliasExists(name, id);
	}

}
