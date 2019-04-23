package com.suneee.platform.service.ats;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.ats.AtsCalendarTemplDao;
import com.suneee.platform.dao.ats.AtsCalendarTemplDetailDao;
import com.suneee.platform.dao.ats.AtsWorkCalendarDao;
import com.suneee.platform.model.ats.AtsCalendarTempl;
import com.suneee.platform.model.ats.AtsCalendarTemplDetail;
import com.suneee.platform.model.ats.AtsConstant;
import com.suneee.platform.model.ats.AtsWorkCalendar;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * <pre>
 * 对象功能:日历模版 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-17 15:44:41
 * </pre>
 */
@Service
public class AtsCalendarTemplService extends BaseService<AtsCalendarTempl> {
	@Resource
	private AtsCalendarTemplDao dao;

	@Resource
	private AtsCalendarTemplDetailDao atsCalendarTemplDetailDao;

	@Resource
	private AtsWorkCalendarDao atsWorkCalendarDao;
	
	public AtsCalendarTemplService() {
	}

	@Override
	protected IEntityDao<AtsCalendarTempl, Long> getEntityDao() {
		return dao;
	}
	
	
	/**
	 * 删除数据
	 * @param ids
	 * @return
	 */
	public String delDataByIds(Long[] ids) {
		//判断是否使用
		String msg = "";
		for (Long id : ids) {
			List<AtsWorkCalendar> list = atsWorkCalendarDao.getByCalendarTempl(id);
			if(BeanUtils.isNotEmpty(list)){
				msg +="已经有使用,不允许删除";
				return msg;		
			}
		}

		try {
			this.delByIds(ids);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * 保存 日历模版 信息
	 * 
	 * @param atsCalendarTempl
	 */
	public void save(AtsCalendarTempl atsCalendarTempl) {
		Long id = atsCalendarTempl.getId();
		if (id == null || id == 0) {
			id = UniqueIdUtil.genId();
			atsCalendarTempl.setId(id);
			atsCalendarTempl.setIsSys(AtsConstant.NO);
			this.add(atsCalendarTempl);
		} else {
			this.update(atsCalendarTempl);
			atsCalendarTemplDetailDao.delByCalendarId(id);
		}
		String detailList = atsCalendarTempl.getDetailList();
		JSONArray jary = JSONArray.fromObject(detailList);
		for (Object obj : jary) {
			JSONObject json = (JSONObject) obj;
			String week = (String) json.get("week");
			String dayType = (String) json.get("dayType");
			AtsCalendarTemplDetail ctd = new AtsCalendarTemplDetail();
			ctd.setWeek(Short.parseShort(week));
			ctd.setDayType(Short.parseShort(dayType));
			ctd.setCalendarId(id);
			ctd.setId(UniqueIdUtil.genId());
			atsCalendarTemplDetailDao.add(ctd);
		}

	}

	public String getDetailList(Long id) {
		List<AtsCalendarTemplDetail> list = atsCalendarTemplDetailDao
				.getByCalendarId(id);
		JSONArray jary = new JSONArray();
		for (AtsCalendarTemplDetail ctd : list) {
			JSONObject json = new JSONObject();
			json.accumulate("week", ctd.getWeek().toString());
			json.accumulate("dayType", ctd.getDayType().toString());
			jary.add(json);
		}
		return jary.toString();
	}

}
